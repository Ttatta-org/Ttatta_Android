package com.umc.login.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umc.design.theme.LocalColorTheme

data class AnimatedProgressBarProp(
    val currentStep: Int,
    val totalSteps: Int
)

@Composable
fun AnimatedProgressBar(
    prop: AnimatedProgressBarProp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = 1f * prop.currentStep / prop.totalSteps,
        animationSpec = tween(durationMillis = 650)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = LocalColorTheme.current.grey[100],
                shape = RoundedCornerShape(percent = 50)
            )
    ) {
        Box(
            modifier = Modifier
                .height(15.dp)
                .fillMaxWidth(animatedProgress)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFFB1A5), Color(0xFFFFEFE4))
                    ),
                    shape = RoundedCornerShape(percent = 50)
                )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAnimatedProgressBar() {
    var currentStep by remember { mutableIntStateOf(2) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        AnimatedProgressBar(
            prop = AnimatedProgressBarProp(
                currentStep = currentStep,
                totalSteps = 4
            )
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { currentStep-- },
            ) {
                Text(text = "이전")
            }
            Button(
                onClick = { currentStep++ },
            ) {
                Text(text = "다음")
            }
        }
    }
}