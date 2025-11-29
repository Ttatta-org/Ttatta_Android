package com.umc.login.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.character.Accessory
import com.umc.design.character.AccessorySet
import com.umc.design.character.CharacterView
import com.umc.design.component.CustomButton
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider
import com.umc.login.R

@Composable
fun DoneScreen(
    nickname: String,
    message: String,
    centerChipContent: String?,
    onGoToFindingPasswordButtonClicked: (() -> Unit)?,
    onBackButtonClicked: () -> Unit,
    onGoToLoginButtonClicked: () -> Unit,
) {
    val density = LocalDensity.current

    val statusBarHeight = WindowInsets.statusBars
        .asPaddingValues()
        .calculateTopPadding()

    val navigationBarHeight = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()

    var characterViewWidth by remember { mutableStateOf(0.dp) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
    ) {
        Spacer(modifier = Modifier.height(statusBarHeight))
        Box(
            modifier = Modifier
                .padding(start = 22.dp, top = 32.dp)
                .clip(CircleShape)
                .clickable { onBackButtonClicked() }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back),
                modifier = Modifier
                    .padding(8.dp)
                    .size(16.dp),
                tint = LocalColorTheme.current.primary[500],
                contentDescription = null,
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                contentAlignment = Alignment.TopCenter,
            ) {
                Image(
                    painter = painterResource(R.drawable.img_flower_background),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                )
                Image(
                    painter = painterResource(R.drawable.img_flower_background),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    colorFilter = ColorFilter.tint(
                        color = Color(0xFFDE806E).copy(alpha = 0.1f),
                        blendMode = BlendMode.SrcIn
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                        .offset(y = 2.dp)
                        .blur(radius = 10.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "${nickname}님 환영해요!",
                        color = LocalColorTheme.current.grey[700],
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W700,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = message,
                        color = Color.Black,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.W800,
                        lineHeight = 35.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp),
                    )
                    if (centerChipContent != null) Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(bottom = 42.dp)
                            .background(
                                color = LocalColorTheme.current.primary[100].copy(alpha = 0.8f),
                                shape = RoundedCornerShape(percent = 50),
                            )
                            .border(
                                width = 1.dp,
                                color = LocalColorTheme.current.primary[200],
                                shape = RoundedCornerShape(percent = 50),
                            )
                    ) {
                        Text(
                            text = centerChipContent,
                            color = LocalColorTheme.current.primary[500],
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W700,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(
                                start = 30.dp,
                                end = 30.dp,
                                top = 10.dp,
                                bottom = 8.dp,
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned {
                                characterViewWidth = with(density) { it.size.width.toDp() }
                            }
                    ) {
                        CharacterView(
                            accessorySet = AccessorySet.create(
                                Accessory.TTOTTO_BAG,
                                Accessory.TTOTTO_HAT,
                                Accessory.TTUTTU_BAG,
                                Accessory.TTUTTU_HAT,
                            ),
                            width = characterViewWidth,
                        )
                    }
                }
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(
                horizontal = 22.dp,
                vertical = 8.dp,
            )
        ) {
            if (onGoToFindingPasswordButtonClicked != null) Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .clickable { onGoToFindingPasswordButtonClicked() }
            ) {
                Text(
                    text = "비밀번호도 잊으셨나요?",
                    color = LocalColorTheme.current.primary[300],
                    fontWeight = FontWeight.W400,
                    fontSize = 12.sp,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
                )
            }
            CustomButton(
                text = "로그인 하러 가기",
                onClick = onGoToLoginButtonClicked,
            )
        }
        Spacer(modifier = Modifier.height(navigationBarHeight))
    }
}

@Preview
@Composable
fun PreviewDoneScreen() {
    ThemeProvider {
        DoneScreen(
            nickname = "김따따",
            message = "아이디 찾기\n완료!",
            centerChipContent = "Ttatta1234",
            onGoToFindingPasswordButtonClicked = {},
            onBackButtonClicked = {},
            onGoToLoginButtonClicked = {},
        )
    }
}