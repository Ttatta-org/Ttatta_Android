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

    private val _isWarningVisible = MutableStateFlow(false)
    val isWarningVisible: StateFlow<Boolean> = _isWarningVisible.asStateFlow()

    private val _nicknameError = MutableStateFlow<String?>(null)
    val nicknameError: StateFlow<String?> = _nicknameError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val isButtonEnabled: StateFlow<Boolean> = combine(
        _nickNameState, _nicknameError, _isLoading
    ) { nickName, error, loading ->
        nickName.isNotEmpty() && nickName.length <= 8 && error == null && !loading
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun onNickNameChange(newNickName: String) {
        if (newNickName.length <= 9) {
            _nickNameState.value = newNickName
            _isWarningVisible.value = (newNickName.length == 9)
            _nicknameError.value = null // 기존 에러 초기화
        }
    }

    fun checkNicknameAvailability() {
        viewModelScope.launch {
            if (_nickNameState.value.isBlank()) {
                _nicknameError.value = "닉네임을 입력해주세요."
                return@launch
            }

            _isLoading.value = true
            try {
                val isOccupied = userRepository.isIdAlreadyOccupied(_nickNameState.value)
                _nicknameError.value = if (isOccupied) "이미 사용 중인 닉네임입니다." else null
            } catch (e: Exception) {
                _nicknameError.value = "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            } finally {
                _isLoading.value = false
            }
        }
    }
}