package com.umc.core.repository

import com.umc.core.model.UserInfo

interface UserRepository {
    suspend fun isAlreadyLogin(): Boolean
    suspend fun isIdAlreadyOccupied(id: String): Boolean
    suspend fun isNicknameAlreadyOccupied(nickname: String): Boolean // ✅ 닉네임 중복 확인 추가

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

    // 확인 코드 인증은 아직 미완성된 API
    suspend fun requestVerificationCodeForJoining(email: String)
    suspend fun checkVerificationCodeForJoining(code: Int): Boolean
    suspend fun requestEmailForFindingId(email: String)
    suspend fun requestEmailForFindingPassword(email: String, id: String)

    suspend fun getUserInfo(): UserInfo
    suspend fun modifyUserInfo(
        name: String? = null,
        email: String? = null,
    )
    suspend fun leaveUser()
}