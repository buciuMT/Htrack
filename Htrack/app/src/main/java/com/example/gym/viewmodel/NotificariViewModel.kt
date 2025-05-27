package com.example.gym.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym.model.*
import com.example.gym.data.RetrofitClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NotificariViewModel(private val userId: Int) : ViewModel() {

    private val _notificari = MutableStateFlow<List<Notificare>>(emptyList())
    val notificari: StateFlow<List<Notificare>> = _notificari

    val unreadCount: StateFlow<Int> = _notificari
        .map { list -> list.count { !it.citit } }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    init {
        loadNotificari()
    }

    fun loadNotificari() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getNotificariUser(userId)
                val notificariCreate = response.map {
                    NotificareFactory.creeaza(
                        tip = deduceTipNotificare(it.mesaj),
                        mesaj = it.mesaj,
                        data = it.data,
                        citit = it.citit
                    )
                }
                _notificari.value = notificariCreate
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }

    }
    fun deduceTipNotificare(mesaj: String): String {
        return when {
            mesaj.contains("activat", ignoreCase = true) -> "abonare"

            mesaj.contains("dezactivat", ignoreCase = true) -> "anulare"

            else -> "generala"
        }
    }



    fun marcheazaToateCitite() {
        viewModelScope.launch {
            Log.d("NotificariVM", "Am intrat în marcheazaToateCitite")
            try {
                val response = RetrofitClient.apiService.marcheazaNotificariCitite(userId)
                Log.d("NotificariVM", "Cod răspuns: ${response.code()}")
                if (response.isSuccessful) {
                    loadNotificari()
                } else {
                    Log.e("NotificariVM", "Eșuat: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("NotificariVM", "Eroare rețea: ${e.message}")
            }
        }
    }


}
