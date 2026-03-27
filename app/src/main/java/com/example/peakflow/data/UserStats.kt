package com.example.peakflow.data

data class UserStats(
    var condition: Int = 0,
    var technique: Int = 0,
    var acclimatization: Int = 0,
    var risk: Int = 0,
    var totalXp: Int = 0
) {
    /** Max possible value for each individual stat */
    companion object {
        const val MAX_STAT = 5
    }
}
