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

    // ✅ 성공 메시지 저장하는 StateFlow 추가
    private val _idSuccessMessage = MutableStateFlow<String?>(null)
    val idSuccessMessage: StateFlow<String?> = _idSuccessMessage.asStateFlow()

    // ✅ 중복 확인 버튼 가시성 관리하는 StateFlow 추가
    private val _isCheckButtonVisible = MutableStateFlow(true)
    val isCheckButtonVisible: StateFlow<Boolean> = _isCheckButtonVisible.asStateFlow()

    private val _isPasswordValid = MutableStateFlow(false)
    val isPasswordValid: StateFlow<Boolean> = _isPasswordValid.asStateFlow()

    private val _isPasswordMatched = MutableStateFlow(false)
    val isPasswordMatched: StateFlow<Boolean> = _isPasswordMatched.asStateFlow()

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible.asStateFlow()

    private val _isConfirmPasswordVisible = MutableStateFlow(false)
    val isConfirmPasswordVisible: StateFlow<Boolean> = _isConfirmPasswordVisible.asStateFlow()

    private val _timerState = MutableStateFlow(600) // 10분 (600초)
    val timerState: StateFlow<Int> = _timerState.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false) // 타이머가 실행 중인지 확인
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _isNavigationTriggered = MutableStateFlow(false)
    val isNavigationTriggered: StateFlow<Boolean> = _isNavigationTriggered.asStateFlow()

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


    fun onNickNameChange(newNickName: String) {
        // 한글, 자모음 조합 허용 + 영문, 숫자, 특수문자 허용
        val allowedChars = Regex("^[가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9!@#\$%^&*()_+=<>?]*$")
        val isValid = allowedChars.matches(newNickName)

        if (newNickName.length <= 8 && isValid) {
            _nickNameState.value = newNickName
            _isWarningVisible.value = (newNickName.length == 8)

            _nicknameError.value = when {
                newNickName.trim().isEmpty() -> "닉네임에 공백만 입력할 수 없습니다."
                newNickName.length < 1 -> "닉네임은 최소 1자 이상 입력해야 합니다."
                else -> null
            }
        } else if (!isValid) {
            _nicknameError.value = "닉네임에는 한글, 영문, 숫자, 특수문자만 사용할 수 있습니다."
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
        _idSuccessMessage.value = null  //  중복 확인 성공 메시지 제거
        _isCheckButtonVisible.value = true  //  중복 확인 버튼 다시 보이게 설정
    }

    // ✅ 비밀번호 가시성 토글 함수
    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun toggleConfirmPasswordVisibility() {
        _isConfirmPasswordVisible.value = !_isConfirmPasswordVisible.value
    }

    fun onPasswordChange(newPassword: String) {
        _passwordState.value = newPassword

        // 비밀번호 검증 조건
        val hasUpperCase = newPassword.any { it.isUpperCase() }
        val hasLowerCase = newPassword.any { it.isLowerCase() }
        val hasDigit = newPassword.any { it.isDigit() }
        val hasSpecialChar = newPassword.any { it in "!@#$%^&*()-_=+[]{};:'\",.<>?/\\|" }
        val isValidLength = newPassword.length >= 8

        // 종류별 개수 카운트 (2개 이상 조합 필수)
        val charTypesCount = listOf(hasUpperCase, hasLowerCase, hasDigit, hasSpecialChar).count { it }

        // 비밀번호 유효성 검사 (길이 충족 + 특수문자 포함 + 2종류 이상 조합)
        _isPasswordValid.value = isValidLength && hasSpecialChar && charTypesCount >= 2
        _isPasswordMatched.value = (newPassword == _confirmPasswordState.value)
    }


    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _confirmPasswordState.value = newConfirmPassword
        _isPasswordMatched.value = (newConfirmPassword == _passwordState.value)
    }

    fun onNameChange(newName: String) {
        // 한글(완성형)과 영문(대소문자)만 허용하는 정규식
        val regex = Regex("^[가-힣a-zA-Z]*$")

        // 입력값을 즉시 반영 (한글 조합이 깨지지 않도록)
        _nameState.value = newName

        // 에러 메시지 설정 (입력값 전체가 유효한지 검사)
        _nameError.value = when {
            newName.isEmpty() -> "이름을 입력해주세요."
            !regex.matches(newName) -> "한글 입력 시 초성은 제한됩니다."
            newName.length > 8 -> "이름은 최대 8자까지 입력 가능합니다."
            else -> null
        }
    }

    fun onEmailLocalPartChange(newLocal: String) {
        _emailLocalPartState.value = newLocal
    }

    fun onEmailDomainChange(newDomain: String) {
        if (newDomain == "직접입력") {
            _emailDomainState.value = "" // ✅ "직접입력"을 선택하면 필드를 비움
            _isCustomDomain.value = true
        } else {
            _emailDomainState.value = newDomain
            _isCustomDomain.value = false
        }
    }

    fun enableCustomDomainInput() {
        _isCustomDomain.value = true
        _emailDomainState.value = "" // ✅ 도메인 필드를 비워서 바로 입력 가능하게 함
    }

    fun onCustomDomainChange(customDomain: String) {
        if (_isCustomDomain.value) {
            _emailDomainState.value = customDomain
        }
    }
    // ✅ 타이머 초기화 및 시작 메서드 (버튼 클릭 시 호출 가능)
    fun startTimer() {
        if (!_isTimerRunning.value) {
            _isTimerRunning.value = true
            _timerState.value = 600 // 타이머 초기화

            viewModelScope.launch {
                while (_timerState.value > 0) {
                    delay(1000L) // 1초 대기
                    _timerState.value -= 1
                }
                _isTimerRunning.value = false
                requestVerificationCodeForResend() // ✅ 타이머 종료 후 인증번호 자동 재전송
            }
        }
    }

    // ✅ 인증번호 재전송 요청 + 타이머 초기화 포함
    fun requestVerificationCodeForResend() {
        viewModelScope.launch {
            try {
                val email = "${_emailLocalPartState.value}@${_emailDomainState.value}"
                userRepository.requestVerificationCodeForJoining(email)
                _emailError.value = null // 기존 에러 제거
                println("✅ 인증번호가 이메일로 재전송되었습니다.")

                // ✅ 인증번호 재전송 후 타이머 초기화 및 다시 시작
                startTimer()
            } catch (e: Exception) {
                _emailError.value = "인증번호 재전송에 실패했습니다."
                println("❌ 인증번호 재전송 실패: ${e.message}")
            }
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
                    val email = "${_emailLocalPartState.value}@${_emailDomainState.value}"
                    val certiCode = newCode.toInt()

                    println("🔍 서버로 인증번호 확인 요청: 이메일=$email, 코드=$certiCode")

                    val isValid = userRepository.checkVerificationCodeForJoining(email, certiCode)

                    println("✅ 서버 응답: 인증번호 확인 결과 -> $isValid")

                    if (isValid) {
                        println("✅ 인증 성공! 회원가입 진행 중...")

                        userRepository.join(
                            nickname = _nickNameState.value,
                            id = _idState.value,
                            password = _passwordState.value,
                            name = _nameState.value,
                            email = email
                        )

                        println("✅ 회원가입 완료! onSuccess() 실행됨")
                        onSuccess()  // 🔥 여기서 onSuccess() 실행!
                    } else {
                        _certiError.value = "❌ 인증번호가 올바르지 않습니다."
                        println("❌ 인증 실패: 서버에서 false 반환")
                        onFailure()
                    }
                } catch (e: Exception) {
                    _certiError.value = "❌ 인증 과정에서 오류 발생: ${e.message}"
                    println("❌ 인증 중 오류 발생: ${e.message}")
                    onFailure()
                }
            }
        }
    }




    fun requestVerificationCode(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val email = "${_emailLocalPartState.value}@${_emailDomainState.value}"
                userRepository.requestVerificationCodeForJoining(email)
                _emailError.value = null // 기존 에러 제거
                onSuccess()

                // ✅ 이메일 발송 성공 후 타이머 시작
                startTimer()
            } catch (e: Exception) {
                _emailError.value = "이메일 발송에 실패했습니다."
                onFailure("이메일 발송에 실패했습니다.")
            }
        }
    }



