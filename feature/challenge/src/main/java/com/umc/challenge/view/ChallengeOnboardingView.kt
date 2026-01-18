package com.umc.challenge.view

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.umc.challenge.R
import com.umc.challenge.component.ChatBubble
import com.umc.challenge.component.ShadowBoxScope
import com.umc.design.character.Accessory
import com.umc.design.character.AccessorySet
import com.umc.design.character.CharacterView
import com.umc.design.component.CustomButton
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider

data class ChallengeOnboardingViewProp(
    val topPadding: Dp,
    val equippedAccessorySet: AccessorySet,
    val isNewChallengeButtonEnabled: Boolean,
    val challengeItemPropList: List<ChallengeItemProp>,
    val onNewChallengeButtonClicked: () -> Unit,
)

enum class ChallengeState(
    val borderColor: Color,
    val backgroundColor: Color,
    @field:DrawableRes val icon: Int
) {
    IN_PROGRESS(
        borderColor = Color(0xFFFFD0C8),
        backgroundColor = Color.White,
        icon = R.drawable.ic_no_stamp
    ),
    COMPLETED(
        borderColor = Color(0xFFFFD0C8),
        backgroundColor = Color(0xFFFFE6E1),
        icon = R.drawable.ic_complete_stamp
    ),
}

data class ChallengeItemProp(
    val title: String,
    val content: String,
    val state: ChallengeState,
    val onClicked: () -> Unit,
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

@Composable
private fun ChallengeItem(
    prop: ChallengeItemProp,
) {
    val titleColor = if (prop.state == ChallengeState.COMPLETED)
        LocalColorTheme.current.primary[400] else LocalColorTheme.current.grey[700]
    val contentColor = if (prop.state == ChallengeState.COMPLETED)
        LocalColorTheme.current.primary[400] else LocalColorTheme.current.grey[600]

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(
                color = prop.state.backgroundColor,
                shape = RoundedCornerShape(18.dp)
            )
            .border(
                width = 1.dp,
                color = prop.state.borderColor,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { prop.onClicked() }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 13.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = prop.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.W700,
                    color = titleColor,
                    maxLines = 1,
                    lineHeight = 18.sp,
                )
                Text(
                    text = prop.content,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400,
                    color = contentColor,
                    lineHeight = 13.sp,
                )
            }

            Image(
                painter = painterResource(id = prop.state.icon),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(38.dp)
            )
        }
    }
}

val previewAccessorySet = AccessorySet.create(
    Accessory.TTOTTO_COZY_MUFFLER,
    Accessory.TTOTTO_CAP,
    Accessory.TTUTTU_LIFESAVER_COFFEE,
    Accessory.TTUTTU_HAT
)

val previewChallengeItemPropList = listOf(
    ChallengeItemProp(
        title = "1시간 공부하기",
        content = "CS 요약 정리 + 백준 2문제",
        state = ChallengeState.IN_PROGRESS,
        onClicked = {}
    ),
    ChallengeItemProp(
        title = "물 하루 3잔 마시기",
        content = "점심 전 1잔, 오후에 2잔",
        state = ChallengeState.COMPLETED,
        onClicked = {}
    ),
    ChallengeItemProp(
        title = "도서관 가기",
        content = "3층 열람실 2시간",
        state = ChallengeState.COMPLETED,
        onClicked = {}
    ),
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