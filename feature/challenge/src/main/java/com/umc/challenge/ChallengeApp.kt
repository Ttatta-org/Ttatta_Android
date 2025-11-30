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
import com.umc.challenge.screen.PastChallengeScreen
import com.umc.challenge.screen.PastChallengeScreenTopBarProp
import com.umc.challenge.screen.ShopItemItemProp
import com.umc.challenge.screen.ShopScreen
import com.umc.challenge.view.ChallengeItemProp
import com.umc.challenge.view.ChallengeOnboardingView
import com.umc.challenge.view.ChallengeOnboardingViewProp
import com.umc.challenge.view.ChallengeState
import com.umc.challenge.view.NewChallengeView
import com.umc.challenge.view.NewChallengeViewProp
import com.umc.core.util.runWithScope
import com.umc.design.character.Accessory
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

data class ClickedOwnedItemInfo(
    val id: Long,
    val item: Accessory,
    val isEquipped: Boolean,
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
                    onMyItemsIconClicked = { navController.navigate("my_item") }
                ),
                challengeCompletionDialogProp = clickedUncompletedChallengeInfo?.let {
                    ChallengeCompletionDialogProp(
                        onDismissed = { clickedUncompletedChallengeInfo = null },
                        onConfirmed = {
                            onChallengeCompletionRequired(it.id)
                            clickedUncompletedChallengeInfo = null
                        }
                    )
                },
                pointGrantedCardDialogProp = pointGrantEvent?.let { event ->
                    PointGrantedCardDialogProp(
                        point = event.point,
                        onDismissed = event.onDismiss,
                        onGoToShopButtonClicked = {
                            event.onDismiss.invoke()
                            navController.navigate("shop")
                        }
                    )
                },
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
                                isNewChallengeButtonEnabled = !isLoading && viewModel.todayChallenges.size < 3,
                                challengeItemPropList = viewModel.todayChallenges.map {
                                    ChallengeItemProp(
                                        title = it.title,
                                        content = it.content,
                                        state = if (it.isCompleted)
                                            ChallengeState.COMPLETED
                                        else
                                            ChallengeState.IN_PROGRESS,
                                        onClicked = {
                                            if (!it.isCompleted) clickedUncompletedChallengeInfo =
                                                ClickedUncompletedChallengeInfo(id = it.id)
                                        }
                                    )
                                },
                                onNewChallengeButtonClicked = {
                                    challengeScreenNavController.navigate("new_challenge")
                                }
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
                                equippedAccessorySet = viewModel.equippedAccessorySet,
                                onTitleChanged = { if (it.length <= 20) title = it },
                                onDescriptionChanged = { description = it },
                                onCreateButtonClicked = {
                                    viewModel.createChallenge(
                                        title = title,
                                        description = description,
                                        onSucceed = {
                                            MainScope().launch { challengeScreenNavController.popBackStack() }
                                        },
                                        onFailed = { /* 에러 처리 */ }
                                    )
                                },
                                onPastChallengeClick = {
                                    navController.navigate("past_challenge")
                                }
                            )
                        )
                    }
                }
            }
        }

        composable("shop") {
            var clickedShopItemInfo: ClickedShopItemInfo? by remember { mutableStateOf(null) }
            var selectedBodyPart: BodyPart? by remember { mutableStateOf(null) }
            var showLackOfPointsPopup by remember { mutableStateOf(false) }
            var showLoading by remember { mutableStateOf(false) }

            LaunchedEffect(key1 = Unit) {
                launch { runCatching { viewModel.getEquippedItems() } }
                launch { runCatching { viewModel.getShopItems() } }
            }

            ShopScreen(
                point = viewModel.point,
                selectedBodyPart = selectedBodyPart,
                equippedAccessorySet = viewModel.equippedAccessorySet,
                shopItemItemPropList = viewModel.unownedItems
                    .filter {
                        selectedBodyPart == null || it.item.bodyPart == selectedBodyPart
                    }
                    .map {
                        ShopItemItemProp(
                            accessory = it.item,
                            cost = it.cost,
                            isOwned = false,  // TODO: 백엔드 반영 시 변경
                            onClicked = {
                                if (it.cost <= viewModel.point) {
                                    clickedShopItemInfo = ClickedShopItemInfo(
                                        id = it.id,
                                        itemName = it.item.title,
                                    )
                                } else {
                                    showLackOfPointsPopup = true
                                }
                            }
                        )
                    },
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
                        }
                    )
                },
                onMyItemsIconClicked = {
                    MainScope().launch {
                        navController.navigate("my_item") {
                            popUpTo(id = navController.graph.startDestinationId) {
                                inclusive = false
                            }
                        }
                    }
                },
                onBodyPartSelected = { selectedBodyPart = it },
                onBackButtonClicked = {
                    MainScope().launch { navController.popBackStack() }
                }
            )

            if (showLackOfPointsPopup) CustomPopup(
                title = "포인트가 부족해요!",
                message = "챌린지를 완료하면 포인트를 모을 수 있어요.",
                cancelText = "네, 알겠어요.",
                onDismiss = { showLackOfPointsPopup = false }
            )

            if (showLoading) LoadingModal()
        }

        composable("my_item") {
            var clickedOwnedItemInfo by remember { mutableStateOf<ClickedOwnedItemInfo?>(null) }

            LaunchedEffect(key1 = Unit) {
                viewModel.getEquippedItems()
                viewModel.getOwnedItems()
            }

            MyItemScreen(
                point = viewModel.point,
                equippedAccessorySet = viewModel.equippedAccessorySet,
                myItemItemItemPropList = remember(viewModel.ownedItems) {
                    viewModel.ownedItems.map {
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
                    }
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

        composable("past_challenge") {
            // 화면 진입 시 지난 챌린지 조회
            LaunchedEffect(Unit) {
                viewModel.getPastChallenges()
            }

            PastChallengeScreen(
                topBarProp = PastChallengeScreenTopBarProp(
                    onHeightChanged = { /* 필요 없으면 무시해도 됨 */ },
                    onBackIconClicked = {
                        // 뒤로가기 → challenge 화면으로 복귀
                        navController.popBackStack()
                    }
                ),
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
                        onFailed = {}
                    )
                }
            )
        }
    }
}
