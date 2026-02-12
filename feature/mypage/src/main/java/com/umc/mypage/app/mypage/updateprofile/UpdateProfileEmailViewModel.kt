package com.umc.mypage.app.mypage.updateprofile

import androidx.lifecycle.viewModelScope
import com.umc.core.model.EmailRequestResult
import com.umc.core.repository.UserRepository
import com.umc.mypage.util.LoadingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class UpdateProfileEmailViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : LoadingViewModel() {
    private val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")

    private val _previousEmail = MutableStateFlow("")
    val previousEmail: StateFlow<String> = _previousEmail

    private val _newEmail = MutableStateFlow("")
    val newEmail: StateFlow<String> = _newEmail

    private val _emailVerificationCode = MutableStateFlow("")
    val emailVerificationCode: StateFlow<String> = _emailVerificationCode

    private val _isEmailCodeSent = MutableStateFlow(false)
    val isEmailCodeSent: StateFlow<Boolean> = _isEmailCodeSent

    private val _isEmailVerificationTimedOut = MutableStateFlow(false)
    val isEmailVerificationTimedOut: StateFlow<Boolean> = _isEmailVerificationTimedOut

    private val _isEmailDuplicated = MutableStateFlow(false)
    val isEmailDuplicated: StateFlow<Boolean> = _isEmailDuplicated

    private val _emailVerificationRemainTime = MutableStateFlow<Duration?>(null)
    val emailVerificationRemainTime: StateFlow<Duration?> = _emailVerificationRemainTime

    private val _isEmailValid = MutableStateFlow(false)
    val isEmailValid: StateFlow<Boolean> = _isEmailValid

    private val _isEmailDoneButtonEnabled = MutableStateFlow(false)
    val isEmailDoneButtonEnabled: StateFlow<Boolean> = _isEmailDoneButtonEnabled

    private var isInitialized = false
    private var emailSentTime: LocalDateTime? = null
    private var emailCountDownJob: Job? = null

    suspend fun loadInitialEmailIfNeeded() = runWithLoading {
        if (isInitialized) return@runWithLoading

        _previousEmail.value = userRepository.getUserInfo().email
        isInitialized = true
    }

    fun onNewEmailChanged(email: String) {
        _newEmail.value = email
        _isEmailValid.value = emailRegex.matches(email)

        _isEmailDuplicated.value = false
        _isEmailCodeSent.value = false
        _isEmailVerificationTimedOut.value = false
        _emailVerificationCode.value = ""
        _isEmailDoneButtonEnabled.value = false
        clearEmailCountDown()
    }

    fun onEmailCodeChanged(code: String) {
        _emailVerificationCode.value = code
        _isEmailDoneButtonEnabled.value = code.length == 6
    }

    suspend fun requestVerificationCodeForUpdateEmail(): EmailRequestResult = runWithLoading {
        val result = userRepository.requestVerificationCodeForChangeEmail(email = _newEmail.value)

        if (result == EmailRequestResult.DUPLICATED) {
            _isEmailDuplicated.value = true
            _isEmailCodeSent.value = false
            clearEmailCountDown()
        } else {
            _isEmailDuplicated.value = false
            _isEmailCodeSent.value = true
            _isEmailVerificationTimedOut.value = false
            _emailVerificationCode.value = ""
            _isEmailDoneButtonEnabled.value = false
            startEmailCountDown()
        }

        result
    }

    suspend fun verifyCodeForUpdateEmail(): Boolean = runWithLoading {
        userRepository.changeEmailWithVerificationCode(
            email = _newEmail.value,
            code = _emailVerificationCode.value.toInt(),
        )
    }

    private fun startEmailCountDown() {
        clearEmailCountDown()
        emailSentTime = LocalDateTime.now()

        emailCountDownJob = viewModelScope.launch {
            while (true) {
                val sentTime = emailSentTime ?: break
                val remain = Duration.between(LocalDateTime.now(), sentTime.plusMinutes(3))

                if (remain.isNegative) {
                    _isEmailVerificationTimedOut.value = true
                    _isEmailCodeSent.value = false
                    _emailVerificationCode.value = ""
                    _isEmailDoneButtonEnabled.value = false
                    clearEmailCountDown()
                    break
                }

                _emailVerificationRemainTime.value = remain
                delay(100)
            }
        }
    }

    private fun clearEmailCountDown() {
        emailCountDownJob?.cancel()
        emailCountDownJob = null
        emailSentTime = null
        _emailVerificationRemainTime.value = null
    }
}
