package com.umc.challenge.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import com.umc.challenge.R
import com.umc.challenge.component.PointChip
import com.umc.challenge.component.PointChipTheme
import com.umc.challenge.modal.PurchaseDialog
import com.umc.challenge.modal.PurchaseDialogProp
import com.umc.challenge.view.previewAccessorySet
import com.umc.design.character.Accessory
import com.umc.design.character.AccessorySet
import com.umc.design.character.BodyPart
import com.umc.design.character.CharacterView
import com.umc.design.component.CustomHeader
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider
import com.umc.design.R as Res

data class ShopItemItemProp(
    val accessory: Accessory,
    /**
     * null일 경우 가격 표시 안함
     *
     * 0일 경우 기본 아이템으로 표시
     *
     * 그 외에는 해당 가격 표시
     */
    val cost: Int?,
    val isOwned: Boolean,
    val isEquipped: Boolean,
    val onClicked: () -> Unit,
)

private val bottomSheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
private val itemItemShape = RoundedCornerShape(16.dp)

@Composable
fun ShopScreen(
    point: Int,
    selectedBodyPart: BodyPart?,
    equippedAccessorySet: AccessorySet,
    shopItemItemPropList: List<ShopItemItemProp>,
    purchaseDialogProp: PurchaseDialogProp?,
    onBodyPartSelected: (BodyPart?) -> Unit,
    onBackButtonClicked: () -> Unit,
) {
    val density = LocalDensity.current
    val scrollState = rememberLazyGridState()

    var screenWidth: Dp? by remember { mutableStateOf(null) }
    var screenHeight: Dp? by remember { mutableStateOf(null) }
    var topBarHeight: Dp? by remember { mutableStateOf(null) }
    var characterViewWidth by remember { mutableStateOf(0.dp) }

    var bottomSheetHeight: Dp? by remember { mutableStateOf(null) }
    var bottomSheetMinHeight: Dp? by remember { mutableStateOf(null) }
    val bottomSheetMaxHeight: Dp? = remember(screenHeight, topBarHeight) {
        val screenHeight = screenHeight
        val topBarHeight = topBarHeight

        if (screenHeight == null || topBarHeight == null) return@remember null
        else screenHeight - (topBarHeight + 64.dp)
    }

    val bottomSheetState = rememberDraggableState {
        bottomSheetHeight?.let { height ->
            val newHeight = height - with(density) { it.toDp() }

            bottomSheetHeight = newHeight.coerceIn(
                minimumValue = bottomSheetMinHeight,
                maximumValue = bottomSheetMaxHeight,
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                with(density) {
                    screenWidth = it.size.width.toDp()
                    screenHeight = it.size.height.toDp()
                }
            },
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(R.drawable.img_challenge_background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        // 내용
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 상단바
            Box(
                modifier = Modifier.onGloballyPositioned {
                    topBarHeight = with(density) { it.size.height.toDp() }
                },
            ) {
                CustomHeader(
                    showLogo = false,
                    backgroundColor = Color(0xFFFFE6E1).copy(alpha = 0.5f),
                    waveColor = LocalColorTheme.current.primary[300],
                    headerTrailing = {
                        Box(
                            modifier = Modifier.padding(horizontal = 21.98.dp)
                        ) {
                            PointChip(
                                point = point,
                                theme = PointChipTheme.EAGLE
                            )
                        }
                    },
                    onBackButtonClicked = onBackButtonClicked,
                )
            }
            Spacer(modifier = Modifier.height(100.dp))
            // 캐릭터
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
                    .onGloballyPositioned {
                        with(density) {
                            characterViewWidth = it.size.width.toDp()
                        }
                    }
            ) {
                CharacterView(
                    accessorySet = equippedAccessorySet,
                    width = min(characterViewWidth, 480.dp),
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            // 남은 공간 계산
            Box(
                modifier = Modifier
                    .weight(1f)
                    .onGloballyPositioned {
                        val initialHeight = with(density) { it.size.height.toDp() }
                        bottomSheetMinHeight = initialHeight
                        bottomSheetHeight = initialHeight
                    }
            )
        }
        // 상점 팻말 리소스
        run {
            val screenWidth = screenWidth
            val topBarHeight = topBarHeight

            if (screenWidth == null || topBarHeight == null) return@run

            Image(
                painter = painterResource(R.raw.img_shop_tag),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .width(110.dp)
                    .offset {
                        with(density) {
                            Offset(
                                x = (screenWidth.toPx() - 110.dp.toPx()) / 2,
                                y = topBarHeight.toPx() - 28.dp.toPx(),
                            )
                        }.round()
                    }
            )
        }
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            // 아이템 바텀 시트
            bottomSheetHeight?.let { bottomSheetHeight ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .dropShadow(
                            shape = bottomSheetShape,
                            shadow = Shadow(
                                radius = 15.dp,
                                offset = DpOffset(0.dp, (-2).dp),
                                color = Color(0xFF9C9C9C),
                                alpha = 0.2f,
                            ),
                        )
                        .background(
                            color = Color.White,
                            shape = bottomSheetShape
                        )
                        .height(bottomSheetHeight)
                        .fillMaxWidth()
                ) {
                    // 드래그 핸들
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .draggable(
                                state = bottomSheetState,
                                orientation = Orientation.Vertical
                            )
                    ) {
                        Image(
                            painter = painterResource(id = Res.drawable.ic_header_deco),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.width(39.53.dp)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 29.dp),
                    ) {
                        listOf(
                            null to "전체",
                            BodyPart.HEAD to "머리",
                            BodyPart.EYE to "눈",
                            BodyPart.TORSO to "몸",
                        ).forEach { (bodyPart, label) ->
                            val isSelected = bodyPart == selectedBodyPart

                            Box(
                                modifier = Modifier.clickable(
                                    indication = null,
                                    interactionSource = null,
                                    onClick = { onBodyPartSelected(bodyPart) },
                                )
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) LocalColorTheme.current.primary[600] else LocalColorTheme.current.grey[400],
                                    fontWeight = if (isSelected) FontWeight.W800 else FontWeight.W700,
                                    fontSize = 16.sp,
                                    lineHeight = 20.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            state = scrollState,
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                count = shopItemItemPropList.size,
                            ) { index ->
                                ShopItemItem(prop = shopItemItemPropList[index])
                            }
                        }
                        // 하얀 블러처리
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (scrollState.canScrollForward) Box(
                                contentAlignment = Alignment.BottomCenter,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .height(48.dp)
                                        .fillMaxWidth()
                                        .background(
                                            brush = Brush.verticalGradient(
                                                0f to Color.Transparent,
                                                1f to Color.White,
                                            ),
                                        )
                                )
                            }
                            if (scrollState.canScrollBackward) Box(
                                contentAlignment = Alignment.TopCenter,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .height(48.dp)
                                        .fillMaxWidth()
                                        .background(
                                            brush = Brush.verticalGradient(
                                                0f to Color.White,
                                                1f to Color.Transparent,
                                            ),
                                        )
                                )
                            }
                        }
                    }
                    Spacer(
                        modifier = Modifier.height(
                            WindowInsets.systemBars
                                .asPaddingValues()
                                .calculateBottomPadding()
                        )
                    )
                }
            }
        }
    }

    purchaseDialogProp?.let { PurchaseDialog(prop = it) }
}

