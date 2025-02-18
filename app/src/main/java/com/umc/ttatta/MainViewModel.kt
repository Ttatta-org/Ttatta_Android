package com.umc.ttatta

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.core.repository.ChallengeRepository
import com.umc.core.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val challengeRepository: ChallengeRepository,
): ViewModel() {

    private val isLoggedInFlow = MutableStateFlow<Boolean?>(null)
    private val userNameState = mutableStateOf("")

    val isLoggedInState: StateFlow<Boolean?> get() = isLoggedInFlow
    val userName get() = userNameState.value

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

            if (isLoggedIn) getUserName(
                onSucceed = { /* TODO */ },
                onFailed = { /* TODO */ }
            )
        }
    }

    private fun getUserName(
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit,
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

    fun makeChallengeComplete(
        challengeId: Long,
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit
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