package com.umc.ttatta.app.component

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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.umc.design.Grey200
import com.umc.design.Grey300
import com.umc.design.Primary500
import com.umc.design.theme.ThemeProvider

val centerButtonSize = DpSize(95.dp, 90.dp)
val iconHeight = 24.dp

@Composable
fun NavigationBar(
    currentNavigationItem: NavigationItem?,
    onNavigate: (NavigationItem) -> Unit,
) {
    val density = LocalDensity.current
    val padding = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
    var maxHeight by remember { mutableStateOf<Dp?>(null) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White),
    ) {
        Row(
            modifier = Modifier.padding(bottom = padding)
        ) {
            Box(
                modifier = Modifier
                    .background(color = Color.Grey200)
                    .width(16.dp)
                    .height(1.dp),
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
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(item) }
                        .let { modifier -> maxHeight?.let{ modifier.height(it) } ?: modifier }
                        .onGloballyPositioned {
                            with(density) {
                                val height = it.size.height.toDp()
                                maxHeight = maxHeight?.let { max(it, height) } ?: run { height }
                            }
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (currentNavigationItem == item)
                                    Color.Primary500
                                else
                                    Color.Grey200
                            )
                            .fillMaxWidth()
                            .height(1.dp),
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                    ) {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = null,
                            tint = if (currentNavigationItem == item) Color.Primary500 else Color.Grey300,
                            modifier = Modifier
                                .width(iconHeight * item.size.width / item.size.height * item.magnification)
                                .height(iconHeight * item.magnification)
                        )
                        Text(
                            text = item.title,
                            fontSize = 10.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.W400,
                            color = if (currentNavigationItem == item) Color.Primary500 else Color.Grey300,
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .background(color = Color.Grey200)
                            .width(centerButtonSize.width)
                            .height(1.dp),
                    )
                }
            }
            Box(
                modifier = Modifier
                    .background(color = Color.Grey200)
                    .width(16.dp)
                    .height(1.dp),
            )
        }
    }
}

@Preview
@Composable
fun PreviewNavigationBar() {
    ThemeProvider {
        NavigationBar(
            currentNavigationItem = NavigationItem.DIARY,
            onNavigate = {},
        )
    }
}