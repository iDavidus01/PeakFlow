package com.example.peakflow.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.peakflow.data.MountainRepository

class DetailViewModelFactory(private val repository: MountainRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = DetailViewModel(repository) as T
}
