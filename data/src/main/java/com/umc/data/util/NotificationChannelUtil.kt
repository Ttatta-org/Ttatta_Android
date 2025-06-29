package com.umc.data.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

fun createChallengeNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "challenge_channel",  // 채널 ID (알림에서 사용)
            "챌린지 리마인더",       // 사용자에게 보일 채널 이름
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "챌린지 시간 알림 채널입니다"
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}