package com.umc.data.implementation.repository

import com.umc.core.model.NotificationSetting
import com.umc.core.model.Theme
import com.umc.core.repository.AlarmResult
import com.umc.core.repository.AlarmSummary
import com.umc.core.repository.SettingRepository
import com.umc.data.api.MemoryDiaryAlarmStatus
import com.umc.data.api.ServerApi
import com.umc.data.api.dto.server.*
import com.umc.data.api.withAuth
import com.umc.data.preference.AuthPreference
import com.umc.data.preference.SettingPreference
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalTime
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
    private val authPreference: AuthPreference,
    private val settingPreference: SettingPreference,   // last-known 캐시
    private val serverApi: ServerApi,
) : SettingRepository {

    // -------------------- 공통 유틸 --------------------
    private fun String?.onOff(): Boolean = this == "ON"
    private fun LocalTime.toHms(): String =
        "%02d:%02d:%02d".format(hour, minute, second)

    // -------------------- Theme / FCM / PIN --------------------
    override suspend fun switchTheme(theme: Theme) {
        settingPreference.theme = theme
    }

    override suspend fun sendFcmToken(token: String) {
        val body = GetFcmTokenRequestDTO(fcmToken = token)
        serverApi.withAuth(authPreference) { sendFcmToken(body) }
        // 알림 ON 가드 등에 사용되므로 로컬에도 저장
        settingPreference.lastSentFcmToken = token
    }

    override suspend fun setPin(pin: Int) {
        val body = SetPinRequestDTO(pin = pin.toString())
        serverApi.withAuth(authPreference) { setPin(body) }
        settingPreference.pinHash = BCrypt.hashpw(pin.toString(), BCrypt.gensalt())
    }

    override suspend fun clearPin() {
        // 서버 API 준비되면:
        // serverApi.withAuth(authPreference) { clearPin() }
        settingPreference.pinHash = null
    }

    override suspend fun getIsPinSet(): Boolean = settingPreference.pinHash != null

    override suspend fun getIsPinCorrect(pin: Int): Boolean =
        BCrypt.checkpw(pin.toString(), settingPreference.pinHash)

    override suspend fun syncPinWithServer() {
        val response = serverApi.withAuth(authPreference) { getPin() }
        settingPreference.pinHash = response.pinHash
    }

    // -------------------- 알림 요약(화면 진입) --------------------
    override suspend fun getAlarmSummary(): AlarmSummary {
        val dto: GetAllAlarmsResponseDTO =
            serverApi.withAuth(authPreference) { getAllAlarms() }

        val writingActive   = dto.writingDiaryAlarm?.isActive.onOff()
        val writingTime     = dto.writingDiaryAlarm?.alarmTime
        val memoryActive    = dto.memoryDiaryAlarm?.isActive.onOff()
        val challengeActive = dto.challengeRemindAlarm?.isActive.onOff()
        val challengeHours  = dto.challengeRemindAlarm?.hoursAgo?.toIntOrNull()
        val dailyActive     = dto.dailySummaryAlarm?.isActive.onOff()
        val dailyTime       = dto.dailySummaryAlarm?.alarmTime

        // ---- 서버 → 로컬(last-known) 캐시 갱신 (null이면 이전 값/기본값 유지)
        val prevWriting = settingPreference.notificationSettings
            .find { it is NotificationSetting.DiaryWriting } as? NotificationSetting.DiaryWriting
        val prevChal = settingPreference.notificationSettings
            .find { it is NotificationSetting.ChallengeRemind } as? NotificationSetting.ChallengeRemind
        val prevDaily = settingPreference.notificationSettings
            .find { it is NotificationSetting.DailySummary } as? NotificationSetting.DailySummary

        settingPreference.setNotificationSetting(
            NotificationSetting.DiaryWriting(
                isOn = writingActive,
                hour = writingTime?.hour ?: prevWriting?.hour ?: 20,
                minute = writingTime?.minute ?: prevWriting?.minute ?: 30
            )
        )
        settingPreference.setNotificationSetting(
            NotificationSetting.LocationBasedRemind(isOn = memoryActive)
        )
        settingPreference.setNotificationSetting(
            NotificationSetting.ChallengeRemind(
                isOn = challengeActive,
                remainingHours = challengeHours ?: prevChal?.remainingHours ?: 1
            )
        )
        settingPreference.setNotificationSetting(
            NotificationSetting.DailySummary(
                isOn = dailyActive,
                hour = dailyTime?.hour ?: prevDaily?.hour ?: 13
            )
        )

        return AlarmSummary(
            writingActive = writingActive,
            writingTime = writingTime,
            memoryActive = memoryActive,
            challengeActive = challengeActive,
            challengeHoursAgo = challengeHours,
            dailyActive = dailyActive,
            dailyTime = dailyTime
        )
    }

    // -------------------- 일기 작성 알림 --------------------
    override suspend fun turnOnWritingDiary(): AlarmResult {
        val res: WrittingDiaryAlarmOnResponseDTO =
            serverApi.withAuth(authPreference) { turnOnDiaryWriteAlarm() }

        val time = res.alarmTime
            ?: (settingPreference.notificationSettings
                .find { it is NotificationSetting.DiaryWriting } as? NotificationSetting.DiaryWriting
                    )?.let { LocalTime.of(it.hour, it.minute) }
            ?: LocalTime.of(20, 30)

        // 캐시 갱신
        settingPreference.setNotificationSetting(
            NotificationSetting.DiaryWriting(isOn = true, hour = time.hour, minute = time.minute)
        )
        return AlarmResult(active = true, time = time)
    }

    override suspend fun updateWritingDiaryTime(time: LocalTime): AlarmResult {
        val body = UpdateWritingAlarmRequestDTO(alarmTime = time.toHms())
        serverApi.withAuth(authPreference) { changeTimeOfDiaryWriteAlarm(body) }

        settingPreference.setNotificationSetting(
            NotificationSetting.DiaryWriting(isOn = true, hour = time.hour, minute = time.minute)
        )
        return AlarmResult(active = true, time = time)
    }

    override suspend fun turnOffWritingDiary() {
        serverApi.withAuth(authPreference) { turnOffDiaryWriteAlarm() }
        val prev = settingPreference.notificationSettings
            .find { it is NotificationSetting.DiaryWriting } as? NotificationSetting.DiaryWriting

        // 시간은 보존(재활성화 시 유용)
        settingPreference.setNotificationSetting(
            NotificationSetting.DiaryWriting(
                isOn = false,
                hour = prev?.hour ?: 20,
                minute = prev?.minute ?: 30
            )
        )
    }

    // -------------------- 위치 기반(토글형) --------------------
    override suspend fun setMemoryDiaryActive(active: Boolean) {
        val status = if (active) MemoryDiaryAlarmStatus.ON else MemoryDiaryAlarmStatus.OFF
        serverApi.withAuth(authPreference) { toggleMemoryDiaryAlarm(status) }

        settingPreference.setNotificationSetting(
            NotificationSetting.LocationBasedRemind(isOn = active)
        )
    }

    // -------------------- 챌린지 리마인드 --------------------
    override suspend fun turnOnChallengeRemind(): AlarmResult {
        val res: ChallengeRemindAlarmOnResponseDTO =
            serverApi.withAuth(authPreference) { turnOnChallengeRemindAlarm() }

        val hours = res.hoursAgo?.toIntOrNull()
            ?: (settingPreference.notificationSettings
                .find { it is NotificationSetting.ChallengeRemind } as? NotificationSetting.ChallengeRemind
                    )?.remainingHours
            ?: 1

        settingPreference.setNotificationSetting(
            NotificationSetting.ChallengeRemind(isOn = true, remainingHours = hours)
        )
        return AlarmResult(active = true, hoursAgo = hours)
    }

    override suspend fun updateChallengeHoursAgo(hoursAgo: Int): AlarmResult {
        val body = UpdateChallengeRemindAlarmRequestDTO(hoursAgo = hoursAgo.toString())
        serverApi.withAuth(authPreference) { changeTimeOfChallengeRemindAlarm(body) }

        settingPreference.setNotificationSetting(
            NotificationSetting.ChallengeRemind(isOn = true, remainingHours = hoursAgo)
        )
        return AlarmResult(active = true, hoursAgo = hoursAgo)
    }

    override suspend fun turnOffChallengeRemind() {
        serverApi.withAuth(authPreference) { turnOffChallengeRemindAlarm() }
        val prev = settingPreference.notificationSettings
            .find { it is NotificationSetting.ChallengeRemind } as? NotificationSetting.ChallengeRemind

        settingPreference.setNotificationSetting(
            NotificationSetting.ChallengeRemind(isOn = false, remainingHours = prev?.remainingHours ?: 1)
        )
    }

    // -------------------- 하루 요약 --------------------
    override suspend fun turnOnDailySummary(): AlarmResult {
        val res: DailySummaryAlarmOnResponseDTO =
            serverApi.withAuth(authPreference) { turnOnDailySummaryAlarm() }

        val time = res.alarmTime
            ?: (settingPreference.notificationSettings
                .find { it is NotificationSetting.DailySummary } as? NotificationSetting.DailySummary
                    )?.let { LocalTime.of(it.hour, 0) }
            ?: LocalTime.of(13, 0)

        settingPreference.setNotificationSetting(
            NotificationSetting.DailySummary(isOn = true, hour = time.hour)
        )
        return AlarmResult(active = true, time = time)
    }

    override suspend fun updateDailySummaryTime(time: LocalTime): AlarmResult {
        // 백 스펙이 정시만 허용이라면 분/초 00으로 정규화
        val normalized = time.withMinute(0).withSecond(0)
        val body = UpdateDailySummaryAlarmRequestDTO(alarmTime = normalized.toHms())
        serverApi.withAuth(authPreference) { changeTimeOfDailySummaryAlarm(body) }

        settingPreference.setNotificationSetting(
            NotificationSetting.DailySummary(isOn = true, hour = normalized.hour)
        )
        return AlarmResult(active = true, time = normalized)
    }

    override suspend fun turnOffDailySummary() {
        serverApi.withAuth(authPreference) { turnOffDailySummaryAlarm() }
        val prev = settingPreference.notificationSettings
            .find { it is NotificationSetting.DailySummary } as? NotificationSetting.DailySummary

        settingPreference.setNotificationSetting(
            NotificationSetting.DailySummary(isOn = false, hour = prev?.hour ?: 13)
        )
    }
}
