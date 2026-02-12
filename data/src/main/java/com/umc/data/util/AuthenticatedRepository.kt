package com.umc.data.util

import com.umc.data.api.ServerApi
import com.umc.data.api.dto.BaseResponse
import com.umc.data.api.dto.ErrorResponse
import com.umc.data.exception.ServerException
import com.umc.data.exception.TokenExpiredException
import com.umc.data.preference.AuthPreference
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import retrofit2.HttpException

private val mutex = Mutex(locked = false)

interface AuthenticatedRepository {
    val authPreference: AuthPreference

    suspend fun <T> ServerApi.withAuth(
        routine: suspend ServerApi.() -> BaseResponse<T>,
    ): T {
        try {
            return withCheck { routine() }
        } catch (e: TokenExpiredException) {
            mutex.withLock {
                if (e.accessToken != authPreference.accessToken) return@withLock

                val response = withCheck {
                    refreshToken(authPreference.refreshToken!!)
                }

                authPreference.refreshToken = response.refreshToken!!
                authPreference.accessToken = response.accessToken!!
            }

            return withCheck { routine() }
        }
    }

    suspend fun <T> ServerApi.withCheck(
        getter: suspend ServerApi.() -> BaseResponse<T>
    ): T {
        val response = try {
            getter()
        } catch (e: HttpException) {
            if (e.code() !in 400 until 500) throw e

            val bodyString = e
                .response()
                ?.errorBody()
                ?.string() ?: throw e

            val body = Json.decodeFromString<ErrorResponse>(bodyString)

            throw ServerException(
                code = body.code,
                message = body.message,
            )
        }

        if (!response.isSuccess) {
            throw ServerException(
                code = response.code,
                message = response.message,
            )
        }

        return response.result
    }
}