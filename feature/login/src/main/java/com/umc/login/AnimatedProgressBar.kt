package com.umc.login

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedProgressBar(
    currentStep: Int,
    totalSteps: Int
) {
    // 예: 4단계 -> 각 단계 25%
    // currentStep가 1이면 0.25, 2면 0.5, 3이면 0.75, 4이면 1.0
    val stepRatios = 1f / totalSteps
    val targetProgress = stepRatios * currentStep

    // 부드럽게 변하도록 animateFloatAsState
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 650) // 0.75초
    )

    Box(
        modifier = Modifier
            .width(340.dp)
            .height(5.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)  // 여기가 애니메이션됨
                .height(5.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFF9861), Color(0xFFFDDDC1))
                    ),
                    shape = RoundedCornerShape(4.dp)
                )
        )
    }
}