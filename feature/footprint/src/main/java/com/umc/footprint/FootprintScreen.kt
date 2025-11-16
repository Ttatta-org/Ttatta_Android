package com.umc.footprint

import android.graphics.BitmapFactory
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import com.umc.design.theme.ThemeProvider
import com.umc.footprint.component.CategorySelectionBar
import com.umc.footprint.component.card.DiaryCard
import com.umc.footprint.component.DiaryModificationBar
import com.umc.footprint.component.ShadowedImage
import com.umc.footprint.component.TopBar
import com.umc.footprint.component.previewCategorySelectionBarProp
import com.umc.footprint.component.card.previewDiaryCardProp
import com.umc.footprint.core.DesignConstant
import com.umc.footprint.model.prop.DiaryModificationBarProp
import com.umc.footprint.model.prop.PositionedDiaryCardProp
import com.umc.footprint.model.prop.VisibleCategorySelectionBarProp
import com.umc.footprint.util.getDiaryCardTopLeftOffset

private const val maxCategorySelectionBarHeightRatio = 0.6f

@Composable
fun FootprintScreen(
    mapView: @Composable () -> Unit,
    isCategorySelected: Boolean,
    categorySelectionBarProp: VisibleCategorySelectionBarProp,
    diaryCardProp: PositionedDiaryCardProp?,
    diaryModificationBarProp: DiaryModificationBarProp?,
    onBackScreenClicked: (() -> Unit)?,
    onCategoryButtonClicked: () -> Unit,
    onLocationButtonClicked: () -> Unit,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    var screenHeight by remember { mutableStateOf<Dp?>(null) }
    var categorySelectionBarHeight by remember { mutableStateOf<Dp?>(null) }

    val categorySelectionBarOffset by animateDpAsState(
        animationSpec = tween(durationMillis = 200),
        targetValue = categorySelectionBarHeight?.let { height ->
            if (categorySelectionBarProp.isVisible) 0.dp else height
        } ?: 9999.dp,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { screenHeight = with(density) { it.size.height.toDp() } }
    ) {
        // 지도
        mapView.invoke()
        // 클릭 이벤트 하이재커
        if (onBackScreenClicked != null) Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = null,
                ) { onBackScreenClicked() }
        )
        // 일기 팝업
        diaryCardProp?.let { prop ->
            Box(
                modifier = Modifier.offset {
                    getDiaryCardTopLeftOffset(
                        density = density,
                        markerOffset = prop.offset,
                    ).round()
                },
            ) {
                DiaryCard(prop = prop.prop)
            }

            if (prop.showMarker) {
                val marker = remember {
                    val resourceMap =
                        if (prop.isMarkerBook) DesignConstant.BookMarkerResourceMap else DesignConstant.FootprintMarkerResourceMap

                    resourceMap[prop.prop.defaultCategoryColor]?.let {
                        BitmapFactory
                            .decodeResource(context.resources, it)
                            .asImageBitmap()
                    }
                }

                val size =
                    if (prop.isMarkerBook) DesignConstant.BookMarkerSize else DesignConstant.FootprintMarkerSize

                marker?.let {
                    Box(
                        modifier = Modifier.offset(
                            x = with(density) { prop.offset.x.toDp() } - size.width / 2,
                            y = with(density) { prop.offset.y.toDp() } - size.height / 2,
                        ),
                    ) {
                        Image(
                            bitmap = marker,
                            contentDescription = null,
                            modifier = Modifier.size(size),
                        )
                    }
                }
            }
        }
        // 탑 바
        TopBar(showBackground = categorySelectionBarProp.isVisible)

        Column(
            modifier = Modifier
                .let {
                    if (categorySelectionBarProp.isVisible) it.clickable(
                        indication = null,
                        interactionSource = null,
                    ) {
                        categorySelectionBarProp.onDismissed()
                    }
                    else it
                }
                .offset {
                    Offset(
                        x = 0f,
                        y = categorySelectionBarOffset.toPx(),
                    ).round()
                },
        ) {
            // 플로팅 버튼
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 카테고리 선택
                    IconButton(
                        onClick = onCategoryButtonClicked,
                        modifier = Modifier.size(72.dp),
                    ) {
                        ShadowedImage(
                            id = if (isCategorySelected) R.drawable.ic_floating_button_category_selected
                            else R.drawable.ic_floating_button_category_unselected,
                            contentDescription = null,
                            size = DpSize(72.dp, 72.dp),
                        )
                    }
                    // 내 위치로 이동
                    IconButton(
                        onClick = onLocationButtonClicked,
                        modifier = Modifier.size(72.dp),
                    ) {
                        ShadowedImage(
                            id = R.drawable.ic_floating_button_location,
                            contentDescription = null,
                            size = DpSize(72.dp, 72.dp),
                        )
                    }
                }
            }
            // 카테고리 메뉴
            screenHeight?.let { screenHeight ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = screenHeight * maxCategorySelectionBarHeightRatio)
                        .onGloballyPositioned {
                            categorySelectionBarHeight = with(density) { it.size.height.toDp() }
                        },
                ) {
                    CategorySelectionBar(prop = categorySelectionBarProp.prop)
                }
            }
        }
    }

    // 일기 수정 및 삭제 메뉴
    diaryModificationBarProp?.let { DiaryModificationBar(prop = it) }
}

@Preview(showBackground = true)
@Composable
fun PreviewFootprintScreen() {
    ThemeProvider {
        FootprintScreen(
            mapView = {},
            isCategorySelected = false,
            diaryCardProp = PositionedDiaryCardProp(
                offset = Offset(600f, 1500f),
                prop = previewDiaryCardProp,
                showMarker = true,
                isMarkerBook = false,
            ),
            diaryModificationBarProp = null, // previewDiaryModificationBarProp,
            categorySelectionBarProp = VisibleCategorySelectionBarProp(
                isVisible = false,
                prop = previewCategorySelectionBarProp,
                onDismissed = {},
            ),
            onBackScreenClicked = null,
            onCategoryButtonClicked = {},
            onLocationButtonClicked = {},
        )
    }
}