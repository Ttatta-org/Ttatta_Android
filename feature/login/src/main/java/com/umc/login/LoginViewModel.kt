package com.umc.login

import androidx.lifecycle.ViewModel
import com.umc.core.model.EmailRequestResult
import com.umc.core.repository.UserRepository
import com.umc.login.logic.certification.CertificationCodeValidationRequest
import com.umc.login.logic.certification.CertificationCodeValidationRequestForFindingId
import com.umc.login.logic.certification.CertificationCodeValidationRequestForFindingPassword
import com.umc.login.logic.certification.CertificationCodeValidationRequestForJoin
import com.umc.login.logic.certification.CertificationMailRequest
import com.umc.login.logic.certification.CertificationMailRequestForFindingId
import com.umc.login.logic.certification.CertificationMailRequestForFindingPassword
import com.umc.login.logic.certification.CertificationMailRequestForJoin
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class FindingIdValidationSuccessInfo(
    val name: String,
    val id: String,
)

data class FindingPasswordValidationSuccessInfo(
    val email: String,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private var findingIdValidationSuccessInfo: FindingIdValidationSuccessInfo? = null
    private var findingPasswordValidationSuccessInfo: FindingPasswordValidationSuccessInfo? = null

    suspend fun login(
        id: String,
        password: String,
    ) {
        userRepository.login(id = id, password = password)
    }

    suspend fun join(
        nickname: String,
        id: String,
        password: String,
        name: String,
        email: String,
    ) {
        userRepository.join(
            id = id,
            password = password,
            name = name,
            nickname = nickname,
            email = email,
        )
    }

    suspend fun checkIdDuplication(
        id: String,
    ): Boolean {
        return userRepository.isIdAlreadyOccupied(id = id)
    }

    suspend fun requestCertificationMail(
        request: CertificationMailRequest,
    ): Boolean {
        val result = when (request) {
            is CertificationMailRequestForJoin -> run {
                userRepository.requestVerificationCodeForJoining(
                    email = request.email,
                )
            }

            is CertificationMailRequestForFindingId -> run {
                userRepository.requestEmailForFindingId(
                    email = request.email,
                    name = request.name,
                )
            }

            is CertificationMailRequestForFindingPassword -> run {
                userRepository.requestEmailForFindingPassword(
                    name = request.name,
                    email = request.email,
                    id = request.id,
                )
            }
        }

        return result == EmailRequestResult.SENT
    }

    suspend fun requestCertificationCodeValidation(
        request: CertificationCodeValidationRequest,
    ): Boolean {
        return when (request) {
            is CertificationCodeValidationRequestForJoin -> run {
                return@run userRepository.checkVerificationCodeForJoining(
                    email = request.email,
                    code = request.code.toInt(),
                )
            }

            is CertificationCodeValidationRequestForFindingId -> run {
                val (id, name) = userRepository.checkVerificationCodeForFindingId(
                    email = request.email,
                    code = request.code.toInt(),
                ) ?: return@run false

                findingIdValidationSuccessInfo = FindingIdValidationSuccessInfo(
                    name = name,
                    id = id,
                )

                return@run true
            }

            is CertificationCodeValidationRequestForFindingPassword -> run {
                val isValid = userRepository.checkVerificationCodeForFindingPassword(
                    email = request.email,
                    code = request.code.toInt(),
                )

                if (isValid) findingPasswordValidationSuccessInfo =
                    FindingPasswordValidationSuccessInfo(email = request.email)

                return@run isValid
            }
        }
    }

    suspend fun checkIdExist(
        id: String,
    ): Boolean {
        return try {
            userRepository.checkIdForFindingPassword(id = id)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun findId(): Pair<String, String> {
        return findingIdValidationSuccessInfo?.let { (name, id) ->
            findingIdValidationSuccessInfo = null
            name to id
        } ?: throw Exception("findingIdSuccessInfo is null")
    }

    suspend fun changePassword(
        password: String,
    ) {
        findingPasswordValidationSuccessInfo?.let { (email) ->
            userRepository.changePassword(email = email, newPassword = password)
            findingPasswordValidationSuccessInfo = null
        }
    }

    suspend fun tryLoginWithKakaoOpenIdToken(
        idToken: String,
    ): Boolean {
        val isAlreadyJoined = userRepository.tryLoginWithKakao(openIdToken = idToken)
        return isAlreadyJoined
    }

    suspend fun sendInfosForKakaoJoin(
        idToken: String,
        nickname: String,
    ) {
        userRepository.postUserInfoWhenFirstKakaoLogin(
            openIdToken = idToken,
            nickname = nickname,
        )
    }
}