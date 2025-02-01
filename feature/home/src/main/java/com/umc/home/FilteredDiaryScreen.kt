package com.umc.home

import android.text.Layout
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
    selectedDate: LocalDate,
    diaries: List<Diary>,
    onBack: () -> Unit,
    onFabClick: () -> Unit
) {
    val filteredDiaries = diaries.filter { it.date.toLocalDate() == selectedDate }
        .sortedByDescending { it.date }

    val recentSearches by viewModel.recentSearches.collectAsState() // ✅ ViewModel의 최근 검색어 사용
    val searchQuery by viewModel.searchQuery.collectAsState() // ✅ ViewModel의 검색어 사용
    val searchResults by viewModel.searchResults.collectAsState()
    var isSearchVisible by remember { mutableStateOf(false) }
    var isSearchTriggered by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopBarComponent(
                isExpanded = false,  // 필터된 화면에서는 달력 기능 없음
                isSearchVisible = isSearchVisible,
                searchQuery = searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                searchResults = searchResults,
                isSearchTriggered = isSearchTriggered,
                onSearch = {
                    viewModel.searchDiaries(searchQuery)
                    isSearchTriggered = true
                },
                onSearchToggle = { isSearchVisible = !isSearchVisible },
                onCalendarToggle = { /* 캘린더 없음 */ },
                calendarContent = {}, // 달력 비활성화
                recentSearches = recentSearches,
                onRecentSearchClick = { viewModel.updateSearchQuery(it) }
            )
        },
        bottomBar = {
            BottomNavigationBarWithFAB(
                selectedTab = "home",
                onTabSelected = { /* 탭 변경 로직 */ },
                onFabClick = onFabClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFEF6F2))

        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (searchResults.isNotEmpty() && isSearchTriggered) {

                    item { // ✅ Column을 사용해 padding 적용
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 25.dp, end = 25.dp) // ✅ 검색 결과에만 padding 추가
                        ) {
                            searchResults.forEach { diary ->
                                DiaryCard(
                                    diary = diary,
                                    onDetailClick = { /* isDetailModalVisible = true */ }
                                )
                            }
                        }
                    }
                } else {

                    items(filteredDiaries) { diary ->
                        FillteredDiaryByDate(diary = diary, onDetailClick = { /* 디테일 보기 */ })
                    }
                }


            }
        }

    }
}


@Composable
fun FillteredDiaryByDate(diary: Diary, onDetailClick: () -> Unit) {

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(top = 13.dp, bottom = 20.dp, start = 30.dp, end = 30.dp)
                .fillMaxWidth()

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

            // 상단 이미지
            Image(
                painter = painterResource(id = diary.imageUrl),
                contentDescription = "Diary Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp) // 이미지 높이 설정
                    .width(280.dp)
                    .clip(RoundedCornerShape(18.dp)),
                contentScale = ContentScale.Crop // 이미지 크롭 설정
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 날짜 텍스트
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
                    text = diary.date.formatToKorean(), // 날짜 텍스트
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
                    .background(Color(0xFFFDDDC1).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(vertical = 5.dp, horizontal = 12.dp)
            ) {
                Text(
                    text = diary.content,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF4B4B4B), // 텍스트 색상
                        fontSize = 12.sp
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