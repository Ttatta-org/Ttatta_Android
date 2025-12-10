package com.umc.record.component

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.umc.design.Secondary100
import com.umc.design.theme.ThemeProvider
import com.umc.record.R

data class TopBarProp(
    val searchWord: String,
    val onSearchWordChanged: (String) -> Unit,
    val onHeightChanged: (Dp) -> Unit,
    val onSearchButtonClicked: () -> Unit,
    val isSearchMode: Boolean,
    val searchPanelHeight: Dp,
    val onSearchModeChanged: (Boolean) -> Unit = {},
    val panelContent: @Composable () -> Unit = {},
)

private const val topBarResourceWidthRatio = 390f
private const val topBarResourceCroppedHeightRatio = 87f

@Composable
fun TopBar(
    prop: TopBarProp
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    var screenWidth by remember { mutableStateOf(0.dp) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { with(density) { prop.onHeightChanged(it.size.height.toDp()) } }
            .onGloballyPositioned { with(density) { screenWidth = it.size.width.toDp() } }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White.copy(alpha = 0.7f))
        ) {
            Spacer(modifier = Modifier.height(statusBarHeight))
            Row(
                horizontalArrangement = Arrangement.spacedBy(13.4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 38.dp, end = 20.dp, bottom = 5.dp)
            ) {
                Box(
                    modifier = Modifier.padding(start = 30.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo),
                        contentScale = ContentScale.Fit,
                        contentDescription = null,
                        modifier = Modifier.size(34.dp)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
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
                            value = prop.searchWord,
                            onValueChange = {
                                prop.onSearchWordChanged(it)
                                if (!prop.isSearchMode) prop.onSearchModeChanged(true)
                            },
                            textStyle = TextStyle(
                                fontSize = 13.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier
                                .padding(start = 15.dp, end = 32.dp, top = 5.dp, bottom = 5.dp)
                                .onFocusChanged { prop.onSearchModeChanged(it.isFocused) }
                        ) { innerTextField ->
                            if (prop.searchWord.isEmpty()) Text(
                                text = stringResource(id = R.string.search_placeholder), // 힌트 텍스트
                                color = Color(0xFF8E8E8E),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            innerTextField()
                        }


                        // 삭제 아이콘 (텍스트가 있을 때만 표시)
                        if (prop.searchWord.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    prop.onSearchWordChanged("")
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
                        onClick = prop.onSearchButtonClicked,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = "Search",
                            tint = Color.Unspecified,
                        )
                    }
                }
            }

            // 확장 영역 (= 검색 결과 들어갈 빈 영역)
            if (prop.searchPanelHeight > 0.dp) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(prop.searchPanelHeight)
                        .padding(horizontal = 24.dp) // 디자인 여백(원하면 조절)
                ) {
                    prop.panelContent()  // 여기서 결과를 렌더
                }
            }
        }
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data("android.resource://${context.packageName}/${R.raw.img_top_bar_record}")
                    .decoderFactory(SvgDecoder.Factory()).build(),
                // 프리뷰를 위한 이미지
                error = BitmapPainter(
                    image = BitmapFactory.decodeResource(
                        context.resources, R.raw.img_top_bar_record_for_preview
                    ).asImageBitmap(),
                )
            ),
            contentScale = ContentScale.FillWidth,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .clipToBounds()
                .offset {
                    IntOffset(
                        x = 0,
                        y = -screenWidth
                            .times(topBarResourceCroppedHeightRatio)
                            .div(topBarResourceWidthRatio)
                            .roundToPx(),
                    )
                }
        )
    }
}

val previewTopBarProp = TopBarProp(
    searchWord = "",
    onHeightChanged = {},
    onSearchWordChanged = {},
    onSearchButtonClicked = {},
    isSearchMode = true,
    searchPanelHeight = 185.dp, // 프리뷰에서 펼친 상태 확인
    onSearchModeChanged = {}
)

@Preview
@Composable
fun PreviewTopBar() {
    ThemeProvider {
        TopBar(prop = previewTopBarProp)
    }
}