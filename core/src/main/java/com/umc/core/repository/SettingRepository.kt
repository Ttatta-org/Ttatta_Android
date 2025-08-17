package com.umc.core.repository

import com.umc.core.model.NotificationSetting
import com.umc.core.model.Theme
import kotlin.reflect.KClass

interface SettingRepository {
    // 테마 설정
    suspend fun switchTheme(theme: Theme)

    // 알림 설정 저장
    suspend fun setNotification(notificationSetting: NotificationSetting)
    suspend fun <T : NotificationSetting> getNotificationSetting(notificationSetting: KClass<T>): T
    suspend fun syncNotificationSettingsWithServer()

    // FCM 토큰 전달
    suspend fun sendFcmToken(token: String)

    // 핀코드 관련
    suspend fun setPin(pin: Int)
    suspend fun clearPin()
    suspend fun getIsPinSet(): Boolean
    suspend fun getIsPinCorrect(pin: Int): Boolean
    suspend fun syncPinWithServer()

    @Deprecated("Use `getNotificationSetting` instead.")
    suspend fun isMemoryNotificationEnabled(): Boolean
}