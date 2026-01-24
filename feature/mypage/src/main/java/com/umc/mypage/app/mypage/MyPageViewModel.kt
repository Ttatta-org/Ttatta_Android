package com.umc.mypage.app.mypage

import com.umc.core.model.UserInfo
import com.umc.core.repository.UserRepository
import com.umc.mypage.util.LoadingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
open class MyPageViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : LoadingViewModel() {

    private val _userInfoState = MutableStateFlow<UserInfo?>(null)
    val userInfoState: StateFlow<UserInfo?> = _userInfoState

    suspend fun loadUserInfo() = runWithLoading {
        val userInfo = userRepository.getUserInfo()
        _userInfoState.value = userInfo
    }

    suspend fun logout() = runWithLoading {
        userRepository.logout()
    }
}
