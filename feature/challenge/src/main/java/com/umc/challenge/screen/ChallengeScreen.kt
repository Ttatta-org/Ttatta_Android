package com.umc.challenge.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.umc.challenge.R
import com.umc.challenge.modal.ChallengeCompletionDialog
import com.umc.challenge.modal.ChallengeCompletionDialogProp
import com.umc.challenge.component.PointChip
import com.umc.challenge.modal.PointGrantedCardDialog
import com.umc.challenge.modal.PointGrantedCardDialogProp
import com.umc.design.component.CustomHeader
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider

data class ChallengeScreenTopBarProp(
    val point: Int,
    val onShopIconClicked: () -> Unit,
)

@Composable
fun ChallengeScreen(
    topBarProp: ChallengeScreenTopBarProp,
    challengeCompletionDialogProp: ChallengeCompletionDialogProp?,
    pointGrantedCardDialogProp: PointGrantedCardDialogProp?,
    view: @Composable (headerHeight: Dp) -> Unit,
) {
    val density = LocalDensity.current

    var headerHeight by remember { mutableStateOf(66.dp) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = LocalColorTheme.current.secondary[100])
    ) {
        // 뒷배경
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = WindowInsets.statusBars
                        .asPaddingValues()
                        .calculateTopPadding() + 48.dp
                ),
        ) {
            Image(
                painter = painterResource(R.drawable.img_challenge_background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        view.invoke(headerHeight)
        Box(
            modifier = Modifier
                .onSizeChanged { headerHeight = with(density) { it.height.toDp() } }
        ) {
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
        }
    }

    challengeCompletionDialogProp?.let { ChallengeCompletionDialog(prop = it) }
    pointGrantedCardDialogProp?.let { PointGrantedCardDialog(prop = it) }
}

val previewChallengeScreenTopBarProp = ChallengeScreenTopBarProp(
    point = 100,
    onShopIconClicked = {},
)

@Preview(showBackground = true)
@Composable
fun PreviewChallengeScreen() {
    ThemeProvider {
        ChallengeScreen(
            topBarProp = previewChallengeScreenTopBarProp,
            challengeCompletionDialogProp = null,
            pointGrantedCardDialogProp = null,
        ) {
            Text(text = "test")
        }
    }
}