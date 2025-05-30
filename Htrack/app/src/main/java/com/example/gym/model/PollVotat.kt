package com.example.gym.model

data class PollVotat(
    val id_poll: Int,
    val trainer_id: Int,
    val is_active: Boolean,
    val data: String,
    val ora_selectata: Int
)
