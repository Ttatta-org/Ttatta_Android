package com.umc.core.notification

interface NotificationHandler {
    fun sendNotification(title: String, message: String)
}