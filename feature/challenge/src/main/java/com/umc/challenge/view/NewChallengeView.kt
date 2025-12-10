package com.umc.challenge.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
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
import com.umc.challenge.component.ChallengeBottomSheetProp
import com.umc.challenge.component.NewChallengeBottomSheet
import com.umc.challenge.component.ShadowBoxScope
import com.umc.design.character.Accessory
import com.umc.design.character.AccessorySet
import com.umc.design.character.CharacterView
import com.umc.design.theme.LocalColorTheme

data class NewChallengeViewProp(
    val maxTitleLength: Int,
    val title: String,
    val description: String,
    val equippedAccessorySet: AccessorySet,
    val onTitleChanged: (String) -> Unit,
    val onDescriptionChanged: (String) -> Unit,
    val onCreateButtonClicked: () -> Unit,
    val onPastChallengeClick: () -> Unit,
)

private val speechBubbleShape = RoundedCornerShape(percent = 50)
private val speechBubbleHeight = 99.dp
private const val speechBubbleTailOffsetRatio = 3f / 4f
private val speechBubbleTailSize = DpSize(42.02.dp, 21.dp)
private val speechBubbleTailVerticalOffset = 2.dp * -1

@Composable
fun NewChallengeView(
    prop: NewChallengeViewProp
) {
    val density = LocalDensity.current

    val totalSpeechBubbleHeight = speechBubbleHeight +
            speechBubbleTailSize.height +
            speechBubbleTailVerticalOffset

    var columnWidth by remember { mutableStateOf(0.dp) }

    Box(
        modifier = Modifier
            .fillMaxSize()
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
                .verticalScroll(state = rememberScrollState())
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
                        .padding(top = 62.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .height(speechBubbleHeight)
                            .width(195.dp)
                            .background(
                                color = Color.White,
                                shape = speechBubbleShape
                            )
                            .onGloballyPositioned { speechBubbleWidth = it.size.width }
                    ) {
                        Text(
                            text = "우리 함께 새 챌린지를\n생성해봐요!",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W700,
                                color = LocalColorTheme.current.grey[700],
                                textAlign = TextAlign.Center
                            ),
                        )
                    }

                    // TODO: 말풍선 꼬리 고치기
                    Box(
                        modifier = Modifier
                            .offset {
                                Offset(
                                    x = 0f,
                                    y = speechBubbleHeight.toPx() + speechBubbleTailVerticalOffset.toPx(),
                                ).round()
                            }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_speech_bubble_tail_left),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(speechBubbleTailSize)
                        )
                    }
                }
            }
        }

        // 바텀시트
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            NewChallengeBottomSheet(
                prop = ChallengeBottomSheetProp(
                    title = prop.title,
                    content = prop.description,
                    onCreateButtonClicked = prop.onCreateButtonClicked,
                    onTitleChanged = { text ->
                        val clipped = if (text.length > prop.maxTitleLength)
                            text.take(prop.maxTitleLength) else text
                        prop.onTitleChanged(clipped)
                    },
                    onContentChanged = { text -> prop.onDescriptionChanged(text) },
                    isButtonEnabled = prop.title.isNotBlank(),
                    onPastChallengeClick = prop.onPastChallengeClick
                )
            )
        }
    }
}

val previewNewChallengeViewProp =  NewChallengeViewProp(
    maxTitleLength = 20,
    title = "",
    description = "",
    equippedAccessorySet = previewAccessorySet,
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