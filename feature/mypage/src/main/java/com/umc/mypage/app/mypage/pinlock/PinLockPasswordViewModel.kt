package com.umc.mypage.app.mypage.pinlock

import com.umc.core.repository.SettingRepository
import com.umc.core.util.runWithScope
import com.umc.mypage.util.LoadingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PinLockPasswordViewModel @Inject constructor(
    private val settingRepository: SettingRepository,
) : LoadingViewModel() {
    private var isChangeMode = false
    private var onPinSuccessfullySetListener: (() -> Unit)? = null
    private var onPinSettingFailedListener: (() -> Unit)? = null
    private var password1 = ""
    private var password2 = ""
    private var currentStep = 0

    private val _showPasswordWrongMessage = MutableStateFlow(false)
    val showPasswordWrongMessage: StateFlow<Boolean> = _showPasswordWrongMessage

    private val _title = MutableStateFlow("암호 잠금")
    val title: StateFlow<String> = _title

    private val _description = MutableStateFlow("암호를 입력해주세요.")
    val description: StateFlow<String> = _description

    private val _fillCount = MutableStateFlow(0)
    val fillCount: StateFlow<Int> = _fillCount

    private var isInitialized = false

    fun initialize(changeMode: Boolean) {
        if (isInitialized) return

        isChangeMode = changeMode
        password1 = ""
        password2 = ""
        currentStep = 0
        _showPasswordWrongMessage.value = false
        refreshDerivedUiState()
        isInitialized = true
    }

    fun setOnPinSuccessfullySetListener(listener: () -> Unit) {
        onPinSuccessfullySetListener = listener
    }

    fun setOnPinSettingFailedListener(listener: () -> Unit) {
        onPinSettingFailedListener = listener
    }

    fun onNumberClicked(number: Char) {
        when (currentStep) {
            0 -> if (password1.length < 4) password1 += number
            1 -> if (password2.length < 4) password2 += number
        }

        evaluateStepProgress()
    }

    fun onEraseButtonClicked() {
        when (currentStep) {
            0 -> password1 = password1.dropLast(1)
            1 -> password2 = password2.dropLast(1)
        }

        refreshDerivedUiState()
    }

    fun onCancelButtonClicked() {
        when (currentStep) {
            0 -> password1 = ""
            1 -> password2 = ""
        }

        refreshDerivedUiState()
    }

    private fun evaluateStepProgress() {
        val readyPin: Int? = when (currentStep) {
            0 -> {
                if (password1.length == 4) currentStep = 1
                null
            }

            1 -> {
                if (password2.length != 4) {
                    null
                } else if (password1 == password2) {
                    password2.toInt()
                } else {
                    password2 = ""
                    _showPasswordWrongMessage.value = true
                    null
                }
            }

            else -> null
        }

        refreshDerivedUiState()

        if (readyPin == null) return

        runWithScope {
            runCatching { runWithLoading { settingRepository.setPin(readyPin) } }
                .onSuccess { onPinSuccessfullySetListener?.invoke() }
                .onFailure {
                    password2 = ""
                    refreshDerivedUiState()
                    onPinSettingFailedListener?.invoke()
                }
        }
    }

    private fun refreshDerivedUiState() {
        _title.value = if (isChangeMode) "암호 변경" else "암호 잠금"

        _description.value = when (currentStep) {
            0 -> if (isChangeMode) "새로운 암호를 입력해주세요." else "암호를 입력해주세요."
            1 -> "확인을 위해 한 번 더 입력해주세요."
            else -> ""
        }

        _fillCount.value = when (currentStep) {
            0 -> password1.length
            1 -> password2.length
            else -> 0
        }
    }
}
