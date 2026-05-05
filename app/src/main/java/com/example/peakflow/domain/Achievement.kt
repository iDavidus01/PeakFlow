package com.example.peakflow.domain

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.example.peakflow.R
import com.example.peakflow.data.Mountain
import com.example.peakflow.data.UserStats

sealed class Achievement(
    @StringRes val titleRes: Int,
    @StringRes val descRes: Int,
    val xpReward: Int,
    @ColorRes val colorRes: Int
) {
    abstract fun isUnlocked(conquered: List<Mountain>, stats: UserStats): Boolean
    open fun progressCurrent(conquered: List<Mountain>, stats: UserStats): Int =
        if (isUnlocked(conquered, stats)) 1 else 0
    open fun progressMax(): Int = 1

    companion object {
        fun all(): List<Achievement> = listOf(
            FirstSummit, FiveSummits, TenSummits, Himalayan,
            TotalHeight, MaxDifficulty, Globetrotter, LevelFive
        )
    }

    data object FirstSummit : Achievement(
        R.string.ach_first_summit_title, R.string.ach_first_summit_desc, 50, R.color.status_green
    ) {
        override fun isUnlocked(conquered: List<Mountain>, stats: UserStats) = conquered.isNotEmpty()
    }

    data object FiveSummits : Achievement(
        R.string.ach_five_summits_title, R.string.ach_five_summits_desc, 150, R.color.status_green
    ) {
        override fun isUnlocked(conquered: List<Mountain>, stats: UserStats) = conquered.size >= 5
        override fun progressCurrent(conquered: List<Mountain>, stats: UserStats) = minOf(conquered.size, 5)
        override fun progressMax() = 5
    }

    data object TenSummits : Achievement(
        R.string.ach_ten_summits_title, R.string.ach_ten_summits_desc, 300, R.color.accent_blue
    ) {
        override fun isUnlocked(conquered: List<Mountain>, stats: UserStats) = conquered.size >= 10
        override fun progressCurrent(conquered: List<Mountain>, stats: UserStats) = minOf(conquered.size, 10)
        override fun progressMax() = 10
    }

    data object Himalayan : Achievement(
        R.string.ach_himalayan_title, R.string.ach_himalayan_desc, 500, R.color.accent_orange
    ) {
        override fun isUnlocked(conquered: List<Mountain>, stats: UserStats) = conquered.any { it.height >= 8000 }
    }

    data object TotalHeight : Achievement(
        R.string.ach_total_height_title, R.string.ach_total_height_desc, 200, R.color.status_blue
    ) {
        override fun isUnlocked(conquered: List<Mountain>, stats: UserStats) = conquered.sumOf { it.height } >= 20000
        override fun progressCurrent(conquered: List<Mountain>, stats: UserStats) = minOf(conquered.sumOf { it.height }, 20000)
        override fun progressMax() = 20000
    }

    data object MaxDifficulty : Achievement(
        R.string.ach_max_difficulty_title, R.string.ach_max_difficulty_desc, 400, R.color.accent_red
    ) {
        override fun isUnlocked(conquered: List<Mountain>, stats: UserStats) = conquered.any { it.totalDifficulty >= 18 }
    }

    data object Globetrotter : Achievement(
        R.string.ach_globetrotter_title, R.string.ach_globetrotter_desc, 250, R.color.accent_purple
    ) {
        override fun isUnlocked(conquered: List<Mountain>, stats: UserStats) =
            conquered.map { it.region }.distinct().size >= 3
        override fun progressCurrent(conquered: List<Mountain>, stats: UserStats) =
            minOf(conquered.map { it.region }.distinct().size, 3)
        override fun progressMax() = 3
    }

    data object LevelFive : Achievement(
        R.string.ach_level_five_title, R.string.ach_level_five_desc, 300, R.color.status_yellow
    ) {
        override fun isUnlocked(conquered: List<Mountain>, stats: UserStats) = stats.level >= 5
        override fun progressCurrent(conquered: List<Mountain>, stats: UserStats) = minOf(stats.level, 5)
        override fun progressMax() = 5
    }
}
