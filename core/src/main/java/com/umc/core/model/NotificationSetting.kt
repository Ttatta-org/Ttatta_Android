package com.umc.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable as KxSerializable

@KxSerializable
sealed class NotificationSetting {
    abstract val isOn: Boolean

    @KxSerializable
    @SerialName("DailySummary")
    data class DailySummary(
        override val isOn: Boolean,
        val hour: Int,
    ) : NotificationSetting()

    @KxSerializable
    @SerialName("ChallengeRemind")
    data class ChallengeRemind(
        override val isOn: Boolean,
        val remainingHours: Int,
    ) : NotificationSetting()

    @KxSerializable
    @SerialName("DiaryWriting")
    data class DiaryWriting(
        override val isOn: Boolean,
        val hour: Int,
        val minute: Int,
    ) : NotificationSetting()

    @KxSerializable
    @SerialName("LocationBasedRemind")
    data class LocationBasedRemind(
        override val isOn: Boolean,
    ) : NotificationSetting()
}