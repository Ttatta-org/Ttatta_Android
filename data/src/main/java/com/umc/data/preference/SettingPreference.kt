package com.umc.data.preference

import com.umc.core.setting.Notification
import com.umc.core.setting.Theme

interface SettingPreference {
    var theme: Theme
    var notificationSettings: List<Notification>
    var pinHash: String?

    fun setNotificationSetting(notification: Notification)
}