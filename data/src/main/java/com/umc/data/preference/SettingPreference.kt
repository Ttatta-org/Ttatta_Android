package com.umc.data.preference

import com.umc.core.model.NotificationSetting
import com.umc.core.model.Theme

interface SettingPreference {
    var theme: Theme
    var notificationSettings: List<NotificationSetting>
    var pinHash: String?

    fun setNotificationSetting(notificationSetting: NotificationSetting)
    var lastSentFcmToken: String?
}