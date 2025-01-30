package com.umc.record

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.size.Size
import android.content.Context
import com.umc.design.R as Res

@Composable
fun RecordScreen() {
    var showCustomDialog by remember { mutableStateOf(true) } // 다이얼로그 초기 상태 off

    val categories = listOf(
        "친구들" to "Red",
        "가족" to "Blue",
        "남자친구" to "Pink",
        "일상" to "Yellow",
        "다시 오고 싶은 장소" to "Green",
        "제주여행" to "Turquoise"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 배경 이미지 추가
        Image(
            painter = painterResource(id = R.drawable.img_recordbackground), // 배경 이미지 리소스
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop, // 이미지를 화면에 꽉 차게 조정
            modifier = Modifier.fillMaxSize()
        )

        Column(
            verticalArrangement = Arrangement.Top, // 맨 위로 배치
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 상단 정보 섹션
            InfoSection(
                date = "2025.01.20",
                location = "Cafe PORTE",
                category = "default", // 카테고리 예시
                onIconClick = { showCustomDialog = !showCustomDialog } // 다이얼로그 상태 변경
            )
        }

        // 바텀 시트
        RecordBottomSheet(name = "서연")

        // 다이얼로그
        if (showCustomDialog) {
            CustomCategoryDialog(
                categories = categories,
                onDismiss = { showCustomDialog = false }
//                onCategorySelected = { selectedCategory ->
//                    if (selectedCategory == "new") {
//                        // 새 발자국 생성 로직
//                    } else {
//                        // 선택된 기존 카테고리 처리
//                    }
//                }
            )
        }
    }
}

@Composable
fun InfoSection(
    date: String,
    location: String,
    category: String,
    onIconClick: () -> Unit // 발자국 아이콘 클릭 콜백
) {
    val (backgroundColor, iconRes) = getCategoryStyle(category) // 배경색과 아이콘 반환

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .padding(top = 60.dp)
    ) {
        // 날짜
        InfoTag(
            text = date,
            backgroundColor = Color(0xE6FEF6F2),
            textColor = Color(0xFFFF9681)
        )

        // 위치
        InfoTag(
            text = location,
            backgroundColor = Color(0xE6FEF6F2),
            textColor = Color(0xFFFF9681),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_location),
                    contentDescription = "Location Icon",
                    tint = Color(0xFFFF9681),
                    modifier = Modifier.size(16.dp)
                )
            }
        )

        // 발자국 아이콘
        Box(
            modifier = Modifier
                .background(backgroundColor, shape = RoundedCornerShape(15.dp))
                .size(width = 38.dp, height = 25.dp)
                .clickable { onIconClick() }, // 클릭 시 콜백 호출
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = "Category Icon",
                tint = Color.Unspecified,
                modifier = Modifier.size(width = 22.05.dp, height = 19.07.dp)
            )
        }
    }
}

@Composable
fun getCategoryStyle(category: String): Pair<Color, Int> {
    return when (category) {
        "Red" -> Pair(Color(0xE6FFD7D7), Res.drawable.ic_foot_red)
        "Orange" -> Pair(Color(0xE6FFE0D3), Res.drawable.ic_foot_orange)
        "Yellow" -> Pair(Color(0xE6FFF0D2), Res.drawable.ic_foot_yellow)
        "Green" -> Pair(Color(0xE6E4F5D6), Res.drawable.ic_foot_green)
        "Turquoise" -> Pair(Color(0xE6E4F4F2), Res.drawable.ic_foot_turquoise)
        "Blue" -> Pair(Color(0xE6E0EFF8), Res.drawable.ic_foot_blue)
        "Navy" -> Pair(Color(0xE6D7DFF5), Res.drawable.ic_foot_navy)
        "Purple" -> Pair(Color(0xE6F2E1FF), Res.drawable.ic_foot_purple)
        "Brown" -> Pair(Color(0xE6EACFC0), Res.drawable.ic_foot_brown)
        "White" -> Pair(Color(0xE6FFFFFF), Res.drawable.ic_foot_white)
        "Pink" -> Pair(Color(0xE6FFA6C3), Res.drawable.ic_foot_pink)
        "Black" -> Pair(Color(0xE69D9D9D), Res.drawable.ic_foot_black)
        "default" -> Pair(Color(0xE6FDDDC1), R.drawable.ic_foot_default) // 기본값 처리
        else -> Pair(Color(0xE6FDDDC1), R.drawable.ic_foot_default)    // 기본 배경 및 아이콘
    }
}

