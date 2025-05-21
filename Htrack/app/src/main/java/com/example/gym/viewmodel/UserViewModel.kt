package com.example.gym.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym.data.RetrofitClient
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    var tipAbonament by mutableStateOf("NEACTIV")
        private set

    var numarSedinte by mutableStateOf(0)
        private set


    fun loadAbonamentActiv(userId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getAbonamentActiv(userId)
                tipAbonament = response.tip_abonament ?: "NEACTIV"   // dacă e null, pune "NEACTIV"
                numarSedinte = response.numar_sedinte
            } catch (e: Exception) {
                tipAbonament = "NEACTIV"
                numarSedinte = 0
            }
        }
    }


}
