package com.example.gym.model

data class StartChatRequest(val id_user: Int, val id_trainer: Int)
data class SendMessageRequest(val id_conversation: Int, val id_sender: Int, val mesaj: String)

data class MessageDto(
    val id_message: Int,
    val id_sender: Int,
    val mesaj: String,
    val timestamp: String,
    val vazut: Boolean
)

data class StartChatResponse(val id_conversatie: Int)
