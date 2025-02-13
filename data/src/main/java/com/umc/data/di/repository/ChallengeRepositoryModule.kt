package com.umc.data.di.repository

import com.umc.core.repository.ChallengeRepository
import com.umc.data.api.ServerApi
import com.umc.data.implementation.repository.ChallengeRepositoryImpl
import com.umc.data.preference.AuthPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChallengeRepositoryModule {
    @Provides
    @Singleton
    fun provideChallengeRepository(
        serverApi: ServerApi,
        authPreference: AuthPreference,
    ): ChallengeRepository {
        return ChallengeRepositoryImpl(
            serverApi = serverApi,
            authPreference = authPreference,
        )
    }
}