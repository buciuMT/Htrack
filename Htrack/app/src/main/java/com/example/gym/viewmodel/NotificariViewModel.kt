package com.example.gym.viewmodel

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
            try {
                val response = RetrofitClient.apiService.marcheazaNotificariCitite(userId).execute()
                if (response.isSuccessful) {
                    val citite = _notificari.value.map {
                        NotificareFactory.creeaza(
                            tip = when (it) {
                                is NotificareAbonare -> "abonare"
                                is NotificareAnulareAbonament -> "anulare"
                                is NotificareGenerala -> "generala"
                            },
                            mesaj = it.mesaj,
                            data = it.data,
                            citit = true
                        )
                    }
                    _notificari.value = citite
                }
            } catch (e: Exception) {
            }
        }
    }
}
