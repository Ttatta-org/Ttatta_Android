package com.umc.mypage.app.mypage.updateprofile

import com.umc.core.model.UserInfo
import com.umc.core.repository.UserRepository
import com.umc.mypage.util.LoadingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class UpdateProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : LoadingViewModel() {
    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo

    suspend fun loadUserInfo() = runWithLoading {
        _userInfo.value = userRepository.getUserInfo()
    }
}
