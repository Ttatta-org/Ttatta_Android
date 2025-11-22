package com.umc.data.di.api

import com.squareup.moshi.Moshi
import com.umc.data.BuildConfig
import com.umc.data.api.ServerApi
import com.umc.data.exception.TokenExpiredException
import com.umc.data.preference.AuthPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServerApiModule {
    @Provides
    @Singleton
    fun provideServerApi(
        authPreference: AuthPreference,
        moshi: Moshi,
    ): ServerApi {

        val clientBuilder = OkHttpClient
            .Builder()
            .addInterceptor { chain ->
                val accessToken = authPreference.accessToken

                val requestBuilder = chain
                    .request()
                    .newBuilder()

                if (accessToken != null) {
                    requestBuilder.addHeader("Authorization", "Bearer $accessToken")
                }

                val response = chain.proceed(requestBuilder.build())

                if (accessToken != null) {
                    val contentLength = response.body?.contentLength() ?: -1L
                    val shouldPeekBody = contentLength != -1L && contentLength < 1024L

                    if (shouldPeekBody &&
                        response
                            .peekBody(1024L)
                            .string()
                            .contains("\"Token_Expired\"")
                    ) {
                        response.close()
                        throw TokenExpiredException(accessToken = accessToken)
                    }
                }

                response
            }

        if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            clientBuilder.addInterceptor(logger)
        }

        return Retrofit
            .Builder()
            .baseUrl(BuildConfig.SERVER_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(clientBuilder.build())
            .build()
            .create(ServerApi::class.java)
    }
}
