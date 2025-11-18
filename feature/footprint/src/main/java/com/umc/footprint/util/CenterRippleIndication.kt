package com.umc.footprint.util

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

object CenterRippleIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return CenterRippleNode(interactionSource)
    }

    override fun hashCode(): Int = -1
    override fun equals(other: Any?) = other === this
}

private class CenterRippleNode(
    private val interactionSource: InteractionSource
) : Modifier.Node(),
    DrawModifierNode {

    private val animatedProgress = Animatable(0f)
    private val rippleColor = Animatable(Color.Black.copy(alpha = 0.0f))

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collectLatest { interaction ->
                if (interaction is PressInteraction.Press) {
                    launch {
                        animatedProgress.snapTo(0f)
                        rippleColor.snapTo(Color.Black.copy(alpha = 0.2f))

                        animatedProgress.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 200, easing = EaseOut)
                        )

                        rippleColor.animateTo(
                            targetValue = Color.Black.copy(alpha = 0.0f),
                            animationSpec = tween(durationMillis = 200, easing = EaseOutExpo)
                        )

                        animatedProgress.snapTo(0f)
                    }
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()

        if (animatedProgress.value > 0f) {
            val maxRadius = size.minDimension / 3

            drawCircle(
                color = rippleColor.value,
                radius = maxRadius * animatedProgress.value,
                center = center
            )
        }
    }
}