package com.umc.ttatta.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.Grey300
import com.umc.design.Primary500
import com.umc.ttatta.R

enum class NavigationItem(
    val title: String,
    @DrawableRes val icon: Int,
) {
    DIARY(
        title = "일기 보관함",
        icon = R.drawable.ic_diary_locker
    ),
    FOOTPRINT(
        title = "나의 발자국",
        icon = R.drawable.ic_my_footprint
    ),
    CHALLENGE(
        title = "나의 챌린지",
        icon = R.drawable.ic_my_challenge
    ),
    MY_PAGE(
        title = "마이페이지",
        icon = R.drawable.ic_my_page
    )
}

val centerButtonSize = DpSize(95.dp, 90.dp)

@Composable
fun NavigationBar(
    currentNavigationItem: NavigationItem?,
    onNavigate: (NavigationItem) -> Unit,
) {
    val padding = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(bottom = padding)
        ) {
            Box(
                modifier = Modifier
                    .background(color = Color.Grey300)
                    .width(16.dp)
                    .height(1.dp)
            )
            listOf(
                NavigationItem.DIARY,
                NavigationItem.FOOTPRINT,
                null,
                NavigationItem.CHALLENGE,
                NavigationItem.MY_PAGE,
            ).forEach { item ->
                if (item != null) Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(item) }
                ) {
                    val color = if (currentNavigationItem == item) Color.Primary500 else Color.Grey300

                    Box(
                        modifier = Modifier
                            .background(color = color)
                            .fillMaxWidth()
                            .height(1.dp)
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                    ) {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = item.title,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Thin,
                            color = color
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .background(color = Color.Grey300)
                            .width(centerButtonSize.width)
                            .height(1.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .background(color = Color.Grey300)
                    .width(16.dp)
                    .height(1.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewNavigationBar() {
    NavigationBar(
        currentNavigationItem = NavigationItem.DIARY,
        onNavigate = {}
    )
}