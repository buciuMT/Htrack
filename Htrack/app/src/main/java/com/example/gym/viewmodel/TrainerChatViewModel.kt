package com.example.gym.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym.data.RetrofitClient
import com.example.gym.data.StartChatRequest
import com.example.gym.model.Conversatie
import com.example.gym.model.MessageDto
import com.example.gym.model.SendMessageRequest
import com.example.gym.model.User
import kotlinx.coroutines.launch

class TrainerChatViewModel : ViewModel() {
    private val api = RetrofitClient.apiService

    var userList by mutableStateOf<List<User>>(emptyList())
    var messages by mutableStateOf<List<MessageDto>>(emptyList())
    var conversationId by mutableStateOf<Int?>(null)

    fun loadUsersForTrainer(trainerId: Int) {
        viewModelScope.launch {
            try {
                userList = api.getUsersForTrainer(trainerId)
            } catch (e: Exception) {
                Log.e("TrainerChatVM", "Eroare la loadUsersForTrainer", e)
            }
        }
    }

    suspend fun startChat(userId: Int, trainerId: Int) {
        try {
            val response = api.startChat(StartChatRequest(userId, trainerId))
            conversationId = response.id_conversatie
            loadMessages(conversationId!!)
        } catch (e: Exception) {
            Log.e("TrainerChatVM", "Eroare la startChat", e)
        }
    }

    suspend fun loadMessages(convId: Int) {
        try {
            messages = api.getMessages(convId)
        } catch (e: Exception) {
            Log.e("TrainerChatVM", "Eroare la loadMessages", e)
        }
    }

    suspend fun sendMessage(senderId: Int, mesaj: String) {
        conversationId?.let {
            api.sendMessage(SendMessageRequest(it, senderId, mesaj))
            loadMessages(it)
        }
    }
}

