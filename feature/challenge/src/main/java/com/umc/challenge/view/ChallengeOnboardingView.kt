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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import com.umc.challenge.R
import com.umc.challenge.ShadowBoxScope
import com.umc.design.Primary200
import com.umc.design.Primary400
import com.umc.design.Primary500
import com.umc.design.Secondary100
import com.umc.design.Secondary300
import com.umc.design.character.Accessory
import com.umc.design.character.AccessorySet
import com.umc.design.character.CharacterView

data class ChallengeOnboardingViewProp(
    val accessorySet: AccessorySet,
    val challengeItemPropList: List<ChallengeItemProp>,
    val onNewChallengeButtonClicked: () -> Unit,
)

enum class ChallengeState(
    val borderColor: Color,
    val backgroundColor: Color,
    @DrawableRes val icon: Int
) {
    IN_PROGRESS(
        borderColor = Color.Secondary300,
        backgroundColor = Color.White,
        icon = R.drawable.ic_no_stamp
    ),
    COMPLETED(
        borderColor = Color.Primary200,
        backgroundColor = Color(0xFFFFEAE2),
        icon = R.drawable.ic_complete_stamp
    ),
}

data class ChallengeItemProp(
    val title: String,
    val state: ChallengeState,
    val onClicked: () -> Unit,
)

private val speechBubbleShape = RoundedCornerShape(percent = 50)
private val speechBubbleHeight = 84.dp
private const val speechBubbleTailOffsetRatio = 3f / 4f
private val speechBubbleTailSize = DpSize(21.dp, 16.dp)
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
            .background(color = Color.Secondary100)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(state = rememberScrollState())
                .onGloballyPositioned {
                    with(density) { columnWidth = it.size.width.toDp() }
                }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp),
            ) {
                Spacer(modifier = Modifier.height(totalSpeechBubbleHeight))
                // 캐릭터
                CharacterView(
                    accessorySet = prop.accessorySet,
                    width = columnWidth
                )
                // 새 챌린지 버튼
                ElevatedButton(
                    onClick = prop.onNewChallengeButtonClicked,
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 4.dp
                    ),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = Color.Primary200,
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.new_challenge),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W600
                        )
                    }
                }
                // 오늘 챌린지 목록
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    prop.challengeItemPropList.forEach { prop ->
                        ChallengeItem(prop = prop)
                    }
                }
            }
            // 말풍선
            ShadowBoxScope(
                radius = 4.dp,
                color = Color.Primary500.copy(alpha = 0.2f),
                offset = DpOffset(0.dp, 4.dp)
            ) {
                var speechBubbleWidth by remember { mutableIntStateOf(0) }

                Box(
                    modifier = Modifier.height(totalSpeechBubbleHeight)
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
                    ) {
                        Text(
                            text = if (prop.challengeItemPropList.isEmpty())
                                stringResource(id = R.string.speech_bubble_content_no_challenge)
                            else
                                stringResource(id = R.string.speech_bubble_content_has_challenge),
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.W600,
                                color = Color.Primary400,
                                textAlign = TextAlign.Center
                            ),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .offset {
                                Offset(
                                    x = speechBubbleWidth * speechBubbleTailOffsetRatio,
                                    y = speechBubbleHeight.toPx() + speechBubbleTailVerticalOffset.toPx(),
                                ).round()
                            }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_speech_bubble_tail),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(speechBubbleTailSize)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChallengeItem(
    prop: ChallengeItemProp,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(
                color = prop.state.backgroundColor,
                shape = RoundedCornerShape(percent = 50)
            )
            .border(
                width = 1.dp,
                color = prop.state.borderColor,
                shape = RoundedCornerShape(percent = 50)
            )
            .clickable { prop.onClicked() }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = prop.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.W600,
                color = Color(0xFF4B4B4B),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Image(
                painter = painterResource(id = prop.state.icon),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(36.dp)
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
        state = ChallengeState.IN_PROGRESS,
        onClicked = {}
    ),
    ChallengeItemProp(
        title = "물 하루 3잔 마시기",
        state = ChallengeState.COMPLETED,
        onClicked = {}
    ),
    ChallengeItemProp(
        title = "도서관 가기",
        state = ChallengeState.COMPLETED,
        onClicked = {}
    ),
)

val previewChallengeOnboardingViewProp = ChallengeOnboardingViewProp(
    accessorySet = previewAccessorySet,
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