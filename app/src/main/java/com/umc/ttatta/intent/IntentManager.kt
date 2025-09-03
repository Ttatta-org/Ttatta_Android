package com.umc.ttatta.intent

import android.content.Context
import android.content.Intent
import com.umc.ttatta.MainActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object IntentManager {
    fun getNewIntent(context: Context, intentType: IntentType) =
        Intent(context, MainActivity::class.java).putExtra("code", intentType.code).let {
            when (intentType) {
                is IntentType.DailySummary -> it.putExtra(
                    "date",
                    intentType.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                )

                is IntentType.LocationMemory -> it.putExtra("diaryId", intentType.diaryId)
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
            diaryId = intent.getLongExtra("diaryId", 0)
        )

        else -> throw IllegalArgumentException("IntentType does not exist.")
    }
}
