package com.umc.challenge

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.core.model.Challenge
import com.umc.core.model.FailedChallenge
import com.umc.core.model.OwnedItem
import com.umc.core.model.UnownedItem
import com.umc.core.repository.ChallengeRepository
import com.umc.core.repository.ItemRepository
import com.umc.design.character.AccessorySet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengeViewModel @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val itemRepository: ItemRepository
): ViewModel() {

    private val pointState = mutableIntStateOf(0)
    private val ownedItemsState = mutableStateOf(listOf<OwnedItem>())
    private val unownedItemsState = mutableStateOf(listOf<UnownedItem>())
    private val equippedAccessorySetState = mutableStateOf(AccessorySet.create())
    private val failedChallengesState = mutableStateOf(listOf<FailedChallenge>())
    private val todayChallengesState = mutableStateOf(listOf<Challenge>())

    val point get() = pointState.intValue
    val ownedItems get() = ownedItemsState.value
    val unownedItems get() = unownedItemsState.value
    val equippedAccessorySet get() = equippedAccessorySetState.value
    val failedChallenges get() = failedChallengesState.value
    val todayChallenges get() = todayChallengesState.value

    init {
        getItemInfos(
            onSucceed = {},
            onFailed = {}
        )
        getChallengeInfos(
            onSucceed = {},
            onFailed = {}
        )
    }

    private fun getItemInfos(
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val (point, unownedItems) = itemRepository.getUnownedItemsWithPoint()
                val (_, ownedItems) = itemRepository.getOwnedItemsWithPoint()
                val equipped = itemRepository.getEquippedItems()

                pointState.intValue = point
                unownedItemsState.value = unownedItems
                ownedItemsState.value = ownedItems
                equippedAccessorySetState.value = AccessorySet.create(equipped.map { it.item })

                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    private fun getChallengeInfos(
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val todayChallenges = challengeRepository.getChallenges()
                val failedChallenges = challengeRepository.getFailedChallenges()

                todayChallengesState.value = todayChallenges
                failedChallengesState.value = failedChallenges

                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }
    
    fun purchaseItem(
        id: Long,
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                itemRepository.purchaseItem(id)
                getItemInfos(
                    onSucceed = onSucceed,
                    onFailed = { throw it }
                )
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun equipItem(
        id: Long,
        isEquipping: Boolean,
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (isEquipping) itemRepository.equipItem(id)
                else itemRepository.disrobeItem(id)
                getItemInfos(
                    onSucceed = onSucceed,
                    onFailed = { throw it }
                )
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun createChallenge(
        title: String,
        description: String,
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                challengeRepository.createChallenge(title, description)
                getChallengeInfos(
                    onSucceed = onSucceed,
                    onFailed = { throw it }
                )
            } catch (e: Exception) {
                onFailed(e)
            }   
        }
    }
}