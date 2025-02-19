package com.umc.login.Join

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.core.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

    private val _confirmPasswordState = MutableStateFlow("")
    val confirmPasswordState: StateFlow<String> = _confirmPasswordState.asStateFlow()

    private val _nameState = MutableStateFlow("")
    val nameState: StateFlow<String> = _nameState.asStateFlow()

    private val _emailLocalPartState = MutableStateFlow("")
    val emailLocalPartState: StateFlow<String> = _emailLocalPartState.asStateFlow()

    private val _emailDomainState = MutableStateFlow("")
    val emailDomainState: StateFlow<String> = _emailDomainState.asStateFlow()

    private val _isCustomDomain = MutableStateFlow(false)
    val isCustomDomain: StateFlow<Boolean> = _isCustomDomain.asStateFlow()

    private val _certiCodeState = MutableStateFlow("")
    val certiCodeState: StateFlow<String> = _certiCodeState.asStateFlow()

    private val _timerState = MutableStateFlow(600) // 10분 (600초)
    val timerState: StateFlow<Int> = _timerState.asStateFlow()

    private val _isWarningVisible = MutableStateFlow(false)
    val isWarningVisible: StateFlow<Boolean> = _isWarningVisible.asStateFlow()

    private val _nicknameError = MutableStateFlow<String?>(null)
    val nicknameError: StateFlow<String?> = _nicknameError.asStateFlow()

    private val _idError = MutableStateFlow<String?>(null)
    val idError: StateFlow<String?> = _idError.asStateFlow()

    private val _nameError = MutableStateFlow<String?>(null)
    val nameError: StateFlow<String?> = _nameError.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _certiError = MutableStateFlow<String?>(null)
    val certiError: StateFlow<String?> = _certiError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isIdAvailable = MutableStateFlow(false) // 중복 확인 성공 여부
    val isIdAvailable: StateFlow<Boolean> = _isIdAvailable.asStateFlow()

    private val _isPasswordValid = MutableStateFlow(false)
    val isPasswordValid: StateFlow<Boolean> = _isPasswordValid.asStateFlow()

    private val _isPasswordMatched = MutableStateFlow(false)
    val isPasswordMatched: StateFlow<Boolean> = _isPasswordMatched.asStateFlow()

    // 버튼 활성화 조건 수정 (공백만 입력되거나 2자 미만인 경우 비활성화)
    val isNicknameButtonEnabled: StateFlow<Boolean> = combine(
        _nickNameState, _nicknameError, _isLoading
    ) { nickName, error, loading ->
        nickName.trim().isNotEmpty() && nickName.length in 1..8 && error == null && !loading
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    val isIdButtonEnabled: StateFlow<Boolean> = combine(
        _idState, _idError, _isLoading, _isIdAvailable
    ) { id, error, loading, isAvailable ->
        id.isNotEmpty() && id.length <= 15 && error == null && !loading && isAvailable
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    val isNameButtonEnabled: StateFlow<Boolean> = _nameState
        .map { it.isNotEmpty() && it.length <= 8 && _nameError.value == null }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val isEmailValid: StateFlow<Boolean> = combine(
        _emailLocalPartState, _emailDomainState
    ) { local, domain ->
        val email = "$local@$domain"
        email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    val isCertiCodeValid: StateFlow<Boolean> = _certiCodeState
        .map { it.length == 6 }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    init {
        startTimer()
    }

    fun onNickNameChange(newNickName: String) {
        // 한글과 영문(대소문자)만 허용
        val filteredNickName = newNickName.filter { it.isLetter() || it == ' ' }

        if (filteredNickName.length <= 8) {
            _nickNameState.value = filteredNickName
            _isWarningVisible.value = (filteredNickName.length == 8)

            // 닉네임이 공백만 있을 경우 오류 메시지 표시
            _nicknameError.value = when {
                filteredNickName.trim().isEmpty() -> "닉네임에 공백만 입력할 수 없습니다."
                filteredNickName.length < 1 -> "닉네임은 최소 1자 이상 입력해야 합니다."
                else -> null
            }
        }
    }

    fun onIdChange(newId: String) {
        // 영어(대소문자)와 숫자만 허용
        val filteredId = newId.filter { it.isLetterOrDigit() }

        // 입력된 값이 15자를 초과하지 않도록 제한
        if (filteredId.length > 15) return

        // 입력값 즉시 상태 반영 (UI에서 안 보이는 문제 해결)
        _idState.value = filteredId

        // 에러 메시지 설정
        _idError.value = when {
            filteredId.isEmpty() -> "아이디를 입력해주세요."
            filteredId.length < 6 -> "아이디는 최소 6자 이상 입력해야 합니다."
            else -> null
        }

        // 아이디 변경 시 중복 확인 플래그 초기화
        _isIdAvailable.value = false
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

    fun onNameChange(newName: String) {
        // 허용하는 문자: 한글 완성형(가-힣) + 영문(a-z, A-Z)
        val filteredName = newName.filter { it.isLetter() && (it in '가'..'힣' || it in 'a'..'z' || it in 'A'..'Z') }

        if (filteredName.length <= 8) {
            _nameState.value = filteredName
            _nameError.value = if (newName != filteredName) "한글과 영문만 입력 가능합니다." else null
        }
    }

    fun onEmailLocalPartChange(newLocal: String) {
        _emailLocalPartState.value = newLocal
    }

    fun onEmailDomainChange(newDomain: String) {
        _emailDomainState.value = newDomain
        _isCustomDomain.value = newDomain == "직접입력"
    }

    fun onCustomDomainChange(customDomain: String) {
        if (_isCustomDomain.value) {
            _emailDomainState.value = customDomain
        }
    }

    fun onCertiCodeChange(
        newCode: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        if (newCode.length <= 6) {
            _certiCodeState.value = newCode
            _certiError.value = null // 입력 시 기존 에러 제거
        }

        if (newCode.length == 6) {
            viewModelScope.launch {
                try {
                    val isValid = userRepository.checkVerificationCodeForJoining(_certiCodeState.value.toIntOrNull() ?: -1)
                    if (isValid) {
                        userRepository.join(
                            nickname = _nickNameState.value,
                            id = _idState.value,
                            password = _passwordState.value,
                            name = _nameState.value,
                            email = "${_emailLocalPartState.value}@${_emailDomainState.value}"
                        )
                        onSuccess()
                    } else {
                        _certiError.value = "인증번호가 올바르지 않습니다."
                        throw Exception()
                    }
                } catch (e: Exception) {
                    onFailure()
                }
            }
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (_timerState.value > 0) {
                delay(1000L) // 1초 대기
                _timerState.value -= 1
            }
        }
    }


    fun checkIdAvailability() {
        viewModelScope.launch {
            if (_idState.value.isBlank()) {
                _idError.value = "아이디를 입력해주세요."
                _isIdAvailable.value = false
                return@launch
            }

            _isLoading.value = true  // 로딩 시작
            try {
                val isOccupied = userRepository.isIdAlreadyOccupied(_idState.value)
                if (isOccupied) {
                    _idError.value = "이미 사용 중인 아이디입니다."
                    _isIdAvailable.value = false
                } else {
                    _idError.value = null
                    _isIdAvailable.value = true
                }
            } catch (e: Exception) {
                _idError.value = "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                _isIdAvailable.value = false
            } finally {
                _isLoading.value = false  // 로딩 종료
            }
        }
    }

    @Deprecated("Do not use")
    fun verifyCertiCode(onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            val isValid = userRepository.checkVerificationCodeForJoining(_certiCodeState.value.toIntOrNull() ?: -1)
            if (isValid) {
                userRepository.join(
                    nickname = _nickNameState.value,
                    id = _idState.value,
                    password = _passwordState.value,
                    name = _nameState.value,
                    email = "${_emailLocalPartState.value}@${_emailDomainState.value}"
                )
                onSuccess()
            } else {
                _certiError.value = "인증번호가 올바르지 않습니다."
                onFailure()
            }
        }
    }
}