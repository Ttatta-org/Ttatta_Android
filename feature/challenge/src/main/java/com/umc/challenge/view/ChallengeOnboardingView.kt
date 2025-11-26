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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import com.umc.challenge.R
import com.umc.challenge.component.ShadowBoxScope
import com.umc.design.Primary200
import com.umc.design.Primary400
import com.umc.design.Primary500
import com.umc.design.Secondary100
import com.umc.design.Secondary300
import com.umc.design.character.Accessory
import com.umc.design.character.AccessorySet
import com.umc.design.character.CharacterView
import com.umc.design.component.CustomButton
import com.umc.design.theme.LocalColorTheme

data class ChallengeOnboardingViewProp(
    val equippedAccessorySet: AccessorySet,
    val isNewChallengeButtonEnabled: Boolean,
    val challengeItemPropList: List<ChallengeItemProp>,
    val onNewChallengeButtonClicked: () -> Unit,
)

enum class ChallengeState(
    val borderColor: Color,
    val backgroundColor: Color,
    @DrawableRes val icon: Int
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

private val speechBubbleShape = RoundedCornerShape(percent = 50)
private val speechBubbleHeight = 155.dp
private const val speechBubbleTailOffsetRatio = 3f / 4f
private val speechBubbleTailSize = DpSize(66.63.dp, 33.dp)
private val speechBubbleTailVerticalOffset = 2.dp * -1

@Composable
fun ChallengeOnboardingView(
    prop: ChallengeOnboardingViewProp,
) {
    val density = LocalDensity.current
    val totalSpeechBubbleHeight = speechBubbleHeight +
            speechBubbleTailSize.height +
            speechBubbleTailVerticalOffset

    var columnWidth by remember { mutableStateOf(0.dp) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
            .background(color = Color(0xFFFEF6F2))
    ) {
        // 뒷배경
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(R.drawable.img_challenge_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .onGloballyPositioned {
                    with(density) { columnWidth = it.size.width.toDp() }
                }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(totalSpeechBubbleHeight))

                // 캐릭터
                CharacterView(
                    accessorySet = prop.equippedAccessorySet,
                    width = columnWidth
                )

                Spacer(modifier = Modifier.height(21.16.dp))

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
            }
            // 말풍선
            ShadowBoxScope(
                radius = 4.dp,
                color = Color(0xFFDE806E).copy(alpha = 0.1f),
                offset = DpOffset(0.dp, 2.dp)
            ) {
                var speechBubbleWidth by remember { mutableIntStateOf(0) }

                Box(
                    modifier = Modifier
                        .height(totalSpeechBubbleHeight)
                        .padding(top = 40.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .height(speechBubbleHeight)
                            .fillMaxSize()
                            .background(
                                color = Color.White,
                                shape = speechBubbleShape
                            )
                            .onGloballyPositioned { speechBubbleWidth = it.size.width }
                            .padding(vertical = 49.dp)
                    ) {
                        Text(
                            text = "오늘의 챌린지를 만들고\n포인트를 얻어보세요!",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W700,
                                color = LocalColorTheme.current.grey[700],
                                textAlign = TextAlign.Center
                            ),
                        )
                    }

                    // TODO: 말풍선 꼬리 고치기
//                    Box(
//                        modifier = Modifier
//                            .offset {
//                                Offset(
//                                    x = speechBubbleWidth * speechBubbleTailOffsetRatio,
//                                    y = speechBubbleHeight.toPx() + speechBubbleTailVerticalOffset.toPx(),
//                                ).round()
//                            }
//                    ) {
//                        Image(
//                            painter = painterResource(id = R.drawable.img_speech_bubble_tail),
//                            contentDescription = null,
//                            contentScale = ContentScale.Fit,
//                            modifier = Modifier.size(speechBubbleTailSize)
//                        )
//                    }
                }
            }
        }
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
                .padding(horizontal = 20.dp, vertical = 15.dp)
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
                    maxLines = 1
                )
                Text(
                    text = prop.content,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400,
                    color = contentColor,
//                    maxLines = 1
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

@Composable
private fun AttendanceCard(
    attended: Boolean,
    onClick: () -> Unit
) {
    val borderColor = Color(0xFFFFD0C8)
    val backgroundColor = if (attended) Color(0xFFFFE6E1) else Color.White
    val titleColor = if (attended) Color(0xFFFF9888) else LocalColorTheme.current.grey[700]
    val contentColor = if (attended) Color(0xFFFF9888) else LocalColorTheme.current.grey[600]
    val iconRes = if (attended) R.drawable.ic_complete_stamp else R.drawable.ic_no_stamp

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor, RoundedCornerShape(18.dp))
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .clickable { onClick() }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 15.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = "출석하기",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.W700,
                    color = titleColor,
                    maxLines = 1
                )
                Text(
                    text = "매일 출석하고 10포인트 받아가세요!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400,
                    color = contentColor,
                    maxLines = 1
                )
            }

            Image(
                painter = painterResource(id = iconRes),
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
    Accessory.TTUTTU_THREE_COLOR_BALLOONS,
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
    equippedAccessorySet = previewAccessorySet,
    isNewChallengeButtonEnabled = true,
    challengeItemPropList = previewChallengeItemPropList,
    onNewChallengeButtonClicked = {}
)

@Preview(showBackground = true)
@Composable
fun PreviewChallengeOnboardingView() {
    ChallengeOnboardingView(
        prop = previewChallengeOnboardingViewProp
    )
}