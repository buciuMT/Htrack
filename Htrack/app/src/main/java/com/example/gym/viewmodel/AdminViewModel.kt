package com.example.gym.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym.model.User
import com.example.gym.model.Trainer
import com.example.gym.data.*
import kotlinx.coroutines.launch
import androidx.compose.runtime.*

class AdminViewModel : ViewModel() {
    private val _users = mutableStateListOf<User>()
    val users: List<User> get() = _users

    var trainers by mutableStateOf<List<Trainer>>(emptyList())
        private set

    fun fetchUsers() {
        viewModelScope.launch {
            try {
                val result = RetrofitClient.apiService.getUsersByType("USER")
                _users.clear()
                _users.addAll(result)
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Eroare la fetchUsers: ${e.message}")
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
