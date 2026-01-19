package com.umc.mypage.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.umc.design.component.CustomHeader
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider
import com.umc.mypage.app.mypage.MyPageViewModel
import com.umc.mypage.R
import com.umc.mypage.component.ToggleSettingItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlin.math.roundToInt

@Composable
fun NotificationSettingsScreen(
    state: MyPageViewModel.NotificationSettingsUiState,
    onDailyToggle: (Boolean) -> Unit,
    onDailyTimeChange: (isPm: Boolean, hour12: Int, minute: Int) -> Unit,
    onSummaryToggle: (Boolean) -> Unit,
    onSummaryHourChange: (hour12: Int) -> Unit,
    onChallengeToggle: (Boolean) -> Unit,
    onChallengeHoursChange: (Int) -> Unit,
    onLocationToggle: (Boolean) -> Unit,
    onBackClick: () -> Unit,
) {
    val systemUiController = rememberSystemUiController()
    val backgroundColor = Color(0xFFFFFFFF) // 상태바 배경색 (배경과 맞춤)

    SideEffect {
        systemUiController.setStatusBarColor(
            color = backgroundColor, // ✅ 상태바를 앱 배경색과 동일하게 설정
        )
    }

    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        CustomHeader(
            showLogo = false,
            centerText = "알림 설정",
            backgroundColor = LocalColorTheme.current.secondary[100],
            onBackButtonClicked = onBackClick,
        )
        // ✅ 2. LazyColumn (스크롤 가능한 콘텐츠)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { Spacer(modifier = Modifier.height(23.dp)) }
            item {
                ToggleSettingItem(
                    title = "일기 작성 알림",
                    description = "매일 일정한 시각에 일기 작성을 알리는 알림을 보내요!",
                    checked = state.dailyOn,
                    onCheckedChange = onDailyToggle,
                    bottomContent = {
                        if (state.dailyOn) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                // 화면에서 파생 상태
                                val dailyIsPm = state.dailyHour24 >= 12
                                val dailyHour12 =
                                    ((state.dailyHour24 % 12).let { if (it == 0) 12 else it })
                                val dailyMinute = state.dailyMinute

                                // 오전/오후 드롭다운
                                DropdownButtonWithMenu(
                                    options = listOf("오전", "오후"),
                                    initialSelectedText = if (dailyIsPm) "오후" else "오전",
                                    onSelected = { ampm ->
                                        val isPm = ampm == "오후"
                                        onDailyTimeChange(
                                            isPm,
                                            dailyHour12,
                                            dailyMinute
                                        ) // ← 이름 제거!
                                    }
                                )

                                // 시/분 휠
                                TimeWheelDropdown(
                                    width = 130.dp,
                                    initialHour12 = dailyHour12,
                                    initialMinute = dailyMinute
                                ) { hour12, minute ->
                                    onDailyTimeChange(dailyIsPm, hour12, minute)
                                }
                            }
                        }
                    }
                )
            }
            item { Spacer(modifier = Modifier.height(22.dp)) }
            item {
                ToggleSettingItem(
                    title = "위치 기반 추억 회상 알림",
                    description = "현재 위치와 가까운 과거 기록을 찾으면 알림을 보내요!",
                    checked = state.locationOn,
                    onCheckedChange = onLocationToggle,
                )
            }
            item { Spacer(modifier = Modifier.height(22.dp)) }
            item {
                ToggleSettingItem(
                    title = "챌린지 리마인드 알림",
                    description = "챌린지 달성 마감 전 리마인드 알림을 보내요!",
                    checked = state.challengeOn,
                    onCheckedChange = onChallengeToggle,
                    bottomContent = {
                        if (state.challengeOn) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                DropdownButtonWithMenu(
                                    options = (0..48).map {
                                        it
                                            .toString()
                                            .padStart(2, '0')
                                    }, // 🔧 범위 확대
                                    initialSelectedText = state.challengeRemainingHours
                                        .toString()
                                        .padStart(2, '0'),
                                    onSelected = { sel ->
                                        onChallengeHoursChange(sel.toInt())
                                    }
                                )
                                Text(
                                    text = "시간 전",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W700,
                                    color = Color(0xFF8E8E8E)
                                )
                            }
                        }
                    }
                )
            }
            item { Spacer(modifier = Modifier.height(22.dp)) }
            item {
                ToggleSettingItem(
                    title = "하루 요약 알림",
                    description = "매일 일정한 시각에 오늘의 일기 요약 알림을 보내요!",
                    checked = state.summaryOn,
                    onCheckedChange = onSummaryToggle,
                    bottomContent = {
                        if (state.summaryOn) {
                            val summaryHour12 = when {
                                state.summaryHour24 == 0 -> 12
                                state.summaryHour24 > 12 -> state.summaryHour24 - 12
                                else -> state.summaryHour24
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                // PM 고정 표시
                                Text(
                                    text = "오후",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W700,
                                    color = Color(0xFF8E8E8E)
                                )
                                // 시간만 선택 (1..12)
                                key(summaryHour12) {
                                    DropdownButtonWithMenu(
                                        options = (1..12).map {
                                            it
                                                .toString()
                                                .padStart(2, '0')
                                        },
                                        initialSelectedText = summaryHour12
                                            .toString()
                                            .padStart(2, '0'),
                                        onSelected = { sel -> onSummaryHourChange(sel.toInt()) }
                                    )
                                }
                                Text(
                                    text = "시",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W700,
                                    color = Color(0xFF8E8E8E)
                                )
                            }
                        }
                    }
                )
            }
            item { Spacer(modifier = Modifier.height(150.dp)) }
        }
    }
}

