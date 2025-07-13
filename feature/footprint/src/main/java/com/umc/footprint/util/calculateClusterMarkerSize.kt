package com.umc.footprint.util

import androidx.compose.ui.geometry.Size
import com.umc.footprint.core.DesignConstant

fun calculateClusteredMarkerSize(density: Float, count: Int): Size {
    return DesignConstant.ClusterMarkerMaxSize.let { maxSize ->
        listOf(maxSize.width, maxSize.height).map { it.value * density * (1 - 1f / (count + 1)) }
    }.let { (width, height) ->
        Size(width, height)
    }
}