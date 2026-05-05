package com.example.peakflow.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peakflow.data.Mountain
import com.example.peakflow.data.MountainRepository
import com.example.peakflow.data.UserStats
import com.example.peakflow.domain.Achievement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class AchievementState(
    val achievement: Achievement,
    val isUnlocked: Boolean,
    val current: Int,
    val max: Int
)

class ProfileViewModel(private val repository: MountainRepository) : ViewModel() {

    val userStats: StateFlow<UserStats> = repository.userStats

    val conqueredCount: StateFlow<Int> = repository.conqueredIds
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalMountains: StateFlow<Int> = repository.mountains
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val conqueredHistory: StateFlow<List<Pair<Mountain, Long>>> = combine(
        repository.mountains, repository.conqueredTimestamps
    ) { mountains, timestamps ->
        timestamps.entries
            .mapNotNull { (id, ts) -> mountains.find { it.id == id }?.let { it to ts } }
            .sortedByDescending { (_, ts) -> ts }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val conqueredMountains: Flow<List<Mountain>> = combine(
        repository.mountains, repository.conqueredIds
    ) { mountains, conquered -> mountains.filter { it.id in conquered } }

    val totalHeightClimbed: StateFlow<Int> = conqueredMountains
        .map { it.sumOf { m -> m.height } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val hardestConquered: StateFlow<Mountain?> = conqueredMountains
        .map { it.maxByOrNull { m -> m.totalDifficulty } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val highestConquered: StateFlow<Mountain?> = conqueredMountains
        .map { it.maxByOrNull { m -> m.height } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val regionsConquered: StateFlow<Int> = conqueredMountains
        .map { it.map { m -> m.region }.distinct().size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val achievementStates: StateFlow<List<AchievementState>> = combine(
        conqueredMountains, repository.userStats
    ) { mountains, stats ->
        Achievement.all()
            .map { ach ->
                AchievementState(
                    achievement = ach,
                    isUnlocked = ach.isUnlocked(mountains, stats),
                    current = ach.progressCurrent(mountains, stats),
                    max = ach.progressMax()
                )
            }
            .sortedByDescending { it.isUnlocked }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
