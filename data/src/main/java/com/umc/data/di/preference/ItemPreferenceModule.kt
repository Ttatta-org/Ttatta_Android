package com.umc.data.di.preference

import android.content.Context
import com.umc.data.implementation.preference.ItemPreferenceImpl
import com.umc.data.preference.ItemPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ItemPreferenceModule {
    @Provides
    @Singleton
    fun provideItemPreference(
        @ApplicationContext context: Context
    ): ItemPreference {
        return ItemPreferenceImpl(context)
    }
}