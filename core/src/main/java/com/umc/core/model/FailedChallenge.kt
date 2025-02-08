package com.umc.core.model

data class FailedChallenge(
    val id: Long,
    val title: String,
    val content: String,
    val deadline: Int,
)
