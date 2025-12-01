package com.umc.challenge.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.umc.challenge.R
import com.umc.challenge.component.ChallengeCompletionDialog
import com.umc.challenge.component.ChallengeCompletionDialogProp
import com.umc.challenge.component.PointChip
import com.umc.challenge.component.PointGrantedCardDialog
import com.umc.challenge.component.PointGrantedCardDialogProp
import com.umc.challenge.component.previewChallengeTopBarProp
import com.umc.challenge.view.ChallengeOnboardingView
import com.umc.challenge.view.ChallengeState
import com.umc.challenge.view.NewChallengeView
import com.umc.challenge.view.previewChallengeOnboardingViewProp
import com.umc.challenge.view.previewNewChallengeViewProp
import com.umc.design.Secondary100
import com.umc.design.component.CustomHeader

data class ChallengeScreenTopBarProp(
    val point: Int,
    val onShopIconClicked: () -> Unit,
)

@Composable
fun ChallengeScreen(
    topBarProp: ChallengeScreenTopBarProp,
    challengeCompletionDialogProp: ChallengeCompletionDialogProp?,
    pointGrantedCardDialogProp: PointGrantedCardDialogProp?,
    view: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Secondary100)
    ) {
        // 내용
        Column {
            CustomHeader(
                backgroundColor = Color.White.copy(alpha = 0.5f),
                headerTrailing = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 21.98.dp)
                    ) {
                        Box(
                            modifier = Modifier.clickable(
                                indication = null,
                                interactionSource = null,
                                onClick = topBarProp.onShopIconClicked
                            )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_shop),
                                contentDescription = null,
                                contentScale = ContentScale.FillHeight,
                                modifier = Modifier.height(24.dp)
                            )
                        }
                        PointChip(point = topBarProp.point)
                    }
                }
            )
            view()
        }
    }

    challengeCompletionDialogProp?.let { ChallengeCompletionDialog(prop = it) }
    pointGrantedCardDialogProp?.let { PointGrantedCardDialog(prop = it) }
}

val previewChallengeScreenTopBarProp = ChallengeScreenTopBarProp(
    point = previewChallengeTopBarProp.point,
    onShopIconClicked = previewChallengeTopBarProp.onShopIconClicked,
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