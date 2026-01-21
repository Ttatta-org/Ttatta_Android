package com.umc.record.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.component.CustomButton
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.LocalFontTheme
import com.umc.design.theme.ThemeProvider
import com.umc.record.R
import com.umc.record.util.hasFinalConsonant
import com.umc.design.R as Res

@Composable
fun LocationBottomSheet(
    location: String,
    isEditingMode: Boolean,
    isConfirmButtonEnabled: Boolean,
    onLocationChanged: (String) -> Unit,
    onConfirmButtonClicked: () -> Unit,
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
            if (isEditingMode) Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    4.dp, Alignment.CenterVertically
                ),
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                BasicTextField(
                    value = location, onValueChange = onLocationChanged,
                    textStyle = TextStyle(
                        fontFamily = LocalFontTheme.current.font,
                        fontSize = 13.sp,
                        lineHeight = 13.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.W400,
                    ),
                ) { innerTextField ->
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(percent = 50))
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(percent = 50),
                            )
                            .border(
                                width = 1.dp,
                                color = LocalColorTheme.current.primary[500],
                                shape = RoundedCornerShape(percent = 50)
                            )
                            .padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 9.dp)
                    ) {
                        Text(
                            text = "지역의 이름을 입력해주세요",
                            fontSize = 13.sp,
                            lineHeight = 13.sp,
                            fontWeight = FontWeight.W400,
                            color = LocalColorTheme.current.grey[600],
                            modifier = Modifier.alpha(if (location.isEmpty()) 1f else 0f)
                        )
                        innerTextField.invoke()
                    }
                }
                Text(
                    text = "주소 정보가 없어요! 지역의 이름을 직접 입력해주세요",
                    fontSize = 12.sp,
                    color = Color(0xFFFF9681),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    letterSpacing = (-0.4).sp
                )
            } else Text(
                text = buildString {
                    append("\'${location}\'")
                    append(if (location.hasFinalConsonant()) "으로\n" else "로\n")
                    append(stringResource(id = R.string.modify_location))
                },
                color = Color(0xFFFF9681),
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
                lineHeight = 24.sp,
                letterSpacing = (-0.4).sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 22.dp)
                    .widthIn(max = 260.dp)
            ) {
                CustomButton(
                    text = stringResource(id = R.string.set),
                    isEnabled = isConfirmButtonEnabled,
                    onClick = onConfirmButtonClicked,
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewEditLocationBottomSheetEntering() {
    ThemeProvider {
        LocationBottomSheet(
            location = "",
            isEditingMode = true,
            isConfirmButtonEnabled = true,
            onLocationChanged = {},
            onConfirmButtonClicked = {},
        )
    }
}

@Preview
@Composable
private fun PreviewEditLocationBottomSheetShowing() {
    ThemeProvider {
        LocationBottomSheet(
            location = "서울특별시 강남구 테헤란로",
            isEditingMode = false,
            isConfirmButtonEnabled = true,
            onLocationChanged = {},
            onConfirmButtonClicked = {},
        )
    }
}