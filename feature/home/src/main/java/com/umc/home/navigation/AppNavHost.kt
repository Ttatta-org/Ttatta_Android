package com.umc.home.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.umc.home.FilteredDiaryScreen
import com.umc.home.HomeEditRecordScreen
import com.umc.home.HomeScreen
import com.umc.home.HomeViewModel
import java.time.LocalDate

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: HomeViewModel
) {
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

    NavHost(navController = navController, startDestination = "home") {
        // Home 화면
        composable("home") {
            HomeScreen(
                // ViewModel의 데이터 전달
                navController = navController,
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
        // Filtered Diary 화면
        composable(
            route = "filtered/{selectedDate}",
            arguments = listOf(navArgument("selectedDate") { type = NavType.StringType })
        ) { backStackEntry ->
            // 전달된 인자를 파싱하여 LocalDate로 변환합니다.
            val selectedDateString = backStackEntry.arguments?.getString("selectedDate") ?: ""
            val selectedDate = LocalDate.parse(selectedDateString)

            // ✅ 선택된 날짜에 해당하는 일기만 가져오기
            val filteredDiaries = diaryList.filter { it.date.toLocalDate() == selectedDate }

            FilteredDiaryScreen(
                viewModel = viewModel,
                selectedDate = selectedDate,
                onFabClick = { /* 필터된 화면의 FAB 클릭 처리 */ },
                diaryList = filteredDiaries,
                isSearchVisible = false,
                isCalendarVisible = false,
                isDetailModalVisible = false,
                searchResults = emptyList(),
                isSearchTriggered = false,
                searchQuery = searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                onSearch = { viewModel.searchDiaries(viewModel.searchQuery.value) },
                onSearchToggle = { /* 검색 토글 처리 */ },
                onCalendarToggle = { /* 달력 토글 처리 */ },
                onRecentSearchClick = { /* 최근 검색어 처리 */ },
                onNavigateToFilteredDiaryScreen = { },
                onShowDetailModal = { /* 모달 열기 처리 */ },
                onDismissDetailModal = { /* 모달 닫기 처리 */ }
            )
        }

        composable("edit_record") { HomeEditRecordScreen() }
    }
}