//    private fun startTimer() {
//        viewModelScope.launch {
//            while (_timerState.value > 0) {
//                delay(1000L) // 1초 대기
//                _timerState.value -= 1
//            }
//        }
//    }


    fun checkIdAvailability() {
        viewModelScope.launch {
            if (_idState.value.isBlank()) {
                _idError.value = "아이디를 입력해주세요."
                _isIdAvailable.value = false
                _idSuccessMessage.value = null
                return@launch
            }

            _isLoading.value = true  // 로딩 시작
            try {
                val isOccupied = userRepository.isIdAlreadyOccupied(_idState.value)
                if (isOccupied) {
                    _idError.value = "이미 사용 중인 아이디입니다."
                    _isIdAvailable.value = false
                    _idSuccessMessage.value = null
                    _isCheckButtonVisible.value = true // 중복 확인 실패 시 버튼 유지
                } else {
                    _idError.value = null
                    _isIdAvailable.value = true
                    _idSuccessMessage.value = "사용 가능한 아이디입니다." // 성공 메시지 설정
                    _isCheckButtonVisible.value = false //  중복 확인 성공 시 버튼 숨기기
                }
            } catch (e: Exception) {
                _idError.value = "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                _isIdAvailable.value = false
                _idSuccessMessage.value = null
                _isCheckButtonVisible.value = true // 오류 발생 시 버튼 유지
            } finally {
                _isLoading.value = false  // 로딩 종료
            }
        }
    }

//    @Deprecated("Do not use")
//    fun verifyCertiCode(onSuccess: () -> Unit, onFailure: () -> Unit) {
//        viewModelScope.launch {
//            val isValid = userRepository.checkVerificationCodeForJoining(_certiCodeState.value.toIntOrNull() ?: -1)
//            if (isValid) {
//                userRepository.join(
//                    nickname = _nickNameState.value,
//                    id = _idState.value,
//                    password = _passwordState.value,
//                    name = _nameState.value,
//                    email = "${_emailLocalPartState.value}@${_emailDomainState.value}"
//                )
//                onSuccess()
//            } else {
//                _certiError.value = "인증번호가 올바르지 않습니다."
//                onFailure()
//            }
//        }
//    }
}