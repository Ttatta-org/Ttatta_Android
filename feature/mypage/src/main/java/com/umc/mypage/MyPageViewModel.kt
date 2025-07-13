package com.umc.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.core.model.UserInfo
import com.umc.core.repository.UserRepository
import com.umc.core.setting.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val settingRepository: SettingRepository
) : ViewModel() {

    // ✅ 유저 정보를 저장할 StateFlow
    private val _userInfoState = MutableStateFlow<UserInfo?>(null)
    val userInfoState: StateFlow<UserInfo?> = _userInfoState

    // ✅ API 요청 중인지 확인하는 로딩 상태
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // ✅ API 에러 메시지 저장
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isPinEnabled = MutableStateFlow(false)
    val isPinEnabled: StateFlow<Boolean> = _isPinEnabled

    /**
     * ✅ 유저 정보 불러오기 (백엔드 API 호출)
     */
    fun loadUserInfo() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("MyPageViewModel", "🚀 유저 정보 불러오는 중...")
                val userInfo = userRepository.getUserInfo()
                _userInfoState.value = userInfo // ✅ UI에 반영
                Log.d("MyPageViewModel", "✅ 유저 정보 로드 성공: $userInfo")
            } catch (e: Exception) {
                _errorMessage.value = "유저 정보를 불러오는 데 실패했습니다."
                Log.e("MyPageViewModel", "❌ 유저 정보 불러오기 실패: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * ✅ 로그아웃 기능 (백엔드 API 호출)
     */
    fun logout(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("MyPageViewModel", "🚀 로그아웃 중...")
                userRepository.logout()
                Log.d("MyPageViewModel", "✅ 로그아웃 성공")
                onSuccess()
            } catch (e: Exception) {
                Log.e("MyPageViewModel", "❌ 로그아웃 실패: ${e.message}")
                onError("로그아웃에 실패했습니다.")
            }
        }
    }

    /**
     * ✅ 유저 탈퇴 기능
     */
    fun leaveUser(
        reason: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("MyPageViewModel", "🚀 회원 탈퇴 중...")
                userRepository.leaveUser(reason)
                Log.d("MyPageViewModel", "✅ 회원 탈퇴 성공")
                onSuccess()
            } catch (e: Exception) {
                Log.e("MyPageViewModel", "❌ 회원 탈퇴 실패: ${e.message}")
                onError("회원 탈퇴에 실패했습니다.")
            }
        }
    }

    fun refreshPinStatus() {
        viewModelScope.launch {
            _isPinEnabled.value = settingRepository.getIsPinSet()
        }
    }

    fun savePin(pin: Int) {
        viewModelScope.launch {
            settingRepository.setPin(pin)
            _isPinEnabled.value = true
        }
    }

    fun clearPin() {
        viewModelScope.launch {
            settingRepository.clearPin()
            _isPinEnabled.value = false
        }
    }


    suspend fun isPinCorrect(input: Int): Boolean {
        return settingRepository.getIsPinCorrect(input)
    }

    suspend fun isPinSet(): Boolean {
        return settingRepository.getIsPinSet()
    }
}