package com.umc.data.implementation.preference

import android.content.Context
import androidx.core.content.edit
import com.umc.core.setting.Notification
import com.umc.core.setting.Theme
import com.umc.data.preference.SettingPreference
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SettingPreferenceImpl @Inject constructor(
    private val context: Context,
) : SettingPreference {
    private val prefs by lazy {
        context.getSharedPreferences("user_settings", Context.MODE_PRIVATE)
    }

    companion object {
        private const val THEME_KEY = "theme_mode"
        private const val NOTIFICATION_SETTINGS_KEY = "notification_settings"
        private const val PIN_HASH_KEY = "pin_hash"
    }

    override var theme: Theme
        get() = Theme.valueOf(prefs.getString(THEME_KEY, Theme.entries.first().name)!!)
        set(value) {
            prefs.edit { putString(THEME_KEY, value.name) }
        }

    override var notificationSettings: List<Notification>
        get() = prefs.getString(NOTIFICATION_SETTINGS_KEY, "")!!.deserializeToNotificationList()
        set(value) {
            prefs.edit { putString(NOTIFICATION_SETTINGS_KEY, value.serialize()) }
        }

    override var pinHash: String?
        get() = prefs.getString(PIN_HASH_KEY, null)
        set(value) {
            prefs.edit { putString(PIN_HASH_KEY, value) }
        }

    override fun setNotificationSetting(notification: Notification) {
        notificationSettings = notificationSettings.map {
            if (it::class == notification::class) notification else it
        }
    }

    private fun List<Notification>.serialize(): String {
        return joinToString(separator = ",") { Json.encodeToString(it) }
    }

    private fun String.deserializeToNotificationList(): List<Notification> {
        return split(",").map { Json.decodeFromString(it) }
    }
}