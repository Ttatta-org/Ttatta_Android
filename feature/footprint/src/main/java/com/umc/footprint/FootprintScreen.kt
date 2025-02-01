package com.umc.footprint

import androidx.compose.animation.core.animate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umc.footprint.component.CategorySelectionBar
import com.umc.footprint.component.CategorySelectionBarProp
import com.umc.footprint.component.DiaryCard
import com.umc.footprint.component.DiaryCardProp
import com.umc.footprint.component.DiaryModificationBar
import com.umc.footprint.component.DiaryModificationBarProp
import com.umc.footprint.component.ShadowedImage
import com.umc.footprint.component.TopBar
import com.umc.footprint.component.diaryCardHeight
import com.umc.footprint.component.diaryCardWidth
import com.umc.footprint.component.previewCategorySelectionBarProp
import com.umc.footprint.component.previewDiaryCardProp
import com.umc.footprint.util.rememberOnlyNotNull

data class DiaryCardPropWithPosition(
    val x: Float,
    val y: Float,
    val prop: DiaryCardProp,
)

@Composable
fun FootprintScreen(
    mapView: @Composable () -> Unit,
    categorySelectionBarProp: CategorySelectionBarProp?,
    diaryCardProp: DiaryCardPropWithPosition?,
    diaryModificationBarProp: DiaryModificationBarProp?,
    onCategoryButtonClicked: () -> Unit,
    onLocationButtonClicked: () -> Unit,
) {
    val density = LocalDensity.current
    var screenHeight by remember { mutableStateOf(0.dp) }

    val residualCategorySelectionBarProp = rememberOnlyNotNull(categorySelectionBarProp)
    var categorySelectionBarHeightRatio by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(key1 = categorySelectionBarProp) {
        animate(
            initialValue = categorySelectionBarHeightRatio,
            targetValue = if (categorySelectionBarProp != null) 1f else 0f,
            block = { value, _ -> categorySelectionBarHeightRatio = value },
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 지도
        mapView()
        // 일기 팝업
        diaryCardProp?.let { (x, y, prop) ->
            Box(
                modifier = Modifier.offset(
                    x = with(density) { x.toDp() - diaryCardWidth / 2 },
                    y = with(density) { y.toDp() - diaryCardHeight - 16.dp },
                )
            ) {
                DiaryCard(prop)
            }
        }
        // 탑 바
        TopBar(
            showBackground = categorySelectionBarProp != null
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { with(density) { screenHeight = it.size.height.toDp() } }
                .let {
                    if (categorySelectionBarProp != null) it.clickable(
                        indication = null,
                        interactionSource = null,
                    ) {
                        categorySelectionBarProp.onDismissed()
                    }
                    else it
                }
        ) {
            // 플로팅 버튼
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 카테고리 선택
                    IconButton(
                        onClick = onCategoryButtonClicked,
                        modifier = Modifier.size(84.dp),
                    ) {
                        ShadowedImage(
                            id = R.drawable.ic_floating_button_category_unselected,
                            contentDescription = null,
                            width = 84.dp,
                            height = 84.dp,
                        )
                    }
                    // 내 위치로 이동
                    IconButton(
                        onClick = onLocationButtonClicked,
                        modifier = Modifier.size(84.dp),
                    ) {
                        ShadowedImage(
                            id = R.drawable.ic_floating_button_location,
                            contentDescription = null,
                            width = 84.dp,
                            height = 84.dp,
                        )
                    }
                }
            }
            // 카테고리 메뉴
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.6f * categorySelectionBarHeightRatio)
            ) {
                residualCategorySelectionBarProp?.let { prop ->
                    CategorySelectionBar(prop = prop)
                }
            }
        }
    }

    // 일기 수정 및 삭제 메뉴
    diaryModificationBarProp?.let { prop ->
        DiaryModificationBar(prop = prop)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFootprintScreen() {
    FootprintScreen(
        mapView = {},
        diaryCardProp = DiaryCardPropWithPosition(
            x = 600f,
            y = 1500f,
            prop = previewDiaryCardProp,
        ),
        diaryModificationBarProp = null, // previewDiaryModificationBarProp,
        categorySelectionBarProp = previewCategorySelectionBarProp,
        onCategoryButtonClicked = {},
        onLocationButtonClicked = {},
    )
}