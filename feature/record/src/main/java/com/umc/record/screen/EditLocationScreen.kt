package com.umc.record.screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import com.umc.core.model.LocationSearchResult
import com.umc.design.Secondary100
import com.umc.design.component.CustomHeader
import com.umc.design.theme.LocalColorTheme
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
    var isExpanded by remember { mutableStateOf(false) }

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

    val density = LocalDensity.current
    val expandedPanel = with(density) {
        // 화면 전체 높이 - TopBar 높이 - 여유
        val screenH = androidx.compose.ui.platform.LocalConfiguration.current.screenHeightDp.dp
        (screenH - topBarHeight - 16.dp).coerceAtLeast(calculatedPanel)
    }

    val panelTarget = when {
        !isSearchMode -> 0.dp
        isExpanded    -> 0.dp  // 펼침
        else          -> calculatedPanel  // 접힘
    }
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
//        TopBar(
//            prop = TopBarProp(
//                searchWord = topBarProp.searchWord,
//                onSearchWordChanged = {
//                    topBarProp.onSearchWordChanged(it)
//                    if (!isSearchMode) {
//                        isSearchMode = true
//                        isExpanded = false  // 새 검색 시작 시 항상 접힘으로
//                    }
//                },
//                onSearchButtonClicked = {
//                    isSearchMode = true
//                    isExpanded = false  // 검색 버튼 눌러도 접힘으로
//                    topBarProp.onSearchButtonClicked()
//                },
//                onHeightChanged = { topBarHeight = it },
//                isSearchMode = isSearchMode,
//                searchPanelHeight = searchPanelHeight,
//                onSearchModeChanged = { opened ->
//                    isSearchMode = opened
//                    if (!opened) isExpanded = false  // 닫힐 때 상태 초기화
//                },
////                panelContent = {
////                    LocationSearchResults(
////                        results = searchResults,
////                        keyword = topBarProp.searchWord,
////                        onSelect = { result ->
////                            onSelectSearchResult(result)
////                            isSearchMode = false  // 선택 시 패널 접기
////                            isExpanded = false
////                        },
////                        onClickMore = {
////                            isExpanded = true  // 더보기 → 펼침
////                            onClickMoreResults()
////                        },
////                        showAll = isExpanded,  // 접힘/펼침에 따라 노출 개수
////                        scrollEnabled = isExpanded
////                    )
////                }
//                panelContent = {
//                    if (!isExpanded && isSearchMode && searchPanelHeight > 0.dp) {
//                        CollapsedResultsPanel(
//                            results = searchResults,
//                            keyword = topBarProp.searchWord,
//                            onSelect = { result ->
//                                onSelectSearchResult(result)
//                                isSearchMode = false
//                                isExpanded = false
//                            },
//                            onClickMore = {
//                                isExpanded = true      // 여기서 확장 화면으로 전환
//                                onClickMoreResults()
//                            },
//                            panelHeight = calculatedPanel
//                        )
//                    }
//                }
//
//            )
//        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { size ->
                    topBarHeight = with(density) { size.height.toDp() }
                }
        ) {
            CustomHeader(
                showLogo = true,
                showShadow = false,
                backgroundColor = Color.White.copy(alpha = 0.9f), // 필요시 기존과 맞춰 조절
                waveColor = LocalColorTheme.current.primary[400],
                headerTrailingStartPadding = 0.dp,
                headerTrailing = {
                    EditableHeaderSearchRow(
                        keyword = topBarProp.searchWord,
                        onKeywordChanged = { text ->
                            topBarProp.onSearchWordChanged(text)
                            if (!isSearchMode) {
                                isSearchMode = true
                                isExpanded = false
                            }
                        },
                        onClearKeyword = {
                            topBarProp.onSearchWordChanged("")
                            // 텍스트 없으면 검색모드 종료(기존 TopBar 느낌)
                            isSearchMode = false
                            isExpanded = false
                        },
                        onClickSearch = {
                            isSearchMode = true
                            isExpanded = false
                            topBarProp.onSearchButtonClicked()
                        },
                        onFocusChanged = { focused ->
                            if (focused) {
                                if (!isSearchMode) isSearchMode = true
                            } else {
                                if (topBarProp.searchWord.isBlank()) {
                                    isSearchMode = false
                                    isExpanded = false
                                }
                            }
                        }
                    )
                },
                content = { waveHeight ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(searchPanelHeight)
                    ) {
                        if (!isExpanded && isSearchMode && searchPanelHeight > 0.dp) {
                            CollapsedResultsPanel(
                                results = searchResults,
                                keyword = topBarProp.searchWord,
                                onSelect = { result ->
                                    onSelectSearchResult(result)
                                    isSearchMode = false
                                    isExpanded = false
                                },
                                onClickMore = {
                                    isExpanded = true
                                    onClickMoreResults()
                                },
                                panelHeight = calculatedPanel
                            )
                        }
                    }

                    // waveColor를 Transparent로 줬으니 사실상 의미 없지만,
                    // CustomHeader API 구조상 waveHeight는 받아두는 게 안전.
                    Spacer(modifier = Modifier.height(waveHeight))
                }
            )
        }

        // 확장 화면: 완전히 별도 렌더(흰 배경 + 스크롤)
        if (isExpanded) {
            ExpandedResultsScreen(
                results = searchResults,
                keyword = topBarProp.searchWord,
                onSelect = { result ->
                    onSelectSearchResult(result)
                    isExpanded = false
                    isSearchMode = false
                },
                onBack = {
                    // 뒤로/닫기 -> 접힘 상태로 복귀(검색 패널 다시 보이게)
                    isExpanded = false
                    isSearchMode = true
                },
                onClearKeyword = { topBarProp.onSearchWordChanged("") },
                onClickSearch = topBarProp.onSearchButtonClicked
            )
        }

    }
}

