package com.umc.record.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.Primary300
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider
import com.umc.record.R
import com.umc.record.util.hasFinalConsonant
import com.umc.design.R as Res

data class LocationBottomSheetProp(
    val location: String?,
    val onConfirm: (confirmedLocationName: String) -> Unit,
    val isConfirming: Boolean = false,
)

private val bottomSheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

@Composable
fun LocationBottomSheet(
    prop: LocationBottomSheetProp
) {
    var locationName by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = bottomSheetShape
            )
            .background(
                color = Color(0xFFFEF6F2), // 배경색
                shape = bottomSheetShape
            )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
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
                modifier = Modifier.padding(8.dp)
            ) {
                Image(
                    painter = painterResource(id = Res.drawable.ic_header_deco), // 헤더 데코 이미지 리소스
                    contentDescription = null, modifier = Modifier.size(32.dp) // 이미지 크기 설정
                )
            }
            // 안내 텍스트
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.height(64.dp)
            ) {
                if (prop.location != null) Text(
                    text = "\'${prop.location}\'"
                            + (if (prop.location.hasFinalConsonant()) "으로 " else "로 ")
                            + stringResource(id = R.string.modify_location),
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color(0xFFFF9681),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineBreak = LineBreak.Heading,
                    ),
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) else Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    BasicTextField(
                        value = locationName,
                        onValueChange = { locationName = it },
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black,
                        )
                    ) { innerTextField ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(percent = 50))
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(percent = 50),
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color.Primary300,
                                    shape = RoundedCornerShape(percent = 50)
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                innerTextField()
                            }
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
                }
            }
            // 버튼
            ElevatedButton(
                onClick = {
                    if (prop.isConfirming) return@ElevatedButton
                    prop.onConfirm(prop.location ?: locationName)
                },
                enabled = !prop.isConfirming && (prop.location != null || locationName.isNotBlank()),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LocalColorTheme.current.primary[400],
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .padding(vertical = 8.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.set),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

val previewLocationBottomSheetProp = LocationBottomSheetProp(
    location = null,
    onConfirm = {},
    isConfirming = false
)

@Preview
@Composable
fun PreviewEditLocationBottomSheet() {
    ThemeProvider {
        LocationBottomSheet(
            prop = previewLocationBottomSheetProp
        )
    }
}