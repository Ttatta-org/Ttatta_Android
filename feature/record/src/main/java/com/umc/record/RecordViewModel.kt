package com.umc.record

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.core.Geocoder
import com.umc.core.model.CategoryInfo
import com.umc.core.repository.DiaryRepository
import com.umc.core.repository.UserRepository
import com.umc.record.core.LocationHandler
import com.umc.record.core.MapHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import javax.inject.Inject

data class CurrentPinnedLocationInfo(
    val name: String,
    val latitude: Double,
    val longitude: Double,
)

data class SearchResultInfo(
    val name: String,
    val latitude: Double,
    val longitude: Double,
)

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val mapHandler: MapHandler,
    private val geocoder: Geocoder,
    private val locationHandler: LocationHandler,
    private val userRepository: UserRepository,
    private val diaryRepository: DiaryRepository
) : ViewModel() {

    private val userNameState = mutableStateOf("")
    private val categoryInfosState = mutableStateOf(listOf<CategoryInfo>())
    private val currentPinnedLocationInfoState = mutableStateOf<CurrentPinnedLocationInfo?>(null)

    val userName get() = userNameState.value
    val categoryInfos get() = categoryInfosState.value
    val currentPinnedLocationInfo get() = currentPinnedLocationInfoState.value

    init {
        getUserName(
            onSucceed = { /* TODO */ },
            onFailed = { /* TODO */ },
        )
        getAllCategoryInfo(
            onSucceed = { /* TODO */ },
            onFailed = { /* TODO */ },
        )
        setMapIdleListener(
            onSucceed = { /* TODO */ },
            onFailed = { /* TODO */ },
        )
    }

    private fun getUserName(
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                userNameState.value = userRepository.getUserInfo().name
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    private fun getAllCategoryInfo(
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                categoryInfosState.value = diaryRepository.getAllCategoryInfo()
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    private fun setMapIdleListener(
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                mapHandler.addCameraIdleListener {
                    viewModelScope.launch {
                        val (lat, lng) = mapHandler.getCurrentPinnedCoordination()
                        try {
                            val locationName = geocoder.convertCoordinateToAddress(
                                latitude = lat,
                                longitude = lng,
                            )
                            currentPinnedLocationInfoState.value = CurrentPinnedLocationInfo(
                                name = locationName,
                                latitude = lat,
                                longitude = lng,
                            )
                        } catch (e: Exception) {
                            currentPinnedLocationInfoState.value = null
                        }
                    }
                }
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    // 지도
    @Composable
    fun MapView(
        isLocationMarkingEnabled: Boolean,
    ) {
        mapHandler.MapView(
            isLocationMarkingEnabled = isLocationMarkingEnabled
        )
    }

    // ✅ 현재 위치 가져와서 지도 이동시키는 함수
    fun movePinToCurrentLocation(
        onSucceed: () -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                val location = locationHandler.getCurrentLocation() // ✅ 현재 위치 가져오기
                mapHandler.movePin(location.latitude, location.longitude) // ✅ 지도 핀 이동
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun saveDiary(
        image: File,
        content: String,
        categoryId: Long,
        date: LocalDateTime,
        latitude: Double,
        longitude: Double,
        locationName: String,
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                diaryRepository.createDiary(
                    categoryId = categoryId,
                    date = date,
                    content = content,
                    image = image,
                    latitude = latitude,
                    longitude = longitude,
                    locationName = locationName
                )
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun searchLocation(
        searchWord: String,
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                val (lat, lng) = geocoder.convertAddressToCoordinate(searchWord)
                mapHandler.movePin(lat, lng)
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun movePin(
        latitude: Double,
        longitude: Double,
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                mapHandler.movePin(latitude, longitude)
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun searchLocation(
        latitude: Double,
        longitude: Double,
        onSucceed: (address: String) -> Unit,
        onFailed: (e: Exception) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                val address = geocoder.convertCoordinateToAddress(latitude = latitude, longitude = longitude)
                onSucceed(address)
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }
}
