package com.umc.core.repository

import com.umc.core.model.EquippedItem
import com.umc.core.model.OwnedItem
import com.umc.core.model.UnownedItem

interface ItemRepository {
    suspend fun getUnownedItemsWithPoint(): Pair<Int, List<UnownedItem>> // (보유 포인트, 아이템 리스트)
    suspend fun getOwnedItemsWithPoint(): Pair<Int, List<OwnedItem>> // (보유 포인트, 아이템 리스트)
    suspend fun getEquippedItems(): List<EquippedItem>

    suspend fun purchaseItem(id: Long)
    suspend fun equipItem(id: Long)
    suspend fun disrobeItem(id: Long)
}