package com.umc.footprint.component.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider
import com.umc.footprint.model.prop.DiaryCardFrameProp
import com.umc.footprint.model.prop.DiaryCardFrontProp
import java.time.LocalDate

@Composable
fun DiaryCardFront(
    prop: DiaryCardFrontProp?,
) {
    val colors = LocalColorTheme.current

    DiaryCardFrame(
        prop = DiaryCardFrameProp(
            date = prop?.date,
            borderColor = prop?.categoryColor?.b ?: colors.primary[400],
            backgroundColor = prop?.categoryColor?.c ?: colors.secondary[200],
            contentContainerColor = prop?.categoryColor?.a ?: colors.secondary[300],
            onModifyButtonClicked = prop?.onModifyButtonClicked,
            content = {
                // 본문
                if (prop != null) Image(
                    painter = rememberAsyncImagePainter(model = prop.imageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(220.dp).clip(RoundedCornerShape(8.dp))
                ) else Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(220.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = colors.primary[400],
                    )
                }
            },
        )
    )
}

private val previewDiaryCardFrontProp = DiaryCardFrontProp(
    date = LocalDate.now(),
    imageUrl = "",
    onModifyButtonClicked = {},
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