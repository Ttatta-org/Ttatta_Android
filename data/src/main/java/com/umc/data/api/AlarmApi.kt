package com.umc.data.api

import com.umc.data.api.dto.BaseResponse
import com.umc.data.api.dto.server.GetFcmTokenRequestDTO
import com.umc.data.api.dto.server.UpdateWritingAlarmRequestDTO
import com.umc.data.api.dto.server.WrittingDiaryAlarmOnResponseDTO
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AlarmApi {
    // 일기 작성 알림 켜기
    @POST("/alarms/write/diary/on")
    suspend fun turnOnDiaryWriteAlarm(): BaseResponse<WrittingDiaryAlarmOnResponseDTO>

    // 일기 작성 알림 시간 변경
    @PATCH("/alarms/write/diary/on")
    suspend fun changeTimeOfDiaryWriteAlarm(
        @Body body: UpdateWritingAlarmRequestDTO
    ): BaseResponse<Any?>

    // FCM 토큰 전달
    @POST("/alarms/token")
    suspend fun sendFcmToken(
        @Body body: GetFcmTokenRequestDTO
    ): BaseResponse<Any?>

    // 일기 작성 알림 끄기
    @PATCH("/alarms/write/diary/off")
    suspend fun turnOffDiaryWriteAlarm(): BaseResponse<Any?>
}