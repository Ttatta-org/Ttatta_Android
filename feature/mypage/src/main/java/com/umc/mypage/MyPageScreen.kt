package com.umc.mypage

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
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
import coil3.compose.AsyncImage
import com.umc.core.model.LoginType
import com.umc.core.model.UserInfo
import com.umc.core.model.UserStatus
import com.umc.mypage.components.BottomNavigationBarWithFAB
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.umc.mypage.components.TopBarComponent
import com.umc.mypage.R
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MyPageScreen(
    userInfo: UserInfo?,
    isLoading: Boolean,
    errorMessage: String?,
    onLogout: () -> Unit,
    onLeaveUser: () -> Unit,
    onFabClick: () -> Unit,
) {

    val systemUiController = rememberSystemUiController()
    val backgroundColor = Color(0xFFFFFFFF) // 상태바 배경색 (배경과 맞춤)

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
                        .background(Color(0xFFFFF6F2))
                        .padding(horizontal = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item { Spacer(modifier = Modifier.height(40.dp)) }
                    item {
                        if (userInfo != null) {
                            ProfileSection(
                                name = userInfo.name,
                                profileImage = userInfo.profileImageUrl
                            )

                            Spacer(modifier = Modifier.height(26.dp))

                            SummarySection(
                                diaryCount = userInfo.totalDiaryCount,
                                points = userInfo.point
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            AppSettingsSection(
                                themeSubtitle = "기본 테마",
                                notificationsEnabled = false,
                                passwordLockEnabled = false,
                                onThemeChangeClick = { /* 테마 변경 로직 */ },
                                onNotificationToggle = {  },
                                onPasswordLockToggle = {  },
                                onLeaveUser = onLeaveUser,
                                onLogout = onLogout
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                        } else {
                            Text(
                                text = errorMessage ?: "유저 정보를 불러오는 중입니다..",
                                color = Color(0xFFFF8072),
                                fontSize = 12.sp
                            )
                        }
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
fun ProfileSection(name: String, profileImage: String?) {

    val displayName = "$name 님"

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        AsyncImage(
            model = profileImage ?: R.drawable.default_profile, // ✅ URL이 없으면 기본 이미지 사용
            contentDescription = "프로필 이미지",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            //contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = displayName,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp, // 텍스트 크기
                fontWeight = FontWeight.W800 // 텍스트 굵기
            ),
            color = Color(0xFF333333)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(12.dp)
                )
                .background(Color(0xFFFFEFE4)), // 버튼 배경색
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier
                    .padding(vertical = 3.dp, horizontal = 9.dp),
                text = "프로필 수정",
                fontSize = 13.sp,
                color = Color(0xFFFCA598) // 텍스트 색상
            )
        }
    }
}

@Composable
fun SummarySection(
    diaryCount: Int,
    points: Long) {

    Card(
        modifier = Modifier
            .fillMaxWidth() // ✅ 남은 공간을 가득 채우도록 설정
            .shadow(
                elevation = 2.dp, // 그림자의 높이 조정
                shape = RoundedCornerShape(22.dp), // 카드의 모서리 둥글기
                spotColor = Color(0xDE806E33),
                ambientColor = Color(0xDE806E33),
                clip = true // 모서리가 잘리도록 설정
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly, // 아이템들을 균등하게 배치
            verticalAlignment = Alignment.CenterVertically // 아이템들을 수직 중앙 정렬
        ) {
            SummaryItem(label = "나의 일기", value = diaryCount)

            // ✅ 세로 구분선
            Box(
                modifier = Modifier
                    .height(30.dp)
                    .width(0.5.dp)
                    .background(Color(0xFFFFE6E1))
            )

            SummaryItem(label = "포인트", value = points)
        }
    }
}

@Composable
fun SummaryItem(label: String, value: Number) {
    Row(
        modifier = Modifier.padding(vertical = 15.dp),
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF4B4B4B),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W400)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = pointNumberWithComma(value),
            fontSize = 14.sp,
            color = Color(0xFFFF8072),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W800),
        )
    }
}


//SummaryItem에 필요한 숫자 콤마 만들기
fun pointNumberWithComma(number: Number): String {
    return NumberFormat.getNumberInstance(Locale.US).format(number)
}

