package com.umc.core.repository

import com.umc.core.model.Challenge
import com.umc.core.model.FailedChallenge

interface ChallengeRepository {
    suspend fun createChallenge(title: String, content: String)
    suspend fun getChallenges(): List<Challenge>
    suspend fun completeChallenge(id: Long)
    suspend fun getFailedChallenges(): List<FailedChallenge>
}