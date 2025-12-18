package com.umc.record

import android.widget.Toast
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.umc.design.component.LoadingModal
import com.umc.record.component.CategoryDropdownItemProp
import com.umc.record.component.CategoryDropdownProp
import com.umc.record.component.DiaryBottomSheetProp
import com.umc.record.component.LocationBottomSheetProp
import com.umc.record.screen.EditLocationScreen
import com.umc.record.screen.EditLocationScreenTopBarProp
import com.umc.record.screen.RecordScreen
import com.umc.record.util.ImageMetadata
import com.umc.record.util.getImageMetadata
import java.io.File
import java.time.LocalDateTime

@Composable
fun RecordApp(
    viewModel: RecordViewModel,
    image: File?,
    diaryContent: String,
    onDiaryContentChanged: (String) -> Unit,
    onNavigateToCategoryApp: () -> Unit,
    onDone: () -> Unit,
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    val metadata: ImageMetadata? = remember { image?.let { getImageMetadata(it) } }

    var date by remember(metadata) { mutableStateOf(metadata?.date ?: LocalDateTime.now()) }

//    var coordinates by remember(metadata) {
//        mutableStateOf(
//            if (metadata?.latitude != null && metadata.longitude != null) metadata.latitude to metadata.longitude
//            else null
//        )
//    }
//
//    var locationName by remember { mutableStateOf("") }
    val selectedLocationInfo = viewModel.selectedLocationInfo
    val coordinates = selectedLocationInfo?.let { it.latitude to it.longitude }
    val locationName = selectedLocationInfo?.name ?: ""

    var showCategoryDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(metadata) {
        val metaLat = metadata?.latitude
        val metaLng = metadata?.longitude

        if (viewModel.selectedLocationInfo == null && metaLat != null && metaLng != null) {
            viewModel.searchLocation(
                latitude = metaLat,
                longitude = metaLng,
                onSucceed = { address ->
                    viewModel.updateSelectedLocation(
                        name = address,
                        latitude = metaLat,
                        longitude = metaLng,
                    )
                },
                onFailed = { /* TODO */ }
            )
        }
    }

    NavHost(
        navController = navController,
        startDestination = "onboarding",
        exitTransition = { ExitTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable("onboarding") {
            var isUploading by remember { mutableStateOf(false) }

            LaunchedEffect(key1 = Unit) {
                viewModel.getAllCategoryInfo()
            }

            RecordScreen(
                image = image,
                date = date,
                location = locationName,
                selectedCategoryColor = viewModel.selectedCategory?.color,
                showLoadingDialog = isUploading,
                categoryDropdownProp = if (showCategoryDropdown) CategoryDropdownProp(
                    itemProps = viewModel.categoryInfos.map {
                        CategoryDropdownItemProp(
                            color = it.color,
                            name = it.name,
                            onClicked = {
                                viewModel.selectCategory(categoryId = it.id)
                                showCategoryDropdown = false
                            }
                        )
                    },
                    onNewCategoryButtonClicked = onNavigateToCategoryApp
                ) else null,
                diaryBottomSheetProp = DiaryBottomSheetProp(
                    userName = viewModel.userName,
                    diaryContent = diaryContent,
                    onCreateButtonClicked = {
                        val selectedLocation = viewModel.selectedLocationInfo

                        if (!isUploading && image != null && selectedLocation != null) {
                            isUploading = true
                            viewModel.saveDiary(
                                image = image,
                                content = diaryContent,
                                categoryId = viewModel.selectedCategory?.id!!,
                                date = date,
                                latitude = selectedLocation.latitude,
                                longitude = selectedLocation.longitude,
                                locationName = selectedLocation.name ?: "",
                                onSucceed = onDone,
                                onFailed = {
                                    isUploading = false
                                    Toast.makeText(context, "등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else if (selectedLocation == null) {
                            Toast.makeText(context, "위치를 설정해 주세요!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onDiaryContentChanged = onDiaryContentChanged,
                    isButtonEnabled = !isUploading
                ),
                onDateChipClicked = { /* TODO */ },
                onLocationChipClicked = { navController.navigate("location") },
                onCategoryChipClicked = { showCategoryDropdown = !showCategoryDropdown }
            )
        }

        composable("location") {
            var searchWord by remember { mutableStateOf("") }
            var isConfirming by remember { mutableStateOf(false) }

            Box(modifier = Modifier.fillMaxSize()) {
                EditLocationScreen(
                    mapView = {
                        viewModel.MapView(
                            isLocationMarkingEnabled = false,
                        )
                    },
                    topBarProp = EditLocationScreenTopBarProp(
                        searchWord = searchWord,
                        onSearchWordChanged = { new ->
                            searchWord = new
                            // 타이핑할 때마다 ViewModel 쪽 실시간 검색 트리거
                            viewModel.onSearchWordChangedRealtime(new)
                        },
                        onSearchButtonClicked = {
                            viewModel.searchLocation(
                                searchWord = searchWord,
                                onSucceed = { /* TODO */ },
                                onFailed = { /* TODO */ }
                            )
                        }
                    ),
                    bottomSheetProp = LocationBottomSheetProp(
                        location = viewModel.currentPinnedLocationInfo?.name,
                        isConfirming = isConfirming,
                        onConfirm = { confirmedLocationName ->
                            if (isConfirming) return@LocationBottomSheetProp // 연타 방지

                            val info = viewModel.currentPinnedLocationInfo
                            if (info == null) {
                                Toast.makeText(
                                    context,
                                    "위치를 불러오는 중입니다. 잠시 후 다시 시도해 주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@LocationBottomSheetProp
                            }

                            isConfirming = true

                            viewModel.currentPinnedLocationInfo?.let { info ->
                                viewModel.updateSelectedLocation(
                                    name = confirmedLocationName,
                                    latitude = info.latitude,
                                    longitude = info.longitude,
                                )
                                navController.popBackStack()
                            }
                        }
                    ),
                    onLocationButtonClicked = {
                        viewModel.movePinToCurrentLocation(
                            onSucceed = { /* TODO */ },
                            onFailed = { /* TODO */ }
                        )
                    },
                    searchResults = viewModel.searchResults,  // 패널에 표시할 검색 결과 전달
                    onSelectSearchResult = { result ->  // 검색 결과 선택 시 핀 이동
                        viewModel.selectSearchResult(
                            result = result,
                            onSucceed = { /* 필요 시 패널 닫기 동작은 EditLocationScreen에서 처리됨 */ },
                            onFailed = { /* TODO */ }
                        )
                    },
                    onClickMoreResults = {  // '더보기' 클릭 처리 (필요 시 구현)
                        // TODO: 전체 목록 보여주기
                    }
                )

                if (isConfirming) LoadingModal()
            }

            LaunchedEffect(key1 = Unit) {
                coordinates?.let { location ->
                    viewModel.movePin(
                        latitude = location.first,
                        longitude = location.second,
                        onSucceed = { /* TODO */ },
                        onFailed = { /* TODO */ }
                    )
                }
            }
        }
    }
}
