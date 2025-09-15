package com.umc.data.api

import com.umc.data.api.dto.BaseResponse
import com.umc.data.api.dto.server.GetFcmTokenRequestDTO
import com.umc.data.api.dto.server.UpdateWritingAlarmRequestDTO
import com.umc.data.api.dto.server.WrittingDiaryAlarmOnResponseDTO
import com.umc.data.api.dto.server.ChallengeRemindAlarmOnResponseDTO
import com.umc.data.api.dto.server.UpdateChallengeRemindAlarmRequestDTO
import com.umc.data.api.dto.server.DailySummaryAlarmOnResponseDTO
import com.umc.data.api.dto.server.GetAllAlarmsResponseDTO
import com.umc.data.api.dto.server.UpdateDailySummaryAlarmRequestDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface AlarmApi {
    // ===== 일기 작성 알림 =====

    // 일기 작성 알림 켜기
    @POST("/alarms/write/diary/on")
    suspend fun turnOnDiaryWriteAlarm(): BaseResponse<WrittingDiaryAlarmOnResponseDTO>

    // 일기 작성 알림 시간 변경
    @PATCH("/alarms/write/diary/on")
    suspend fun changeTimeOfDiaryWriteAlarm(
        @Body body: UpdateWritingAlarmRequestDTO
    ): BaseResponse<Any?>

    // 일기 작성 알림 끄기
    @PATCH("/alarms/write/diary/off")
    suspend fun turnOffDiaryWriteAlarm(): BaseResponse<Any?>

    // ===== FCM 토큰 =====
    @POST("/alarms/token")
    suspend fun sendFcmToken(
        @Body body: GetFcmTokenRequestDTO
    ): BaseResponse<Any?>

    // ===== 위치 기반 추억 회상 알림 (on/off 토글) =====
    @POST("/alarms/memory/diary/on/off")
    suspend fun toggleMemoryDiaryAlarm(
        @Query("memoryDiaryAlarmStatus") status: MemoryDiaryAlarmStatus
    ): BaseResponse<Any?>

    // ===== 챌린지 리마인드 알림 =====
    @POST("/alarms/challenge/remind/on")
    suspend fun turnOnChallengeRemindAlarm(): BaseResponse<ChallengeRemindAlarmOnResponseDTO>

    @PATCH("/alarms/challenge/remind/on")
    suspend fun changeTimeOfChallengeRemindAlarm(
        @Body body: UpdateChallengeRemindAlarmRequestDTO
    ): BaseResponse<Any?>

    @PATCH("/alarms/challenge/remind/off")
    suspend fun turnOffChallengeRemindAlarm(): BaseResponse<Any?>

    // ===== 하루 요약 알림 =====
    @POST("/alarms/summary/diary/on")
    suspend fun turnOnDailySummaryAlarm(): BaseResponse<DailySummaryAlarmOnResponseDTO>

    @PATCH("/alarms/summary/diary/on")
    suspend fun changeTimeOfDailySummaryAlarm(
        @Body body: UpdateDailySummaryAlarmRequestDTO
    ): BaseResponse<Any?>

    @PATCH("/alarms/summary/diary/off")
    suspend fun turnOffDailySummaryAlarm(): BaseResponse<Any?>

    @GET("/alarms")
    suspend fun getAllAlarms(): BaseResponse<GetAllAlarmsResponseDTO>
}

enum class MemoryDiaryAlarmStatus { ON, OFF }