package com.umc.ttatta.app

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.umc.core.repository.ChallengeRepository
import com.umc.core.repository.ItemRepository
import com.umc.core.repository.SettingRepository
import com.umc.core.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val userRepository: UserRepository,
    private val itemRepository: ItemRepository,
    private val challengeRepository: ChallengeRepository,
    private val settingRepository: SettingRepository,
) : ViewModel() {
    private val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    private val isLoggedInFlow = MutableStateFlow<Boolean?>(null)
    private val isLockedFlow = MutableStateFlow(false)  // 잠금화면을 띄워야 하는가에 대한 여부
    private val isLocationBasedRemindEnabledFlow = MutableStateFlow(false)
    private val userNameState = mutableStateOf("")

    private var isLoggedInBefore: Boolean
        get() = prefs.getBoolean("isLoggedInBefore", false)
        set(value) {
            prefs.edit { putBoolean("isLoggedInBefore", value) }
        }

    private var isDiaryRecordOccurred: Boolean
        get() = prefs.getBoolean("isDiaryRecordOccurred", false)
        set(value) {
            prefs.edit { putBoolean("isDiaryRecordOccurred", value) }
        }

    val isLoggedInState: StateFlow<Boolean?> get() = isLoggedInFlow
    val isLockedState: StateFlow<Boolean> get() = isLockedFlow
    val isLocationBasedRemindEnabledState: StateFlow<Boolean> get() = isLocationBasedRemindEnabledFlow
    val equippedItemState get() = itemRepository.equippedItemState
    val userName get() = userNameState.value

    val isDiaryRecordOccurredState = MutableStateFlow(isDiaryRecordOccurred).also {
        viewModelScope.launch { it.collect { value -> isDiaryRecordOccurred = value } }
    }

    suspend fun refresh() {
        isLoggedInFlow.value = null

        val isLoggedIn = runCatching {
            userRepository.isAlreadyLogin()
        }.getOrDefault(defaultValue = false)

        if (isLoggedIn) withContext(Dispatchers.IO) {
            coroutineScope {
                launch { runCatching { checkIsLocationBasedRemindEnabled() } }
                launch { runCatching { getUserName() } }
                launch { runCatching { getEquippedAccessories() } }
                launch { runCatching { handleFcmToken() } }
                launch { runCatching { syncPin() } }
            }
        } else {
            isLoggedInBefore = false
            isLockedFlow.value = false
        }

        isLoggedInFlow.value = isLoggedIn
    }

    suspend fun makeChallengeComplete(challengeId: Long): Int {
        challengeRepository.completeChallenge(id = challengeId)
        return 50  // TODO: 실제 얻은 포인트를 반환하도록 수정
    }

    private suspend fun checkIsLocationBasedRemindEnabled() {
        isLocationBasedRemindEnabledFlow.value = settingRepository.getAlarmSummary().memoryActive
    }

    private suspend fun syncPin() {
        settingRepository.syncPinWithServer()

        if (isLoggedInBefore) {
            isLockedFlow.value = settingRepository.getIsPinSet()
        } else {
            isLoggedInBefore = true
        }
    }

    private suspend fun getUserName() {
        userNameState.value = userRepository.getUserInfo().name
    }

    private suspend fun getEquippedAccessories() {
        itemRepository.getEquippedItems()
    }

    private fun handleFcmToken() {
        val instance = FirebaseMessaging.getInstance()

        instance.token.addOnCompleteListener { tokenTask ->
            if (!tokenTask.isSuccessful) return@addOnCompleteListener

            val newToken = tokenTask.result
            Log.d("MainViewModel", "FCM token: $newToken")
            viewModelScope.launch {
                runCatching { settingRepository.sendFcmToken(token = newToken) }
            }
        }
    }
}
