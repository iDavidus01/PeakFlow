package com.example.peakflow.data

data class UserStats(
    val condition: Int = 0,
    val technique: Int = 0,
    val acclimatization: Int = 0,
    val risk: Int = 0,
    val totalXp: Int = 0
) {
    val level: Int get() = calculateLevel()

    val maxLevelXp: Int get() = calculateXpForLevel(level + 1)

    val currentLevelStartXp: Int get() = calculateXpForLevel(level)

    val progressToNextLevel: Float
        get() {
            val start = currentLevelStartXp
            val end = maxLevelXp
            if (end - start <= 0) return 1f
            return (totalXp - start).toFloat() / (end - start).toFloat()
        }

    private fun calculateLevel(): Int {
        var lvl = 1
        while (totalXp >= calculateXpForLevel(lvl + 1)) lvl++
        return lvl
    }

    private fun calculateXpForLevel(lvl: Int): Int {
        if (lvl <= 1) return 0
        return (lvl - 1) * (lvl - 1) * 30 + (lvl - 1) * 20
    }

    companion object {
        const val MAX_STAT = 5
    }
}
