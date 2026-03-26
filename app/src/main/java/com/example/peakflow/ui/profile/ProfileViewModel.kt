package com.example.peakflow.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.peakflow.data.MountainRepository
import com.example.peakflow.data.UserStats

class ProfileViewModel(private val repository: MountainRepository) : ViewModel() {

    val userStats: LiveData<UserStats> = repository.userStats
    val conqueredIds: LiveData<Set<Int>> = repository.conqueredIds

    val conqueredCount = MediatorLiveData<Int>().apply {
        addSource(conqueredIds) { value = it.size }
    }

    val totalMountains = MediatorLiveData<Int>().apply {
        addSource(repository.mountains) { value = it.size }
    }
}
