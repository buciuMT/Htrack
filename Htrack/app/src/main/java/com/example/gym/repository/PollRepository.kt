package com.example.gym.repository

import com.example.gym.data.ApiService
import com.example.gym.data.VoteResponse
import com.example.gym.model.Poll

class PollRepository(private val api: ApiService) {
    suspend fun createPoll(trainerId: Int) = api.createPoll(mapOf("id_trainer" to trainerId))
    suspend fun deactivatePoll(pollId: Int) = api.deactivatePoll(pollId)
    suspend fun getActivePoll(trainerId: Int) = api.getActivePoll(trainerId)
    suspend fun vote(pollId: Int, userId: Int, hour: Int) =
        api.vote(mapOf("id_poll" to pollId, "id_user" to userId, "ora" to hour))

    suspend fun getActivePollForTrainer(trainerId: Int): Poll? {
        val response = api.getActivePoll(trainerId)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getVotesForPoll(pollId: Int): List<VoteResponse> {
        return try {
            val response = api.getVotesForPoll(pollId)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }



    suspend fun checkIfUserHasVoted(userId: Int, pollId: Int): Boolean {
        val response = api.getVoteByUserAndPoll(pollId, userId)
        return response.isSuccessful && response.body() != null
    }
    suspend fun getPollActivByUserId(userId: Int): Poll? {
        val response = api.getPollActivByUserId(userId)
        return if (response.isSuccessful) response.body() else null
    }

}
