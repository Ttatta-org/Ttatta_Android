package com.umc.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.umc.mypage.components.BottomNavigationBarWithFAB
import com.umc.mypage.components.TopBarComponent
import com.umc.mypage.R
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MyPageScreen(
    viewModel: MyPageViewModel,
    onTabSelected: (String) -> Unit,
    onFabClick: () -> Unit,
    onThemeChangeClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = { TopBarComponent() },
            bottomBar = {
                BottomNavigationBarWithFAB(
                    selectedTab = "mypage",
                    onTabSelected = onTabSelected
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFEF6F2))
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 프로필 섹션
                ProfileSection(name = uiState.displayName, profileImage = uiState.profileImage)
                Spacer(modifier = Modifier.height(16.dp))

                // 요약 섹션
                SummarySection(diaryCount = uiState.diaryCount, points = uiState.points)
                Spacer(modifier = Modifier.height(16.dp))

                // 앱 설정 섹션
                AppSettingsSection(
                    themeSubtitle = "기본 테마 사용 중",
                    notificationsEnabled = uiState.notificationsEnabled,
                    passwordLockEnabled = uiState.passwordLockEnabled,
                    onThemeChangeClick = onThemeChangeClick
                )
                Spacer(modifier = Modifier.height(16.dp))

            }
        }

        // Custom Floating Image Button
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            CustomFloatingImageButton(onClick = onFabClick)
        }
    }
}



@Composable
fun CustomFloatingImageButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .offset(y = (-40).dp) // 바텀 네비게이션에 걸치도록 위치 조정
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_add), // 꽃 모양 이미지
            contentDescription = "추가 버튼",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun ProfileSection(name: String, profileImage: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = profileImage),
            contentDescription = "프로필 이미지",
            modifier = Modifier
                .size(115.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 20.sp, // 텍스트 크기
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold // 텍스트 굵기
            ),
            color = Color(0xFF333333)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .width(74.dp) // 버튼 너비
                .height(20.dp) // 버튼 높이
                .clip(
                    RoundedCornerShape(12.dp)
                )
                .background(Color(0xFFFFEFE4)), // 버튼 배경색
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "내 프로필 수정",
                fontSize = 10.sp,
                color = Color(0xFFFCAD98) // 텍스트 색상
            )
        }
    }
}

@Composable
fun SummarySection(diaryCount: Int, points: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SummaryItem(label = "나의 일기", value = diaryCount)
        SummaryItem(label = "포인트", value = points)
    }
}

