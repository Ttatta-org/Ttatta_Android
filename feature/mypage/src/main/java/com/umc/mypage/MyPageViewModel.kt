package com.umc.mypage

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.umc.mypage.R

data class MyPageUiState(
    val name: String = "서연",
    val profileImage: Int = R.drawable.default_profile,
    val diaryCount: Int = 129,
    val points: Int = 1300,
    val notificationsEnabled: Boolean = false,
    val passwordLockEnabled: Boolean = true
) {
    val displayName: String
        get() = if (name.endsWith("님")) name else "$name 님"
}

open class MyPageViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(MyPageUiState())
    open val uiState: StateFlow<MyPageUiState> = _uiState
}