package com.umc.core.notification

import android.app.PendingIntent
import java.time.LocalDate

data class Notification(
    val title: String,
    val message: String,
    val type: NotificationType,
    val pendingIntent: PendingIntent? = null,
) {
    sealed class NotificationType {
        data class DailySummary(
            val date: LocalDate,
        ) : NotificationType()

        data class LocationBasedReminder(
            val diaryId: Long,
            val description: String,
        ) : NotificationType()

        object ChallengeReminder : NotificationType()
        object DiaryWritingReminder : NotificationType()
    }
}
