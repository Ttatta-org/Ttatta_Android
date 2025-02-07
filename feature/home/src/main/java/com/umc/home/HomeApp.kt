package com.umc.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.umc.home.HomeScreen

@Composable
fun HomeApp(viewModel: HomeViewModel) {
    // HomeViewModel의 상태는 mutableStateOf로 관리되고 있으므로,
    // 예를 들어 diaryList와 searchResults는 viewModel.diaryList, viewModel.searchResults로 읽어옵니다.
    // 검색어는 viewModel.searchQuery.value를 읽거나 쓸 수 있습니다.

    // 화면에 필요한 로컬 UI 상태
    var isExpanded by remember { mutableStateOf(false) }
    var isCalendarVisible by remember { mutableStateOf(false) }
    var isSearchVisible by remember { mutableStateOf(false) }
    var isSearchTriggered by remember { mutableStateOf(false) }
    var isDetailModalVisible by remember { mutableStateOf(false) }

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

    HomeScreen(
        // ViewModel의 데이터 전달
        diaryList = viewModel.diaryList,               // List<Diary>
        searchResults = viewModel.searchResults,       // List<Diary>
        searchQuery = viewModel.searchQuery.value,     // String

        // 로컬 UI 상태 전달
        isExpanded = isExpanded,
        isCalendarVisible = isCalendarVisible,
        isSearchVisible = isSearchVisible,
        isDetailModalVisible = isDetailModalVisible,

        // 콜백들
        onQueryChange = { newQuery -> viewModel.searchQuery.value = newQuery },
        onSearch = {
            viewModel.searchDiaries(viewModel.searchQuery.value)
            isSearchTriggered = true
        },
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
        onRecentSearchClick = { recent -> viewModel.searchQuery.value = recent },
        onFabClick = { /* FAB 클릭 이벤트 처리 */ },
        onNavigateToFilteredDiaryScreen = { selectedDate ->
            // 예: 캘린더에서 선택된 날짜에 해당하는 일기를 필터링하는 화면으로 이동
        },
        onShowDetailModal = { isDetailModalVisible = true },
        onDismissDetailModal = { isDetailModalVisible = false }
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