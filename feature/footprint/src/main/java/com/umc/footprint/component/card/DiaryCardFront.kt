package com.umc.footprint.component.card

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider
import com.umc.footprint.model.prop.DiaryCardFrameProp
import com.umc.footprint.model.prop.DiaryCardFrontLoadedProp
import com.umc.footprint.model.prop.DiaryCardFrontProp
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun DiaryCardFront(
    prop: DiaryCardFrontProp,
) {
    val colors = LocalColorTheme.current
    val categoryColor = prop.prop?.categoryColor ?: prop.defaultColor
    val scope = rememberCoroutineScope()
    val alpha = remember { Animatable(0f) }

    DiaryCardFrame(
        prop = DiaryCardFrameProp(
            date = prop.prop?.date,
            borderColor = categoryColor?.b ?: colors.primary[400],
            backgroundColor = Color.White,
            contentContainerColor = categoryColor?.c ?: colors.secondary[300],
            onModifyButtonClicked = prop.prop?.onModifyButtonClicked,
            content = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = categoryColor?.b ?: colors.primary[400],
                    )
                    if (prop.prop != null) Image(
                        painter = rememberAsyncImagePainter(
                            model = prop.prop.imageUrl,
                            onState = { state ->
                                if (state is AsyncImagePainter.State.Success) scope.launch {
                                    alpha.animateTo(
                                        targetValue = 1f, animationSpec = tween(
                                            durationMillis = diaryCardFrameAnimationDurationMillis,
                                            easing = LinearEasing,
                                        )
                                    )
                                }
                            },
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(alpha.value)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            },
        )
    )
}

private val previewDiaryCardFrontProp = DiaryCardFrontProp(
    prop = DiaryCardFrontLoadedProp(
        date = LocalDate.now(),
        imageUrl = "",
        onModifyButtonClicked = {},
    ),
)

@Preview
@Composable
fun PreviewDiaryCardFront() {
    ThemeProvider {
        DiaryCardFront(
            prop = previewDiaryCardFrontProp
        )
    }
}