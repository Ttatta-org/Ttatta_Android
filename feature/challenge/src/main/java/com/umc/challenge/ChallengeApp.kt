package com.umc.challenge

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.umc.challenge.component.ChallengeCompletionDialogProp
import com.umc.challenge.component.PointGrantedCardDialogProp
import com.umc.challenge.component.PurchaseDialogProp
import com.umc.challenge.screen.ChallengeScreen
import com.umc.challenge.screen.ChallengeScreenTopBarProp
import com.umc.challenge.screen.ClickedItemProp
import com.umc.challenge.screen.MyItemItemItemProp
import com.umc.challenge.screen.MyItemScreen
import com.umc.challenge.screen.ShopItemItemProp
import com.umc.challenge.screen.ShopScreen
import com.umc.challenge.view.ChallengeItemProp
import com.umc.challenge.view.ChallengeOnboardingView
import com.umc.challenge.view.ChallengeOnboardingViewProp
import com.umc.challenge.view.ChallengeState
import com.umc.challenge.view.FailedChallengeItemProp
import com.umc.challenge.view.NewChallengeView
import com.umc.challenge.view.NewChallengeViewProp
import com.umc.design.character.Accessory

data class ClickedUncompletedChallengeInfo(
    val id: Long,
)

data class ClickedShopItemInfo(
    val id: Long,
    val itemName: String,
)

data class ClickedOwnedItemInfo(
    val id: Long,
    val item: Accessory,
    val isEquipped: Boolean,
)

@Composable
fun ChallengeApp(
    viewModel: ChallengeViewModel,
    onNavigationBarVisibilityChanged: (Boolean) -> Unit,
) {
    val navController = rememberNavController()

    LaunchedEffect(key1 = Unit) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.route) {
                "challenge" -> onNavigationBarVisibilityChanged(true)
                else -> onNavigationBarVisibilityChanged(false)
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "challenge"
    ) {
        composable("challenge") {
            var showPointGrantedCardDialog by remember { mutableStateOf(false) }
            var clickedUncompletedChallengeInfo by remember { mutableStateOf<ClickedUncompletedChallengeInfo?>(null) }

            ChallengeScreen(
                topBarProp = ChallengeScreenTopBarProp(
                    point = viewModel.point,
                    onShopIconClicked = { navController.navigate("shop") },
                    onMyItemsIconClicked = { navController.navigate("my_item") }
                ),
                challengeCompletionDialogProp = clickedUncompletedChallengeInfo?.let {
                    ChallengeCompletionDialogProp(
                        onDismissed = { clickedUncompletedChallengeInfo = null },
                        onConfirmed = { /* TODO */ }
                    )
                },
                pointGrantedCardDialogProp = if (showPointGrantedCardDialog) PointGrantedCardDialogProp(
                    onDismissed = { showPointGrantedCardDialog = false },
                ) else null,
            ) {
                val challengeScreenNavController = rememberNavController()

                LaunchedEffect(key1 = Unit) {
                    challengeScreenNavController.addOnDestinationChangedListener { _, destination, _ ->
                        when (destination.route) {
                            "onboarding" -> onNavigationBarVisibilityChanged(true)
                            else -> onNavigationBarVisibilityChanged(false)
                        }
                    }
                }

                NavHost(
                    navController = challengeScreenNavController,
                    startDestination = "onboarding"
                ) {
                    composable("onboarding") {
                        ChallengeOnboardingView(
                            prop = ChallengeOnboardingViewProp(
                                equippedAccessorySet = viewModel.equippedAccessorySet,
                                challengeItemPropList = viewModel.todayChallenges.map {
                                    ChallengeItemProp(
                                        title = it.title,
                                        state = if (it.isCompleted)
                                            ChallengeState.COMPLETED
                                        else
                                            ChallengeState.IN_PROGRESS,
                                        onClicked = { clickedUncompletedChallengeInfo = ClickedUncompletedChallengeInfo(id = it.id) }
                                    )
                                },
                                onNewChallengeButtonClicked = { navController.navigate("new_challenge") }
                            )
                        )
                    }

                    composable("new_challenge") {
                        var title by remember { mutableStateOf("") }
                        var description by remember { mutableStateOf("") }

                        NewChallengeView(
                            prop = NewChallengeViewProp(
                                maxTitleLength = 20,
                                title = title,
                                description = description,
                                failedChallengeItemPropList = viewModel.failedChallenges.map {
                                    FailedChallengeItemProp(
                                        elapsedDays = it.deadline,
                                        title = it.title,
                                        description = it.content,
                                        onClick = { /* TODO */ }
                                    )
                                },
                                onTitleChanged = { if (it.length <= 20) title = it },
                                onDescriptionChanged = { description = it },
                                onCreateButtonClicked = {
                                    viewModel.createChallenge(
                                        title = title,
                                        description = description,
                                        onSucceed = { navController.navigate("challenge") },
                                        onFailed = { /* TODO */ }
                                    )
                                }
                            )
                        )
                    }
                }
            }
        }
        
        composable("shop") {
            var clickedShopItemInfo by remember { mutableStateOf<ClickedShopItemInfo?>(null) }

            ShopScreen(
                point = viewModel.point,
                equippedAccessorySet = viewModel.equippedAccessorySet,
                shopItemItemPropList = viewModel.unownedItems.map {
                    ShopItemItemProp(
                        accessory = it.item,
                        cost = it.cost,
                        onClicked = {
                            clickedShopItemInfo = ClickedShopItemInfo(
                                id = it.id,
                                itemName = it.item.title
                            )
                        }
                    )
                },
                purchaseDialogProp = clickedShopItemInfo?.let { info ->
                    PurchaseDialogProp(
                        itemName = info.itemName,
                        onDismissed = { clickedShopItemInfo = null },
                        onPurchase = {
                            viewModel.purchaseItem(
                                id = info.id,
                                onSucceed = { clickedShopItemInfo = null },
                                onFailed = { clickedShopItemInfo = null }
                            )
                        }
                    )
                },
                onMyItemsIconClicked = {
                    navController.navigate("my_item") {
                        popUpTo(id = navController.graph.startDestinationId) {
                            inclusive = false
                        }
                    }
                }
            )
        }
        
        composable("my_item") {
            var clickedOwnedItemInfo by remember { mutableStateOf<ClickedOwnedItemInfo?>(null) }

            MyItemScreen(
                point = viewModel.point,
                equippedAccessorySet = viewModel.equippedAccessorySet,
                myItemItemItemPropList = viewModel.ownedItems.map {
                    MyItemItemItemProp(
                        accessory = it.item,
                        isEquipped = it.isEquipped,
                        onClicked = { 
                            clickedOwnedItemInfo = ClickedOwnedItemInfo(
                                id = it.id,
                                item = it.item,
                                isEquipped = it.isEquipped
                            ) 
                        }
                    )
                },
                clickedItemProp = clickedOwnedItemInfo?.let { info ->
                    ClickedItemProp(
                        item = info.item,
                        isEquipped = info.isEquipped,
                        onBackPressed = { clickedOwnedItemInfo = null },
                        onConfirmed = {
                            viewModel.equipItem(
                                id = info.id,
                                isEquipping = !info.isEquipped,
                                onSucceed = { clickedOwnedItemInfo = null },
                                onFailed = { clickedOwnedItemInfo = null }
                            )
                        }
                    )
                },
                onShopIconClicked = {
                    navController.navigate("shop") {
                        popUpTo(id = navController.graph.startDestinationId) {
                            inclusive = false
                        }
                    }
                }
            )
        }
    }
}
