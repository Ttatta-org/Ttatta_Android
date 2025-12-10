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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.R
import com.umc.design.component.CustomButton
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.LocalFontTheme

data class ChallengeBottomSheetProp(
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
    val isEnabled = prop.title.isNotBlank() && prop.content.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = bottomSheetShape,
            )
            .background(
                color = Color.White,
                shape = bottomSheetShape
            )
            .padding(horizontal = 22.dp)
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
            Box(
                modifier = Modifier.padding(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_header_deco), // 헤더 데코 이미지 리소스
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp) // 이미지 크기 설정
                )
            }
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(8.dp), // 간격 9dp 설정
//                modifier = Modifier
//                    .padding(24.dp)
//                    .fillMaxWidth()
//            ) {
//                Box(
//                    modifier = Modifier
//                        .weight(1f) // Row 내에서 남은 공간을 차지
//                        .border(
//                            width = 1.dp,
//                            color = Color(0xFFFCAD98),
//                            shape = RoundedCornerShape(28.dp)
//                        ) // 테두리 추가
//                        .background(
//                            color = Color.White,
//                            shape = RoundedCornerShape(28.dp) // 둥근 모서리 28dp
//                        )
//                        .padding(horizontal = 18.dp, vertical = 13.dp) // 내부 여백
//                ) {
//                    BasicTextField(
//                        value = prop.diaryContent,
//                        onValueChange = prop.onDiaryContentChanged,
//                        textStyle = TextStyle(
//                            fontSize = 13.sp,
//                            color = Color.Black
//                        ),
//                        modifier = Modifier.fillMaxWidth()
//                    ) { innerTextField ->
//                        innerTextField()
//                        if (prop.diaryContent.isEmpty()) Text(
//                            text = stringResource(id = R.string.record_placeholder), // 힌트 텍스트
//                            color = Color(0xFFCACACA),
//                            fontSize = 13.sp
//                        )
//                    }
//                }
//                IconButton(
//                    onClick = { if (prop.isButtonEnabled) prop.onCreateButtonClicked() },
//                    modifier = Modifier.size(48.dp)
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.btn_add),
//                        contentDescription = "Add",
//                        tint = if (prop.isButtonEnabled) Color.Unspecified else Color.Unspecified,
//                        modifier = Modifier.size(48.dp)
//                    )
//                }
//            }

            Column {
                Box(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = "챌린지 명",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600,
                        color = LocalColorTheme.current.grey[600],
                        textAlign = TextAlign.Center
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // 간격 9dp 설정
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = LocalColorTheme.current.primary[100],
                            shape = RoundedCornerShape(18.dp)
                        ) // 테두리 추가
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(18.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp) // 내부 여백
                ) {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier.weight(1f)
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

                    val count = prop.title.length
                    val countColor = if (count == 0)
                        LocalColorTheme.current.grey[400]
                    else
                        LocalColorTheme.current.primary[500]

                    Row {
                        Text(
                            text = count.toString(),
                            style = TextStyle(
                                fontSize = 13.sp,
                                color = countColor,
                                textAlign = TextAlign.Center
                            ),
                        )
                        Text(
                            text = "/20",
                            style = TextStyle(
                                fontSize = 13.sp,
                                color = LocalColorTheme.current.grey[400],
                                textAlign = TextAlign.Center
                            ),
                        )
                    }
                }

                Box(
                    modifier = Modifier.padding(top = 20.dp, start = 16.dp)
                ) {
                    Text(
                        text = "내용",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600,
                        color = LocalColorTheme.current.grey[600],
                        textAlign = TextAlign.Center,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // 간격 9dp 설정
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = LocalColorTheme.current.primary[100],
                            shape = RoundedCornerShape(18.dp)
                        ) // 테두리 추가
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(18.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp) // 내부 여백
                ) {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier.weight(1f)
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
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, bottom = 20.dp)
                        .clickable { prop.onPastChallengeClick() }
                ) {
                    Text(
                        text = "지난 챌린지를 보러가볼까요?",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W400,
                            color = LocalColorTheme.current.primary[300],
                            textAlign = TextAlign.Center,
                            textDecoration = TextDecoration.Underline
                        ),
                    )
                }

                CustomButton(
                    text = "챌린지 생성하기",
                    isEnabled = isEnabled,
                    onClick = prop.onCreateButtonClicked
                )

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

val previewChallengeBottomSheetProp = ChallengeBottomSheetProp(
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
    NewChallengeBottomSheet(prop = previewChallengeBottomSheetProp)
}
