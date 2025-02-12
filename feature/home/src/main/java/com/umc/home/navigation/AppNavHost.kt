package com.umc.home.navigation

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.umc.home.FilteredDiaryScreen
import com.umc.home.HomeEditRecordScreen
import com.umc.home.HomeScreen
import com.umc.home.HomeViewModel
import com.umc.home.SearchScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import java.io.File
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
    var selectedDiaryId by remember { mutableStateOf<Long?>(null) }

    val diaryList by viewModel.diaryListState.collectAsState()
    val searchResults by viewModel.searchResultsState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val recentSearches by viewModel.recentSearchesState.collectAsState()
    Log.d("RecentSearches", "📌 UI에서 받은 최근 검색어: $recentSearches")

    // ✅ 전체 일기 목록을 가져옴
//    val fullDiaryList by viewModel.fullDiaryListState.collectAsState()
//    val allDiaryDates = remember(fullDiaryList) {
//        fullDiaryList.map { it.date.toLocalDate() }.distinct()
//    }

    // ✅ 전체 일기 날짜 가져오기 (달력에서 사용)
    val recordedDates by viewModel.recordedDatesState.collectAsState()
    val allDiaryDates = remember(recordedDates) {
        recordedDates.distinct()
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

    val onSearch: (String) -> Unit = { query ->
        isSearchTriggered = true

        viewModel.searchDiaries(
            searchWord = query,
            reset = true // ✅ ViewModel 내부에서 `searchPage = 0`으로 초기화하도록 변경
        )
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

    // ✅ 수정 기능 추가
    val onModifyDiary: (Long, String, File?) -> Unit = { diaryId, content, image ->
        viewModel.modifyDiary(
            diaryId = diaryId,
            categoryId = null,
            content = content,
            image = null,
            onSucceed = {
                Log.d("AppNavHost", "✅ 수정 성공!")
                navController.popBackStack() // ✅ 수정 완료 후 이전 화면으로 이동
                isDetailModalVisible = false // ✅ 모달 닫기
            },
            onFailed = { e ->
                Log.e("AppNavHost", "❌ 수정 실패: ${e.message}", e)
            }
        )
    }

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

//    LaunchedEffect(lazyListState, diaryList, searchResults) { // ✅ 검색 & 일반 리스트 둘 다 감지
//        snapshotFlow { lazyListState.layoutInfo }
//            .collect { layoutInfo ->
//                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
//                val totalItems = layoutInfo.totalItemsCount
//
//                Log.d("Pagination", "📌 마지막 보이는 아이템 인덱스: $lastVisibleItemIndex, 전체 아이템 개수: $totalItems")
//
//                // ✅ 검색 중이면 `searchDiaries()` 호출, 아니라면 `loadNextPage()` 호출
//                if (totalItems > 1 && lastVisibleItemIndex >= totalItems - 1) {
//                    if (searchResults.isNotEmpty()) {
//                        Log.d("Pagination", "✅ (검색) 스크롤 80% 도달 - 다음 페이지 로드 요청")
//                        viewModel.searchDiaries(searchQuery, reset = false, onSucceed = {}, onFailed = {})
//                    } else {
//                        Log.d("Pagination", "✅ (다이어리) 스크롤 80% 도달 - 다음 페이지 로드 요청")
//                        viewModel.loadNextPage()
//                    }
//                }
//            }
//    }
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo }
            .collect { layoutInfo ->
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val totalItems = layoutInfo.totalItemsCount

                if (totalItems > 1 && lastVisibleItemIndex >= totalItems - 1) {
                    when (navController.currentDestination?.route) {
                        "home" -> {
                            viewModel.loadNextPage()
                        }
                        "search" -> {
                            viewModel.searchDiaries(searchWord = searchQuery, reset = false) // ✅ ViewModel이 알아서 `searchPage++` 관리
                        }
                    }
                }
            }
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
                lazyListState = lazyListState,

                // 로컬 UI 상태 전달
                isExpanded = isExpanded,
                isCalendarVisible = isCalendarVisible,
                isSearchVisible = isSearchVisible,
                isDetailModalVisible = isDetailModalVisible,

                // ✅ 달력에서 모든 일기 날짜 유지
                allDiaryDates = allDiaryDates,

                // 콜백들
                onQueryChange = { newQuery -> viewModel.updateSearchQuery(newQuery) },
                onSearch = { onSearch(searchQuery) },
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
                onRecentSearchClick = { query -> onSearch(query) },
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
        // 🔍 SearchScreen (검색 결과 전용)
        composable("search") {
            SearchScreen(
                navController = navController,
                searchResults = searchResults, // ✅ 검색된 결과만 표시
                searchQuery = searchQuery,     // String
                recentSearches = recentSearches,
                lazyListState = lazyListState,

                // 로컬 UI 상태 전달
                isExpanded = isExpanded,
                isCalendarVisible = isCalendarVisible,
                isSearchVisible = isSearchVisible,
                isDetailModalVisible = isDetailModalVisible,

                // ✅ 달력에서 모든 일기 날짜 유지
                allDiaryDates = allDiaryDates,

                // 콜백들
                onQueryChange = { newQuery -> viewModel.updateSearchQuery(newQuery) },
                onSearch = { onSearch(searchQuery) },
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
                onRecentSearchClick = { query -> onSearch(query) },
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
                navController = navController,
                selectedDate = selectedDate,
                onFabClick = { /* 필터된 화면의 FAB 클릭 처리 */ },
                diaryList = filteredDiaries,
                isCalendarVisible = false,
                isDetailModalVisible = isDetailModalVisible,
                onCalendarToggle = { /* 달력 토글 처리 */ },
                isSearchVisible = isSearchVisible,
                isSearchTriggered = isSearchTriggered,
                searchQuery = searchQuery,
                recentSearches = recentSearches,
                searchResults = searchResults,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                onSearch = { onSearch(searchQuery) },
                onSearchToggle = { isSearchVisible = !isSearchVisible },
                onRecentSearchClick = { query -> onSearch(query) },
                onShowDetailModal = { isDetailModalVisible = true },
                onDismissDetailModal = { isDetailModalVisible = false },
                onDeleteDiary = onDeleteDiary
            )
        }

        composable(
            route = "edit_record/{diaryId}",
            arguments = listOf(navArgument("diaryId") { type = NavType.LongType })
        ) { backStackEntry ->
            val diaryId = backStackEntry.arguments?.getLong("diaryId") ?: -1
            val diary = diaryList.find { it.id == diaryId }

            if (diary != null) {
                HomeEditRecordScreen(
                    diary = diary,
                    //viewModel = viewModel, // ViewModel 전달
                    navController = navController, // NavController 전달
                    onModifyDiary = onModifyDiary
                )
            }
        }
    }
}
