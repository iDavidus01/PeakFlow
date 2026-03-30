package com.example.peakflow.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.peakflow.data.Mountain
import com.example.peakflow.data.MountainRepository

class HomeViewModel(private val repository: MountainRepository) : ViewModel() {

    val mountains: LiveData<List<Mountain>> = repository.mountains
    val conqueredIds: LiveData<Set<Int>> = repository.conqueredIds
    val userStats = repository.userStats

    private val _searchQuery = MutableLiveData("")

    val nextGoal = MediatorLiveData<Mountain?>().apply {
        addSource(mountains) { updateNextGoal() }
        addSource(conqueredIds) { updateNextGoal() }
        addSource(userStats) { updateNextGoal() }
    }

    private fun MediatorLiveData<Mountain?>.updateNextGoal() {
        val all = mountains.value ?: return
        val conquered = conqueredIds.value ?: return
        val stats = userStats.value ?: return

        val unconquered = all.filter { it.id !in conquered }.sortedBy { it.height }
        
        val possibleGoals = unconquered.filter { it.requiredLevel <= stats.level }
        value = possibleGoals.lastOrNull() ?: unconquered.firstOrNull()
    }

    val filteredMountains = MediatorLiveData<List<Mountain>>().apply {
        addSource(mountains) { updateFilter() }
        addSource(_searchQuery) { updateFilter() }
    }

    private fun MediatorLiveData<List<Mountain>>.updateFilter() {
        val query = _searchQuery.value.orEmpty().lowercase()
        val all = mountains.value.orEmpty()
        value = if (query.isBlank()) all
        else all.filter {
            it.name.lowercase().contains(query) ||
                    it.region.lowercase().contains(query)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}