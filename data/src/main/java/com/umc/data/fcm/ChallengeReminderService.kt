package com.umc.data.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.umc.data.notification.FirebaseNotificationHandler

class ChallengeReminderService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: "챌린지 리마인더"
        val body = remoteMessage.notification?.body ?: "지금 챌린지를 시작해보세요!"

        val handler = FirebaseNotificationHandler(applicationContext)
        handler.sendNotification(title, body)
    }

    override fun onNewToken(token: String) {
        // 서버에 토큰 업데이트 필요시 사용
    }
}