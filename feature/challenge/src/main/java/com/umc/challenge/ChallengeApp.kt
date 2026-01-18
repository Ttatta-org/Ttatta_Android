package com.umc.challenge

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.util.fastAny
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.umc.challenge.modal.ChallengeCompletionDialogProp
import com.umc.challenge.modal.PointGrantedCardDialogProp
import com.umc.challenge.modal.PurchaseDialogProp
import com.umc.challenge.screen.ChallengeScreen
import com.umc.challenge.screen.ChallengeScreenTopBarProp
import com.umc.challenge.screen.PastChallengeScreen
import com.umc.challenge.screen.ShopItemItemProp
import com.umc.challenge.screen.ShopScreen
import com.umc.challenge.component.ChallengeItemProp
import com.umc.challenge.view.ChallengeOnboardingView
import com.umc.challenge.view.ChallengeOnboardingViewProp
import com.umc.challenge.component.ChallengeState
import com.umc.challenge.view.NewChallengeView
import com.umc.challenge.view.NewChallengeViewProp
import com.umc.core.util.runWithScope
import com.umc.design.character.Accessory
import com.umc.design.character.AccessorySet
import com.umc.design.character.BodyPart
import com.umc.design.component.CustomPopup
import com.umc.design.component.LoadingModal
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

data class ClickedUncompletedChallengeInfo(
    val id: Long,
)

data class ClickedShopItemInfo(
    val id: Long,
    val itemName: String,
)

data class PointGrantEvent(
    val point: Int,
    val onDismiss: () -> Unit,
)

