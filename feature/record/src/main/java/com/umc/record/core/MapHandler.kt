package com.umc.record.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

val locatorWidth = 48.dp
val locatorHeight = 48.dp

interface MapHandler {
    @Composable fun MapView(isLocationMarkingEnabled: Boolean)

    suspend fun getCurrentPinnedCoordination(): Pair<Double, Double>  // (위도, 경도)
    suspend fun movePin(latitude: Double, longitude: Double)

    suspend fun addCameraIdleListener(listener: () -> Unit)
    suspend fun removeCameraIdleListener(listener: () -> Unit)
}