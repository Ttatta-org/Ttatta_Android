package com.umc.core.notification

interface NotificationHandler {
    fun sendNotification(notification: Notification)
}