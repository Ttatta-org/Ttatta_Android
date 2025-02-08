package com.umc.data.implementation.repository

import androidx.compose.runtime.Composable
import com.umc.core.model.OwnedItem
import com.umc.core.model.UnownedItem
import com.umc.core.repository.TtottuRepository
import com.umc.data.api.ServerApi
import com.umc.data.preference.AuthPreference
import javax.inject.Inject

class TtottuRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference,
): TtottuRepository {

    override suspend fun getTtottuComposable(): @Composable () -> Unit {
        TODO("Not yet implemented")
    }

    override suspend fun getUnownedItems(): List<UnownedItem> {
        TODO("Not yet implemented")
    }

    override suspend fun getOwnedItems(): List<OwnedItem> {
        TODO("Not yet implemented")
    }

    override suspend fun getEquippedItemIds(): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun purchaseItem(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun equipItem(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun disrobeItem(id: Long) {
        TODO("Not yet implemented")
    }
}