package com.umc.challenge.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umc.challenge.component.ChallengeItem
import com.umc.challenge.component.ChallengeItemProp
import com.umc.challenge.component.ChallengeState
import com.umc.core.model.Challenge
import com.umc.design.component.CustomButton
import com.umc.design.component.CustomHeader
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider

@Composable
fun PastChallengeScreen(
    pastChallenges: List<Challenge>,
    onRetryChallenge: (Challenge) -> Unit,
    onBackButtonClicked: () -> Unit,
) {
    val density = LocalDensity.current

    var topBarHeight by remember { mutableStateOf(0.dp) }

    // 지금 선택된 아이템 인덱스 (없으면 -1)
    var selectedIndex by remember { mutableIntStateOf(-1) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.secondary[100])
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Spacer(modifier = Modifier.height(topBarHeight + 30.dp))
            }
            itemsIndexed(pastChallenges) { index, challenge ->
                val isSelected = selectedIndex == index
                val onSelected = { selectedIndex = index }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 5.dp, horizontal = 22.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                color = Color.White,
                                shape = CircleShape,
                            )
                            .border(
                                width = 1.dp,
                                color = LocalColorTheme.current.primary[300],
                                shape = CircleShape,
                            )
                            .clickable(
                                indication = null,
                                interactionSource = null,
                                onClick = onSelected
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(
                                        color = LocalColorTheme.current.primary[300],
                                        shape = CircleShape,
                                    )
                            )
                        }
                    }
                    ChallengeItem(
                        prop = ChallengeItemProp(
                            title = challenge.title,
                            content = challenge.content,
                            state = if (challenge.isCompleted) ChallengeState.COMPLETED else ChallengeState.FAILED,
                            onClicked = { selectedIndex = index },
                        )
                    )
                }
            }
            item {
                Spacer(
                    modifier = Modifier.height(
                        WindowInsets.navigationBars
                            .asPaddingValues()
                            .calculateBottomPadding() + 100.dp
                    )
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(15.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                LocalColorTheme.current.secondary[100],
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LocalColorTheme.current.secondary[100])
                    .padding(
                        start = 22.dp,
                        end = 22.dp,
                        bottom = WindowInsets.navigationBars
                            .asPaddingValues()
                            .calculateBottomPadding() + 8.dp
                    )
            ) {
                CustomButton(
                    text = "다시 도전하기",
                    isEnabled = selectedIndex != -1,
                    onClick = {
                        pastChallenges
                            .getOrNull(selectedIndex)
                            ?.let { onRetryChallenge(it) }
                    }
                )
            }
        }
        Box(
            modifier = Modifier.onSizeChanged {
                topBarHeight = with(density) { it.height.toDp() }
            },
        ) {
            CustomHeader(
                showLogo = false,
                centerText = "지난 챌린지",
                backgroundColor = Color.White.copy(alpha = 0.5f),
                onBackButtonClicked = onBackButtonClicked,
            )
        }
    }
}

@Preview
@Composable
fun PreviewPastChallengeScreen() {
    val dummyChallenges = listOf(
        Challenge(
            id = 1L,
            title = "헬스장 가기",
            content = "오늘은 하체 뿌시기",
            isCompleted = true,
        ),
        Challenge(
            id = 2L,
            title = "영어 단어 30개 외우기",
            content = "오늘 분량은 꼭!",
            isCompleted = false,
        ),
    ).let {
        it + it + it + it + it + it + it
    }

    ThemeProvider {
        PastChallengeScreen(
            pastChallenges = dummyChallenges,
            onRetryChallenge = {},
            onBackButtonClicked = {},
        )
    }
}