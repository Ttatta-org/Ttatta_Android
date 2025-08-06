package com.umc.data.implementation.repository

import com.umc.core.setting.Notification
import com.umc.core.setting.SettingRepository
import com.umc.core.setting.Theme
import com.umc.data.api.ServerApi
import com.umc.data.api.dto.server.GetFcmTokenRequestDTO
import com.umc.data.api.dto.server.SetPinRequestDTO
import com.umc.data.api.withAuth
import com.umc.data.preference.AuthPreference
import com.umc.data.preference.SettingPreference
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
    private val authPreference: AuthPreference,
    private val settingPreference: SettingPreference,
    private val serverApi: ServerApi,
) : SettingRepository {

    override suspend fun switchTheme(theme: Theme) {
        settingPreference.theme = theme
    }

    override suspend fun setNotification(notification: Notification) {
        // TODO
    }

    override suspend fun getNotificationSettings(): List<Notification> {
        return settingPreference.notificationSettings
    }

    override suspend fun sendFcmToken(token: String) {
        val body = GetFcmTokenRequestDTO(fcmToken = token)
        serverApi.withAuth(authPreference) { sendFcmToken(body = body) }
    }

    override suspend fun setPin(pin: Int) {
        val body = SetPinRequestDTO(pin = pin.toString())
        serverApi.withAuth(authPreference) { setPin(body = body) }
        settingPreference.pinHash = BCrypt.hashpw(pin.toString(), BCrypt.gensalt())
    }

    override suspend fun getIsPinSet(): Boolean {
        return settingPreference.pinHash != null
    }

    override suspend fun getIsPinCorrect(pin: Int): Boolean {
        return BCrypt.checkpw(pin.toString(), settingPreference.pinHash)
    }

    override suspend fun syncPin() {
        val response = serverApi.withAuth(authPreference) { getPin() }
        settingPreference.pinHash = response.pinHash
    }

    override suspend fun isChallengeNotificationEnabled(): Boolean {
        // TODO
        return true
    }

    override suspend fun getChallengeReminderHour(): Int {
        // TODO
        return 12
    }

    override suspend fun isMemoryNotificationEnabled(): Boolean {
        // TODO
        return true
    }
}