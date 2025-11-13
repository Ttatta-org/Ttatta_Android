package com.umc.record

import android.widget.Toast
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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

    var coordinates by remember(metadata) {
        mutableStateOf(
            if (metadata?.latitude != null && metadata.longitude != null) metadata.latitude to metadata.longitude
            else null
        )
    }

    var locationName by remember { mutableStateOf("") }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        coordinates?.let { location ->
            viewModel.searchLocation(
                latitude = location.first,
                longitude = location.second,
                onSucceed = { locationName = it },
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
                        if (!isUploading && image != null) coordinates?.let { location ->
                            isUploading = true
                            viewModel.saveDiary(
                                image = image,
                                content = diaryContent,
                                categoryId = viewModel.selectedCategory?.id!!,
                                date = date,
                                latitude = location.first,
                                longitude = location.second,
                                locationName = locationName,
                                onSucceed = onDone,
                                onFailed = {
                                    isUploading = false
                                    Toast.makeText(context, "등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            )
                        } ?: run {
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
                    onConfirm = { confirmedLocationName ->
                        // TODO: 현 코드는 버그의 위험이 있음
                        viewModel.currentPinnedLocationInfo?.let { info ->
                            coordinates = info.latitude to info.longitude
                            locationName = confirmedLocationName
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
