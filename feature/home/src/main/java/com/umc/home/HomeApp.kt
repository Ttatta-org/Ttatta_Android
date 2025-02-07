package com.umc.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import com.umc.home.HomeScreen
import com.umc.home.navigation.AppNavHost

@OptIn(UnstableApi::class)
@Composable
fun HomeApp(viewModel: HomeViewModel, navController: NavHostController) {
    // HomeViewModel의 상태는 mutableStateOf로 관리되고 있으므로,
    // 예를 들어 diaryList와 searchResults는 viewModel.diaryList, viewModel.searchResults로 읽어옵니다.
    // 검색어는 viewModel.searchQuery.value를 읽거나 쓸 수 있습니다.

    // ✅ 앱이 실행될 때 자동으로 전체 다이어리 로드
    LaunchedEffect(Unit) {
        viewModel.loadAllDiaries()
    }

    AppNavHost(navController = navController, viewModel = viewModel)

    // 화면에 필요한 로컬 UI 상태
    var isExpanded by remember { mutableStateOf(false) }
    var isCalendarVisible by remember { mutableStateOf(false) }
    var isSearchVisible by remember { mutableStateOf(false) }
    var isSearchTriggered by remember { mutableStateOf(false) }
    var isDetailModalVisible by remember { mutableStateOf(false) }

    val diaryList by viewModel.diaryListState.collectAsState()
    val searchResults by viewModel.searchResultsState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val recentSearches by viewModel.recentSearchesState.collectAsState()

    // ✅ 전체 일기 목록을 가져와 달력에서 사용할 날짜 리스트 생성
    val fullDiaryList by viewModel.fullDiaryListState.collectAsState()
    val allDiaryDates = remember(fullDiaryList) {
        fullDiaryList.map { it.date.toLocalDate() }.distinct()
    }

    // onCalendarToggle 함수 정의: 캘린더 보임 상태를 토글하고,
    // 캘린더가 보일 때 검색창은 닫히도록 설정
    val onCalendarToggle = {
        isCalendarVisible = !isCalendarVisible
        isExpanded = isCalendarVisible
        if (isCalendarVisible) {
            isSearchVisible = false
        }
//        // 아이콘 클릭 시 동작
//        if (isSearchVisible || isCalendarVisible) {
//            // 검색창 또는 캘린더가 보이는 경우 모두 초기화
//            isSearchVisible = false
//            isCalendarVisible = false
//        } else {
//            // 캘린더를 토글
//            isCalendarVisible = !isCalendarVisible
//        }
    }

    val onSearch: () -> Unit = {
        viewModel.searchDiaries(searchQuery)
    }

    val onDeleteDiary: (Long) -> Unit = { diaryId ->
        viewModel.deleteDiary(
            diaryId = diaryId,
            onSucceed = {
                isDetailModalVisible = false // ✅ 모달 닫기
            },
            onFailed = { e ->
                println("❌ 삭제 실패: ${e.message}")
            }
        )
    }

    HomeScreen(
        // ViewModel의 데이터 전달
        diaryList = diaryList,               // List<Diary>
        searchResults = searchResults,       // List<Diary>
        searchQuery = searchQuery,     // String
        recentSearches = recentSearches,

        // 로컬 UI 상태 전달
        isExpanded = isExpanded,
        isCalendarVisible = isCalendarVisible,
        isSearchVisible = isSearchVisible,
        isDetailModalVisible = isDetailModalVisible,

        // ✅ 달력에서 모든 일기 날짜 유지
        allDiaryDates = allDiaryDates,

        // 콜백들
        onQueryChange = { newQuery -> viewModel.updateSearchQuery(newQuery) },
        onSearch = onSearch,
        onSearchToggle = {
            isSearchVisible = !isSearchVisible
            if (isSearchVisible) isCalendarVisible = false
        },
        onCalendarToggle = {
            isCalendarVisible = !isCalendarVisible
            isExpanded = isCalendarVisible
            if (isCalendarVisible) {
                isSearchVisible = false
            }
        },
        onRecentSearchClick = { query -> viewModel.searchDiaries(query) },
        onFabClick = { /* FAB 클릭 이벤트 처리 */ },
        onNavigateToFilteredDiaryScreen = { selectedDate ->
            Log.d("HomeScreen", "🚀 3. FilteredDiaryScreen으로 이동: $selectedDate")
            // 🔥 애니메이션 없이 이동 (기존 기록 유지)
            navController.navigate("filtered/$selectedDate") {
                popUpTo(navController.graph.startDestinationId) { inclusive = false }
                launchSingleTop = true
                restoreState = true // ✅ 기존 상태 유지
            }
        },
        onShowDetailModal = { isDetailModalVisible = true },
        onDismissDetailModal = { isDetailModalVisible = false },
        onDeleteDiary = onDeleteDiary
    )
}

//class HomeApp : ComponentActivity() {
//    private val homeViewModel: HomeViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            HomeScreen(
//                viewModel = homeViewModel,
//                onFabClick = { /* FAB 클릭 이벤트 */ },
//                onNavigateToFilteredDiaryScreen = { selectedDate -> println("Navigating to $selectedDate") },
//                onCalendarToggle = { /* 캘린더 열기/닫기 이벤트 */ }
//            )
//        }
//    }
//}