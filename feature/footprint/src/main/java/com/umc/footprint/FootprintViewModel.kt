package com.umc.footprint

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.core.model.CategoryInfo
import com.umc.core.model.DiaryForCard
import com.umc.core.repository.DiaryRepository
import com.umc.core.repository.UserRepository
import com.umc.design.CategoryColor
import com.umc.footprint.core.MapHandler
import com.umc.footprint.core.MapMarker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClickedMarkerInfo(
    val x: Float,
    val y: Float,
    val latitude: Double,
    val longitude: Double,
)

@HiltViewModel
class FootprintViewModel @Inject constructor(
    private val mapHandler: MapHandler,
    private val diaryRepository: DiaryRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val markers: MutableMap<Long, MutableList<MapMarker>> = mutableMapOf()

    private val clickedMarkerInfoState = mutableStateOf<ClickedMarkerInfo?>(null)
    private val diaryListState = mutableStateOf<List<DiaryForCard>>(listOf())
    private val categoryInfoListState = mutableStateOf<List<CategoryInfo>>(listOf())
    private val userNameState = mutableStateOf("")

    val clickedMarkerInfo get() = clickedMarkerInfoState.value
    val diaryList get() = diaryListState.value
    val categoryInfoList get() = categoryInfoListState.value
    val userName get() = userNameState.value

    init {
        viewModelScope.launch {
            // 모든 발자국 로드 및 마커 추가
            launch {
                diaryRepository.getAllFootprints().map { footprint ->
                    markMap(
                        latitude = footprint.latitude,
                        longitude =  footprint.longitude,
                        categoryId = footprint.categoryId,
                        color = footprint.color,
                    )
                }
            }
            // 현재 위치로 맵 이동
            launch { moveMapToCurrentPosition() }
            // 카테고리 정보 로드
            launch {
                getAllCategoryInfoFromServer(
                    onSucceed = { /* TODO */ },
                    onFailed = { /* TODO */ },
                )
            }
            // 사용자 이름 로드
            launch {
                getUserNameFromServer(
                    onSucceed = { /* TODO */ },
                    onFailed = { /* TODO */ },
                )
            }
        }
    }

    fun getMapView(): @Composable () -> Unit {
        return mapHandler.getMapView()
    }

    fun moveMapToCurrentPosition() {
        viewModelScope.launch { mapHandler.moveToCurrentPosition() }
    }

    fun markMap(
        latitude: Double,
        longitude: Double,
        categoryId: Long,
        color: CategoryColor?,
    ) {
        val marker = MapMarker(
            latitude = latitude,
            longitude = longitude,
            color = color,
            onClicked = onClicked@{ x, y ->
                clickedMarkerInfoState.value = ClickedMarkerInfo(
                    x = x,
                    y = y,
                    latitude = latitude,
                    longitude = longitude,
                )
                getDiaryFromServer(
                    onSucceed = { /* TODO */ },
                    onFailed = { /* TODO */ },
                )
                return@onClicked {
                    clickedMarkerInfoState.value = null
                    diaryListState.value = listOf()
                }
            }
        )
        markMap(categoryId = categoryId, marker = marker)
    }

    fun selectShowingCategory(
        categoryId: Long?,
    ) {
        viewModelScope.launch {
            mapHandler.removeAllMarkers()
            if (categoryId != null) markers[categoryId]?.forEach { marker ->
                markMap(
                    categoryId = categoryId,
                    marker = marker,
                )
            } else {
                markers.forEach { (id, markerList) ->
                    markerList.forEach { marker ->
                        markMap(
                            categoryId = id,
                            marker = marker,
                        )
                    }
                }
            }
        }
    }

    fun getDiaryFromServer(
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                diaryListState.value = listOf(
                    diaryRepository.getDiaries(
                        page = 0 /* TODO: 제거 */,
                        latitude = clickedMarkerInfo!!.latitude,
                        longitude = clickedMarkerInfo!!.longitude,
                    )
                )
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun modifyDiary(
        diaryId: Long,
        content: String,
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                diaryRepository.modifyDiary(
                    diaryId = diaryId,
                    content = content,
                )
                getDiaryFromServer(
                    onSucceed = { onSucceed() },
                    onFailed = { throw it },
                )
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun deleteDiary(
        diaryId: Long,
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                diaryRepository.deleteDiary(diaryId = diaryId)
                getDiaryFromServer(
                    onSucceed = onSucceed,
                    onFailed = { throw it }
                )
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun getAllCategoryInfoFromServer(
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                categoryInfoListState.value = diaryRepository.getAllCategoryInfo()
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun getUserNameFromServer(
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit,
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

    private fun markMap(
        categoryId: Long,
        marker: MapMarker,
    ) {
        markers[categoryId]?.add(marker) ?: run { markers[categoryId] = mutableListOf(marker) }
        viewModelScope.launch { mapHandler.addMarker(marker) }
    }
}