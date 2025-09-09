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

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun sendNotification(notification: Notification) {
        if (!hasNotificationPermission) return

        val channelType =
            NotificationChannelType.entries.find { it.type == notification.type::class.java }
                ?: run {
                    Log.e(
                        "FirebaseNotificationHandler",
                        "Notification type does not exist. $notification"
                    )
                    return
                }

        if (notificationManager.notificationChannels.none { it.id == channelType.id }) {
            createChannel(channelType)
        }

        val builder = NotificationCompat.Builder(context, channelType.id)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(notification.pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(System.currentTimeMillis().toInt(), builder.build())
            } catch (e: SecurityException) {
                Log.e("FirebaseNotificationHandler", "Failed to send notification: ${e.message}")
            }
        }
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val hasNotificationPermission: Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

    private fun createChannel(channelType: NotificationChannelType) {
        val channel = NotificationChannel(
            channelType.id,
            channelType.title,
            channelType.importance,
        ).apply {
            this.description = channelType.description
        }

        notificationManager.createNotificationChannel(channel)
    }
}