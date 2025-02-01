package com.umc.data.di

import com.google.gson.Gson
import com.umc.data.BuildConfig
import com.umc.data.api.ServerApi
import com.umc.data.preference.AuthPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServerApiModule {
    @Provides
    @Singleton
    fun provideServerApi(
        authPreference: AuthPreference,
        gson: Gson,
    ): ServerApi {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addNetworkInterceptor {
                val request = it.request()
                    .newBuilder()
                    .let { builder ->
                        authPreference.accessToken?.let { token ->
                            builder.addHeader("Authorization", "Bearer $token")
                        } ?: builder
                    }
                    .build()
                it.proceed(request)
            }
            .addInterceptor(logger)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
            .create(ServerApi::class.java)
    }
}