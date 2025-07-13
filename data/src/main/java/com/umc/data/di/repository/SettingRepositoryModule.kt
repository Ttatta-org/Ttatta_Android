package com.umc.data.di.repository

import android.content.Context
import com.umc.core.setting.SettingRepository
import com.umc.data.setting.SettingRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingRepositoryModule {

    @Provides
    @Singleton
    fun provideSettingRepository(
        @ApplicationContext context: Context
    ): SettingRepository {
        return SettingRepositoryImpl(context)
    }
}
