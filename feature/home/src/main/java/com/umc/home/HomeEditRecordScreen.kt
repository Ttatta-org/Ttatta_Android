package com.umc.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.rememberAsyncImagePainter
import com.umc.design.R as Res

@Composable
fun HomeEditRecordScreen() {
    var date by remember { mutableStateOf("2025년 01월 12일") }
    var footprint by remember { mutableStateOf("서울여자대학교 학생누리관 소원나무 앞") }
    var todayRecord by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) } // 이미지 상태

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Topbar를 Box의 상단에 고정
        Box(
            modifier = Modifier
                .fillMaxWidth()
//                .height(97.dp) // Topbar 높이 유지
                .background(Color.White) // 배경색 추가하여 스크롤 시 레이어 문제 방지
                .zIndex(1f) // 스크롤되는 콘텐츠보다 위에 위치
        ) {
            Topbar()
        }

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFEF6F2))
                .verticalScroll(rememberScrollState()) // 스크롤 가능하게 설정
                .padding(top = 97.dp, bottom = 38.dp) // topbar 높이만큼 padding 추가하여 가려지지 않게 설정
        ) {
            Spacer(Modifier.height(25.53.dp))

            // Date
            CommonText(label = "날짜", text = date, onTextChange = { date = it })
            Spacer(Modifier.height(17.dp))

            // Location
            CommonText(label = "나의 발자국", text = footprint, onTextChange = { footprint = it })
            Spacer(Modifier.height(17.dp))

            // Photo
            ImageUploadField(
                selectedImageUri = selectedImageUri,
                onImageSelected = { selectedImageUri = it }
            )
            Spacer(Modifier.height(17.dp))

            // Today's Record
            CommonTextField(label = "오늘의 기록", text = todayRecord, onTextChange = { todayRecord = it }, placeholder = "오늘을 기록해주세요")
            Spacer(Modifier.height(17.dp))

            // Edit Category
            CategoryField(initialCategory = "남자친구")
            Spacer(Modifier.height(58.dp))

            // Button
            Button(
                onClick = { /* 버튼 클릭 로직 */ },
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFCAD98)),
                modifier = Modifier
                    .height(50.dp)
                    .width(LocalConfiguration.current.screenWidthDp.dp - 190.dp)
                    .align(Alignment.CenterHorizontally)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(28.dp),
                        spotColor = Color(0xDE806E38),
                        ambientColor = Color(0xDE806E38),
                        clip = true
                    ),
            ) {
                Text(
                    text = "기록하기",
                    fontSize = 16.sp,
                    color = Color(0xFFFFFFFF),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun Topbar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(97.dp) // TopBar 높이
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.img_topbar),
            contentDescription = "Top Bar Background",
            modifier = Modifier.fillMaxSize()
        )

        // TopBar 요소
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 12.dp)
                .zIndex(1f), // 이미지 위에 아이콘 배치
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 로고 이미지
            Icon(
                painter = painterResource(id = R.drawable.img_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(30.dp),
                tint = Color.Unspecified // 원본 색 유지
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = painterResource(id = R.drawable.ic_locaad),
                contentDescription = "Location",
                tint = Color.Unspecified // Tint 효과 제거
            )

            Spacer(modifier = Modifier.width(16.dp))

            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search",
                tint = Color.Unspecified // Tint 효과 제거
            )
        }
    }
}

@Composable
fun CommonText(
    label: String,
    text: String,
    onTextChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 헤더 이미지
            Image(
                painter = painterResource(id = Res.drawable.ic_header_deco),
                contentDescription = "Header Decoration",
                modifier = Modifier
                    .size(width = 39.18.dp, height = 16.dp) // 이미지 크기 설정
            )

            // 텍스트
            Text(
                text = label,
                fontSize = 16.sp,
                color = Color(0xFFFF9681),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 6.82.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth() // 각 카드의 너비 설정
                .height(45.dp) // 카드 높이 설정
                .shadow(
                    elevation = 6.dp, // 그림자의 높이 조정
                    shape = RoundedCornerShape(28.dp), // 카드의 모서리 둥글기
                    spotColor = Color(0xDE806E38),
                    ambientColor = Color(0xDE806E38),
                    clip = true // 모서리가 잘리도록 설정
                ),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White // 카드 배경 색상 설정
            )
        ) {
            Text(
                text = text,
                color = Color(0xFFCACACA),
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(horizontal = 28.dp, vertical = 13.dp)
            )
        }
    }
}

