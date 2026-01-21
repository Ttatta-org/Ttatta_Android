package com.umc.mypage.app.mypage.pinlock

import com.umc.core.repository.SettingRepository
import com.umc.mypage.util.LoadingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PinLockViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : LoadingViewModel() {

    private val _isPinEnabled = MutableStateFlow(false)
    val isPinEnabled: StateFlow<Boolean> = _isPinEnabled

    suspend fun refreshPinStatus() = runWithLoading {
        _isPinEnabled.value = settingRepository.getIsPinSet()
    }

    suspend fun savePin(pin: Int) = runWithLoading {
        settingRepository.setPin(pin)
        _isPinEnabled.value = true
    }

    suspend fun clearPin() = runWithLoading {
        settingRepository.clearPin()
        _isPinEnabled.value = false
    }
}
