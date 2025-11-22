package com.umc.data.implementation.repository

import com.umc.core.model.EquippedItem
import com.umc.core.model.OwnedItem
import com.umc.core.model.UnownedItem
import com.umc.core.repository.ItemRepository
import com.umc.data.api.ServerApi
import com.umc.data.preference.AuthPreference
import com.umc.data.preference.ItemPreference
import com.umc.data.util.withAuth
import com.umc.design.character.Accessory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference,
    private val itemPreference: ItemPreference,
) : ItemRepository {

    override suspend fun getUnownedItemsWithPoint(): Pair<Int, List<UnownedItem>> {
        val response = serverApi.withAuth(authPreference) { getShopItems() }
        return response.point!!.toInt() to (response.itemShopList?.mapNotNull {
            Accessory.entries
                .firstOrNull { accessory ->
                    accessory.code == it.itemUniqueId!!
                }
                ?.let { accessory ->
                    UnownedItem(
                        id = it.itemId!!,
                        item = accessory,
                        cost = it.cost!!.toInt(),
                    )
                }
        } ?: listOf())
    }

    override suspend fun getOwnedItemsWithPoint(): Pair<Int, List<OwnedItem>> {
        val response = serverApi.withAuth(authPreference) { getOwnedItems() }
        return response.point!!.toInt() to (response.myItemList?.mapNotNull {
            Accessory.entries
                .firstOrNull { accessory ->
                    accessory.code == it.itemUniqueId!!
                }
                ?.let { accessory ->
                    OwnedItem(
                        id = it.itemId!!,
                        item = accessory,
                        isEquipped = it.isEquipped!!
                    )
                }
        } ?: listOf())
    }

    override suspend fun getEquippedItems(): List<EquippedItem> {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                val response = serverApi.withAuth(authPreference) { getEquippedItems() }

                itemPreference.itemList = response.idList?.mapNotNull {
                    Accessory.entries
                        .firstOrNull { accessory ->
                            accessory.code == it.itemUniqueId!!
                        }
                        ?.let { accessory ->
                            EquippedItem(
                                id = it.itemId!!,
                                item = accessory,
                            )
                        }
                } ?: listOf()
            }
        }

        return itemPreference.itemList
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