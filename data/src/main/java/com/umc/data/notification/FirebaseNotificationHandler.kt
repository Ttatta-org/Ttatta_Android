package com.umc.data.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.umc.core.notification.Notification
import com.umc.core.notification.NotificationHandler
import com.umc.data.R

class FirebaseNotificationHandler(
    private val context: Context
) : NotificationHandler {

    companion object {
        private const val CHANNEL_ID = "TTATTA_NOTIFICATION"
        private const val CHANNEL_NAME = "Ttatta 알림"
        private const val CHANNEL_DESC = "앱의 일반 알림 채널"
    }

    init {
        // 앱 시작 후 최초 한 번 채널 생성 (O+)
        ensureChannel()
    }

    // ✅ 매 호출 시점에 권한 상태를 확인하도록 getter로 변경
    private val hasNotificationPermission: Boolean
        get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun sendNotification(notification: Notification) {
        if (!hasNotificationPermission) {
            Log.w("FirebaseNotificationHandler", "No POST_NOTIFICATIONS permission. Skip notify.")
            return
        }

        // 혹시 채널이 아직 없으면 보장
        ensureChannel()

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notification.message)) // 긴 문구 대비
            .setPriority(NotificationCompat.PRIORITY_HIGH) // pre-O에서만 사용
            .setAutoCancel(true)
            .setContentIntent(notification.pendingIntent)

        with(NotificationManagerCompat.from(context)) {
            try {
                // id를 타입별로 고정하면 업데이트에 유리, 지금은 임시로 unique
                notify(System.currentTimeMillis().toInt(), builder.build())
            } catch (e: SecurityException) {
                Log.e("FirebaseNotificationHandler", "Failed to send notification", e)
            }
        }
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = nm.getNotificationChannel(CHANNEL_ID)
        if (channel == null) {
            val newChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                setShowBadge(true)
            }
            nm.createNotificationChannel(newChannel)
        }
    }
}