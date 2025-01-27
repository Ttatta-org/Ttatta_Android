package com.umc.data.di

import com.umc.core.repository.DiaryRepository
import com.umc.data.api.ServerApi
import com.umc.data.implementation.repository.DiaryRepositoryImpl
import com.umc.data.preference.AuthPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DiaryRepositoryModule {
    @Provides
    @Singleton
    fun provideDiaryRepository(
        serverApi: ServerApi,
        authPreference: AuthPreference
    ): DiaryRepository {
        return DiaryRepositoryImpl(
            serverApi = serverApi,
            authPreference = authPreference,
        )
    }
}