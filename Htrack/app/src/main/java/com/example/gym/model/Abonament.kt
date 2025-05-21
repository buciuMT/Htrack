package com.example.gym.model
import com.google.gson.annotations.SerializedName

data class Abonament(
    @SerializedName("TIP_ABONAMENT")
    val tip_abonament: String?,

    @SerializedName("NUMAR_SEDINTE")
    val numar_sedinte: Int
)
