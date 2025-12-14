package com.umc.home.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.component.CustomHeader
import com.umc.home.R
import com.umc.home.ScreenMode
import com.umc.home.TopBarState
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TopBarComponent(
    topBarState: TopBarState,
    screenMode: ScreenMode,
    onBackClick: () -> Unit,
    searchQuery: String,
    isSearchTriggered: Boolean,
    areSearchResultsEmpty: Boolean,
    onQueryChange: (String) -> Unit,
    onSearchSubmitted: (String) -> Unit,
    onSearchToggle: () -> Unit,
    calendarContent: @Composable (Modifier) -> Unit,
    recentSearches: List<String>,
    onRecentSearchClick: (String) -> Unit
) {
    CustomHeader(
        showLogo = screenMode is ScreenMode.Home,
        centerText = if (screenMode is ScreenMode.Filtered && topBarState == TopBarState.Closed) {
            screenMode.date.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일", Locale.KOREAN))
        } else null,
        centerTextLetterSpacing = (-0.4).sp,
        onBackButtonClicked = if (screenMode !is ScreenMode.Home) onBackClick else null,
        headerTrailing = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 22.dp)
            ) {
                AnimatedVisibility(
                    visible = topBarState == TopBarState.SearchOpen,
                ) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = onQueryChange,
                        onSearch = { onSearchSubmitted(searchQuery) },
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                }
                Image(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "검색",
                    modifier = Modifier
                        .width(22.dp)
                        .height(24.dp)
                        .clickableNoRipple { onSearchToggle() }
                )
            }
        },
        showShadow = topBarState != TopBarState.Closed,
    ) {
        // --- 캘린더 ---
        AnimatedVisibility(
            visible = topBarState == TopBarState.CalendarOpen,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            calendarContent(Modifier.padding(horizontal = 22.dp))
        }

        // --- 최근 검색어 ---
        AnimatedVisibility(
            visible = topBarState == TopBarState.SearchOpen,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            // Column으로 "최근 검색어"와 "결과 없음"을 둘 다 담습니다.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally // "결과 없음"을 중앙 정렬
            ) {
                // 최근 검색어 (항상 왼쪽 정렬)
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
                    RecentSearches(
                        recentSearches = recentSearches,
                        onRecentSearchClick = onRecentSearchClick
                    )
                }

                // "검색 결과 없음" 메시지 (조건부 표시)
                if (isSearchTriggered && areSearchResultsEmpty) {
                    Spacer(modifier = Modifier.height(16.dp)) // 둘 사이의 간격
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_error_small),
                            contentDescription = "에러 아이콘",
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "찾으시는 검색어의 결과가 없어요!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W400,
                            lineHeight = 18.sp,
                            color = Color(0xFFFF6060)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = Color(0xFFFEF6F2),
                shape = RoundedCornerShape(18.dp)
            )
            .height(36.dp)
            .padding(horizontal = 15.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f), // 텍스트필드가 남는 공간을 모두 차지
                contentAlignment = Alignment.CenterStart
            ) {
                // Placeholder 텍스트를 기본 텍스트처럼 보이게
                if (query.isEmpty()) {
                    Text(
                        text = "찾고 싶은 내용을 입력해주세요!",
                        style = TextStyle(
                            fontSize = 13.sp,
                            color = Color(0xFF8E8E8E),
                            fontWeight = FontWeight.W400,
                            lineHeight = 20.sp,
                            letterSpacing = 0.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onSearch() // "검색" 버튼 클릭 시 동작
                        }
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.Black,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (query.isNotEmpty()) {
                Spacer(modifier = Modifier.width(15.dp)) // 텍스트와 아이콘 사이 간격

                Image(
                    // ⚠️ 'ic_search_cancle'을 drawable에 추가하셔야 합니다.
                    painter = painterResource(id = R.drawable.ic_search_cancle),
                    contentDescription = "텍스트 지우기",
                    modifier = Modifier
                        .size(16.dp) // 아이콘 크기
                        .clickableNoRipple {
                            onQueryChange("") // ✅ 클릭 시 텍스트를 비웁니다.
                        }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecentSearches(
    recentSearches: List<String>, // 최근 검색어 리스트
    onRecentSearchClick: (String) -> Unit // 클릭 시 동작
) {
    Log.d("RecentSearches", "최근 검색어 리스트: $recentSearches")

    if (recentSearches.isNotEmpty()) { // ✅ 검색어가 있을 때만 표시
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "최근 검색어",
                fontSize = 13.sp,
                color = Color(0xFF8E8E8E),
                fontWeight = FontWeight.W400,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                // 세로 (줄바꿈 시) 항목들 사이의 간격
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                //.height(IntrinsicSize.Min) // Row 높이 최소 보장
            ) {
                recentSearches
                    .take(4)
                    .forEach { search -> // 최대 3개만 표시
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(13.dp))
                                .border(1.dp, Color(0xFFFFD2AC), RoundedCornerShape(13.dp))
                                .background(Color(0xFFFEF6F2))
                                .clickableNoRipple { onRecentSearchClick(search) }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = search,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.W400,
                                color = Color(0xFF333333)
                            )
                        }
                    }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
