package com.umc.data.api.dto.server

import com.squareup.moshi.Json
import java.time.LocalDateTime

data class UserDeleteResultDTO(
    @Json(name = "id")
    val id: Long?,

    @Json(name = "reason")
    val reason: String?,

    @Json(name = "withdrawnAt")
    val withdrawnAt: String?,   // LocalDateTime -> Moshi 변환 문제 없으면 String으로 받는게 안전

    @Json(name = "activeDays")
    val activeDays: Int?,

    @Json(name = "totalDiary")
    val totalDiary: Int?
)