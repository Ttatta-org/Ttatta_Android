package com.umc.footprint.data

import android.content.Context
import android.graphics.PointF
import android.location.Location
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.viewinterop.AndroidView
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationSource
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.clustering.Clusterer
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.umc.design.CategoryColor
import com.umc.footprint.R
import com.umc.footprint.core.LocationHandler
import com.umc.footprint.core.MapHandler
import com.umc.footprint.core.MapMarker
import com.umc.footprint.core.clusteredMarkerMaxHeight
import com.umc.footprint.core.clusteredMarkerMaxWidth
import com.umc.footprint.core.clusteringDp
import com.umc.footprint.core.locatorHeight
import com.umc.footprint.core.locatorWidth
import com.umc.footprint.core.markerHeight
import com.umc.footprint.core.markerWidth
import com.umc.footprint.util.loadRawImageAsBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

class MapHandlerImpl @Inject constructor(
    private val context: Context,
    private val locationHandler: LocationHandler,
) : MapHandler {

    private val locatorImage: OverlayImage = OverlayImage.fromBitmap(
        loadRawImageAsBitmap(
            context = context,
            rawResourceId = R.raw.ic_locator,
            width = locatorWidth,
            height = locatorHeight,
        )
    )

    private val clusterImage: OverlayImage = OverlayImage.fromBitmap(
        loadRawImageAsBitmap(
            context = context,
            rawResourceId = R.raw.ic_clustered_marker,
            width = clusteredMarkerMaxWidth,
            height = clusteredMarkerMaxHeight,
        )
    )

    private val defaultMarkerImage: OverlayImage = OverlayImage.fromBitmap(
        loadRawImageAsBitmap(
            context = context,
            rawResourceId = R.raw.ic_footprint,
            width = markerWidth,
            height = markerHeight,
        )
    )

    private val markerImages: Map<CategoryColor, OverlayImage> = mapOf(
        CategoryColor.RED to R.raw.ic_footprint_red,
        CategoryColor.ORANGE to R.raw.ic_footprint_orange,
        CategoryColor.YELLOW to R.raw.ic_footprint_yellow,
        CategoryColor.GREEN to R.raw.ic_footprint_green,
        CategoryColor.TURQUOISE to R.raw.ic_footprint_turquoise,
        CategoryColor.BLUE to R.raw.ic_footprint_blue,
        CategoryColor.NAVY to R.raw.ic_footprint_navy,
        CategoryColor.PURPLE to R.raw.ic_footprint_purple,
        CategoryColor.BROWN to R.raw.ic_footprint_brown,
        CategoryColor.PINK to R.raw.ic_footprint_pink,
        CategoryColor.WHITE to R.raw.ic_footprint_white,
        CategoryColor.BLACK to R.raw.ic_footprint_black,
    ).mapValues { (_, value) ->
        OverlayImage.fromBitmap(
            loadRawImageAsBitmap(
                context = context,
                rawResourceId = value,
                width = markerWidth,
                height = markerHeight,
            )
        )
    }

    private val mapView: MapView
    private val clusterManager: Clusterer<MarkerKey>

    private val mapFlow = MutableStateFlow<NaverMap?>(null)
    private val isNonClusteringZoomLevelReached = MutableStateFlow(false)
    private val markers = mutableMapOf<MapMarker, MarkerKey>()
    private var onPreviousMarkerDismissed: (() -> Unit)? = null

    private suspend fun getMap(): NaverMap {
        return mapFlow.first { map -> map != null }!!
    }

    init {
        // 클러스터 매니저 설정
        clusterManager = Clusterer.Builder<MarkerKey>()
            .maxZoom(15)
            .screenDistance(clusteringDp.value.toDouble())
            .clusterMarkerUpdater { info, marker ->
                marker.toClusterMarker(clusterSize = info.size)
            }
            .leafMarkerUpdater { info, naverMarker ->
                val key = info.key as MarkerKey

                if (isNonClusteringZoomLevelReached.value) {
                    naverMarker.toFootMarker(
                        color = key.mapMarker.color,
                        zIndex = key.mapMarker.zIndex,
                        onClicked = key.mapMarker.onClicked
                    )
                } else {
                    naverMarker.toClusterMarker(clusterSize = 1)
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
                    addOnCameraChangeListener { _, _ ->
                        onPreviousMarkerDismissed?.invoke()
                        onPreviousMarkerDismissed = null
                    }
                    setOnMapClickListener { _, _ ->
                        onPreviousMarkerDismissed?.invoke()
                        onPreviousMarkerDismissed = null
                    }
                    addOnCameraIdleListener {
                        isNonClusteringZoomLevelReached.value = map.cameraPosition.zoom > 16
                        Log.d("MapHandlerImpl", "zoom level: ${map.cameraPosition.zoom}")
                    }

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
                    locationSource = object : LocationSource {
                        private var locationChangeListener: (Location) -> Unit = {}

                        override fun activate(listener: LocationSource.OnLocationChangedListener) {
                            try {
                                { location: Location ->
                                    listener.onLocationChanged(location)
                                }.let { lambda ->
                                    locationHandler.addLocationChangeListener(lambda)
                                    locationChangeListener = lambda
                                }
                            } catch (e: Exception) { /* TODO */ }
                        }

                        override fun deactivate() {
                            try {
                                locationHandler.removeLocationChangeListener(locationChangeListener)
                            } catch (e: Exception) { /* TODO */ }
                        }
                    }
                }

                mapFlow.value = map
                clusterManager.map = map
            }
        }
        
        // 최대 비클러스터링 확대 레벨 감지
        CoroutineScope(Dispatchers.Main).launch {
            isNonClusteringZoomLevelReached.collect { isReached ->
                markers.values.toList().forEach { key ->
                    key.naverMarker?.let {
                        if (isReached) {
                            it.toFootMarker(
                                color = key.mapMarker.color,
                                zIndex = key.mapMarker.zIndex,
                                onClicked = key.mapMarker.onClicked
                            )
                        } else {
                            it.toClusterMarker(clusterSize = 1)
                        }
                    }
                }
            }
        }
    }

    @Composable
    override fun MapView(
        isBlurApplied: Boolean,
        isLocationMarkingEnabled: Boolean,
    ) {
        var capturedMap by remember { mutableStateOf<ImageBitmap?>(null) }

        LaunchedEffect(key1 = isBlurApplied) {
            if (isBlurApplied) {
                getMap().takeSnapshot { capturedMap = it.asImageBitmap() }
            } else {
                capturedMap = null
            }
        }

        LaunchedEffect(key1 = isLocationMarkingEnabled) {
            if (isLocationMarkingEnabled) {
                getMap().locationTrackingMode = LocationTrackingMode.Follow
            } else {
                getMap().locationTrackingMode = LocationTrackingMode.None
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView(
                factory = {
                    mapView.apply {
                        parent?.let { (it as ViewGroup).removeView(mapView) }
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (capturedMap != null) 0f else 1f)
            )
            capturedMap?.let { bitmap ->
                Image(
                    bitmap = bitmap,
                    contentScale = ContentScale.Fit,
                    contentDescription = null,
                    modifier = Modifier.matchParentSize()
                )
            }
        }
    }

    override suspend fun moveTo(latitude: Double, longitude: Double) {
        val target = LatLng(latitude, longitude)
        val cameraUpdate = CameraUpdate
            .scrollTo(target)
            .animate(CameraAnimation.Easing, 500L)
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
    }

    override suspend fun removeAllMarkers() {
        onPreviousMarkerDismissed?.invoke()
        onPreviousMarkerDismissed = null
        markers.values.forEach { key -> clusterManager.remove(key) }
        markers.clear()
    }

    private fun calculateClusteredMarkerSize(count: Int): Pair<Int, Int> {
        val density = context.resources.displayMetrics.density
        return listOf(clusteredMarkerMaxWidth, clusteredMarkerMaxHeight).map {
            (it.value * density * (1.0 - 1.0 / (count + 1))).roundToInt()
        }.run {
            this.first() to this.last()
        }
    }

    private fun Marker.toClusterMarker(clusterSize: Int) {
        calculateClusteredMarkerSize(count = clusterSize).let { (clusterWidth, clusterHeight) ->
            width = clusterWidth
            height = clusterHeight
        }
        anchor = PointF(0.5f, 0.5f)
        icon = clusterImage
        setOnClickListener { true }
    }

    private fun Marker.toFootMarker(
        color: CategoryColor?,
        zIndex: Int,
        onClicked: ((Float, Float) -> (() -> Unit)?)? = null
    ) {
        val density = context.resources.displayMetrics.density

        anchor = PointF(0.5f, 0.5f)
        icon = markerImages[color] ?: defaultMarkerImage
        width = (markerWidth.value * density).roundToInt()
        height = (markerHeight.value * density).roundToInt()
        this.zIndex = zIndex
        setOnClickListener {
            onPreviousMarkerDismissed?.invoke()
            map?.projection?.toScreenLocation(this.position)?.apply {
                onPreviousMarkerDismissed = onClicked?.invoke(x, y)
            }
            true
        }
    }
}