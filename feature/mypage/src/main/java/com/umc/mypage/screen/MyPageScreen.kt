package com.umc.mypage.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.core.model.LoginType
import com.umc.core.model.UserInfo
import com.umc.core.model.UserStatus
import com.umc.design.component.CustomHeader
import com.umc.design.component.CustomPopup
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider
import java.text.NumberFormat.getNumberInstance
import java.util.Locale.US

@Composable
fun MyPageScreen(
    userInfo: UserInfo?,
    errorMessage: String?,
    onUpdateProfileButtonClicked: () -> Unit,
    onNotificationSettingButtonClicked: () -> Unit,
    onPinLockButtonClicked: () -> Unit,
    onLogoutButtonClicked: () -> Unit,
    onLeaveUserButtonClicked: () -> Unit,
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .background(LocalColorTheme.current.secondary[100])
            .fillMaxSize()
    ) {
        CustomHeader(
            centerText = "마이페이지",
        )
        if (userInfo != null) Column(
            modifier = Modifier.padding(horizontal = 22.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "${userInfo.name}님",
                fontWeight = FontWeight.W800,
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(start = 8.dp),
            )
            Spacer(modifier = Modifier.height(15.dp))
            SummarySection(
                diaryCount = userInfo.totalDiaryCount,
                points = userInfo.point,
            )
            Spacer(modifier = Modifier.height(36.dp))
            SettingCard(title = "앱 설정") {
                SettingCardItem(label = "프로필 수정", onClick = onUpdateProfileButtonClicked)
                SettingCardItem(label = "알림 설정", onClick = onNotificationSettingButtonClicked)
                SettingCardItem(label = "암호 잠금", onClick = onPinLockButtonClicked)
            }
            Spacer(modifier = Modifier.height(12.dp))
            SettingCard(title = "기타") {
                SettingCardItem(label = "로그아웃", onClick = { showLogoutDialog = true })
                SettingCardItem(label = "탈퇴하기", onClick = onLeaveUserButtonClicked)
            }
        } else {
            Text(
                text = errorMessage ?: "유저 정보를 불러오는 중입니다..",
                color = Color(0xFFFF8072),
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(50.dp),
            )
        }
    }

    if (showLogoutDialog) CustomPopup(
        title = "로그아웃 하시겠습니까?",
        message = "언제든 따따와 함께하고 싶다면 찾아와 주세요!",
        onDismiss = { showLogoutDialog = false },
        onConfirm = {
            showLogoutDialog = false
            onLogoutButtonClicked()
        },
    )
}

@Composable
private fun SummarySection(
    diaryCount: Int,
    points: Long,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .dropShadow(
                shape = RoundedCornerShape(22.dp),
                shadow = Shadow(
                    radius = 10.dp,
                    color = Color(0xFF806E33),
                    offset = DpOffset(0.dp, 2.dp),
                    alpha = 0.1f,
                ),
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(22.dp),
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SummaryItem(label = "나의 일기", value = diaryCount)
            Box(
                modifier = Modifier
                    .height(30.dp)
                    .width(1.dp)
                    .background(Color(0xFFFFE6E1))
            )
            SummaryItem(label = "포인트", value = points)
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: Number,
) {
    Row(
        modifier = Modifier.padding(vertical = 15.dp),
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.W400,
            letterSpacing = (-0.5).sp,
            color = LocalColorTheme.current.grey[700],
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = getNumberInstance(US).format(value),
            fontSize = 14.sp,
            fontWeight = FontWeight.W800,
            letterSpacing = (-0.5).sp,
            color = LocalColorTheme.current.primary[500],
        )
    }
}

@Composable
private fun SettingCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .dropShadow(
                shape = RoundedCornerShape(22.dp),
                shadow = Shadow(
                    radius = 10.dp,
                    color = Color(0xFF806E33),
                    offset = DpOffset(0.dp, 2.dp),
                    alpha = 0.1f,
                ),
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(22.dp),
            )
    ) {
        Column(
            modifier = Modifier.padding(
                start = 30.dp,
                top = 20.dp,
                end = 35.dp,
                bottom = 19.dp,
            )
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.W800,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content.invoke(this)
        }
    }
}

@Composable
private fun SettingCardItem(
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = null,
                onClick = onClick,
            )
            .padding(start = 10.dp, bottom = 7.dp),
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.W400,
            letterSpacing = (-0.5).sp,
            color = LocalColorTheme.current.grey[700],
        )
    }
}

@Preview
@Composable
private fun PreviewMyPageScreen() {
    ThemeProvider {
        MyPageScreen(
            userInfo = UserInfo(
                id = 0,
                name = "김따따",
                loginType = LoginType.KAKAO,
                email = "email@email.com",
                profileImageUrl = "",
                point = 12345,
                status = UserStatus.ACTIVE,
                totalDiaryCount = 123,
            ),
            errorMessage = null,
            onUpdateProfileButtonClicked = {},
            onNotificationSettingButtonClicked = {},
            onPinLockButtonClicked = {},
            onLogoutButtonClicked = {},
            onLeaveUserButtonClicked = {},
        )
    }
}