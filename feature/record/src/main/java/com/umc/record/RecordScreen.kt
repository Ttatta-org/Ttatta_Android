package com.umc.record

import android.util.Log
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
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.geometry.Offset
import coil3.compose.rememberAsyncImagePainter
import com.umc.design.R as Res

@Composable
fun RecordScreen(
    categories: List<Pair<String, String>>,
    selectedCategory: String,
    diaryText: String,
    selectedImage: String?,
    userName: String,
    location: String,
    latitude: Double,  // 현재 위치 정보
    longitude: Double,
    onCategorySelect: (String) -> Unit,
    onDiaryTextUpdate: (String) -> Unit,
    onSaveDiary: () -> Unit,
    onEditLocation: (String) -> Unit
) {
    var showCustomDialog by remember { mutableStateOf(false) }
    var location by remember { mutableStateOf("Cafe PORTE") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 배경 이미지 추가
//        Image(
//            painter = painterResource(id = R.drawable.img_recordbackground), // 배경 이미지 리소스
//            contentDescription = "Background Image",
//            contentScale = ContentScale.Crop, // 이미지를 화면에 꽉 차게 조정
//            modifier = Modifier.fillMaxSize()
//        )
        Log.d("RecordScreen", "selectedImage: ${ selectedImage.toString() }")

        // 선택한 배경 이미지가 있으면 표시, 없으면 기본 배경 사용
        if (selectedImage != null) {
            Image(
                painter = rememberAsyncImagePainter(selectedImage),  // 선택한 이미지 표시
                contentDescription = "Selected Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.img_recordbackground),
                contentDescription = "Background Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            verticalArrangement = Arrangement.Top, // 맨 위로 배치
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 상단 정보 섹션
            InfoSection(
                date = "2025.02.01",
                location = location,  // 추후 지도에서 저장한 데이터로 교체 필요
                category = selectedCategory,
                onLocationClick = { // 위치 클릭 시 이벤트 발생
                    println("📌 RecordScreen: onEditLocation() called!") // ✅ 로그 추가
                    onEditLocation(location)
                },
                onIconClick = { showCustomDialog = !showCustomDialog } // 다이얼로그 상태 변경
            )
        }

        // 바텀 시트 -> 추후 사용자명 받아와 교체 필요
        RecordBottomSheet(
            name = userName,
            diaryText = diaryText,
            selectedCategory = selectedCategory,
            selectedImage = selectedImage,
            location = location,
            onDiaryTextUpdate = onDiaryTextUpdate,
            onSaveDiary = onSaveDiary
        )

        // 다이얼로그
        if (showCustomDialog) {
            CustomCategoryDialog(
                categories = categories,
                onDismiss = { showCustomDialog = false },
                onCategorySelected = { category ->
                    onCategorySelect(category)
                    showCustomDialog = false
                }
            )
        }
    }
}

@Composable
fun InfoSection(
    date: String,
    location: String,
    category: String,
    onLocationClick: () -> Unit,
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
            //.widthIn()
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
            onClick = {
                println("✅ InfoSection: onLocationClick() called!") // ✅ 추가된 로그
                onLocationClick()
            },
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
    leadingIcon: (@Composable (() -> Unit))? = null,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null // 클릭 이벤트 추가
) {
    Box(
        modifier = Modifier
            .clickable {
                println("🔥 InfoTag clicked!")
                if (onClick == null) {
                    println("🚨 onClick is NULL in InfoTag!") // ✅ 추가된 로그
                } else {
                    println("✅ onClick is NOT NULL, calling function!") // ✅ 추가된 로그
                    onClick.invoke()
                }
                onClick?.invoke()
            }
            .background(backgroundColor, shape = RoundedCornerShape(15.dp))
            .padding(horizontal = 15.5.dp, vertical = 4.5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
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

}

@Composable
fun RecordBottomSheet(
    name: String,
    diaryText: String,
    selectedCategory: String,
    selectedImage: String?,
    location: String,
    onDiaryTextUpdate: (String) -> Unit,
    onSaveDiary: () -> Unit
) {
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
                            onValueChange = {
                                inputText = it
                                onDiaryTextUpdate(it)
                            }, // ViewModel에 업데이트
                            textStyle = TextStyle(
                                fontSize = 13.sp,
                                color = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }

                    // 추가 버튼 -> 클릭 이벤트 처리 해야함
                    Button(
                        onClick = { onSaveDiary() }, // 저장 함수 실행
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFCAD98)),
                        modifier = Modifier
                            .height(50.dp)
                            .fillMaxWidth(0.8f)
                    ) {
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
}

//@Composable
//fun CustomCategoryDialog(
//    onDismiss: () -> Unit,
//    categories: List<Pair<String, String>> // 외부에서 받아오는 카테고리 데이터
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .clickable { onDismiss() }, // 바깥 클릭 시 닫기
//        contentAlignment = Alignment.TopCenter
//    ) {
//        // 배경 이미지
//        Box(
//            modifier = Modifier
//                .wrapContentSize()
//                .padding(start = 90.dp, top = 90.dp) // 상단 여백 설정
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.img_catebackground), // 배경 이미지 리소스
//                contentDescription = "Background Image",
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .wrapContentHeight(), // 높이는 내용에 맞춤
//                contentScale = ContentScale.Fit // 배경 이미지를 맞춤
//            )
//
//            // 카테고리 리스트
//            Column(
//                modifier = Modifier
//                    .padding(horizontal = 60.dp, vertical = 12.dp) // 배경 안쪽 여백
//            ) {
//                categories.forEach { (name, colorKey) ->
//                    val (backgroundColor, iconRes) = getCategoryStyle(colorKey) // 카테고리 스타일
//
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .widthIn(max = 100.dp) // 카테고리 항목 너비 제한
//                            .padding(vertical = 4.dp) // 항목 간격
//                    ) {
//                        Icon(
//                            painter = painterResource(id = iconRes),
//                            contentDescription = name,
//                            modifier = Modifier.size(24.dp),
//                            tint = Color.Unspecified
//                        )
//
//                        Spacer(modifier = Modifier.width(11.17.dp)) // 아이콘과 텍스트 간격
//
//                        Text(
//                            text = name,
//                            fontSize = 12.sp,
//                            color = Color.Black
//                        )
//                    }
//                }
//
//                // "발자국 새로 만들기" 항목
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 8.dp)
//                        .clickable { /* 새 발자국 생성 로직 추가 */ }
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_foot_new),
//                        contentDescription = "발자국 새로 만들기",
//                        modifier = Modifier.size(24.dp),
//                        tint = Color.Unspecified
//                    )
//
//                    Spacer(modifier = Modifier.width(16.dp))
//
//                    Text(
//                        text = "발자국 새로 만들기",
//                        fontSize = 12.sp,
//                        color = Color(0xFFFF9681)
//                    )
//                }
//            }
//        }
//    }
//}

