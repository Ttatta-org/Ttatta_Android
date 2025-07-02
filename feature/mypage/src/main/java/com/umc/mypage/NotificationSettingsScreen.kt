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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.umc.mypage.components.TopBarComponent
import kotlin.math.roundToInt

@Composable
fun NotificationSettingsScreen(

){
    val systemUiController = rememberSystemUiController()
    val backgroundColor = Color(0xFFFFFFFF) // 상태바 배경색 (배경과 맞춤)

    var dailyReminder by remember { mutableStateOf(false) }
    var locationReminder by remember { mutableStateOf(false) }
    var challengeReminder by remember { mutableStateOf(false) }
    var summaryReminder by remember { mutableStateOf(false) }

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
                            checked = dailyReminder,
                            onCheckedChange = { dailyReminder = it },
                            bottomContent = {
                                if (dailyReminder) {
                                    DropdownButtonWithMenu(
                                        options = listOf("오전", "오후"),
                                        initialSelectedText = "오전",
                                        onSelected = { selected ->
                                            // ✅ 선택된 값에 따른 처리
                                            Log.d("NotificationSetting", "선택된 시간: $selected")
                                        }
                                    )
                                }
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(22.dp)) }
                    item {
                        NotificationSettingItem(
                            title = "위치 기반 추억 회상 알림",
                            description = "현재 위치와 가까운 과거 기록을 찾으면 알림을 보내요!",
                            checked = locationReminder,
                            onCheckedChange = { locationReminder = it }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(22.dp)) }
                    item {
                        NotificationSettingItem(
                            title = "챌린지 리마인드 알림",
                            description = "챌린지 달성 마감 전 리마인드 알림을 보내요!",
                            checked = challengeReminder,
                            onCheckedChange = { challengeReminder = it },
                            bottomContent = {
                                if (challengeReminder) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    ) {
                                        DropdownButtonWithMenu(
                                            options = (1..11).map { it.toString().padStart(2, '0') },
                                            initialSelectedText = "01",
                                            onSelected = { selected ->
                                                // ✅ 선택된 값에 따른 처리
                                                Log.d("NotificationSetting", "선택된 시간: $selected")
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
                            checked = summaryReminder,
                            onCheckedChange = { summaryReminder = it },
                            bottomContent = {
                                if (summaryReminder) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    ) {
                                        Text(
                                            text = "오후",
                                            fontSize = 14.sp,
                                            color = Color(0xFF8E8E8E)
                                        )

                                        DropdownButtonWithMenu(
                                            options = (1..12).map { it.toString().padStart(2, '0') },
                                            initialSelectedText = "01",
                                            onSelected = { selected ->
                                                // ✅ 선택된 값에 따른 처리
                                                Log.d("NotificationSetting", "선택된 시간: $selected")
                                            }
                                        )

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
    bottomContent: @Composable (() -> Unit)? = null
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
                onCheckedChange = { onCheckedChange(it) }
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
    var selectedText by remember { mutableStateOf(initialSelectedText) }
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
        modifier = Modifier.wrapContentSize()
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
    val icon = if (expanded) painterResource(id = R.drawable.ic_arrow_down_expanded) else painterResource(id = R.drawable.ic_arrow_up)

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
            Icon(
                painter = icon,
                contentDescription = "선택",
                tint = textColor
            )
        }
    }
}

@Composable
fun DropdownMenuBox(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    width: Dp,
    maxHeight: Dp = 150.dp
) {
    Box(
        modifier = Modifier
            .width(width)
            .wrapContentHeight()
    ) {
        if (expanded) {
            // 뒤 배경 클릭 시 닫기
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onDismissRequest() }
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(14.dp),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .width(width)
                    .heightIn(max = maxHeight)
            ) {
                val scrollState = rememberScrollState()

                Box {
                    Column(
                        modifier = Modifier
                            .heightIn(max = maxHeight)
                            .verticalScroll(scrollState)
                    ) {
                        options.forEachIndexed { index, label ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(30.dp)
                                    .clickable {
                                        onOptionSelected(label)
                                        onDismissRequest()
                                    }
                                    .padding(horizontal = 12.dp, vertical = 3.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 14.sp,
                                    color = Color(0xFF8E8E8E)
                                )
                            }
                            if (index != options.lastIndex) {
                                Spacer(modifier = Modifier.height(3.dp))
                            }
                        }
                    }

                    // 아래 Fade
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.White)
                                )
                            )
                            .align(Alignment.BottomCenter)
                    )
                }
            }

        }
    }
}

