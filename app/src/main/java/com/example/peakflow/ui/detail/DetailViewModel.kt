package com.example.peakflow.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.peakflow.data.Mountain
import com.example.peakflow.data.MountainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import androidx.lifecycle.viewModelScope

data class WeatherState(
    val conditionText: String,
    val conditionColor: String,
    val temp: Double,
    val wind: Double,
    val isSnowing: Boolean,
    val isSunny: Boolean,
    val desc: String
)

class DetailViewModel(private val repository: MountainRepository) : ViewModel() {

    private val _mountain = MutableLiveData<Mountain>()
    val mountain: LiveData<Mountain> = _mountain

    val userStats = repository.userStats

    private val _isConquered = MutableLiveData<Boolean>()
    val isConquered: LiveData<Boolean> = _isConquered

    private val _weather = MutableLiveData<WeatherState?>()
    val weather: LiveData<WeatherState?> = _weather

    fun loadMountain(id: Int) {
        val m = repository.getMountainById(id)
        _mountain.value = m
        _isConquered.value = repository.isConquered(id)
        
        if (m != null && (m.lat != 0.0 || m.lng != 0.0)) {
            fetchWeather(m.lat, m.lng)
        }
    }

    private fun fetchWeather(lat: Double, lng: Double) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    URL("https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lng&current_weather=true").readText()
                }
                val json = JSONObject(response).getJSONObject("current_weather")
                val temp = json.getDouble("temperature")
                val wind = json.getDouble("windspeed")
                val code = json.getInt("weathercode")
                
                // Smart logic
                val isSnowing = code in listOf(71, 73, 75, 77, 85, 86)
                val isSunny = code in listOf(0, 1)
                
                var conditionText = "Akceptowalne warunki"
                var conditionColor = "#2196F3"
                var desc = "Możesz spróbować wejścia, ale uważaj na zmiany pogody."

                if (temp < -15 || wind > 50 || code in listOf(95, 96, 99)) {
                    conditionText = "Warunki NIEBEZPIECZNE"
                    conditionColor = "#F44336"
                    desc = "Silny wiatr, burze lub ekstremalne mrozy. Stanowczo odradzamy."
                } else if (temp > 0 && wind < 15 && isSunny) {
                    conditionText = "Idealne okno wejścia"
                    conditionColor = "#4CAF50"
                    desc = "Wymarzona pogoda na atak szczytowy!"
                } else if (isSnowing || temp < -5) {
                    conditionText = "Ciężkie warunki zimowe"
                    conditionColor = "#00BCD4"
                    desc = "Ujemna temperatura i opady. Wymagany pełen sprzęt zimowy."
                }

                _weather.value = WeatherState(
                    conditionText, conditionColor, temp, wind, isSnowing, isSunny, desc
                )
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleConquered() {
        val m = _mountain.value ?: return
        repository.toggleConquered(m.id)
        _isConquered.value = repository.isConquered(m.id)
    }
}
