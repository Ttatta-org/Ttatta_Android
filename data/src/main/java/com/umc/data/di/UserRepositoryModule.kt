package com.umc.data.di

import com.umc.core.repository.UserRepository
import com.umc.data.api.ServerApi
import com.umc.data.implementation.repository.UserRepositoryImpl
import com.umc.data.preference.AuthPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserRepositoryModule {
    @Provides
    @Singleton
    fun provideUserRepository(
        serverApi: ServerApi,
        authPreference: AuthPreference
    ): UserRepository {
        return UserRepositoryImpl(
            serverApi = serverApi,
            authPreference = authPreference,
        )
    }
}