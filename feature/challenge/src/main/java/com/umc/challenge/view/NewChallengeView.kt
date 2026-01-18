package com.umc.challenge.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.challenge.component.ChallengeBottomSheetProp
import com.umc.challenge.component.ChatBubble
import com.umc.challenge.component.NewChallengeBottomSheet
import com.umc.challenge.component.ShadowBoxScope
import com.umc.design.character.AccessorySet
import com.umc.design.character.CharacterView

data class NewChallengeViewProp(
    val topPadding: Dp,
    val maxTitleLength: Int,
    val title: String,
    val description: String,
    val equippedAccessorySet: AccessorySet,
    val isButtonEnabled: Boolean,
    val onTitleChanged: (String) -> Unit,
    val onDescriptionChanged: (String) -> Unit,
    val onCreateButtonClicked: () -> Unit,
    val onPastChallengeClick: () -> Unit,
)

@Composable
fun NewChallengeView(
    prop: NewChallengeViewProp
) {
    val density = LocalDensity.current
    var columnWidth by remember { mutableStateOf(0.dp) }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = prop.topPadding)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 32.dp, end = 32.dp, top = 46.dp)
                .onGloballyPositioned { with(density) { columnWidth = it.size.width.toDp() } },
        ) {
            // 캐릭터
            Box(
                modifier = Modifier.padding(top = 48.dp),
            ) {
                CharacterView(
                    accessorySet = prop.equippedAccessorySet,
                    width = columnWidth,
                )
            }
            // 말풍선
            ShadowBoxScope(
                radius = 10.dp,
                color = Color(0xFFDE806E).copy(alpha = 0.1f),
                offset = DpOffset(0.dp, 2.dp)
            ) {
                Box(
                    modifier = Modifier.widthIn(max = 195.dp)
                ) {
                    ChatBubble(
                        text = "우리 함께 새 챌린지를\n생성해봐요!",
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        left = true,
                    )
                }
            }
        }
        NewChallengeBottomSheet(
            prop = ChallengeBottomSheetProp(
                maxTitleLength = 20,
                title = prop.title,
                content = prop.description,
                onCreateButtonClicked = prop.onCreateButtonClicked,
                onTitleChanged = prop.onTitleChanged,
                onContentChanged = prop.onDescriptionChanged,
                isButtonEnabled = prop.isButtonEnabled,
                onPastChallengeClick = prop.onPastChallengeClick,
            )
        )
    }
}

val previewNewChallengeViewProp = NewChallengeViewProp(
    topPadding = 66.dp,
    maxTitleLength = 20,
    title = "",
    description = "",
    equippedAccessorySet = previewAccessorySet,
    isButtonEnabled = true,
    onTitleChanged = {},
    onDescriptionChanged = {},
    onCreateButtonClicked = {},
    onPastChallengeClick = {}
)

@Preview
@Composable
fun PreviewNewChallengeView() {
    NewChallengeView(
        prop = previewNewChallengeViewProp
    )
}