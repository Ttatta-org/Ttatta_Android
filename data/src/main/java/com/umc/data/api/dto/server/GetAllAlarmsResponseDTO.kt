package com.umc.data.api.dto.server

import com.squareup.moshi.Json
// @JsonClass(generateAdapter = true)
data class GetAllAlarmsResponseDTO(
    @Json(name = "writingDiaryAlarm")    val writingDiaryAlarm: WritingDiaryAlarm? = null,
    @Json(name = "memoryDiaryAlarm")     val memoryDiaryAlarm: MemoryDiaryAlarm? = null,
    @Json(name = "challengeRemindAlarm") val challengeRemindAlarm: ChallengeRemindAlarm? = null,
    @Json(name = "dailySummaryAlarm")    val dailySummaryAlarm: DailySummaryAlarm? = null
)