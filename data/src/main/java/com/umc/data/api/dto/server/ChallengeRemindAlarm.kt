package com.umc.data.api.dto.server

import com.squareup.moshi.Json
// @JsonClass(generateAdapter = true)
data class ChallengeRemindAlarm(
    @Json(name = "isActive") val isActive: String? = null,
    @Json(name = "hoursAgo") val hoursAgo: String? = null
)