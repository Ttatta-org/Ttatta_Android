package com.umc.record.data

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationSource
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.OverlayImage
import com.umc.record.R
import com.umc.record.core.LocationHandler
import com.umc.record.core.MapHandler
import com.umc.record.core.MapMarker
import com.umc.record.util.loadRawImageAsBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private val locatorWidth = 48.dp
private val locatorHeight = 48.dp
private val markerWidth = 64.dp

class MapHandlerImpl @Inject constructor(
    private val context: Context,
    private val locationHandler: LocationHandler,
): MapHandler {

    private val locatorImage = OverlayImage.fromBitmap(
        loadRawImageAsBitmap(
            context = context,
            rawResourceId = R.raw.ic_locator,  // 현재 사용자 위치
            width = locatorWidth,
            height = locatorHeight,
        )
    )

    private val mapView: MapView
    private val mapFlow = MutableStateFlow<NaverMap?>(null)

    private val markers = mutableMapOf<MapMarker, MarkerKey>()
    private val clickedMarkerKeyFlow = MutableStateFlow<MarkerKey?>(null)
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

            getMapAsync { map ->
                map.apply {
                    // 이벤트 리스너 설정
                    addOnCameraChangeListener { _, _ -> clickedMarkerKeyFlow.value = null }
                    setOnMapClickListener { _, _ -> clickedMarkerKeyFlow.value = null }

                    // UI 설정
                    uiSettings.apply {
                        isCompassEnabled = false
                        isScaleBarEnabled = false
                        isZoomControlEnabled = false
                        isIndoorLevelPickerEnabled = false
                        isLocationButtonEnabled = false
                    }

                    isIndoorEnabled = false
                    locationOverlay.icon = locatorImage

                    // 위치 추적 기능 설정
                    locationSource = object: LocationSource {
                        private var locationChangeListener: (Location) -> Unit = {}

                        override fun activate(listener: LocationSource.OnLocationChangedListener) {
                            locationChangeListener = { listener.onLocationChanged(it) }
                            locationHandler.addLocationChangeListener(locationChangeListener)
                        }

                        override fun deactivate() {
                            locationHandler.removeLocationChangeListener(locationChangeListener)
                        }
                    }
                    locationTrackingMode = LocationTrackingMode.NoFollow
                }

                mapFlow.value = map
            }

            onCreate(Bundle())
        }

        // 마커의 클릭 이벤트를 다루기 위한 설정(오류나서 필요하면 수정해야함)
        CoroutineScope(Dispatchers.Main).launch {
            launch {
                clickedMarkerKeyFlow.collect { key ->
                    onPreviousMarkerDismissed?.invoke()
                    val onDismissed = key?.let {
                        val naverMarker = key.naverMarker
                        val mapMarker = key.mapMarker
                        val position = getMap().projection.toScreenLocation(naverMarker?.position)
                        return@let mapMarker.onClicked?.invoke(position.x, position.y)
                    }
                    onPreviousMarkerDismissed = onDismissed
                    if (onDismissed == null) clickedMarkerKeyFlow.value = null
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
        val key = MarkerKey(marker)
        markers[marker] = key
    }

    override suspend fun getAllMarkers(): List<MapMarker> {
        return markers.keys.toList()
    }

    override suspend fun removeMarker(marker: MapMarker) {
        val key = markers[marker]!!
        markers.remove(marker)
        clickedMarkerKeyFlow.value?.let { if (key == it) clickedMarkerKeyFlow.value = null }
    }

    override suspend fun removeAllMarkers() {
        clickedMarkerKeyFlow.value = null
        markers.clear()
    }
}