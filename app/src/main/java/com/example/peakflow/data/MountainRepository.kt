package com.example.peakflow.data

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.json.JSONArray

class MountainRepository private constructor(context: Context) {

    private val appContext = context.applicationContext
    private val prefs: SharedPreferences =
        appContext.getSharedPreferences("peakflow_prefs", Context.MODE_PRIVATE)

    private val _mountains = MutableLiveData<List<Mountain>>()
    val mountains: LiveData<List<Mountain>> = _mountains

    private val _conqueredIds = MutableLiveData<Set<Int>>()
    val conqueredIds: LiveData<Set<Int>> = _conqueredIds

    private val _userStats = MutableLiveData<UserStats>()
    val userStats: LiveData<UserStats> = _userStats

    init {
        loadMountains()
        loadConqueredIds()
        recalculateStats()
    }

    private fun loadMountains() {
        val json = appContext.assets.open("mountains.json")
            .bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(json)
        val list = mutableListOf<Mountain>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            list.add(
                Mountain(
                    id = obj.getInt("id"),
                    name = obj.getString("name"),
                    height = obj.getInt("height"),
                    region = obj.getString("region"),
                    condReq = obj.getInt("condReq"),
                    techReq = obj.getInt("techReq"),
                    acclReq = obj.getInt("acclReq"),
                    riskReq = obj.getInt("riskReq"),
                    description = obj.getString("description"),
                    imageUrl = obj.optString("imageUrl", ""),
                    lat = obj.optDouble("lat", 0.0),
                    lng = obj.optDouble("lng", 0.0)
                )
            )
        }
        _mountains.value = list
    }

    private fun loadConqueredIds() {
        val saved = prefs.getStringSet("conquered_ids", emptySet()) ?: emptySet()
        _conqueredIds.value = saved.map { it.toInt() }.toSet()
    }

    private fun saveConqueredIds(ids: Set<Int>) {
        prefs.edit()
            .putStringSet("conquered_ids", ids.map { it.toString() }.toSet())
            .apply()
    }

    fun toggleConquered(mountainId: Int) {
        val current = _conqueredIds.value?.toMutableSet() ?: mutableSetOf()
        if (current.contains(mountainId)) {
            current.remove(mountainId)
        } else {
            current.add(mountainId)
        }
        _conqueredIds.value = current
        saveConqueredIds(current)
        recalculateStats()
    }

    fun isConquered(mountainId: Int): Boolean {
        return _conqueredIds.value?.contains(mountainId) == true
    }

    fun getMountainById(id: Int): Mountain? {
        return _mountains.value?.find { it.id == id }
    }

    private fun recalculateStats() {
        val conquered = _conqueredIds.value ?: emptySet()
        val allMountains = _mountains.value ?: emptyList()
        val conqueredMountains = allMountains.filter { it.id in conquered }

        val stats = UserStats(
            // Individual stats = MAX from any conquered mountain
            condition = conqueredMountains.maxOfOrNull { it.condReq } ?: 0,
            technique = conqueredMountains.maxOfOrNull { it.techReq } ?: 0,
            acclimatization = conqueredMountains.maxOfOrNull { it.acclReq } ?: 0,
            risk = conqueredMountains.maxOfOrNull { it.riskReq } ?: 0,
            // Total XP = sum of all requirements from all conquered mountains
            totalXp = conqueredMountains.sumOf { it.totalDifficulty }
        )
        _userStats.value = stats
    }

    companion object {
        @Volatile
        private var INSTANCE: MountainRepository? = null

        fun getInstance(context: Context): MountainRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MountainRepository(context).also { INSTANCE = it }
            }
        }
    }
}
