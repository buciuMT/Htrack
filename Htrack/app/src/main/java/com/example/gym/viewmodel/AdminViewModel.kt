package com.example.gym.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym.model.User
import com.example.gym.data.*
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

class AdminViewModel : ViewModel() {
    private val _users = mutableStateListOf<User>()
    val users: SnapshotStateList<User> get() = _users

    private val _trainers = mutableStateListOf<User>()
    val trainers: SnapshotStateList<User> get() = _trainers

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
                val result = RetrofitClient.apiService.getTrainers()
                Log.d("fetchTrainers", "Traineri primiți: ${result.map { it.tip_user }}") // DEBUG
                _trainers.clear()
                _trainers.addAll(result)
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Eroare la fetchTrainers: ${e.message}")
            }
        }
    }



    fun transformaUserInTrainer(userId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.transformUserToTrainer(userId)
                if (response.isSuccessful) {
                    fetchUsers()
                    fetchTrainers()
                } else {
                    Log.e("TransformUser", "Transformare eșuată: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("TransformUser", "Eroare: ${e.message}")
            }
        }
    }

    fun assignTrainer(userId: Int, trainerId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.assignTrainerToUser(userId, trainerId)
                if (response.isSuccessful) {
                    fetchUsers()
                    fetchUsersFaraAntrenor()
                } else {
                    Log.e("AssignTrainer", "Eroare: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("AssignTrainer", "Exceptie: ${e.message}")
            }
        }
    }
    fun fetchUsersFaraAntrenor() {
        viewModelScope.launch {
            try {
                val result = RetrofitClient.apiService.getUsersFaraAntrenor()
                _users.clear()
                _users.addAll(result)
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Eroare la fetchUsersFaraAntrenor: ${e.message}")
            }
        }
    }

}
