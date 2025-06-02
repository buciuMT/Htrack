package com.example.gym.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym.data.RetrofitClient
import com.example.gym.data.StartChatRequest
import com.example.gym.model.MessageDto
import com.example.gym.model.SendMessageRequest
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val api = RetrofitClient.apiService

    var messages by mutableStateOf<List<MessageDto>>(emptyList())
    var conversationId by mutableStateOf<Int?>(null)
    var antrenorId by mutableStateOf<Int?>(null)
        private set

    fun loadAntrenorId(userId: Int) {
        viewModelScope.launch {
            try {
                val response = api.getAntrenorId(userId)
                Log.d("loadAntrenorId", "Răspuns: $response")
                antrenorId = response.id_trainer
                Log.d("loadAntrenorId", "Setat antrenorId = ${antrenorId}")
            } catch (e: Exception) {
                Log.e("loadAntrenorId", "Eroare la obținere antrenorId", e)
            }
        }
    }


    suspend fun startChat(userId: Int, trainerId: Int?): Int {
        requireNotNull(trainerId) { "trainerId este null!" }

        val response = api.startChat(StartChatRequest(userId, trainerId))
        conversationId = response.id_conversatie
        return conversationId!!
    }


    suspend fun sendMessage(senderId: Int, message: String) {
        conversationId?.let {
            try {
                api.sendMessage(SendMessageRequest(it, senderId, message))
                loadMessages(it) // Reîncarcă mesajele
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Eroare la trimitere mesaj: ${e.message}")
            }
        }
    }

    suspend fun loadMessages(conversationId: Int) {
        try {
            messages = api.getMessages(conversationId)
        } catch (e: Exception) {
            Log.e("ChatViewModel", "Eroare la încărcare mesaje: ${e.message}")
        }
    }
}
