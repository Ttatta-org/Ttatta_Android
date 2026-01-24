package com.umc.record.navigation.record

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.umc.core.util.runWithScope
import com.umc.core.util.showToast
import com.umc.design.CategoryColor
import com.umc.design.component.CustomPopup
import com.umc.design.component.LoadingModal
import com.umc.record.component.CategoryDropdownItemProp
import com.umc.record.navigation.editlocation.EditLocationNavGraph
import com.umc.record.screen.CategoryDropdownProp
import com.umc.record.screen.RecordScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.io.File

object RecordNavGraph {
    @Serializable
    data object Route

    fun NavGraphBuilder.addRecordNavGraph(
        viewModel: RecordViewModel,
        navController: NavController,
        image: File?,
        onBackToHome: () -> Unit,
        onNavigateToCategoryApp: () -> Unit,
        onDiaryUploadDone: () -> Unit,
    ) = composable<Route> { backStackEntry ->
        val context = LocalContext.current

        val editLocationResult by backStackEntry.savedStateHandle
            .getStateFlow<EditLocationNavGraph.Result?>(EditLocationNavGraph.Result.KEY, null)
            .collectAsState()

        val date by viewModel.date.collectAsState()
        val location by viewModel.location.collectAsState()
        val locationName by viewModel.locationName.collectAsState()
        val categories by viewModel.categoryInfos.collectAsState()
        val selectedCategory by viewModel.selectedCategory.collectAsState()
        val userName by viewModel.userName.collectAsState()
        val diaryContent by viewModel.content.collectAsState()
        val isLocationMissing by viewModel.isLocationMissing.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()

        var showCategoryDropdown by remember { mutableStateOf(false) }
        var showExitWithoutUploadPopup by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            viewModel.run {
                launch { runCatching { loadUserName() } }
                launch { runCatching { loadCategoryInfos() } }
            }
        }

        LaunchedEffect(image) {
            if (image != null && viewModel.image.value != image) viewModel.setImage(image)
        }

        LaunchedEffect(editLocationResult) {
            editLocationResult?.let { result ->
                viewModel.updateLocation(
                    latitude = result.latitude,
                    longitude = result.longitude,
                    locationName = result.address,
                )
            }
        }

        BackHandler {
            showExitWithoutUploadPopup = true
        }

        RecordScreen(
            image = image,
            date = date,
            location = locationName,
            selectedCategoryColor = selectedCategory?.color ?: CategoryColor.RED,
            showLocationMissingTooltip = isLocationMissing == true,
            userName = userName,
            diaryContent = diaryContent,
            isSubmitButtonEnabled = diaryContent.isNotBlank() && locationName != null,
            categoryDropdownProp = if (showCategoryDropdown) {
                CategoryDropdownProp(
                    itemProps = categories.map {
                        CategoryDropdownItemProp(
                            color = it.color,
                            name = it.name,
                            onClicked = { viewModel.selectCategory(it.id) },
                        )
                    },
                    onNewCategoryButtonClicked = onNavigateToCategoryApp,
                )
            } else null,
            onCreateButtonClicked = {
                viewModel.runWithScope {
                    runCatching { saveDiary() }
                        .onSuccess { onDiaryUploadDone() }
                        .onFailure { context.showToast("일기 업로드에 실패했습니다.") }
                }
            },
            onDiaryContentChanged = { viewModel.content.value = it },
            onDateChipClicked = {
                // TODO: 날짜 수정 기능 추가
            },
            onLocationChipClicked = {
                MainScope().launch {
                    navController.navigate(
                        route = EditLocationNavGraph.Route.Map(
                            lat = location?.first,
                            lng = location?.second,
                        )
                    )
                }
            },
            onCategoryChipClicked = { showCategoryDropdown = !showCategoryDropdown },
        )

        if (isLoading) LoadingModal()

        if (showExitWithoutUploadPopup) CustomPopup(
            title = "저장하지 않고 나가시겠습니까?",
            message = "저장하지 않은 일기는 사라집니다.",
            cancelText = "아니오",
            confirmText = "네",
            onDismiss = {
                showExitWithoutUploadPopup = false
            },
            onConfirm = {
                showExitWithoutUploadPopup = false
                onBackToHome()
            },
        )
    }
}