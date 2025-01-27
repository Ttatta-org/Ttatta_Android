package com.umc.data.preference

interface AuthPreference {
    var accessToken: String?
    var refreshToken: String?
    var userId: Long?
}