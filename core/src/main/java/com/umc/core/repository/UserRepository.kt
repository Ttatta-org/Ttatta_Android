package com.umc.core.repository

import com.umc.core.model.UserInfo

interface UserRepository {
    suspend fun isAlreadyLogin(): Boolean
    suspend fun isIdAlreadyOccupied(id: String): Boolean

    suspend fun login(id: String, password: String)
    suspend fun loginWithKakao(kakaoToken: String)
    suspend fun logout()

    suspend fun join(
        id: String,
        password: String,
        name: String,
        nickname: String,
        email: String,
    )

    suspend fun joinWithKakao(
        kakaoToken: String,
        name: String,
        nickname: String,
        email: String,
    )

    suspend fun getUserInfo(): UserInfo
    suspend fun modifyUserInfo(userInfo: UserInfo)
    suspend fun leaveUser()
}