package com.umc.core.model

import com.umc.design.character.Accessory

data class OwnedItem(
    val id: Long,
    val item: Accessory,
    val isEquipped: Boolean,
)