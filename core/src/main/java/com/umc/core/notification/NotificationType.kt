package com.umc.core.notification

enum class NotificationType(val rawValue: String) {
    MEMORY_LOCATION("MEMORY_LOCATION"),
    DAILY_SUMMARY("DAILY_SUMMARY"),
    CHALLENGE_REMINDER("CHALLENGE_REMINDER"),
    DIARY_REMINDER("DIARY_REMINDER");

    companion object {
        fun from(raw: String?): NotificationType? =
            values().find { it.rawValue == raw }
    }
}