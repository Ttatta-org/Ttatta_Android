package com.umc.home.navigation

import androidx.compose.runtime.Composable
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
    NavHost(navController = navController, startDestination = "home") {
        // Home 화면
        composable("home") {
            HomeScreen(
                diaryList = viewModel.diaryList,
                isExpanded = false,
                isSearchVisible = false,
                isCalendarVisible = false,
                isDetailModalVisible = false,
                searchResults = emptyList(),
                searchQuery = viewModel.searchQuery.value,
                onQueryChange = { viewModel.searchQuery.value = it },
                onSearch = { viewModel.searchDiaries(viewModel.searchQuery.value) },
                onSearchToggle = { /* 검색 토글 처리 */ },
                onCalendarToggle = { /* 달력 토글 처리 */ },
                onRecentSearchClick = { /* 최근 검색어 처리 */ },
                onFabClick = { /* FAB 클릭 처리 */ },
                onNavigateToFilteredDiaryScreen = { selectedDate ->
                    // 선택한 날짜를 문자열로 변환하여 내비게이션 경로에 포함시킵니다.
                    navController.navigate("filtered/${selectedDate.toString()}")
                },
                onShowDetailModal = { /* 모달 열기 처리 */ },
                onDismissDetailModal = { /* 모달 닫기 처리 */ }
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
            FilteredDiaryScreen(
                viewModel = viewModel,
                selectedDate = selectedDate,
                onFabClick = { /* 필터된 화면의 FAB 클릭 처리 */ },
                diaryList = viewModel.diaryList,
                isSearchVisible = false,
                isCalendarVisible = false,
                isDetailModalVisible = false,
                searchResults = emptyList(),
                isSearchTriggered = false,
                searchQuery = viewModel.searchQuery.value,
                onQueryChange = { viewModel.searchQuery.value = it },
                onSearch = { viewModel.searchDiaries(viewModel.searchQuery.value) },
                onSearchToggle = { /* 검색 토글 처리 */ },
                onCalendarToggle = { /* 달력 토글 처리 */ },
                onRecentSearchClick = { /* 최근 검색어 처리 */ },
                onNavigateToFilteredDiaryScreen = { selectedDate ->
                    // 선택한 날짜를 문자열로 변환하여 내비게이션 경로에 포함시킵니다.
                    navController.navigate("filtered/${selectedDate.toString()}")
                },
                onShowDetailModal = { /* 모달 열기 처리 */ },
                onDismissDetailModal = { /* 모달 닫기 처리 */ }
            )
        }
    }
}
