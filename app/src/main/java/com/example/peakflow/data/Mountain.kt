package com.example.peakflow.data

data class Mountain(
    val id: Int,
    val name: String,
    val height: Int,
    val region: String,
    val condReq: Int,
    val techReq: Int,
    val acclReq: Int,
    val riskReq: Int,
    val description: String,
    val imageUrl: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0
) {
    val totalDifficulty: Int get() = condReq + techReq + acclReq + riskReq
}
