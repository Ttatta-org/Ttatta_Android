package com.umc.data.di

import com.umc.core.repository.TtottuRepository
import com.umc.data.api.ServerApi
import com.umc.data.implementation.repository.TtottuRepositoryImpl
import com.umc.data.preference.AuthPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TtottuRepositoryModule {
    @Provides
    @Singleton
    fun provideTtottuRepository(
        serverApi: ServerApi,
        authPreference: AuthPreference,
    ): TtottuRepository {
        return TtottuRepositoryImpl(
            serverApi = serverApi,
            authPreference = authPreference,
        )
    }
}