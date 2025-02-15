package com.umc.home

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.umc.core.model.Diary
import com.umc.home.components.TopBarComponent_recordEditPage
import com.umc.home.utils.formatToKorean
import com.umc.home.utils.getFileFromUri
import java.io.File
import android.content.Context
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.umc.design.R as Res

@Composable
fun HomeEditRecordScreen(
    diary: Diary, // ✅ 클릭된 다이어리 정보
    //viewModel: HomeViewModel, // ViewModel 전달받기
    navController: NavHostController, // NavController 전달받기
    onModifyDiary: (Long, String, File?) -> Unit
) {
    var todayRecord by remember { mutableStateOf(diary.content) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(Uri.parse(diary.imageUrl)) }
    //var selectedCategory by remember { mutableStateOf(diary.category) } // ✅ 기존 카테고리 표시
    var selectedCategory by remember { mutableStateOf("일상") } // ✅ 여기서 기본값을 설정

    val context = LocalContext.current


    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f) // ✅ BottomNavigation을 밀어내지 않도록 LazyColumn에 weight 적용
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFFEF6F2))
                        .verticalScroll(rememberScrollState()) // 스크롤 가능하게 설정
                        .padding(top = 90.5.dp, bottom = 38.dp) // topbar 높이만큼 padding 추가하여 가려지지 않게 설정
                ) {
                    // Date (변경 불가)
                    CommonText(
                        label = "날짜",
                        text = diary.date.formatToKorean(),
                        onTextChange = { }
                    )
                    Spacer(Modifier.height(17.dp))

                    // Location (변경 불가)
                    CommonText(
                        label = "나의 발자국",
                        text = diary.locationName,
                        onTextChange = { }
                    )
                    Spacer(Modifier.height(17.dp))

                    // Photo (기존 데이터 받아오기)
                    ImageUploadField(
                        selectedImageUri = selectedImageUri,
                        onImageSelected = { selectedImageUri = it }
                    )
                    Spacer(Modifier.height(17.dp))

                    // Today's Record (기존 데이터 받아오기)
                    CommonTextField(
                        label = "오늘의 기록",
                        text = todayRecord,
                        onTextChange = { todayRecord = it },
                        placeholder = "오늘을 기록해주세요"
                    )

                    Spacer(Modifier.height(17.dp))

                    // Edit Category (기존 데이터 받아오기)
                    CustomCategoryField(
                        initialCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it }
                    )
                    Spacer(Modifier.height(58.dp))

                    // Button
                    Button(
                        onClick = {
                            val imageFile = selectedImageUri?.let { uri -> getFileFromUri(context, uri) }
                            //val imageFile = selectedImageUri?.let { uri -> File(uri.path!!) })

                            // ✅ 이미지 수정 안 할 경우 null 전달
                            onModifyDiary(diary.id, todayRecord, imageFile)
                            //navController.popBackStack()
                        },
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFCAD98)),
                        modifier = Modifier
                            .height(45.dp)
                            .width(200.dp)
                            .align(Alignment.CenterHorizontally)
                            .shadow(
                                elevation = 6.dp,
                                shape = RoundedCornerShape(28.dp),
                                spotColor = Color(0xFFDE806E),
                                ambientColor = Color(0xFFDE806E),
                                clip = true
                            ),
                    ) {
                        Text(
                            text = "기록하기",
                            fontSize = 17.sp,
                            color = Color(0xFFFFFFFF),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                TopBarComponent_recordEditPage()
            }
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
                .shadow(
                    elevation = 6.dp, // 그림자의 높이 조정
                    shape = RoundedCornerShape(28.dp), // 카드의 모서리 둥글기
                    spotColor = Color(0xFFDE806E),
                    ambientColor = Color(0xFFDE806E),
                    clip = true // 모서리가 잘리도록 설정
                )
                .background(
                    color = Color(0xFFFFFFFF),
                    shape = RoundedCornerShape(20.dp)
                )
                .fillMaxWidth() // 각 카드의 너비 설정
                .height(45.dp), // 카드 높이 설정
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White // 카드 배경 색상 설정
            )
        ) {
            Row(
                modifier = Modifier.fillMaxSize(), // Card의 크기에 맞춤
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = text,
                    color = Color(0xFF4B4B4B),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
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

        Box(
            modifier = Modifier
                .shadow(
                    elevation = 6.dp, // 그림자의 높이 조정
                    shape = RoundedCornerShape(28.dp), // 카드의 모서리 둥글기
                    spotColor = Color(0xFFDE806E),
                    ambientColor = Color(0xFFDE806E),
                    clip = true // 모서리가 잘리도록 설정
                )
                .background(
                    color = Color(0xFFFFFFFF),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .heightIn(27.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier.fillMaxSize(), // Card의 크기에 맞춤
                contentAlignment = Alignment.Center, // 중앙 정렬
            ) {
                // Placeholder 텍스트를 기본 텍스트처럼 보이게
                if (text.isEmpty()) {
                    Text(
                        text = "오늘을 기록해주세요",
                        fontSize = 14.sp,
                        color = Color(0xFF4B4B4B)
                    )
                }

                BasicTextField(
                    value = text,
                    onValueChange = { onTextChange(it) },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color(0xFF4B4B4B),
                        fontSize = 14.sp
                    ),
                    singleLine = false,
                    maxLines = Int.MAX_VALUE, // 여러 줄 입력 가능
                    minLines = 1, // 기본적으로 한 줄
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp)
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
                .heightIn(max = 330.dp)
                .shadow(
                    elevation = 6.dp, // 그림자의 높이 조정
                    shape = RoundedCornerShape(28.dp), // 카드의 모서리 둥글기
                    spotColor = Color(0xFFDE806E),
                    ambientColor = Color(0xFFDE806E),
                    clip = true // 모서리가 잘리도록 설정
                )
                .background(Color(0xFFFFFFFF), shape = RoundedCornerShape(28.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .background(Color.White, shape = RoundedCornerShape(28.dp))
                ){
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = "Selected Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(15.dp))
                            .clickable { imagePickerLauncher.launch("image/*") }
                    )

                    // 검정색 반투명 오버레이
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f), shape = RoundedCornerShape(15.dp)) // 투명도 30% 적용
                    )

                    // 가운데 아이콘과 텍스트
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_addphoto), // 아이콘 리소스
                            contentDescription = "Add Photo",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp) // 아이콘 크기
                        )
                        Spacer(modifier = Modifier.height(8.dp)) // 아이콘과 텍스트 간격
                        Text(
                            text = "교체하고 싶은 사진 한 장을\n업로드해주세요",
                            color = Color.White,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

            } else {
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .background(Color.White, shape = RoundedCornerShape(28.dp))
                ){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(206.dp)
                            .padding(horizontal = 12.dp)
                            .background(Color(0xFFFEEAD9), shape = RoundedCornerShape(15.dp))
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
}

