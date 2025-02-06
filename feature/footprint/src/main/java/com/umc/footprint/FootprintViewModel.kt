package com.umc.footprint

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
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
    val clusterId: Long,
)

@HiltViewModel
class FootprintViewModel @Inject constructor(
    private val mapHandler: MapHandler,
    private val diaryRepository: DiaryRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val markers = mutableMapOf<Long, MutableList<MapMarker>>()

    private val diaryStateMap = mutableStateMapOf<Int, DiaryForCard>()
    private val clickedMarkerInfoState = mutableStateOf<ClickedMarkerInfo?>(null)
    private val categoryListState = mutableStateOf<List<CategoryInfo>>(listOf())
    private val selectedCategoryIdState = mutableStateOf<Long?>(null)
    private val userNameState = mutableStateOf("")

    val clickedMarkerInfo get() = clickedMarkerInfoState.value
    val diaryMap get() = diaryStateMap.toMap()
    val categoryList get() = categoryListState.value
    val selectedCategoryId get() = selectedCategoryIdState.value
    val userName get() = userNameState.value

    init {
        viewModelScope.launch {
            // 모든 발자국 로드 및 마커 추가
            launch {
                diaryRepository.getAllFootprints().map { footprint ->
                    markMap(
                        latitude = footprint.latitude,
                        longitude = footprint.longitude,
                        diaryId = footprint.diaryId,
                        clusterId = footprint.clusterId,
                        categoryId = footprint.categoryId,
                        color = footprint.color,
                    )
                }
            }
            // 현재 위치로 맵 이동
            launch {
                moveMapToCurrentPosition()
            }
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

    fun selectShowingCategory(
        categoryId: Long?,
    ) {
        viewModelScope.launch {
            mapHandler.removeAllMarkers()
            val markerList = markers[categoryId] ?: markers.values.flatten()
            markerList.forEach { marker -> mapHandler.addMarker(marker) }
            selectedCategoryIdState.value = categoryId
        }
    }

    fun getDiaryFromServer(
        page: Int,
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                val diary = diaryRepository.getDiaries(
                    page = page,
                    clusterId = clickedMarkerInfo!!.clusterId,
                )
                diaryStateMap[page] = diary
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
                    page = diaryStateMap.firstNotNullOf { (page, diary) ->
                        if (diary.id == diaryId) page else null
                    },
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
                    page = diaryStateMap.firstNotNullOf { (page, diary) ->
                        if (diary.id == diaryId) page else null
                    },
                    onSucceed = onSucceed,
                    onFailed = { throw it }
                )
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    private fun getAllCategoryInfoFromServer(
        onSucceed: () -> Unit,
        onFailed: (e: Exception) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                categoryListState.value = diaryRepository.getAllCategoryInfo()
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    private fun getUserNameFromServer(
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
        latitude: Double,
        longitude: Double,
        diaryId: Long,
        clusterId: Long,
        categoryId: Long,
        color: CategoryColor?,
    ) {
        val marker = MapMarker(
            latitude = latitude,
            longitude = longitude,
            zIndex = diaryId.toInt(),
            color = color,
            onClicked = onClicked@{ x, y ->
                clickedMarkerInfoState.value = ClickedMarkerInfo(
                    x = x,
                    y = y,
                    clusterId = clusterId,
                )
                return@onClicked {
                    clickedMarkerInfoState.value = null
                    diaryStateMap.clear()
                }
            }
        )
        markers[categoryId]?.add(marker) ?: run { markers[categoryId] = mutableListOf(marker) }
        viewModelScope.launch { mapHandler.addMarker(marker) }
    }
}