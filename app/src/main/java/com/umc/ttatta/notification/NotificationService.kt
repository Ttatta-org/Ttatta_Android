package com.umc.ttatta.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.Constants.MessageNotificationKeys
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.umc.core.repository.SettingRepository
import com.umc.core.repository.UserRepository
import com.umc.data.R
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

    private val notificationManager
        get() = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    private val notificationManagerCompat
        get() = NotificationManagerCompat.from(this)

    private val hasNotificationPermission: Boolean
        get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

    override fun handleIntent(intent: Intent?) {
        val newIntent = intent?.apply {
            val (title, body) = listOf(
                MessageNotificationKeys.TITLE,
                MessageNotificationKeys.BODY,
            ).map { key ->
                getStringExtra(key) ?: getStringExtra(getKeyWithOldPrefix(key))
            }

            putExtra("title", title)
            putExtra("body", body)

            extras?.apply {
                remove(MessageNotificationKeys.ENABLE_NOTIFICATION)
                remove(getKeyWithOldPrefix(MessageNotificationKeys.ENABLE_NOTIFICATION))
            }.let { extras ->
                replaceExtras(extras)
            }
        }

        super.handleIntent(newIntent)
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("NotificationService", "Message received: $remoteMessage")

        // 알림 권한 검사
        if (!hasNotificationPermission) {
            Log.d("NotificationService", "Has no notification permission.")
            return
        }

        // 일림의 타입 검사
        val type = remoteMessage.data["type"]?.let { raw ->
            NotificationType.entries.find { it.rawValue == raw }
        } ?: run {
            Log.e("NotificationService", "Alarm type does not exist.")
            return
        }

        // 알림 채널이 없으면 새로 생성
        if (notificationManager.notificationChannels.find { it.id == type.channelId } == null) {
            val channel = NotificationChannel(
                type.channelId,
                type.channelTitle,
                type.channelImportance,
            ).apply {
                this.description = type.channelDescription
            }

            notificationManager.createNotificationChannel(channel)
        }

        // 알림 타입에 따라 PendingIntent 생성
        val pendingIntent = try {
            PendingIntent.getActivity(
                this,
                type.requestCode,
                type.getIntent(this, remoteMessage),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } catch (e: Exception) {
            Log.e("NotificationService", "Failed to create PendingIntent: ${e.message}")
            return
        }

        // 알림 빌더 설정
        val builder = NotificationCompat.Builder(this, type.channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(remoteMessage.data["title"] ?: "")
            .setContentText(remoteMessage.data["body"] ?: "")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // 알림 표시
        with(notificationManagerCompat) {
            try {
                notify(System.currentTimeMillis().toInt(), builder.build())
            } catch (e: SecurityException) {
                Log.e("NotificationService", "Failed to send notification: ${e.message}")
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.d("NotificationService", "Token: $token")
        CoroutineScope(Dispatchers.IO).launch {
            if (!userRepository.isAlreadyLogin()) return@launch

            repeat(5) { index ->
                try {
                    settingRepository.sendFcmToken(token)
                    return@repeat
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

    private fun getKeyWithOldPrefix(key: String): String {
        val prefix = MessageNotificationKeys.NOTIFICATION_PREFIX

        return if (!key.startsWith(prefix)) {
            key
        } else {
            key.replace(prefix, MessageNotificationKeys.NOTIFICATION_PREFIX_OLD)
        }
    }
}