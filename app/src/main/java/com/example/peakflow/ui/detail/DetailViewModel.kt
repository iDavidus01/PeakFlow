package com.example.peakflow.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.peakflow.data.Mountain
import com.example.peakflow.data.MountainRepository

class DetailViewModel(private val repository: MountainRepository) : ViewModel() {

    private val _mountain = MutableLiveData<Mountain>()
    val mountain: LiveData<Mountain> = _mountain

    private val _isConquered = MutableLiveData<Boolean>()
    val isConquered: LiveData<Boolean> = _isConquered

    fun loadMountain(id: Int) {
        val m = repository.getMountainById(id)
        _mountain.value = m
        _isConquered.value = repository.isConquered(id)
    }

    fun toggleConquered() {
        val m = _mountain.value ?: return
        repository.toggleConquered(m.id)
        _isConquered.value = repository.isConquered(m.id)
    }
}
