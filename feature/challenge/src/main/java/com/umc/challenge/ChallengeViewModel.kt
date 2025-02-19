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
import com.umc.core.repository.UserRepository
import com.umc.design.character.AccessorySet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengeViewModel @Inject constructor(
    private val userRepository: UserRepository,
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

    fun getPoint(
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                pointState.intValue = userRepository.getUserInfo().point.toInt()
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun getEquippedItems(
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val equipped = itemRepository.getEquippedItems()
                equippedAccessorySetState.value = AccessorySet.create(equipped.map { it.item })
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun getShopItems(
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val (point, unownedItems) = itemRepository.getUnownedItemsWithPoint()
                pointState.intValue = point
                unownedItemsState.value = unownedItems
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun getOwnedItems(
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val (point, ownedItems) = itemRepository.getOwnedItemsWithPoint()
                pointState.intValue = point
                ownedItemsState.value = ownedItems
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun getTodayChallenges(
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val todayChallenges = challengeRepository.getChallenges()
                todayChallengesState.value = todayChallenges
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun getFailedChallenges(
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val failedChallenges = challengeRepository.getFailedChallenges()
                failedChallengesState.value = failedChallenges
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun purchaseItem(
        id: Long,
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                itemRepository.purchaseItem(id)
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            } finally {
                getEquippedItems()
                getShopItems()
            }
        }
    }

    fun equipItem(
        id: Long,
        isEquipping: Boolean,
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                if (isEquipping) itemRepository.equipItem(id)
                else itemRepository.disrobeItem(id)
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            } finally {
                getOwnedItems()
                getEquippedItems()
            }
        }
    }

    fun createChallenge(
        title: String,
        description: String,
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                challengeRepository.createChallenge(title, description)
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            } finally {
                getTodayChallenges()
            }
        }
    }
}