package com.umc.record.di

import android.content.Context
import com.google.android.gms.location.LocationServices
import com.umc.record.core.LocationHandler
import com.umc.record.data.LocationHandlerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationHandlerModule {
    @Provides
    @Singleton
    fun provideLocationHandler(
        @ApplicationContext context: Context
    ): LocationHandler {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        return LocationHandlerImpl(
            context = context,
            locationSource = fusedLocationProviderClient
        )
    }
}