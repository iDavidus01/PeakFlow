package com.example.peakflow.ui.path

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peakflow.data.Mountain
import com.example.peakflow.data.MountainRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PathViewModel(private val repository: MountainRepository) : ViewModel() {

    val conqueredIds: StateFlow<Set<Int>> = repository.conqueredIds

    val sortedMountains: StateFlow<List<Mountain>> = repository.mountains
        .map { mountains -> mountains.sortedBy { it.totalDifficulty } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val nextSuggestedIndex: StateFlow<Int> = combine(sortedMountains, conqueredIds) { mountains, conquered ->
        mountains.indexOfFirst { it.id !in conquered }.takeIf { it >= 0 } ?: -1
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), -1)
}
