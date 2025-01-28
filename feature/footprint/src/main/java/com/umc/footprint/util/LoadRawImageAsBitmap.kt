package com.umc.footprint.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.unit.Dp

fun loadRawImageAsBitmap(
    context: Context,
    rawResourceId: Int,
    width: Dp,
    height: Dp,
): Bitmap {
    val inputStream = context.resources.openRawResource(rawResourceId)
    val originalBitmap = BitmapFactory.decodeStream(inputStream)

    val density = context.resources.displayMetrics.density
    val targetWidthPx = (width.value * density).toInt()
    val targetHeightPx = (height.value * density).toInt()

    val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidthPx, targetHeightPx, true)
    return resizedBitmap
}
