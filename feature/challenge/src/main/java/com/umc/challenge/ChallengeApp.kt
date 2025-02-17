package com.umc.challenge

import androidx.compose.runtime.Composable

@Composable
fun ChallengeApp(
    viewModel: ChallengeViewModel,
    onNavigationBarVisibilityChanged: (Boolean) -> Unit,
) {
    ChallengeScreen(
        topBarProp = previewChallengeScreenTopBarProp,
        challengeItemPropList = previewChallengeItemPropList,
        accessorySet = previewAccessorySet,
        onNewChallengeButtonClicked = {}
    )
}
