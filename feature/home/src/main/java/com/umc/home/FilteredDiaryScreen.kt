package com.umc.home

import android.text.Layout
import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
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

@Composable
fun FilteredDiaryScreen(
    viewModel: HomeViewModel,
    navController: NavHostController,
    selectedDate: LocalDate,
    diaryList: List<Diary>,
    isSearchVisible: Boolean,
    isCalendarVisible: Boolean,
    isSearchTriggered: Boolean,
    searchResults: List<Diary>,
    searchQuery: String,
    recentSearches: List<String>,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onFabClick: () -> Unit,
    onSearchToggle: () -> Unit,
    onCalendarToggle: () -> Unit,
    onRecentSearchClick: (String) -> Unit,
    onShowDetailModal: () -> Unit,
    onDismissDetailModal: () -> Unit,
    isDetailModalVisible: Boolean,
    onDeleteDiary: (Long) -> Unit,
) {

    Log.d("FilteredDiaryScreen", "🔥 화면 넘어가는 Composable 실행됨 - 날짜: $selectedDate")
    // 필터링: viewModel의 diaryList에서 선택한 날짜와 일치하는 일기만 가져옴

    val systemUiController = rememberSystemUiController()
    val backgroundColor = Color(0xFFFFFFFF) // 상태바 배경색 (배경과 맞춤)

    SideEffect {
        systemUiController.setStatusBarColor(
            color = backgroundColor, // ✅ 상태바를 앱 배경색과 동일하게 설정
        )
    }

    var topBarHeight by remember { mutableStateOf(65.dp) }

    var selectedDiaryId by remember { mutableStateOf<Long?>(null) }
//
//
//    val filteredDiaries = diaryList.filter { it.date.toLocalDate() == selectedDate }
//
//    // ✅ diaryList 안의 데이터 확인
//    filteredDiaries.forEach { diary ->
//        Log.d("FilteredDiaryScreen", "📖 일기 내용: ${diary.content}, 날짜: ${diary.date}")
//    }
//
//    // ✅ diaryList가 업데이트되는지 확인
//    LaunchedEffect(selectedDate) {
//        Log.d("FilteredDiaryScreen", "📌 현재 diaryList 크기: ${diaryList.size}")
//
//        // ✅ Compose recomposition이 발생하는지 확인
//        snapshotFlow { diaryList }
//            .collect { updatedList ->
//                Log.d("FilteredDiaryScreen", "🔄 diaryList 변경됨: 크기 ${updatedList.size}")
//            }
//    }
//
//    // ✅ 화면이 변경되지 않을 경우, 임시로 recompose 강제 (테스트)
//    SideEffect {
//        Log.d("FilteredDiaryScreen", "⚡ SideEffect 실행 - UI 강제 업데이트")
//    }
    val filteredDiaries by viewModel.filteredDiaryListState.collectAsState()

    // 선택한 날짜가 변경되면 해당 날짜에 맞는 일기 목록을 불러옴
    LaunchedEffect(selectedDate) {
        Log.d("FilteredDiaryScreen", "📌 선택된 날짜 변경됨: $selectedDate")
        viewModel.loadDiaries(page = 0, date = selectedDate, isFiltered = true)
        //viewModel.loadNextPage(isFiltered = true, selectedDate = selectedDate)
    }

//    BackHandler {
//        Log.d("FilteredDiaryScreen", "🔙 뒤로 가기 감지 - 필터 해제")
//        viewModel.loadDiaries(page = 0, date = null, isFiltered = false)
//        viewModel.updateSearchQuery("") // ✅ 검색어 초기화
//        navController.popBackStack() // 홈 화면으로 돌아가기
//    }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .weight(1f) // ✅ LazyColumn이 BottomNavigation을 밀어내지 않도록 가변 높이 적용
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp) // ✅ TopBar와 겹치지 않도록 패딩 추가
                        .background(Color(0xFFFEF6F2)) // ✅ 부드러운 배경색 추가
                ) {
                    if (selectedDate != null) {
                        // ✅ 달력 날짜를 선택했으면 항상 `filteredDiaries` 표시
                        item { Spacer(modifier = Modifier.height(20.dp)) }
                        items(filteredDiaries) { diary ->
                            FillteredDiaryByDate(
                                diary = diary,
                                onDetailClick = {
                                    selectedDiaryId = diary.id
                                    onShowDetailModal()
                                }
                            )
                        }
                    } else if (isSearchTriggered || isSearchVisible) {
                        // ✅ 검색이 활성화되었거나, 검색창이 열려 있으면 `searchResults` 표시
                        if (searchResults.isNotEmpty()) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 25.dp, end = 25.dp)
                                ) {
                                    searchResults.forEach { diary ->
                                        DiaryCard(
                                            diary = diary,
                                            onDetailClick = {
                                                selectedDiaryId = diary.id
                                                onShowDetailModal()
                                            },
                                        )
                                    }
                                }
                            }
                        } else {
                            // ✅ 검색 결과가 없을 때 (예: 검색어에 맞는 다이어리가 없을 때)
                            item {
                                Text(
                                    text = "검색 결과가 없습니다.",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
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
                    calendarContent = { },
                    recentSearches = recentSearches,
                    onRecentSearchClick = onRecentSearchClick,
                    onHeightChange = { height -> topBarHeight = height }
                )
            }

            // ✅ 4. BottomNavigationBarWithFAB (항상 하단에 고정)
//            BottomNavigationBarWithFAB(
//                selectedTab = "home",
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
fun FillteredDiaryByDate(diary: Diary, onDetailClick: () -> Unit) {
    Log.d("FilteredDiaryScreen", "✅ FillteredDiaryByDate 실행됨 - 내용: ${diary.content}")


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .padding(top = 13.dp, bottom = 20.dp, start = 30.dp, end = 30.dp)
                .fillMaxWidth()
                .fillMaxHeight()

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

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = diary.date.formatToKorean(), // 날짜 텍스트
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFFFF9681), // 텍스트 색상
                        fontSize = 14.sp
                    )
                )
            }

//                Spacer(modifier = Modifier.height(5.dp))
//
//                // 구분선
//                Divider(
//                    color = Color(0xFFFF9681),
//                    thickness = 0.5.dp,
//                    modifier = Modifier
//                        .padding(start = 40.dp, end = 40.dp)
//                )

            Spacer(modifier = Modifier.height(12.dp))

            AsyncImage(
                model = diary.imageUrl,
                contentDescription = "Diary Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(18.dp)),
                contentScale = ContentScale.Crop,
                // 필요 시 placeholder나 error 설정도 할 수 있음
                error = painterResource(id = R.drawable.pudding)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 위치 텍스트
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .border(1.dp, Color(0xFFFDE9D9), RoundedCornerShape(20.dp))
                    .background(Color.White, RoundedCornerShape(20.dp))
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

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = diary.locationName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFFFF9681), // 텍스트 색상
                        fontSize = 12.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.5.dp))

            // 내용 텍스트
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFDDDC1).copy(alpha = 0.5f), RoundedCornerShape(17.dp))
                    .padding(vertical = 10.dp, horizontal = 12.dp)
            ) {
                Text(
                    text = diary.content,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF4B4B4B), // 텍스트 색상
                        fontSize = 14.sp,
                        lineHeight = 16.sp
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