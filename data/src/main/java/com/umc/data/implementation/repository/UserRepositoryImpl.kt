package com.umc.data.implementation.repository

import com.umc.core.model.LoginType
import com.umc.core.model.UserInfo
import com.umc.core.model.UserStatus
import com.umc.core.repository.UserRepository
import com.umc.data.api.ServerApi
import com.umc.data.api.dto.server.*
import com.umc.data.api.withAuth
import com.umc.data.api.withCheck
import com.umc.data.preference.AuthPreference
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference,
): UserRepository {

    override suspend fun isAlreadyLogin(): Boolean {
        return try { getUserInfo() } catch (_: Exception) { null } != null
    }

    override suspend fun isIdAlreadyOccupied(id: String): Boolean {
        val response = serverApi.withCheck { checkUsernameSame(username = id) }
        return response.isAvailable != VerifyUsernameOverlapResultDTO.IsAvailable.AVAILABLE
    }


    override suspend fun login(id: String, password: String) {
        val body = SignInRequestDTO(
            username = id,
            password = password,
        )
        val response = serverApi.withCheck { signIn(body = body) }
        authPreference.accessToken = response.accessToken
        authPreference.refreshToken = response.refreshToken
        authPreference.userId = response.userId
    }

    override suspend fun join(
        id: String,
        password: String,
        name: String,
        nickname: String,
        email: String
    ) {
        val body = SignUpRequestDTO(
            name = name,
            email = email,
            nickname = nickname,
            username = id,
            password = password
        )
        serverApi.withCheck { signUp(body = body) }
    }

    override suspend fun loginWithKakao(kakaoToken: String) {
        val body = SignInKakaoRequestDTO(kakaoToken = kakaoToken)
        val response = serverApi.withCheck { signInKakao(body = body) }
        authPreference.accessToken = response.accessToken
        authPreference.refreshToken = response.refreshToken
        authPreference.userId = response.userId
    }

    override suspend fun joinWithKakao(
        kakaoToken: String,
        name: String,
        nickname: String,
        email: String
    ) {
        val body = SignUpKakaoRequestDTO(
            kakaoToken = kakaoToken,
            nickname = name,
        )
        serverApi.withCheck { signUpKakao(body = body) }
    }

    override suspend fun requestVerificationCodeForJoining(email: String) {
        TODO("Not yet implemented")
    }

    override suspend fun checkVerificationCodeForJoining(code: Int): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun requestEmailForFindingId(email: String) {
        TODO("Not yet implemented")
    }

    override suspend fun requestEmailForFindingPassword(email: String, id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun logout() {
        serverApi.withCheck { logout() }
        authPreference.accessToken = null
        authPreference.refreshToken = null
        authPreference.userId = null
    }

    override suspend fun getUserInfo(): UserInfo {
        val response = serverApi.withAuth(authPreference = authPreference) { getUserInfo() }
        return UserInfo(
            id = response.userId!!,
            name = response.nickname!!,
            loginType = when (response.loginType!!) {
                UserInfoResultDTO.LoginType.KAKAO -> LoginType.KAKAO
                UserInfoResultDTO.LoginType.REGULAR -> LoginType.REGULAR
            },
            email = response.email!!,
            profileImageUrl = response.profileImg,
            point = response.point!!,
            status = UserStatus.ACTIVE,  // TODO: 백엔드 구현시 연결
            totalDiaryCount = response.diaryCount!!.toInt()
        )
    }

    override suspend fun modifyUserInfo(
        name: String?,
        email: String?,
    ) {
        val body = EditRequestDTO(
            nickname = name,
            email = email,
            profileImage = null,
            point = null,
        )
        serverApi.withAuth(authPreference = authPreference) {
            updateUserInfo(body = body)
        }
    }

    override suspend fun leaveUser() {
        serverApi.withAuth(authPreference = authPreference) {
            deleteUser()
        }
    }
}