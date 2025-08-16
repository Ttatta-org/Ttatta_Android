package com.umc.data.di.preference

import android.content.Context
import com.umc.data.implementation.preference.SettingPreferenceImpl
import com.umc.data.preference.SettingPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingPreferenceModule {
    @Provides
    @Singleton
    fun provideSettingPreference(
        @ApplicationContext context: Context,
    ): SettingPreference {
        return SettingPreferenceImpl(context)
    }
}