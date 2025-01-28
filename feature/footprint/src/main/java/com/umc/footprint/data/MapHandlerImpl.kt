package com.umc.footprint.data

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
import com.naver.maps.map.clustering.Clusterer
import com.naver.maps.map.overlay.OverlayImage
import com.umc.design.CategoryColor
import com.umc.footprint.R
import com.umc.footprint.core.LocationHandler
import com.umc.footprint.core.MapHandler
import com.umc.footprint.core.MapMarker
import com.umc.footprint.util.loadRawImageAsBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

private val locatorWidth = 48.dp
private val locatorHeight = 48.dp
private val clusteredMarkerMaxWidth = 128.dp
private val clusteredMarkerMaxHeight = 128.dp
private val markerWidth = 64.dp
private val markerHeight = 64.dp
private val clusteringDp = 32.dp

private fun calculateClusteredMarkerSize(context: Context, count: Int): Pair<Int, Int> {
    val density = context.resources.displayMetrics.density
    return listOf(clusteredMarkerMaxWidth, clusteredMarkerMaxHeight).map {
        (it.value * density * (1.0 - 1.0 / (count + 1))).roundToInt()
    }.run {
        this.first() to this.last()
    }
}

class MapHandlerImpl @Inject constructor(
    private val context: Context,
    private val locationHandler: LocationHandler,
): MapHandler {

    private val locatorImage = OverlayImage.fromBitmap(
        loadRawImageAsBitmap(
            context = context,
            rawResourceId = R.raw.ic_locator,
            width = locatorWidth,
            height = locatorHeight,
        )
    )

    private val clusterImage = OverlayImage.fromBitmap(
        loadRawImageAsBitmap(
            context = context,
            rawResourceId = R.raw.ic_clustered_marker,
            width = clusteredMarkerMaxWidth,
            height = clusteredMarkerMaxHeight,
        )
    )

    private val markerImages = listOf(
        R.raw.ic_footprint,
        R.raw.ic_footprint_red,
        R.raw.ic_footprint_orange,
        R.raw.ic_footprint_yellow,
        R.raw.ic_footprint_green,
        R.raw.ic_footprint_turquoise,
        R.raw.ic_footprint_blue,
        R.raw.ic_footprint_navy,
        R.raw.ic_footprint_purple,
        R.raw.ic_footprint_brown,
        R.raw.ic_footprint_pink,
        R.raw.ic_footprint_white,
        R.raw.ic_footprint_black,
    ).map {
        OverlayImage.fromBitmap(
            loadRawImageAsBitmap(
                context = context,
                rawResourceId = it,
                width = markerWidth,
                height = markerHeight,
            )
        )
    }

    private val mapView: MapView
    private val clusterManager: Clusterer<MarkerKey>
    private val mapFlow = MutableStateFlow<NaverMap?>(null)

    private val markers = mutableMapOf<MapMarker, MarkerKey>()
    private val clickedMarkerKeyFlow = MutableStateFlow<MarkerKey?>(null)
    private var onPreviousMarkerDismissed: (() -> Unit)? = null

    private suspend fun getMap(): NaverMap {
        return mapFlow.first { map -> map != null }!!
    }

    init {
        // 클러스터 매니저 설정
        clusterManager = Clusterer.Builder<MarkerKey>()
            .screenDistance(clusteringDp.value.toDouble())
            .maxZoom(15)
            .clusterMarkerUpdater { info, marker ->
                marker.apply {
                    val size = calculateClusteredMarkerSize(context = context, count = info.size)
                    width = size.first
                    height = size.second
                    icon = clusterImage
                    setOnClickListener { true }
                }
            }
            .leafMarkerUpdater { info, naverMarker ->
                val key = info.key as MarkerKey
                val mapMarker = key.mapMarker

                naverMarker.apply {
                    val density = context.resources.displayMetrics.density
                    icon = markerImages[
                        mapMarker.color?.let { CategoryColor.entries.indexOf(it) + 1 } ?: 0
                    ]
                    width = (markerWidth.value * density).roundToInt()
                    height = (markerHeight.value * density).roundToInt()
                    setOnClickListener {
                        clickedMarkerKeyFlow.value = key
                        true
                    }
                }

                key.naverMarker = naverMarker
            }
            .build()

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
                clusterManager.map = map
            }

            onCreate(Bundle())
        }

        // 마커의 클릭 이벤트를 다루기 위한 설정
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
        clusterManager.add(key, null)
        markers[marker] = key
    }

    override suspend fun getAllMarkers(): List<MapMarker> {
        return markers.keys.toList()
    }

    override suspend fun removeMarker(marker: MapMarker) {
        val key = markers[marker]!!
        clusterManager.remove(key)
        markers.remove(marker)
        clickedMarkerKeyFlow.value?.let { if (key == it) clickedMarkerKeyFlow.value = null }
    }

    override suspend fun removeAllMarkers() {
        clickedMarkerKeyFlow.value = null
        clusterManager.clear()
        markers.clear()
    }
}