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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.umc.design.Primary400
import com.umc.design.theme.ThemeProvider
import com.umc.footprint.model.prop.DiaryCardFrameProp
import com.umc.footprint.model.prop.DiaryCardFrontProp
import java.time.LocalDate

@Composable
fun DiaryCardFront(
    prop: DiaryCardFrontProp?,
) {
    DiaryCardFrame(
        prop = DiaryCardFrameProp(
            date = prop?.date,
            borderColor = Color(0xFFE5E5E5),
            backgroundColor = Color(0xFFFFFFFF),
            contentContainerColor = Color(0xFFF5F5F5),
            onModifyButtonClicked = prop?.onModifyButtonClicked,
            content = {
                // 본문
                if (prop != null) Image(
                    painter = rememberAsyncImagePainter(model = prop.imageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Companion.Crop,
                    modifier = Modifier.Companion.size(220.dp).clip(RoundedCornerShape(8.dp))
                ) else Box(
                    contentAlignment = Alignment.Companion.Center,
                    modifier = Modifier.Companion.size(220.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.Companion.size(32.dp),
                        color = Color.Companion.Primary400,
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