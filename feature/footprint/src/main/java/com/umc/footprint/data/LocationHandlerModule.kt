package com.umc.footprint.data

import android.content.Context
import com.google.android.gms.location.LocationServices
import com.umc.footprint.core.LocationHandler
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
            locationSource = fusedLocationProviderClient,
        )
    }
}