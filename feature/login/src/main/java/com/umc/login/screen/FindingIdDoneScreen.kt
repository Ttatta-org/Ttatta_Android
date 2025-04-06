package com.umc.login.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.umc.design.Grey500
import com.umc.design.Primary200
import com.umc.design.Primary400
import com.umc.design.Secondary100
import com.umc.design.Secondary200
import com.umc.design.character.Accessory
import com.umc.design.character.AccessorySet
import com.umc.design.character.CharacterType
import com.umc.design.character.CharacterView
import com.umc.design.theme.ThemeProvider
import com.umc.login.R

private val cardShape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
private val textBalloonTailSize = DpSize(49.dp, 61.dp)

@Composable
fun FindingIdDoneScreen(
    id: String,
    name: String,
    onBackToLoginButtonClicked: () -> Unit,
    onGoToFindPasswordButtonClicked: () -> Unit,
) {
    val density = LocalDensity.current

    var textRenderEvent by remember { mutableStateOf<RenderEvent?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding())
    ) {
        // 카드
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 4.dp,
                    shape = cardShape,
                    spotColor = Color(0x80000000),
                    ambientColor = Color(0x80000000)
                )
                .widthIn(max = 480.dp)
                .weight(1f, fill = false),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.Secondary100,
                        shape = cardShape,
                    )
                    .heightIn(600.dp)
                    .clip(shape = cardShape),
            ) {
                textRenderEvent?.let { (textTopLeft, textSize) ->
                    val centerX = textTopLeft.x + textSize.width * 0.5f

                    // 캐릭터
                    Box(
                        modifier = Modifier.offset {
                            Offset(
                                x = centerX - 300.dp.toPx(),
                                y = 300.dp.toPx(),
                            ).round()
                        },
                    ) {
                        Box(
                            modifier = Modifier.rotate(15f),
                        ) {
                            CharacterView(
                                accessorySet = remember {
                                    AccessorySet.create(
                                        Accessory.TTUTTU_BAG,
                                        Accessory.TTUTTU_HAT,
                                    )
                                },
                                characterType = CharacterType.TTUTTU,
                                width = 350.dp,
                            )
                        }
                    }
                    Box(
                        modifier = Modifier.offset {
                            Offset(
                                x = centerX + 50.dp.toPx(),
                                y = 350.dp.toPx(),
                            ).round()
                        },
                    ) {
                        Box(
                            modifier = Modifier.rotate(-30f),
                        ) {
                            CharacterView(
                                accessorySet = remember {
                                    AccessorySet.create(
                                        Accessory.TTOTTO_BAG,
                                        Accessory.TTOTTO_HAT,
                                    )
                                },
                                characterType = CharacterType.TTOTTO,
                                width = 270.dp,
                            )
                        }
                    }
                    // 말풍선
                    Canvas(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        drawCircle(
                            color = Color.Secondary200,
                            center = textTopLeft + Offset(x = textSize.width * 0.5f, y = 0f),
                            radius = textSize.height + 64.dp.toPx()
                        )
                    }
                    Box(
                        modifier = Modifier.offset {
                            textTopLeft.plus(
                                other = Offset(
                                    x = textSize.width / 2 + 16.dp.toPx(),
                                    y = textSize.height + 32.dp.toPx(),
                                )
                            ).round()
                        },
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_text_balloon_tail),
                            contentScale = ContentScale.Fit,
                            contentDescription = null,
                            modifier = Modifier.size(textBalloonTailSize)
                        )
                    }
                }
                // 텍스트
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    modifier = Modifier
                        .padding(
                            top = WindowInsets.systemBars.asPaddingValues()
                                .calculateTopPadding() + 32.dp,
                            bottom = 32.dp,
                        )
                        .fillMaxWidth()
                        .onGloballyPositioned {
                            textRenderEvent = RenderEvent(
                                topLeft = it.positionInParent(),
                                size = it.size.toSize(),
                            )
                        },
                ) {
                    Text(
                        text = stringResource(id = R.string.find_id),
                        color = Color.Primary400,
                        fontWeight = FontWeight.W600,
                        fontSize = 16.sp,
                    )
                    Text(
                        text = stringResource(id = R.string.find_id_done),
                        color = Color.Primary400,
                        fontWeight = FontWeight.W800,
                        fontSize = 24.sp,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                    ) {
                        @Composable
                        fun cellText(text: String) {
                            Box(
                                contentAlignment = Alignment.CenterStart,
                                modifier = Modifier.height(with(density) { 24.sp.toDp() }),
                            ) {
                                Text(
                                    text = text,
                                    color = Color.Grey500,
                                    fontWeight = FontWeight.W600,
                                    fontSize = 16.sp,
                                )
                            }
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            cellText(text = stringResource(id = R.string.subscriber))
                            cellText(text = stringResource(id = R.string.id))
                        }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            cellText(text = name)
                            cellText(text = id)
                        }
                    }
                }
            }
        }
        // 버튼
        Column(
            verticalArrangement = Arrangement.spacedBy(
                space = 11.dp,
                alignment = Alignment.Bottom,
            ),
            modifier = Modifier
                .widthIn(max = 480.dp)
                .padding(32.dp),
        ) {
            ElevatedButton(
                onClick = onGoToFindPasswordButtonClicked,
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(
                    color = Color.Primary200,
                    width = 1.dp,
                ),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Primary200,
                    containerColor = Color.White,
                ),
                contentPadding = PaddingValues(13.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.find_password),
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.W600,
                )
            }
            ElevatedButton(
                onClick = onBackToLoginButtonClicked,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White, containerColor = Color.Primary200
                ),
                contentPadding = PaddingValues(13.dp),
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
fun PreviewFindingIdDoneScreen() {
    ThemeProvider {
        FindingIdDoneScreen(
            id = "ddadda1225",
            name = "이서연",
            onBackToLoginButtonClicked = {},
            onGoToFindPasswordButtonClicked = {},
        )
    }
}