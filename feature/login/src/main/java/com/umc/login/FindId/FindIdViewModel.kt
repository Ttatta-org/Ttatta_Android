package com.umc.login.FindId

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.core.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FindIdViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _findNameState = MutableStateFlow("")
    val findNameState: StateFlow<String> = _findNameState.asStateFlow()

    private val _findEmailLocalPartState = MutableStateFlow("")
    val findEmailLocalPartState: StateFlow<String> = _findEmailLocalPartState.asStateFlow()

    private val _findEmailDomainState = MutableStateFlow("")
    val findEmailDomainState: StateFlow<String> = _findEmailDomainState.asStateFlow()

    private val _isCustomDomain = MutableStateFlow(false)
    val isCustomDomain: StateFlow<Boolean> = _isCustomDomain.asStateFlow()

    private val _findCertiCodeState = MutableStateFlow("")
    val findCertiCodeState: StateFlow<String> = _findCertiCodeState.asStateFlow()

    private val _findTimerState = MutableStateFlow(600) // 10분 타이머
    val findTimerState: StateFlow<Int> = _findTimerState.asStateFlow()

    private val _isFindCertiEnabled = MutableStateFlow(false)
    val isFindCertiEnabled: StateFlow<Boolean> = _isFindCertiEnabled.asStateFlow()

    private val _findError = MutableStateFlow<String?>(null)
    val findError: StateFlow<String?> = _findError.asStateFlow()

    private val _isFindButtonEnabled = MutableStateFlow(false)
    val isFindButtonEnabled: StateFlow<Boolean> = _isFindButtonEnabled.asStateFlow()

    private val _foundId = MutableStateFlow<String?>(null)
    val foundId: StateFlow<String?> = _foundId.asStateFlow()

    init {
        combine(_findNameState, _findEmailLocalPartState, _findEmailDomainState) { name, local, domain ->
            name.isNotEmpty() && local.isNotEmpty() && domain.isNotEmpty()
        }.onEach {
            _isFindButtonEnabled.value = it
        }.launchIn(viewModelScope)
    }

    fun onFindNameChange(newName: String) {
        val filteredName = newName.filter { it.isLetter() }
        if (filteredName.length <= 8) {
            _findNameState.value = filteredName
        }
    }

    fun onFindEmailLocalPartChange(newLocal: String) {
        _findEmailLocalPartState.value = newLocal
    }

    fun onFindEmailDomainChange(newDomain: String) {
        _findEmailDomainState.value = newDomain
        _isCustomDomain.value = newDomain == "직접입력"
    }

    fun onFindCustomDomainChange(customDomain: String) {
        if (_isCustomDomain.value) {
            _findEmailDomainState.value = customDomain
        }
    }

    fun onFindCertiCodeChange(newCode: String) {
        if (newCode.length <= 6) {
            _findCertiCodeState.value = newCode
        }
    }

//    fun requestFindCertiCode() {
//        val email = "${_findEmailLocalPartState.value}@${_findEmailDomainState.value}"
//        viewModelScope.launch {
//            try {
//                userRepository.requestEmailForFindingId(email)
//            } catch (e: Exception) {
//                _findError.value = "이메일 요청 실패: ${e.message}"
//            }
//        }
//    }

    fun findUserId() {
        val email = "${_findEmailLocalPartState.value}@${_findEmailDomainState.value}"
        val name = _findNameState.value

        viewModelScope.launch {
//            try {
//                val userId = userRepository.findUserId(name, email)
//                _foundId.value = userId
//            } catch (e: Exception) {
//                _findError.value = "아이디 찾기 실패: ${e.message}"
//            }
        }
    }
}