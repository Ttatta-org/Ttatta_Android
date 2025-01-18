package com.umc.footprint.data

import android.content.Context
import com.umc.footprint.core.MapHandler
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
        @ApplicationContext context: Context
    ): MapHandler {
        return MapHandlerImpl(context = context)
    }
}