package com.umc.challenge.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.challenge.R
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider

enum class PointChipTheme(
    val backgroundColor: @Composable () -> Color,
    @get:DrawableRes val icon: Int
) {
    GOLD(
        backgroundColor = { LocalColorTheme.current.primary[200] },
        icon = R.drawable.img_point_icon_1
    ),
    EAGLE(
        backgroundColor = { LocalColorTheme.current.primary[400] },
        icon = R.drawable.img_point_icon_2
    ),
}

@Composable
fun PointChip(
    point: Int,
    theme: PointChipTheme = PointChipTheme.GOLD
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(color = theme.backgroundColor.invoke())
            .height(30.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.5.dp),
        ) {
            Image(
                painter = painterResource(id = theme.icon),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxHeight()
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(min = 47.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(color = LocalColorTheme.current.primary[100])
            ) {
                Text(
                    text = point
                        .toString()
                        .replace(
                            regex = Regex("\\B(?=(\\d{3})+(?!\\d))"),
                            replacement = ","
                        ),
                    color = LocalColorTheme.current.primary[500],
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.4).sp,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewGoldPointChip() {
    ThemeProvider {
        PointChip(
            point = 130000000,
            theme = PointChipTheme.GOLD
        )
    }
}

@Preview
@Composable
fun PreviewEaglePointChip2() {
    ThemeProvider {
        PointChip(
            point = 13,
            theme = PointChipTheme.EAGLE
        )
    }
}