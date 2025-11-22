package com.umc.footprint

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import com.umc.design.theme.ThemeProvider
import com.umc.footprint.component.card.DiaryCard
import com.umc.footprint.component.ShadowedImage
import com.umc.footprint.component.TopBar
import com.umc.footprint.component.card.previewDiaryCardProp
import com.umc.footprint.core.DesignConstant
import com.umc.footprint.model.prop.PositionedDiaryCardProp
import com.umc.footprint.util.CenterRippleIndication
import com.umc.footprint.util.getDiaryCardTopLeftOffset

@Composable
fun FootprintScreen(
    mapView: @Composable () -> Unit,
    isCategorySelected: Boolean,
    diaryCardProp: PositionedDiaryCardProp?,
    onBackScreenClicked: (() -> Unit)?,
    onCategoryButtonClicked: () -> Unit,
    onLocationButtonClicked: () -> Unit,
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    Box(
        modifier = Modifier.fillMaxSize()
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
                ) { onBackScreenClicked() },
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
        TopBar()
        // 플로팅 버튼
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                // 카테고리 선택
                Box(
                    modifier = Modifier.clickable(
                        indication = CenterRippleIndication,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onCategoryButtonClicked,
                    )
                ) {
                    ShadowedImage(
                        id = if (isCategorySelected) R.drawable.ic_floating_button_category_selected else R.drawable.ic_floating_button_category_unselected,
                        contentDescription = null,
                        size = DpSize(60.dp, 56.dp),
                    )
                }
                // 내 위치로 이동
                Box(
                    modifier = Modifier.clickable(
                        indication = CenterRippleIndication,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onLocationButtonClicked,
                    ),
                ) {
                    ShadowedImage(
                        id = R.drawable.ic_floating_button_location,
                        contentDescription = null,
                        size = DpSize(60.dp, 56.dp),
                    )
                }
            }
        }
    }
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
            onBackScreenClicked = null,
            onCategoryButtonClicked = {},
            onLocationButtonClicked = {},
        )
    }
}