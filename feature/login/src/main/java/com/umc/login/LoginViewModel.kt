package com.umc.login

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.launch
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
): ViewModel() {

    private var findingIdValidationSuccessInfo: FindingIdValidationSuccessInfo? = null
    private var findingPasswordValidationSuccessInfo: FindingPasswordValidationSuccessInfo? = null

    fun login(
        id: String,
        password: String,
        onSucceed: () -> Unit = {},
        onFailed: (Exception) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                userRepository.login(id = id, password = password)
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun join(
        nickname: String,
        id: String,
        password: String,
        name: String,
        email: String,
        onSucceed: () -> Unit = {},
        onFailed: (Exception) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                userRepository.join(
                    id = id,
                    password = password,
                    name = name,
                    nickname = nickname,
                    email = email,
                )
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun checkIdDuplication(
        id: String,
        onSucceed: (isDuplicated: Boolean) -> Unit = {},
        onFailed: (Exception) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                val isDuplicated = userRepository.isIdAlreadyOccupied(id = id)
                onSucceed(isDuplicated)
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun requestCertificationMail(
        request: CertificationMailRequest,
        onSucceed: () -> Unit = {},
        onFailed: (Exception) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                when (request) {
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
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun requestCertificationCodeValidation(
        request: CertificationCodeValidationRequest,
        onSucceed: (isValid: Boolean) -> Unit = {},
        onFailed: (Exception) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                val isValid = when (request) {
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
                        if (isValid) findingPasswordValidationSuccessInfo = FindingPasswordValidationSuccessInfo(
                            email = request.email
                        )
                        return@run isValid
                    }
                }
                onSucceed(isValid)
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun checkIdExist(
        id: String,
        onSucceed: (isExist: Boolean) -> Unit = {},
        onFailed: (Exception) -> Unit = {},
    ) {
        viewModelScope.launch {
            val isExist = try {
                userRepository.checkIdForFindingPassword(id = id)
                true
            } catch (_: Exception) {
                false
            }
            onSucceed(isExist)
        }
    }

    fun findId(
        onSucceed: (id: String, name: String) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {},
    ) {
        findingIdValidationSuccessInfo?.let { (name, id) ->
            findingIdValidationSuccessInfo = null
            onSucceed(id, name)
        } ?: run {
            onFailed(Exception("findingIdSuccessInfo is null"))
        }
    }

    fun changePassword(
        password: String,
        onSucceed: () -> Unit = {},
        onFailed: (Exception) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                findingPasswordValidationSuccessInfo?.let { (email) ->
                    userRepository.changePassword(email = email, newPassword = password)
                    findingPasswordValidationSuccessInfo = null
                }
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun tryLoginWithKakaoOpenIdToken(
        idToken: String,
        onSucceed: (isLoggedIn: Boolean) -> Unit = {},
        onFailed: (e: Exception) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                val isAlreadyJoined = userRepository.tryLoginWithKakao(openIdToken = idToken)
                onSucceed(isAlreadyJoined)
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun sendInfosForKakaoJoin(
        idToken: String,
        nickname: String,
        onSucceed: () -> Unit = {},
        onFailed: (Exception) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                userRepository.postUserInfoWhenFirstKakaoLogin(
                    openIdToken = idToken,
                    nickname = nickname,
                )
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }
}