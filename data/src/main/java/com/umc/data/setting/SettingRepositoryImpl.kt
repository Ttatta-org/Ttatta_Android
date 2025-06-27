package com.umc.data.setting

import android.content.Context
import android.content.SharedPreferences
import com.umc.core.setting.ChallengeRemindNotification
import com.umc.core.setting.DailySummaryNotification
import com.umc.core.setting.Notification
import com.umc.core.setting.SettingRepository
import com.umc.core.setting.Theme
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class SettingRepositoryImpl(
    private val context: Context
) : SettingRepository {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("user_settings", Context.MODE_PRIVATE)
    }

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    // 테마 저장
    override suspend fun switchTheme(theme: Theme) {
        prefs.edit().putString("theme_mode", theme.name).apply()
    }

    // 알림 설정 저장
    override suspend fun setNotification(notification: Notification) {
        when (notification) {
            is DailySummaryNotification -> {
                prefs.edit()
                    .putBoolean("daily_summary_on", notification.isOn)
                    .putString("daily_summary_time", notification.time.format(timeFormatter))
                    .apply()
            }
            is ChallengeRemindNotification -> {
                prefs.edit()
                    .putBoolean("challenge_remind_on", notification.isOn)
                    .putString("challenge_remind_time", notification.remainTime.format(timeFormatter))
                    .apply()
            }
        }
    }

    // 알림 설정 전체 조회
    override suspend fun getNotificationSettings(): List<Notification> {
        val dailyOn = prefs.getBoolean("daily_summary_on", false)
        val dailyTimeStr = prefs.getString("daily_summary_time", "08:00")!!
        val dailyTime = LocalTime.parse(dailyTimeStr, timeFormatter)

        val challengeOn = prefs.getBoolean("challenge_remind_on", false)
        val challengeTimeStr = prefs.getString("challenge_remind_time", "03:00")!!
        val challengeTime = LocalTime.parse(challengeTimeStr, timeFormatter)

        return listOf(
            DailySummaryNotification(dailyOn, dailyTime),
            ChallengeRemindNotification(challengeOn, challengeTime)
        )
    }

    // PIN 설정
    override suspend fun setPin(pin: Int) {
        prefs.edit()
            .putBoolean("is_pin_set", true)
            .putInt("user_pin", pin)
            .apply()
    }

    override suspend fun getIsPinSet(): Boolean {
        return prefs.getBoolean("is_pin_set", false)
    }

    override suspend fun getIsPinCorrect(pin: Int): Boolean {
        val savedPin = prefs.getInt("user_pin", -1)
        return savedPin == pin
    }

    // 챌린지 알림 관련
    override suspend fun isChallengeNotificationEnabled(): Boolean {
        return getChallengeReminderHour() > 0  // 시간 설정이 되어 있으면 알림 허용
    }

    override suspend fun getChallengeReminderHour(): Int {
        return prefs.getInt("challenge_reminder_hour", 0)  // 0이면 알림 없음
    }

    // 필요 시 저장용 함수도 추가 가능
    suspend fun setChallengeReminderHour(hour: Int) {
        prefs.edit().putInt("challenge_reminder_hour", hour).apply()
    }

//    실제 사용 예시 - 사용자 설정 시간 저장
//    val repo = SettingRepositoryImpl(context)
//    repo.setChallengeReminderHour(3)  // 사용자가 "3시간 전" 선택 시

//    실제 사용 예시 - 알림 조건 판단 시
//    val hour = repo.getChallengeReminderHour()
//    if (hour > 0) {
//        // 알림을 보낼 수 있는 설정
//    }

    override suspend fun isMemoryNotificationEnabled(): Boolean {
        // 예시. 실제로는 SharedPreferences나 DataStore 사용
        return true
    }
}