package com.umc.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.component.CustomHeader
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider
import com.umc.mypage.R

@Composable
fun LockPasswordScreen(
    title: String,
    description: String,
    errorMessage: String?,
    totalCount: Int,
    fillCount: Int,
    onBackButtonClicked: (() -> Unit)?,
    onNumberClicked: (Char) -> Unit,
    onEraseButtonClicked: () -> Unit,
    onCancelButtonClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = WindowInsets.navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding()
            ),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        CustomHeader(
            showLogo = false,
            backgroundColor = Color.Transparent,
            waveColor = Color.Transparent,
            onBackButtonClicked = onBackButtonClicked,
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.W700,
                color = LocalColorTheme.current.primary[400]
            )
            Spacer(modifier = Modifier.height(7.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                color = LocalColorTheme.current.primary[200]
            )
            Spacer(modifier = Modifier.height(17.7.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(totalCount) { index ->
                    Box {
                        val resource =
                            if (index < fillCount) R.drawable.ic_dot_filled else R.drawable.ic_dot_empty

                        Icon(
                            painter = painterResource(resource),
                            contentDescription = null,
                            tint = Color.Black.copy(alpha = 0.1f),
                            modifier = Modifier
                                .size(43.dp)
                                .blur(radius = 4.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                                .offset(y = 2.dp),
                        )
                        Image(
                            painter = painterResource(resource),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(43.dp)
                        )
                    }
                }
            }
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_warning_red),
                        contentDescription = "경고",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = errorMessage,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W400,
                        color = LocalColorTheme.current.negative,
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 45.dp),
        ) {
            val keys: List<List<Pair<@Composable () -> Unit, () -> Unit>>> = run {
                val numberKeys = (0..9)
                    .map { it.digitToChar() }
                    .map { number ->
                        val key = @Composable {
                            Text(
                                text = number.toString(),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.W400,
                                color = LocalColorTheme.current.primary[200],
                            )
                        }

                        key to { onNumberClicked(number) }
                    }

                val cancelKey = @Composable {
                    Text(
                        text = "취소",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W400,
                        color = LocalColorTheme.current.primary[200],
                    )
                } to onCancelButtonClicked

                val eraseKey = @Composable {
                    Image(
                        painter = painterResource(R.drawable.ic_numberpad_back),
                        contentDescription = "지우기",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.width(24.dp),
                    )
                } to onEraseButtonClicked

                numberKeys.slice(1..9) + cancelKey + numberKeys[0] + eraseKey
            }.chunked(3)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .widthIn(max = 360.dp)
                    .heightIn(max = 300.dp)
            ) {
                keys.forEach { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row.forEach { (key, onClick) ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable(onClick = onClick)
                            ) {
                                key.invoke()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLockPasswordScreen() {
    ThemeProvider {
        LockPasswordScreen(
            title = "암호 변경이란다",
            description = "암호를 입력해라",
            errorMessage = "암호가 안 맞는다",
            totalCount = 5,
            fillCount = 4,
            onBackButtonClicked = null,
            onNumberClicked = {},
            onEraseButtonClicked = {},
            onCancelButtonClicked = {},
        )
    }
}