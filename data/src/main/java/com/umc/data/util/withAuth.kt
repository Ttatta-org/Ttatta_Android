package com.umc.data.util

import com.umc.data.api.ServerApi
import com.umc.data.api.dto.BaseResponse
import com.umc.data.preference.AuthPreference
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException

private val mutex = Mutex(locked = false)

suspend fun <T> ServerApi.withAuth(
    authPreference: AuthPreference,
    routine: suspend ServerApi.() -> BaseResponse<T>,
): T {
    try {
        return withCheck { routine() }
    } catch (e: HttpException) {
        if (e.code() != 200) throw e  // 토큰 만료 시의 백엔드의 응답 코드가 200임

        val accessToken = e
            .response()
            ?.raw()?.request?.header("Authorization")

        mutex.withLock {
            if (authPreference.accessToken != null && accessToken != authPreference.accessToken) return@withLock

            val response = withCheck { refreshToken(authPreference.refreshToken!!) }
            authPreference.refreshToken = response.refreshToken!!
            authPreference.accessToken = response.accessToken!!
        }

        return withCheck { routine() }
    }
}