package com.example.peakflow.ui

import android.view.View
import com.example.peakflow.R
import com.example.peakflow.data.Mountain

fun View.animateClick(action: () -> Unit) {
    animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction {
        animate().scaleX(1f).scaleY(1f).setDuration(100).withEndAction(action)
    }
}

fun Mountain.difficultyColorRes(): Int = when {
    totalDifficulty < 10 -> R.color.status_green
    totalDifficulty in 10..14 -> R.color.status_yellow
    totalDifficulty in 15..18 -> R.color.status_orange
    else -> R.color.status_red
}

val Mountain.heightDisplay: String get() = "$height m n.p.m."
