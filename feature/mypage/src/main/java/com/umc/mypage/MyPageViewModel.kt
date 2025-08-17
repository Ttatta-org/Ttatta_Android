package com.umc.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.umc.core.model.UserInfo
import com.umc.core.repository.SettingRepository
import com.umc.core.repository.UserRepository
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

    // ----- Pin/유저 메서드 유지 -----

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

    // ===== 알림 설정 UI 상태 =====
    data class NotificationSettingsUiState(
        val isLoading: Boolean = true,
        val error: String? = null,

        val dailyOn: Boolean = false,
        val dailyHour24: Int = 20,
        val dailyMinute: Int = 30,

        val summaryOn: Boolean = true,
        val summaryHour24: Int = 13,

        val challengeOn: Boolean = true,
        val challengeRemainingHours: Int = 1,

        val locationOn: Boolean = true
    )

    suspend fun ensureFcmRegistered() {
        if (!userRepository.isAlreadyLogin()) return

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                viewModelScope.launch {
                    try {
                        settingRepository.sendFcmToken(token)
                    } catch (e: Exception) {
                        Log.e("MyPageViewModel", "FCM 토큰 전송 실패: ${e.message}")
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("MyPageViewModel", "FCM 토큰 획득 실패: ${e.message}")
            }
    }

    private val _notificationUi = MutableStateFlow(NotificationSettingsUiState())
    val notificationUi: StateFlow<NotificationSettingsUiState> = _notificationUi

    init {
        // 필요 시 진입 시점에 알림 설정도 미리 로드
        loadNotificationSettings()
    }

    fun loadNotificationSettings() = viewModelScope.launch {
        _notificationUi.value = _notificationUi.value.copy(isLoading = true, error = null)
        runCatching {
            val diary = settingRepository.getNotificationSetting(com.umc.core.model.NotificationSetting.DiaryWriting::class)
            val summary = settingRepository.getNotificationSetting(com.umc.core.model.NotificationSetting.DailySummary::class)
            val chall = settingRepository.getNotificationSetting(com.umc.core.model.NotificationSetting.ChallengeRemind::class)
            val loc = settingRepository.getNotificationSetting(com.umc.core.model.NotificationSetting.LocationBasedRemind::class)

            _notificationUi.value = NotificationSettingsUiState(
                isLoading = false,
                dailyOn = diary.isOn,
                dailyHour24 = diary.hour,
                dailyMinute = diary.minute,
                summaryOn = summary.isOn,
                summaryHour24 = summary.hour,
                challengeOn = chall.isOn,
                challengeRemainingHours = chall.remainingHours,
                locationOn = loc.isOn
            )
        }.onFailure { e ->
            _notificationUi.value = _notificationUi.value.copy(isLoading = false, error = e.message)
        }
    }

    // ===== 액션: UI → 저장소 반영 =====
    fun onDailyToggle(isOn: Boolean) = viewModelScope.launch {
        _notificationUi.value = _notificationUi.value.copy(dailyOn = isOn)
        settingRepository.setNotification(
            com.umc.core.model.NotificationSetting.DiaryWriting(
                isOn = isOn,
                hour = _notificationUi.value.dailyHour24,
                minute = _notificationUi.value.dailyMinute
            )
        )
    }

    fun onDailyTimeChange(isPm: Boolean, hour12: Int, minute: Int) = viewModelScope.launch {
        val h24 = to24h(isPm, hour12)
        _notificationUi.value = _notificationUi.value.copy(dailyHour24 = h24, dailyMinute = minute)
        settingRepository.setNotification(
            com.umc.core.model.NotificationSetting.DiaryWriting(
                isOn = _notificationUi.value.dailyOn,
                hour = h24,
                minute = minute
            )
        )
    }

    fun onSummaryToggle(isOn: Boolean) = viewModelScope.launch {
        _notificationUi.value = _notificationUi.value.copy(summaryOn = isOn)
        settingRepository.setNotification(
            com.umc.core.model.NotificationSetting.DailySummary(
                isOn = isOn,
                hour = _notificationUi.value.summaryHour24
            )
        )
    }

    fun onSummaryHourChange(hour12: Int) = viewModelScope.launch {
        val h24 = to24hPm(hour12)          // 오후 고정 변환
        _notificationUi.value = _notificationUi.value.copy(summaryHour24 = h24)
        settingRepository.setNotification(
            com.umc.core.model.NotificationSetting.DailySummary(
                isOn = _notificationUi.value.summaryOn,
                hour = h24
            )
        )
    }

    fun onChallengeToggle(isOn: Boolean) = viewModelScope.launch {
        _notificationUi.value = _notificationUi.value.copy(challengeOn = isOn)
        settingRepository.setNotification(
            com.umc.core.model.NotificationSetting.ChallengeRemind(
                isOn = isOn,
                remainingHours = _notificationUi.value.challengeRemainingHours
            )
        )
    }

    fun onChallengeHoursChange(hours: Int) = viewModelScope.launch {
        _notificationUi.value = _notificationUi.value.copy(challengeRemainingHours = hours)
        settingRepository.setNotification(
            com.umc.core.model.NotificationSetting.ChallengeRemind(
                isOn = _notificationUi.value.challengeOn,
                remainingHours = hours
            )
        )
    }

    fun onLocationToggle(isOn: Boolean) = viewModelScope.launch {
        _notificationUi.value = _notificationUi.value.copy(locationOn = isOn)
        settingRepository.setNotification(
            com.umc.core.model.NotificationSetting.LocationBasedRemind(
                isOn = isOn
            )
        )
    }

    // ===== 12/24시간 변환 유틸 =====
    private fun to24h(isPm: Boolean, hour12: Int): Int {
        val base = if (hour12 == 12) 0 else hour12
        return if (isPm) base + 12 else base
    }
    // 오후(PM) 고정: 12 -> 12, 1..11 -> 13..23
    private fun to24hPm(hour12: Int): Int =
        if (hour12 == 12) 12 else hour12 + 12

}