package com.umc.data.di.repository

import com.umc.core.repository.SettingRepository
import com.umc.data.api.ServerApi
import com.umc.data.implementation.repository.SettingRepositoryImpl
import com.umc.data.preference.AuthPreference
import com.umc.data.preference.SettingPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingRepositoryModule {
    @Provides
    @Singleton
    fun provideSettingRepository(
        authPreference: AuthPreference,
        settingPreference: SettingPreference,
        serverApi: ServerApi,
    ): SettingRepository {
        return SettingRepositoryImpl(
            authPreference = authPreference,
            settingPreference = settingPreference,
            serverApi = serverApi,
        )
    }
}