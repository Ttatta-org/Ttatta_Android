package com.umc.footprint.di

import android.content.Context
import com.umc.footprint.core.MapHandler
import com.umc.footprint.implementation.MapHandlerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapHandlerModule {
    @Provides
    @Singleton
    fun provideMapHandler(
        @ApplicationContext context: Context,
    ): MapHandler {
        return MapHandlerImpl(context = context)
    }
}