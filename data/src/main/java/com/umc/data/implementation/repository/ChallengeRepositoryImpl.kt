package com.umc.data.implementation.repository

import com.umc.core.model.Challenge
import com.umc.core.model.FailedChallenge
import com.umc.core.repository.ChallengeRepository
import com.umc.data.api.ServerApi
import com.umc.data.api.dto.server.CreateChallengeRequestDTO
import com.umc.data.preference.AuthPreference
import com.umc.data.util.withAuth
import javax.inject.Inject

class ChallengeRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference,
): ChallengeRepository {

    override suspend fun createChallenge(title: String, content: String) {
        val body = CreateChallengeRequestDTO(title = title, content = content)
        serverApi.withAuth(authPreference) { createChallenge(body = body) }
    }

    override suspend fun getChallenges(): List<Challenge> {
        val response = serverApi.withAuth(authPreference) { getChallenges() }
        return response.challengeList?.map {
            Challenge(
                id = it.challengeId!!,
                title = it.title!!,
                content = "",
                isCompleted = it.isCompleted!!
            )
        } ?: listOf()
    }

    override suspend fun completeChallenge(id: Long) {
        serverApi.withAuth(authPreference) { successChallenge(challengeId = id) }
    }

    override suspend fun getFailedChallenges(): List<FailedChallenge> {
        val response = serverApi.withAuth(authPreference) { getFailChallenges() }
        return response.failChallengeList?.map {
            FailedChallenge(
                id = it.challengeId!!,
                title = it.title!!,
                content = it.content!!,
                deadline = it.term!!
            )
        } ?: listOf()
    }
}