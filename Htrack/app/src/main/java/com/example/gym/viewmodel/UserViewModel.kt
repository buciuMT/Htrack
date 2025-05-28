package com.example.gym.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym.data.RetrofitClient
import com.example.gym.data.RetrofitClient.apiService
import com.example.gym.model.Abonament
import com.example.gym.model.Poll
import com.example.gym.repository.PollRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    var tipAbonament by mutableStateOf("NEACTIV")
        private set

    var numarSedinte by mutableStateOf(0)
        private set

    val istoricAbonamente = mutableStateListOf<Abonament>()

    private val _pollActive = MutableStateFlow<Poll?>(null)
    val pollActive: StateFlow<Poll?> = _pollActive
    private val repository = PollRepository(RetrofitClient.apiService)

    fun loadPollActiv(userId: Int) {
        viewModelScope.launch {
            val poll = repository.getPollActivByUserId(userId)
            _pollActive.value = poll
        }
    }


    suspend fun voteaza(idPoll: Int, userId: Int, ora: Int): Boolean {
        return try {
            val body = mapOf(
                "id_poll" to idPoll,
                "id_user" to userId,
                "ora" to ora
            )
            val response = apiService.vote(body)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }


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
    suspend fun hasVoted(userId: Int, pollId: Int): Boolean {
        return try {
            val response = apiService.getVoteByUserAndPoll(pollId, userId)
            response.isSuccessful && response.body() != null
        } catch (e: Exception) {
            false
        }
    }



    fun loadIstoricAbonamente(userId: Int) {
        viewModelScope.launch {
            try {
                val list = RetrofitClient.apiService.getIstoricAbonamente(userId)
                istoricAbonamente.clear()
                istoricAbonamente.addAll(list)
            } catch (e: Exception) {
                // Poți adăuga un Log dacă vrei
            }
        }
    }
}
