package com.example.peakflow.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray

class MountainRepository private constructor(context: Context) {

    private val appContext = context.applicationContext

    private val prefs: SharedPreferences by lazy {
        appContext.getSharedPreferences("peakflow_prefs", Context.MODE_PRIVATE)
    }

    private val _mountains = MutableStateFlow<List<Mountain>>(emptyList())
    val mountains: StateFlow<List<Mountain>> = _mountains.asStateFlow()

    private val _conqueredIds = MutableStateFlow<Set<Int>>(emptySet())
    val conqueredIds: StateFlow<Set<Int>> = _conqueredIds.asStateFlow()

    private val _conqueredTimestamps = MutableStateFlow<Map<Int, Long>>(emptyMap())
    val conqueredTimestamps: StateFlow<Map<Int, Long>> = _conqueredTimestamps.asStateFlow()

    private val _userStats = MutableStateFlow(UserStats())
    val userStats: StateFlow<UserStats> = _userStats.asStateFlow()

    init {
        loadMountains()
        loadConqueredData()
        recalculateStats()
    }

    private fun loadMountains() {
        val json = appContext.assets.open("mountains.json")
            .bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(json)
        _mountains.value = (0 until jsonArray.length()).map { i ->
            jsonArray.getJSONObject(i).run {
                Mountain(
                    id = getInt("id"),
                    name = getString("name"),
                    height = getInt("height"),
                    region = getString("region"),
                    condReq = getInt("condReq"),
                    techReq = getInt("techReq"),
                    acclReq = getInt("acclReq"),
                    riskReq = getInt("riskReq"),
                    description = getString("description"),
                    imageUrl = optString("imageUrl", ""),
                    lat = optDouble("lat", 0.0),
                    lng = optDouble("lng", 0.0)
                )
            }
        }
    }

    private fun loadConqueredData() {
        val newData = prefs.getStringSet("conquered_data", null)
        val oldIds = prefs.getStringSet("conquered_ids", null)

        val timestamps: Map<Int, Long> = when {
            newData != null -> newData.mapNotNull { entry ->
                val parts = entry.split(":")
                if (parts.size == 2) parts[0].toIntOrNull()?.let { id -> id to (parts[1].toLongOrNull() ?: 0L) }
                else null
            }.toMap()
            oldIds != null -> {
                val migrated = oldIds.mapNotNull { it.toIntOrNull()?.let { id -> id to 0L } }.toMap()
                saveConqueredData(migrated)
                prefs.edit().remove("conquered_ids").apply()
                migrated
            }
            else -> emptyMap()
        }

        _conqueredTimestamps.value = timestamps
        _conqueredIds.value = timestamps.keys.toSet()
    }

    private fun saveConqueredData(timestamps: Map<Int, Long>) {
        val data = timestamps.entries.map { (id, ts) -> "$id:$ts" }.toSet()
        prefs.edit().putStringSet("conquered_data", data).apply()
    }

    fun toggleConquered(mountainId: Int) {
        val isAdding = mountainId !in _conqueredIds.value
        val updatedTimestamps = _conqueredTimestamps.value.toMutableMap().also { map ->
            if (isAdding) map[mountainId] = System.currentTimeMillis() else map.remove(mountainId)
        }
        _conqueredTimestamps.value = updatedTimestamps
        _conqueredIds.value = updatedTimestamps.keys
        saveConqueredData(updatedTimestamps)
        recalculateStats()
    }

    fun isConquered(mountainId: Int): Boolean = mountainId in _conqueredIds.value

    fun getMountainById(id: Int): Mountain? = _mountains.value.find { it.id == id }

    private fun recalculateStats() {
        val conquered = _conqueredIds.value
        val conqueredMountains = _mountains.value.filter { it.id in conquered }
        _userStats.value = UserStats(
            condition = conqueredMountains.maxOfOrNull { it.condReq } ?: 0,
            technique = conqueredMountains.maxOfOrNull { it.techReq } ?: 0,
            acclimatization = conqueredMountains.maxOfOrNull { it.acclReq } ?: 0,
            risk = conqueredMountains.maxOfOrNull { it.riskReq } ?: 0,
            totalXp = conqueredMountains.sumOf { it.totalDifficulty }
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: MountainRepository? = null

        fun getInstance(context: Context): MountainRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: MountainRepository(context).also { INSTANCE = it }
            }
    }
}
