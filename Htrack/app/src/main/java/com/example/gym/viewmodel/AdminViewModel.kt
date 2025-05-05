package com.example.gym.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym.model.User
import com.example.gym.model.Trainer
import com.example.gym.data.*
import kotlinx.coroutines.launch
import androidx.compose.runtime.*

class AdminViewModel : ViewModel() {

    var users by mutableStateOf<List<User>>(emptyList())
        private set

    var trainers by mutableStateOf<List<Trainer>>(emptyList())
        private set

    fun fetchUsers() {
        viewModelScope.launch {
            try {
                users = RetrofitClient.apiService.getUsers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchTrainers() {
        viewModelScope.launch {
            try {
                trainers = RetrofitClient.apiService.getTrainers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addTrainer(name: String) {
        viewModelScope.launch {
            try {
                val newTrainer = Trainer(0, name)
                RetrofitClient.apiService.addTrainer(newTrainer)
                fetchTrainers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
