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


    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount
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
                updateUnreadCount()
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
    private fun updateUnreadCount() {
        val count = _notificari.value.count { !it.citit }
        _unreadCount.value = count
    }



    fun marcheazaToateCitite() {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.marcheazaNotificariCitite(userId)
                _notificari.value = _notificari.value.map { it.copyWithCitit(citit = true) }
                updateUnreadCount()
            } catch (e: Exception) {
                Log.e("Notificari", "Eroare la marcare notificări ca citite", e)
            }
        }
    }

}
