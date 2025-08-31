package com.umc.data.notification

import android.app.NotificationManager
import com.umc.core.notification.Notification

enum class NotificationChannelType(
    val type: Class<out Notification.NotificationType>,
    val id: String,
    val title: String,
    val description: String,
    val importance: Int,
) {
    DIARY_WRITING_REMINDER(
        type = Notification.NotificationType.DiaryWritingReminder::class.java,
        id = "DIARY_WRITING_REMINDER",
        title = "일기 작성",
        description = "일기 작성 시간에 알림을 받습니다.",
        importance = NotificationManager.IMPORTANCE_HIGH,
    ),
    DAILY_SUMMARY(
        type = Notification.NotificationType.DailySummary::class.java,
        id = "DAILY_SUMMARY",
        title = "하루 요약 도착",
        description = "일일 요약을 받습니다.",
        importance = NotificationManager.IMPORTANCE_HIGH,
    ),
    LOCATION_BASED_REMINDER(
        type = Notification.NotificationType.LocationBasedReminder::class.java,
        id = "LOCATION_BASED_REMINDER",
        title = "내 위치 주변의 추억",
        description = "과거에 작성한 근처 일기를 알려줍니다.",
        importance = NotificationManager.IMPORTANCE_HIGH,
    ),
    CHALLENGE_REMINDER(
        type = Notification.NotificationType.ChallengeReminder::class.java,
        id = "CHALLENGE_REMINDER",
        title = "챌린지 리마인더",
        description = "챌린지 마감 임박을 알려줍니다.",
        importance = NotificationManager.IMPORTANCE_HIGH,
    )
}