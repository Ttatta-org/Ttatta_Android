package com.umc.home

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.umc.core.model.Diary
import com.umc.design.character.Accessory
import com.umc.design.character.AccessorySet
import com.umc.design.character.CharacterView
import com.umc.design.theme.LocalColorTheme
import com.umc.home.components.DetailModal
import com.umc.home.components.TopBarComponent
import com.umc.home.components.clickableNoRipple
import com.umc.home.utils.formatToKorean
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF6F2))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .let {
                    if (topBarState == TopBarState.Closed) it
                    else it.blur(12.dp)
                }
        ) {
            Log.d("HomeViewModel", "isLoading 상태 확인: ${uiState.isLoading}")

            if (uiState.isLoading) {
                // (로딩 인디케이터...)
            } else if (uiState.diaries.isNotEmpty()) {
                LazyColumn(
                    state = lazyListState, // AppNavHost에서 받은 state
                    modifier = Modifier.fillMaxSize()
                ) {
                    item { Spacer(modifier = Modifier.height(130.dp)) }

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
                                                .background(
                                                    Color.Gray.copy(alpha = 0.1f),
                                                    RoundedCornerShape(22.dp)
                                                ),
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

                        if (diary != uiState.diaries.last()) {
                            // 점선 구분선
                            Spacer(modifier = Modifier.height(18.dp))
                            DashedDivider()
                            Spacer(modifier = Modifier.height(18.dp))
                        }
                    }

                    item {
                        Spacer(
                            modifier = Modifier.height(
                                WindowInsets.navigationBars
                                    .asPaddingValues()
                                    .calculateBottomPadding() + 80.dp
                            )
                        )
                    }
                }
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        CharacterView(
                            width = 161.dp,
                            accessorySet = AccessorySet.create(
                                Accessory.TTOTTO_BAG,
                                Accessory.TTOTTO_HAT,
                                Accessory.TTUTTU_BAG,
                                Accessory.TTUTTU_HAT,
                            )
                        )
                        Text(
                            text = if (uiState.screenMode is ScreenMode.Home) "아직 작성된 일기가 없어요!" else "검색 결과가 없어요!\n다른 검색어를 입력해보세요",
                            color = LocalColorTheme.current.grey[600],
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W700,
                            textAlign = TextAlign.Center,
                            letterSpacing = (-0.4).sp,
                        )
                    }
                }
            }
        }

        if (topBarState != TopBarState.Closed) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFFFDDDC1).copy(alpha = 0.5f))
            )
        }

        Column {
            TopBarComponent(
                topBarState = topBarState,

                screenMode = uiState.screenMode,
                onBackClick = onBackClick,

                searchQuery = searchQuery,
                isSearchTriggered = isSearchTriggered,
                // ✅ "검색 결과 없음" 여부를 TopBar에 알려줌
                areSearchResultsEmpty = (uiState.screenMode is ScreenMode.Search && uiState.diaries.isEmpty()),
                onQueryChange = onQueryChange,
                onSearchSubmitted = onSearchSubmitted,
                onSearchToggle = onSearchToggle,
                calendarContent = { modifier ->
                    CalendarView(
                        modifier = modifier,
                        onDateSelected = onDateSelected,
                        diaryDates = allDiaryDates
                    )
                },
                recentSearches = recentSearches,
                onRecentSearchClick = onRecentSearchClick,
            )
            // ✅ 드래그 가능 영역
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp, 16.dp)
                        .clickableNoRipple { // 회색 그림자 제거
                            when (topBarState) {
                                TopBarState.SearchOpen -> onSearchToggle()
                                TopBarState.CalendarOpen -> onCalendarToggle()
                                TopBarState.Closed -> onCalendarToggle()
                            }
                        },
                    contentAlignment = Alignment.Center
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
    }

    // 디테일 모달창 (수정/삭제)
    if (isDetailModalVisible) {
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
                        .clickableNoRipple {
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
    val initialPage = pageCount - 1
    val pagerState = rememberPagerState(initialPage = initialPage) { pageCount }

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    LaunchedEffect(pagerState.currentPage) {
        selectedDate = null
    }

    val (currentYear, currentMonth) = remember(pagerState.currentPage, pagerState.targetPage) {
        val page =
            if (pagerState.isScrollInProgress) pagerState.targetPage else pagerState.currentPage
        val monthsOffset = (page - initialPage).toLong()
        today
            .plusMonths(monthsOffset)
            .let { it.year to it.monthValue }
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
                    .clickableNoRipple {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // ✅ 원본 Image 코드
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_left), // (리소스 ID 확인)
                    contentDescription = "Previous Month",
                    modifier = Modifier.size(width = 9.dp, height = 16.dp),
                    tint = LocalColorTheme.current.primary[400],
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
                    .clickableNoRipple {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // ✅ 원본 Image 코드
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right), // (리소스 ID 확인)
                    contentDescription = "Next Month",
                    modifier = Modifier.size(width = 9.dp, height = 16.dp),
                    tint = if (currentYear == today.year && currentMonth == today.monthValue) LocalColorTheme.current.primary[100] else LocalColorTheme.current.primary[400],
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
                today
                    .plusMonths(monthsOffset)
                    .let { it.year to it.monthValue }
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 25.dp)
            .dropShadow(
                shape = RoundedCornerShape(28.dp),
                shadow = Shadow(
                    color = Color(0xFFDE806E),
                    alpha = 0.1f,
                    radius = 10.dp,
                    offset = DpOffset(0.dp, 2.dp),
                ),
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(28.dp),
            ),
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
                    if (style == DiaryCardStyle.DEFAULT) Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(top = 6.dp),
                    ) {
                        Text(
                            text = diary.date.formatToKorean(), // 날짜 텍스트
                            color = Color(0xFFFF9888), // 텍스트 색상
                            fontSize = 13.sp,
                            fontWeight = FontWeight.W700,
                            letterSpacing = (-0.4).sp,
                            lineHeight = 20.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(17.dp)
                                    .clickableNoRipple(onDetailClick),
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
                }

                Spacer(modifier = Modifier.height(12.dp))
                Log.d("ImageDebug", "Loading image with URL: ${diary.imageUrl}")
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(
                            color = LocalColorTheme.current.secondary[300],
                            shape = RoundedCornerShape(15.dp),
                        )
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .clip(RoundedCornerShape(15.dp))
                ) {
                    var isLoaded by remember { mutableStateOf(false) }

                    val alpha by animateFloatAsState(
                        targetValue = if (isLoaded) 1f else 0f,
                        animationSpec = tween(durationMillis = 100)
                    )

                    CircularProgressIndicator(
                        color = Color(0xFFFF9681).copy(alpha = 0.5f),
                        strokeWidth = 5.dp,
                        modifier = Modifier.size(50.dp),
                    )
                    AsyncImage(
                        model = diary.imageUrl,
                        contentDescription = "Diary Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(alpha),
                        contentScale = ContentScale.Crop,
                        onSuccess = { isLoaded = true },
                        onError = { isLoaded = true },
                        error = painterResource(id = R.drawable.if_image_error),
                    )
                }

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
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 내용 텍스트
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFFFFEFE4).copy(alpha = 0.5f),
                            RoundedCornerShape(18.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = diary.content,
                        color = Color(0xFF4B4B4B), // 텍스트 색상
                        fontSize = 13.sp,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.W400,
                        letterSpacing = (-0.4).sp,
                    )
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
                    modifier = Modifier
                        .width(20.dp)
                        .height(20.dp)
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
                        .clickableNoRipple { onRefresh() }
                )
            }
        }
    }
}

@Composable
fun DashedDivider() {
    val color = LocalColorTheme.current.secondary[300]

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 25.dp, end = 25.dp)
            .height(1.dp) // Divider의 높이 조정
    ) {
        val dashWidth = 3.dp.toPx() // 대시의 길이
        val gapWidth = 3.dp.toPx() // 대시 사이의 간격
        val strokeWidth = 1.dp.toPx() // 대시의 두께

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