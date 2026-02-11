package com.umc.mypage.app.mypage.updateprofile

import com.umc.core.model.EmailRequestResult
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
        val userInfo = userRepository.getUserInfo()
        _userInfo.value = userInfo
    }

    suspend fun updateNickname(nickname: String) = runWithLoading {
        userRepository.modifyUserInfo(name = nickname)
    }

    suspend fun requestVerificationCodeForUpdateEmail(email: String): EmailRequestResult = runWithLoading {
        val result = userRepository.requestVerificationCodeForChangeEmail(email = email)
        result
    }

    suspend fun verifyCodeForUpdateEmail(email: String, code: String): Boolean = runWithLoading {
        val result = userRepository.changeEmailWithVerificationCode(email = email, code = code.toInt())
        result
    }
}
