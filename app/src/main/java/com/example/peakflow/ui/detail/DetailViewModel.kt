package com.example.peakflow.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peakflow.data.Mountain
import com.example.peakflow.data.MountainRepository
import com.example.peakflow.data.UserStats
import com.example.peakflow.domain.WeatherCondition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

data class WeatherState(
    val condition: WeatherCondition,
    val temp: Double,
    val wind: Double,
    val isSnowing: Boolean,
    val isSunny: Boolean
)

class DetailViewModel(private val repository: MountainRepository) : ViewModel() {

    private val _mountain = MutableStateFlow<Mountain?>(null)
    val mountain: StateFlow<Mountain?> = _mountain.asStateFlow()

    val userStats: StateFlow<UserStats> = repository.userStats

    private val _isConquered = MutableStateFlow(false)
    val isConquered: StateFlow<Boolean> = _isConquered.asStateFlow()

    private val _weather = MutableStateFlow<WeatherState?>(null)
    val weather: StateFlow<WeatherState?> = _weather.asStateFlow()

    fun loadMountain(id: Int) {
        repository.getMountainById(id)?.also { m ->
            _mountain.value = m
            _isConquered.value = repository.isConquered(id)
            if (m.lat != 0.0 || m.lng != 0.0) fetchWeather(m.lat, m.lng)
        }
    }

    private fun fetchWeather(lat: Double, lng: Double) {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    URL("https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lng&current_weather=true").readText()
                }
            }.onSuccess { response ->
                val json = JSONObject(response).getJSONObject("current_weather")
                val temp = json.getDouble("temperature")
                val wind = json.getDouble("windspeed")
                val code = json.getInt("weathercode")
                val snowCodes = setOf(71, 73, 75, 77, 85, 86)
                val sunnyCodes = setOf(0, 1)
                val stormCodes = setOf(95, 96, 99)
                val isSnowing = code in snowCodes
                val isSunny = code in sunnyCodes
                val condition = when {
                    temp < -15 || wind > 50 || code in stormCodes -> WeatherCondition.Dangerous
                    temp > 0 && wind < 15 && isSunny -> WeatherCondition.Ideal
                    isSnowing || temp < -5 -> WeatherCondition.Winter
                    else -> WeatherCondition.Acceptable
                }
                _weather.value = WeatherState(condition, temp, wind, isSnowing, isSunny)
            }.onFailure {
                _weather.value = null
            }
        }
    }

    fun toggleConquered() {
        val m = _mountain.value ?: return
        repository.toggleConquered(m.id)
        _isConquered.value = repository.isConquered(m.id)
    }
}
