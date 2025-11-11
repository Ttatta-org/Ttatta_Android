package com.umc.challenge.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.umc.design.Primary200
import com.umc.design.Primary300
import com.umc.design.Secondary100
import com.umc.design.Secondary300

enum class PointChipTheme(
    val backgroundColor: Color,
    @DrawableRes val icon: Int
) {
    GOLD(
        backgroundColor = Color.Secondary300,
        icon = R.drawable.ic_point1
    ),
    EAGLE(
        backgroundColor = Color.Primary200,
        icon = R.drawable.ic_point2
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
            .background(color = theme.backgroundColor)
            .height(30.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(4.dp)
        ) {
            Image(
                painter = painterResource(id = theme.icon),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxHeight()
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(47.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(color = Color.Secondary100)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = point.toString().replace(
                            regex = Regex("\\B(?=(\\d{3})+(?!\\d))"),
                            replacement = ","
                        ),
                        color = Color.Primary300,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewGoldPointChip() {
    PointChip(
        point = 1300,
        theme = PointChipTheme.GOLD
    )
}

@Preview
@Composable
fun PreviewEaglePointChip2() {
    PointChip(
        point = 1300,
        theme = PointChipTheme.EAGLE
    )
}