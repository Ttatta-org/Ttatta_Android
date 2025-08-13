package com.umc.ttatta.notification

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.umc.core.notification.Notification
import com.umc.core.notification.NotificationHandler
import com.umc.core.repository.SettingRepository
import com.umc.core.repository.UserRepository
import com.umc.ttatta.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : FirebaseMessagingService() {
    @Inject
    lateinit var settingRepository: SettingRepository

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var notificationHandler: NotificationHandler

    enum class NotificationType(
        val rawValue: String,
        val requestCode: Int,
    ) {
        MEMORY_LOCATION(
            rawValue = "MEMORY_LOCATION",
            requestCode = 50,
        ),
        DAILY_SUMMARY(
            rawValue = "DAILY_SUMMARY",
            requestCode = 60,
        ),
        CHALLENGE_REMINDER(
            rawValue = "CHALLENGE_REMINDER",
            requestCode = 70,
        ),
        DIARY_REMINDER(
            rawValue = "DIARY_REMINDER",
            requestCode = 80,
        );
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val type = remoteMessage.data["type"]?.let { raw ->
            NotificationType.entries.find { it.rawValue == raw }
        } ?: run {
            Log.e("NotificationService", "Alarm type does not exist.")
            return
        }

        val notification: Notification = when (type) {
            NotificationType.MEMORY_LOCATION -> {
                TODO()
            }

            NotificationType.DIARY_REMINDER -> {
                Notification(
                    title = remoteMessage.notification?.title ?: "일기 작성",
                    message = remoteMessage.notification?.tag ?: "따따와 함께 오늘의 일기를 작성해보세요!",
                    type = Notification.NotificationType.DiaryWritingReminder,
                    pendingIntent = getActivityPendingIntent(type)
                )
            }

            NotificationType.CHALLENGE_REMINDER -> {
                TODO()
            }

            NotificationType.DAILY_SUMMARY -> {
                TODO()
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            notificationHandler.sendNotification(notification)
        }
    }

    override fun onNewToken(token: String) {
        Log.d("NotificationService", "Token: $token")
        CoroutineScope(Dispatchers.IO).launch {
            if (!userRepository.isAlreadyLogin()) return@launch

            repeat(5) { index ->
                try {
                    settingRepository.sendFcmToken(token)
                } catch (e: Exception) {
                    Log.e(
                        "NotificationService",
                        "Failed to send FCM Token ${index + 1} times. Waiting to resend.. \n${e.toString()}"
                    )
                    delay(timeMillis = (index + 1).let { it * it } * 1000L)
                }
            }
        }
    }

    private fun getActivityPendingIntent(type: NotificationType): PendingIntent {
        return Intent(
            this,
            MainActivity::class.java,
        ).getActivityPendingIntent(type)
    }

    private fun Intent.getActivityPendingIntent(type: NotificationType): PendingIntent {
        return PendingIntent.getActivity(
            applicationContext,
            type.requestCode,
            this,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }
}