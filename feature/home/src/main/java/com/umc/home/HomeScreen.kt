package com.umc.home

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.umc.home.components.BottomNavigationBarWithFAB
import com.umc.home.components.TopBarComponent
import com.umc.home.HomeViewModel
import com.umc.home.components.SearchBar
import com.umc.home.utils.formatToKorean
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onFabClick: () -> Unit,
    onCalendarToggle: () -> Unit,
    onNavigateToFilteredDiaryScreen: (LocalDate) -> Unit // 캘린더 날짜
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    // 캘린더가 보이는지 여부를 관리하는 상태
    var isCalendarVisible by remember { mutableStateOf(false) }
    var isSearchVisible by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }

    // 최근 검색어 관리
    val recentSearches = remember { mutableStateListOf<String>() } // 최근 검색어 리스트


    // 드래그 버튼의 상태 (ic_bottom_arrow 또는 ic_top_arrow)
    val dragIcon = when {
        isSearchVisible -> R.drawable.ic_top_arrow // 검색 상태에서는 아래로 화살표
        isCalendarVisible -> R.drawable.ic_top_arrow // 캘린더가 보이는 상태
        else -> R.drawable.ic_bottom_arrow // 기본 상태
    }

    // TopBar 확장 여부
    var isExpanded by remember { mutableStateOf(false) }
    // 디테일 모달의 표시 여부 상태 관리
    var isDetailModalVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBarComponent(
                isExpanded = isCalendarVisible,
                isSearchVisible = isSearchVisible,
                searchQuery = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = {
                    if (searchQuery.isNotEmpty()) {
                        recentSearches.add(0, searchQuery) // 최근 검색어 추가
                        if (recentSearches.size > 3) recentSearches.removeAt(recentSearches.size - 1) // 최대 3개 유지
                    }
                    viewModel.searchDiaries(searchQuery) // 검색 실행
                },
                onSearchToggle = {
                    isSearchVisible = !isSearchVisible
                    if (isSearchVisible) isCalendarVisible = false // 검색창 활성화 시 달력 비활성화
                },
                onCalendarToggle = {
                    isCalendarVisible = !isCalendarVisible
                    if (isCalendarVisible) isSearchVisible = false // 달력 활성화 시 검색창 비활성화
                },
                calendarContent = { modifier ->
                    CalendarView(
                        modifier = modifier,
                        onDateSelected = { selectedDate ->
                            onNavigateToFilteredDiaryScreen(selectedDate)
                        },
                        diaryDates = uiState.diaries.map { it.date.toLocalDate() }
                    )
                },
                recentSearches = recentSearches,
                onRecentSearchClick = { query -> searchQuery = query } // 최근 검색어 클릭 시 동작
            )
        },
        bottomBar = {
            BottomNavigationBarWithFAB(
                selectedTab = "home",
                onTabSelected = { /* 탭 변경 로직 */ }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFEF6F2))
                .padding(innerPadding)
                .padding(start = 25.dp, end = 25.dp)

        ) {
            // 드래그 가능 영역
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    modifier = Modifier.size(42.dp, 14.dp),
                    onClick = {
                        // 아이콘 클릭 시 동작
                        if (isSearchVisible || isCalendarVisible) {
                            // 검색창 또는 캘린더가 보이는 경우 모두 초기화
                            isSearchVisible = false
                            isCalendarVisible = false
                        } else {
                            // 캘린더를 토글
                            isCalendarVisible = !isCalendarVisible
                        }
                    }
                ) {
                    Image(
                        painter = painterResource(id = dragIcon), // 드래그 아이콘 변경
                        contentDescription = null,
                        modifier = Modifier
                            .width(42.dp)
                            .height(14.dp)
                    )
                }
            }

            // TopBar 확장 애니메이션
            LaunchedEffect(isCalendarVisible) {
                isExpanded = isCalendarVisible
            }
//
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//            ) {
//                // 캘린더 표시
//                AnimatedVisibility(
//                    visible = isCalendarVisible,
//                    enter = expandVertically(),
//                    exit = shrinkVertically()
//                ) {
//                    CalendarView(
//                        onDateSelected = { selectedDate ->
//                            println("Selected Date: $selectedDate")
//                        },
//                        diaryDates = uiState.diaries.map { LocalDate.parse(it.date.toString()) } // 다이어리 날짜 전달
//                    )
//                }

//            if (isSearchVisible) {
//                SearchBar(
//                    query = searchQuery,
//                    onQueryChange = { searchQuery = it },
//                    onSearch = { viewModel.searchDiaries(searchQuery) }
//                )
//            }

            Spacer(modifier = Modifier.height(8.dp))

            // 검색 결과 또는 전체 리스트 표시
            LazyColumn {
                val itemsToShow = if (searchQuery.isNotEmpty() && searchResults.isNotEmpty()) {
                    searchResults // 검색 결과 표시
                } else {
                    viewModel.getSortedDiaries() // 검색 결과가 없거나 검색 쿼리가 비어 있을 때 기본 다이어리 표시
                }
                items(itemsToShow) { diary ->
                    DiaryCard(
                        diary = diary,
                        onDetailClick = { isDetailModalVisible = true }
                    )
                }
            }
        }
    }
        // 커스텀 플로팅 버튼 자리
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            CustomFloatingImageButton(onClick = onFabClick)
        }

        // 디테일 모달창 (수정/삭제)
        // 모달이 열렸을 때만 FullSize 배경 클릭 이벤트 처리
        if (isDetailModalVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        onClick = { isDetailModalVisible = false }, // 모달 외부 클릭 시 닫기
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
                        DetailModal(onDismiss = { isDetailModalVisible = false })
                    }
                }
            }
        }
    }





