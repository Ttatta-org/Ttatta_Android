package com.umc.login.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.umc.design.Grey500
import com.umc.design.Primary200
import com.umc.design.Primary400
import com.umc.design.Secondary100
import com.umc.design.Secondary300
import com.umc.design.character.Accessory
import com.umc.design.character.AccessorySet
import com.umc.design.character.CharacterView
import com.umc.design.theme.ThemeProvider
import com.umc.login.R

@Composable
fun JoinDoneScreen(
    name: String,
    onBackToLoginButtonClicked: () -> Unit,
) {
    val density = LocalDensity.current

    var screenRenderEvent by remember { mutableStateOf<RenderEvent?>(null) }
    var layoutRenderEvent by remember { mutableStateOf<RenderEvent?>(null) }
    var characterRenderEvent by remember { mutableStateOf<RenderEvent?>(null) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Secondary100)
            .onGloballyPositioned {
                screenRenderEvent = RenderEvent(
                    topLeft = it.positionInParent(),
                    size = it.size.toSize(),
                )
            },
    ) {
        // 뒷배경
        layoutRenderEvent?.let { (layoutTopLeft, _) ->
            characterRenderEvent?.let { (characterTopLeft, characterSize) ->
                val characterCenter = layoutTopLeft + characterTopLeft + characterSize.center

                Canvas(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    // 블러 원
                    fun DrawScope.drawBlurredCircle(
                        radius: Float,
                        centerOffsetByCharacterViewCenter: Offset,
                    ) {
                        val center = characterCenter + centerOffsetByCharacterViewCenter

                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color.Secondary300, Color.Transparent),
                                center = center,
                                radius = radius,
                            ),
                            radius = radius,
                            center = center,
                        )
                    }

                    drawBlurredCircle(
                        radius = characterSize.width * 0.7f,
                        centerOffsetByCharacterViewCenter = Offset(
                            x = characterSize.width * 0.4f,
                            y = -characterSize.height,
                        ),
                    )

                    drawBlurredCircle(
                        radius = characterSize.width * 0.5f,
                        centerOffsetByCharacterViewCenter = Offset(
                            x = -characterSize.width * 0.4f,
                            y = characterSize.height * 0.5f,
                        ),
                    )
                }

                // 꽃잎
                @Composable
                fun drawCloud(
                    @DrawableRes resId: Int,
                    offsetFromCharacterViewCenter: Offset,
                    scaleFactorByCharacterView: Float,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset { characterCenter.plus(offsetFromCharacterViewCenter).round() },
                    ) {
                        Image(
                            painter = painterResource(id = resId),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(
                                size = with(density) {
                                    characterSize.times(scaleFactorByCharacterView).toDpSize()
                                },
                            )
                        )
                    }
                }

                drawCloud(
                    resId = R.drawable.img_cloud_join,
                    offsetFromCharacterViewCenter = Offset(
                        x = -characterSize.width * 0.5f,
                        y = -characterSize.height * 0.7f,
                    ),
                    scaleFactorByCharacterView = 0.2f,
                )

                drawCloud(
                    resId = R.drawable.img_cloud_join2,
                    offsetFromCharacterViewCenter = Offset(
                        x = characterSize.width * 0.35f,
                        y = characterSize.height * 0.5f,
                    ),
                    scaleFactorByCharacterView = 0.2f,
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(64.dp),
            modifier = Modifier
                .widthIn(max = 480.dp)
                .padding(32.dp)
                .onGloballyPositioned {
                    layoutRenderEvent = RenderEvent(
                        topLeft = it.positionInParent(),
                        size = it.size.toSize(),
                    )
                },
        ) {
            // 텍스트
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "${stringResource(id = R.string.welcome)} ${name}${stringResource(id = R.string.welcome_suffix)}",
                    fontWeight = FontWeight.W800,
                    fontSize = 28.sp,
                    color = Color.Grey500,
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.W600,
                                fontSize = 16.sp,
                                color = Color.Primary400,
                            )
                        ) {
                            appendLine(stringResource(id = R.string.welcome_line1))
                            append(stringResource(id = R.string.welcome_line2))
                        }
                    },
                    textAlign = TextAlign.Center,
                )
            }
            // 캐릭터
            screenRenderEvent?.let { (_, screenSize) ->
                layoutRenderEvent?.let { (_, layoutSize) ->
                    Box(
                        modifier = Modifier.onGloballyPositioned {
                            characterRenderEvent = RenderEvent(
                                topLeft = it.positionInParent(),
                                size = it.size.toSize(),
                            )
                        },
                    ) {
                        CharacterView(
                            accessorySet = remember {
                                AccessorySet.create(
                                    Accessory.TTOTTO_BAG,
                                    Accessory.TTOTTO_HAT,
                                    Accessory.TTUTTU_BAG,
                                    Accessory.TTUTTU_HAT
                                )
                            },
                            width = with(density) { layoutSize.width.toDp() },
                            height = with(density) { screenSize.height.toDp() * 0.3f },
                        )
                    }
                }
            }
            // 버튼
            ElevatedButton(
                onClick = onBackToLoginButtonClicked,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color.Primary200,
                ),
            ) {
                Text(
                    text = stringResource(id = R.string.go_to_login),
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.W600,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JoinDoneScreenPreview() {
    ThemeProvider {
        JoinDoneScreen(
            name = "서연",
            onBackToLoginButtonClicked = {},
        )
    }
}