package com.umc.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.umc.mypage.components.TopBarComponent

@Composable
fun LockSettingsScreen(
    onLockPassword: () -> Unit,
    onChangePassword: () -> Unit,
    isPinSet: Boolean,
    clearPin: () -> Unit,
    onFabClick: () -> Unit = {}
){
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
                        .navigationBarsPadding()
                        .padding(top = 60.dp)
                        .background(Color.White)
                        .padding(horizontal = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item { Spacer(modifier = Modifier.height(23.dp)) }
                    item {
                        NotificationSettingItem(
                            title = "잠금 설정",
                            checked = isPinSet,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    onLockPassword()
                                } else {
                                    clearPin()
                                }
                            },
                            //onSwitchOn = { onLockPassword() },
                            bottomContent = {
                                if (isPinSet) {
                                    Spacer(modifier = Modifier.height(22.dp))

                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .clickable { onChangePassword() },
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {

                                        Text(text = "암호 변경", fontSize = 16.sp, fontWeight = FontWeight.W400)

                                        Image(
                                            painter = painterResource(id = R.drawable.ic_next_arrow),
                                            contentDescription = "다음 화면으로 가기",
                                            modifier = Modifier
                                                .width(8.dp)
                                                .height(16.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(11.dp))
                                }
                            }
                        )
                    }
                    item {
                        PasswordRecoveryNotice()
                    }

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

@Composable
fun PasswordRecoveryNotice() {
    Spacer(modifier = Modifier.height(11.dp))

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_warning_red),
            contentDescription = "경고 이미지",
            modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = "암호 분실시 앱을 삭제 후 재설치한 뒤, [마이페이지]에서 암호 잠금을 해제하거나 변경하실 수 있습니다.",
            fontSize = 12.sp,
            color = Color(0xFFB1B1B1),
            lineHeight = 15.sp,

        )
    }
}