@Composable
fun CustomCategoryDialog(
    categories: List<Pair<String, String>>, // 외부에서 받아오는 카테고리 데이터
    onDismiss: () -> Unit,
    onCategorySelected: (String) -> Unit // 선택된 카테고리를 부모 컴포넌트에 전달
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDismiss() } // 다이얼로그 외부 클릭 시 닫기
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .wrapContentSize()
                .padding(start = 135.dp, top = 90.dp) // 상단 여백 설정
        ) {
            // 다이얼로그 상단 이미지
            Image(
                painter = painterResource(R.drawable.img_categorytop),
                contentDescription = "Category Dialog Top",
                modifier = Modifier.wrapContentSize()
            )

            // 카테고리 목록 영역
            Box(
                modifier = Modifier
                    .background(Color(0xE6FEF6F2)) // 반투명 효과 및 둥근 모서리
                    .padding(start = 16.dp, end = 16.dp) // 내부 여백
                    .fillMaxWidth(0.8f) // 다이얼로그 크기 조정
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    categories.forEachIndexed() { index, (name, colorKey) ->
                        val (_, iconRes) = getCategoryStyle(colorKey)

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onCategorySelected(colorKey) // 선택된 카테고리 업데이트
                                        onDismiss() // 다이얼로그 닫기
                                    }
                                    .padding(horizontal = 12.dp, vertical = 3.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = name,
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.Unspecified
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = name,
                                    fontSize = 14.sp,
                                    color = Color(0xFF000000),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // 점선 추가
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 2.dp) // 카테고리와 점선 간격 2dp
                                    .height(1.dp) // 점선 높이 설정
                            ) {
                                val dotSize = 5f // 점선 길이
                                val spaceSize = 5f // 점선 간 간격
                                val strokeWidth = 3f // 점선 두께
                                val startX = 0f
                                val endX = size.width

                                var currentX = startX
                                while (currentX < endX) {
                                    drawLine(
                                        color = Color(0xFFFCAD98), // 점선 색상
                                        start = Offset(currentX, size.height / 2),
                                        end = Offset(currentX + dotSize, size.height / 2),
                                        strokeWidth = strokeWidth
                                    )
                                    currentX += dotSize + spaceSize
                                }
                            }
                        }
                    }

                    // "발자국 새로 만들기" 버튼
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .clickable { /* 새 발자국 생성 로직 추가 */ }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_foot_new),
                            contentDescription = "발자국 새로 만들기",
                            modifier = Modifier
                                .size(20.dp),
                            tint = Color.Unspecified
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "발자국 새로 만들기",
                            fontSize = 14.sp,
                            color = Color(0xFFFF9681),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 다이얼로그 하단 이미지
            Image(
                painter = painterResource(R.drawable.img_categorybottom),
                contentDescription = "Category Dialog Bottom",
                modifier = Modifier.wrapContentSize()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRecordScreen() {
    val sampleCategories = listOf(
        "친구들" to "Red",
        "가족" to "Blue",
        "남자친구" to "Pink",
        "일상" to "Yellow",
        "다시 오고 싶은 장소" to "Green",
        "제주여행" to "Turquoise"
    )

    var selectedCategory by remember { mutableStateOf("default") }
    var diaryText by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf<String?>(null) }
    val userName = "테스트 사용자"

    RecordScreen(
        categories = sampleCategories,
        selectedCategory = selectedCategory,
        diaryText = diaryText,
        selectedImage = selectedImage,
        userName = userName,
        onCategorySelect = { selectedCategory = it },
        onDiaryTextUpdate = { diaryText = it },
        onEditLocation = {}
    )
}