package com.umc.core.model

sealed class NotificationSetting(
    open val isOn: Boolean,
) {

    data class DailySummary(
        override val isOn: Boolean,
        val hour: Int,
    ) : NotificationSetting(isOn = isOn)

    data class ChallengeRemind(
        override val isOn: Boolean,
        val remainingHours: Int,
    ) : NotificationSetting(isOn = isOn)

    data class DiaryWriting(
        override val isOn: Boolean,
        val hour: Int,
        val minute: Int,
    ): NotificationSetting(isOn = isOn)

    data class LocationBasedRemind(
        override val isOn: Boolean,
    ): NotificationSetting(isOn = isOn)
}