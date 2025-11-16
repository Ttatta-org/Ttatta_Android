package com.umc.ttatta.app

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.umc.core.repository.ChallengeRepository
import com.umc.core.repository.ItemRepository
import com.umc.core.repository.SettingRepository
import com.umc.core.repository.UserRepository
import com.umc.design.character.AccessorySet
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import androidx.core.content.edit

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
    private val equippedAccessoriesState = mutableStateOf(AccessorySet.create())

    private var isLoggedInBefore: Boolean
        get() = prefs.getBoolean("isLoggedInBefore", false)
        set(value) {
            prefs.edit { putBoolean("isLoggedInBefore", value) }
        }

    val isLoggedInState: StateFlow<Boolean?> get() = isLoggedInFlow
    val isLockedState: StateFlow<Boolean> get() = isLockedFlow
    val isLocationBasedRemindEnabledState: StateFlow<Boolean> get() = isLocationBasedRemindEnabledFlow
    val userName get() = userNameState.value
    val equippedAccessories get() = equippedAccessoriesState.value

    suspend fun refresh() {
        isLoggedInFlow.value = null

        val isLoggedIn = runCatching {
            userRepository.isAlreadyLogin()
        }.getOrDefault(defaultValue = false)

        if (isLoggedIn) {
            runBlocking {
                launch { runCatching { checkIsLocationBasedRemindEnabled() } }
                launch { runCatching { getUserName() } }
                launch { runCatching { getEquippedAccessories() } }
                launch { runCatching { handleFcmToken() } }
                launch { runCatching { syncPin() } }
            }

            if (isLoggedInBefore) {
                isLockedFlow.value = settingRepository.getIsPinSet()
            } else {
                isLoggedInBefore = true
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
    }

    private suspend fun getUserName() {
        userNameState.value = userRepository.getUserInfo().name
    }

    private suspend fun getEquippedAccessories() {
        val equippedItems = itemRepository.getEquippedItems()
        equippedAccessoriesState.value = AccessorySet.create(
            equippedItems.map { item -> item.item }
        )
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
