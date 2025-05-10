package com.umc.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.umc.mypage.components.TopBarComponent

@Composable
fun LockSettingsScreen(){
    val systemUiController = rememberSystemUiController()
    val backgroundColor = Color(0xFFFFFFFF) // 상태바 배경색 (배경과 맞춤)

    var lockSetting by remember { mutableStateOf(false) }

    SideEffect {
        systemUiController.setStatusBarColor(
            color = backgroundColor, // ✅ 상태바를 앱 배경색과 동일하게 설정
        )
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .weight(1f) // ✅ BottomNavigation을 밀어내지 않도록 LazyColumn에 weight 적용
            ) {
                // ✅ 2. LazyColumn (스크롤 가능한 콘텐츠)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp)
                        .background(Color.White)
                        .padding(horizontal = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item { Spacer(modifier = Modifier.height(23.dp)) }
                    item {
                        NotificationSettingItem(
                            title = "잠금 설정",
                            checked = lockSetting,
                            onCheckedChange = { lockSetting = it },
                        )
                    }
                    item { Spacer(modifier = Modifier.height(22.dp)) }

                }

                // ✅ 3. TopBar (스크롤 가능한 LazyColumn 위에 배치)
                TopBarComponent()
            }

            // ✅ 4. BottomNavigationBarWithFAB (항상 하단에 고정)
//            BottomNavigationBarWithFAB(
//                selectedTab = "mypage",
//                onTabSelected = { /* 탭 변경 로직 */ },
//                onFabClick = onFabClick
//            )

        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewLockSettingScreen() {
    LockSettingsScreen()
}