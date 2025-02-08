package com.umc.home.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.umc.home.FilteredDiaryScreen
import com.umc.home.HomeScreen
import com.umc.home.HomeViewModel
import java.time.LocalDate

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: HomeViewModel
) {
    // ✅ 현재 화면 상태 확인
    val currentBackStackEntry by navController.currentBackStackEntryFlow.collectAsState(initial = null)
    LaunchedEffect(currentBackStackEntry) {
        Log.d("AppNavHost", "📌 현재 네비게이션 경로: ${currentBackStackEntry?.destination?.route}")
    }

    val diaryList by viewModel.diaryListState.collectAsState()
    val searchResults by viewModel.searchResultsState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val recentSearches by viewModel.recentSearchesState.collectAsState()

    // ✅ 전체 일기 목록 가져오기 (달력에 표시할 날짜용)
    val fullDiaryList by viewModel.fullDiaryListState.collectAsState()
    val allDiaryDates = remember(fullDiaryList) {
        fullDiaryList.map { it.date.toLocalDate() }.distinct()
    }

    NavHost(navController = navController, startDestination = "home") {
        // Home 화면
        composable("home") {
            HomeScreen(
                diaryList = diaryList,
                isExpanded = false,
                isSearchVisible = false,
                isCalendarVisible = false,
                isDetailModalVisible = false,
                searchResults = emptyList(),
                searchQuery = searchQuery,
                recentSearches = recentSearches,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                onSearch = { viewModel.searchDiaries(viewModel.searchQuery.value) },
                onSearchToggle = { /* 검색 토글 처리 */ },
                onCalendarToggle = { /* 달력 토글 처리 */ },
                onRecentSearchClick = { /* 최근 검색어 처리 */ },
                onFabClick = { /* FAB 클릭 처리 */ },
                onNavigateToFilteredDiaryScreen = {},
                onShowDetailModal = { /* 모달 열기 처리 */ },
                onDismissDetailModal = { /* 모달 닫기 처리 */ },
                onDeleteDiary = {},
                allDiaryDates = allDiaryDates
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
    }
}
