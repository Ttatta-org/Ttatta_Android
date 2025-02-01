package com.umc.record.data

import android.content.Context
import com.umc.record.core.LocationHandler
import com.umc.record.core.MapHandler
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
        locationHandler: LocationHandler
    ): MapHandler {
        return MapHandlerImpl(
            context = context,
            locationHandler = locationHandler,
        )
    }
}