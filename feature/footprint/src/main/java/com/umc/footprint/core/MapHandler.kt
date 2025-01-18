package com.umc.footprint.core

import androidx.compose.runtime.Composable

data class MapMarker(
    val latitude: Double,
    val longitude: Double,
    val title: String? = null,
    val description: String? = null,
    val onClicked: ((Float, Float) -> (() -> Unit)?)? = null,
)

interface MapHandler {
    fun getMapView(): @Composable () -> Unit

    suspend fun moveTo(latitude: Double, longitude: Double)
    suspend fun getViewingPosition(): Pair<Double, Double>?
    suspend fun addMarker(marker: MapMarker)
    suspend fun removeMarker(marker: MapMarker)
    suspend fun getAllMarkers(): List<MapMarker>
    suspend fun removeAllMarkers()
}