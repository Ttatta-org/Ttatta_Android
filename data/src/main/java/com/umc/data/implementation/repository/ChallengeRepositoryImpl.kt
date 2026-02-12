package com.umc.data.implementation.repository

import com.umc.core.model.Challenge
import com.umc.core.model.FailedChallenge
import com.umc.core.repository.ChallengeRepository
import com.umc.data.api.ServerApi
import com.umc.data.api.dto.server.CreateChallengeRequestDTO
import com.umc.data.preference.AuthPreference
import com.umc.data.util.AuthenticatedRepository
import javax.inject.Inject

class ChallengeRepositoryImpl @Inject constructor(
    override val authPreference: AuthPreference,
    private val serverApi: ServerApi,
): ChallengeRepository, AuthenticatedRepository {

    override suspend fun createChallenge(title: String, content: String) {
        val body = CreateChallengeRequestDTO(title = title, content = content)
        serverApi.withAuth { createChallenge(body = body) }
    }

    override suspend fun getChallenges(): List<Challenge> {
        val response = serverApi.withAuth { getChallenges() }
        return response.challengeList?.map {
            Challenge(
                id = it.challengeId!!,
                title = it.title!!,
                content = it.content!!,
                isCompleted = it.isCompleted!!
            )
        } ?: listOf()
    }

    override suspend fun completeChallenge(id: Long) {
        serverApi.withAuth { successChallenge(challengeId = id) }
    }

    override suspend fun getFailedChallenges(): List<FailedChallenge> {
        val response = serverApi.withAuth { getFailChallenges() }
        return response.failChallengeList?.map {
            FailedChallenge(
                id = it.challengeId!!,
                title = it.title!!,
                content = it.content!!,
                deadline = it.term!!
            )
        } ?: listOf()
    }

    override suspend fun getPastChallenges(): List<Challenge> {
        val response = serverApi.withAuth { getAllPastChallenges() }
        return response.getAllPastChallengeResultDTOList
            ?.map {
                Challenge(
                    id = it.challengeId!!,
                    title = it.title!!,
                    content = it.content ?: "",
                    isCompleted = it.completed!!
                )
            } ?: emptyList()
    }
}