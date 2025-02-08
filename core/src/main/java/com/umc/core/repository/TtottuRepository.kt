package com.umc.core.repository

import androidx.compose.runtime.Composable
import com.umc.core.model.OwnedItem
import com.umc.core.model.UnownedItem

interface TtottuRepository {
    suspend fun getTtottuComposable(): @Composable () -> Unit

    suspend fun getUnownedItems(): List<UnownedItem>
    suspend fun getOwnedItems(): List<OwnedItem>
    suspend fun getEquippedItemIds(): List<Long>

    suspend fun purchaseItem(id: Long)
    suspend fun equipItem(id: Long)
    suspend fun disrobeItem(id: Long)
}