@Composable
fun SummaryItem(label: String, value: Int) {
    Card(
        modifier = Modifier
            .width(165.dp) // 각 카드의 너비 설정
            .height(50.dp) // 카드 높이 설정
            .shadow(
                elevation = 6.dp, // 그림자의 높이 조정
                shape = RoundedCornerShape(28.dp), // 카드의 모서리 둥글기
                spotColor = Color(0xDE806E38),
                ambientColor = Color(0xDE806E38),
                clip = true // 모서리가 잘리도록 설정
                ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // 카드 배경 색상 설정
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF4B4B4B), // 라벨 텍스트 색상 설정
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = formatNumberWithComma(value),
                fontSize = 15.sp,
                color = Color(0xFFFF7162), // 값 텍스트 색상 설정
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

//SummaryItem에 필요한 숫자 콤마 만들기
fun formatNumberWithComma(number: Int): String {
    return NumberFormat.getNumberInstance(Locale.US).format(number)
}

@Composable
fun AppSettingsSection(
    themeSubtitle: String,
    notificationsEnabled: Boolean,
    passwordLockEnabled: Boolean,
    onThemeChangeClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(340.dp) // 카드 너비 설정
            .wrapContentHeight() // 카드 높이 동적으로 설정
            .shadow(
                elevation = 6.dp, // 그림자의 높이 조정
                shape = RoundedCornerShape(28.dp), // 카드의 모서리 둥글기
                spotColor = Color(0xDE806E38),
                ambientColor = Color(0xDE806E38),
                clip = true // 모서리가 잘리도록 설정
            ),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // 카드 배경 색상 설정
        )
    ) {
        Box {
            Column(
                modifier = Modifier.padding(
                    start = 40.dp,
                    top = 27.dp,
                    end = 40.dp,
                    bottom = 27.dp
                )
            ) {
                // "앱 설정" 제목
                Text(
                    text = "앱 설정",
                    fontSize = 15.sp,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W600),
                    color = Color(0xFF333333), // 제목 색상
                    modifier = Modifier.padding(bottom = 12.dp) // 아래 여백 추가
                )

                // 테마 변경 설정
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onThemeChangeClick() }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "테마 변경",
                        fontSize = 12.sp,
                        color = Color(0xFF8E8E8E),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.weight(1f)) // 여백 추가
                    Text(
                        text = themeSubtitle,
                        fontSize = 12.sp,
                        color = Color(0xFFAAAAAA), // 서브 텍스트 색상
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // 알림 설정
                SettingSwitchItem(
                    title = "알림 설정",
                    isChecked = notificationsEnabled
                )

                // 암호 잠금
                SettingSwitchItem(
                    title = "암호 잠금",
                    isChecked = passwordLockEnabled
                )

                // 구분선 공간 확보
                Spacer(modifier = Modifier.height(20.dp))

                // "기타" 제목
                Text(
                    text = "기타",
                    fontSize = 15.sp,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W600),
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // 기타 설정
                Text(
                    text = "탈퇴하기",
                    fontSize = 12.sp,
                    color = Color(0xFF8E8E8E),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = false) { /* 탈퇴하기 로직 */ } // 클릭 비활성화
                        .padding(8.dp) // 간격 조절
                )

                Text(
                    text = "로그아웃",
                    fontSize = 12.sp,
                    color = Color(0xFF8E8E8E),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = false) { /* 로그아웃 로직 */ } // 클릭 비활성화
                        .padding(8.dp) // 간격 조절
                )
            }

            // 구분선 (Column 밖으로 살짝 튀어나오게 설정)
            Divider(
                color = Color(0xFFFCAD98),
                thickness = 1.dp,
                modifier = Modifier
                    .padding(start = 25.dp, end = 25.dp)
                    .offset(y = 190.dp) // Column 안에서 원하는 위치로 조절
            )
        }
    }
}


@Composable
fun SettingSwitchItem(
    title: String,
    isChecked: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color(0xFF8E8E8E),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.weight(1f)) // 여백 추가

        // 커스텀 Switch 사용
        CustomSwitch(
            checked = isChecked,
            onCheckedChange = {},
            modifier = Modifier.padding(end = 5.dp)
        )
    }
}

@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // 전체 Switch 박스
    Box(
        modifier = modifier
            .width(30.dp) // Switch 전체 너비
            .height(16.dp) // Switch 전체 높이
            .clip(
                RoundedCornerShape(8.dp) // 커스텀 Border-Radius
            )
            .background(
                if (checked) Color(0xFFFDDDC1) else Color(0xFFE1E1E1) // 상태에 따른 배경색
            )
            .clickable { onCheckedChange(!checked) }
            .padding(end = 1.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Thumb (Circle)
        Box(
            modifier = Modifier
                .size(14.dp) // Thumb 크기
                .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart) // 상태에 따른 위치
                .padding(1.dp) // Thumb 패딩
                .clip(CircleShape) // 원형
                .background(Color(0xFFF5F5F5)) // Thumb 배경색
        )
    }
}

@Preview(showBackground = true, name = "MyPageScreen Preview")
@Composable
fun PreviewMyPageScreen() {
    val mockUiState = MyPageUiState(
        name = "서연",
        profileImage = R.drawable.default_profile, // 기본 제공 이미지로 대체
        diaryCount = 129,
        points = 1300,
        notificationsEnabled = true,
        passwordLockEnabled = false
    )

    MyPageScreen(
        viewModel = object : MyPageViewModel() {
            override val uiState = MutableStateFlow(mockUiState)
        },
        onTabSelected = { /* Preview에서는 동작 없음 */ },
        onFabClick = { /* Preview에서는 동작 없음 */ },
        onThemeChangeClick = { /* Preview에서는 동작 없음 */ }
    )
}