@Composable
fun CustomFloatingImageButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .offset(y = (-40).dp) // 바텀 네비게이션에 걸치도록 위치 조정
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_add), // 꽃 모양 이미지
            contentDescription = "추가 버튼",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun DetailModal(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(147.dp) // 모달 높이
            .offset(y = (-6).dp) // 그림자가 위로 올라도록
            .shadow(
                elevation = 6.dp, // 그림자의 높이 조정
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp), // 카드의 모서리 둥글기
                spotColor = Color(color = 0xFFCACACA),
                ambientColor = Color(0xFFCACACA),
                //clip = true // 모서리가 잘리도록 설정
            )
            .background(Color.White, RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
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
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B4B4B),
                        modifier = Modifier.clickable { /* 수정 로직 */ }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "삭제하기",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B4B4B),
                        modifier = Modifier.clickable { /* 삭제 로직 */ }
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

    Column(
        modifier = modifier
            .fillMaxWidth()
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

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(5.dp)
                        .clickable { onDateSelected(date) },
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
fun RecentSearches(
    recentSearches: List<String>, // 최근 검색어 리스트
    onRecentSearchClick: (String) -> Unit // 클릭 시 동작
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 13.dp, start = 36.dp)
    ) {
        Text(
            text = "최근 검색어",
            fontSize = 12.sp,
            color = (Color(0xFF4B4B4B)),
            modifier = Modifier.padding(bottom = 5.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            recentSearches.take(3).forEach { search -> // 최대 3개만 표시
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFFDDDC1), RoundedCornerShape(16.dp))
                        .background(Color(0xFFFEF6F2))
                        .clickable { onRecentSearchClick(search) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = search,
                        fontSize = 10.sp,
                        color = Color(0xFF8E8E8E)
                    )
                }
            }
        }
    }
}



@Composable
fun DiaryCard(diary: Diary, onDetailClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(
                elevation = 6.dp, // 그림자의 높이 조정
                shape = RoundedCornerShape(28.dp), // 카드의 모서리 둥글기
                spotColor = Color(0xDE806E38),
                ambientColor = Color(0xDE806E38),
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
                            .width(39.dp)
                            .height(16.dp)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = diary.date.formatToKorean(), // 날짜 텍스트
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFFFF9681), // 텍스트 색상
                            fontSize = 12.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                // 구분선
                Divider(
                    color = Color(0xFFFF9681),
                    thickness = 0.5.dp,
                    modifier = Modifier
                        .padding(start = 40.dp, end = 40.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 상단 이미지
                Image(
                    painter = painterResource(id = diary.imageUrl),
                    contentDescription = "Diary Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp) // 이미지 높이 설정
                        .width(280.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop // 이미지 크롭 설정
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 날짜 텍스트
                Row(
                    modifier = Modifier
                        .wrapContentWidth()
                        .background(Color(0xFFFEF6F2), RoundedCornerShape(20.dp)) // 배경색 및 모양 설정
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_location), // 위치 아이콘 리소스 사용
                        contentDescription = "위치 아이콘",
                        modifier = Modifier
                            .width(7.5.dp)
                            .height(10.dp),
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = diary.date.formatToKorean(), // 날짜 텍스트
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFFFF9681), // 텍스트 색상
                            fontSize = 10.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                // 내용 텍스트
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFDDDC1).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(vertical = 5.dp, horizontal = 12.dp)
                ) {
                    Text(
                        text = diary.content,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF4B4B4B), // 텍스트 색상
                            fontSize = 12.sp
                        )
                    )
                }
            }
            // 오른쪽 상단에 디테일 아이콘
            IconButton(
                onClick = onDetailClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 15.dp, end = 25.dp)
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
    Spacer(modifier = Modifier.height(10.dp))
    DashedDivider()
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun DashedDivider() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
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



@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    val mockUiState = HomeUiState(
        diaries = listOf(
            Diary(LocalDateTime.of(2025, 1, 25, 14, 30), R.drawable.pudding, "귀여운 깜찍 토끼 초코푸딩!"),
            Diary(LocalDateTime.of(2025, 1, 22, 18, 45), R.drawable.letter, "항상 건강하고 행복하게!"),
            Diary(LocalDateTime.of(2025, 1, 22, 9, 15), R.drawable.cafe, "오늘의 다짐: 더 나은 내가 되자!"),
            Diary(LocalDateTime.of(2025, 1, 21, 11, 0), R.drawable.cafe, "토끼 모양 케이크가 정말 귀엽다.")
        )
    )

    // ViewModel 생성 및 검색 상태 관리
    val viewModel = object : HomeViewModel() {
        override val uiState = MutableStateFlow(mockUiState)
    }

    // 검색 상태 관리ㄱ
    var searchQuery by remember { mutableStateOf("") }

    HomeScreen(
        viewModel = viewModel,
        onFabClick = { /* Do nothing */ },
        onNavigateToFilteredDiaryScreen = { selectedDate -> println("Navigating to $selectedDate") },
        onCalendarToggle = { /* Do nothing */ }
    )
}

