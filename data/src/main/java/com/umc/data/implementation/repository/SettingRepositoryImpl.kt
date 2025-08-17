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
        when (notificationSetting) {
            is NotificationSetting.DiaryWriting -> {
                if (notificationSetting.isOn) {
                    val body = UpdateWritingAlarmRequestDTO(
                        alarmTime = "%02d:%02d:00".format(
                            notificationSetting.hour,
                            notificationSetting.minute
                        )
                    )

                    serverApi.withAuth(authPreference) { turnOnDiaryWriteAlarm() }
                    serverApi.withAuth(authPreference) { changeTimeOfDiaryWriteAlarm(body = body) }
                } else {
                    serverApi.withAuth(authPreference) { turnOffDiaryWriteAlarm() }
                }
            }

            else -> Unit  // TODO
        }

        settingPreference.apply {
            notificationSettings = notificationSettings.map {
                if (it::class == notificationSetting::class) notificationSetting else it
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T : NotificationSetting> getNotificationSetting(notificationSetting: KClass<T>): T {
        val setting = settingPreference.notificationSettings.find { it::class == notificationSetting } as? T
        if (setting != null) return setting

        val default =  when (notificationSetting) {
            NotificationSetting.DiaryWriting::class -> NotificationSetting.DiaryWriting(
                isOn = true,
                hour = 20,
                minute = 30
            )

            NotificationSetting.LocationBasedRemind::class -> NotificationSetting.LocationBasedRemind(
                isOn = true,
            )

            NotificationSetting.ChallengeRemind::class -> NotificationSetting.ChallengeRemind(
                isOn = true,
                remainingHours = 1
            )

            NotificationSetting.DailySummary::class -> NotificationSetting.DailySummary(
                isOn = true,
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