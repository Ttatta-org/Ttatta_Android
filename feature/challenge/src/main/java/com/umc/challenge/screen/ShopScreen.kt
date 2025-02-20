package com.umc.challenge.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.challenge.R
import com.umc.challenge.component.PointChip
import com.umc.challenge.component.PointChipTheme
import com.umc.challenge.component.PurchaseDialog
import com.umc.challenge.component.PurchaseDialogProp
import com.umc.challenge.component.TitledTopBar
import com.umc.challenge.component.TitledTopBarMode
import com.umc.challenge.component.TitledTopBarProp
import com.umc.challenge.view.previewAccessorySet
import com.umc.design.Primary300
import com.umc.design.Secondary100
import com.umc.design.character.Accessory
import com.umc.design.character.AccessorySet
import com.umc.design.character.CharacterView
import com.umc.design.R as Res

data class ShopItemItemProp(
    val accessory: Accessory,
    val cost: Int,
    val onClicked: () -> Unit,
)

private val bottomSheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
private val itemItemShape = RoundedCornerShape(16.dp)

@Composable
fun ShopScreen(
    point: Int,
    equippedAccessorySet: AccessorySet,
    shopItemItemPropList: List<ShopItemItemProp>,
    purchaseDialogProp: PurchaseDialogProp?,
    onMyItemsIconClicked: () -> Unit
) {
    val density = LocalDensity.current
    val scrollState = rememberLazyGridState()

    var topBarHeight by remember { mutableStateOf(0.dp) }
    var chipMenuVerticalOffset by remember { mutableStateOf(0.dp) }
    var characterViewWidth by remember { mutableStateOf(0.dp) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Secondary100)
    ) {
        // 내용
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(chipMenuVerticalOffset + 32.dp))
            // 캐릭터
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
                    .onGloballyPositioned { with(density) { characterViewWidth = it.size.width.toDp() } }
            ) {
                CharacterView(
                    accessorySet = equippedAccessorySet,
                    width = characterViewWidth
                )
            }
            // 아이템 바텀 시트
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .shadow(
                        elevation = 16.dp,
                        shape = bottomSheetShape
                    )
                    .background(
                        color = Color.White,
                        shape = bottomSheetShape
                    )
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = Res.drawable.ic_header_deco),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(32.dp)
                    )
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
                        items(shopItemItemPropList.size) { index ->
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
                        WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
                    )
                )
            }
        }
        // 탑 바
        TitledTopBar(
            prop = TitledTopBarProp(
                mode = TitledTopBarMode.SHOP,
                onHeightChanged = { topBarHeight = it }
            )
        )
        Column(
            modifier = Modifier.onGloballyPositioned {
                with(density) { chipMenuVerticalOffset = it.size.height.toDp() }
            }
        ) {
            Spacer(modifier = Modifier.height(topBarHeight))
            // 상단 메뉴
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp, alignment = Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(end = 16.dp, top = 32.dp)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = null,
                        onClick = onMyItemsIconClicked
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_my_items),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(24.dp)
                    )
                }
                PointChip(
                    point = point,
                    theme = PointChipTheme.EAGLE
                )
            }
        }
    }

    purchaseDialogProp?.let { PurchaseDialog(prop = it) }
}

@Composable
private fun ShopItemItem(
    prop: ShopItemItemProp
) {
    val density = LocalDensity.current
    var width by remember { mutableStateOf(0.dp) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(width)
            .background(
                brush = Brush.verticalGradient(
                    0f to Color(0x80FEF6F2),
                    1f to Color(0x80FFEAE2)
                ),
                shape = itemItemShape
            )
            .clip(itemItemShape)
            .clickable { prop.onClicked() }
            .border(
                width = 1.dp,
                color = Color(0xFFFFDACB),
                shape = itemItemShape,
            )
            .onGloballyPositioned { with(density) { width = it.size.width.toDp() } }
    ) {
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
                        color = Color(0xFFFFEAE2)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${prop.cost} ${stringResource(id = R.string.point_unit)}",
                    fontWeight = FontWeight.W600,
                    fontSize = 12.sp,
                    color = Color.Primary300,
                )
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(32.dp)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = prop.accessory.res),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = prop.accessory.title,
                fontWeight = FontWeight.W600,
                fontSize = 12.sp,
                color = Color(0xFF4B4B4B),
            )
        }
    }
}

val previewShopItemItemPropList = listOf(
    ShopItemItemProp(
        accessory = Accessory.TTUTTU_PERL_NECKLACE,
        cost = 400,
        onClicked = {}
    ),
    ShopItemItemProp(
        accessory = Accessory.TTOTTO_COZY_MUFFLER,
        cost = 600,
        onClicked = {}
    ),
    ShopItemItemProp(
        accessory = Accessory.TTOTTO_CAP,
        cost = 300,
        onClicked = {}
    ),
    ShopItemItemProp(
        accessory = Accessory.TTUTTU_BAG,
        cost = 200,
        onClicked = {}
    ),
    ShopItemItemProp(
        accessory = Accessory.TTUTTU_THREE_COLOR_BALLOONS,
        cost = 300,
        onClicked = {}
    )
)

@Preview
@Composable
fun PreviewShopScreen() {
    ShopScreen(
        point = 1234,
        equippedAccessorySet = previewAccessorySet,
        shopItemItemPropList = previewShopItemItemPropList,
        purchaseDialogProp = null,
        onMyItemsIconClicked = {}
    )
}
