package com.example.gym.model

data class Poll(
    val id: Int,
    val trainerId: Int,
    val isActive: Boolean,
    val votes: List<Vote> = listOf()
)