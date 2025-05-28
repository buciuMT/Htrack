package com.example.gym.model
import com.google.gson.annotations.SerializedName
data class Vote(
    @SerializedName("id_vote") val id: Int = 0,
    @SerializedName("id_poll") val pollId: Int,
    @SerializedName("id_user") val userId: Int,
    @SerializedName("ora") val hour: Int,
    @SerializedName("data_vot") val voteDate: String
)