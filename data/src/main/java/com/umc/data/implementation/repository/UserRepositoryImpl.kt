package com.umc.data.implementation.repository

import com.umc.core.model.EmailRequestResult
import com.umc.core.model.LoginType
import com.umc.core.model.UserInfo
import com.umc.core.model.UserStatus
import com.umc.core.repository.UserRepository
import com.umc.data.api.ServerApi
import com.umc.data.api.dto.server.CheckVerificationCodeRequestDTO
import com.umc.data.api.dto.server.DeleteRequestDTO
import com.umc.data.api.dto.server.EditRequestDTO
import com.umc.data.api.dto.server.FindPwRequestDTO
import com.umc.data.api.dto.server.MypageSendVerificationCodeRequestDTO
import com.umc.data.api.dto.server.MypageVerifyVerificationCodeAndUpdateEmailRequestDTO
import com.umc.data.api.dto.server.SendVerificationMailFindIdRequestDTO
import com.umc.data.api.dto.server.SendVerificationMailFindPwRequestDTO
import com.umc.data.api.dto.server.SendVerificationMailSignUpRequestDTO
import com.umc.data.api.dto.server.SignInRequestDTO
import com.umc.data.api.dto.server.SignUpKakaoRequestDTO
import com.umc.data.api.dto.server.SignUpRequestDTO
import com.umc.data.api.dto.server.UserInfoResultDTO
import com.umc.data.api.dto.server.VerifyUsernameOverlapResultDTO
import com.umc.data.exception.ServerException
import com.umc.data.preference.AuthPreference
import com.umc.data.preference.SettingPreference
import com.umc.data.util.AuthenticatedRepository
import retrofit2.HttpException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    override val authPreference: AuthPreference,
    private val serverApi: ServerApi,
    private val settingPreference: SettingPreference,
) : UserRepository,
    AuthenticatedRepository {

    override suspend fun isAlreadyLogin(): Boolean {
        return runCatching { getUserInfo() }.isSuccess
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
        serverApi.withAuth { signUpKakao(body = body) }
    }

    override suspend fun requestVerificationCodeForJoining(email: String): EmailRequestResult {
        val body = SendVerificationMailSignUpRequestDTO(email = email)

        return try {
            serverApi.withCheck { sendVerificationMailForSignUp(body = body) }
            EmailRequestResult.SENT
        } catch (e: ServerException) {
            when (e.code) {
                "USER_1004" -> EmailRequestResult.DUPLICATED
                else -> EmailRequestResult.ERROR
            }
        }
    }

    override suspend fun checkVerificationCodeForJoining(email: String, code: Int): Boolean {
        val body = CheckVerificationCodeRequestDTO(email = email, code = code.toString())

        return try {
            serverApi.withCheck { checkVerificationCode(body = body) }
            true
        } catch (_: ServerException) {
            false
        }
    }

    override suspend fun requestEmailForFindingId(name: String, email: String): EmailRequestResult {
        val body = SendVerificationMailFindIdRequestDTO(name = name, email = email)

        return try {
            serverApi.withCheck { sendVerificationMailForFindingId(body) }
            EmailRequestResult.SENT
        } catch (e: ServerException) {
            when (e.code) {
                "USER_1002", "USER_1006" -> EmailRequestResult.NO_MATCHED
                else -> EmailRequestResult.ERROR
            }
        }
    }

    override suspend fun checkVerificationCodeForFindingId(
        email: String,
        code: Int
    ): Pair<String, String>? {
        val body = CheckVerificationCodeRequestDTO(email = email, code = code.toString())

        return try {
            val response = serverApi.withCheck { findId(body = body) }
            response.name!! to response.id!!
        } catch (_: ServerException) {
            null
        }
    }

    override suspend fun requestEmailForFindingPassword(
        name: String,
        email: String,
        id: String
    ): EmailRequestResult {
        val body = SendVerificationMailFindPwRequestDTO(name = name, email = email, username = id)

        return try {
            serverApi.withCheck { sendVerificationMailForFindingPassword(body = body) }
            EmailRequestResult.SENT
        } catch (e: ServerException) {
            when (e.code) {
                "USER_1002", "USER_1007", "USER_1006", "USER_1008" -> EmailRequestResult.NO_MATCHED
                else -> EmailRequestResult.ERROR
            }
        }
    }

    override suspend fun checkVerificationCodeForFindingPassword(
        email: String,
        code: Int,
    ): Boolean {
        val body = CheckVerificationCodeRequestDTO(email = email, code = code.toString())

        return try {
            serverApi.withCheck { checkVerificationCode(body = body) }
            true
        } catch (_: ServerException) {
            false
        }
    }

    override suspend fun checkIdForFindingPassword(id: String): Boolean {
        return try {
            serverApi.withCheck { checkIdDuplicationOnFindingPassword(id = id) }
            true
        } catch (_: ServerException) {
            false
        }
    }

    override suspend fun changePassword(email: String, newPassword: String) {
        val body = FindPwRequestDTO(email = email, password = newPassword)
        serverApi.withCheck { findPassword(body = body) }
    }

    override suspend fun requestVerificationCodeForChangeEmail(email: String): EmailRequestResult {
        val body = MypageSendVerificationCodeRequestDTO(email = email)

        return try {
            serverApi.withAuth { sendVerificationMailForChangeEmail(body = body) }
            EmailRequestResult.SENT
        } catch (e: ServerException) {
            when (e.code) {
                "USER_1004" -> EmailRequestResult.DUPLICATED
                "COMMON400" -> EmailRequestResult.INVALID_EMAIL
                else -> EmailRequestResult.ERROR
            }
        }
    }

    override suspend fun changeEmailWithVerificationCode(email: String, code: Int): Boolean {
        val body = MypageVerifyVerificationCodeAndUpdateEmailRequestDTO(
            verificationCode = code.toString(),
            email = email,
        )

        return try {
            serverApi.withAuth { checkVerificationCodeForChangeEmail(body = body) }
            true
        } catch (_: ServerException) {
            false
        }
    }


    override suspend fun logout() {
        serverApi.withCheck { logout() }

        authPreference.accessToken = null
        authPreference.refreshToken = null

        settingPreference.pinHash = null
        settingPreference.lastSentFcmToken = null
    }

    override suspend fun getUserInfo(): UserInfo {
        val response = serverApi.withAuth { getUserInfo() }
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

    override suspend fun modifyUserInfo(name: String?) {
        val body = EditRequestDTO(
            nickname = name,
            email = null,
            profileImage = null,
            point = null,
        )

        serverApi.withAuth { updateUserInfo(body = body) }
    }

    override suspend fun leaveUser(reason: String?) {
        val body = DeleteRequestDTO(reason = reason ?: "")
        serverApi.withAuth { deleteUser(body = body) }

        authPreference.accessToken = null
        authPreference.refreshToken = null

        settingPreference.pinHash = null
        settingPreference.lastSentFcmToken = null
    }
}