@Composable
fun CustomCategoryField(
    initialCategory: String,
    onCategorySelected: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(initialCategory) }


    // 사용자가 선택한 아이콘을 기억하도록 상태 저장
    var selectedIcon by remember { mutableIntStateOf(Res.drawable.ic_foot_red) }

    val categoryIcons = mapOf(
        "일상" to Res.drawable.ic_foot_red,
        "여행" to Res.drawable.ic_foot_blue,
        "운동" to Res.drawable.ic_foot_navy,
        "취미" to Res.drawable.ic_foot_pink,
        "남자친구" to Res.drawable.ic_foot_orange
    )

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
            Image(
                painter = painterResource(id = Res.drawable.ic_header_deco),
                contentDescription = "Header Decoration",
                modifier = Modifier.size(width = 39.18.dp, height = 16.dp)
            )

            Text(
                text = "카테고리 수정",
                fontSize = 16.sp,
                color = Color(0xFFFF9681),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 6.82.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        // 선택된 카테고리 카드 -> 높이 추후 수정
        Card(
            modifier = Modifier
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(28.dp),
                    spotColor = Color(0xFFDE806E),
                    ambientColor = Color(0xFFDE806E),
                    clip = true
                )
                .background(Color(0xFFFFFFFF), shape = RoundedCornerShape(28.dp))
                .fillMaxWidth()
                .height(45.dp)
                .clickable { isExpanded = !isExpanded },
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isExpanded) Color(0xFFFEEAD9) else Color(0xFFFFFFFF)
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = selectedIcon),
                    contentDescription = "Selected Category Icon",
                    modifier = Modifier.size(21.dp),
                    tint = Color.Unspecified
                )

                Spacer(Modifier.width(11.17.dp))

                Text(
                    text = selectedCategory,
                    fontSize = 14.sp,
                    color = Color(0xFF4B4B4B),
                    fontWeight = FontWeight.Normal
                )

                Spacer(Modifier.weight(1f))

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

        Spacer(Modifier.height(9.dp))

        // 카테고리 목록 (펼쳐지는 애니메이션 수정, 그림자 적용 필요)
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            Column(
                horizontalAlignment = Alignment.End, // 오른쪽 정렬
                modifier = Modifier.fillMaxWidth()
            ) {
                // 다이얼로그 상단 이미지
                Image(
                    painter = painterResource(R.drawable.img_categorytopwhite),
                    contentDescription = "Category Dialog Top",
                    modifier = Modifier
                        .align(Alignment.End)
                        .width(220.dp)
                )

                // 카테고리 목록 영역 (오른쪽 정렬)
                Box(
                    modifier = Modifier
                        .align(Alignment.End) // 오른쪽으로 배치
                        .width(220.dp) // 너비 조정
                        .background(Color.White, shape = RoundedCornerShape(0.dp))
                        .padding(horizontal = 16.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        categoryIcons.forEach { (categoryName, iconResId) ->
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedCategory = categoryName
                                            selectedIcon = iconResId
                                            isExpanded = false
                                            onCategorySelected(categoryName)
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = iconResId),
                                        contentDescription = categoryName,
                                        modifier = Modifier.size(20.dp),
                                        tint = Color.Unspecified
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Text(
                                        text = categoryName,
                                        fontSize = 14.sp,
                                        color = Color(0xFF000000),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                // 점선 추가
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .padding(top = 2.dp)
                                ) {
                                    val dotSize = 5f
                                    val spaceSize = 5f
                                    val strokeWidth = 3f
                                    val startX = 0f
                                    val endX = size.width

                                    var currentX = startX
                                    while (currentX < endX) {
                                        drawLine(
                                            color = Color(0xFFFDDDC1),
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
                                painter = painterResource(id = R.drawable.ic_new_category),
                                contentDescription = "발자국 새로 만들기",
                                modifier = Modifier.size(20.dp),
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
                    painter = painterResource(R.drawable.img_categorybottomwhite),
                    contentDescription = "Category Dialog Bottom",
                    modifier = Modifier.align(Alignment.End).width(220.dp)
                )
            }
        }
    }
}
