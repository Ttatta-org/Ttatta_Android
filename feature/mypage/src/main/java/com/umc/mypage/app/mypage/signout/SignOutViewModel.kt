package com.umc.mypage.app.mypage.signout

import com.umc.core.repository.UserRepository
import com.umc.mypage.util.LoadingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignOutViewModel @Inject constructor(
    private val userRepository: UserRepository
) : LoadingViewModel() {

    suspend fun leaveUser(reason: String) = runWithLoading {
        userRepository.leaveUser(reason)
    }
}
