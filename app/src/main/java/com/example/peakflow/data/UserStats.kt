package com.example.peakflow.data

data class UserStats(
    var condition: Int = 0,
    var technique: Int = 0,
    var acclimatization: Int = 0,
    var risk: Int = 0
) {
    val totalXp: Int get() = condition + technique + acclimatization + risk
}
