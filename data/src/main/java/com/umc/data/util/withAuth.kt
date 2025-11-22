package com.umc.data.util

import com.umc.data.api.ServerApi
import com.umc.data.api.dto.BaseResponse
import com.umc.data.exception.TokenExpiredException
import com.umc.data.preference.AuthPreference
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private val mutex = Mutex(locked = false)

suspend fun <T> ServerApi.withAuth(
    authPreference: AuthPreference,
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