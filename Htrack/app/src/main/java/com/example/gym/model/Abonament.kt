package com.example.gym.model
import com.google.gson.annotations.SerializedName

data class Abonament(
    @SerializedName("ID_USER") val idUser: Int,
    @SerializedName("TIP_ABONAMENT") val tipAbonament: String,
    @SerializedName("NUMAR_SEDINTE") val numarSedinte: Int,
    @SerializedName("DATA_START") val dataCreare: String,
    @SerializedName("DATA_FINALIZARE") val dataFinalizare: String
)

