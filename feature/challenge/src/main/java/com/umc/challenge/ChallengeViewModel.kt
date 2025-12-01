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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val challengeRepository: ChallengeRepository,
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val pointState = mutableIntStateOf(0)
    private val ownedItemsMutableState = MutableStateFlow(listOf<OwnedItem>())
    private val unownedItemsMutableState = MutableStateFlow(listOf<UnownedItem>())
    private val failedChallengesState = mutableStateOf(listOf<FailedChallenge>())
    private val todayChallengesState = mutableStateOf(listOf<Challenge>())
    private val pastChallengesState = mutableStateOf(listOf<Challenge>())

    val equippedItemsState get() = itemRepository.equippedItemState
    val ownedItemsState: StateFlow<List<OwnedItem>> get() = ownedItemsMutableState
    val unownedItemsState: StateFlow<List<UnownedItem>> get() = unownedItemsMutableState
    val point get() = pointState.intValue
    val failedChallenges get() = failedChallengesState.value
    val todayChallenges get() = todayChallengesState.value
    val pastChallenges get() = pastChallengesState.value

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
                itemRepository.getEquippedItems()
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }


    fun getShopItems() {
        CoroutineScope(Dispatchers.IO).launch {
            itemRepository.getEquippedItems()
        }

        CoroutineScope(Dispatchers.IO).launch {
            val (point, ownedItems) = itemRepository.getOwnedItemsWithPoint()
            pointState.intValue = point
            ownedItemsMutableState.value = ownedItems
        }

        CoroutineScope(Dispatchers.IO).launch {
            val (point, unownedItems) = itemRepository.getUnownedItemsWithPoint()
            pointState.intValue = point
            unownedItemsMutableState.value = unownedItems
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
                ownedItemsMutableState.value = ownedItems
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

    suspend fun purchaseItem(id: Long) {
        itemRepository.purchaseItem(id)
        getShopItems()
    }

    suspend fun equipItem(id: Long, equip: Boolean) {
        if (equip) itemRepository.equipItem(id)
        else itemRepository.disrobeItem(id)
        getShopItems()
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

    fun getPastChallenges(
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                val challenges = challengeRepository.getPastChallenges()
                pastChallengesState.value = challenges
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }
}