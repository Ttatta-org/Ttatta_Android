package com.umc.record.data

import android.content.Context
import android.graphics.PointF
import android.location.Location
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
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
import com.umc.record.core.locatorHeight
import com.umc.record.core.locatorWidth
import com.umc.record.util.loadRawImageAsBitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private val pinSize = DpSize(56.dp, 83.dp)
private val pinOffsetRatio = PointF(0.5f, 0.5f)

class MapHandlerImpl @Inject constructor(
    @ApplicationContext context: Context,
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

    private val mapView: MapView
    private val mapFlow = MutableStateFlow<NaverMap?>(null)
    private val pinningOffsetState = MutableStateFlow(Offset.Zero)
    private var cameraListener: (() -> Unit)? = null

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
            }
        }
    }

    @Composable
    override fun MapView(isLocationMarkingEnabled: Boolean) {
        val pinningOffset by pinningOffsetState.collectAsState()

        LaunchedEffect(key1 = isLocationMarkingEnabled) {
            if (isLocationMarkingEnabled) {
                getMap().locationTrackingMode = LocationTrackingMode.Follow
            } else {
                getMap().locationTrackingMode = LocationTrackingMode.None
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    pinningOffsetState.value = Offset(
                        x = it.size.width * pinOffsetRatio.x,
                        y = it.size.height * pinOffsetRatio.y,
                    )
                }
        ) {
            AndroidView(
                factory = {
                    mapView.apply {
                        parent?.let { (it as ViewGroup).removeView(mapView) }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier.offset {
                    Offset(
                        x = pinningOffset.x - pinSize.width.toPx() / 2,
                        y = pinningOffset.y - pinSize.height.toPx()
                    ).round()
                }
            ) {
                Image(
                    painter = painterResource(id = R.raw.img_record_map_pin),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(pinSize)
                )
            }
        }
    }

    override suspend fun getCurrentPinnedCoordination(): Pair<Double, Double> {
        return pinningOffsetState.value.let { offset ->
            getMap().projection.fromScreenLocation(
                PointF(offset.x, offset.y)
            ).let {
                it.latitude to it.longitude
            }
        }
    }

    override suspend fun movePin(latitude: Double, longitude: Double) {
        val target = LatLng(latitude, longitude)
        val cameraUpdate = CameraUpdate
            .scrollTo(target)
            .pivot(pinOffsetRatio)
            .animate(CameraAnimation.Easing, 500L)
        getMap().moveCamera(cameraUpdate)
    }

    override suspend fun addCameraIdleListener(listener: () -> Unit) {
        cameraListener?.let { getMap().removeOnCameraIdleListener(it) }
        cameraListener = listener
        getMap().addOnCameraIdleListener(listener)
    }

    override suspend fun removeCameraIdleListener(listener: () -> Unit) {
        cameraListener?.let { getMap().removeOnCameraIdleListener(it) }
        cameraListener = null
    }
}