package com.umc.core.model

enum class LoginType {
    REGULAR,
    KAKAO,
}

enum class UserStatus {
    ACTIVE,
    INACTIVE,
}

enum class Gender {
    MALE,
    FEMALE,
}

data class UserInfo(
    val id: Long,
    val name: String,
    val loginType: LoginType,
    val email: String,
    val profileImageUrl: String?,
    val point: Long,
    val status: UserStatus,
    // val gender: Gender,
    // val phoneNumber: String,
)