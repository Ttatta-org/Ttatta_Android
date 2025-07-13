package com.umc.footprint

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.core.model.CategoryInfo
import com.umc.core.model.DiaryForCard
import com.umc.core.repository.DiaryRepository
import com.umc.core.repository.UserRepository
import com.umc.design.CategoryColor
import com.umc.footprint.core.MapHandler
import com.umc.footprint.core.MapMarker
import com.umc.footprint.model.event.MapMarkerClickedEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FootprintViewModel @Inject constructor(
    private val mapHandler: MapHandler,
    private val diaryRepository: DiaryRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private var previousClickedClusterId: Long? = null

    private val diaryMapState = mutableStateOf<Map<Int, DiaryForCard>>(emptyMap())
    private val mapMarkerClickedEventState = mutableStateOf<MapMarkerClickedEvent?>(null)
    private val categoryListState = mutableStateOf<List<CategoryInfo>>(listOf())
    private val selectedCategoryIdState = mutableStateOf<Long?>(null)
    private val userNameState = mutableStateOf("")

    val footprintMarkerClickedEvent get() = mapMarkerClickedEventState.value
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
                dismissSelectedMarker()
                mapHandler.moveToCurrentPosition()
                onSucceed()
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    fun moveMapToPosition(
        latitude: Double,
        longitude: Double,
        pivot: Offset,
        onSucceed: () -> Unit = {},
        onFailed: (e: Exception) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                mapHandler.moveTo(
                    latitude = latitude,
                    longitude = longitude,
                    offset = pivot,
                    animationTime = 200,
                    onAnimationEnded = onSucceed
                )
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
                    CoroutineScope(Dispatchers.IO).launch {
                        markMap(
                            latitude = it.latitude,
                            longitude = it.longitude,
                            clusterId = it.clusterId,
                            zIndex = it.diaryId.toInt(),
                            isOverlapping = try {
                                diaryRepository.getDiaries(page = 1, clusterId = it.clusterId)
                                true
                            } catch (_: Exception) {
                                false
                            },
                            color = it.color,
                        )
                    }
                }
                selectedCategoryIdState.value = categoryId
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
                    clusterId = footprintMarkerClickedEvent!!.clusterId,
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
                    onSucceed = onSucceed,
                    onFailed = onFailed,
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
                            if (page == 0) {
                                dismissSelectedMarker()
                                selectShowingCategory(categoryId = null)
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

    private suspend fun markMap(
        latitude: Double,
        longitude: Double,
        clusterId: Long,
        zIndex: Int,
        isOverlapping: Boolean,
        color: CategoryColor?,
    ) {
        val marker = MapMarker(
            latitude = latitude,
            longitude = longitude,
            zIndex = zIndex,
            color = color,
            isOverlapping = isOverlapping,
            onClicked = onClicked@ { offset ->
                if (previousClickedClusterId != clusterId) {
                    mapMarkerClickedEventState.value = MapMarkerClickedEvent(
                        offset = offset,
                        latitude = latitude,
                        longitude = longitude,
                        clusterId = clusterId,
                        isBook = isOverlapping,
                        color = color,
                    )
                    previousClickedClusterId = clusterId
                } else {
                    previousClickedClusterId = null
                }

                return@onClicked {
                    mapMarkerClickedEventState.value = null
                    diaryMapState.value = emptyMap()
                }
            }
        )

        mapHandler.addMarker(marker)
    }
}