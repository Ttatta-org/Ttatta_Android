package com.umc.core.setting

interface SettingRepository {
    // 테마 설정
    suspend fun switchTheme(theme: Theme)

    // 알림 설정 저장
    suspend fun setNotification(notification: Notification)

    // 모든 알림 설정 조회
    suspend fun getNotificationSettings(): List<Notification>

    // 핀코드 관련
    suspend fun setPin(pin: Int)
    suspend fun getIsPinSet(): Boolean
    suspend fun getIsPinCorrect(pin: Int): Boolean

    // 챌린지 알림 설정
    suspend fun isChallengeNotificationEnabled(): Boolean
    suspend fun getChallengeReminderHour(): Int

    // 위치 기반 추억 회상 알림 설정
    suspend fun isMemoryNotificationEnabled(): Boolean
}