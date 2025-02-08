package com.umc.core.model

data class Challenge(
    val id: Long,
    val title: String,
    val content: String,
    val isCompleted: Boolean,
)
