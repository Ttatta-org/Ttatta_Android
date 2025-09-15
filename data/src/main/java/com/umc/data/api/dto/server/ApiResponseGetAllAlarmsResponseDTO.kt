package com.umc.data.api.dto.server

import com.squareup.moshi.Json

// 서버가 "한 번에" 내려주는 형태에 맞춰 조정하세요.
// 여기선 예시로 alarmTime을 "HH:mm:ss" 문자열로 가정했습니다.
data class ApiResponseGetAllAlarmsResponseDTO(
    @Json(name = "isSuccess")
    val isSuccess: Boolean? = null,

    @Json(name = "code")
    val code: String? = null,

    @Json(name = "message")
    val message: String? = null,

    @Json(name = "result")
    val result: GetAllAlarmsResponseDTO? = null
)