@Composable
private fun ShopItemItem(
    prop: ShopItemItemProp
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(itemItemShape)
                .clickable { prop.onClicked() }
                .border(
                    width = 1.dp,
                    color = LocalColorTheme.current.primary[200],
                    shape = itemItemShape,
                )
        ) {
            when {
                prop.isEquipped -> "착용중"
                prop.isOwned -> "보유"
                else -> null
            }?.let { badge ->
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                shape = RoundedCornerShape(percent = 50),
                                color = LocalColorTheme.current.primary[100]
                            )
                            .padding(horizontal = 10.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = badge,
                            fontWeight = FontWeight.W700,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = LocalColorTheme.current.primary[500],
                        )
                    }
                }
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = prop.accessory.shopRes),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = prop.accessory.title,
                fontWeight = FontWeight.W700,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                color = LocalColorTheme.current.grey[700],
                modifier = Modifier.padding(start = 5.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.36.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .let { if (prop.cost == null) it.alpha(0f) else it },
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_point_icon_1),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.height(18.dp),
                )
                Text(
                    text = when (prop.cost) {
                        null -> "0"
                        0 -> "기본"
                        else -> prop.cost.toString()
                    },
                    fontWeight = FontWeight.W700,
                    fontSize = 14.sp,
                    color = LocalColorTheme.current.grey[700],
                )
            }
        }
    }
}

val previewShopItemItemPropList = listOf(
    ShopItemItemProp(
        accessory = Accessory.TTUTTU_PERL_NECKLACE,
        cost = 0,
        isOwned = false,
        isEquipped = true,
        onClicked = {}
    ),
    ShopItemItemProp(
        accessory = Accessory.TTOTTO_COZY_MUFFLER,
        cost = 600,
        isOwned = true,
        isEquipped = false,
        onClicked = {}
    ),
    ShopItemItemProp(
        accessory = Accessory.TTOTTO_CAP,
        cost = null,
        isOwned = false,
        isEquipped = false,
        onClicked = {}
    ),
    ShopItemItemProp(
        accessory = Accessory.TTUTTU_BAG,
        cost = null,
        isOwned = false,
        isEquipped = false,
        onClicked = {}
    ),
    ShopItemItemProp(
        accessory = Accessory.TTUTTU_GIANT_RIBBON,
        cost = 300,
        isOwned = false,
        isEquipped = false,
        onClicked = {}
    )
)

@Preview
@Composable
fun PreviewShopScreen() {
    ThemeProvider {
        ShopScreen(
            point = 1234,
            selectedBodyPart = null,
            equippedAccessorySet = previewAccessorySet,
            shopItemItemPropList = previewShopItemItemPropList,
            purchaseDialogProp = null,
            onBodyPartSelected = {},
            onBackButtonClicked = {},
        )
    }
}
