package com.umc.footprint.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.umc.design.CategoryColor

val locatorWidth = 48.dp
val locatorHeight = 48.dp
val clusteredMarkerMaxWidth = 128.dp
val clusteredMarkerMaxHeight = 128.dp
val markerWidth = 64.dp
val markerHeight = 64.dp
val clusteringDp = 32.dp

data class MapMarker(
    val latitude: Double,
    val longitude: Double,
    val zIndex: Int,
    val color: CategoryColor? = null,
    val onClicked: ((Float, Float) -> (() -> Unit)?)? = null,
)

interface MapHandler {
    @Composable fun MapView(
        isBlurApplied: Boolean,
        isLocationMarkingEnabled: Boolean,
    )

    suspend fun moveTo(latitude: Double, longitude: Double)
    suspend fun moveToCurrentPosition()
    suspend fun getViewingPosition(): Pair<Double, Double>?
    suspend fun addMarker(marker: MapMarker)
    suspend fun removeMarker(marker: MapMarker)
    suspend fun getAllMarkers(): List<MapMarker>
    suspend fun removeAllMarkers()

    suspend fun addOnDismissListener(listener: () -> Unit)
    suspend fun dismissMarkerEvent()
}