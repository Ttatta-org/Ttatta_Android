package com.umc.ttatta.intent

import java.time.LocalDate

sealed class IntentType(
    open val code: Long,
) {
    data object DiaryWritingReminder : IntentType(code = 1)
    data object ChallengeReminder : IntentType(code = 2)
    data class DailySummary(val date: LocalDate) : IntentType(code = 3)
    data class LocationMemory(val diaryId: Long) : IntentType(code = 4)
}