package com.umc.ttatta.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.umc.core.notification.NotificationType
import com.umc.core.setting.SettingRepository
import com.umc.data.setting.SettingRepositoryImpl
import com.umc.data.notification.FirebaseNotificationHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChallengeReminderService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val typeRaw = remoteMessage.data["type"]
        val type = NotificationType.from(typeRaw)

        val settingRepository: SettingRepository = SettingRepositoryImpl()
        val handler = FirebaseNotificationHandler(applicationContext)

        CoroutineScope(Dispatchers.IO).launch {
            when (type) {
                NotificationType.MEMORY_LOCATION -> {
                    val isEnabled = settingRepository.isMemoryNotificationEnabled()
                    if (isEnabled) {
                        val diaryId = remoteMessage.data["diaryId"]?.toIntOrNull() ?: return@launch
                        val daysAgo = remoteMessage.data["daysAgo"] ?: "며칠 전"
                        val latitude = remoteMessage.data["latitude"]?.toDoubleOrNull()
                        val longitude = remoteMessage.data["longitude"]?.toDoubleOrNull()

                        handler.sendMemoryNotification(
                            title = "추억을 회상해보세요!",
                            message = "$daysAgo 이 장소에서 일기를 썼어요.",
                            diaryId = diaryId,
                            latitude = latitude,
                            longitude = longitude
                        )
                    }
                }

                // 다른 타입은 추후 구현
                else -> {
                    Log.d("FCM", "지원하지 않는 알림 타입 또는 비어 있음: $typeRaw")
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        // 서버에 토큰 업데이트 필요시 사용
    }
}