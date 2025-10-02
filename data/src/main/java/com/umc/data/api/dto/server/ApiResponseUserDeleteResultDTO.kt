package com.umc.data.api.dto.server

import com.squareup.moshi.Json

data class ApiResponseUserDeleteResultDTO (

    @Json(name = "isSuccess")
    val isSuccess: kotlin.Boolean? = null,

    @Json(name = "code")
    val code: kotlin.String? = null,

    @Json(name = "message")
    val message: kotlin.String? = null,

    @Json(name = "result")
    val result: UserDeleteResultDTO? = null

)