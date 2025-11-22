package com.umc.ttatta.app

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import com.umc.design.character.Accessory
import com.umc.design.character.AccessorySet
import com.umc.design.theme.ThemeProvider
import com.umc.ttatta.app.component.NavigationBar
import com.umc.ttatta.app.component.NavigationItem
import com.umc.ttatta.app.component.RecordOptionPicker
import com.umc.ttatta.app.component.ShadowedImage
import com.umc.ttatta.app.component.centerButtonSize
import com.umc.ttatta.app.model.prop.CenterButtonProp
import com.umc.ttatta.app.model.prop.NavigationBarProp
import com.umc.ttatta.app.model.prop.RecordOptionPickerProp

private val centerButtonTopOffsetFromNavBarTopCenter = 12.dp
private val recordTooltipSize = DpSize(175.dp, 40.5.dp)

@Composable
fun MainScreen(
    navigationBarProp: NavigationBarProp?,
    centerButtonProp: CenterButtonProp?,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current

    var centerButtonCenter by remember { mutableStateOf<Offset?>(null) }
    var recordOptionPickerHeight by remember { mutableIntStateOf(0) }
    var residualCenterButtonProp by remember { mutableStateOf(centerButtonProp) }

    LaunchedEffect(key1 = centerButtonProp) {
        centerButtonProp?.let { residualCenterButtonProp = it }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .let { if (centerButtonProp != null) it.blur(16.dp) else it },
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
                },
            ) {
                NavigationBar(
                    currentNavigationItem = navigationBarProp.currentNavigationItem,
                    onNavigate = navigationBarProp.onNavigate,
                )
            }
        }
        // 중앙 버튼
        if (navigationBarProp != null) centerButtonCenter?.let { (x, y) ->
            Box(
                modifier = Modifier.offset {
                    Offset(
                        x = x - centerButtonSize.width.toPx() / 2,
                        y = y - centerButtonSize.height.toPx() / 2
                    ).round()
                },
            ) {
                Box(
                    modifier = Modifier
                        .size(centerButtonSize)
                        .clickable(
                            indication = null,
                            interactionSource = null,
                            onClick = centerButtonProp?.onDismissed
                                ?: navigationBarProp.onCenterButtonClicked,
                        )
                ) {
                    ShadowedImage(
                        id = if (centerButtonProp == null) R.drawable.btn_record else R.drawable.btn_cancel_record,
                        contentDescription = null,
                        width = centerButtonSize.width,
                        height = centerButtonSize.height,
                        shadowColor = Color.Black.copy(alpha = 0.25f),
                        shadowBlur = 8.dp,
                        offsetY = 4.dp
                    )
                }
            }

            AnimatedVisibility(
                visible = navigationBarProp.showTooltip,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.offset {
                    Offset(
                        x = x - recordTooltipSize.width.toPx() / 2,
                        y = y - centerButtonSize.height.toPx() / 2 - 9.5.dp.toPx() - recordTooltipSize.height.toPx(),
                    ).round()
                },
            ) {
                Box(
                    modifier = Modifier.padding(bottom = 9.5.dp)
                ) {
                    ShadowedImage(
                        id = R.drawable.img_record_tooltip,
                        contentDescription = null,
                        width = recordTooltipSize.width,
                        height = recordTooltipSize.height,
                        shadowColor = Color(0xFFDE806E).copy(alpha = 0.1f),
                        shadowBlur = 10.dp,
                    )
                }
            }
        }
        // 중앙 버튼 클릭 시 표시되는 다이얼로그 버튼
        Box(
            modifier = Modifier
                .fillMaxSize()
                .let {
                    if (centerButtonProp != null) it.clickable(
                        indication = null,
                        interactionSource = null,
                        onClick = centerButtonProp.onDismissed
                    ) else it
                },
        ) {
            AnimatedVisibility(
                visible = centerButtonProp != null,
                enter = fadeIn(animationSpec = tween(durationMillis = 200)),
                exit = fadeOut(animationSpec = tween(durationMillis = 200)),
                modifier = Modifier
                    .offset {
                        centerButtonCenter?.let { (_, y) ->
                            Offset(
                                x = 0f,
                                y = y - centerButtonSize.height.toPx() / 2 - recordOptionPickerHeight - 12.dp.toPx()
                            ).round()
                        } ?: Offset.Zero.round()
                    },
            ) {
                residualCenterButtonProp?.let { prop ->
                    Box(
                        contentAlignment = Alignment.TopCenter,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .onGloballyPositioned {
                                recordOptionPickerHeight = it.size.height
                            },
                    ) {
                        RecordOptionPicker(prop = prop.recordOptionPickerProp)
                        BackHandler { prop.onDismissed() }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    var currentNavigationItem by remember { mutableStateOf(NavigationItem.DIARY) }
    var isCenterButtonActivated by remember { mutableStateOf(false) }

    ThemeProvider {
        MainScreen(
            navigationBarProp = NavigationBarProp(
                currentNavigationItem = currentNavigationItem,
                showTooltip = true,
                onNavigate = { currentNavigationItem = it },
                onCenterButtonClicked = { isCenterButtonActivated = true },
            ),
            centerButtonProp = if (isCenterButtonActivated) CenterButtonProp(
                recordOptionPickerProp = RecordOptionPickerProp(
                    userName = "test",
                    accessories = AccessorySet.create(
                        Accessory.TTOTTO_BAG,
                        Accessory.TTOTTO_HAT,
                        Accessory.TTUTTU_BAG,
                        Accessory.TTUTTU_HAT,
                    ),
                    onCameraOptionClicked = {},
                    onGalleryOptionClicked = {},
                ),
                onDismissed = { isCenterButtonActivated = false },
            ) else null,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(text = "테스트")
            }
        }
    }
}