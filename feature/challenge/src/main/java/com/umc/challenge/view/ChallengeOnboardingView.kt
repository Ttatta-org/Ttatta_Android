package com.umc.challenge.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.max
import com.umc.challenge.component.ChallengeItem
import com.umc.challenge.component.ChallengeItemProp
import com.umc.challenge.component.ChatBubble
import com.umc.challenge.component.ShadowBoxScope
import com.umc.challenge.component.previewChallengeItemPropList
import com.umc.design.character.Accessory
import com.umc.design.character.AccessorySet
import com.umc.design.character.CharacterView
import com.umc.design.component.CustomButton
import com.umc.design.theme.ThemeProvider

data class ChallengeOnboardingViewProp(
    val topPadding: Dp,
    val equippedAccessorySet: AccessorySet,
    val isNewChallengeButtonEnabled: Boolean,
    val challengeItemPropList: List<ChallengeItemProp>,
    val onNewChallengeButtonClicked: () -> Unit,
)

@Composable
fun ChallengeOnboardingView(
    prop: ChallengeOnboardingViewProp,
) {
    val density = LocalDensity.current

    var columnWidth by remember { mutableStateOf(0.dp) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp)
            .onGloballyPositioned { with(density) { columnWidth = it.size.width.toDp() } }
    ) {
        Spacer(modifier = Modifier.height(50.dp + prop.topPadding))
        // 말풍선
        ShadowBoxScope(
            radius = 10.dp,
            color = Color(0xFFDE806E).copy(alpha = 0.1f),
            offset = DpOffset(0.dp, 2.dp)
        ) {
            Box(
                modifier = Modifier.widthIn(max = 307.dp)
            ) {
                ChatBubble(text = "오늘의 챌린지를 만들고\n포인트를 얻어보세요!")
            }
        }
        // 캐릭터
        CharacterView(
            accessorySet = prop.equippedAccessorySet,
            width = max(columnWidth - 20.dp, 0.dp),
        )
        Spacer(modifier = Modifier.height(40.dp))
        // 새 챌린지 버튼
        CustomButton(
            text = "챌린지 생성하기",
            isEnabled = prop.isNewChallengeButtonEnabled,
            onClick = prop.onNewChallengeButtonClicked,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 오늘 챌린지 목록
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                prop.challengeItemPropList.forEach { prop ->
                    ChallengeItem(prop = prop)
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

val previewAccessorySet = AccessorySet.create(
    Accessory.TTOTTO_COZY_MUFFLER,
    Accessory.TTOTTO_CAP,
    Accessory.TTUTTU_LIFESAVER_COFFEE,
    Accessory.TTUTTU_HAT
)

val previewChallengeOnboardingViewProp = ChallengeOnboardingViewProp(
    topPadding = 50.dp,
    equippedAccessorySet = previewAccessorySet,
    isNewChallengeButtonEnabled = true,
    challengeItemPropList = previewChallengeItemPropList,
    onNewChallengeButtonClicked = {}
)

@Preview(showBackground = true)
@Composable
fun PreviewChallengeOnboardingView() {
    ThemeProvider {
        ChallengeOnboardingView(
            prop = previewChallengeOnboardingViewProp
        )
    }
}