package com.umc.home.navigation

import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
    // ✅ Composable 내부에서 `isLoading` 상태를 추적하기 위해 `remember` 사용
    var isLoading by remember { mutableStateOf(viewModel.isLoading) }

    // ✅ ViewModel에서 isLoading 값이 변경될 때 UI에 반영되도록 observe
    LaunchedEffect(viewModel.isLoading) {
        isLoading = viewModel.isLoading
    }

    // 화면에 필요한 로컬 UI 상태
    var isExpanded by remember { mutableStateOf(false) }
    var isCalendarVisible by remember { mutableStateOf(false) }
    var isSearchVisible by remember { mutableStateOf(false) }
    var isSearchTriggered by remember { mutableStateOf(false) }

    var isDetailModalVisible by remember { mutableStateOf(false) }
    var selectedDiaryId by remember { mutableStateOf<Long?>(null) }

    val diaryList by viewModel.diaryListState.collectAsState()
    val filteredDiaryList by viewModel.filteredDiaryListState.collectAsState()
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
    val onModifyDiary: (Long, Long, String, File?) -> Unit = { diaryId, categoryId, content, image ->
        viewModel.modifyDiary(
            diaryId = diaryId,
            categoryId = categoryId,
            content = content,
            image = image,
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
    val layoutInfo by remember { derivedStateOf( { lazyListState.layoutInfo } ) }
    val coroutineScope = rememberCoroutineScope()

    // ✅ 필터링 여부를 저장하는 상태 변수
    var isFiltered by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    LaunchedEffect(lazyListState, isSearchVisible) {
        snapshotFlow { lazyListState.layoutInfo }
            .collect { layoutInfo ->
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val totalItems = layoutInfo.totalItemsCount

                if (totalItems > 1 && lastVisibleItemIndex >= totalItems - 1) { // 마지막 아이템 감지
                    val currentRoute = navController.currentDestination?.route
                    Log.d("Pagination", "➡️ 현재 네비게이션 경로: $currentRoute")

                    when (currentRoute) {
                        "home" -> {
                            Log.d("Pagination", "♦️ 홈 무한스크롤 - viewModel.loadNextPage() 호출")
                            viewModel.loadNextPage(isFiltered = false, selectedDate = null)
                        }
                        "search" -> {
                            Log.d("Pagination", "♦️ 검색 결과 무한스크롤 - viewModel.searchDiaries() 호출")
                            viewModel.searchDiaries(searchWord = searchQuery, reset = false)
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
                isloading = isLoading,
                navController = navController,
                diaryList = diaryList,               // List<Diary>
                searchResults = searchResults,       // List<Diary>
                searchQuery = searchQuery,     // String
                recentSearches = recentSearches,
                lazyListState = lazyListState,
                isSearchTriggered = isSearchTriggered,

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
                    if (isCalendarVisible || isSearchVisible) {
                        // ✅ 둘 중 하나라도 열려 있으면 모두 닫기
                        isCalendarVisible = false
                        isSearchVisible = false
                    } else {
                        // ✅ 둘 다 닫혀 있으면 캘린더 열기
                        isCalendarVisible = true
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
                isSearchTriggered = isSearchTriggered,

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
                    if (isCalendarVisible || isSearchVisible) {
                        // ✅ 둘 중 하나라도 열려 있으면 모두 닫기
                        isCalendarVisible = false
                        isSearchVisible = false
                    } else {
                        // ✅ 둘 다 닫혀 있으면 캘린더 열기
                        isCalendarVisible = true
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

            // ✅ ViewModel에서 카테고리 데이터 가져오기
            val categoryList by viewModel.categoryListState.collectAsState()
            val selectedCategoryPair by viewModel.selectedCategoryState.collectAsState()

            // ✅ diary.categoryId를 기반으로 카테고리 이름 가져오기
            val initialCategory = categoryList.find { it.id == diary?.categoryId }?.name ?: "일상"

            if (diary != null) {
                HomeEditRecordScreen(
                    diary = diary,
                    navController = navController,
                    onModifyDiary = onModifyDiary,
                    categoryList = categoryList,

                    // ✅ 선택된 카테고리 없으면 기본값 사용
                    selectedCategory = selectedCategoryPair[diary.id]?.second ?: initialCategory,

                    // ✅ diaryId도 함께 전달하도록 수정
                    onCategorySelected = { selectedDiaryId, newCategory, newCategoryId ->  // ✅ 변수명 변경
                        viewModel.updateSelectedCategory(selectedDiaryId, newCategoryId, newCategory)
                    }
                )
            }
        }
    }
}
