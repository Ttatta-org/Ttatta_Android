package com.umc.challenge.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.umc.challenge.component.ChallengeCompletionDialog
import com.umc.challenge.component.ChallengeCompletionDialogProp
import com.umc.challenge.component.ChallengeTopBar
import com.umc.challenge.component.ChallengeTopBarProp
import com.umc.challenge.component.PointGrantedCardDialog
import com.umc.challenge.component.PointGrantedCardDialogProp
import com.umc.challenge.component.previewChallengeTopBarProp
import com.umc.challenge.view.ChallengeOnboardingView
import com.umc.challenge.view.ChallengeState
import com.umc.challenge.view.NewChallengeView
import com.umc.challenge.view.previewChallengeOnboardingViewProp
import com.umc.challenge.view.previewNewChallengeViewProp
import com.umc.design.Secondary100

data class ChallengeScreenTopBarProp(
    val point: Int,
    val onShopIconClicked: () -> Unit,
    val onMyItemsIconClicked: () -> Unit,
)

@Composable
fun ChallengeScreen(
    topBarProp: ChallengeScreenTopBarProp,
    challengeCompletionDialogProp: ChallengeCompletionDialogProp?,
    pointGrantedCardDialogProp: PointGrantedCardDialogProp?,
    view: @Composable () -> Unit,
) {
    var topBarHeight by remember { mutableStateOf(0.dp) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Secondary100)
    ) {
        // 내용
        Column {
            Spacer(modifier = Modifier.height(topBarHeight))
            view()
        }
        // 탑 바
        ChallengeTopBar(
            prop = ChallengeTopBarProp(
                point = topBarProp.point,
                onHeightChanged = { topBarHeight = it },
                onShopIconClicked = topBarProp.onShopIconClicked,
                onMyItemsIconClicked = topBarProp.onMyItemsIconClicked
            )
        )
    }

    challengeCompletionDialogProp?.let { ChallengeCompletionDialog(prop = it) }
    pointGrantedCardDialogProp?.let { PointGrantedCardDialog(prop = it) }
}

val previewChallengeScreenTopBarProp = ChallengeScreenTopBarProp(
    point = previewChallengeTopBarProp.point,
    onShopIconClicked = previewChallengeTopBarProp.onShopIconClicked,
    onMyItemsIconClicked = previewChallengeTopBarProp.onMyItemsIconClicked
)

@Preview(showBackground = true)
@Composable
fun PreviewChallengeScreen() {
    val navigator = rememberNavController()
    var showChallengeCompletionDialog by remember { mutableStateOf(false) }

    ChallengeScreen(
        topBarProp = previewChallengeScreenTopBarProp,
        challengeCompletionDialogProp = if (showChallengeCompletionDialog) ChallengeCompletionDialogProp(
            onDismissed = { showChallengeCompletionDialog = false },
            onConfirmed = { showChallengeCompletionDialog = false },
        ) else null,
        pointGrantedCardDialogProp = null,
    ) {
        NavHost(
            navController = navigator,
            startDestination = "onboarding"
        ) {
            composable("onboarding") {
                ChallengeOnboardingView(
                    prop = previewChallengeOnboardingViewProp.let {
                        it.copy(
                            onNewChallengeButtonClicked = { navigator.navigate("new_challenge") },
                            challengeItemPropList = it.challengeItemPropList.map { prop ->
                                if (prop.state == ChallengeState.IN_PROGRESS) {
                                    prop.copy(onClicked = { showChallengeCompletionDialog = true })
                                } else prop
                            }
                        )
                    }
                )
            }

            composable("new_challenge") {
                NewChallengeView(
                    prop = previewNewChallengeViewProp.copy(
                        onCreateButtonClicked = { navigator.navigate("onboarding") }
                    )
                )
            }
        }
    }
}