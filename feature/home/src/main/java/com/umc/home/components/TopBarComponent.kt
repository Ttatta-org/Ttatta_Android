package com.umc.home.components

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.umc.home.R
import com.umc.home.ScreenMode
import com.umc.home.TopBarState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TopBarComponent(
    navController: NavHostController,
    topBarState: TopBarState,

    screenMode: ScreenMode,
    selectedDate: LocalDate?, // Filtered 모드에서 날짜를 표시하기 위함
    onBackClick: () -> Unit,

    searchQuery: String,
    isSearchTriggered: Boolean,
    areSearchResultsEmpty: Boolean,
    onQueryChange: (String) -> Unit,
    onSearchSubmitted: (String) -> Unit,
    onSearchToggle: () -> Unit,
    onCalendarToggle: () -> Unit,
    calendarContent: @Composable (Modifier) -> Unit,
    recentSearches: List<String>,
    onRecentSearchClick: (String) -> Unit,
    onHeightChange: (Dp) -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.Transparent)
            .zIndex(2f)
            .onSizeChanged { size ->
                onHeightChange(with(density) { size.height.toDp() })
            },
        contentAlignment = Alignment.BottomCenter,
    ) {
        // 배경 이미지
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data("android.resource://${context.packageName}/${R.raw.view_top_bar}") // ✅ SVG 파일
                    .decoderFactory(SvgDecoder.Factory()) // ✅ SVG 지원
                    .build(),
                error = BitmapPainter( // ✅ 에러 시 사용할 기본 이미지
                    BitmapFactory.decodeResource(
                        context.resources, R.raw.view_top_bar_for_preview
                    ).asImageBitmap()
                )
            ),
            contentDescription = "배경 이미지",
            alignment = Alignment.BottomCenter,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(), // 필요에 따라 수정
        )
        Column(
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            // 상단 Row를 별도의 Box로 TopCenter에 배치
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                // --- ✅ 핵심: 두 가지 레이아웃을 조건부로 분리 ---
                val isFilteredAndSearchClosed =
                    (screenMode is ScreenMode.Filtered && topBarState != TopBarState.SearchOpen)

                if (isFilteredAndSearchClosed) {
                    // --- 1. Filtered 모드 + 검색창 닫힘 (가운데 날짜용 Box 레이아웃) ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 28.dp, vertical = 12.dp)
                            .height(IntrinsicSize.Min), // 높이를 자식에 맞춤
                    ) {
                        // --- 왼쪽: 뒤로가기 버튼 ---
                        Image(
                            painter = painterResource(id = R.drawable.ic_arrow_left_new),
                            contentDescription = "뒤로 가기",
                            modifier = Modifier
                                .width(10.dp)
                                .height(16.25.dp)
                                .clickable { onBackClick() }
                                .align(Alignment.CenterStart) // ✅ 왼쪽 정렬
                        )

                        // --- 중앙: 날짜 텍스트 ---
                        Text(
                            text = selectedDate?.format(
                                DateTimeFormatter.ofPattern("yyyy년 MM월 dd일", Locale.KOREAN)
                            ) ?: "날짜 없음",
                            style = TextStyle( // 🎨 스크린샷과 유사한 스타일
                                fontSize = 17.sp,
                                fontWeight = FontWeight.W700,
                                color = Color(0xFFF07B7B) // (디자인에 맞게 색상 변경)
                            ),
                            modifier = Modifier.align(Alignment.Center) // ✅ 중앙 정렬
                        )

                        // --- 오른쪽: 아이콘 2개 (Row로 묶음) ---
                        Row(
                            modifier = Modifier.align(Alignment.CenterEnd), // ✅ 오른쪽 정렬
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { onSearchToggle() }, // 검색창 열기
                                modifier = Modifier.size(24.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_search),
                                    contentDescription = "검색",
                                    modifier = Modifier.width(22.dp).height(24.dp)
                                )
                            }
                        }
                    }
                } else {
                    // --- 2. 그 외 모든 경우 (검색창 열림, 홈 모드 등) (기존 Row 레이아웃) ---
                    // ✅ 이 부분은 처음에 질문 주셨던 원본 코드와 동일합니다.
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 28.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 왼쪽 로고 또는 뒤로가기 (상황에 맞게 표시됨)
                        if (screenMode is ScreenMode.Filtered) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_arrow_left_new),
                                contentDescription = "뒤로 가기",
                                modifier = Modifier
                                    .width(10.dp)
                                    .height(16.25.dp)
                                    .clickable { onBackClick() }
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_ttatta_logo),
                                contentDescription = "로고",
                                modifier = Modifier
                                    .width(34.6.dp)
                                    .height(30.dp)
                            )
                        }

                        // 검색창과 아이콘 모두를 포함하는 Row (기존 로직)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = if (topBarState == TopBarState.SearchOpen) Arrangement.SpaceBetween else Arrangement.End
                        ) {
                            AnimatedVisibility(visible = topBarState == TopBarState.SearchOpen) {
                                SearchBar(
                                    query = searchQuery,
                                    onQueryChange = onQueryChange,
                                    onSearch = { onSearchSubmitted(searchQuery) },
                                    modifier = Modifier.weight(4f) // ✅ 이 weight가 정상 작동
                                )
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                            IconButton(
                                onClick = {
                                    if (topBarState == TopBarState.SearchOpen) {
                                        onSearchSubmitted(searchQuery)
                                    } else {
                                        onSearchToggle()
                                    }
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_search),
                                    contentDescription = "검색",
                                    modifier = Modifier.width(22.dp).height(24.dp)
                                )
                            }
                        }
                    }
                }
            }

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

            Spacer(modifier = Modifier.height(10.dp))

//            // --- 드래그 핸들 ---
//            val dragIcon = when (topBarState) {
//                TopBarState.Closed -> R.drawable.ic_bottom_arrow
//                else -> R.drawable.ic_top_arrow
//            }
//            IconButton(
//                modifier = Modifier
//                    .align(Alignment.CenterHorizontally)
//                    .size(50.dp, 16.dp),
//                onClick = onCalendarToggle // 캘린더만 토글
//            ) {
//                Image(
//                    painter = painterResource(id = dragIcon),
//                    contentDescription = "Toggle Calendar",
//                    modifier = Modifier
//                        .width(50.dp)
//                        .height(16.dp)
//                )
//            }
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
                shape = RoundedCornerShape(15.5.dp)
            )
            .padding(horizontal = 17.dp, vertical = 6.dp)
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
                            color = Color(0xFFCACACA),
                            fontWeight = FontWeight.W400,
                            lineHeight = 20.sp,
                            letterSpacing = (-0.4).sp
                        )
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
                        .clickable {
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
                recentSearches.take(4).forEach { search -> // 최대 3개만 표시
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(13.dp))
                            .border(1.dp, Color(0xFFFFD2AC), RoundedCornerShape(13.dp))
                            .background(Color(0xFFFEF6F2))
                            .clickable { onRecentSearchClick(search) }
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



