package com.umc.record.component

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.LocalFontTheme
import com.umc.design.theme.ThemeProvider
import com.umc.record.R
import com.umc.design.R as Res

@Composable
fun DiaryBottomSheet(
    userName: String,
    diaryContent: String,
    isButtonEnabled: Boolean,
    onCreateButtonClicked: () -> Unit,
    onDiaryContentChanged: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .dropShadow(
                shadow = Shadow(
                    color = Color(0xFF9C9C9C),
                    offset = DpOffset(0.dp, -2.dp),
                    radius = 15.dp,
                    alpha = 0.2f,
                ),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .background(
                color = Color(0xFFFEF6F2), // 배경색
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = WindowInsets.safeDrawing
                        .asPaddingValues()
                        .calculateBottomPadding()
                )
        ) {
            // 헤더 이미지
            Box(
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = Res.drawable.ic_header_deco),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.height(16.dp),
                )
            }
            // 안내 텍스트
            Text(
                text = (if (userName.isNotBlank()) userName + "님 " else "") + "이곳에 기록을 남겨주세요",
                color = LocalColorTheme.current.primary[500],
                fontSize = 15.sp,
                lineHeight = 16.sp,
                letterSpacing = (-0.4).sp,
                fontWeight = FontWeight.W700,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(13.dp),
                modifier = Modifier
                    .padding(horizontal = 22.dp)
                    .fillMaxWidth()
            ) {
                BasicTextField(
                    value = diaryContent,
                    onValueChange = onDiaryContentChanged,
                    textStyle = TextStyle(
                        fontFamily = LocalFontTheme.current.font,
                        fontSize = 13.sp,
                        lineHeight = 17.sp,
                        letterSpacing = (-0.4).sp,
                        color = Color.Black
                    ),
                    modifier = Modifier.weight(1f),
                ) { innerTextField ->
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = LocalColorTheme.current.primary[400],
                                shape = RoundedCornerShape(22.dp)
                            )
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(22.dp)
                            )
                            .padding(horizontal = 18.dp, vertical = 12.dp)
                    ) {

                        Text(
                            text = stringResource(id = R.string.record_placeholder),
                            color = LocalColorTheme.current.grey[400],
                            fontSize = 13.sp,
                            lineHeight = 17.sp,
                            letterSpacing = (-0.4).sp,
                            modifier = Modifier.alpha(if (diaryContent.isEmpty()) 1f else 0f),
                        )
                        innerTextField.invoke()
                    }
                }
                Icon(
                    painter = painterResource(id = R.drawable.btn_add),
                    contentDescription = "Add",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .height(42.dp)
                        .clickable(
                            enabled = isButtonEnabled,
                            indication = null,
                            interactionSource = null,
                            onClick = onCreateButtonClicked,
                        )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview
@Composable
private fun PreviewDiaryBottomSheetNotEmpty() {
    ThemeProvider {
        DiaryBottomSheet(
            userName = "hello",
            diaryContent = "오늘 서촌에 있는 한 카페에 갔는데 너무 귀여운 사과에이드를 마셨다... 휘낭시에나 구움과자들도 많았는데 배가 불러서 다 못먹어봤다... 다음에",
            isButtonEnabled = true,
            onCreateButtonClicked = {},
            onDiaryContentChanged = {},
        )
    }
}

@Preview
@Composable
private fun PreviewDiaryBottomSheetEmpty() {
    ThemeProvider {
        DiaryBottomSheet(
            userName = "hello",
            diaryContent = "",
            isButtonEnabled = true,
            onCreateButtonClicked = {},
            onDiaryContentChanged = {},
        )
    }
}
