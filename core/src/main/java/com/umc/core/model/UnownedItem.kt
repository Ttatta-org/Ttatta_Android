package com.umc.core.model

import com.umc.design.character.Accessory

data class UnownedItem(
    val id: Long,
    val item: Accessory,
    val cost: Int,
)
