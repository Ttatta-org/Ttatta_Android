package com.umc.footprint

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.core.repository.DiaryRepository
import com.umc.design.CategoryColor
import com.umc.design.R
import com.umc.footprint.core.MapHandler
import com.umc.footprint.core.MapMarker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FootprintViewModel @Inject constructor(
    private val mapHandler: MapHandler
) : ViewModel() {

    private val clickedMarkerPositionState = mutableStateOf<Pair<Float, Float>?>(null)

    val clickedMarkerPosition get() = clickedMarkerPositionState.value

    fun getMapView(): @Composable () -> Unit {
        return mapHandler.getMapView()
    }

    fun moveMapToCurrentPosition() {
        viewModelScope.launch { mapHandler.moveToCurrentPosition() }
    }

    fun markPositionOnMap(
        latitude: Double,
        longitude: Double,
        category: CategoryColor? = null
    ) {
        val marker = MapMarker(
            latitude = latitude,
            longitude = longitude,
            color = category,
            onClicked = onClicked@{ x, y ->
                clickedMarkerPositionState.value = x to y
                return@onClicked { clickedMarkerPositionState.value = null }
            }
        )
        viewModelScope.launch { mapHandler.addMarker(marker) }
    }


}