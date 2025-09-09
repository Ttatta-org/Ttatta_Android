package com.umc.data.implementation.repository

import com.umc.core.model.NotificationSetting
import com.umc.core.model.Theme
import com.umc.core.repository.SettingRepository
import com.umc.data.api.MemoryDiaryAlarmStatus
import com.umc.data.api.ServerApi
import com.umc.data.api.dto.server.GetFcmTokenRequestDTO
import com.umc.data.api.dto.server.SetPinRequestDTO
import com.umc.data.api.dto.server.UpdateChallengeRemindAlarmRequestDTO
import com.umc.data.api.dto.server.UpdateWritingAlarmRequestDTO
import com.umc.data.api.dto.server.UpdateDailySummaryAlarmRequestDTO
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
        val prev = settingPreference.notificationSettings.find { it::class == notificationSetting::class }
        if (prev != null && prev == notificationSetting) return

        fun commit() = settingPreference.setNotificationSetting(notificationSetting)
        val fcm = settingPreference.lastSentFcmToken

        when (notificationSetting) {

            // ===== 일기 작성 =====
            is NotificationSetting.DiaryWriting -> {
                val wasOn = (prev as? NotificationSetting.DiaryWriting)?.isOn == true
                val body  = UpdateWritingAlarmRequestDTO(
                    alarmTime = "%02d:%02d:%02d".format(notificationSetting.hour, notificationSetting.minute, 0)
                )

                if (notificationSetting.isOn) {
                    if (fcm.isNullOrBlank()) return
                    // 레코드 없을 수 있으니 항상 한 번 생성 시도 후, 시간 PATCH
                    runCatching { serverApi.withAuth(authPreference) { turnOnDiaryWriteAlarm() } }
                    serverApi.withAuth(authPreference) { changeTimeOfDiaryWriteAlarm(body) }
                    commit(); return
                } else {
                    if (wasOn) serverApi.withAuth(authPreference) { turnOffDiaryWriteAlarm() }
                    commit(); return
                }
            }

            // ===== 챌린지 리마인드 =====
            is NotificationSetting.ChallengeRemind -> {
                val wasOn = (prev as? NotificationSetting.ChallengeRemind)?.isOn == true
                val body  = UpdateChallengeRemindAlarmRequestDTO(
                    // 서버 스펙에 맞게: Int 필드면 그대로, String 필드면 .toString()
                    hoursAgo = notificationSetting.remainingHours.toString()
                )

                if (notificationSetting.isOn) {
                    if (fcm.isNullOrBlank()) return
                    runCatching { serverApi.withAuth(authPreference) { turnOnChallengeRemindAlarm() } }
                    serverApi.withAuth(authPreference) { changeTimeOfChallengeRemindAlarm(body) }
                    commit(); return
                } else {
                    if (wasOn) serverApi.withAuth(authPreference) { turnOffChallengeRemindAlarm() }
                    commit(); return
                }
            }

            // ===== 하루 요약 =====
            is NotificationSetting.DailySummary -> {
                val wasOn = (prev as? NotificationSetting.DailySummary)?.isOn == true
                val body  = UpdateDailySummaryAlarmRequestDTO(
                    alarmTime = "%02d:%02d:%02d".format(notificationSetting.hour, 0, 0)
                )

                if (notificationSetting.isOn) {
                    if (fcm.isNullOrBlank()) return
                    // ✅ 항상 POST 시도 → 이어서 PATCH(시간) → ALARM_9003 방지
                    runCatching { serverApi.withAuth(authPreference) { turnOnDailySummaryAlarm() } }
                    serverApi.withAuth(authPreference) { changeTimeOfDailySummaryAlarm(body) }
                    commit(); return
                } else {
                    if (wasOn) serverApi.withAuth(authPreference) { turnOffDailySummaryAlarm() }
                    commit(); return
                }
            }

            // ===== 위치 기반(토글형) =====
            is NotificationSetting.LocationBasedRemind -> {
                val status = if (notificationSetting.isOn) MemoryDiaryAlarmStatus.ON else MemoryDiaryAlarmStatus.OFF
                serverApi.withAuth(authPreference) { toggleMemoryDiaryAlarm(status) }
                commit(); return
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T : NotificationSetting> getNotificationSetting(
        notificationSetting: KClass<T>
    ): T {
        // 1) 이미 저장돼 있으면 그대로 반환
        settingPreference.notificationSettings
            .find { it::class == notificationSetting }
            ?.let { return it as T }

        // 2) 없으면 기본값 생성
        val default: NotificationSetting = when (notificationSetting) {
            NotificationSetting.DiaryWriting::class ->
                NotificationSetting.DiaryWriting(isOn = false, hour = 20, minute = 30)

            NotificationSetting.LocationBasedRemind::class ->
                NotificationSetting.LocationBasedRemind(isOn = false)

            NotificationSetting.ChallengeRemind::class ->
                NotificationSetting.ChallengeRemind(isOn = false, remainingHours = 1)

            NotificationSetting.DailySummary::class ->
                NotificationSetting.DailySummary(isOn = false, hour = 13)

            else -> throw IllegalArgumentException(
                "Unknown notification setting class: ${notificationSetting.java.name}"
            )
        }

        // 3) 기본값을 로컬에도 시드 (중복 없이 치환)
        settingPreference.setNotificationSetting(default)

        return default as T
    }

    override suspend fun syncNotificationSettingsWithServer() { /* TODO: 필요 시 배치 동기화 구현 */ }

    override suspend fun sendFcmToken(token: String) {
        val body = GetFcmTokenRequestDTO(fcmToken = token)
        serverApi.withAuth(authPreference) { sendFcmToken(body) }
    }

    override suspend fun setPin(pin: Int) {
        val body = SetPinRequestDTO(pin = pin.toString())
        serverApi.withAuth(authPreference) { setPin(body) }
        settingPreference.pinHash = BCrypt.hashpw(pin.toString(), BCrypt.gensalt())
    }

    override suspend fun clearPin() {
        // serverApi.withAuth(authPreference) { clearPin() } // 서버 준비되면 활성화
        settingPreference.pinHash = null
    }

    override suspend fun getIsPinSet(): Boolean = settingPreference.pinHash != null
    override suspend fun getIsPinCorrect(pin: Int): Boolean =
        BCrypt.checkpw(pin.toString(), settingPreference.pinHash)

    override suspend fun syncPinWithServer() {
        val response = serverApi.withAuth(authPreference) { getPin() }
        settingPreference.pinHash = response.pinHash
    }

    @Deprecated("Use `getNotificationSetting` instead.")
    override suspend fun isMemoryNotificationEnabled(): Boolean = true
}
