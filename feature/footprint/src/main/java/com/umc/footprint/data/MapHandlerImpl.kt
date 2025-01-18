package com.umc.footprint.data

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.umc.footprint.core.MapHandler
import com.umc.footprint.core.MapMarker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class MapHandlerImpl @Inject constructor(
    @ApplicationContext val context: Context
): MapHandler {

    private val mapView: MapView
    private val mapFlow = MutableStateFlow<NaverMap?>(null)
    private val markers = mutableMapOf<MapMarker, Marker>()
    private val clickedMarkerFlow = MutableStateFlow<MapMarker?>(null)
    private var onPreviousMarkerDismissed: (() -> Unit)? = null

    private suspend fun getMap(): NaverMap {
        return mapFlow.first { map -> map != null }!!
    }

    init {
        mapView = MapView(context).apply {
            id = View.generateViewId()
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            getMapAsync {
                it.addOnCameraChangeListener { _, _ -> clickedMarkerFlow.value = null }
                it.setOnMapClickListener { _, _ -> clickedMarkerFlow.value = null }
                mapFlow.value = it
            }
            onCreate(Bundle())
        }

        CoroutineScope(Dispatchers.Main).launch {
            launch {
                clickedMarkerFlow.collect { marker ->
                    onPreviousMarkerDismissed?.invoke()
                    val onDismissed = marker?.let {
                        val naverMarker = markers[marker]!!
                        val position = getMap().projection.toScreenLocation(naverMarker.position)
                        return@let it.onClicked?.invoke(position.x, position.y)
                    }
                    onPreviousMarkerDismissed = onDismissed
                    if (onDismissed == null) clickedMarkerFlow.value = null
                }
            }
        }
    }

    override fun getMapView(): @Composable () -> Unit {
        return {
            AndroidView(
                factory = { mapView },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    override suspend fun moveTo(latitude: Double, longitude: Double) {
        val target = LatLng(latitude, longitude)
        val cameraUpdate = CameraUpdate.scrollTo(target)
        getMap().moveCamera(cameraUpdate)
    }

    override suspend fun getViewingPosition(): Pair<Double, Double> {
        val position = getMap().cameraPosition.target
        return position.latitude to position.longitude
    }

    override suspend fun addMarker(marker: MapMarker) {
        markers[marker] = Marker().apply {
            setOnClickListener {
                clickedMarkerFlow.value = marker
                true
            }
            position = LatLng(marker.latitude, marker.longitude)
            this@apply.map = this@MapHandlerImpl.getMap()
        }
    }

    override suspend fun removeMarker(marker: MapMarker) {
        markers[marker]?.map = null
        markers.remove(marker)
        if (marker == clickedMarkerFlow.value) clickedMarkerFlow.value = null
    }

    override suspend fun getAllMarkers(): List<MapMarker> {
        return markers.keys.toList()
    }

    override suspend fun removeAllMarkers() {
        markers.values.forEach { it.map = null }
        markers.clear()
        clickedMarkerFlow.value = null
    }
}