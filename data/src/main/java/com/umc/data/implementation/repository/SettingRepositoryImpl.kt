package com.umc.data.implementation.repository

import com.umc.core.model.NotificationSetting
import com.umc.core.repository.SettingRepository
import com.umc.core.model.Theme
import com.umc.data.api.ServerApi
import com.umc.data.api.dto.server.GetFcmTokenRequestDTO
import com.umc.data.api.dto.server.SetPinRequestDTO
import com.umc.data.api.dto.server.UpdateWritingAlarmRequestDTO
import com.umc.data.api.withAuth
import com.umc.data.preference.AuthPreference
import com.umc.data.preference.SettingPreference
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject
import kotlin.reflect.KClass

class SettingRepositoryImpl @Inject constructor(
    private val authPreference: AuthPreference,
    private val settingPreference: SettingPreference,
    private val serverApi: ServerApi,
) : SettingRepository {

    override suspend fun switchTheme(theme: Theme) {
        settingPreference.theme = theme
    }

    override suspend fun setNotification(notificationSetting: NotificationSetting) {
        // 1) 로컬 먼저 갱신 (항상 성공)
        settingPreference.apply {
            notificationSettings = notificationSettings.map {
                if (it::class == notificationSetting::class) notificationSetting else it
            }
        }
        // 2) 서버 동기화 (실패해도 예외 삼켜서 크래시 방지)
        when (notificationSetting) {
            is NotificationSetting.DiaryWriting -> {
                val body = UpdateWritingAlarmRequestDTO(
                    alarmTime = "%02d:%02d:00".format(
                        notificationSetting.hour,
                        notificationSetting.minute
                    )
                )

                runCatching {
                    if (notificationSetting.isOn) {
                        // 서버 제약: 시간 먼저 → ON
                        serverApi.withAuth(authPreference) { changeTimeOfDiaryWriteAlarm(body = body) }
                        serverApi.withAuth(authPreference) { turnOnDiaryWriteAlarm() }
                    } else {
                        serverApi.withAuth(authPreference) { turnOffDiaryWriteAlarm() }
                    }
                }.onFailure {
                    android.util.Log.e("SettingRepositoryImpl", "Diary alarm sync failed", it)
                }
            }

            // 아직 백엔드 미구현 → 로컬만 유지
            is NotificationSetting.DailySummary, // Todo
            is NotificationSetting.ChallengeRemind, // Todo
            is NotificationSetting.LocationBasedRemind -> Unit // Todo
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T : NotificationSetting> getNotificationSetting(notificationSetting: KClass<T>): T {
        val setting = settingPreference.notificationSettings.find { it::class == notificationSetting } as? T
        if (setting != null) return setting

        val default =  when (notificationSetting) {
            NotificationSetting.DiaryWriting::class -> NotificationSetting.DiaryWriting(
                isOn = false,   // ← 기본 OFF
                hour = 20,
                minute = 30
            )

            NotificationSetting.LocationBasedRemind::class -> NotificationSetting.LocationBasedRemind(
                isOn = false,   // ← 기본 OFF
            )

            NotificationSetting.ChallengeRemind::class -> NotificationSetting.ChallengeRemind(
                isOn = false,   // ← 기본 OFF
                remainingHours = 1
            )

            NotificationSetting.DailySummary::class -> NotificationSetting.DailySummary(
                isOn = false,   // ← 기본 OFF
                hour = 13,
            )

            else -> throw IllegalArgumentException("Unknown notification setting class: ${notificationSetting.java.name}")
        }

        return default as T
    }

    override suspend fun syncNotificationSettingsWithServer() {
        // TODO
    }

    override suspend fun sendFcmToken(token: String) {
        val body = GetFcmTokenRequestDTO(fcmToken = token)
        serverApi.withAuth(authPreference) { sendFcmToken(body = body) }
    }

    override suspend fun setPin(pin: Int) {
        val body = SetPinRequestDTO(pin = pin.toString())
        serverApi.withAuth(authPreference) { setPin(body = body) }
        settingPreference.pinHash = BCrypt.hashpw(pin.toString(), BCrypt.gensalt())
    }

    override suspend fun clearPin() {
        // TODO: 서버 API clearPin() 구현되면 아래 호출 활성화할 것
        // serverApi.withAuth(authPreference) { clearPin() }
        settingPreference.pinHash = null
    }

    override suspend fun getIsPinSet(): Boolean {
        return settingPreference.pinHash != null
    }

    override suspend fun getIsPinCorrect(pin: Int): Boolean {
        return BCrypt.checkpw(pin.toString(), settingPreference.pinHash)
    }

    override suspend fun syncPinWithServer() {
        val response = serverApi.withAuth(authPreference) { getPin() }
        settingPreference.pinHash = response.pinHash
    }

    @Deprecated("Use `getNotificationSetting` instead.")
    override suspend fun isMemoryNotificationEnabled(): Boolean {
        return true
    }
}