package com.example.gym.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym.model.*
import com.example.gym.data.RetrofitClient
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    var tipAbonament by mutableStateOf("NEACTIV")
        private set

    var numarSedinte by mutableStateOf(0)
        private set
    val istoricAbonamente = mutableStateListOf<Abonament>()

    fun loadAbonamentActiv(userId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getAbonamentActiv(userId)
                tipAbonament = response.tipAbonament ?: "NEACTIV"
                numarSedinte = response.numarSedinte
            } catch (e: Exception) {
                tipAbonament = "NEACTIV"
                numarSedinte = 0
            }
        }
    }
    fun loadIstoricAbonamente(userId: Int) {
        viewModelScope.launch {
            try {
                val list = RetrofitClient.apiService.getIstoricAbonamente(userId)
                istoricAbonamente.clear()
                istoricAbonamente.addAll(list)
            } catch (e: Exception) {
            }
        }
    }

}
