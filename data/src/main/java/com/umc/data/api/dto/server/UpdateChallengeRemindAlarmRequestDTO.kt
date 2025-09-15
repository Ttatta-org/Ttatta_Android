package com.umc.data.api.dto.server


import com.squareup.moshi.Json

/**
 * Challenge Remind 시간 설정 요청
 * 서버 스웨거: UpdateChallengeRemindAlarmRequestDTO
 * - hoursAgo: 마감 N시간 전 문자열 (보통 "6" 같은 정수 문자열)
 */
data class UpdateChallengeRemindAlarmRequestDTO(
    @Json(name = "hoursAgo")
    val hoursAgo: String
)