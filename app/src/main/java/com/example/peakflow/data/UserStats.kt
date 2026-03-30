package com.example.peakflow.data

data class UserStats(
    var condition: Int = 0,
    var technique: Int = 0,
    var acclimatization: Int = 0,
    var risk: Int = 0,
    var totalXp: Int = 0
) {
    val level: Int
        get() = calculateLevel()

    val maxLevelXp: Int
        get() = calculateXpForLevel(level + 1)
        
    val currentLevelStartXp: Int
        get() = calculateXpForLevel(level)

    val progressToNextLevel: Float
        get() {
            val start = currentLevelStartXp
            val end = maxLevelXp
            if (end - start <= 0) return 1f
            return (totalXp - start).toFloat() / (end - start).toFloat()
        }

    private fun calculateLevel(): Int {
        var lvl = 1
        while (totalXp >= calculateXpForLevel(lvl + 1)) {
            lvl++
        }
        return lvl
    }

    private fun calculateXpForLevel(lvl: Int): Int {
        if (lvl <= 1) return 0
        // simple progression:
        // Level 2 -> 50
        // Level 3 -> 150
        // Level 4 -> 300
        // Level 5 -> 550
        // Level 6 -> 900
        // Level 7 -> 1350
        // Level 8 -> 1900
        // Level 9 -> 2550
        // Level 10 -> 3300
        return (lvl - 1) * (lvl - 1) * 30 + (lvl - 1) * 20
    }

    /** Max possible value for each individual stat */
    companion object {
        const val MAX_STAT = 5
    }
}
