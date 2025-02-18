package com.umc.record.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.record.R
import com.umc.design.R as Res

data class DiaryBottomSheetProp(
    val userName: String,
    val diaryContent: String,
    val onCreateButtonClicked: () -> Unit,
    val onDiaryContentChanged: (String) -> Unit
)

private val bottomSheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

@Composable
fun DiaryBottomSheet(
    prop: DiaryBottomSheetProp,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = bottomSheetShape,
            )
            .background(
                color = Color(0xFFFEF6F2),
                shape = bottomSheetShape
            )
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(
                bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
            )
        ) {
            // 헤더 이미지
            Box(
                modifier = Modifier.padding(8.dp)
            ) {
                Image(
                    painter = painterResource(id = Res.drawable.ic_header_deco), // 헤더 데코 이미지 리소스
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp) // 이미지 크기 설정
                )
            }
            // 안내 텍스트
            Text(
                text = prop.userName + stringResource(id = R.string.record_here),
                fontSize = 15.sp,
                color = Color(0xFFFF9681),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp), // 간격 9dp 설정
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f) // Row 내에서 남은 공간을 차지
                        .border(
                            width = 1.dp,
                            color = Color(0xFFFCAD98),
                            shape = RoundedCornerShape(28.dp)
                        ) // 테두리 추가
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(28.dp) // 둥근 모서리 28dp
                        )
                        .padding(horizontal = 18.dp, vertical = 10.dp) // 내부 여백
                ) {
                    BasicTextField(
                        value = prop.diaryContent,
                        onValueChange = prop.onDiaryContentChanged,
                        textStyle = TextStyle(
                            fontSize = 13.sp,
                            color = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) { innerTextField ->
                        innerTextField()
                        if (prop.diaryContent.isEmpty()) Text(
                            text = stringResource(id = R.string.record_placeholder), // 힌트 텍스트
                            color = Color(0xFFCACACA),
                            fontSize = 13.sp
                        )
                    }
                }
                // 추가 버튼
                IconButton(
                    onClick = prop.onCreateButtonClicked,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.btn_add), // 리소스 파일의 추가 버튼
                        contentDescription = "Add",
                        tint = Color.Unspecified, // Tint 효과 제거
                        modifier = Modifier.size(48.dp) // 아이콘 크기 설정
                    )
                }
            }
        }
    }
}

val previewDiaryBottomSheetProp = DiaryBottomSheetProp(
    userName = "hello",
    diaryContent = "",
    onCreateButtonClicked = {},
    onDiaryContentChanged = {}
)

@Preview
@Composable
fun PreviewDiaryBottomSheet() {
    DiaryBottomSheet(prop = previewDiaryBottomSheetProp)
}
