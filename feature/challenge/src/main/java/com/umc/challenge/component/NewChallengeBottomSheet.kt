package com.umc.challenge.component

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.R
import com.umc.design.component.CustomButton
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.LocalFontTheme
import com.umc.design.theme.ThemeProvider

data class ChallengeBottomSheetProp(
    val maxTitleLength: Int,
    val title: String,
    val content: String,
    val onCreateButtonClicked: () -> Unit,
    val onTitleChanged: (String) -> Unit,
    val onContentChanged: (String) -> Unit,
    val isButtonEnabled: Boolean,
    val onPastChallengeClick: () -> Unit,
)

private val bottomSheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

@Composable
fun NewChallengeBottomSheet(
    prop: ChallengeBottomSheetProp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .dropShadow(
                shape = bottomSheetShape,
                shadow = Shadow(
                    radius = 15.dp,
                    offset = DpOffset(x = 0.dp, y = (-2).dp),
                    color = Color(0xFF9C9C9C).copy(alpha = 0.2f),
                ),
            )
            .background(color = Color.White, shape = bottomSheetShape)
            .padding(horizontal = 22.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(
                bottom = WindowInsets.safeDrawing
                    .asPaddingValues()
                    .calculateBottomPadding()
            )
        ) {
            // 헤더 이미지
            Image(
                painter = painterResource(id = R.drawable.ic_header_deco),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(32.dp)
                    .padding(vertical = 16.dp),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "챌린지 명",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W700,
                    lineHeight = 15.sp,
                    color = LocalColorTheme.current.grey[600],
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 16.dp),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = LocalColorTheme.current.primary[100],
                            shape = RoundedCornerShape(18.dp)
                        )
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(18.dp),
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = "챌린지 명을 입력해주세요", // 힌트 텍스트
                            color = LocalColorTheme.current.grey[400],
                            fontSize = 13.sp,
                            modifier = Modifier.alpha(if (prop.title.isEmpty()) 1f else 0f),
                        )
                        BasicTextField(
                            value = prop.title,
                            onValueChange = prop.onTitleChanged,
                            textStyle = TextStyle(
                                fontFamily = LocalFontTheme.current.font,
                                fontSize = 13.sp,
                                color = Color.Black
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = if (prop.title.isEmpty()) LocalColorTheme.current.grey[400] else LocalColorTheme.current.primary[500])) {
                                append(prop.title.length.toString())
                            }
                            withStyle(style = SpanStyle(color = LocalColorTheme.current.grey[400])) {
                                append("/${prop.maxTitleLength}")
                            }
                        },
                        fontWeight = FontWeight.W400,
                        fontSize = 13.sp,
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "내용",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W700,
                    lineHeight = 15.sp,
                    color = LocalColorTheme.current.grey[600],
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 16.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = LocalColorTheme.current.primary[100],
                            shape = RoundedCornerShape(18.dp)
                        )
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(18.dp),
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text(
                            text = "챌린지 명을 입력해주세요", // 힌트 텍스트
                            color = LocalColorTheme.current.grey[400],
                            fontSize = 13.sp,
                            modifier = Modifier.alpha(if (prop.content.isEmpty()) 1f else 0f),
                        )
                        BasicTextField(
                            value = prop.content,
                            onValueChange = prop.onContentChanged,
                            textStyle = TextStyle(
                                fontFamily = LocalFontTheme.current.font,
                                fontSize = 13.sp,
                                color = Color.Black
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        indication = null,
                        interactionSource = null,
                        onClick = { prop.onPastChallengeClick() },
                    )
            ) {
                Text(
                    text = "지난 챌린지를 보러가볼까요?",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400,
                    color = LocalColorTheme.current.primary[300],
                    textAlign = TextAlign.Center,
                    textDecoration = TextDecoration.Underline
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            CustomButton(
                text = "챌린지 생성하기",
                isEnabled = prop.isButtonEnabled,
                onClick = prop.onCreateButtonClicked,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

val previewChallengeBottomSheetProp = ChallengeBottomSheetProp(
    maxTitleLength = 20,
    title = "도서관 가기",
    content = "시험공부 및 과제",
    onCreateButtonClicked = {},
    onTitleChanged = {},
    onContentChanged = {},
    isButtonEnabled = true,
    onPastChallengeClick = {},
)

@Preview
@Composable
fun PreviewDiaryBottomSheet() {
    ThemeProvider {
        NewChallengeBottomSheet(prop = previewChallengeBottomSheetProp)
    }
}
