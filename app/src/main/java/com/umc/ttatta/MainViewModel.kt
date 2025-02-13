package com.umc.ttatta

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.core.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
): ViewModel() {

    private val isLoggedInMutable = MutableStateFlow<Boolean?>(null)
    private val userNameState = mutableStateOf("")

    val isLoggedIn: StateFlow<Boolean?> get() = isLoggedInMutable
    val userName get() = userNameState.value

    init {
        checkLogin()
    }

    fun checkLogin() {
        isLoggedInMutable.value = null

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

            isLoggedInMutable.value = isLoggedIn
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
}