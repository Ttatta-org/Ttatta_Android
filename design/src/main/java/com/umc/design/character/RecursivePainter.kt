package com.umc.design.character

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.painter.Painter
import kotlin.math.min

class RecursivePainter private constructor(
    private val root: RenderInfo,
) : Painter() {

    companion object {
        fun create(root: RenderInfo) = RecursivePainter(root)
    }

    override val intrinsicSize = root.originalSize
    override fun DrawScope.onDraw() {
        val scaleFactor = min(
            size.width / root.originalSize.width,
            size.height / root.originalSize.height,
        )

        val horizontalPadding = (size.width - root.originalSize.width * scaleFactor) / 2
        val verticalPadding = (size.height - root.originalSize.height * scaleFactor) / 2

        drawIntoCanvas { canvas ->
            with(root) {
                canvas.save()
                canvas.translate(horizontalPadding, verticalPadding)
                render(
                    canvas = canvas,
                    offset = Offset.Zero,
                    scaleFactor = scaleFactor,
                )
                canvas.restore()
            }
        }
    }
}

interface RenderInfo {
    val originalSize: Size
    val painter: Painter? get() = null
    val children: List<ChildRenderInfo> get() = listOf()  // 인덱스 순서 중요

    fun DrawScope.render(
        canvas: Canvas,
        offset: Offset,
        scaleFactor: Float,
    ) {
        painter?.let { painter ->
            canvas.save()
            canvas.translate(offset.x, offset.y)
            with(painter) { draw(size = originalSize * scaleFactor) }
            canvas.restore()
        }
        children.forEach { child ->
            with(child.info) {
                render(
                    canvas = canvas,
                    offset = offset + child.offset * scaleFactor,
                    scaleFactor = scaleFactor
                )
            }
        }
    }
}

data class ChildRenderInfo(
    val offset: Offset,
    val info: RenderInfo
)

