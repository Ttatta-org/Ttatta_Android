package com.umc.data.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.umc.data.serializer.LocalDateSerializer
import com.umc.data.serializer.LocalDateTimeSerializer
import com.umc.data.serializer.LocalTimeSerializer
import com.umc.data.serializer.OffsetDateTimeSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MoshiModule {
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(OffsetDateTimeSerializer())
            .add(LocalDateSerializer())
            .add(LocalDateTimeSerializer())
            .add(LocalTimeSerializer())
            .build()
    }
}