package com.umc.mypage

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.key
import androidx.compose.runtime.snapshotFlow

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.umc.mypage.components.TopBarComponent
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
){
    val systemUiController = rememberSystemUiController()
    val backgroundColor = Color(0xFFFFFFFF) // 상태바 배경색 (배경과 맞춤)

    val context = LocalContext.current

    // 권한 요청 후 실행할 보류 액션
    val pendingAction = remember { mutableStateOf<(() -> Unit)?>(null) }

    // 멀티 권한 런처 (POST_NOTIFICATIONS/LOCATION 등 한 번에 처리)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val granted = grants.entries.all { (perm, ok) ->
            // TIRAMISU 미만에서는 POST_NOTIFICATIONS가 필요없으므로 ok로 간주
            if (perm == Manifest.permission.POST_NOTIFICATIONS &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
            ) true else ok
        }
        if (granted) {
            pendingAction.value?.invoke()
        } else {
            Toast.makeText(context, "필수 권한이 없어 기능을 사용할 수 없어요.", Toast.LENGTH_SHORT).show()
        }
        pendingAction.value = null
    }

    // 단일 권한 체크
    fun hasPermission(perm: String): Boolean =
        if (perm == Manifest.permission.POST_NOTIFICATIONS &&
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        ) true
        else ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED

    // 공통 보장 함수: 모든 권한이 있으면 onGranted, 아니면 요청
    fun ensurePermissions(perms: Array<String>, onGranted: () -> Unit) {
        val allGranted = perms.all { hasPermission(it) }
        if (allGranted) onGranted()
        else {
            pendingAction.value = onGranted
            permissionLauncher.launch(perms)
        }
    }

    SideEffect {
        systemUiController.setStatusBarColor(
            color = backgroundColor, // ✅ 상태바를 앱 배경색과 동일하게 설정
        )
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .weight(1f) // ✅ BottomNavigation을 밀어내지 않도록 LazyColumn에 weight 적용
            ) {
                // ✅ 2. LazyColumn (스크롤 가능한 콘텐츠)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp)
                        .background(Color.White)
                        .padding(horizontal = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item { Spacer(modifier = Modifier.height(23.dp)) }
                    item {
                        NotificationSettingItem(
                            title = "일기 작성 알림",
                            description = "매일 일정한 시각에 일기 작성을 알리는 알림을 보내요!",
                            checked = state.dailyOn,
                            onCheckedChange = { isOn ->
                                if (!isOn) onDailyToggle(false) else {
                                    ensurePermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS)) {
                                        onDailyToggle(true)
                                    }
                                }
                            }
                            ,
                            bottomContent = {
                                if (state.dailyOn) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    ) {
                                        // 화면에서 파생 상태
                                        val dailyIsPm = state.dailyHour24 >= 12
                                        val dailyHour12 = ((state.dailyHour24 % 12).let { if (it == 0) 12 else it })
                                        val dailyMinute = state.dailyMinute

                                        // 오전/오후 드롭다운
                                        DropdownButtonWithMenu(
                                            options = listOf("오전", "오후"),
                                            initialSelectedText = if (dailyIsPm) "오후" else "오전",
                                            onSelected = { ampm ->
                                                val isPm = ampm == "오후"
                                                onDailyTimeChange(isPm, dailyHour12, dailyMinute) // ← 이름 제거!
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
                        NotificationSettingItem(
                            title = "위치 기반 추억 회상 알림",
                            description = "현재 위치와 가까운 과거 기록을 찾으면 알림을 보내요!",
                            checked = state.locationOn,
                            onCheckedChange = { isOn ->
                                if (!isOn) onLocationToggle(false) else {
                                    ensurePermissions(
                                        arrayOf(
                                            Manifest.permission.POST_NOTIFICATIONS,
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    ) { onLocationToggle(true) }
                                }
                            }

                        )
                    }
                    item { Spacer(modifier = Modifier.height(22.dp)) }
                    item {
                        NotificationSettingItem(
                            title = "챌린지 리마인드 알림",
                            description = "챌린지 달성 마감 전 리마인드 알림을 보내요!",
                            checked = state.challengeOn,
                            onCheckedChange = { isOn ->
                                if (!isOn) onChallengeToggle(false) else {
                                    ensurePermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS)) {
                                        onChallengeToggle(true)
                                    }
                                }
                            },
                            bottomContent = {
                                if (state.challengeOn) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    ) {
                                        DropdownButtonWithMenu(
                                            options = (0..48).map { it.toString().padStart(2, '0') }, // 🔧 범위 확대
                                            initialSelectedText = state.challengeRemainingHours.toString().padStart(2, '0'),
                                            onSelected = { sel ->
                                                onChallengeHoursChange(sel.toInt())
                                            }
                                        )
                                        Text(
                                            text = "시간 전",
                                            fontSize = 14.sp,
                                            color = Color(0xFF8E8E8E)
                                        )
                                    }
                                }
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(22.dp)) }
                    item {
                        NotificationSettingItem(
                            title = "하루 요약 알림",
                            description = "매일 일정한 시각에 오늘의 일기 요약 알림을 보내요!",
                            checked = state.summaryOn,
                            onCheckedChange = { isOn ->
                                if (!isOn) onSummaryToggle(false) else {
                                    ensurePermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS)) {
                                        onSummaryToggle(true)
                                    }
                                }
                            },
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
                                            color = Color(0xFF8E8E8E)
                                        )
                                        // 시간만 선택 (1..12)
                                        androidx.compose.runtime.key(summaryHour12) {
                                            DropdownButtonWithMenu(
                                                options = (1..12).map { it.toString().padStart(2, '0') },
                                                initialSelectedText = summaryHour12.toString().padStart(2, '0'),
                                                onSelected = { sel -> onSummaryHourChange(sel.toInt()) }
                                            )
                                        }
                                        Text(
                                            text = "시",
                                            fontSize = 14.sp,
                                            color = Color(0xFF8E8E8E)
                                        )
                                    }
                                }
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(150.dp)) }
                }

                // ✅ 3. TopBar (스크롤 가능한 LazyColumn 위에 배치)
                TopBarComponent()
            }

            // ✅ 4. BottomNavigationBarWithFAB (항상 하단에 고정)
