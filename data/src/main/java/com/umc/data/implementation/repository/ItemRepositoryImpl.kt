package com.umc.data.implementation.repository

import com.umc.core.model.EquippedItem
import com.umc.core.model.OwnedItem
import com.umc.core.model.UnownedItem
import com.umc.core.repository.ItemRepository
import com.umc.data.api.ServerApi
import com.umc.data.api.withAuth
import com.umc.data.preference.AuthPreference
import com.umc.data.preference.ItemPreference
import com.umc.design.character.Accessory
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference,
    private val itemPreference: ItemPreference,
): ItemRepository {

    override suspend fun getUnownedItemsWithPoint(): Pair<Int, List<UnownedItem>> {
        val response = serverApi.withAuth(authPreference) { getShopItems() }
        return response.point!!.toInt() to (response.itemShopList?.map {
            UnownedItem(
                id = it.itemId!!,
                item = Accessory.entries.first { accessory ->
                    accessory.code == it.itemUniqueId!!
                },
                cost = it.cost!!.toInt(),
            )
        } ?: listOf())
    }

    override suspend fun getOwnedItemsWithPoint(): Pair<Int, List<OwnedItem>> {
        val response = serverApi.withAuth(authPreference) { getOwnedItems() }
        return response.point!!.toInt() to (response.myItemList?.map {
            OwnedItem(
                id = it.itemId!!,
                item = Accessory.entries.first { accessory ->
                    accessory.code == it.itemUniqueId!!
                },
                isEquipped = it.isEquipped!!
            )
        } ?: listOf())
    }

    override suspend fun getEquippedItems(): List<EquippedItem> {
        try {
            val response = serverApi.withAuth(authPreference) { getEquippedItems() }
            return response.idList?.map {
                EquippedItem(
                    id = it.itemId!!,
                    item = Accessory.entries.first { accessory ->
                        accessory.code == it.itemUniqueId!!
                    },
                )
            } ?: listOf()
        } catch (e: Exception) {
            return itemPreference.itemList
        }
    }

    override suspend fun purchaseItem(id: Long) {
        serverApi.withAuth(authPreference) { buyItem(id) }
    }

    override suspend fun equipItem(id: Long) {
        serverApi.withAuth(authPreference) { equipItem(id) }
        itemPreference.itemList = getEquippedItems()
    }

    override suspend fun disrobeItem(id: Long) {
        serverApi.withAuth(authPreference) { disrobeItem(id) }
    }
}