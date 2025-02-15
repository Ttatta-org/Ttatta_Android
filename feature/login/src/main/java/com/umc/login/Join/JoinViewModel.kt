package com.umc.login.Join

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.core.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _nickNameState = MutableStateFlow("")
    val nickNameState: StateFlow<String> = _nickNameState.asStateFlow()

    private val _idState = MutableStateFlow("")
    val idState: StateFlow<String> = _idState.asStateFlow()

    private val _passwordState = MutableStateFlow("")
    val passwordState: StateFlow<String> = _passwordState.asStateFlow()

    private val _isWarningVisible = MutableStateFlow(false)
    val isWarningVisible: StateFlow<Boolean> = _isWarningVisible.asStateFlow()

    private val _nicknameError = MutableStateFlow<String?>(null)
    val nicknameError: StateFlow<String?> = _nicknameError.asStateFlow()

    private val _idError = MutableStateFlow<String?>(null)
    val idError: StateFlow<String?> = _idError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _confirmPasswordState = MutableStateFlow("")
    val confirmPasswordState: StateFlow<String> = _confirmPasswordState.asStateFlow()

    private val _isPasswordValid = MutableStateFlow(false)
    val isPasswordValid: StateFlow<Boolean> = _isPasswordValid.asStateFlow()

    private val _isPasswordMatched = MutableStateFlow(false)
    val isPasswordMatched: StateFlow<Boolean> = _isPasswordMatched.asStateFlow()

    val isNicknameButtonEnabled: StateFlow<Boolean> = combine(
        _nickNameState, _nicknameError, _isLoading
    ) { nickName, error, loading ->
        nickName.isNotEmpty() && nickName.length <= 8 && error == null && !loading
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    val isIdButtonEnabled: StateFlow<Boolean> = combine(
        _idState, _idError, _isLoading
    ) { id, error, loading ->
        id.isNotEmpty() && id.length <= 15 && error == null && !loading
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun onNickNameChange(newNickName: String) {
        if (newNickName.length <= 9) {
            _nickNameState.value = newNickName
            _isWarningVisible.value = (newNickName.length == 9)
            _nicknameError.value = null // 기존 에러 초기화
        }
    }

    fun onIdChange(newId: String) {
        if (newId.length <= 16) {
            _idState.value = newId
            _isWarningVisible.value = (newId.length == 16)
            _idError.value = null // 기존 에러 초기화
        }
    }

    fun onPasswordChange(newPassword: String) {
        _passwordState.value = newPassword
        _isPasswordValid.value = newPassword.length >= 8
        _isPasswordMatched.value = (newPassword == _confirmPasswordState.value)
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _confirmPasswordState.value = newConfirmPassword
        _isPasswordMatched.value = (newConfirmPassword == _passwordState.value)
    }

    fun checkNicknameAvailability() {
        viewModelScope.launch {
            if (_nickNameState.value.isBlank()) {
                _nicknameError.value = "닉네임을 입력해주세요."
                return@launch
            }

            _isLoading.value = true
            try {
                // ✅ 현재는 로컬에서 검사 (백엔드 미구현 상태이므로)
                val isOccupied = userRepository.isNicknameAlreadyOccupied(_nickNameState.value)
                _nicknameError.value = if (isOccupied) "이미 사용 중인 닉네임입니다." else null
            } catch (e: Exception) {
                _nicknameError.value = "오류가 발생했습니다. 나중에 다시 시도해주세요."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun checkIdAvailability() {
        viewModelScope.launch {
            if (_idState.value.isBlank()) {
                _idError.value = "아이디를 입력해주세요."
                return@launch
            }

            _isLoading.value = true
            try {
                val isOccupied = userRepository.isIdAlreadyOccupied(_idState.value)
                _idError.value = if (isOccupied) "이미 사용 중인 아이디입니다." else null
            } catch (e: Exception) {
                _idError.value = "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            } finally {
                _isLoading.value = false
            }
        }
    }
}