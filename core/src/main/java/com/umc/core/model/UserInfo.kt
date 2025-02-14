package com.umc.core.model

data class UserInfo(
    val id: Long,
    val name: String,
    val loginType: LoginType,
    val email: String,
    val profileImageUrl: String?,
    val point: Long,
    val status: UserStatus,
    val totalDiaryCount: Int,
    // val gender: Gender,
    // val phoneNumber: String,
)