package com.umc.challenge.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.challenge.R
import com.umc.challenge.component.ShadowBoxScope
import com.umc.design.Grey300
import com.umc.design.Primary200
import com.umc.design.Primary300
import com.umc.design.Primary500
import com.umc.design.Secondary100

data class NewChallengeViewProp(
    val maxTitleLength: Int,
    val title: String,
    val description: String,
    val failedChallengeItemPropList: List<FailedChallengeItemProp>,
    val onTitleChanged: (String) -> Unit,
    val onDescriptionChanged: (String) -> Unit,
    val onCreateButtonClicked: () -> Unit,
)

data class FailedChallengeItemProp(
    val elapsedDays: Int,
    val title: String,
    val description: String,
    val onClick: () -> Unit,
)

private val ttottoHeight = 108.dp
private val ttottoCardHeight = 84.dp
private val failedChallengeCardSize = DpSize(128.dp, 156.dp)

@Composable
fun NewChallengeView(
    prop: NewChallengeViewProp
) {
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Secondary100)
    ) {
        Column {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                // 또또 카드
                Box(
                    contentAlignment = Alignment.BottomStart
                ) {
                    var contentOffset by remember { mutableStateOf(0.dp) }

                    Box(
                        modifier = Modifier
                            .height(ttottoCardHeight)
                            .background(
                                color = Color(0xFFFFDACB),
                                shape = RoundedCornerShape(32.dp)
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                                .drawWithContent {
                                    drawContent()
                                    drawOutline(
                                        outline = RoundedCornerShape(32.dp - 8.dp)
                                            .createOutline(size, layoutDirection, density = this),
                                        style = Stroke(
                                            cap = StrokeCap.Round,
                                            width = 2.dp.toPx(),
                                            pathEffect = PathEffect.dashPathEffect(
                                                intervals = floatArrayOf(4.dp.toPx(), 6.dp.toPx())
                                            )
                                        ),
                                        brush = SolidColor(value = Color.Secondary100)
                                    )
                                }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        start = contentOffset + 4.dp,
                                        end = 32.dp
                                    )
                            ) {
                                Text(
                                    text = stringResource(id = R.string.ttotto_card_content),
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.W600,
                                        color = Color.Primary300,
                                        lineBreak = LineBreak.Heading,
                                        textAlign = TextAlign.Center,
                                    )
                                )
                            }
                        }
                    }
                    Image(
                        painter = painterResource(id = R.drawable.img_masked_ttotto),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .height(ttottoHeight)
                            .onGloballyPositioned {
                                contentOffset = with(density) {
                                    it.positionInParent().x.toDp() + it.size.width.toDp()
                                }
                            }
                    )
                }
                // 입력란
                Column(
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.title),
                            color = Color.Primary300,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W600,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                        Box(
                            modifier = Modifier
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(percent = 50),
                                    spotColor = Color.Primary500
                                )
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(percent = 50)
                                )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 16.dp)
                            ) {
                                BasicTextField(
                                    value = prop.title,
                                    onValueChange = prop.onTitleChanged,
                                    textStyle = TextStyle(
                                        fontSize = 12.sp,
                                        color = Color.Black,
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    if (prop.title.isBlank()) Text(
                                        text = stringResource(id = R.string.title_field_placeholder),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.W400,
                                        color = Color.Grey300
                                    )
                                }
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                color = Color.Black,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.W400,
                                            )
                                        ) {
                                            append(prop.title.length.toString())
                                        }
                                        withStyle(
                                            style = SpanStyle(
                                                color = Color.Grey300,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.W400,
                                            )
                                        ) {
                                            append("/${prop.maxTitleLength}")
                                        }
                                    }
                                )
                            }
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.description),
                            color = Color.Primary300,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W600,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                        Box(
                            modifier = Modifier
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(percent = 50),
                                    spotColor = Color.Primary500
                                )
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(percent = 50)
                                )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 16.dp)
                            ) {
                                BasicTextField(
                                    value = prop.description,
                                    onValueChange = prop.onDescriptionChanged,
                                    textStyle = TextStyle(
                                        fontSize = 12.sp,
                                        color = Color.Black,
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (prop.description.isBlank()) Text(
                                        text = stringResource(id = R.string.description_field_placeholder),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.W400,
                                        color = Color.Grey300
                                    )
                                }
                            }
                        }
                    }
                }
                // 생성 버튼
                ElevatedButton(
                    onClick = prop.onCreateButtonClicked,
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 4.dp
                    ),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = Color.Primary200,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.padding(vertical = 32.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .width(192.dp)
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.create),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W600
                        )
                    }
                }
            }
            // 실패한 챌린지
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item { Spacer(modifier = Modifier) }
                items(count = prop.failedChallengeItemPropList.size) { index ->
                    FailedChallengeItem(prop = prop.failedChallengeItemPropList[index])
                }
                item { Spacer(modifier = Modifier) }
            }
        }
    }
}

@Composable
private fun FailedChallengeItem(
    prop: FailedChallengeItemProp
) {
    ShadowBoxScope(
        radius = 4.dp,
        color = Color.Black.copy(alpha = 0.2f),
        offset = DpOffset(0.dp, 4.dp),
    ) {
        Box(
            modifier = Modifier.size(failedChallengeCardSize)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_failed_challenge_card),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }),
                modifier = Modifier.fillMaxSize()
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 32.dp)
            ) {
                Spacer(modifier = Modifier)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = prop.elapsedDays.toString() + stringResource(id = R.string.days_ago),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.W400,
                        color = Color(0xFF4B4B4B),
                    )
                    Text(
                        text = prop.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W600,
                        color = Color(0xFF4B4B4B),
                        )
                }
                Text(
                    text = prop.description,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.W400,
                    color = Color(0xFF4B4B4B),
                )
            }
        }
    }
}

val previewNewChallengeViewProp = NewChallengeViewProp(
    maxTitleLength = 20,
    title = "",
    description = "",
    failedChallengeItemPropList = listOf(
        FailedChallengeItemProp(
            elapsedDays = 2,
            title = "도서관 가기",
            description = "도서 대여 및 시험공부",
            onClick = {}
        ),
        FailedChallengeItemProp(
            elapsedDays = 5,
            title = "한강 러닝하기",
            description = "1시간 러닝뛰기",
            onClick = {}
        ),
        FailedChallengeItemProp(
            elapsedDays = 5,
            title = "택시 안 타기",
            description = "택시 타는 습관 고치기",
            onClick = {}
        )
    ),
    onTitleChanged = {},
    onDescriptionChanged = {},
    onCreateButtonClicked = {}
)

@Preview
@Composable
fun PreviewNewChallengeView() {
    NewChallengeView(
        prop = previewNewChallengeViewProp
    )
}