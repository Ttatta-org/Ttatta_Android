package com.umc.home

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import com.umc.core.model.Diary
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.umc.home.components.BottomNavigationBarWithFAB
import com.umc.home.components.TopBarComponent
import java.time.LocalDate

@Composable
fun SearchScreen(
    navController: NavHostController,
    searchResults: List<Diary>,
    lazyListState: LazyListState,
    isExpanded: Boolean,
    isSearchVisible: Boolean,
    isSearchTriggered: Boolean,
    isCalendarVisible: Boolean,
    searchQuery: String,
    recentSearches: List<String>,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onSearchToggle: () -> Unit,
    onCalendarToggle: () -> Unit,
    onRecentSearchClick: (String) -> Unit,
    onFabClick: () -> Unit,
    onNavigateToFilteredDiaryScreen: (LocalDate) -> Unit,
    onShowDetailModal: () -> Unit,
    onDismissDetailModal: () -> Unit,
    isDetailModalVisible: Boolean,
    onDeleteDiary: (Long) -> Unit,
    allDiaryDates: List<LocalDate>
) {
    val systemUiController = rememberSystemUiController()
    val backgroundColor = Color(0xFFFFFFFF) // 상태바 배경색 (배경과 맞춤)

    SideEffect {
        systemUiController.setStatusBarColor(
            color = backgroundColor, // ✅ 상태바를 앱 배경색과 동일하게 설정
        )
    }

    BackHandler {
        navController.popBackStack(route = "home", inclusive = false)
    }

    //val lazyListState = rememberLazyListState()

    // 드래그 버튼의 상태 (ic_bottom_arrow 또는 ic_top_arrow)
    val dragIcon = when {
        isSearchVisible -> R.drawable.ic_top_arrow // 검색 상태에서는 아래로 화살표
        isCalendarVisible -> R.drawable.ic_top_arrow // 캘린더가 보이는 상태
        else -> R.drawable.ic_bottom_arrow // 기본 상태
    }

//    // TopBar 확장 여부
//    var isExpanded by remember { mutableStateOf(false) }
//    // 디테일 모달의 표시 여부 상태 관리
//    var isDetailModalVisible by remember { mutableStateOf(false) }
//    // 🔹 검색 실행 여부를 추적하는 변수
//
    var topBarHeight by remember { mutableStateOf(65.dp) }

    // var isSearchTriggered by remember { mutableStateOf(false) } // 🔹 검색 버튼이 눌렸는지 여부를 저장하는 상태 변수

    var selectedDiaryId by remember { mutableStateOf<Long?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f) // ✅ LazyColumn이 BottomNavigation을 밀어내지 않도록 가변 높이 적용
                //.background(Color(0xFFFEF6F2)) // ✅ 부드러운 배경색 추가
            ) {
                // ✅ 캘린더 또는 검색창이 열렸을 때만 배경을 블러 처리
                if (isSearchVisible || isCalendarVisible) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFFBDDC8).copy(alpha = 0.1f),
                                        Color(0xFFFBDDC8).copy(alpha = 0.3f),
                                        Color(0xFFFBDDC8).copy(alpha = 0.5f),
                                        Color(0xFFFBDDC8).copy(alpha = 0.7f),
                                        Color(0xFFFEDDC8).copy(alpha = 0.85f)  // ✅ 부드러운 그라디언트 유지
                                    ),
                                    startY = 0f,
                                    endY = Float.POSITIVE_INFINITY
                                )
                            )
                            .blur(50.dp) // ✅ 블러 강도를 높여 기존과 비슷한 효과를 줌
                            .zIndex(1f)
                    )
                }
                if (searchResults.isNotEmpty()){
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFFEF6F2))
                            .padding(top = 50.dp)
                    ) {
                        LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize()) {
                            item { Spacer(modifier = Modifier.height(50.dp)) }
                            items(searchResults) { diary ->
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
                }
                else {
                    // ✅ 검색결과가 없을 경우 빈 화면 표시
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFFEF6F2)),
                        contentAlignment = Alignment.Center // ✅ 이미지가 하단에 붙도록 정렬
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.no_search_results), // ✅ Drawable에 있는 이미지 사용
                            contentDescription = "초대장 이미지",
                            modifier = Modifier
                                .fillMaxWidth()
                        )
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
                    calendarContent = { modifier ->
                        CalendarView(
                            modifier = modifier,
                            onDateSelected = { selectedDate ->
                                Log.d("HomeScreen", "📌 2. CalendarView에서 날짜 선택됨: $selectedDate")
                                onNavigateToFilteredDiaryScreen(selectedDate) // 🔹 네비게이션 실행
                            },
                            diaryDates = allDiaryDates
                        )
                    },
                    recentSearches = recentSearches,
                    onRecentSearchClick = onRecentSearchClick,
                    onHeightChange = { height -> topBarHeight = height }

                )
                // ✅ 드래그 가능 영역
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = topBarHeight)
                        .height(30.dp)
                        .background(Color.Transparent)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onCalendarToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        modifier = Modifier.size(42.dp, 14.dp),
                        onClick = { onCalendarToggle() }
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
            }
            // ✅ 4. BottomNavigationBarWithFAB (항상 하단에 고정)
//            BottomNavigationBarWithFAB(
//                selectedTab = "diary",
//                onTabSelected = { /* 탭 변경 로직 */ },
//                onFabClick = onFabClick
//            )
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
}
