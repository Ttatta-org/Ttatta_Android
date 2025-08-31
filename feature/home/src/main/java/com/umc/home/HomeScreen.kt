package com.umc.home

import android.app.Activity
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalDragOrCancellation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.core.model.Diary
import com.umc.home.components.BottomNavigationBarWithFAB
import com.umc.home.components.TopBarComponent
import com.umc.home.HomeViewModel
import com.umc.home.components.SearchBar
import com.umc.home.utils.formatToKorean
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import coil3.compose.AsyncImage
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.zIndex
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(
    isLoading : Boolean,
    // HomeApp에서 전달받은 데이터와 콜백들
    navController: NavHostController,
    diaryList: List<Diary>,
    lazyListState: LazyListState,
    isExpanded: Boolean,
    isSearchVisible: Boolean,
    isSearchTriggered: Boolean,
    isCalendarVisible: Boolean,
    searchResults: List<Diary>,
    searchQuery: String,
    recentSearches: List<String>,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onSearchToggle: () -> Unit,
    onCalendarToggle: () -> Unit,
    onRecentSearchClick: (String) -> Unit,
    onFabClick: () -> Unit,
    onNavigateToFilteredDiaryScreen: (LocalDate) -> Unit,
    onShowDetailModal: () -> Unit,
    onDismissDetailModal: () -> Unit,
    isDetailModalVisible: Boolean,
    onDeleteDiary: (Long) -> Unit,
    allDiaryDates: List<LocalDate>
) {

    val systemUiController = rememberSystemUiController()
    val backgroundColor = Color(0xFFFFFFFF) // 상태바 배경색 (배경과 맞춤)

    SideEffect {
        systemUiController.setStatusBarColor(
            color = backgroundColor, // ✅ 상태바를 앱 배경색과 동일하게 설정
        )
    }
//    val uiState by viewModel.uiState.collectAsState()
//    val searchResults by viewModel.searchResults.collectAsState()
//
//    // 캘린더가 보이는지 여부를 관리하는 상태
//    var isCalendarVisible by remember { mutableStateOf(false) }
//    var isSearchVisible by remember { mutableStateOf(false) }
//
//    val recentSearches by viewModel.recentSearches.collectAsState() // ✅ ViewModel의 최근 검색어 사용
//    val searchQuery by viewModel.searchQuery.collectAsState() // ✅ ViewModel의 검색어 사용

    // 드래그 버튼의 상태 (ic_bottom_arrow 또는 ic_top_arrow)
    val dragIcon = when {
        isSearchVisible -> R.drawable.ic_top_arrow // 검색 상태에서는 아래로 화살표
        isCalendarVisible -> R.drawable.ic_top_arrow // 캘린더가 보이는 상태
        else -> R.drawable.ic_bottom_arrow // 기본 상태
    }

//    // TopBar 확장 여부
//    var isExpanded by remember { mutableStateOf(false) }
//    // 디테일 모달의 표시 여부 상태 관리
//    var isDetailModalVisible by remember { mutableStateOf(false) }
//    // 🔹 검색 실행 여부를 추적하는 변수
//
    var topBarHeight by remember { mutableStateOf(65.dp) }

//    var isSearchTriggered by remember { mutableStateOf(false) } // 🔹 검색 버튼이 눌렸는지 여부를 저장하는 상태 변수

    var selectedDiaryId by remember { mutableStateOf<Long?>(null) }

//    val view = LocalView.current
//    val density = LocalDensity.current
//    val context = LocalContext.current
//    val activity = context as? Activity // ✅ 현재 Activity 가져오기
//    val window = activity?.window
//
//    // ✅ 네비게이션 바(소프트키) 높이 가져오기
//    val systemBarsHeight = with(density) {
//        val insets = ViewCompat.getRootWindowInsets(view)
//            ?.getInsets(WindowInsetsCompat.Type.systemBars())
//        insets?.bottom?.toDp() ?: 0.dp
//    }
//    if (isSearchVisible || isCalendarVisible) {
//        window?.navigationBarColor = 0x80FBDDC8.toInt()
//    }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ✅ 1. TopBarComponent (항상 상단에 고정)
            Box(
                modifier = Modifier
                    .weight(1f) // ✅ LazyColumn이 BottomNavigation을 밀어내지 않도록 가변 높이 적용
                    //.background(Color(0xFFFEF6F2)) // ✅ 부드러운 배경색 추가
            ) {
                // ✅ 캘린더 또는 검색창이 열렸을 때만 배경을 블러 처리
                if (isSearchVisible || isCalendarVisible) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFFBDDC8).copy(alpha = 0.1f),
                                        Color(0xFFFBDDC8).copy(alpha = 0.3f),
                                        Color(0xFFFBDDC8).copy(alpha = 0.5f),
                                        Color(0xFFFBDDC8).copy(alpha = 0.7f),
                                        Color(0xFFFEDDC8).copy(alpha = 0.85f)  // ✅ 부드러운 그라디언트 유지
                                    ),
                                    startY = 0f,
                                    endY = Float.POSITIVE_INFINITY
                                )
                            )
                            .blur(50.dp) // ✅ 블러 강도를 높여 기존과 비슷한 효과를 줌
                            .zIndex(1f)
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFFEF6F2))
                        .padding(top = 50.dp)
                ) {
                    Log.d("HomeViewModel", "isLoading 상태 확인: $isLoading")
                    if (isLoading) {
                        // ✅ 로딩 중이면 로딩 인디케이터 표시
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            // 빈화면 출력
                        }
                    } else {
                        if (diaryList.isNotEmpty()){

                            // ✅ 검색 결과가 있거나, 전체 리스트가 있을 경우 `LazyColumn` 표시
                            LazyColumn(
                                state = lazyListState,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                item { Spacer(modifier = Modifier.height(60.dp)) }
                                items(diaryList) { diary ->
                                    DiaryCard(
                                        diary = diary,
                                        onDetailClick = {
                                            selectedDiaryId = diary.id
                                            onShowDetailModal()
                                        }
                                    )
                                }
                            }
                        } else {
                            // ✅ 다이어리가 없을 경우 빈 화면 표시
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.BottomCenter // ✅ 이미지가 하단에 붙도록 정렬
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.invitation), // ✅ Drawable에 있는 이미지 사용
                                    contentDescription = "초대장 이미지",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                TopBarComponent(
                    navController = navController,
                    isExpanded = isCalendarVisible,
                    isSearchVisible = isSearchVisible,
                    searchQuery = searchQuery,
                    onQueryChange = onQueryChange,
                    searchResults = searchResults,
                    isSearchTriggered = isSearchTriggered,
                    onSearch = { onSearch(searchQuery) },
                    onSearchToggle = onSearchToggle,
                    onCalendarToggle = onCalendarToggle,
                    calendarContent = { modifier ->
                        Column(modifier = modifier.fillMaxWidth()) {
                            AnimatedVisibility(
                                visible = isCalendarVisible,
                                enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
                            ) {
                                CalendarView(
                                    modifier = modifier,
                                    onDateSelected = { selectedDate ->
                                        Log.d("HomeScreen", "📌 2. CalendarView에서 날짜 선택됨: $selectedDate")
                                        onNavigateToFilteredDiaryScreen(selectedDate) // 🔹 네비게이션 실행
                                    },
                                    diaryDates = allDiaryDates
                                )
                            }
                        }
                    },
                    recentSearches = recentSearches,
                    onRecentSearchClick = onRecentSearchClick,
                    onHeightChange = { height -> topBarHeight = height }

                )
                // ✅ 드래그 가능 영역
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = topBarHeight)
                        .height(30.dp)
                        .background(Color.Transparent)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onCalendarToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        modifier = Modifier.size(50.dp, 16.dp),
                        onClick = { onCalendarToggle() }
                    ) {
                        Image(
                            painter = painterResource(id = dragIcon), // 드래그 아이콘 변경
                            contentDescription = null,
                            modifier = Modifier
                                .width(50.dp)
                                .height(16.dp)
                        )
                    }
                }
            }

