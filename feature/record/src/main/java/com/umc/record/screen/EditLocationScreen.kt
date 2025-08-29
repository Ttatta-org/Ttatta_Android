package com.umc.record.screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umc.core.model.LocationSearchResult
import com.umc.record.R
import com.umc.record.component.LocationBottomSheet
import com.umc.record.component.LocationBottomSheetProp
import com.umc.record.component.LocationSearchResults
import com.umc.record.component.ShadowedIcon
import com.umc.record.component.TopBar
import com.umc.record.component.TopBarProp
import com.umc.record.component.previewLocationBottomSheetProp

data class EditLocationScreenTopBarProp(
    val searchWord: String,
    val onSearchWordChanged: (String) -> Unit,
    val onSearchButtonClicked: () -> Unit,
)

@Composable
fun EditLocationScreen(
    mapView: @Composable () -> Unit,
    topBarProp: EditLocationScreenTopBarProp,
    bottomSheetProp: LocationBottomSheetProp,
    onLocationButtonClicked: () -> Unit,
    searchResults: List<LocationSearchResult> = emptyList(),
    onSelectSearchResult: (LocationSearchResult) -> Unit = {},
    onClickMoreResults: () -> Unit = {}
) {
    var topBarHeight by remember { mutableStateOf(0.dp) }
    var isSearchMode by remember { mutableStateOf(false) }

    // 결과 개수 기반 패널 높이 계산
    // LazyColumn의 상하 contentPadding 합 16.dp,
    // '더보기' 버튼이 있을 때 32.dp 정도로 가정
    val visibleCount = remember(searchResults) { minOf(searchResults.size, 3) }
    val itemsHeight = 50.dp * visibleCount
    val spacingHeight = if (visibleCount > 0) 20.dp * (visibleCount - 1) else 0.dp
    val contentPadding = 16.dp
    val moreBtnHeight = if (searchResults.size > 3) 32.dp else 0.dp
    val safetyBuffer = 10.dp

    // 필요 시 최솟값/최댓값으로 가드
    val calculatedPanel = (itemsHeight + spacingHeight + contentPadding + moreBtnHeight + safetyBuffer)
        .coerceAtLeast(0.dp)
        .coerceAtMost(360.dp) // 너무 커지지 않도록 상한 (원하면 조정/제거)

//    val visibleCount = remember(searchResults) { minOf(searchResults.size, 3) }
//    val itemMin = 50.dp
//    val itemsHeight    = itemMin * visibleCount
//    val spacingHeight  = if (visibleCount > 0) 20.dp * (visibleCount - 1) else 0.dp
//    val contentPadding = 16.dp
//    val moreBtnHeight  = if (searchResults.size > 3) 40.dp else 0.dp  // 더보기 높이 상향
//    val safetyBuffer   = 12.dp                                        // 여유 버퍼
//
//    val calculatedPanel = (itemsHeight + spacingHeight + contentPadding + moreBtnHeight + safetyBuffer)
//        .coerceAtLeast(0.dp)

    val panelTarget = if (isSearchMode) calculatedPanel else 0.dp
    val searchPanelHeight by animateDpAsState(
        targetValue = panelTarget,
        animationSpec = tween(240),
        label = "searchPanelHeight"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 지도
        mapView()
        Column {
            // 플로팅 버튼
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
            ) {
                IconButton(
                    onClick = onLocationButtonClicked,
                    modifier = Modifier.size(72.dp)
                ) {
                    ShadowedIcon(
                        id = R.drawable.btn_location,
                        contentDescription = null,
                        width = 72.dp,
                        height = 72.dp,
                    )
                }
            }
            // 바텀 시트
            LocationBottomSheet(prop = bottomSheetProp)
        }
        // 탑 바
        TopBar(
            prop = TopBarProp(
                searchWord = topBarProp.searchWord,
                onSearchWordChanged = {
                    topBarProp.onSearchWordChanged(it)
                    if (!isSearchMode) isSearchMode = true
                },
                onSearchButtonClicked = topBarProp.onSearchButtonClicked,
                onHeightChanged = { topBarHeight = it },
                isSearchMode = isSearchMode,
                searchPanelHeight = searchPanelHeight,
                onSearchModeChanged = { isSearchMode = it },
                panelContent = {
                    LocationSearchResults(
                        results = searchResults,
                        keyword = topBarProp.searchWord,
                        onSelect = { result ->
                            onSelectSearchResult(result)
                            isSearchMode = false  // 선택 시 패널 접기
                        },
                        onClickMore = onClickMoreResults
                    )
                }
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRecordEditLocationScreen() {
    EditLocationScreen(
        mapView = {},
        topBarProp = EditLocationScreenTopBarProp(
            searchWord = "",
            onSearchWordChanged = {},
            onSearchButtonClicked = {},
        ),
        bottomSheetProp = previewLocationBottomSheetProp,
        onLocationButtonClicked = {},
    )
}