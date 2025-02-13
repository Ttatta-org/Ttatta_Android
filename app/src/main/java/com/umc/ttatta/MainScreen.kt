package com.umc.ttatta

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import com.umc.ttatta.component.NavigationBar
import com.umc.ttatta.component.NavigationItem
import com.umc.ttatta.component.RecordOptionPicker
import com.umc.ttatta.component.RecordOptionPickerProp
import com.umc.ttatta.component.ShadowedImage
import com.umc.ttatta.component.centerButtonSize

data class NavigationBarProp(
    val currentNavigationItem: NavigationItem,
    val onNavigate: (NavigationItem) -> Unit,
    val onCenterButtonClicked: () -> Unit,
)

data class CenterButtonProp(
    val recordOptionPickerProp: RecordOptionPickerProp,
    val onDismissed: () -> Unit,
)

private val centerButtonTopOffsetFromNavBarTopCenter = 12.dp

@Composable
fun MainScreen(
    navigationBarProp: NavigationBarProp?,
    centerButtonProp: CenterButtonProp?,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current

    var centerButtonCenter by remember { mutableStateOf(Offset.Zero) }
    var recordOptionPickerHeight by remember { mutableIntStateOf(0) }

    var residualCenterButtonProp by remember { mutableStateOf(centerButtonProp) }

    LaunchedEffect(key1 = centerButtonProp) {
        centerButtonProp?.let { residualCenterButtonProp = it }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Green)
            .let {
                if (centerButtonProp != null) it.clickable(
                    indication = null,
                    interactionSource = null,
                    onClick = centerButtonProp.onDismissed
                ) else it
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .let { if (centerButtonProp != null) it.blur(16.dp) else it }
        ) {
            // 화면
            Box(
                modifier = Modifier.weight(1f)
            ) {
                content()
            }
            // 내비게이션 바
            if (navigationBarProp != null) Box(
                modifier = Modifier.onGloballyPositioned {
                    val offset = it.positionInParent()
                    centerButtonCenter = with(density) {
                        Offset(
                            x = offset.x + it.size.width / 2,
                            y = offset.y + centerButtonTopOffsetFromNavBarTopCenter.toPx()
                        )
                    }
                }
            ) {
                NavigationBar(
                    currentNavigationItem = navigationBarProp.currentNavigationItem,
                    onNavigate = navigationBarProp.onNavigate,
                )
            }
        }
        // 중앙 버튼
        if (navigationBarProp != null) Box(
            modifier = Modifier.offset {
                Offset(
                    x = centerButtonCenter.x - centerButtonSize.width.toPx() / 2,
                    y = centerButtonCenter.y - centerButtonSize.height.toPx() / 2
                ).round()
            }
        ) {
            IconButton(
                onClick = centerButtonProp?.onDismissed ?: navigationBarProp.onCenterButtonClicked,
                modifier = Modifier.size(centerButtonSize)
            ) {
                ShadowedImage(
                    id = if (centerButtonProp == null)
                        R.drawable.btn_record
                    else
                        R.drawable.btn_cancel_record,
                    contentDescription = null,
                    width = centerButtonSize.width,
                    height = centerButtonSize.height,
                    shadowColor = Color.Black.copy(alpha = 0.5f),
                    shadowBlur = 8.dp,
                    offsetY = 4.dp
                )
            }
        }
        // 중앙 버튼 클릭 시 표시되는 다이얼로그 버튼
        AnimatedVisibility(
            visible = centerButtonProp != null,
            enter = fadeIn(animationSpec = tween(durationMillis = 200)),
            exit = fadeOut(animationSpec = tween(durationMillis = 200)),
            modifier = Modifier.offset {
                Offset(
                    x = 0f,
                    y = centerButtonCenter.y
                            - centerButtonSize.height.toPx() / 2
                            - recordOptionPickerHeight
                ).round()
            }
        ) {
            residualCenterButtonProp?.let { prop ->
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned {
                            recordOptionPickerHeight = it.size.height
                        }
                ) {
                    RecordOptionPicker(
                        prop = prop.recordOptionPickerProp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewNavigationBarScreen() {
    var currentNavigationItem by remember { mutableStateOf(NavigationItem.DIARY) }
    var isCenterButtonActivated by remember { mutableStateOf(false) }

    MainScreen(
        navigationBarProp = NavigationBarProp(
            currentNavigationItem = currentNavigationItem,
            onNavigate = { currentNavigationItem = it },
            onCenterButtonClicked = { isCenterButtonActivated = true },
        ),
        centerButtonProp = if (isCenterButtonActivated) CenterButtonProp(
            recordOptionPickerProp = RecordOptionPickerProp(
                userName = "test",
                onCameraOptionClicked = {},
                onGalleryOptionClicked = {},
            ),
            onDismissed = { isCenterButtonActivated = false }
        ) else null,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = "테스트")
        }
    }
}