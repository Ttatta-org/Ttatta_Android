package com.umc.record

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@Composable
fun RecordEditLocationScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Topbar를 Box의 상단에 고정
        Box(
            modifier = Modifier
                .fillMaxWidth()
//                .height(97.dp) // Topbar 높이 유지
                .background(Color.White) // 배경색 추가하여 스크롤 시 레이어 문제 방지
                .zIndex(1f) // 스크롤되는 콘텐츠보다 위에 위치
        ) {
            Topbar()
        }

        // 네이버 지도
        

        // 바텀 시트
        EditLocationBottomSheet(location = "고래와")
    }
}

@Composable
fun Topbar() {
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
                .padding(horizontal = 32.dp, vertical = 12.dp)
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

            Spacer(modifier = Modifier.weight(1f))

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
                .height(150.dp)
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
                    .fillMaxSize()
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
                    onClick = { /* 버튼 클릭 로직 */ },
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

@Preview(showBackground = true)
@Composable
fun PreviewRecordEditLocationScreen() {
    RecordEditLocationScreen()
}