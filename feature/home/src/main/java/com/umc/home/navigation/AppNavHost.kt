package com.umc.home.navigation

import android.util.Log
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.umc.home.HomeEditRecordScreen
import com.umc.home.HomeScreen
import com.umc.home.HomeViewModel
import com.umc.home.ScreenMode
import com.umc.home.TopBarState
import java.io.File

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: HomeViewModel,
    onNavigationBarVisibilityChanged: (Boolean) -> Unit,
    onNavigateToCategoryApp: () -> Unit,
) {
    // --- 데이터 구독 ---
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val recentSearches by viewModel.recentSearchesState.collectAsState()
    val isPaging by viewModel.isPaging.collectAsState()
    val categoryList by viewModel.categoryListState.collectAsState()
    val selectedCategoryPair by viewModel.selectedCategoryState.collectAsState()

    // --- UI 상태 ---
    var topBarState by remember { mutableStateOf(TopBarState.Closed) }
    var isDetailModalVisible by remember { mutableStateOf(false) }
    var selectedDiaryId by remember { mutableStateOf<Long?>(null) }
    var isSearchTriggered by remember { mutableStateOf(false) }

    val onDeleteDiary: (Long) -> Unit = { diaryId ->
        viewModel.deleteDiary(
            diaryId = diaryId,
            onSucceed = { isDetailModalVisible = false}, //모달 닫기
            onFailed = { e -> println("❌ 삭제 실패: ${e.message}") }
        )
    }

    //  수정 기능 추가
    val onModifyDiary: (Long, Long, String, File?) -> Unit = { diaryId, categoryId, content, image ->
        viewModel.modifyDiary(
            diaryId = diaryId,
            categoryId = categoryId,
            content = content,
            image = image,
            onSucceed = {
                Log.d("AppNavHost", "✅ 수정 성공!")
                navController.popBackStack() // 수정 완료 후 뒤로 가기
            },
            onFailed = { e ->
                Log.e("AppNavHost", "❌ 수정 실패: ${e.message}", e)
            }
        )
    }

    // --- 페이지네이션 로직 ---
    val lazyListState = rememberLazyListState()
    LaunchedEffect(lazyListState, uiState.screenMode) { // 화면 모드가 바뀔 때마다 재시작
        snapshotFlow { lazyListState.layoutInfo }
            .collect { layoutInfo ->
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val totalItems = layoutInfo.totalItemsCount

                if (totalItems > 1 && lastVisibleItemIndex >= totalItems - 2) {
                    if (!isPaging) {
                        when (val mode = uiState.screenMode) {
                            is ScreenMode.Home -> {
                                viewModel.loadNextPage(isFiltered = false, selectedDate = null)
                            }
                            is ScreenMode.Filtered -> {
                                viewModel.loadNextPage(isFiltered = true, selectedDate = mode.date)
                            }
                            is ScreenMode.Search -> {
                                viewModel.searchDiaries(searchWord = mode.query, reset = false)
                            }
                        }
                    }
                }
            }
    }

    DisposableEffect(navController, onNavigationBarVisibilityChanged) {
        val callback = NavController.OnDestinationChangedListener { _, destination, _ ->
            when (destination.route) {
                "home" -> onNavigationBarVisibilityChanged(true)
                else -> onNavigationBarVisibilityChanged(false)
            }
        }

        navController.addOnDestinationChangedListener(callback)
        onDispose { navController.removeOnDestinationChangedListener(callback) }
    }

    NavHost(navController = navController, startDestination = "home") {
        // Home 화면
        composable("home") {
            HomeScreen(
                navController = navController,

                // 데이터
                uiState = uiState, // ✅ ViewModel에서 가져온 핵심 데이터
                searchQuery = searchQuery,
                recentSearches = recentSearches,
                allDiaryDates = uiState.recordedDates,

                // UI 상태
                topBarState = topBarState,
                isDetailModalVisible = isDetailModalVisible,
                isSearchTriggered = isSearchTriggered,

                // 콜백들
                onDeleteDiary = onDeleteDiary,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                onSearchSubmitted = { query ->
                    isSearchTriggered = true // 검색 실행됨
                    viewModel.onSearchSubmitted(query)
                    topBarState = TopBarState.Closed // 검색 후 TopBar 닫기
                },
                onRecentSearchClick = { query ->
                    viewModel.updateSearchQuery(query) // 검색어 UI 업데이트
                    isSearchTriggered = true
                    viewModel.onSearchSubmitted(query) // 검색 실행
                    topBarState = TopBarState.Closed // TopBar 닫기
                },
                onSearchToggle = {
                    topBarState = if (topBarState == TopBarState.SearchOpen) {
                        TopBarState.Closed
                    } else {
                        TopBarState.SearchOpen
                    }
                },
                onCalendarToggle = {
                    topBarState = if (topBarState == TopBarState.CalendarOpen) {
                        TopBarState.Closed
                    } else {
                        TopBarState.CalendarOpen
                    }
                },
                onDateSelected = { date ->
                    viewModel.onDateSelected(date) // 날짜로 필터링
                    topBarState = TopBarState.Closed // TopBar 닫기
                },
                onShowDetailModal = { diaryId ->
                    selectedDiaryId = diaryId
                    isDetailModalVisible = true
                },
                onDismissDetailModal = {
                    isDetailModalVisible = false
                },
                lazyListState = lazyListState,
                onBackClick = { viewModel.onClearMode() },
                onRefreshSummary = viewModel::onRefreshSummary
            )
        }

        composable(
            route = "edit_record/{diaryId}",
            arguments = listOf(navArgument("diaryId") { type = NavType.LongType })
        ) { backStackEntry ->
            val diaryId = backStackEntry.arguments?.getLong("diaryId") ?: -1L

            val diary = remember(diaryId, uiState.diaries) {
                uiState.diaries.find { it.id == diaryId }
            }

            Log.d("AppNavHost", "✅ 수정 화면 진입 (diaryId: ${diaryId}), 찾은 다이어리: ${diary != null}")

            val initialCategory = categoryList.find { it.id == diary?.categoryId }?.name ?: "일상"

            if (diary != null) {
                HomeEditRecordScreen(
                    diary = diary,
                    navController = navController,
                    onModifyDiary = onModifyDiary,
                    categoryList = categoryList, // AppNavHost에서 가져온 categoryList
                    selectedCategory = selectedCategoryPair[diary.id]?.second ?: initialCategory,
                    onCategorySelected = { selectedDiaryId, newCategory, newCategoryId ->
                        viewModel.updateSelectedCategory(selectedDiaryId, newCategoryId, newCategory)
                    },
                    onNewCategoryButtonClicked = {
                        onNavigateToCategoryApp()
                    }
                )
            }
        }
    }
}