//            BottomNavigationBarWithFAB(
//                selectedTab = "mypage",
//                onTabSelected = { /* 탭 변경 로직 */ },
//                onFabClick = onFabClick
//            )

        }
    }
}
@Composable
fun NotificationSettingItem(
    title: String,
    description: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    bottomContent: @Composable (() -> Unit)? = null,
    onSwitchOn: (() -> Unit)? = null,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.W400)
                if (description != null) {
                    Text(text = description, fontSize = 12.sp, fontWeight = FontWeight.W400, color = Color(0xFF8E8E8E))
                }
            }

            CustomSwitch(
                checked = checked,
                onCheckedChange = {
                    onCheckedChange(it)
                    if (it) onSwitchOn?.invoke()
                }
            )
        }

        // ✅ 스위치가 켜졌고, 하위 content가 있다면 보여줌
        if (checked && bottomContent != null) {
            Spacer(modifier = Modifier.height(12.dp))
            bottomContent()
        }
    }
}

@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // 전체 Switch 박스
    Box(
        modifier = modifier
            .width(46.dp) // Switch 전체 너비
            .height(26.dp) // Switch 전체 높이
            .graphicsLayer {
                shadowElevation = 2.dp.toPx() // ✅ 박스용 shadow
                shape = RoundedCornerShape(12.dp)
                clip = false
            }
            .clip(
                RoundedCornerShape(12.dp) // 커스텀 Border-Radius
            )
            .background(
                // 오렌지 400
                if (checked) Color(0xFFFF9888) else Color(0xFFE1E1E1) // 상태에 따른 배경색
            )
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Thumb (Circle)
        Box(
            modifier = Modifier
                .size(21.9.dp) // Thumb 크기
                .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart) // 상태에 따른 위치
                .graphicsLayer {
                    shadowElevation = 2.dp.toPx() // ✅ Thumb용 shadow
                    shape = CircleShape
                    clip = false
                }
                .clip(CircleShape) // 원형
                .background(Color(0xFFF5F5F5)) // Thumb 배경색
        )
    }
}

@Composable
fun DropdownButtonWithMenu(
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
fun DropdownButton(
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
fun WheelPicker(
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
            .filter { it == false }
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
fun TimeWheelDropdown(
    modifier: Modifier = Modifier,
    width: Dp,
    initialHour12: Int = 8,
    initialMinute: Int = 30,
    onChanged: (hour12: Int, minute: Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var hourIdx by remember(initialHour12) { mutableStateOf((initialHour12 - 1).coerceIn(0, 11)) }
    var minuteIdx by remember(initialMinute) { mutableStateOf(initialMinute.coerceIn(0, 59)) }

    val hourItems = remember { (1..12).map { it.toString().padStart(2, '0') } }
    val minuteItems = remember { (0..59).map { it.toString().padStart(2, '0') } }

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
                .border(1.dp, if (expanded) Color(0xFFFFB1A5) else Color.Transparent, RoundedCornerShape(14.dp))
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
object NotiIconTokens {
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
fun DropdownChevronIcon(
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

//@Composable
//fun DropdownMenuBox(
//    expanded: Boolean,
//    onDismissRequest: () -> Unit,
//    options: List<String>,
//    onOptionSelected: (String) -> Unit,
//    width: Dp,
//    maxHeight: Dp = 150.dp
//) {
//    Box(
//        modifier = Modifier
//            .width(width)
//            .wrapContentHeight()
//    ) {
//        if (expanded) {
//            // 뒤 배경 클릭 시 닫기
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .clickable(
//                        indication = null,
//                        interactionSource = remember { MutableInteractionSource() }
//                    ) { onDismissRequest() }
//            )
//        }
//
//        AnimatedVisibility(
//            visible = expanded,
//            enter = fadeIn() + expandVertically(),
//            exit = fadeOut() + shrinkVertically()
//        ) {
//            Surface(
//                color = Color.White,
//                shape = RoundedCornerShape(14.dp),
//                shadowElevation = 4.dp,
//                modifier = Modifier
//                    .width(width)
//                    .heightIn(max = maxHeight)
//            ) {
//                val scrollState = rememberScrollState()
//
//                Box {
//                    Column(
//                        modifier = Modifier
//                            .heightIn(max = maxHeight)
//                            .verticalScroll(scrollState)
//                    ) {
//                        options.forEachIndexed { index, label ->
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(30.dp)
//                                    .clickable {
//                                        onOptionSelected(label)
//                                        onDismissRequest()
//                                    }
//                                    .padding(horizontal = 12.dp, vertical = 3.dp),
//                                contentAlignment = Alignment.CenterStart
//                            ) {
//                                Text(
//                                    text = label,
//                                    fontSize = 14.sp,
//                                    color = Color(0xFF8E8E8E)
//                                )
//                            }
//                            if (index != options.lastIndex) {
//                                Spacer(modifier = Modifier.height(3.dp))
//                            }
//                        }
//                    }
//
//                    // 아래 Fade
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(20.dp)
//                            .background(
//                                brush = Brush.verticalGradient(
//                                    colors = listOf(Color.Transparent, Color.White)
//                                )
//                            )
//                            .align(Alignment.BottomCenter)
//                    )
//                }
//            }
//
//        }
//    }
//}

