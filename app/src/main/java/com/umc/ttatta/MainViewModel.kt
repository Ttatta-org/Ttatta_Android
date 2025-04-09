package com.umc.ttatta

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.core.repository.ChallengeRepository
import com.umc.core.repository.ItemRepository
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
            val isLoggedIn = try {
                userRepository.isAlreadyLogin()
            } catch (_: Exception) {
                false
            }

            if (isLoggedIn) {
                getUserName()
                getEquippedAccessories()
            }

            isLoggedInFlow.value = isLoggedIn
        }
    }

    private fun getUserName(
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                userNameState.value = userRepository.getUserInfo().name
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    private fun getEquippedAccessories(
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                val equippedItems = itemRepository.getEquippedItems()
                equippedAccessoriesState.value = AccessorySet.create(
                    equippedItems.map { item -> item.item }
                )
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
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