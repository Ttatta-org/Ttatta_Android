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
import com.umc.design.CategoryColor
import com.umc.design.component.LoadingModal
import com.umc.record.component.CategoryDropdownItemProp
import com.umc.record.screen.CategoryDropdownProp
import com.umc.record.screen.EditLocationScreen
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
                selectedCategoryColor = viewModel.selectedCategory?.color ?: CategoryColor.RED,
                showLocationMissingTooltip = false,  // TODO
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
                                Toast
                                    .makeText(context, "등록에 실패했습니다.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        )
                    } else if (selectedLocation == null) {
                        Toast
                            .makeText(context, "위치를 설정해 주세요!", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                onDiaryContentChanged = onDiaryContentChanged,
                isSubmitButtonEnabled = !isUploading,
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
                    searchValue = searchWord,
                    searchResults = TODO(),
                    location = viewModel.currentPinnedLocationInfo?.name ?: "",
                    isTopBarExpanded = TODO(),
                    isEnteringMode = TODO(),
                    onSearchValueChanged = { new ->
                        searchWord = new
                        viewModel.onSearchWordChangedRealtime(new)
                    },
                    onSearchButtonClicked = {
                        viewModel.searchLocation(
                            searchWord = searchWord,
                            onSucceed = { /* TODO */ },
                            onFailed = { /* TODO */ }
                        )
                    },
                    onLocationChanged = {
                        TODO()
                    },
                    onConfirmButtonClicked = {
                        TODO()
                    },
                    onCurrentLocationButtonClicked = {
                        viewModel.movePinToCurrentLocation(
                            onSucceed = { /* TODO */ },
                            onFailed = { /* TODO */ }
                        )
                    },
                    onMoreResultsButtonClicked = TODO(),
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
