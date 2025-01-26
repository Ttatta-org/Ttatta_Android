package com.umc.footprint.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.Grey500
import com.umc.design.Primary400
import com.umc.design.Secondary300
import com.umc.footprint.R
import java.time.LocalDate

val diaryCardWidth = 261.dp
val diaryCardHeight = 311.dp

data class DiaryCardProp(
    val date: LocalDate,
    val image: ImageBitmap,
    val content: String,
    val isFlipped: Boolean,
    val onCardClicked: () -> Unit,
    val onModifyButtonClicked: () -> Unit,
)

@Composable
fun DiaryCard(prop: DiaryCardProp) {
    var cardRotationAngle by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(key1 = prop.isFlipped) {
        animate(
            initialValue = cardRotationAngle,
            targetValue = if (prop.isFlipped) 180f else 0f,
            animationSpec = tween(
                durationMillis = 400,
                easing = FastOutSlowInEasing,
            )
        ) { value, _ ->
            cardRotationAngle = value
        }
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                rotationY = cardRotationAngle
                cameraDistance = 8 * density
            }
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                prop.onCardClicked()
            }
    ) {
        // 앞면
        if (cardRotationAngle < 90f) {
            DiaryCardFront(
                date = prop.date,
                image = prop.image,
                onModifyButtonClicked = prop.onModifyButtonClicked
            )
        }
        // 뒷면
        else Box(
            modifier = Modifier.graphicsLayer { rotationY = 180f }
        ) {
            DiaryCardBack(
                date = prop.date,
                content = prop.content,
                onModifyButtonClicked = prop.onModifyButtonClicked
            )
        }
    }
}

@Composable
private fun DiaryCardFront(
    date: LocalDate,
    image: ImageBitmap,
    onModifyButtonClicked: () -> Unit
) {
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .width(diaryCardWidth)
            .height(diaryCardHeight)
    ) {
        // 카드 이미지
        ShadowedImage(
            id = R.drawable.view_diary_popup,
            contentDescription = null,
            width = diaryCardWidth,
            height = diaryCardHeight,
            shadowColor = Color(0x806E38DE)
        )
        // 카드 내용
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(top = 12.dp, bottom = 36.dp)
                .fillMaxWidth()
        ) {
            // 제목 라인
            Box(
                modifier = Modifier.padding(horizontal = 32.dp),
                contentAlignment = Alignment.BottomEnd,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = com.umc.design.R.drawable.ic_header_deco),
                        contentDescription = null,
                        modifier = Modifier.width(32.dp),
                    )
                    Text(
                        text = "${date.year}년 ${date.monthValue}월 ${date.dayOfMonth}일",
                        fontSize = with(density) { 12.dp.toSp() },
                        color = Color.Primary400,
                    )
                }
                IconButton(
                    onClick = onModifyButtonClicked,
                    modifier = Modifier.size(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_modify),
                        contentDescription = null,
                    )
                }
            }
            // 본문
            Image(
                bitmap = image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}

@Composable
private fun DiaryCardBack(
    date: LocalDate,
    content: String,
    onModifyButtonClicked: () -> Unit
) {
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .width(diaryCardWidth)
            .height(diaryCardHeight)
    )  {
        ShadowedImage(
            id = R.drawable.view_diary_popup_flipped,
            contentDescription = null,
            width = diaryCardWidth,
            height = diaryCardHeight,
            shadowColor = Color(0x806E38DE)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(top = 12.dp, bottom = 36.dp)
                .fillMaxWidth()
        ) {
            // 제목 라인
            Box(
                modifier = Modifier.padding(horizontal = 32.dp),
                contentAlignment = Alignment.BottomEnd,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = com.umc.design.R.drawable.ic_header_deco),
                        contentDescription = null,
                        modifier = Modifier.width(32.dp),
                    )
                    Text(
                        text = "${date.year}년 ${date.monthValue}월 ${date.dayOfMonth}일",
                        fontSize = with(density) { 12.dp.toSp() },
                        color = Color.Primary400,
                    )
                }
                IconButton(
                    onClick = onModifyButtonClicked,
                    modifier = Modifier.size(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_modify),
                        contentDescription = null,
                    )
                }
            }
            // 본문
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Secondary300)
                    .padding(16.dp)
            ) {
                Text(
                    text = content,
                    color = Color.Grey500,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

val previewDiaryCardProp = DiaryCardProp(
    date = LocalDate.now(),
    image = ImageBitmap(width = 220, height = 220),
    content = "This is diary.",
    isFlipped = false,
    onCardClicked = {},
    onModifyButtonClicked = {},
)

@Preview
@Composable
fun PreviewDiaryCardFront() {
    DiaryCardFront(
        date = previewDiaryCardProp.date,
        image = previewDiaryCardProp.image,
        onModifyButtonClicked = previewDiaryCardProp.onModifyButtonClicked
    )
}

@Preview
@Composable
fun PreviewDiaryCardBack() {
    DiaryCardBack(
        date = previewDiaryCardProp.date,
        content = previewDiaryCardProp.content,
        onModifyButtonClicked = previewDiaryCardProp.onModifyButtonClicked
    )
}