@Composable
fun InfoTag(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    leadingIcon: (@Composable (() -> Unit))? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(backgroundColor, shape = RoundedCornerShape(15.dp))
            .padding(horizontal = 15.5.dp, vertical = 4.5.dp)
    ) {
        leadingIcon?.invoke()
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = textColor
        )
    }
}

@Composable
fun RecordBottomSheet(name: String) {
    Box(
        modifier = Modifier
            .fillMaxSize() // 전체 화면 크기 차지
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight() // 컨텐츠 내용에 따라 높이 조정
                .align(Alignment.BottomCenter) // 화면 하단 중앙에 고정
                .background(
                    color = Color(0xFFFEF6F2), // 배경색
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp) // 상단 모서리 둥글게
                )
                .padding(WindowInsets.navigationBars.asPaddingValues()) // 내비게이션 바 높이만큼 여백 추가
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp) // 여백 추가
            ) {
                // 헤더 이미지
                Image(
                    painter = painterResource(id = Res.drawable.ic_header_deco), // 헤더 데코 이미지 리소스
                    contentDescription = "Header Decoration",
                    modifier = Modifier
                        .size(width = 39.18.dp, height = 32.dp) // 이미지 크기 설정
                        .padding(bottom = 8.dp) // 텍스트와 간격 추가
                )

                // 안내 텍스트
                Text(
                    text = "${name}님 이곳에 기록을 남겨주세요",
                    fontSize = 16.sp,
                    color = Color(0xFFFF9681),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                // 텍스트 입력 공간과 버튼 한 줄 배열
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp), // 간격 9dp 설정
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 22.dp) // 입력 필드 하단에 22dp 여백 추가
                ) {
                    // 텍스트 입력 공간
                    var inputText by remember { mutableStateOf("") }

                    Box(
                        modifier = Modifier
                            .weight(1f) // Row 내에서 남은 공간을 차지
                            .border(
                                1.dp,
                                Color(0xFFFCAD98),
                                shape = RoundedCornerShape(28.dp)
                            ) // 테두리 추가
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(28.dp) // 둥근 모서리 28dp
                            )
                            .padding(horizontal = 18.dp, vertical = 10.dp) // 내부 여백
                            .wrapContentHeight()
                    ) {
                        if (inputText.isEmpty()) {
                            Text(
                                text = "일기 내용을 작성해주세요", // 힌트 텍스트
                                color = Color(0xFFCACACA),
                                fontSize = 13.sp
                            )
                        }

                        BasicTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            textStyle = TextStyle(
                                fontSize = 13.sp,
                                color = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }

                    // 추가 버튼
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add), // 리소스 파일의 추가 버튼
                        contentDescription = "Add",
                        tint = Color.Unspecified, // Tint 효과 제거
                        modifier = Modifier.size(width = 42.94.dp, height = 40.dp) // 아이콘 크기 설정
                    )
                }
            }
        }
    }
}

@Composable
fun CustomCategoryDialog(
    onDismiss: () -> Unit,
    categories: List<Pair<String, String>> // 외부에서 받아오는 카테고리 데이터
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onDismiss() }, // 바깥 클릭 시 닫기
        contentAlignment = Alignment.TopCenter
    ) {
        // 배경 이미지
        Box(
            modifier = Modifier
                .wrapContentSize()
                .padding(start = 90.dp, top = 90.dp) // 상단 여백 설정
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_catebackground), // 배경 이미지 리소스
                contentDescription = "Background Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(), // 높이는 내용에 맞춤
                contentScale = ContentScale.Fit // 배경 이미지를 맞춤
            )

            // 카테고리 리스트
            Column(
                modifier = Modifier
                    .padding(horizontal = 60.dp, vertical = 12.dp) // 배경 안쪽 여백
            ) {
                categories.forEach { (name, colorKey) ->
                    val (backgroundColor, iconRes) = getCategoryStyle(colorKey) // 카테고리 스타일

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 100.dp) // 카테고리 항목 너비 제한
                            .padding(vertical = 4.dp) // 항목 간격
                    ) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = name,
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )

                        Spacer(modifier = Modifier.width(11.17.dp)) // 아이콘과 텍스트 간격

                        Text(
                            text = name,
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    }
                }

                // "발자국 새로 만들기" 항목
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { /* 새 발자국 생성 로직 추가 */ }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_foot_new),
                        contentDescription = "발자국 새로 만들기",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "발자국 새로 만들기",
                        fontSize = 12.sp,
                        color = Color(0xFFFF9681)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRecordScreen() {
    RecordScreen()
}