@Composable
fun AppSettingsSection(
    themeSubtitle: String,
    notificationsEnabled: Boolean,
    passwordLockEnabled: Boolean,
    onThemeChangeClick: () -> Unit,
    onNotificationToggle: (Boolean) -> Unit, // ✅ 알림 설정 변경 이벤트 추가
    onPasswordLockToggle: (Boolean) -> Unit, // ✅ 암호 잠금 설정 변경 이벤트 추가
    onLeaveUser: () -> Unit, // ✅ 탈퇴하기 이벤트 추가
    onLogout: () -> Unit // ✅ 로그아웃 이벤트 추가
) {
    Card(
        modifier = Modifier
            //.width(340.dp) // 카드 너비 설정
            .wrapContentHeight() // 카드 높이 동적으로 설정
            .shadow(
                elevation = 2.dp, // 그림자의 높이 조정
                shape = RoundedCornerShape(22.dp), // 카드의 모서리 둥글기
                spotColor = Color(0xDE806E33),
                ambientColor = Color(0xDE806E33),
                clip = true // 모서리가 잘리도록 설정
            ),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // 카드 배경 색상 설정
        )
    ) {
        Box {
            Column(
                modifier = Modifier.padding(
                    start = 30.dp,
                    top = 20.dp,
                    end = 35.dp,
                    bottom = 19.dp
                )
            ) {
                // "앱 설정" 제목
                Text(
                    text = "앱 설정",
                    fontSize = 15.sp,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W800),
                    color = Color(0xFF000000), // 제목 색상
                    modifier = Modifier.padding(bottom = 12.dp) // 아래 여백 추가
                )

                // 테마 변경 설정
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onThemeChangeClick() }
                        .padding(start = 10.dp, bottom = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "테마",
                        fontSize = 14.sp,
                        color = Color(0xFF4B4B4B),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W400)
                    )
                    Spacer(modifier = Modifier.weight(1f)) // 여백 추가
                    Text(
                        text = themeSubtitle,
                        fontSize = 14.sp,
                        color = Color(0xFFFFD0C8), // 서브 텍스트 색상
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W400)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onThemeChangeClick() }
                        .padding(start = 10.dp, bottom = 7.dp),
                    //verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "알림 설정",
                        fontSize = 14.sp,
                        color = Color(0xFF4B4B4B),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W400)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onThemeChangeClick() }
                        .padding(start = 10.dp, bottom = 7.dp),
                    //verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "암호 잠금",
                        fontSize = 14.sp,
                        color = Color(0xFF4B4B4B),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W400)
                    )
                }

//                // 알림 설정
//                SettingSwitchItem(
//                    title = "알림 설정",
//                    isChecked = notificationsEnabled,
//                    onCheckedChange = onNotificationToggle
//                )
//
//                // 암호 잠금
//                SettingSwitchItem(
//                    title = "암호 잠금",
//                    isChecked = passwordLockEnabled,
//                    onCheckedChange = onPasswordLockToggle
//                )
//
//                // 점선 구분선
//                Spacer(modifier = Modifier.height(10.dp))
//                DashedDivider()
//                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Card(
        modifier = Modifier
            //.width(340.dp) // 카드 너비 설정
            .wrapContentHeight() // 카드 높이 동적으로 설정
            .shadow(
                elevation = 2.dp, // 그림자의 높이 조정
                shape = RoundedCornerShape(22.dp), // 카드의 모서리 둥글기
                spotColor = Color(0xDE806E33),
                ambientColor = Color(0xDE806E33),
                clip = true // 모서리가 잘리도록 설정
            ),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // 카드 배경 색상 설정
        )
    ) {
        Box {
            Column(
                modifier = Modifier.padding(
                    start = 30.dp,
                    top = 20.dp,
                    end = 35.dp,
                    bottom = 19.dp
                )
            ) {
                // "기타" 제목
                Text(
                    text = "기타",
                    fontSize = 15.sp,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W800),
                    color = Color(0xFF000000), // 제목 색상
                    modifier = Modifier.padding(bottom = 12.dp) // 아래 여백 추가
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLogout() }
                        .padding(start = 10.dp, bottom = 7.dp),
                    //verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "로그아웃",
                        fontSize = 14.sp,
                        color = Color(0xFF4B4B4B),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W400)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLeaveUser() }
                        .padding(start = 10.dp, bottom = 7.dp),
                    //verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "탈퇴하기",
                        fontSize = 14.sp,
                        color = Color(0xFF4B4B4B),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W400)
                    )
                }
            }
        }
    }
}


@Composable
fun SettingSwitchItem(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            color = Color(0xFF8E8E8E),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.weight(1f)) // 여백 추가

        // 커스텀 Switch 사용
        CustomSwitch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
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

@Composable
fun DashedDivider() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp) // Divider의 높이 조정
    ) {
        val dashWidth = 10f // 대시의 길이
        val gapWidth = 6f // 대시 사이의 간격
        val strokeWidth = 2f // 대시의 두께
        val color = Color(0xFFFCAD98)

        var currentX = 0f
        while (currentX < size.width) {
            // Draw a single dash
            drawLine(
                color = color,
                start = Offset(currentX, size.height / 2),
                end = Offset(currentX + dashWidth, size.height / 2),
                strokeWidth = strokeWidth
            )
            currentX += dashWidth + gapWidth // Move to the next dash position
        }
    }
}

@Preview(showBackground = true, name = "MyPageScreen Preview")
@Composable
fun PreviewMyPageScreen() {
    // ✅ 가짜 사용자 데이터 생성
    val mockUserInfo = UserInfo(
        id = 1L,
        name = "서연",
        loginType = LoginType.REGULAR, // ✅ 이메일 로그인
        email = "seoyeon@example.com",
        profileImageUrl = null, // ✅ 프로필 이미지 없음 (기본 이미지 표시)
        point = 1300L,
        status = UserStatus.ACTIVE, // ✅ 활성 상태
        totalDiaryCount = 129
    )

    // ✅ Preview에서 사용할 기본 상태
    MyPageScreen(
        userInfo = mockUserInfo,
        isLoading = false,
        errorMessage = null,
        onLogout = { /* 로그아웃 테스트 */ },
        onLeaveUser = { /* 탈퇴 테스트 */ },
        onFabClick = {}
    )
}