//            // ✅ 4. BottomNavigationBarWithFAB (항상 하단에 고정)
//            BottomNavigationBarWithFAB(
//                selectedTab = "diary",
//                onTabSelected = { /* 탭 변경 로직 */ },
//                onFabClick = onFabClick
//            )
        }
    }

    // 디테일 모달창 (수정/삭제)
    // 모달이 열렸을 때만 FullSize 배경 클릭 이벤트 처리
    if (isDetailModalVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    onClick = onDismissDetailModal, // 모달 외부 클릭 시 닫기
                    indication = null, // 클릭 애니메이션 제거
                    interactionSource = remember { MutableInteractionSource() }
                )
        )

        // 디테일 모달창 (수정/삭제)
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter // 하단 중앙 정렬
        ) {
            AnimatedVisibility(
                visible = isDetailModalVisible,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                // 모달 내용
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clickable(
                            onClick = { /* 모달 내부 클릭 시 닫히지 않음 */ },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                ) {
                    DetailModal(
                        onDismiss = onDismissDetailModal,
                        onDelete = { onDeleteDiary(selectedDiaryId!!) },
                        onEdit = { navController.navigate("edit_record/${selectedDiaryId!!}") }
                    )
                }
            }
        }
    }
}

@Composable
fun DetailModal(
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(147.dp) // 모달 높이
            .background(Color.White, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center // 가운데 정렬
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_point),
                    contentDescription = "Point Icon",
                    modifier = Modifier
                        .width(39.dp)
                        .height(16.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, top = 20.dp)
            ){
                Column(
                    Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "수정하기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4B4B4B),
                        modifier = Modifier.clickable {
                            onEdit()
                            onDismiss()
                        }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "삭제하기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B4B4B),
                        modifier = Modifier.clickable { onDelete() }
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarView(
    modifier: Modifier = Modifier,
    onDateSelected: (LocalDate) -> Unit,
    diaryDates: List<LocalDate> // 다이어리를 작성한 날짜 리스트
) {
    // 상태 관리
    val today = LocalDate.now()
    var currentYear by remember { mutableStateOf(today.year) }
    var currentMonth by remember { mutableStateOf(today.monthValue) }

    val daysInMonth = YearMonth.of(currentYear, currentMonth).lengthOfMonth()
    val firstDayOfWeek = YearMonth.of(currentYear, currentMonth).atDay(1).dayOfWeek.value % 7

    var dragTotalX by remember { mutableStateOf(0f) }
    var currentMonthOffset by remember { mutableStateOf(0) } // 월 이동 애니메이션용 오프셋


    Column(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        dragTotalX = 0f
                    },
                    onDragEnd = {
                        if (dragTotalX > 100f) {
                            // 👉 오른쪽 스와이프
                            if (currentMonth == 1) {
                                currentMonth = 12
                                currentYear -= 1
                            } else {
                                currentMonth -= 1
                            }
                            currentMonthOffset = -1
                        } else if (dragTotalX < -100f) {
                            // 👉 왼쪽 스와이프
                            if (currentMonth == 12) {
                                currentMonth = 1
                                currentYear += 1
                            } else {
                                currentMonth += 1
                            }
                            currentMonthOffset = 1
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragTotalX += dragAmount.x
                    }
                )
            }

    ) {
        // 상단 월/연도와 이동 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp), // 위아래 여백 조정
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center // 중앙 정렬
        ) {
            // 이전 달 버튼
            IconButton(
                onClick = {
                    if (currentMonth == 1) {
                        currentMonth = 12
                        currentYear -= 1
                    } else {
                        currentMonth -= 1
                    }
                },
                modifier = Modifier.size(24.dp) // 버튼 크기 축소
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous Month",
                    tint = Color.Gray, // 색상 변경
                    modifier = Modifier
                        .width(24.dp)
                        .height(18.dp)
                )
            }

            // 중앙 텍스트
            Text(
                text = "${currentYear}년 ${currentMonth}월",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold // 강조 효과
                ),
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 13.dp) // 텍스트 양옆 여백
            )

            // 다음 달 버튼
            IconButton(
                onClick = {
                    if (currentMonth == 12) {
                        currentMonth = 1
                        currentYear += 1
                    } else {
                        currentMonth += 1
                    }
                },
                modifier = Modifier.size(24.dp) // 버튼 크기 축소
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next Month",
                    tint = Color.Gray, // 색상 변경
                    modifier = Modifier
                        .width(24.dp)
                        .height(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 요일 표시
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    color = Color(0xFFCACACA),
                    modifier = Modifier.weight(1f, true), // 균등 배분
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 날짜 그리드 표시
        LazyVerticalGrid(
            columns = GridCells.Fixed(7), // 7열 그리드
            modifier = Modifier.fillMaxWidth()
        ) {
            // 첫 주 빈칸 추가
            items(firstDayOfWeek) {
                Spacer(modifier = Modifier.size(40.dp))
            }

            // 날짜 버튼 표시
            items(daysInMonth) { day ->
                val date = LocalDate.of(currentYear, currentMonth, day + 1)
                val hasDiary = diaryDates.contains(date) // 해당 날짜에 일기 있는지 확인

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(5.dp)
                        .clickable {
                            if (hasDiary) {  // 일기가 있는 경우만 실행
                                Log.d("CalendarView", "📌 1. 날짜 선택됨: $date")
                                onDateSelected(date)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // 일기 날짜일 경우 배경 이미지
                    if (diaryDates.contains(date)) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_calender_point), // 꽃 이미지
                            contentDescription = null,
                            modifier = Modifier
                                .width(35.dp)
                                .height(32.5.dp)
                                .align(Alignment.Center)
                        )
                    }

                    // 날짜 텍스트
                    Text(
                        text = (day + 1).toString(),
                        color = when {
                            date == today -> Color.Red // 오늘 날짜는 빨간색
                            diaryDates.contains(date) -> Color.Black // 일기 작성 날짜는 검정색
                            else -> Color(0xFFCACACA) // 일반 날짜는 #CACACA
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}





@Composable
fun DiaryCard(
    diary: Diary,
    onDetailClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 25.dp)
            .shadow(
                elevation = 2.dp, // 그림자의 높이 조정
                shape = RoundedCornerShape(28.dp), // 카드의 모서리 둥글기
                spotColor = Color(0xFFDE806E),
                ambientColor = Color(0xFFDE806E),
                clip = true // 모서리가 잘리도록 설정
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 13.dp, bottom = 20.dp, start = 30.dp, end = 30.dp)
                    .fillMaxWidth()
                    .background(Color.White)

            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_point),
                        contentDescription = "Point Icon",
                        modifier = Modifier
                            .width(39.18.dp)
                            .height(16.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = diary.date.formatToKorean(), // 날짜 텍스트
                        style = TextStyle(
                            color = Color(0xFFFF9888), // 텍스트 색상
                            fontSize = 13.sp,
                            fontWeight = FontWeight.W700,
                            fontFamily = FontFamily.Default,
                            letterSpacing = -0.4.sp,
                            lineHeight = 20.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                AsyncImage(
                    model = diary.imageUrl,
                    contentDescription = "Diary Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        //.height(280.dp)
                        .clip(RoundedCornerShape(15.dp)),
                    contentScale = ContentScale.Crop,

                    error = painterResource(id = R.drawable.if_image_error)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .wrapContentWidth()
                        .background(Color(0xFFFEF6F2), RoundedCornerShape(15.dp)) // 배경색 및 모양 설정
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_location), // 위치 아이콘 리소스 사용
                        contentDescription = "위치 아이콘",
                        modifier = Modifier
                            .width(9.4.dp)
                            .height(12.dp),
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = diary.locationName, // locationName 위치 가져오기
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFFFF9888), // 텍스트 색상
                            fontSize = 12.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 내용 텍스트
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFEFE4).copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = diary.content,
                        style = TextStyle(
                            color = Color(0xFF4B4B4B), // 텍스트 색상
                            fontSize = 13.sp,
                            lineHeight = 17.sp,
                            fontWeight = FontWeight.W400,
                            fontFamily = FontFamily.Default,
                            letterSpacing = -0.4.sp
                        )
                    )
                }
            }
            // 오른쪽 상단에 디테일 아이콘
            IconButton(
                onClick = onDetailClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 18.dp, end = 25.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_detail),
                    contentDescription = "Detail Icon",
                    modifier = Modifier
                        .width(2.dp)
                        .height(12.dp)
                )
            }

        }

    }

    // 점선 구분선
    Spacer(modifier = Modifier.height(20.dp))
    DashedDivider()
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
fun DashedDivider() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 25.dp, end = 25.dp)
            .height(1.dp) // Divider의 높이 조정
    ) {
        val dashWidth = 10f // 대시의 길이
        val gapWidth = 6f // 대시 사이의 간격
        val strokeWidth = 2f // 대시의 두께
        val color = Color(0xFFFDDDC1) // 대시의 색상

        var currentX = 0f
        while (currentX < size.width) {
            // Draw a single dash
            drawLine(
                color = color,
                start = Offset(currentX, size.height / 2),
                end = Offset(currentX + dashWidth, size.height / 2),
                strokeWidth = strokeWidth
            )
            currentX += dashWidth + gapWidth // Move to the next dash position
        }
    }
}



