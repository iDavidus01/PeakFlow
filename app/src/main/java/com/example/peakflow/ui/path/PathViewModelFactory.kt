package com.example.peakflow.ui.path

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.peakflow.data.MountainRepository

class PathViewModelFactory(private val repository: MountainRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = PathViewModel(repository) as T
}
