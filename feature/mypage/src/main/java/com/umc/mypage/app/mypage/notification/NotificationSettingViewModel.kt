package com.umc.mypage.app.mypage.notification

import com.umc.core.repository.SettingRepository
import com.umc.mypage.util.LoadingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class NotificationSettingViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : LoadingViewModel() {
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

    suspend fun loadNotificationSettings() = runWithLoading {
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

    suspend fun onDailyToggle(isOn: Boolean) = runWithLoading {
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
                .onFailure { _notificationUi.value = prev }
        }
    }

    suspend fun onDailyTimeChange(isPm: Boolean, hour12: Int, minute: Int) = runWithLoading {
        val h24 = to24h(isPm, hour12)
        val prev = _notificationUi.value
        _notificationUi.value = prev.copy(dailyHour24 = h24, dailyMinute = minute)

        runCatching {
            settingRepository.updateWritingDiaryTime(LocalTime.of(h24, minute))
        }.onFailure {
            // 실패 시 롤백
            _notificationUi.value = prev
        }
    }

    suspend fun onSummaryToggle(isOn: Boolean) = runWithLoading {
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

    suspend fun onSummaryHourChange(hour12: Int) = runWithLoading {
        val h24 = to24hPm(hour12) // 오후 고정
        val prev = _notificationUi.value
        _notificationUi.value = prev.copy(summaryHour24 = h24)

        runCatching {
            settingRepository.updateDailySummaryTime(LocalTime.of(h24, 0))
        }.onFailure {
            _notificationUi.value = prev
        }
    }

    suspend fun onChallengeToggle(isOn: Boolean) = runWithLoading {
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

    suspend fun onChallengeHoursChange(hours: Int) =  runWithLoading {
        val prev = _notificationUi.value
        _notificationUi.value = prev.copy(challengeRemainingHours = hours)

        runCatching {
            settingRepository.updateChallengeHoursAgo(hours)
        }.onFailure {
            _notificationUi.value = prev
        }
    }

    suspend fun onLocationToggle(isOn: Boolean) = runWithLoading {
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
