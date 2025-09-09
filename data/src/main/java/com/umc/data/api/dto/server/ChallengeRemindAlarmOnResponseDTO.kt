package com.umc.data.api.dto.server

import com.squareup.moshi.Json

/**
 * Challenge Remind ON 응답
 * 서버 스웨거: ChallengeRemindAlarmOnResponseDTO
 */
data class ChallengeRemindAlarmOnResponseDTO(
    @Json(name = "hoursAgo")
    val hoursAgo: String?
)
