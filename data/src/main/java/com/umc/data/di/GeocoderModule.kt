package com.umc.data.di

import com.umc.core.Geocoder
import com.umc.data.implementation.GeocoderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GeocoderModule {
    @Provides
    @Singleton
    fun provideGeocoder(): Geocoder {
        return GeocoderImpl()
    }
}