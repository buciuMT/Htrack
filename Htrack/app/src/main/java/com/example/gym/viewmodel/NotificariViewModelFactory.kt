package com.example.gym.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NotificariViewModelFactory(private val userId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificariViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificariViewModel(userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
