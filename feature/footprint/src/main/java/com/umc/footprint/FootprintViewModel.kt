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
    val clusterId: Long,
)

@HiltViewModel
class FootprintViewModel @Inject constructor(
    private val mapHandler: MapHandler,
    private val diaryRepository: DiaryRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private var previousClickedClusterId: Long? = null

    private val diaryMapState = mutableStateOf<Map<Int, DiaryForCard>>(emptyMap())
    private val clickedMarkerInfoState = mutableStateOf<ClickedMarkerInfo?>(null)
    private val categoryListState = mutableStateOf<List<CategoryInfo>>(listOf())
    private val selectedCategoryIdState = mutableStateOf<Long?>(null)
    private val userNameState = mutableStateOf("")

    val clickedMarkerInfo get() = clickedMarkerInfoState.value
    val diaryMap get() = diaryMapState.value
    val categoryList get() = categoryListState.value
    val selectedCategoryId get() = selectedCategoryIdState.value
    val userName get() = userNameState.value

    init {
        viewModelScope.launch {
            mapHandler.addOnDismissListener { previousClickedClusterId = null }
        }
    }

    @Composable
    fun MapView(
        isBlurApplied: Boolean,
        isLocationMarkingEnabled: Boolean,
    ) {
        mapHandler.MapView(
            isBlurApplied = isBlurApplied,
            isLocationMarkingEnabled = isLocationMarkingEnabled
        )
    }

    fun moveMapToCurrentPosition(
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                mapHandler.moveToCurrentPosition()
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    private fun dismissSelectedMarker(
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                mapHandler.dismissMarkerEvent()
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun selectShowingCategory(
        categoryId: Long?,
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                mapHandler.removeAllMarkers()
                diaryRepository.getAllFootprints(categoryId = categoryId).forEach {
                    markMap(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        diaryId = it.diaryId,
                        clusterId = it.clusterId,
                        color = it.color,
                    )
                }
                selectedCategoryIdState.value = categoryId
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    private fun getAllFootprint(
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                mapHandler.removeAllMarkers()
                diaryRepository.getAllFootprints(categoryId = null).forEach {
                    markMap(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        diaryId = it.diaryId,
                        clusterId = it.clusterId,
                        color = it.color,
                    )
                }
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun getDiaryFromServer(
        page: Int,
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                val diary = diaryRepository.getDiaries(
                    page = page,
                    clusterId = clickedMarkerInfo!!.clusterId,
                    categoryId = selectedCategoryId,
                )
                diaryMapState.value += (page to diary)
                onSucceed()
            } catch (e: Exception) {
                diaryMapState.value -= page
                onFailed(e)
            }
        }
    }

    fun modifyDiary(
        diaryId: Long,
        content: String,
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                diaryRepository.modifyDiary(
                    diaryId = diaryId,
                    content = content,
                )
                getDiaryFromServer(
                    page = diaryMap.firstNotNullOf { (page, diary) ->
                        if (diary.id == diaryId) page else null
                    },
                    onSucceed = { onSucceed() },
                    onFailed = { onFailed(it) },
                )
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun deleteDiary(
        diaryId: Long,
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                diaryRepository.deleteDiary(diaryId = diaryId)
                getAllCategoryInfoFromServer()

                val startPage = diaryMap.firstNotNullOf { (page, diary) ->
                    if (diary.id == diaryId) page else null
                }
                val endPage = diaryMap.keys.max()

                (startPage .. endPage).forEach { page ->
                    getDiaryFromServer(
                        page = page,
                        onFailed = {
                            if (diaryMap.isEmpty()) {
                                dismissSelectedMarker()
                                getAllFootprint()
                            }
                        }
                    )
                }
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun getAllCategoryInfoFromServer(
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {},
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

    fun getUserNameFromServer(
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {},
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
        color: CategoryColor?,
    ) {
        val marker = MapMarker(
            latitude = latitude,
            longitude = longitude,
            zIndex = diaryId.toInt(),
            color = color,
            onClicked = onClicked@ { x, y ->
                if (previousClickedClusterId != clusterId) {
                    clickedMarkerInfoState.value = ClickedMarkerInfo(
                        x = x,
                        y = y,
                        clusterId = clusterId,
                    )
                    previousClickedClusterId = clusterId
                } else {
                    previousClickedClusterId = null
                }
                return@onClicked {
                    clickedMarkerInfoState.value = null
                    diaryMapState.value = emptyMap()
                }
            }
        )
        viewModelScope.launch { mapHandler.addMarker(marker) }
    }
}