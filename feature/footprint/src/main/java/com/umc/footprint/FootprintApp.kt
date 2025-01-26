package com.umc.footprint

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import com.umc.footprint.component.CategorySelectionBarProp
import com.umc.footprint.component.DiaryCardProp
import com.umc.footprint.component.DiaryModificationBarProp
import java.time.LocalDate

@Composable
fun FootprintApp(
    viewModel: FootprintViewModel,
) {
    var isCategoryMenuVisible by remember { mutableStateOf(false) }
    var isDiaryModificationMenuVisible by remember { mutableStateOf(false) }
    var isDiaryCardFlipped by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = viewModel.clickedMarkerPosition) {
        isDiaryCardFlipped = false
    }

    FootprintScreen(
        mapView = viewModel.getMapView(),
        diaryCardProp = viewModel.clickedMarkerPosition?.let { (x, y) ->
            DiaryCardPropWithPosition(
                x = x,
                y = y,
                prop = DiaryCardProp(
                    date = LocalDate.now(),
                    image = ImageBitmap(width = 220, height = 220),
                    content = "This is diary.",
                    isFlipped = isDiaryCardFlipped,
                    onCardClicked = { isDiaryCardFlipped = !isDiaryCardFlipped },
                    onModifyButtonClicked = { isDiaryModificationMenuVisible = true },
                )
            )
        },
        diaryModificationBarProp = if (isDiaryModificationMenuVisible) DiaryModificationBarProp(
            onModifyOptionClicked = {},
            onDeleteOptionClicked = {},
            onDismissed = { isDiaryModificationMenuVisible = false },
        ) else null,
        categorySelectionBarProp = if (isCategoryMenuVisible) CategorySelectionBarProp(
            userName = "test",
            itemProps = listOf(),
            onNewCategoryButtonClicked = {},
            onDismissed = { isCategoryMenuVisible = false }
        ) else null,
        onCategoryButtonClicked = { isCategoryMenuVisible = !isCategoryMenuVisible },
        onLocationButtonClicked = { viewModel.moveMapToCurrentPosition() },
    )
}
