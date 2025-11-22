package com.umc.ttatta.app.intent

import android.content.Context
import android.content.Intent
import com.umc.ttatta.app.MainActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object IntentManager {
    fun Context.getNewIntent(intentType: IntentType) =
        Intent(this, MainActivity::class.java)
            .putExtra("code", intentType.code)
            .let {
                when (intentType) {
                    is IntentType.DailySummary -> it.putExtra(
                        "date",
                        intentType.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    )

                    is IntentType.LocationMemory -> {
                        it.putExtra("diaryId", intentType.diaryId)
                        it.putExtra("description", intentType.description)
                    }

                    else -> it
                }
            }

    fun getIntentType(intent: Intent) = when (intent.getLongExtra("code", 0)) {
        0L -> null
        1L -> IntentType.DiaryWritingReminder
        2L -> IntentType.ChallengeReminder
        3L -> IntentType.DailySummary(
            date = LocalDate.parse(intent.getStringExtra("date"))
        )

        4L -> IntentType.LocationMemory(
            diaryId = intent.getLongExtra("diaryId", 0),
            description = intent.getStringExtra("description") ?: "오래전 이 곳을 방문했어요!",
        )

        else -> throw IllegalArgumentException("IntentType does not exist.")
    }
}
