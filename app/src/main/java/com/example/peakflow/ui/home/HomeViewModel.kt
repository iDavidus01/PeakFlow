package com.example.peakflow.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peakflow.data.Mountain
import com.example.peakflow.data.MountainRepository
import com.example.peakflow.data.UserStats
import com.example.peakflow.domain.SortOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val repository: MountainRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _sortOrder = MutableStateFlow(SortOrder.DEFAULT)
    private val _regionFilter = MutableStateFlow<String?>(null)

    val conqueredIds: StateFlow<Set<Int>> = repository.conqueredIds
    val userStats: StateFlow<UserStats> = repository.userStats

    val regions: StateFlow<List<String>> = repository.mountains
        .map { mountains -> mountains.map { it.region }.distinct().sorted() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredMountains: StateFlow<List<Mountain>> = combine(
        repository.mountains,
        _searchQuery,
        _sortOrder,
        _regionFilter
    ) { mountains, query, sort, region ->
        mountains
            .filter { m -> region == null || m.region == region }
            .filter { m ->
                query.isBlank() ||
                    m.name.lowercase().contains(query.lowercase()) ||
                    m.region.lowercase().contains(query.lowercase())
            }
            .let { list ->
                when (sort) {
                    SortOrder.HEIGHT_DESC -> list.sortedByDescending { it.height }
                    SortOrder.DIFFICULTY_ASC -> list.sortedBy { it.totalDifficulty }
                    SortOrder.DEFAULT -> list
                }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val nextGoal: StateFlow<Mountain?> = combine(
        repository.mountains,
        repository.conqueredIds,
        repository.userStats
    ) { mountains, conquered, stats ->
        val unconquered = mountains.filter { it.id !in conquered }.sortedBy { it.height }
        unconquered.filter { it.requiredLevel <= stats.level }.lastOrNull()
            ?: unconquered.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setSortOrder(order: SortOrder) { _sortOrder.value = order }
    fun setRegionFilter(region: String?) { _regionFilter.value = region }
}
