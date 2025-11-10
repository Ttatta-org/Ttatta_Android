package com.umc.home

import android.app.Activity
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import kotlinx.coroutines.launch
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.zIndex
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

enum class DiaryCardStyle {
    DEFAULT,    // 사진 1번 (홈, 검색) 스타일
    SUMMARIZED  // 사진 2번 (필터) 스타일
}

@Composable
fun HomeScreen(
    navController: NavHostController,
    uiState: HomeUiState,
    searchQuery: String,
    recentSearches: List<String>,
    allDiaryDates: List<LocalDate>,
    topBarState: TopBarState,
    isDetailModalVisible: Boolean,
    isSearchTriggered: Boolean,
    onDeleteDiary: (Long) -> Unit,
    onQueryChange: (String) -> Unit,
    onSearchSubmitted: (String) -> Unit,
    onRecentSearchClick: (String) -> Unit,
    onSearchToggle: () -> Unit,
    onCalendarToggle: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onShowDetailModal: (Long) -> Unit, // (Long) 타입으로 수정됨
    onDismissDetailModal: () -> Unit,
    lazyListState: LazyListState,
    onBackClick: () -> Unit,
    onRefreshSummary: () -> Unit
) {

    val systemUiController = rememberSystemUiController()
    val backgroundColor = Color(0xFFFFFFFF) // 상태바 배경색 (배경과 맞춤)
    SideEffect {
        systemUiController.setStatusBarColor(
            color = backgroundColor, // ✅ 상태바를 앱 배경색과 동일하게 설정
        )
    }

    // 드래그 버튼의 상태 (TopBarState에 따라 아이콘 변경)
    val dragIcon = when (topBarState) {
        TopBarState.Closed -> R.drawable.ic_bottom_arrow_new
        else -> R.drawable.ic_top_arrow_new
    }

    var selectedDiaryId by remember { mutableStateOf<Long?>(null) }

    var topBarHeight by remember { mutableStateOf(0.dp) }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ✅ 1. TopBarComponent (항상 상단에 고정)
            Box(
                modifier = Modifier
                    .weight(1f) // ✅ LazyColumn이 BottomNavigation을 밀어내지 않도록 가변 높이 적용
                    //.background(Color(0xFFFEF6F2)) // ✅ 부드러운 배경색 추가
            ) {
                // ✅ 캘린더 또는 검색창이 열렸을 때만 배경을 블러 처리
                if (topBarState != TopBarState.Closed) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFFDDDC1).copy(alpha = 0.5f),
                                        Color(0xFFFDDDC1).copy(alpha = 0.5f),
                                        Color(0xFFFDDDC1).copy(alpha = 0.5f),
                                        Color(0xFFFDDDC1).copy(alpha = 0.5f),
                                        Color(0xFFFDDDC1).copy(alpha = 0.5f)
                                    ),
                                    startY = 0f,
                                    endY = Float.POSITIVE_INFINITY
                                )
                            )
                            .blur(60.dp) // ✅ 블러 강도를 높여 기존과 비슷한 효과를 줌
                            .zIndex(1f)
                    )
                }
                // 다이어리 목록
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFFEF6F2))
                        .padding(top = 50.dp)
                ) {
                    Log.d("HomeViewModel", "isLoading 상태 확인: ${uiState.isLoading}")
                    if (uiState.isLoading) {
                        // (로딩 인디케이터...)
                    } else if (uiState.diaries.isNotEmpty()) {
                        LazyColumn(
                            state = lazyListState, // AppNavHost에서 받은 state
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item { Spacer(modifier = Modifier.height(80.dp)) }

                            if (uiState.screenMode is ScreenMode.Filtered) {
                                val summaryState = uiState.aiSummaryState
                                if (summaryState.isVisible) {
                                    item {
                                        when {
                                            summaryState.isLoading -> {
                                                // TODO: (선택) 요약 카드용 로딩 스켈레톤 UI
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 25.dp)
                                                        .padding(bottom = 12.dp)
                                                        .height(150.dp) // AiSummaryCard와 비슷한 높이
                                                        .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(22.dp)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text("요약 불러오는 중...")
                                                }
                                            }
                                            summaryState.error != null -> {
                                                AiSummaryCard(
                                                    summaryText = summaryState.error, // 에러 메시지 표시
                                                    timestamp = "오류 발생",
                                                    onRefresh = onRefreshSummary // 에러 시에도 새로고침
                                                )
                                            }
                                            else -> {
                                                // 성공 시 데이터 연결
                                                AiSummaryCard(
                                                    summaryText = summaryState.summaryText,
                                                    timestamp = summaryState.timestamp,
                                                    onRefresh = onRefreshSummary
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            items(uiState.diaries) { diary ->
                                // ✅ 7. uiState.screenMode에 따라 카드 스타일 결정
                                val style = when (uiState.screenMode) {
                                    is ScreenMode.Filtered -> DiaryCardStyle.SUMMARIZED
                                    else -> DiaryCardStyle.DEFAULT
                                }

                                DiaryCard(
                                    diary = diary,
                                    style = style, // 3단계에서 만든 통합 카드 사용
                                    onDetailClick = {
                                        selectedDiaryId = diary.id // 내부 ID 저장
                                        onShowDetailModal(diary.id) // 부모(AppNavHost)에게 알림
                                    }
                                )
                            }
                        }
                    } else {
                        // ✅ 8. 빈 화면 (검색/홈 분기)
                        val emptyImage = if (uiState.screenMode is ScreenMode.Search) {
                            R.drawable.no_search_results
                        } else {
                            R.drawable.invitation
                        }
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center // (기존 BottomCenter에서 수정)
                        ) {
                            Image(
                                painter = painterResource(id = emptyImage),
                                contentDescription = "빈 화면",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                TopBarComponent(
                    navController = navController,
                    topBarState = topBarState,

                    screenMode = uiState.screenMode,
                    selectedDate = (uiState.screenMode as? ScreenMode.Filtered)?.date,
                    onBackClick = onBackClick,

                    searchQuery = searchQuery,
                    isSearchTriggered = isSearchTriggered,
                    // ✅ "검색 결과 없음" 여부를 TopBar에 알려줌
                    areSearchResultsEmpty = (uiState.screenMode is ScreenMode.Search && uiState.diaries.isEmpty()),
                    onQueryChange = onQueryChange,
                    onSearchSubmitted = onSearchSubmitted,
                    onSearchToggle = onSearchToggle,
                    onCalendarToggle = onCalendarToggle,
                    calendarContent = { modifier ->
                        CalendarView(
                            modifier = modifier,
                            onDateSelected = onDateSelected,
                            diaryDates = allDiaryDates
                        )
                    },
                    recentSearches = recentSearches,
                    onRecentSearchClick = onRecentSearchClick,
                    onHeightChange = { newHeight -> // 높이가 바뀔 때마다 topBarHeight 변수 업데이트
                        topBarHeight = newHeight
                    }
                )
                // ✅ 드래그 가능 영역
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = topBarHeight)
                        .height(30.dp)
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        modifier = Modifier.size(50.dp, 16.dp),
                        onClick = {
                            when (topBarState) {
                                TopBarState.SearchOpen -> onSearchToggle()   // 검색창 닫기
                                TopBarState.CalendarOpen -> onCalendarToggle() // 캘린더 닫기
                                TopBarState.Closed -> onCalendarToggle()     // 캘린더 열기
                            }
                        }
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
    if (isDetailModalVisible) {
        // (배경 클릭 시 닫기)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    onClick = onDismissDetailModal,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                visible = isDetailModalVisible,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                DetailModal(
                    onDismiss = onDismissDetailModal,
                    onDelete = {
                        if (selectedDiaryId != null) {
                            onDeleteDiary(selectedDiaryId!!) // 내부 ID로 삭제 요청
                        }
                    },
                    onEdit = {
                        navController.navigate("edit_record/${selectedDiaryId!!}")
                    }
                )
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDelete() }
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarBody(
    modifier: Modifier = Modifier,
    year: Int,
    month: Int,
    diaryDates: List<LocalDate>,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
) {
    val yearMonth = remember(year, month) { YearMonth.of(year, month) }
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = (yearMonth.atDay(1).dayOfWeek.value % 7)

    // UI 사이즈 (원본과 동일)
    val cellSize = 50.dp
    val dayFontSize = 15.sp
    val weekFontSize = 14.sp
    val flowerWidth = 38.5.dp
    val flowerHeight = 35.86.dp

    Column(modifier = modifier) {
        // 요일 표시 (원본과 동일)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = weekFontSize),
                    color = Color(0xFFBDBDBD),
                    modifier = Modifier.weight(1f, true),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // 날짜 그리드 (원본과 동일)
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            userScrollEnabled = false
        ) {
            items(firstDayOfWeek) { Spacer(modifier = Modifier.size(cellSize)) }

            items(daysInMonth) { index ->
                val day = index + 1
                val date = LocalDate.of(year, month, day)
                val hasDiary = diaryDates.contains(date)
                val isSelected = selectedDate == date

                Box(
                    modifier = Modifier
                        .size(cellSize)
                        .padding(4.dp)
                        .clickable {
                            if (hasDiary) {
                                onDateSelected(date) // 부모의 selectedDate 갱신
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (hasDiary) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_calender_point), // (리소스 ID 확인)
                            contentDescription = null,
                            modifier = Modifier
                                .width(flowerWidth)
                                .height(flowerHeight)
                                .align(Alignment.Center)
                        )
                    }
                    Text(
                        text = day.toString(),
                        color = when {
                            isSelected -> Color.White
                            hasDiary -> Color.White
                            else -> Color(0xFFCACACA)
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = dayFontSize),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
        Spacer(Modifier.height(5.dp))
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarView(
    modifier: Modifier = Modifier,
    onDateSelected: (LocalDate) -> Unit,
    diaryDates: List<LocalDate> // 다이어리를 작성한 날짜 리스트
) {
    val today = LocalDate.now()
    val scope = rememberCoroutineScope()

    val pageCount = Int.MAX_VALUE
    val initialPage = pageCount / 2
    val pagerState = rememberPagerState(initialPage = initialPage) { pageCount }

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    LaunchedEffect(pagerState.currentPage) {
        selectedDate = null
    }

    val (currentYear, currentMonth) = remember(pagerState.currentPage, pagerState.targetPage) {
        val page = if (pagerState.isScrollInProgress) pagerState.targetPage else pagerState.currentPage
        val monthsOffset = (page - initialPage).toLong()
        today.plusMonths(monthsOffset).let { it.year to it.monthValue }
    }


    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        // ✅ 상단 월/연도 + 네비게이션 (원본 디자인 코드로 복원)
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .clickable {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // ✅ 원본 Image 코드
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_left), // (리소스 ID 확인)
                    contentDescription = "Previous Month",
                    modifier = Modifier.size(width = 9.dp, height = 16.dp)
                )
            }

            // ✅ 원본 Text 코드 (스타일, 모디파이어 모두 복원)
            Text(
                text = "${currentYear}년 ${currentMonth}월",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W700,
                    color = Color(0xFF4B4B4B),
                    letterSpacing = (-0.4).sp,
                    lineHeight = 20.sp
                ),
                modifier = Modifier.padding(horizontal = 14.dp)
            )

            Box(
                modifier = Modifier
                    .clickable {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // ✅ 원본 Image 코드
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_right), // (리소스 ID 확인)
                    contentDescription = "Next Month",
                    modifier = Modifier.size(width = 9.dp, height = 16.dp)
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        // --- ✅ 여기가 HorizontalPager로 변경됨 ---
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            // 각 페이지에 해당하는 년/월 계산
            val (pageYear, pageMonth) = remember(page) {
                val monthsOffset = (page - initialPage).toLong()
                today.plusMonths(monthsOffset).let { it.year to it.monthValue }
            }

            // 분리된 CalendarBody 호출
            CalendarBody(
                year = pageYear,
                month = pageMonth,
                diaryDates = diaryDates,
                selectedDate = selectedDate,
                onDateSelected = { date ->
                    selectedDate = date
                    onDateSelected(date)
                }
            )
        }
    }
}

@Composable
fun DiaryCard(
    diary: Diary,
    onDetailClick: () -> Unit,
    style: DiaryCardStyle = DiaryCardStyle.DEFAULT
) {
    if (style == DiaryCardStyle.DEFAULT) {
        // ✅ [기존 DiaryCard 코드] (홈/검색용)
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
                        .padding(top = 14.dp, bottom = 20.dp, start = 24.dp, end = 24.dp)
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
                    Log.d("ImageDebug", "Loading image with URL: ${diary.imageUrl}")
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
                            .width(17.dp)
                            .height(3.dp)
                    )
                }
            }
        }

        // 점선 구분선
        Spacer(modifier = Modifier.height(20.dp))
        DashedDivider()
        Spacer(modifier = Modifier.height(20.dp))

    } else {
        // ✅ [기존 FillteredDiaryByDate 코드] (필터용)
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
                        .padding(top = 14.dp, bottom = 20.dp, start = 24.dp, end = 24.dp)
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
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    AsyncImage(
                        model = diary.imageUrl,
                        contentDescription = "Diary Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                            .clip(RoundedCornerShape(15.dp)),
                        contentScale = ContentScale.Crop,
                        // 필요 시 placeholder나 error 설정도 할 수 있음
                        error = painterResource(id = R.drawable.if_image_error)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 위치 텍스트
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

                    Spacer(modifier = Modifier.height(6.57.dp))

                    // 내용 텍스트
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFDDDC1).copy(alpha = 0.5f), RoundedCornerShape(15.dp))
                            .padding(vertical = 10.dp, horizontal = 12.dp)
                    ) {
                        Text(
                            text = diary.content,
                            style = TextStyle(
                                color = Color(0xFF4B4B4B), // 텍스트 색상
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                fontWeight = FontWeight.W400,
                                fontFamily = FontFamily.Default,
                                letterSpacing = -0.4.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AiSummaryCard(
    modifier: Modifier = Modifier,
    summaryText: String,
    timestamp: String,
    onRefresh: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp) // DiaryCard와 동일한 좌우 여백
            .padding(bottom = 12.dp), // 위쪽 여백
        shape = RoundedCornerShape(22.dp), // DiaryCard보다 둥글게
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.5.dp, Color(0xFFFFD0C8)) // 옅은 분홍색 테두리
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .padding(top = 18.dp, bottom = 15.dp)
                .fillMaxWidth()
        ) {
            // --- AI 아이콘 + 타이틀 ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_ai_summary),
                    contentDescription = "AI Summary Icon",
                    modifier = Modifier.width(20.dp).height(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "하루 일기 요약",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W700,
                    color = Color(0xFFFF9888)
                )
            }

            Spacer(modifier = Modifier.height(11.dp))

            // --- 2. 요약 내용 ---
            Text(
                text = summaryText,
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                color = Color(0xFF4B4B4B),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // --- 3. 타임스탬프 + 새로고침 ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = timestamp,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.W400,
                    color = Color(0xFFFF9888)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Image(
                    painter = painterResource(id = R.drawable.ic_refresh),
                    contentDescription = "새로고침",
                    modifier = Modifier
                        .size(15.dp)
                        .clickable { onRefresh() }
                )
            }
        }
    }
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