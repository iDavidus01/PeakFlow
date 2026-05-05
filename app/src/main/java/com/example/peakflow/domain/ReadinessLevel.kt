package com.example.peakflow.domain

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.example.peakflow.R

sealed class ReadinessLevel(
    @StringRes val labelRes: Int,
    @ColorRes val colorRes: Int
) {
    data object Ready : ReadinessLevel(R.string.readiness_ready, R.color.status_green)
    data object Risky : ReadinessLevel(R.string.readiness_risky, R.color.status_orange)
    data object NotReady : ReadinessLevel(R.string.readiness_forget, R.color.status_red)

    companion object {
        fun from(score: Int): ReadinessLevel = when {
            score >= 80 -> Ready
            score >= 50 -> Risky
            else -> NotReady
        }
    }
}
