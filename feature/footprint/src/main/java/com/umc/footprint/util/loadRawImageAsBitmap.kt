package com.umc.footprint.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.unit.DpSize
import androidx.core.graphics.scale

fun loadRawImageAsBitmap(
    context: Context,
    rawResourceId: Int,
    size: DpSize,
): Bitmap {
    val inputStream = context.resources.openRawResource(rawResourceId)
    val originalBitmap = BitmapFactory.decodeStream(inputStream)

    val density = context.resources.displayMetrics.density
    val targetWidthPx = (size.width.value * density).toInt()
    val targetHeightPx = (size.height.value * density).toInt()

    val resizedBitmap = originalBitmap.scale(targetWidthPx, targetHeightPx)
    return resizedBitmap
}