@Composable
fun ChallengeApp(
    viewModel: ChallengeViewModel,
    pointGrantEvent: PointGrantEvent?,
    onNavigationBarVisibilityChanged: (Boolean) -> Unit,
    onChallengeCompletionRequired: (id: Long) -> Unit,
) {
    val navController = rememberNavController()

    val equippedItem by viewModel.equippedItemsState.collectAsState()
    val accessorySet = remember(equippedItem) {
        AccessorySet.create(
            equippedItem.mapNotNull {
                Accessory.entries.firstOrNull { accessory ->
                    accessory.code == it.item.code
                }
            },
        )
    }

    LaunchedEffect(key1 = Unit) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.route) {
                "challenge" -> onNavigationBarVisibilityChanged(true)
                else -> onNavigationBarVisibilityChanged(false)
            }
        }
    }

    NavHost(
        navController = navController, startDestination = "challenge"
    ) {
        composable("challenge") {
            var isLoading by remember { mutableStateOf(true) }
            var clickedUncompletedChallengeInfo by remember {
                mutableStateOf<ClickedUncompletedChallengeInfo?>(null)
            }

            LaunchedEffect(key1 = Unit) {
                viewModel.getTodayChallenges(onSucceed = { isLoading = false })
                viewModel.getEquippedItems()
                viewModel.getPoint()
            }

            ChallengeScreen(
                topBarProp = ChallengeScreenTopBarProp(
                    point = viewModel.point,
                    onShopIconClicked = { navController.navigate("shop") },
                ),
                challengeCompletionDialogProp = clickedUncompletedChallengeInfo?.let {
                    ChallengeCompletionDialogProp(
                        onDismissed = {
                            clickedUncompletedChallengeInfo = null
                        },
                        onConfirmed = {
                            onChallengeCompletionRequired(it.id)
                            clickedUncompletedChallengeInfo = null
                        },
                    )
                },
                pointGrantedCardDialogProp = pointGrantEvent?.let { event ->
                    PointGrantedCardDialogProp(
                        point = event.point,
                        onDismissed = event.onDismiss,
                        onGoToShopButtonClicked = {
                            event.onDismiss.invoke()
                            navController.navigate("shop")
                        },
                    )
                },
            ) { headerHeight ->
                val challengeScreenNavController = rememberNavController()

                LaunchedEffect(key1 = Unit) {
                    challengeScreenNavController.addOnDestinationChangedListener { _, destination, _ ->
                        when (destination.route) {
                            "onboarding" -> onNavigationBarVisibilityChanged(true)
                            "new_challenge" -> onNavigationBarVisibilityChanged(false)
                            else -> throw Exception("Unknown destination")
                        }
                    }
                }

                NavHost(
                    navController = challengeScreenNavController, startDestination = "onboarding"
                ) {
                    composable("onboarding") {
                        ChallengeOnboardingView(
                            prop = ChallengeOnboardingViewProp(
                                topPadding = headerHeight,
                                equippedAccessorySet = accessorySet,
                                isNewChallengeButtonEnabled = !isLoading && viewModel.todayChallenges.size < 3,
                                challengeItemPropList = viewModel.todayChallenges.map {
                                    ChallengeItemProp(
                                        title = it.title,
                                        content = it.content,
                                        state = if (it.isCompleted) ChallengeState.COMPLETED else ChallengeState.IN_PROGRESS,
                                        onClicked = {
                                            if (!it.isCompleted) clickedUncompletedChallengeInfo =
                                                ClickedUncompletedChallengeInfo(id = it.id)
                                        },
                                    )
                                },
                                onNewChallengeButtonClicked = {
                                    challengeScreenNavController.navigate("new_challenge")
                                },
                            )
                        )
                    }

                    composable("new_challenge") {
                        var title by remember { mutableStateOf("") }
                        var description by remember { mutableStateOf("") }

                        NewChallengeView(
                            prop = NewChallengeViewProp(
                                topPadding = headerHeight,
                                maxTitleLength = 20,
                                title = title,
                                description = description,
                                equippedAccessorySet = accessorySet,
                                isButtonEnabled = title.isNotBlank() && description.isNotBlank(),
                                onTitleChanged = { if (it.length <= 20) title = it },
                                onDescriptionChanged = { description = it },
                                onCreateButtonClicked = {
                                    viewModel.createChallenge(
                                        title = title,
                                        description = description,
                                        onSucceed = {
                                            MainScope().launch { challengeScreenNavController.popBackStack() }
                                        },
                                        onFailed = { /* 에러 처리 */ })
                                },
                                onPastChallengeClick = {
                                    navController.navigate("past_challenge")
                                },
                            )
                        )
                    }
                }
            }
        }

        composable("shop") {
            val equippedItem by viewModel.equippedItemsState.collectAsState()
            val ownedItems by viewModel.ownedItemsState.collectAsState()
            val unownedItems by viewModel.unownedItemsState.collectAsState()

            var clickedShopItemInfo: ClickedShopItemInfo? by remember { mutableStateOf(null) }
            var selectedBodyPart: BodyPart? by remember { mutableStateOf(null) }
            var showLackOfPointsPopup by remember { mutableStateOf(false) }
            var showLoading by remember { mutableStateOf(false) }

            val shopItems = remember(equippedItem, ownedItems, unownedItems) {
                val ownedShopItems = ownedItems.map { item ->
                    val isEquipped = equippedItem.fastAny { it.id == item.id }

                    ShopItemItemProp(
                        accessory = item.item,
                        cost = null,
                        isOwned = true,
                        isEquipped = isEquipped,
                        onClicked = {
                            viewModel.runWithScope {
                                runCatching { equipItem(id = item.id, equip = !isEquipped) }
                            }
                        },
                    )
                }

                val unownedShopItems = unownedItems.map { item ->
                    ShopItemItemProp(
                        accessory = item.item,
                        cost = item.cost,
                        isOwned = false,
                        isEquipped = false,
                        onClicked = {
                            if (viewModel.point >= item.cost) {
                                clickedShopItemInfo = ClickedShopItemInfo(
                                    id = item.id,
                                    itemName = item.item.title,
                                )
                            } else {
                                showLackOfPointsPopup = true
                            }
                        })
                }

                (ownedShopItems + unownedShopItems).sortedBy {
                    Accessory.entries.indexOf(it.accessory)
                }
            }

            val filteredShopItems = remember(shopItems, selectedBodyPart) {
                shopItems.filter {
                    selectedBodyPart == null || it.accessory.bodyPart == selectedBodyPart
                }
            }

            LaunchedEffect(key1 = Unit) {
                launch { runCatching { viewModel.getShopItems() } }
            }

            ShopScreen(
                point = viewModel.point,
                selectedBodyPart = selectedBodyPart,
                equippedAccessorySet = accessorySet,
                shopItemItemPropList = filteredShopItems,
                purchaseDialogProp = clickedShopItemInfo?.let { info ->
                    PurchaseDialogProp(
                        itemName = info.itemName,
                        onDismissed = { clickedShopItemInfo = null },
                        onPurchase = {
                            viewModel.runWithScope {
                                showLoading = true

                                runCatching { purchaseItem(id = info.id) }
                                clickedShopItemInfo = null

                                showLoading = false
                            }
                        },
                    )
                },
                onBodyPartSelected = { selectedBodyPart = it },
                onBackButtonClicked = {
                    MainScope().launch { navController.popBackStack() }
                },
            )

            if (showLackOfPointsPopup) CustomPopup(
                title = "포인트가 부족해요!",
                message = "챌린지를 완료하면 포인트를 모을 수 있어요.",
                cancelText = "네, 알겠어요.",
                onDismiss = { showLackOfPointsPopup = false })

            if (showLoading) LoadingModal()
        }

        composable("past_challenge") {
            // 화면 진입 시 지난 챌린지 조회
            LaunchedEffect(Unit) {
                viewModel.getPastChallenges()
            }

            PastChallengeScreen(
                pastChallenges = viewModel.pastChallenges,
                onRetryChallenge = { challenge ->
                    viewModel.createChallenge(
                        title = challenge.title,
                        description = challenge.content,
                        onSucceed = {
                            // 오늘 챌린지 목록은 createChallenge의 finally에서 getTodayChallenges()로 다시 불러옴
                            // 지난 챌린지 화면 닫고 challenge 화면으로 복귀
                            navController.navigate("challenge") {
                                popUpTo("challenge") {
                                    inclusive = true   // 기존 challenge까지 같이 제거
                                }
                                launchSingleTop = true    // 혹시나 중복 생기는 것 방지용
                            }
                        },
                        onFailed = {},
                    )
                },
                onBackButtonClicked = {
                    MainScope().launch { navController.popBackStack() }
                },
            )
        }
    }
}
