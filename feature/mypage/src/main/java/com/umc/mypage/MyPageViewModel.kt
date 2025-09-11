package com.umc.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.umc.core.model.UserInfo
import com.umc.core.repository.SettingRepository
import com.umc.core.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
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

        val summaryOn: Boolean = false,
        val summaryHour24: Int = 13,

        val challengeOn: Boolean = false,
        val challengeRemainingHours: Int = 1,

        val locationOn: Boolean = false
    )

    private val _notificationUi = MutableStateFlow(NotificationSettingsUiState())
    val notificationUi: StateFlow<NotificationSettingsUiState> = _notificationUi

    init {
        loadNotificationSettings()
    }

    /** 화면 진입 시 서버 요약 한 방에 로드 */
    fun loadNotificationSettings() = viewModelScope.launch {
        _notificationUi.value = _notificationUi.value.copy(isLoading = true, error = null)
        runCatching {
            val s = settingRepository.getAlarmSummary()
            _notificationUi.value = NotificationSettingsUiState(
                isLoading = false,
                dailyOn = s.writingActive,
                dailyHour24 = s.writingTime?.hour ?: 20,
                dailyMinute = s.writingTime?.minute ?: 30,
                summaryOn = s.dailyActive,
                summaryHour24 = s.dailyTime?.hour ?: 13,
                challengeOn = s.challengeActive,
                challengeRemainingHours = s.challengeHoursAgo ?: 1,
                locationOn = s.memoryActive
            )
        }.onFailure { e ->
            _notificationUi.value = _notificationUi.value.copy(isLoading = false, error = e.message)
        }
    }

    // ===== 일기 작성 알림 =====
    fun onDailyToggle(isOn: Boolean) = viewModelScope.launch {
        val prev = _notificationUi.value
        if (isOn) {
            // 서버가 내려주는 기본 시간으로 세팅
            runCatching {
                val r = settingRepository.turnOnWritingDiary()
                val t = r.time ?: LocalTime.of(prev.dailyHour24, prev.dailyMinute)
                _notificationUi.value = prev.copy(
                    dailyOn = true,
                    dailyHour24 = t.hour,
                    dailyMinute = t.minute
                )
            }.onFailure {
                _notificationUi.value = prev.copy(dailyOn = false)
            }
        } else {
            runCatching { settingRepository.turnOffWritingDiary() }
                .onSuccess { _notificationUi.value = prev.copy(dailyOn = false) }
                .onFailure { _notificationUi.value = prev } // 롤백
        }
    }

    fun onDailyTimeChange(isPm: Boolean, hour12: Int, minute: Int) = viewModelScope.launch {
        val h24 = to24h(isPm, hour12)
        val prev = _notificationUi.value
        // 낙관적 반영
        _notificationUi.value = prev.copy(dailyHour24 = h24, dailyMinute = minute)
        runCatching {
            settingRepository.updateWritingDiaryTime(LocalTime.of(h24, minute))
        }.onFailure {
            // 실패 시 롤백
            _notificationUi.value = prev
        }
    }

    // ===== 하루 요약 알림 (오후 고정) =====
    fun onSummaryToggle(isOn: Boolean) = viewModelScope.launch {
        val prev = _notificationUi.value
        if (isOn) {
            runCatching {
                val r = settingRepository.turnOnDailySummary()
                val t = r.time ?: LocalTime.of(prev.summaryHour24, 0)
                _notificationUi.value = prev.copy(
                    summaryOn = true,
                    summaryHour24 = t.hour
                )
            }.onFailure {
                _notificationUi.value = prev.copy(summaryOn = false)
            }
        } else {
            runCatching { settingRepository.turnOffDailySummary() }
                .onSuccess { _notificationUi.value = prev.copy(summaryOn = false) }
                .onFailure { _notificationUi.value = prev }
        }
    }

    fun onSummaryHourChange(hour12: Int) = viewModelScope.launch {
        val h24 = to24hPm(hour12) // 오후 고정
        val prev = _notificationUi.value
        _notificationUi.value = prev.copy(summaryHour24 = h24)
        runCatching {
            settingRepository.updateDailySummaryTime(LocalTime.of(h24, 0))
        }.onFailure {
            _notificationUi.value = prev
        }
    }

    // ===== 챌린지 리마인드 =====
    fun onChallengeToggle(isOn: Boolean) = viewModelScope.launch {
        val prev = _notificationUi.value
        if (isOn) {
            runCatching {
                val r = settingRepository.turnOnChallengeRemind()
                val hours = r.hoursAgo ?: prev.challengeRemainingHours
                _notificationUi.value = prev.copy(
                    challengeOn = true,
                    challengeRemainingHours = hours
                )
            }.onFailure {
                _notificationUi.value = prev.copy(challengeOn = false)
            }
        } else {
            runCatching { settingRepository.turnOffChallengeRemind() }
                .onSuccess { _notificationUi.value = prev.copy(challengeOn = false) }
                .onFailure { _notificationUi.value = prev }
        }
    }

    fun onChallengeHoursChange(hours: Int) = viewModelScope.launch {
        val prev = _notificationUi.value
        _notificationUi.value = prev.copy(challengeRemainingHours = hours)
        runCatching {
            settingRepository.updateChallengeHoursAgo(hours)
        }.onFailure {
            _notificationUi.value = prev
        }
    }

    // ===== 위치 기반(토글형) =====
    fun onLocationToggle(isOn: Boolean) = viewModelScope.launch {
        val prev = _notificationUi.value
        _notificationUi.value = prev.copy(locationOn = isOn)
        runCatching {
            settingRepository.setMemoryDiaryActive(isOn)
        }.onFailure {
            _notificationUi.value = prev // 실패 시 롤백
        }
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