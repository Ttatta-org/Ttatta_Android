package com.umc.data.util

import com.umc.data.api.ServerApi
import com.umc.data.api.dto.BaseResponse
import com.umc.data.preference.AuthPreference

suspend fun <T> ServerApi.withAuth(
    authPreference: AuthPreference,
    routine: suspend ServerApi.() -> BaseResponse<T>,
): T {
    try {
        return withCheck { routine() }
    } catch (_: Exception) {
        val response = withCheck { refreshToken(authPreference.refreshToken!!) }
        authPreference.refreshToken = response.refreshToken!!
        authPreference.accessToken = response.accessToken!!
        return withCheck { routine() }
    }
}