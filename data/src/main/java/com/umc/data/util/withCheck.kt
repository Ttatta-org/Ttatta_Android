package com.umc.data.util

import com.umc.data.api.ServerApi
import com.umc.data.api.dto.BaseResponse
import com.umc.data.exception.ServerException

suspend fun <T> ServerApi.withCheck(
    getter: suspend ServerApi.() -> BaseResponse<T>
): T {
    val response = getter()

    if (!response.isSuccess) throw ServerException(
        code = response.code,
        message = response.message,
    )

    return response.result
}