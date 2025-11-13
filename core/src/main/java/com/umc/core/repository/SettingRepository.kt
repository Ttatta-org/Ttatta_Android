package com.umc.core.repository

import com.umc.core.model.Theme
import java.time.LocalTime

interface SettingRepository {
    // 테마 설정
    suspend fun switchTheme(theme: Theme)

    // FCM 토큰 전달
    suspend fun sendFcmToken(token: String)

    // 핀코드 관련
    suspend fun setPin(pin: Int)
    suspend fun clearPin()
    suspend fun getIsPinSet(): Boolean
    suspend fun getIsPinCorrect(pin: Int): Boolean
    suspend fun syncPinWithServer()

    // 알림: 화면 진입 요약
    suspend fun getAlarmSummary(): AlarmSummary

    // 일기 작성
    suspend fun turnOnWritingDiary(): AlarmResult
    suspend fun updateWritingDiaryTime(time: LocalTime): AlarmResult
    suspend fun turnOffWritingDiary()

    // 위치 기반(토글형)
    suspend fun setMemoryDiaryActive(active: Boolean)

    // 챌린지 리마인드
    suspend fun turnOnChallengeRemind(): AlarmResult
    suspend fun updateChallengeHoursAgo(hoursAgo: Int): AlarmResult
    suspend fun turnOffChallengeRemind()

    // 하루 요약
    suspend fun turnOnDailySummary(): AlarmResult
    suspend fun updateDailySummaryTime(time: LocalTime): AlarmResult
    suspend fun turnOffDailySummary()

    // 위치 기반 리마인드를 위한 위치 전송
    suspend fun sendLocation(latitude: Double, longitude: Double)
}

// 화면 초기화용 도메인 모델
data class AlarmSummary(
    val writingActive: Boolean,
    val writingTime: LocalTime?,
    val memoryActive: Boolean,
    val challengeActive: Boolean,
    val challengeHoursAgo: Int?,
    val dailyActive: Boolean,
    val dailyTime: LocalTime?
)

// 개별 동작 결과(ON/변경 공통)
data class AlarmResult(
    val active: Boolean,
    val time: LocalTime? = null,
    val hoursAgo: Int? = null
)