package com.umc.mypage.app.mypage.updateprofile

import com.umc.core.repository.UserRepository
import com.umc.mypage.util.LoadingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class UpdateProfileNicknameViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : LoadingViewModel() {
    private val _newNickname = MutableStateFlow("")
    val newNickname: StateFlow<String> = _newNickname

    private var isInitialized = false

    suspend fun loadInitialNicknameIfNeeded() = runWithLoading {
        if (isInitialized) return@runWithLoading

        _newNickname.value = userRepository.getUserInfo().name
        isInitialized = true
    }

    fun onNicknameChanged(nickname: String) {
        _newNickname.value = nickname
    }

    suspend fun updateNickname() = runWithLoading {
        userRepository.modifyUserInfo(name = _newNickname.value)
    }
}
