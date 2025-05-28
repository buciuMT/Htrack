package com.example.gym.model
import com.google.gson.annotations.SerializedName
data class Poll(
    @SerializedName("id_poll") val id: Int,
    @SerializedName("id_trainer") val trainerId: Int,
    @SerializedName("activ") val isActive: Boolean,
    @SerializedName("data") val date: String
)