@Composable
fun NDropdownMenuBox(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    width: Dp,
    maxHeight: Dp = 200.dp
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .width(width)
            .background(Color.White, shape = RoundedCornerShape(14.dp))
    ) {
        Column(
            modifier = Modifier
                .heightIn(max = maxHeight)
                .verticalScroll(rememberScrollState())
        ) {
            options.forEach { label ->
                DropdownMenuItem(
                    onClick = {
                        onOptionSelected(label)
                        onDismissRequest()
                    },
                    text = {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            color = Color(0xFF4B4B4B),
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }
                )
            }
        }
    }
}


//@Composable
//fun TimePickerRow(
//    amPm: String?,
//    hour: String?,
//    minute: String?,
//    onAmPmSelected: (String) -> Unit,
//    onHourSelected: (String) -> Unit,
//    onMinuteSelected: (String) -> Unit
//) {
//    val isTimeSelected = hour != null && minute != null && amPm != null
//    val backgroundColor = if (isTimeSelected) Color(0xFFFFEFE4) else Color(0xFFE1E1E1)
//
//    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
//        // AM/PM 선택 버튼
//        DropdownSelector(
//            options = listOf("오전", "오후"),
//            selectedOption = amPm,
//            onOptionSelected = onAmPmSelected,
//            width = 72.dp
//        )
//
//        // 시간:분 선택 버튼
//        DropdownSelector(
//            options = (1..12).map { it.toString().padStart(2, '0') }
//                .flatMap { hour -> listOf(hour, ":") } +
//                    (0..59).map { it.toString().padStart(2, '0') },
//            selectedOption = "$hour:$minute",
//            onOptionSelected = { time ->
//                val parts = time.split(":")
//                if (parts.size == 2) {
//                    onHourSelected(parts[0])
//                    onMinuteSelected(parts[1])
//                }
//            },
//            width = 130.dp
//        )
//    }
//}
//@Composable
//fun DropdownSelector(
//    options: List<String>,
//    selectedOption: String?,
//    onOptionSelected: (String) -> Unit,
//    width: Dp
//) {
//    var expanded by remember { mutableStateOf(false) }
//
//    Column { // ✅ Column으로 감싸 버튼 아래로 드롭다운 배치 고정
//        Box(
//            modifier = Modifier
//                .width(width)
//                .height(36.dp)
//                .clip(RoundedCornerShape(14.dp))
//                .background(if (selectedOption == null) Color(0xFFE1E1E1) else Color(0xFFFFEFE4))
//                .clickable { expanded = !expanded }
//                .padding(start = 15.dp, end = 13.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Row(
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text(
//                    text = selectedOption ?: options.first(),
//                    color = if (selectedOption == null) Color.Gray else Color(0xFFFF9888),
//                    fontSize = 13.sp,
//                    fontWeight = FontWeight.W700
//                )
//                Icon(
//                    imageVector = Icons.Default.ArrowDropDown,
//                    contentDescription = "선택",
//                    tint = if (selectedOption == null) Color.Gray else Color(0xFFFF9888)
//                )
//            }
//        }
//
//        if (expanded) {
//            Box(
//                modifier = Modifier
//                    .width(width)
//                    .shadow(
//                        elevation = 4.dp,
//                        shape = RoundedCornerShape(14.dp),
//                        clip = false // ✅ 그림자만 적용하고 clip은 직접 해줌
//                    )
//                    .clip(RoundedCornerShape(14.dp)) // ✅ radius 유지
//                    .background(Color.White)         // ✅ 배경 흰색
//            ) {
//                Column {
//                    options.forEach { label ->
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .clickable {
//                                    onOptionSelected(label)
//                                    expanded = false
//                                }
//                                .padding(vertical = 12.dp, horizontal = 16.dp),
//                            contentAlignment = Alignment.CenterStart
//                        ) {
//                            Text(
//                                text = label,
//                                fontSize = 14.sp,
//                                color = if (label == selectedOption) Color(0xFFFF9888) else Color(0xFF4B4B4B)
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}



@Preview(showBackground = true)
@Composable
fun PreviewNotification() {
    NotificationSettingsScreen(

    )
}
