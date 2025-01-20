package com.umc.data.di

import com.umc.core.Geocoder
import com.umc.data.GeocoderImpl
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
    fun bindGeocoder(): Geocoder {
        return GeocoderImpl()
    }
}