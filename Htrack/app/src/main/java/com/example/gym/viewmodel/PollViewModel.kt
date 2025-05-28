package com.example.gym.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym.model.Poll
import com.example.gym.repository.PollRepository
import kotlinx.coroutines.launch

class PollViewModel(private val repo: PollRepository) : ViewModel() {
    val poll = MutableLiveData<Poll?>()
    val error = MutableLiveData<String>()

    fun getPoll(trainerId: Int) {
        viewModelScope.launch {
            val response = repo.getActivePoll(trainerId)
            if (response.isSuccessful) {
                poll.value = response.body()
            } else {
                error.value = "Eroare la încărcarea poll-ului"
            }
        }
    }

    fun vote(pollId: Int, userId: Int, hour: Int) {
        viewModelScope.launch {
            val response = repo.vote(pollId, userId, hour)
            if (!response.isSuccessful) {
                error.value = "Eroare la trimiterea votului"
            }
        }
    }
}
