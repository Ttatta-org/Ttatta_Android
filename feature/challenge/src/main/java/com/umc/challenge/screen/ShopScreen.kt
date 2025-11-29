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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import com.umc.challenge.R
import com.umc.challenge.component.PointChip
import com.umc.challenge.component.PointChipTheme
import com.umc.challenge.component.PurchaseDialog
import com.umc.challenge.component.PurchaseDialogProp
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
    val cost: Int,
    val isOwned: Boolean,
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
    onMyItemsIconClicked: () -> Unit,
    onBodyPartSelected: (BodyPart?) -> Unit,
    onBackButtonClicked: () -> Unit,
) {
    val density = LocalDensity.current
    val scrollState = rememberLazyGridState()

    var screenWidth: Dp? by remember { mutableStateOf(null) }
    var topBarHeight: Dp? by remember { mutableStateOf(null) }
    var characterViewWidth by remember { mutableStateOf(0.dp) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                screenWidth = with(density) { it.size.width.toDp() }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            modifier = Modifier.padding(vertical = 18.dp, horizontal = 21.98.dp)
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
            // 상단 메뉴
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp, alignment = Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(end = 16.dp)
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
            }
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
                    modifier = Modifier.padding(vertical = 16.dp)
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
                        WindowInsets.systemBars
                            .asPaddingValues()
                            .calculateBottomPadding()
                    )
                )
            }
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
                    .width(92.dp)
                    .offset {
                        with(density) {
                            Offset(
                                x = (screenWidth.toPx() - 92.dp.toPx()) / 2,
                                y = topBarHeight.toPx() - 24.dp.toPx(),
                            )
                        }.round()
                    }
            )
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

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(width)
                .clip(itemItemShape)
                .clickable { prop.onClicked() }
                .border(
                    width = 1.dp,
                    color = LocalColorTheme.current.primary[200],
                    shape = itemItemShape,
                )
                .onGloballyPositioned { with(density) { width = it.size.width.toDp() } }
        ) {
            if (prop.isOwned) Row(
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
                        text = "보유",
                        fontWeight = FontWeight.W700,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = LocalColorTheme.current.primary[500],
                    )
                }
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = prop.accessory.shopRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
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
                modifier = Modifier.padding(start = 8.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_point_icon_1),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.height(18.dp),
                )
                Text(
                    text = prop.cost.toString(),
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
        cost = 400,
        isOwned = false,
        onClicked = {}
    ),
    ShopItemItemProp(
        accessory = Accessory.TTOTTO_COZY_MUFFLER,
        cost = 600,
        isOwned = true,
        onClicked = {}
    ),
    ShopItemItemProp(
        accessory = Accessory.TTOTTO_CAP,
        cost = 300,
        isOwned = false,
        onClicked = {}
    ),
    ShopItemItemProp(
        accessory = Accessory.TTUTTU_BAG,
        cost = 200,
        isOwned = false,
        onClicked = {}
    ),
    ShopItemItemProp(
        accessory = Accessory.TTUTTU_GIANT_RIBBON,
        cost = 300,
        isOwned = false,
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
            onMyItemsIconClicked = {},
            onBodyPartSelected = {},
            onBackButtonClicked = {},
        )
    }
}
