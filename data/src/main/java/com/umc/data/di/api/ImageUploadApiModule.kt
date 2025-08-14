package com.umc.data.di.api

import com.umc.data.api.ImageUploadApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.time.Duration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageUploadApiModule {
    @Provides
    @Singleton
    fun provideImageUploadApi(): ImageUploadApi {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .writeTimeout(duration = Duration.ofMinutes(10))
            .build()

        return Retrofit.Builder()
            .baseUrl("https://dummy-base-url.com/")  // Dummy URL
            .client(client)
            .build()
            .create(ImageUploadApi::class.java)
    }
}