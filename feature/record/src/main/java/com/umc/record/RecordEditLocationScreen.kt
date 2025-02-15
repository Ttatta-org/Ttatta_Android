package com.umc.record

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.umc.record.component.ShadowedIcon

data class SelectedPosition(
    val x: Float,
    val y: Float,
    //val prop: DiaryCardProp,
)

@Composable
fun RecordEditLocationScreen(
    mapView: @Composable () -> Unit,
    onLocationButtonClicked: () -> Unit,
    location: String
) {
    val context = LocalContext.current

    // ✅ 위치 권한 확인 후 지도 활성화
    if (ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 네이버 지도
            Log.d("RecordEditLocationScreen", "🌍 mapView() 실행됨!") // ✅ 실행 로그 추가
            mapView()

            // 플로팅 버튼
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 12.96.dp, bottom = 180.dp), // 바텀시트(150dp) + 21dp 만큼 띄우기
                contentAlignment = Alignment.BottomEnd
            ) {
                IconButton(
                    onClick = onLocationButtonClicked,
                    modifier = Modifier
                        .size(84.dp)
                ) {
                    ShadowedIcon(
                        id = R.drawable.btn_location,
                        contentDescription = null,
                        width = 84.dp,
                        height = 84.dp,
                    )
                }
            }

            // 바텀 시트
            EditLocationBottomSheet(location = location)

            // Topbar를 Box의 상단에 배치 (최상단에 유지)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
//                .height(97.dp) // Topbar 높이 유지
                    .zIndex(1f)
            ) {
                Topbar()
            }
        }
    } else {
        println("🚨 Location permission is NOT granted! Skipping map rendering.") // ✅ 오류 확인 로그
    }
}

@Composable
fun Topbar() {
    var searchQuery by remember { mutableStateOf("") }

    val onQueryChange: (String) -> Unit = { newQuery ->
        searchQuery = newQuery
    }

    val onSearch: () -> Unit = {
        println("검색 실행: $searchQuery")
        // 검색 로직 추가 가능
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(97.dp) // TopBar 높이
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.img_topbartrans),
            contentDescription = "Top Bar Background",
            modifier = Modifier.fillMaxSize()
        )

        // TopBar 요소
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 32.dp, end = 22.dp, top = 32.dp, bottom = 12.dp)
                .zIndex(1f), // 이미지 위에 아이콘 배치
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 로고 이미지
            Icon(
                painter = painterResource(id = R.drawable.img_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(30.dp),
                tint = Color.Unspecified // 원본 색 유지
            )

            Spacer(modifier = Modifier.width(26.dp))

            // 검색 바
            SearchField(
                query = searchQuery,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                modifier = Modifier.weight(4f)
            )

            Spacer(modifier = Modifier.width(11.dp))

            // 검색 아이콘
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search",
                tint = Color.Unspecified // Tint 효과 제거
            )
        }
    }
}

@Composable
fun EditLocationBottomSheet(location: String) {
    Box(
        modifier = Modifier
            .fillMaxSize() // 전체 화면 크기 차지
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight() // 컨텐츠 내용에 따라 높이 조정
                .align(Alignment.BottomCenter) // 화면 하단 중앙에 고정
                .background(
                    color = Color(0xFFFEF6F2), // 배경색
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp) // 상단 모서리 둥글게
                )
                .padding(WindowInsets.navigationBars.asPaddingValues()) // 내비게이션 바 높이만큼 여백 추가
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 헤더 이미지
                Image(
                    painter = painterResource(id = com.umc.design.R.drawable.ic_header_deco), // 헤더 데코 이미지 리소스
                    contentDescription = "Header Decoration",
                    modifier = Modifier
                        .size(width = 39.18.dp, height = 32.dp) // 이미지 크기 설정
                        .padding(bottom = 11.dp) // 텍스트와 간격 추가
                )

                // 안내 텍스트
                Text(
                    text = "\'${location}\'(으)로 위치를 수정하시겠어요?",
                    fontSize = 16.sp,
                    color = Color(0xFFFF9681),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                Button(
                    onClick = {
                        Log.d("RecordEditLocationScreen", "📍 Returning to RecordScreen")
                    },
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFCAD98)),
                    modifier = Modifier
                        .height(50.dp)
                        .width(LocalConfiguration.current.screenWidthDp.dp - 123.dp)
                        .align(Alignment.CenterHorizontally)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(28.dp),
                            spotColor = Color(0xDE806E38),
                            ambientColor = Color(0xDE806E38),
                            clip = true
                        ),
                ) {
                    Text(
                        text = "설정하기",
                        fontSize = 16.sp,
                        color = Color(0xFFFFFFFF),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = Color(0xFFFEF6F2),
                shape = RoundedCornerShape(20.dp)
            )
            .border(1.dp, Color(0xFFFF9681), shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .fillMaxWidth()
            .height(31.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Placeholder 텍스트를 기본 텍스트처럼 보이게
        if (query.isEmpty()) {
            Text(
                text = "찾고 싶은 내용을 입력해주세요!",
                fontSize = 13.sp,
                color = Color(0xFF8E8E8E),
                modifier = Modifier
                    .padding(horizontal = 7.dp)
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
                .padding(horizontal = 7.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRecordEditLocationScreen() {
    RecordEditLocationScreen(
        mapView = {},
        onLocationButtonClicked = {},
        location = "Cafe Porte"
    )
}