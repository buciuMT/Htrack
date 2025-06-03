// com/example/gym/data/FoodJournalRepository.kt
package com.example.gym.data

import com.example.gym.model.Aliment
import com.example.gym.model.JurnalAlimentarEntry

object FoodJournalRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun searchAlimente(query: String): List<Aliment> {
        return apiService.searchAlimente(query)
    }

    suspend fun addAlimentToJournal(userId: Int, alimentId: Int, tipMasa: String, cantitate: Int, date: String): Boolean {
        val requestBody = AddAlimentRequest(userId, alimentId, tipMasa, cantitate, date)
        val response = apiService.addAlimentToJournal(requestBody)
        return response.isSuccessful
    }

    suspend fun removeAlimentFromJournal(jurnalAlimentarId: Int): Boolean {
        val requestBody = RemoveAlimentRequest(jurnalAlimentarId)
        val response = apiService.removeAlimentFromJournal(requestBody)
        return response.isSuccessful
    }

    suspend fun getJournalEntriesByDateAndMeal(userId: Int, date: String, tipMasa: String): List<JurnalAlimentarEntry> {
        return apiService.getJournalEntriesByDateAndMeal(userId, date, tipMasa)
    }

    suspend fun getDailyCalories(userId: Int, date: String): Int {
        val response = apiService.getDailyCalories(userId, date)
        return response.total_calorii.toInt()
    }
}