// 더보기 전: TopBar 안에 들어가는 작은 패널
@Composable
private fun CollapsedResultsPanel(
    results: List<LocationSearchResult>,
    keyword: String,
    onSelect: (LocationSearchResult) -> Unit,
    onClickMore: () -> Unit,
    panelHeight: Dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(panelHeight)          // 3개 + 더보기 높이 기준
            .padding(horizontal = 24.dp)
    ) {
        LocationSearchResults(
            results = results,
            keyword = keyword,
            onSelect = onSelect,
            onClickMore = onClickMore,
            showAll = false,              // 3개만
            scrollEnabled = false         // 스크롤 금지
        )
    }
}

//// 더보기 후: 전용 풀스크린 결과 화면(흰 배경 + 스크롤 가능)
//@Composable
//private fun ExpandedResultsScreen(
//    results: List<LocationSearchResult>,
//    keyword: String,
//    onSelect: (LocationSearchResult) -> Unit,
//    onBack: () -> Unit,
//    onClearKeyword: () -> Unit,
//    onClickSearch: () -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFFFFFFF))
//    ) {
//        // 상단 바(뒤로)
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .windowInsetsPadding(WindowInsets.statusBars)
//                .padding(start = 20.dp, end = 22.dp, top = 39.dp, bottom = 20.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(22.dp)
//        ) {
//            IconButton(
//                onClick = onBack,
//                modifier = Modifier.size(24.dp)
//            ) {
//                Icon(
//                    painter = painterResource(R.drawable.ic_back),
//                    contentDescription = "Back",
//                    tint = Color.Unspecified
//                )
//            }
//
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(10.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.weight(1f)
//            ) {
//                Box(
//                    contentAlignment = Alignment.CenterStart,
//                    modifier = Modifier
//                        .weight(1f)
//                        .height(34.dp)
//                        .border(
//                            width = 1.dp,
//                            color = Color(0xFFFF9681),
//                            shape = RoundedCornerShape(percent = 50)
//                        )
//                        .background(
//                            color = Color.Secondary100,
//                            shape = RoundedCornerShape(percent = 50)
//                        )
//                ) {
//                    // keyword 표시
//                    Text(
//                        text = if (keyword.isEmpty())
//                            stringResource(id = R.string.search_placeholder)
//                        else keyword,
//                        fontSize = 13.sp,
//                        color = if (keyword.isEmpty()) Color(0xFF8E8E8E) else Color.Black,
//                        fontWeight = FontWeight.Medium,
//                        modifier = Modifier.padding(start = 15.dp, end = 32.dp)
//                    )
//
//                    // 삭제 버튼
//                    if (keyword.isNotEmpty()) {
//                        IconButton(
//                            onClick = onClearKeyword,
//                            modifier = Modifier
//                                .align(Alignment.CenterEnd)
//                                .size(24.dp)
//                                .padding(end = 11.dp)
//                        ) {
//                            Icon(
//                                painter = painterResource(id = R.drawable.ic_delete),
//                                contentDescription = "Clear",
//                                tint = Color.Unspecified
//                            )
//                        }
//                    }
//                }
//
//                IconButton(
//                    onClick = onClickSearch,
//                    modifier = Modifier.size(24.dp)
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_search),
//                        contentDescription = "Search",
//                        tint = Color.Unspecified,
//                    )
//                }
//            }
//        }
//
//        // 전체 리스트 (좌우 24dp 패딩)
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(start = 24.dp, end = 24.dp)
//        ) {
//            LocationSearchResults(
//                results = results,
//                keyword = keyword,
//                onSelect = onSelect,
//                onClickMore = {},     // 확장 화면에서는 더보기 없음
//                showAll = true,       // 전체
//                scrollEnabled = true  // 스크롤 허용
//            )
//        }
//    }
//}

