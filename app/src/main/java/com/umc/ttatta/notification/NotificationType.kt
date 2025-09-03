package com.umc.ttatta.notification

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import com.google.firebase.messaging.RemoteMessage
import com.umc.ttatta.intent.IntentManager
import com.umc.ttatta.intent.IntentType
import java.time.LocalDate

enum class NotificationType(
    val rawValue: String,
    val requestCode: Int,
    val channelId: String,
    val channelTitle: String,
    val channelDescription: String,
    val channelImportance: Int,
    val getIntent: (Context, RemoteMessage) -> Intent,
) {
    MEMORY_LOCATION(
        rawValue = "MEMORY_LOCATION",
        requestCode = 50,
        channelId = "LOCATION_BASED_REMINDER",
        channelTitle = "내 위치 주변의 추억",
        channelDescription = "과거에 작성한 근처 일기를 알려줍니다.",
        channelImportance = NotificationManager.IMPORTANCE_HIGH,
        getIntent = { context, remoteMessage ->
            IntentManager.getNewIntent(
                context = context,
                intentType = IntentType.LocationMemory(
                    diaryId = remoteMessage.data["diaryId"]!!.toLong()
                ),
            )
        },
    ),
    DAILY_SUMMARY(
        rawValue = "DAILY_SUMMARY",
        requestCode = 60,
        channelId = "DAILY_SUMMARY",
        channelTitle = "하루 요약 도착",
        channelDescription = "일일 요약을 받습니다.",
        channelImportance = NotificationManager.IMPORTANCE_HIGH,
        getIntent = { context, remoteMessage ->
            IntentManager.getNewIntent(
                context = context,
                intentType = IntentType.DailySummary(
                    date = remoteMessage.data["date"]!!.let { LocalDate.parse(it) },
                ),
            )
        },
    ),
    CHALLENGE_REMINDER(
        rawValue = "CHALLENGE_REMINDER",
        requestCode = 70,
        channelId = "CHALLENGE_REMINDER",
        channelTitle = "챌린지 리마인더",
        channelDescription = "챌린지 마감 임박을 알려줍니다.",
        channelImportance = NotificationManager.IMPORTANCE_HIGH,
        getIntent = { context, remoteMessage ->
            IntentManager.getNewIntent(
                context = context,
                intentType = IntentType.ChallengeReminder,
            )
        },
    ),
    DIARY_REMINDER(
        rawValue = "DIARY_REMINDER",
        requestCode = 80,
        channelId = "DIARY_WRITING_REMINDER",
        channelTitle = "일기 작성",
        channelDescription = "일기 작성 시간에 알림을 받습니다.",
        channelImportance = NotificationManager.IMPORTANCE_HIGH,
        getIntent = { context, remoteMessage ->
            IntentManager.getNewIntent(
                context = context,
                intentType = IntentType.DiaryWritingReminder,
            )
        },
    );
}