@Composable
fun CommonTextField(
    label: String,
    text: String,
    onTextChange: (String) -> Unit,
    placeholder: String = ""
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 헤더 이미지
            Image(
                painter = painterResource(id = Res.drawable.ic_header_deco),
                contentDescription = "Header Decoration",
                modifier = Modifier
                    .size(width = 39.18.dp, height = 16.dp) // 이미지 크기 설정
            )

            // 텍스트
            Text(
                text = label,
                fontSize = 16.sp,
                color = Color(0xFFFF9681),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 6.82.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth() // 각 카드의 너비 설정
                .heightIn(min = 45.dp)
                .shadow(
                    elevation = 6.dp, // 그림자의 높이 조정
                    shape = RoundedCornerShape(28.dp), // 카드의 모서리 둥글기
                    spotColor = Color(0xDE806E38),
                    ambientColor = Color(0xDE806E38),
                    clip = true // 모서리가 잘리도록 설정
                )
                .wrapContentHeight(), // 컨텐츠 내용에 따라 높이 조정
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White // 카드 배경 색상 설정
            )
        ) {
//            TextField(
//                value = text,
//                onValueChange = { onTextChange(it) },
//                placeholder = {
//                    Text(
//                        text = placeholder,
//                        color = Color(0xFFCACACA),
//                        fontSize = 12.sp,
//                        modifier = Modifier
//                            .padding(horizontal = 28.dp, vertical = 12.5.dp)
//                    )
//                },
//                shape = RoundedCornerShape(16.dp),
//                colors = TextFieldDefaults.colors(
//                    focusedContainerColor = Color.Transparent,
//                    unfocusedContainerColor = Color.Transparent,
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent
//                ),
//                maxLines = Int.MAX_VALUE, // 여러 줄 입력 가능
//                minLines = 1, // 최소 한 줄부터 시작
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .heightIn(min = 45.dp)
//                    .padding(0.dp)
//            )
            Box( // Box로 감싸서 높이 조절
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp, vertical = 6.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (text.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color(0xFFCACACA),
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(vertical = 12.5.dp) // placeholder가 높이 늘리지 않도록 조정
                    )
                }
                TextField(
                    value = text,
                    onValueChange = { onTextChange(it) },
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    maxLines = Int.MAX_VALUE, // 여러 줄 입력 가능
                    minLines = 1, // 기본적으로 한 줄
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 45.dp) // 최소 높이 45dp 유지
                        .padding(0.dp)
                )
            }
        }
    }
}