@Composable
private fun DropdownButtonWithMenu(
    options: List<String>,
    initialSelectedText: String = options.first(),
    width: Dp = 72.dp,
    maxHeight: Dp = 150.dp,
    onSelected: (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember(initialSelectedText) { mutableStateOf(initialSelectedText) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val isResumed = remember { mutableStateOf(true) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            isResumed.value = event == Lifecycle.Event.ON_RESUME
            if (!isResumed.value) {
                expanded = false
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(
        modifier = Modifier
            .wrapContentSize()
            .height(30.dp)
    ) {
        DropdownButton(
            selectedText = selectedText,
            width = width,
            expanded = expanded,
            onClick = { expanded = !expanded }
        )

        if (expanded && isResumed.value) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .width(width)
                    .heightIn(max = maxHeight)
                    .background(Color.White, shape = RoundedCornerShape(14.dp))
                    .drawWithContent {
                        drawContent()
                        val fade = Brush.verticalGradient(
                            0f to Color.White,
                            0.15f to Color.White.copy(alpha = 0.6f),
                            0.5f to Color.Transparent,
                            0.85f to Color.White.copy(alpha = 0.6f),
                            1f to Color.White
                        )
                        drawRect(fade)
                    }
            ) {
                options.forEach { label ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedText = label
                                onSelected(label)
                                expanded = false
                            }
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = label,
                            color = Color(0xFF8E8E8E),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W700
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DropdownButton(
    selectedText: String,
    width: Dp,
    expanded: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (expanded) Color(0xFFFFEFE4) else Color(0xFFF5F5F5)
    val borderColor = if (expanded) Color(0xFFFFB1A5) else Color.Transparent
    val textColor = if (expanded) Color(0xFFFF8072) else Color(0xFF8E8E8E)
    val textSize = if (expanded) 15.sp else 13.sp

    Box(
        modifier = Modifier
            .width(width)
            .height(34.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(horizontal = 15.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = selectedText,
                color = textColor,
                fontSize = textSize,
                fontWeight = FontWeight.W700
            )
            DropdownChevronIcon(
                expanded = expanded,
                tint = textColor
            )
        }
    }
}


@Composable
private fun WheelPicker(
    items: List<String>,
    modifier: Modifier = Modifier,
    visibleCount: Int = 5,
    rowHeight: Dp = 36.dp,
    initialIndex: Int = 0,
    onSelectedIndexChanged: (Int) -> Unit
) {
    val height = rowHeight * visibleCount
    val state = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val fling = rememberSnapFlingBehavior(lazyListState = state)

    // 컴포저블 컨텍스트에서 density를 캡처
    val density = LocalDensity.current
    // px 변환도 컴포저블 스코프에서 계산(remember로 고정)
    val rowPx = remember(rowHeight, density) { with(density) { rowHeight.toPx() } }

    // 스크롤 종료 시, 중앙 아이템 인덱스 계산해서 콜백
    LaunchedEffect(state, rowPx) {
        snapshotFlow { state.isScrollInProgress }
            .distinctUntilChanged()
            .filter { !it }
            .map {
                val offsetPx = state.firstVisibleItemScrollOffset
                val delta = (offsetPx / rowPx).roundToInt()
                (state.firstVisibleItemIndex + delta).coerceIn(0, items.lastIndex)
            }
            .collectLatest { onSelectedIndexChanged(it) }
    }

    Box(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            // 위/아래 페이드 마스크
            .drawWithContent {
                drawContent()
                val fade = Brush.verticalGradient(
                    0f to Color.White,
                    0.15f to Color.White.copy(alpha = 0.6f),
                    0.5f to Color.Transparent,
                    0.85f to Color.White.copy(alpha = 0.6f),
                    1f to Color.White
                )
                drawRect(fade)
            }
    ) {
        LazyColumn(
            state = state,
            flingBehavior = fling,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = (height - rowHeight) / 2)
        ) {
            items(items.size) { idx ->
                Box(
                    modifier = Modifier
                        .height(rowHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items[idx],
                        fontWeight = FontWeight.W700,
                        color = Color(0xFF8E8E8E)
                    )
                }
            }
        }

        // 중앙 가이드 라인(선택 영역)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .height(rowHeight)
                .fillMaxWidth()
                .background(Color.Transparent)
        )
    }
}

@Composable
private fun TimeWheelDropdown(
    modifier: Modifier = Modifier,
    width: Dp,
    initialHour12: Int = 8,
    initialMinute: Int = 30,
    onChanged: (hour12: Int, minute: Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var hourIdx by remember(initialHour12) { mutableStateOf((initialHour12 - 1).coerceIn(0, 11)) }
    var minuteIdx by remember(initialMinute) { mutableStateOf(initialMinute.coerceIn(0, 59)) }

    val hourItems = remember {
        (1..12).map {
            it
                .toString()
                .padStart(2, '0')
        }
    }

    val minuteItems = remember {
        (0..59).map {
            it
                .toString()
                .padStart(2, '0')
        }
    }

    val headerHeight = 30.dp
    val hPadding = 10.dp
    val iconWidth = 12.dp
    val gap = 15.dp

    Box(modifier = modifier.width(width)) {
        val innerWidth = width - hPadding * 2
        val textBlockWidth = (innerWidth - iconWidth - gap).coerceAtLeast(0.dp)

        // 헤더
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .clip(RoundedCornerShape(14.dp))
                .background(if (expanded) Color(0xFFFFEFE4) else Color(0xFFF5F5F5))
                .border(
                    1.dp,
                    if (expanded) Color(0xFFFFB1A5) else Color.Transparent,
                    RoundedCornerShape(14.dp)
                )
                .clickable { expanded = !expanded }
                .padding(horizontal = hPadding),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.width(textBlockWidth + gap + iconWidth),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.width(textBlockWidth),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = hourItems[hourIdx],
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = if (expanded) 15.sp else 14.sp,
                        fontWeight = FontWeight.W700,
                        color = if (expanded) Color(0xFFFF8072) else Color(0xFF8E8E8E)
                    )
                    Text(
                        text = ":",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = if (expanded) 15.sp else 14.sp,
                        fontWeight = FontWeight.W700,
                        color = if (expanded) Color(0xFFFF8072) else Color(0xFF8E8E8E)
                    )
                    Text(
                        text = minuteItems[minuteIdx],
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = if (expanded) 15.sp else 14.sp,
                        fontWeight = FontWeight.W700,
                        color = if (expanded) Color(0xFFFF8072) else Color(0xFF8E8E8E)
                    )
                }

                Spacer(Modifier.width(gap))
                DropdownChevronIcon(
                    expanded = expanded,
                    tint = if (expanded) Color(0xFFFF8072) else Color(0xFF8E8E8E)
                )
            }
        }

        // 바디
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            properties = PopupProperties(
                focusable = true,
                dismissOnClickOutside = true,
                dismissOnBackPress = true
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .zIndex(10f)
                .width(width)
                .background(Color.White, shape = RoundedCornerShape(14.dp))
        ) {
            Surface(
                modifier = Modifier.width(width),
                shape = RoundedCornerShape(14.dp),
                color = Color.White
            ) {
                Column(Modifier.padding(end = 30.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WheelPicker(
                            items = hourItems,
                            visibleCount = 5,
                            rowHeight = 36.dp,
                            initialIndex = hourIdx,
                            modifier = Modifier.weight(1f)
                        ) { idx ->
                            hourIdx = idx
                            onChanged(hourIdx + 1, minuteIdx)
                        }
                        Text(text = ":", color = Color(0xFF8E8E8E), fontSize = 16.sp)
                        WheelPicker(
                            items = minuteItems,
                            visibleCount = 5,
                            rowHeight = 36.dp,
                            initialIndex = minuteIdx,
                            modifier = Modifier.weight(1f)
                        ) { idx ->
                            minuteIdx = idx
                            onChanged(hourIdx + 1, minuteIdx)
                        }
                    }
                }
            }
        }
    }
}

@Stable
private object NotiIconTokens {
    val CollapsedWidth: Dp = 11.dp
    val CollapsedHeight: Dp = 5.dp
    val ExpandedWidth: Dp = 12.dp
    val ExpandedHeight: Dp = 6.dp
}

/**
 * 공용 드롭다운 체브론 아이콘.
 * - expanded=true  → 12x6 dp
 * - expanded=false → 11x5 dp
 * 기본은 하나의 자원(예: 위쪽 화살표)을 회전해 사용(시각 일관성↑).
 * 두 개 리소스를 꼭 써야 하면 useSingleAsset=false로 전환.
 */
@Composable
private fun DropdownChevronIcon(
    expanded: Boolean,
    tint: Color,
    modifier: Modifier = Modifier,
    useSingleAsset: Boolean = true,   // true: 하나의 아이콘 회전 / false: 리소스 2개 스왑
) {
    val (w, h) = if (expanded)
        NotiIconTokens.ExpandedWidth to NotiIconTokens.ExpandedHeight
    else
        NotiIconTokens.CollapsedWidth to NotiIconTokens.CollapsedHeight

    if (useSingleAsset) {
        // 하나의 아이콘(예: ic_arrow_up)을 회전해서 사용
        Icon(
            painter = painterResource(R.drawable.ic_arrow_up),
            contentDescription = null,
            tint = tint,
            modifier = modifier
                .graphicsLayer { rotationZ = if (expanded) 180f else 0f }
                .width(w)
                .height(h)
        )
    } else {
        // 리소스 2개를 스왑해서 사용
        Icon(
            painter = if (expanded)
                painterResource(R.drawable.ic_arrow_down_expanded)
            else
                painterResource(R.drawable.ic_arrow_up),
            contentDescription = null,
            tint = tint,
            modifier = modifier
                .width(w)
                .height(h)
        )
    }
}

@Preview
@Composable
private fun PreviewNotificationSettingsScreen() {
    ThemeProvider {
        NotificationSettingsScreen(
            state = MyPageViewModel.NotificationSettingsUiState(
                isLoading = false,
                error = null,
                dailyOn = true,
                dailyHour24 = 20,
                dailyMinute = 20,
                summaryOn = true,
                summaryHour24 = 20,
                challengeOn = true,
                challengeRemainingHours = 20,
                locationOn = true,
            ),
            onDailyToggle = {},
            onDailyTimeChange = { _, _, _ -> },
            onSummaryToggle = {},
            onSummaryHourChange = {},
            onChallengeToggle = {},
            onChallengeHoursChange = {},
            onLocationToggle = {},
            onBackClick = {},
        )
    }
}