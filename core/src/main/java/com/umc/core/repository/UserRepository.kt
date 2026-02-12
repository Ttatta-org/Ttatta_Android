package com.umc.core.repository

import com.umc.core.model.EmailRequestResult
import com.umc.core.model.UserInfo

interface UserRepository {
    suspend fun isAlreadyLogin(): Boolean
    suspend fun isIdAlreadyOccupied(id: String): Boolean

    suspend fun login(id: String, password: String)
    suspend fun logout()

    suspend fun join(
        id: String,
        password: String,
        name: String,
        nickname: String,
        email: String,
    )

    suspend fun tryLoginWithKakao(openIdToken: String): Boolean  // 기존 가입 여부
    suspend fun postUserInfoWhenFirstKakaoLogin(openIdToken: String, nickname: String)

    suspend fun requestVerificationCodeForJoining(email: String): EmailRequestResult
    suspend fun checkVerificationCodeForJoining(email: String, code: Int): Boolean

    suspend fun requestEmailForFindingId(name: String, email: String): EmailRequestResult
    suspend fun checkVerificationCodeForFindingId(email: String, code: Int): Pair<String, String>?  // (이름, ID)

    suspend fun checkIdForFindingPassword(id: String): Boolean
    suspend fun requestEmailForFindingPassword(name: String, email: String, id: String): EmailRequestResult
    suspend fun checkVerificationCodeForFindingPassword(email: String, code: Int): Boolean
    suspend fun changePassword(email: String, newPassword: String)

    suspend fun requestVerificationCodeForChangeEmail(email: String): EmailRequestResult
    suspend fun changeEmailWithVerificationCode(email: String, code: Int): Boolean

    suspend fun getUserInfo(): UserInfo

    suspend fun modifyUserInfo(
        name: String? = null,
    )

    suspend fun leaveUser(
        reason: String? = null
    )
}