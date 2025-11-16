package com.umc.footprint.implementation

import android.content.Context
import android.graphics.PointF
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.clustering.Clusterer
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.umc.footprint.R
import com.umc.footprint.core.DesignConstant
import com.umc.footprint.core.MapHandler
import com.umc.footprint.core.MapMarker
import com.umc.footprint.util.calculateClusteredMarkerSize
import com.umc.footprint.util.loadRawImageAsBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MapHandlerImpl(
    private val context: Context,
) : MapHandler {

    val locatorImage by lazy {
        OverlayImage.fromBitmap(
            loadRawImageAsBitmap(
                context = context,
                rawResourceId = R.raw.ic_locator,
                size = DesignConstant.LocatorSize,
            )
        )
    }

    val clusterImage by lazy {
        OverlayImage.fromBitmap(
            loadRawImageAsBitmap(
                context = context,
                rawResourceId = R.raw.ic_clustered_marker,
                size = DesignConstant.ClusterMarkerMaxSize,
            )
        )
    }

    val footMarkerImages by lazy {
        DesignConstant.FootprintMarkerResourceMap.mapValues { (_, value) ->
            OverlayImage.fromBitmap(
                loadRawImageAsBitmap(
                    context = context,
                    rawResourceId = value,
                    size = DesignConstant.FootprintMarkerSize,
                )
            )
        }
    }

    val bookMarkerImages by lazy {
        DesignConstant.BookMarkerResourceMap.mapValues { (_, value) ->
            OverlayImage.fromBitmap(
                loadRawImageAsBitmap(
                    context = context,
                    rawResourceId = value,
                    size = DesignConstant.BookMarkerSize,
                )
            )
        }
    }

    private val mapView: MapView
    private val clusterManager: Clusterer<MarkerKey>
    private var fusedLocationSource: FusedLocationSource? = null

    private val mapFlow = MutableStateFlow<NaverMap?>(null)
    private val isNonClusteringZoomLevelReached = MutableStateFlow(false)
    private val markers = mutableSetOf<Marker>()
    private var onMoveAnimationEnded: (() -> Unit)? = null
    private var onPreviousMarkerDismissed: (() -> Unit)? = null

    private suspend fun getMap(): NaverMap {
        return mapFlow.first { map -> map != null }!!
    }

    init {
        // 클러스터 매니저 설정
        clusterManager = Clusterer
            .Builder<MarkerKey>()
            .maxZoom(14)
            .screenDistance(DesignConstant.ClusteringDistance.value.toDouble())
            .clusterMarkerUpdater { info, marker ->
                marker.toClusterMarker(clusterSize = info.size)
            }
            .leafMarkerUpdater { info, marker ->
                val mapMarker = (info.key as MarkerKey).mapMarker
                marker.tag = mapMarker
                markers.add(marker)

                if (isNonClusteringZoomLevelReached.value) marker.toNormalMarker(marker = mapMarker)
                else marker.toClusterMarker(clusterSize = 1)
            }
            .build()

        // 네이버 SDK를 사용한 지도 초기 설정
        mapView = MapView(context).apply {
            id = View.generateViewId()
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )

            getMapAsync { map ->
                // 이벤트 리스너 설정
                map.addOnCameraChangeListener { reason, _ ->
                    if (reason == CameraUpdate.REASON_GESTURE) CoroutineScope(Dispatchers.Main).launch {
                        dismissMarkerEvent()
                    }
                }

                map.setOnMapClickListener { _, _ ->
                    CoroutineScope(Dispatchers.Main).launch {
                        dismissMarkerEvent()
                    }
                }

                map.addOnCameraIdleListener {
                    isNonClusteringZoomLevelReached.value = map.cameraPosition.zoom > 15
                }

                // UI 설정
                map.uiSettings.apply {
                    isLogoClickEnabled = false
                    isCompassEnabled = false
                    isScaleBarEnabled = false
                    isZoomControlEnabled = false
                    isIndoorLevelPickerEnabled = false
                    isLocationButtonEnabled = false
                }

                map.isIndoorEnabled = false
                map.locationOverlay.icon = locatorImage

                mapFlow.value = map
                clusterManager.map = map
            }
        }

        // 최대 비클러스터링 확대 레벨 감지
        CoroutineScope(Dispatchers.Main).launch {
            isNonClusteringZoomLevelReached.collect { isReached ->
                markers
                    .filter { it.isAdded }
                    .forEach {
                        if (isReached) it.toNormalMarker(marker = it.tag as MapMarker)
                        else it.toClusterMarker(clusterSize = 1)
                    }
            }
        }
    }

    @Composable
    override fun MapView(
        isBlurApplied: Boolean,
        isLocationPermissionGranted: Boolean,
    ) {
        val context = LocalContext.current as ComponentActivity
        var capturedMap by remember { mutableStateOf<ImageBitmap?>(null) }

        LaunchedEffect(key1 = isBlurApplied) {
            if (isBlurApplied) {
                getMap().takeSnapshot { capturedMap = it.asImageBitmap() }
            } else {
                capturedMap = null
            }
        }

        LaunchedEffect(key1 = isLocationPermissionGranted) {
            if (isLocationPermissionGranted) {
                val source = FusedLocationSource(context, 100)

                getMap().apply {
                    locationTrackingMode = LocationTrackingMode.Follow
                    locationSource = source
                }

                fusedLocationSource = source
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
                    .alpha(if (capturedMap != null) 0f else 1f),
            )
            capturedMap?.let { bitmap ->
                Image(
                    bitmap = bitmap,
                    contentScale = ContentScale.Fit,
                    contentDescription = null,
                    modifier = Modifier
                        .matchParentSize()
                        .let { if (isBlurApplied) it.blur(radius = 10.dp) else it })
            }
        }
    }

    override suspend fun moveTo(
        latitude: Double,
        longitude: Double,
        offset: Offset,
        zoom: Boolean,
        animationTime: Long,
        onAnimationEnded: () -> Unit,
    ) {
        val pivot = getMap().contentRect.let { mapSize ->
            PointF(
                0.5f + offset.x / mapSize.width(),
                0.5f + offset.y / mapSize.height(),
            )
        }

        val target = LatLng(latitude, longitude)
        onMoveAnimationEnded = onAnimationEnded
        val cameraUpdate = run {
            if (zoom) CameraUpdate.scrollAndZoomTo(target, 16.0)
            else CameraUpdate.scrollTo(target)
        }
            .pivot(pivot)
            .animate(CameraAnimation.Easing, animationTime)
            .finishCallback { onMoveAnimationEnded?.invoke() }

        getMap().moveCamera(cameraUpdate)
    }

    override suspend fun moveToCurrentPosition() {
        val location = fusedLocationSource?.lastLocation ?: return
        moveTo(location.latitude, location.longitude)
    }

    override suspend fun getViewingPosition(): Pair<Double, Double> {
        val position = getMap().cameraPosition.target
        return position.latitude to position.longitude
    }

    override suspend fun addMarkers(vararg markers: MapMarker) {
        val keys = markers.associate { marker ->
            MarkerKey(marker) to null
        }

        MainScope().launch {
            clusterManager.addAll(keys)
        }
    }

    override suspend fun removeAllMarkers() {
        onPreviousMarkerDismissed?.invoke()
        onPreviousMarkerDismissed = null

        MainScope().launch {
            clusterManager.clear()
            markers.clear()
        }
    }

    override suspend fun addOnDismissListener(listener: () -> Unit) {
        getMap().apply {
            setOnMapClickListener { _, _ ->
                CoroutineScope(Dispatchers.Main).launch {
                    dismissMarkerEvent()
                    listener()
                }
            }

            addOnCameraChangeListener { reason, _ ->
                if (reason == CameraUpdate.REASON_GESTURE) CoroutineScope(Dispatchers.Main).launch {
                    listener()
                }
            }
        }
    }

    override suspend fun dismissMarkerEvent() {
        onMoveAnimationEnded = null
        onPreviousMarkerDismissed?.invoke()
        onPreviousMarkerDismissed = null
    }

    private fun Marker.toClusterMarker(clusterSize: Int) {
        val (clusterWidth, clusterHeight) = calculateClusteredMarkerSize(
            density = context.resources.displayMetrics.density,
            count = clusterSize,
        )

        width = clusterWidth.roundToInt()
        height = clusterHeight.roundToInt()
        anchor = PointF(0.5f, 0.5f)
        icon = clusterImage
        setOnClickListener { true }
    }

    private fun Marker.toNormalMarker(marker: MapMarker) {
        val density = context.resources.displayMetrics.density
        val color = marker.color
        val zIndex = marker.zIndex
        val isOverlapping = marker.isOverlapping
        val onClicked = marker.onClicked

        val size =
            if (isOverlapping) DesignConstant.BookMarkerSize else DesignConstant.FootprintMarkerSize
        val imageMap = if (isOverlapping) bookMarkerImages else footMarkerImages

        this.width = (size.width.value * density).roundToInt()
        this.height = (size.height.value * density).roundToInt()
        this.anchor = PointF(0.5f, 0.5f)
        this.zIndex = zIndex
        imageMap[color]?.let { this.icon = it }

        setOnClickListener {
            onPreviousMarkerDismissed?.invoke()
            map?.projection
                ?.toScreenLocation(this.position)
                ?.apply { onPreviousMarkerDismissed = onClicked?.invoke(Offset(x, y)) }
            true
        }
    }
}