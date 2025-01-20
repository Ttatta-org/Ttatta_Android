package com.umc.footprint.data

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.location.FusedLocationProviderClient
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationSource
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.umc.footprint.core.LocationHandler
import com.umc.footprint.core.MapHandler
import com.umc.footprint.core.MapMarker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class MapHandlerImpl @Inject constructor(
    private val context: Context,
    private val locationHandler: LocationHandler,
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
        // 네이버 SDK를 사용한 지도 초기 설정
        mapView = MapView(context).apply {
            id = View.generateViewId()
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            getMapAsync { naverMap ->
                naverMap.addOnCameraChangeListener { _, _ -> clickedMarkerFlow.value = null }
                naverMap.setOnMapClickListener { _, _ -> clickedMarkerFlow.value = null }
                naverMap.uiSettings.apply {
                    isCompassEnabled = false
                    isScaleBarEnabled = false
                    isZoomControlEnabled = false
                    isIndoorLevelPickerEnabled = false
                    isLocationButtonEnabled = false
                }
                naverMap.isIndoorEnabled = false

                naverMap.locationSource = object: LocationSource {
                    private var locationChangeListener: (Location) -> Unit = {}

                    override fun activate(listener: LocationSource.OnLocationChangedListener) {
                        locationChangeListener = { listener.onLocationChanged(it) }
                        locationHandler.addLocationChangeListener(locationChangeListener)
                    }

                    override fun deactivate() {
                        locationHandler.removeLocationChangeListener(locationChangeListener)
                    }
                }
                naverMap.locationTrackingMode = LocationTrackingMode.NoFollow

                mapFlow.value = naverMap
            }
            onCreate(Bundle())
        }

        // 마커의 클릭 이벤트를 다루기 위한 설정
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
        val cameraUpdate = CameraUpdate.scrollTo(target).apply {
            animate(CameraAnimation.Easing, 500L)
        }
        getMap().moveCamera(cameraUpdate)
    }

    override suspend fun moveToCurrentPosition() {
        val location = locationHandler.getCurrentLocation()
        moveTo(location.latitude, location.longitude)
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
            marker.icon?.let { icon = OverlayImage.fromResource(it) }
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