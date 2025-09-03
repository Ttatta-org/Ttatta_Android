package com.umc.ttatta

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
import com.umc.ttatta.util.runWithScope
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
): ViewModel() {
    private val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    private val isLoggedInFlow = MutableStateFlow<Boolean?>(null)
    private val isLockedFlow = MutableStateFlow(prefs.getBoolean("isLocked", false))  // 잠금화면을 띄워야 하는가에 대한 여부
    private val userNameState = mutableStateOf("")
    private val equippedAccessoriesState = mutableStateOf(AccessorySet.create())

    val isLoggedInState: StateFlow<Boolean?> get() = isLoggedInFlow
    val isLockedState: StateFlow<Boolean> get() = isLockedFlow
    val userName get() = userNameState.value
    val equippedAccessories get() = equippedAccessoriesState.value

    init {
        runWithScope { checkLogin() }
    }

    suspend fun checkLogin() {
        isLoggedInFlow.value = null

        val isLoggedIn = runCatching {
            userRepository.isAlreadyLogin()
        }.getOrDefault(defaultValue = false)

        if (isLoggedIn) runBlocking {
            launch { getUserName() }
            launch { getEquippedAccessories() }
            launch { handleFcmToken() }
            launch { syncPin() }
        } else {
            prefs.edit { putBoolean("isLocked", false) }
            isLockedFlow.value = false
        }

        isLoggedInFlow.value = isLoggedIn
    }

    suspend fun checkIsPinCorrect(pin: Int): Boolean {
        return settingRepository.getIsPinCorrect(pin = pin)
    }

    suspend fun makeChallengeComplete(challengeId: Long) {
        challengeRepository.completeChallenge(id = challengeId)
    }

    suspend fun enableLock() {
        if (settingRepository.getIsPinSet()) {
            prefs.edit { putBoolean("isLocked", true) }
            isLockedFlow.value = true
        }
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
