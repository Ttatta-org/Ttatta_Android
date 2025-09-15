package com.umc.data.implementation.preference

import android.content.Context
import androidx.core.content.edit
import com.umc.core.model.NotificationSetting
import com.umc.core.model.Theme
import com.umc.data.preference.SettingPreference
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
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
        private const val LAST_SENT_FCM_TOKEN_KEY = "last_sent_fcm_token"
    }

    // sealed class 다형성 지원 JSON 설정
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        classDiscriminator = "type" // (@SerialName과 함께 써도 되고 기본값 사용해도 됩니다)
    }

    override var theme: Theme
        get() = Theme.valueOf(prefs.getString(THEME_KEY, Theme.entries.first().name)!!)
        set(value) { prefs.edit { putString(THEME_KEY, value.name) } }

    override var notificationSettings: List<NotificationSetting>
        get() {
            val raw = prefs.getString(NOTIFICATION_SETTINGS_KEY, "[]")!!
            return runCatching { json.decodeFromString<List<NotificationSetting>>(raw) }
                .getOrElse { emptyList() }
        }
        set(value) {
            val raw = json.encodeToString(value)
            prefs.edit { putString(NOTIFICATION_SETTINGS_KEY, raw) }
        }

    override var pinHash: String?
        get() = prefs.getString(PIN_HASH_KEY, null)
        set(value) {
            if (value == null) prefs.edit { remove(PIN_HASH_KEY) }
            else prefs.edit { putString(PIN_HASH_KEY, value) }
        }

    override fun setNotificationSetting(notificationSetting: NotificationSetting) {
        // 같은 타입은 교체, 없으면 추가
        val updated = notificationSettings
            .filterNot { it::class == notificationSetting::class } + notificationSetting
        notificationSettings = updated
    }

    override var lastSentFcmToken: String?
        get() = prefs.getString(LAST_SENT_FCM_TOKEN_KEY, null)
        set(value) {
            if (value == null) prefs.edit { remove(LAST_SENT_FCM_TOKEN_KEY) }
            else prefs.edit { putString(LAST_SENT_FCM_TOKEN_KEY, value) }
        }
}
