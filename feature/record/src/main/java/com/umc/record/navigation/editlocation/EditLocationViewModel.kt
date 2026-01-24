package com.umc.record.navigation.editlocation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.core.Geocoder
import com.umc.core.model.LocationSearchResult
import com.umc.record.core.LocationHandler
import com.umc.record.core.MapHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditLocationViewModel @Inject constructor(
    private val geocoder: Geocoder,
    private val mapHandler: MapHandler,
    private val locationHandler: LocationHandler,
): ViewModel() {

    private val currentPinInfoState = MutableStateFlow<Pair<Pair<Double, Double>, String?>>((0.0 to 0.0) to null)
    private val searchResultsState = MutableStateFlow<List<LocationSearchResult>?>(null)

    val searchValue = MutableStateFlow("")
    val searchResults: StateFlow<List<LocationSearchResult>?> = searchResultsState
    val currentPinInfo: StateFlow<Pair<Pair<Double, Double>, String?>> = currentPinInfoState
    val editingLocationName = MutableStateFlow("")

    private val cameraIdleListener = { onPinStopped() }
    private var previousCameraIdleListenerJob: Job? = null
    private var previousSearchValueListenerJob: Job? = null

    init {
        viewModelScope.launch {
            launch { mapHandler.addCameraIdleListener(cameraIdleListener) }
            launch { searchValue.collect { onSearchValueChanged() } }

            launch { onPinStopped() }
        }
    }

    override fun onCleared() {
        previousCameraIdleListenerJob?.cancel()
        previousSearchValueListenerJob?.cancel()

        viewModelScope.launch {
            mapHandler.removeCameraIdleListener(cameraIdleListener)
        }

        super.onCleared()
    }

    @Composable
    fun MapView() = mapHandler.MapView(isLocationMarkingEnabled = false)

    suspend fun movePin(latitude: Double, longitude: Double) {
        mapHandler.movePin(latitude, longitude)
    }

    suspend fun movePinToCurrentPosition() {
        val location = locationHandler.getCurrentLocation()
        movePin(location.latitude, location.longitude)
    }

    suspend fun searchLocation() {
        val value = searchValue.value
        val results = geocoder.searchLocationByKeyword(value)
        searchResultsState.value = results
    }

    private fun onSearchValueChanged() {
        previousSearchValueListenerJob?.cancel()

        previousSearchValueListenerJob = viewModelScope.launch {
            val search = searchValue.value
            searchResultsState.value = null

            if (search.isBlank()) return@launch

            val results = runCatching {
                geocoder.searchLocationByKeyword(search)
            }.getOrNull()

            if (!isActive) return@launch

            searchResultsState.value = results
        }
    }

    private fun onPinStopped() {
        previousCameraIdleListenerJob?.cancel()

        previousCameraIdleListenerJob = viewModelScope.launch {
            val (lat, lng) = runCatching {
                mapHandler.getCurrentPinnedCoordination()
            }.getOrNull() ?: return@launch

            if (!isActive) return@launch

            val address = runCatching {
                geocoder.convertCoordinateToAddress(lat, lng)
            }.getOrNull()

            if (!isActive) return@launch

            currentPinInfoState.value = (lat to lng) to address
        }
    }
}