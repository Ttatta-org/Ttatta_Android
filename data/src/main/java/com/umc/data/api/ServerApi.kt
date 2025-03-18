package com.umc.data.api

import com.umc.data.api.dto.BaseResponse
import com.umc.data.preference.AuthPreference

interface ServerApi: UserApi, DiaryApi, CategoryApi, ChallengeApi, ItemApi

suspend fun <T> ServerApi.withCheck(
    getter: suspend ServerApi.() -> BaseResponse<T>
): T {
    val response = getter()
    if (!response.isSuccess) throw Exception(response.message)
    return response.result
}

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