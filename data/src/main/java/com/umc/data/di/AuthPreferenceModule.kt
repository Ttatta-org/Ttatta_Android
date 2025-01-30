package com.umc.data.di

import android.content.Context
import com.umc.data.implementation.preference.AuthPreferenceImpl
import com.umc.data.preference.AuthPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthPreferenceModule {
    @Provides
    @Singleton
    fun provideAuthPreference(
        @ApplicationContext context: Context
    ): AuthPreference {
        return AuthPreferenceImpl(context)
    }
}