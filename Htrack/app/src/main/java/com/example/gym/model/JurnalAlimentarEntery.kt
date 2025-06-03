package com.example.gym.model

import com.google.gson.annotations.SerializedName

data class JurnalAlimentarEntry(
    @SerializedName("id_jurnal_alimentar") // Match "id_jurnal_alimentar"
    val id_jurnal_alimentar: Int,
    @SerializedName("id_user")           // Match "id_user"
    val id_user: Int,
    @SerializedName("id_aliment")        // Match "id_aliment"
    val id_aliment: Int, // Added missing id_aliment in constructor
    @SerializedName("tip_masa")          // Match "tip_masa"
    val tip_masa: String,
    @SerializedName("cantitate")         // Match "cantitate"
    val cantitate: Int,
    @SerializedName("data_adaugare")     // Match "data_adaugare" from JSON
    val data: String, // Keep as String
    @SerializedName("aliment")           // Match "aliment"
    val aliment: Aliment
)