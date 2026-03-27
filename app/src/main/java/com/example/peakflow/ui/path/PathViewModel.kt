package com.example.peakflow.ui.path

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.peakflow.data.Mountain
import com.example.peakflow.data.MountainRepository

class PathViewModel(private val repository: MountainRepository) : ViewModel() {

    val conqueredIds: LiveData<Set<Int>> = repository.conqueredIds

    // Mountains sorted by total difficulty (sum of all requirements)
    val sortedMountains = MediatorLiveData<List<Mountain>>().apply {
        addSource(repository.mountains) { mountains ->
            value = mountains.sortedBy { it.condReq + it.techReq + it.acclReq + it.riskReq }
        }
    }

    // Index of the next suggested mountain (first unconquered in the sorted list)
    val nextSuggestedIndex = MediatorLiveData<Int>().apply {
        addSource(sortedMountains) { updateSuggested() }
        addSource(conqueredIds) { updateSuggested() }
    }

    private fun MediatorLiveData<Int>.updateSuggested() {
        val mountains = sortedMountains.value.orEmpty()
        val conquered = conqueredIds.value.orEmpty()
        val idx = mountains.indexOfFirst { it.id !in conquered }
        value = if (idx >= 0) idx else -1
    }
}
