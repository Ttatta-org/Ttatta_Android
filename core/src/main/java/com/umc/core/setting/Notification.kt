package com.umc.core.setting

import java.time.LocalTime

sealed class Notification(
    open val isOn: Boolean,
)

data class DailySummaryNotification(
    override val isOn: Boolean,
    val time: LocalTime,
) : Notification(isOn)

data class ChallengeRemindNotification(
    override val isOn: Boolean,
    val remainTime: LocalTime,
) : Notification(isOn)