//@RequiresApi(Build.VERSION_CODES.S)
//@Preview(showBackground = true)
//@Composable
//fun PreviewHomeScreen() {
//
//    val navController = rememberNavController()
//
//    // 더미 다이어리 데이터 (Diary 클래스는 imageUrl을 String 타입으로 사용한다고 가정)
//    val dummyDiaries = listOf(
//        Diary(
//            id = 1,
//            date = LocalDateTime.of(2025, 1, 25, 14, 30),
//            content = "귀여운 깜찍 토끼 초코푸딩!",
//            imageUrl = "https://via.placeholder.com/280", // Preview용 더미 URL
//            locationName = "서울"
//        ),
//        Diary(
//            id = 2,
//            date = LocalDateTime.of(2025, 1, 22, 18, 45),
//            content = "항상 건강하고 행복하게!",
//            imageUrl = "https://via.placeholder.com/280",
//            locationName = "부산"
//        ),
//        Diary(
//            id = 3,
//            date = LocalDateTime.of(2025, 1, 22, 9, 15),
//            content = "오늘의 다짐: 더 나은 내가 되자!",
//            imageUrl = "https://via.placeholder.com/280",
//            locationName = "대구"
//        ),
//        Diary(
//            id = 4,
//            date = LocalDateTime.of(2025, 1, 21, 11, 0),
//            content = "토끼 모양 케이크가 정말 귀엽다.",
//            imageUrl = "https://via.placeholder.com/280",
//            locationName = "인천"
//        )
//    )
//
//    HomeScreen(
//        navController = navController,
//        diaryList = dummyDiaries,
//        lazyListState = rememberLazyListState(),
//        isExpanded = false,
//        isSearchVisible = false,
//        isCalendarVisible = false,
//        isDetailModalVisible = false,
//        searchResults = emptyList(),
//        searchQuery = "",
//        recentSearches = emptyList(),
//        onQueryChange = {},
//        onSearch = {},
//        onSearchToggle = {},
//        onCalendarToggle = {},
//        onRecentSearchClick = {},
//        onFabClick = {},
//        onNavigateToFilteredDiaryScreen = { /* 선택된 날짜 처리 */ },
//        onShowDetailModal = {},
//        onDismissDetailModal = {},
//        onDeleteDiary = {},
//        allDiaryDates = emptyList()
//    )
//}