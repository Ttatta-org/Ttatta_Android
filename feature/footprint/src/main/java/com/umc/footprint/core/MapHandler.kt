package com.umc.footprint.core

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset

interface MapHandler {
    @Composable
    fun MapView(
        isBlurApplied: Boolean,
        isLocationPermissionGranted: Boolean,
    )

    suspend fun moveTo(
        latitude: Double,
        longitude: Double,
        offset: Offset = Offset.Zero,
        zoom: Boolean = false,
        animationTime: Long = 500,
        onAnimationEnded: () -> Unit = {}
    )

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    suspend fun moveToCurrentPosition()

    suspend fun getViewingPosition(): Pair<Double, Double>?
    suspend fun addMarkers(vararg markers: MapMarker)
    suspend fun removeAllMarkers()

    suspend fun addOnDismissListener(listener: () -> Unit)
    suspend fun dismissMarkerEvent()
}
