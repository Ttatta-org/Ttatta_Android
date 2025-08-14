package com.umc.data.implementation.preference

import android.content.Context
import com.umc.data.preference.AuthPreference
import androidx.core.content.edit

class AuthPreferenceImpl(context: Context) : AuthPreference {
    companion object {
        private const val PREF_NAME = "auth"
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
        private const val USER_ID_KEY = "user_id"
    }

    private val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    override var accessToken: String?
        get() = pref.getString(ACCESS_TOKEN_KEY, null)
        set(value) { pref.edit { putString(ACCESS_TOKEN_KEY, value) } }

    override var refreshToken: String?
        get() = pref.getString(REFRESH_TOKEN_KEY, null)
        set(value) { pref.edit { putString(REFRESH_TOKEN_KEY, value) } }

    override var userId: Long?
        get() = pref.getLong(USER_ID_KEY, -1L)
        set(value) { pref.edit { putLong(USER_ID_KEY, value ?: -1L) } }
}