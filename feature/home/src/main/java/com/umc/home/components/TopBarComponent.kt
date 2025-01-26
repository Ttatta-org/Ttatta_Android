package com.umc.home.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.umc.home.R

@Composable
fun TopBarComponent(
    isExpanded: Boolean,
    isSearchVisible: Boolean,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onSearchToggle: () -> Unit,
    onCalendarToggle: () -> Unit,
    calendarContent: @Composable (Modifier) -> Unit
) {
    val baseHeight = when {
        isSearchVisible -> 230.dp
        isExpanded -> 420.dp
        else -> 65.dp
    }
    val imageHeight by animateDpAsState(targetValue = baseHeight)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(imageHeight),
        contentAlignment = Alignment.BottomCenter,
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.full_top_bar),
            contentDescription = "배경 이미지",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
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
                                onSearch = onSearch,
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
                                if (isSearchVisible) {
                                    onSearch()
                                }
                                onSearchToggle()
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
        }
    }
}





// 검색창 모양의 Composable 함수
//@Composable
//fun SearchBar(
//    query: String,
//    onQueryChange: (String) -> Unit,
//    onSearch: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    // 검색창
//    Row(
//        modifier = Modifier
//            .background(
//                color = Color(0xFFFEF6F2), // 배경 색상
//                shape = RoundedCornerShape(15.dp) // 둥근 모서리
//            )
//            //.fillMaxWidth()
//            .padding(start = 10.dp, end = 55.dp, top = 5.dp, bottom = 5.dp), // 안쪽 여백
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(
//            text = "찾고 싶은 내용을 입력해주세요!", // placeholder 텍스트
//            fontSize = 13.sp,
//            color = Color(0xFFCACACA),
//            modifier = Modifier.padding(start = 8.dp)
//        )
//    }
//}



@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .background(
                color = Color(0xFFFEF6F2),
                shape = RoundedCornerShape(15.dp)
            )
            .padding(start = 10.dp, end = 55.dp, top = 5.dp, bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    text = "찾고 싶은 내용을 입력해주세요!",
                    fontSize = 13.sp,
                    color = Color(0xFFCACACA)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}





