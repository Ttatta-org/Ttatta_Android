package com.umc.data.implementation.repository

import com.umc.core.model.LoginType
import com.umc.core.model.UserInfo
import com.umc.core.model.UserStatus
import com.umc.core.repository.UserRepository
import com.umc.data.api.ServerApi
import com.umc.data.api.dto.server.CheckVerificationCodeRequestDTO
import com.umc.data.api.dto.server.DeleteRequestDTO
import com.umc.data.api.dto.server.EditRequestDTO
import com.umc.data.api.dto.server.FindPwRequestDTO
import com.umc.data.api.dto.server.SendVerificationMailFindIdRequestDTO
import com.umc.data.api.dto.server.SendVerificationMailFindPwRequestDTO
import com.umc.data.api.dto.server.SendVerificationMailSignUpRequestDTO
import com.umc.data.api.dto.server.SignInRequestDTO
import com.umc.data.api.dto.server.SignUpKakaoRequestDTO
import com.umc.data.api.dto.server.SignUpRequestDTO
import com.umc.data.api.dto.server.UserInfoResultDTO
import com.umc.data.api.dto.server.VerifyUsernameOverlapResultDTO
import com.umc.data.preference.AuthPreference
import com.umc.data.util.withAuth
import com.umc.data.util.withCheck
import retrofit2.HttpException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference,
) : UserRepository {

    override suspend fun isAlreadyLogin(): Boolean {
        return try {
            getUserInfo()
        } catch (_: Exception) {
            null
        } != null
    }

    override suspend fun isIdAlreadyOccupied(id: String): Boolean {
        val response = serverApi.withCheck { checkIdDuplication(username = id) }
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

    override suspend fun tryLoginWithKakao(openIdToken: String): Boolean {
        val response = serverApi.withCheck { serverApi.loginWithKakao(idToken = openIdToken) }
        authPreference.accessToken = response.accessToken
        authPreference.refreshToken = response.refreshToken
        return response.isRegistered!!
    }

    override suspend fun postUserInfoWhenFirstKakaoLogin(openIdToken: String, nickname: String) {
        // TODO: 필요 없는 인자(openIdToken) 제거
        val body = SignUpKakaoRequestDTO(nickname = nickname)
        serverApi.withAuth(authPreference) { signUpKakao(body = body) }
    }

    override suspend fun requestVerificationCodeForJoining(email: String): Boolean {
        val body = SendVerificationMailSignUpRequestDTO(email = email)

        return try {
            serverApi.withCheck { sendVerificationMailForSignUp(body = body) }
            true
        } catch (e: HttpException) {
            if (e.code() == 400) return false
            throw e
        }
    }

    override suspend fun checkVerificationCodeForJoining(email: String, code: Int): Boolean {
        val body = CheckVerificationCodeRequestDTO(email = email, code = code.toString())

        return try {
            serverApi.withCheck { checkVerificationCodeForSignUp(body = body) }
            true
        } catch (e: HttpException) {
            if (e.code() == 400) return false
            throw e
        }
    }

    override suspend fun requestEmailForFindingId(name: String, email: String): Boolean {
        val body = SendVerificationMailFindIdRequestDTO(name = name, email = email)

        return try {
            serverApi.withCheck { sendVerificationMailForFindingId(body) }
            true
        } catch (e: HttpException) {
            if (e.code() == 400) return false
            throw e
        }
    }

    override suspend fun checkVerificationCodeForFindingId(
        email: String,
        code: Int
    ): Pair<String, String> {
        val body = CheckVerificationCodeRequestDTO(email = email, code = code.toString())
        val response = serverApi.withCheck { findId(body = body) }
        return response.name!! to response.id!!
    }

    override suspend fun requestEmailForFindingPassword(name: String, email: String, id: String): Boolean {
        val body = SendVerificationMailFindPwRequestDTO(name = name, email = email, username = id)

        return try {
            serverApi.withCheck { sendVerificationMailForFindingPassword(body = body) }
            true
        } catch (e: HttpException) {
            if (e.code() == 400) return false
            throw e
        }
    }

    override suspend fun checkVerificationCodeForFindingPassword(
        email: String,
        code: Int,
    ): Boolean {
        return true
    }

    override suspend fun checkIdForFindingPassword(id: String): Boolean {
        return try {
            serverApi.withCheck { checkIdDuplicationOnFindingPassword(id = id) }
            true
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun changePassword(email: String, newPassword: String) {
        val body = FindPwRequestDTO(email = email, password = newPassword)
        serverApi.withCheck { findPassword(body = body) }
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
            email = response.email ?: "", // TODO: 예외 처리 필수
            profileImageUrl = response.profileImg,
            point = response.point!!,
            status = when (response.status) {  // TODO: 이 필드는 사라지는 것이 옳아 보임
                UserInfoResultDTO.Status.ACTIVE, null -> UserStatus.ACTIVE
                UserInfoResultDTO.Status.INACTIVE -> UserStatus.INACTIVE
                UserInfoResultDTO.Status.PENDING -> UserStatus.PENDING
            },
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

    override suspend fun leaveUser(reason: String?) {
        val body = DeleteRequestDTO(reason = reason ?: "")
        serverApi.withAuth(authPreference = authPreference) {
            deleteUser(body = body)
        }
    }
}