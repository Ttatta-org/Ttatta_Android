package com.umc.ttatta

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val itemRepository: ItemRepository,
    private val challengeRepository: ChallengeRepository,
    private val settingRepository: SettingRepository,
): ViewModel() {

    private val isLoggedInFlow = MutableStateFlow<Boolean?>(null)
    private val userNameState = mutableStateOf("")
    private val equippedAccessoriesState = mutableStateOf(AccessorySet.create())

    val isLoggedInState: StateFlow<Boolean?> get() = isLoggedInFlow
    val userName get() = userNameState.value
    val equippedAccessories get() = equippedAccessoriesState.value

    init {
        checkLogin()
    }

    fun checkLogin() {
        isLoggedInFlow.value = null

        viewModelScope.launch {
            val isLoggedIn = runCatching {
                userRepository.isAlreadyLogin()
            }.getOrDefault(defaultValue = false)

            if (isLoggedIn) {
                runCatching { getUserName() }
                runCatching { getEquippedAccessories() }
                runCatching { getNewFcmToken() }
            }

            isLoggedInFlow.value = isLoggedIn
        }
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

    private fun getNewFcmToken() {
        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener { task ->
            if (!task.isSuccessful) return@addOnCompleteListener
            FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                if (!tokenTask.isSuccessful) return@addOnCompleteListener
                val newToken = tokenTask.result
                Log.d("MainViewModel", "New FCM token: $newToken")
                viewModelScope.launch {
                    runCatching { settingRepository.sendFcmToken(token = newToken) }
                }
            }
        }
    }

    fun makeChallengeComplete(
        challengeId: Long,
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                challengeRepository.completeChallenge(id = challengeId)
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }
}
