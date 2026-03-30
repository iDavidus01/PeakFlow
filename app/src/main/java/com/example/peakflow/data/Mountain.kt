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

    val requiredLevel: Int get() = when {
        height >= 8000 -> 10
        height >= 7000 -> 9
        height >= 6000 -> 8
        height >= 5000 -> 7
        height >= 4000 -> 6
        height >= 3000 -> 5
        height >= 2000 -> 4
        height >= 1500 -> 3
        height >= 1000 -> 2
        else -> 1
    }
}
