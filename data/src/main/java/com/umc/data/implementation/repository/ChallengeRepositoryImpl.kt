package com.umc.data.implementation.repository

import com.umc.core.model.Challenge
import com.umc.core.model.FailedChallenge
import com.umc.core.repository.ChallengeRepository
import com.umc.data.api.ServerApi
import com.umc.data.preference.AuthPreference
import javax.inject.Inject

class ChallengeRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference,
): ChallengeRepository {

    override suspend fun createChallenge(title: String, content: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getChallenges(): List<Challenge> {
        TODO("Not yet implemented")
    }

    override suspend fun completeChallenge(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun getFailedChallenges(): List<FailedChallenge> {
        TODO("Not yet implemented")
    }
}