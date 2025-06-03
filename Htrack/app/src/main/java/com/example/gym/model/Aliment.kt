package com.example.gym.model

import com.google.gson.annotations.SerializedName // Importă această adnotare

data class Aliment(
    @SerializedName("ID_Aliment") // Numele exact din JSON-ul Go
    val id_aliment: Int, // Numele câmpului din Kotlin
    @SerializedName("Nume")
    val nume: String,
    @SerializedName("Calorii")
    val calorii: Double,
    @SerializedName("Proteine")
    val proteine: Double,
    @SerializedName("Carbohidrati")
    val carbohidrati: Double,
    @SerializedName("Zaharuri")
    val zaharuri: Double,
    @SerializedName("Grasimi_Saturate")
    val grasimi_saturate: Double,
    @SerializedName("Grasimi_Nesaturate")
    val grasimi_nesaturate: Double,
    @SerializedName("Fibre")
    val fibre: Double
)