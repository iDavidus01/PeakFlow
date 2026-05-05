package com.example.peakflow.domain

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.example.peakflow.R

sealed class WeatherCondition(
    @StringRes val labelRes: Int,
    @ColorRes val colorRes: Int,
    @StringRes val descRes: Int
) {
    data object Dangerous : WeatherCondition(R.string.weather_dangerous, R.color.status_red, R.string.weather_desc_dangerous)
    data object Ideal : WeatherCondition(R.string.weather_ideal, R.color.status_green, R.string.weather_desc_ideal)
    data object Winter : WeatherCondition(R.string.weather_winter, R.color.status_cyan, R.string.weather_desc_winter)
    data object Acceptable : WeatherCondition(R.string.weather_acceptable, R.color.status_blue, R.string.weather_desc_acceptable)
}
