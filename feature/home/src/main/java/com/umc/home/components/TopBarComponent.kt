package com.umc.home.components

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.umc.home.R
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.umc.core.model.Diary

@Composable
fun TopBarComponent(
    navController: NavHostController,
    isExpanded: Boolean,
    isSearchVisible: Boolean,
    searchQuery: String,
    searchResults: List<Diary>,
    isSearchTriggered: Boolean,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onSearchToggle: () -> Unit,
    onCalendarToggle: () -> Unit,
    calendarContent: @Composable (Modifier) -> Unit,
    recentSearches: List<String>, // 최근 검색어 리스트 추가
    onRecentSearchClick: (String) -> Unit, // 최근 검색어 클릭 동작 추가
    onHeightChange: (Dp) -> Unit
) {
    val baseHeight = when {
        isSearchVisible -> 230.dp
        isExpanded -> 420.dp
        else -> 65.dp
    }
    val imageHeight by animateDpAsState(targetValue = baseHeight)

    val context = LocalContext.current

    // ✅ 높이 값이 변경될 때 외부로 전달 (dragIcon 위치 조정 가능)
    LaunchedEffect(imageHeight) {
        onHeightChange(imageHeight)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(imageHeight)
            .background(Color.Transparent)
            .zIndex(2f),
        contentAlignment = Alignment.BottomCenter,
    ) {
        // 배경 이미지
//        Image(
//            painter = painterResource(id = R.drawable.full_top_bar),
//            contentDescription = "배경 이미지",
//            alignment = Alignment.BottomCenter,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier.fillMaxSize()
//        )
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
            modifier = Modifier.fillMaxWidth(), // 필요에 따라 수정
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
        ) {
            // 상단 Row를 별도의 Box로 TopCenter에 배치
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 왼쪽 로고
                    Image(
                        painter = painterResource(id = R.drawable.ic_ttatta_logo),
                        contentDescription = "로고",
                        modifier = Modifier
                            .width(30.dp)
                            .height(26.dp)
                    )

                    // 검색창과 아이콘 모두를 포함하는 Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = if (!isSearchVisible) Arrangement.End else Arrangement.SpaceBetween
                    ) {
                        if (isSearchVisible) {
                            // 검색창 표시
                            SearchBar(
                                query = searchQuery,
                                onQueryChange = onQueryChange,
                                onSearch = { onSearch(searchQuery) },
                                modifier = Modifier.weight(4f)
                            )
                        } else {
                            IconButton(
                                onClick = { /* 위치 핀 클릭 동작 */ },
                                modifier = Modifier.size(22.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_location_pin),
                                    contentDescription = "위치 핀",
                                    modifier = Modifier
                                        .width(16.dp)
                                        .height(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp)) // 검색창과 검색 아이콘 간격

                        // 검색 아이콘 (항상 동일한 위치에 유지)
                        IconButton(
                            onClick = {
//                                if (isSearchVisible) {
//                                    onSearch()
//                                }
//                                onSearchToggle()
                                if (isSearchVisible) {
                                    onSearch(searchQuery)
                                    onSearchToggle() // ✅ 검색 후 검색창 닫기
                                    navController.navigate("search")
                                } else {
                                    onSearchToggle() // ✅ 검색창을 열기
                                }
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_search),
                                contentDescription = "검색",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // 달력을 BottomCenter에 배치
            if (!isSearchVisible && isExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp),
                    contentAlignment = Alignment.Center
                ) {

                    calendarContent(Modifier.height(400.dp))

                }
            }

            if (isSearchVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    if (searchResults.isEmpty() && isSearchTriggered) { // 🔹 검색 버튼을 눌렀을 때만 검사
                        Log.d("HomeScreen", "🚫 검색 결과 없음")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_error), // 에러 아이콘
                                contentDescription = "에러 아이콘",
                                modifier = Modifier.size(15.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "찾으시는 검색어의 결과가 없어요!",
                                fontSize = 14.sp,
                                color = Color(0xFF4B4B4B) // 텍스트 색상
                            )
                        }
                    } else {
                        // 🔹 검색 결과가 있거나 검색을 실행하지 않은 상태면 최근 검색어 표시
                        Log.d("HomeScreen", "✅ 검색 결과 있음")
                        RecentSearches(
                            recentSearches = recentSearches,
                            onRecentSearchClick = { query ->
                                onQueryChange(query)
                            }
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
                shape = RoundedCornerShape(15.dp)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        // Placeholder 텍스트를 기본 텍스트처럼 보이게
        if (query.isEmpty()) {
            Text(
                text = "찾고 싶은 내용을 입력해주세요!",
                fontSize = 13.sp,
                color = Color(0xFFCACACA),
                modifier = Modifier.padding(start = 4.dp)
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp)
        )
    }
}


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
                fontSize = 14.sp,
                color = Color(0xFF4B4B4B),
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min) // ✅ Row 높이 최소 보장
            ) {
                recentSearches.take(3).forEach { search -> // 최대 3개만 표시
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFFFDDDC1), RoundedCornerShape(16.dp))
                            .background(Color(0xFFFEF6F2))
                            .clickable { onRecentSearchClick(search) }
                            .padding(horizontal = 10.dp, vertical = 5.dp) // ✅ 내부 패딩 키움
                    ) {
                        Text(
                            text = search,
                            fontSize = 12.sp, // ✅ 폰트 크기 키움
                            color = Color(0xFF333333) // ✅ 더 진한 색상으로 변경
                        )
                    }
                }
            }
        }
    }
}



