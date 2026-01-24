package com.umc.mypage.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.component.CustomHeader
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider
import com.umc.mypage.R

@Composable
fun ProfileEditScreen(
    nickname: String?,
    email: String?,
    onBackButtonClicked: () -> Unit,
    onNicknameClicked: () -> Unit,
    onEmailClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CustomHeader(
            showLogo = false,
            centerText = "프로필 수정",
            onBackButtonClicked = onBackButtonClicked,
            backgroundColor = LocalColorTheme.current.secondary[100],
            waveColor = LocalColorTheme.current.primary[400],
        )
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
        ) {
            ProfileItem(
                label = "닉네임",
                value = nickname ?: "로딩중...",
                onClick = onNicknameClicked,
            )
            ProfileItem(
                label = "이메일",
                value = email ?: "로딩중...",
                onClick = onEmailClicked,
            )
        }
    }
}

@Composable
private fun ProfileItem(
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier.clickable(
            interactionSource = null,
            indication = null,
            onClick = onClick,
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 10.dp),
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.W400,
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = value,
                fontWeight = FontWeight.W400,
                fontSize = 15.sp,
                color = LocalColorTheme.current.grey[700],
            )
            Icon(
                painter = painterResource(R.drawable.ic_profile_menu_bracket_arrow_gray),
                contentDescription = "수정하기",
                tint = LocalColorTheme.current.grey[300],
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Preview
@Composable
private fun ProfileEditScreenPreview() {
    ThemeProvider {
        ProfileEditScreen(
            nickname = "김따따",
            email = "ttatta@gmail.com",
            onBackButtonClicked = {},
            onNicknameClicked = {},
            onEmailClicked = {},
        )
    }
}