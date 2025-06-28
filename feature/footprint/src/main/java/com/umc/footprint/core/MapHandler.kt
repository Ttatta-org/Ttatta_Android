package com.umc.footprint.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset

interface MapHandler {
    @Composable
    fun MapView(
        isBlurApplied: Boolean,
        isLocationMarkingEnabled: Boolean,
    )

    suspend fun moveTo(
        latitude: Double,
        longitude: Double,
        offset: Offset = Offset.Zero,
        animationTime: Long = 500,
        onAnimationEnded: () -> Unit = {}
    )

    suspend fun moveToCurrentPosition()
    suspend fun getViewingPosition(): Pair<Double, Double>?
    suspend fun addMarker(marker: MapMarker)
    suspend fun removeAllMarkers()

    suspend fun addOnDismissListener(listener: () -> Unit)
    suspend fun dismissMarkerEvent()
}
