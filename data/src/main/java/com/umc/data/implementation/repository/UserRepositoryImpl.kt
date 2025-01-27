package com.umc.data.implementation.repository

import com.umc.core.model.LoginType
import com.umc.core.model.UserInfo
import com.umc.core.model.UserStatus
import com.umc.core.repository.UserRepository
import com.umc.data.api.ServerApi
import com.umc.data.api.dto.server.LogoutRequestDTO
import com.umc.data.api.dto.server.SignInKakaoRequestDTO
import com.umc.data.api.dto.server.SignInRequestDTO
import com.umc.data.api.dto.server.SignUpKakaoRequestDTO
import com.umc.data.api.dto.server.SignUpRequestDTO
import com.umc.data.api.dto.server.UpdateRequestDTO
import com.umc.data.api.dto.server.UserInfoResultDTO
import com.umc.data.api.dto.server.VerifyUsernameOverlapResultDTO
import com.umc.data.api.withAuth
import com.umc.data.api.withCheck
import com.umc.data.preference.AuthPreference
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference,
): UserRepository {

    override suspend fun isAlreadyLogin(): Boolean {
        return try {
            getUserInfo()
            true
        }
        catch (e: Exception) {
            false
        }
    }

    override suspend fun isIdAlreadyOccupied(id: String): Boolean {
        val response = serverApi.withCheck { checkUsername(username = id) }
        return response.isAvailable != VerifyUsernameOverlapResultDTO.IsAvailable.AVAILABLE
    }

    override suspend fun login(id: String, password: String) {
        val body = SignInRequestDTO(
            username = id,
            password = password,
        )
        serverApi.withCheck { login(body = body) }
    }

    override suspend fun join(id: String, name: String, password: String) {
        val body = SignUpRequestDTO(
            username = id,
            nickname = name,
            password = password,
        )
        serverApi.withCheck { join(body = body) }
    }

    override suspend fun loginWithKakao(kakaoToken: String) {
        val body = SignInKakaoRequestDTO(kakaoToken = kakaoToken)
        serverApi.withCheck { loginWithKakao(body = body) }
    }

    override suspend fun joinWithKakao(kakaoToken: String, name: String) {
        val body = SignUpKakaoRequestDTO(
            kakaoToken = kakaoToken,
            nickname = name,
        )
        serverApi.withCheck { joinWithKakao(body = body) }
    }

    override suspend fun logout() {
        val body = LogoutRequestDTO(userId = authPreference.userId!!)
        serverApi.withCheck { logout(body = body) }
    }

    override suspend fun getUserInfo(): UserInfo {
        val response = serverApi.withAuth(authPreference = authPreference) {
            getUserInfo(userId = authPreference.userId!!)
        }
        return UserInfo(
            id = response.userId!!,
            name = response.nickname!!,
            loginType = when (response.loginType!!) {
                UserInfoResultDTO.LoginType.KAKAO -> LoginType.KAKAO
                UserInfoResultDTO.LoginType.REGULAR -> LoginType.REGULAR
            },
            email = response.email!!,
            profileImageUrl = response.profileImg!!,
            point = response.point!!,
            status = when (response.status!!) {
                UserInfoResultDTO.Status.ACTIVE -> UserStatus.ACTIVE
                UserInfoResultDTO.Status.INACTIVE -> UserStatus.INACTIVE
            },
        )
    }

    override suspend fun modifyUserInfo(userInfo: UserInfo) {
        val body = UpdateRequestDTO(
            nickname = userInfo.name,
            email = userInfo.email,
        )
        serverApi.withAuth(authPreference = authPreference) {
            updateUserInfo(userId = authPreference.userId!!, body = body)
        }
    }

    override suspend fun leaveUser() {
        serverApi.withAuth(authPreference = authPreference) {
            deleteUser(userId = authPreference.userId!!)
        }
    }
}