@Composable
private fun ExpandedResultsScreen(
    results: List<LocationSearchResult>,
    keyword: String,
    onSelect: (LocationSearchResult) -> Unit,
    onBack: () -> Unit,
    onClearKeyword: () -> Unit,
    onClickSearch: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CustomHeader(
            showLogo = false,
            showShadow = false,
            backgroundColor = Color.White,
            waveColor = Color.Transparent,
            headerTrailingStartPadding = 0.dp,
            headerTrailing = {
                // ✅ 기존 Expanded 화면: "뒤로 + (읽기전용) 검색바 + 삭제 + 검색"
                ReadOnlyHeaderSearchRow(
                    keyword = keyword,
                    onBack = onBack,
                    onClearKeyword = onClearKeyword,
                    onClickSearch = onClickSearch
                )
            },
            content = { waveHeight ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp)
                ) {
                    LocationSearchResults(
                        results = results,
                        keyword = keyword,
                        onSelect = onSelect,
                        onClickMore = {},
                        showAll = true,
                        scrollEnabled = true
                    )
                }
                Spacer(modifier = Modifier.height(waveHeight))
            }
        )
    }
}


@Composable
private fun EditableHeaderSearchRow(
    keyword: String,
    onKeywordChanged: (String) -> Unit,
    onClearKeyword: () -> Unit,
    onClickSearch: () -> Unit,
    onFocusChanged: (Boolean) -> Unit,
) {
    val keyboard = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .weight(1f)
                .height(34.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFFFF9681),
                    shape = RoundedCornerShape(percent = 50)
                )
                .background(
                    color = Color.Secondary100,
                    shape = RoundedCornerShape(percent = 50)
                )
        ) {
            BasicTextField(
                value = keyword,
                onValueChange = { newText ->
                    val filtered = newText.replace("\n", " ").replace("\r", " ")
                    onKeywordChanged(filtered)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboard?.hide()
                        onClickSearch()
                    }
                ),
                textStyle = TextStyle(
                    fontSize = 13.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, end = 32.dp)
                    .onFocusChanged { onFocusChanged(it.isFocused) }
            ) { inner ->
                if (keyword.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.search_placeholder),
                        fontSize = 13.sp,
                        color = Color(0xFF8E8E8E),
                        fontWeight = FontWeight.Medium
                    )
                }
                inner()
            }

            if (keyword.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onClearKeyword()
                        keyboard?.hide()
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(24.dp)
                        .padding(end = 11.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "Clear",
                        tint = Color.Unspecified
                    )
                }
            }
        }

        IconButton(
            onClick = {
                keyboard?.hide()
                onClickSearch()
            },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search",
                tint = Color.Unspecified
            )
        }
    }
}

@Composable
private fun ReadOnlyHeaderSearchRow(
    keyword: String,
    onBack: () -> Unit,
    onClearKeyword: () -> Unit,
    onClickSearch: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = "Back",
                tint = Color.Unspecified
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFFF9681),
                        shape = RoundedCornerShape(percent = 50)
                    )
                    .background(
                        color = Color.Secondary100,
                        shape = RoundedCornerShape(percent = 50)
                    )
            ) {
                Text(
                    text = if (keyword.isEmpty()) stringResource(id = R.string.search_placeholder) else keyword,
                    fontSize = 13.sp,
                    color = if (keyword.isEmpty()) Color(0xFF8E8E8E) else Color.Black,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 15.dp, end = 32.dp)
                )

                if (keyword.isNotEmpty()) {
                    IconButton(
                        onClick = onClearKeyword,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(24.dp)
                            .padding(end = 11.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = "Clear",
                            tint = Color.Unspecified
                        )
                    }
                }
            }

            IconButton(
                onClick = onClickSearch,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Search",
                    tint = Color.Unspecified
                )
            }
        }
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