package com.umc.data.implementation.repository

import com.umc.core.model.EquippedItem
import com.umc.core.model.OwnedItem
import com.umc.core.model.UnownedItem
import com.umc.core.repository.ItemRepository
import com.umc.data.api.ServerApi
import com.umc.data.preference.AuthPreference
import com.umc.data.preference.ItemPreference
import com.umc.data.util.AuthenticatedRepository
import com.umc.design.character.Accessory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    override val authPreference: AuthPreference,
    private val serverApi: ServerApi,
    private val itemPreference: ItemPreference,
) : ItemRepository, AuthenticatedRepository {

    private val equippedItemMutableState = MutableStateFlow(itemPreference.itemList)

    override val equippedItemState: StateFlow<List<EquippedItem>>
        get() = equippedItemMutableState

    override suspend fun getUnownedItemsWithPoint(): Pair<Int, List<UnownedItem>> {
        val response = serverApi.withAuth { getShopItems() }

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
        val response = serverApi.withAuth { getOwnedItems() }

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
        val response = serverApi.withAuth { getEquippedItems() }

        val result = response.idList?.mapNotNull {
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

        itemPreference.itemList = result
        equippedItemMutableState.value = result

        return result
    }

    override suspend fun purchaseItem(id: Long) {
        serverApi.withAuth { buyItem(id) }
    }

    override suspend fun equipItem(id: Long) {
        serverApi.withAuth { equipItem(id) }
        getEquippedItems()
    }

    override suspend fun disrobeItem(id: Long) {
        serverApi.withAuth { disrobeItem(id) }
        getEquippedItems()
    }
}