package com.umc.data.notification

import android.Manifest
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
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun sendNotification(notification: Notification) {
        if (!hasNotificationPermission) return

        val builder =
            NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(notification.title).setContentText(notification.message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(notification.pendingIntent).setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(System.currentTimeMillis().toInt(), builder.build())
            } catch (e: SecurityException) {
                Log.e("FirebaseNotificationHandler", "Failed to send notification: ${e.message}")
            }
        }
    }

    private val hasNotificationPermission: Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
}