@Composable
fun ImageUploadField(
    selectedImageUri: Uri?, // 부모에서 내려받은 선택된 이미지 URI
    onImageSelected: (Uri?) -> Unit // 선택한 이미지 URI를 부모에게 전달하는 콜백
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected(uri) // 선택한 이미지를 부모에 전달
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = Res.drawable.ic_header_deco),
                contentDescription = "Header Decoration",
                modifier = Modifier
                    .size(width = 39.18.dp, height = 16.dp)
            )

            Text(
                text = "기억하고 싶은 순간",
                fontSize = 16.sp,
                color = Color(0xFFFF9681),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 6.82.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .shadow(
                    elevation = 6.dp, // 그림자의 높이 조정
                    shape = RoundedCornerShape(28.dp), // 카드의 모서리 둥글기
                    spotColor = Color(0xDE806E38),
                    ambientColor = Color(0xDE806E38),
                    clip = true // 모서리가 잘리도록 설정
                )
                .background(Color(0xFFFFFFFF), shape = RoundedCornerShape(28.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(28.dp))
                        .clickable { imagePickerLauncher.launch("image/*") }
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(206.dp)
                        .padding(horizontal = 12.dp)
                        .background(Color(0xFFFEEAD9), shape = RoundedCornerShape(28.dp))
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_addphoto),
                            contentDescription = "Upload",
                            modifier = Modifier.size(19.dp),
                            tint = Color.Unspecified
                        )

                        Spacer(Modifier.height(3.dp))

                        Text(
                            text = "사진 한 장을 업로드해주세요",
                            fontSize = 12.sp,
                            color = Color(0xFFFF9681),
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryField(initialCategory: String) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(initialCategory) } // 선택한 카테고리 상태 추가
    var selectedIcon by remember { mutableStateOf(Res.drawable.ic_foot) } // 선택한 아이콘 상태 추가

    // 카테고리별 아이콘 매핑
    val categoryIcons = mapOf(
        "일상" to Res.drawable.ic_foot_red,
        "여행" to Res.drawable.ic_foot_blue,
        "운동" to Res.drawable.ic_foot_navy,
        "취미" to Res.drawable.ic_foot_pink,
        "기타" to Res.drawable.ic_foot_black
    )

    // 애니메이션 효과 추가 (부드러운 회전)
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
        label = "Toggle Rotation"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 헤더 이미지
            Image(
                painter = painterResource(id = Res.drawable.ic_header_deco),
                contentDescription = "Header Decoration",
                modifier = Modifier.size(width = 39.18.dp, height = 16.dp)
            )

            // 텍스트
            Text(
                text = "카테고리 수정",
                fontSize = 16.sp,
                color = Color(0xFFFF9681),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 6.82.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        // 선택된 카테고리 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(28.dp),
                    spotColor = Color(0xDE806E38),
                    ambientColor = Color(0xDE806E38),
                    clip = true
                )
                .clickable { isExpanded = !isExpanded },
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isExpanded) Color(0xFFFEEAD9) else Color(0xFFFFFFFF)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 12.dp)
            ) {
                // 선택한 카테고리의 아이콘 표시
                Icon(
                    painter = painterResource(id = selectedIcon),
                    contentDescription = "Selected Category Icon",
                    modifier = Modifier.size(21.dp),
                    tint = Color.Unspecified
                )

                Spacer(Modifier.width(11.17.dp))

                Text(
                    text = selectedCategory,
                    fontSize = 12.sp,
                    color = Color(0xFF4B4B4B),
                    fontWeight = FontWeight.Normal
                )

                Spacer(Modifier.weight(1f))

                // Toggle 아이콘
                Icon(
                    painter = painterResource(
                        id = if (isExpanded) R.drawable.ic_toggle_open else R.drawable.ic_toggle_closed
                    ),
                    contentDescription = "Toggle",
                    modifier = Modifier
                        .size(18.dp)
                        .rotate(rotationAngle),
                    tint = Color.Unspecified
                )
            }
        }

        // 카테고리 목록 (애니메이션 적용)
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFEEAD9))
                    .padding(horizontal = 25.dp, vertical = 8.dp)
            ) {
                categoryIcons.forEach { (categoryName, iconResId) ->
                    CategoryItem(name = categoryName, iconResId = iconResId, onClick = {
                        selectedCategory = categoryName
                        selectedIcon = iconResId // 선택한 카테고리에 맞는 아이콘 변경
                        isExpanded = false
                    })
                }
            }
        }
    }
}

@Composable
fun CategoryItem(name: String, iconResId: Int, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() } // 클릭 시 카테고리 변경
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = "$name Icon",
            modifier = Modifier.size(18.dp),
            tint = Color.Unspecified
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = name,
            fontSize = 14.sp,
            color = Color(0xFF4B4B4B)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeEditRecordScreen() {
    HomeEditRecordScreen()
}