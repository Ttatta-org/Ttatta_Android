package com.umc.footprint

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import java.time.LocalDate

@Composable
fun FootprintApp(
    viewModel: FootprintViewModel,
) {
    var isCategoryCardVisible by remember { mutableStateOf(false) }
    var isDiaryCardFlipped by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = viewModel.clickedMarkerPosition) {
        isDiaryCardFlipped = false
    }

    FootprintScreen(
        mapView = viewModel.getMapView(),
        isCategoryMenuVisible = isCategoryCardVisible,
        diaryCardProp = viewModel.clickedMarkerPosition?.let { (x, y) ->
            DiaryCardProp(
                x = x,
                y = y,
                date = LocalDate.now(),
                image = ImageBitmap(width = 220, height = 220),
                content = "This is diary.",
                isFlipped = isDiaryCardFlipped,
                onCardClicked = { isDiaryCardFlipped = !isDiaryCardFlipped },
                onModifyButtonClicked = {},
            )
        },
        userName = "test",
        categoryItemProps = listOf(),
        onNewCategoryButtonClicked = {},
        onCategoryButtonClicked = { isCategoryCardVisible = !isCategoryCardVisible },
        onLocationButtonClicked = { viewModel.moveMapToCurrentPosition() },
    )
}
