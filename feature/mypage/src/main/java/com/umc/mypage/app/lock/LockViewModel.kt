package com.umc.mypage.app.lock

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.umc.core.repository.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LockViewModel @Inject constructor(
    private val settingRepository: SettingRepository
): ViewModel() {

    suspend fun isPinCorrect(pin: String): Boolean {
        if (!settingRepository.getIsPinSet()) return true
        if (pin.length != 4 || !pin.isDigitsOnly()) return false
        return settingRepository.getIsPinCorrect(pin = pin.toInt())
    }
}