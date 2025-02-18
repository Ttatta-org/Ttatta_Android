package com.umc.record

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.net.toFile
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
import com.umc.record.util.createImageUri
import com.umc.record.util.getImageMetadata
import com.umc.record.util.uriToFile
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.LocalDateTime

enum class RecordMode {
    CAMERA,
    GALLERY,
}

@Composable
fun RecordApp(
    viewModel: RecordViewModel,
    mode: RecordMode,
    onBack: () -> Unit,
    onNavigateToCategoryApp: () -> Unit,
    onDone: () -> Unit,
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val uriForCameraMode = remember { createImageUri(context.contentResolver) }
    var image by remember { mutableStateOf<File?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { output ->
        val img = if (output) uriToFile(context = context, uri = uriForCameraMode) else null
        if (img != null) image = img
        else onBack()
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        val img = uri?.let { uriToFile(context = context, uri = uri) }
        if (img != null) image = img
        else onBack()
    }

    LaunchedEffect(key1 = Unit) {
        when (mode) {
            RecordMode.CAMERA -> cameraLauncher.launch(uriForCameraMode)
            RecordMode.GALLERY -> galleryLauncher.launch("image/*")
        }
    }

    image?.let { img ->
        val metaData = remember { getImageMetadata(img) }
        val bitmap = remember { BitmapFactory.decodeStream(img.inputStream()).asImageBitmap() }

        var date by remember { mutableStateOf(metaData.date ?: LocalDateTime.now()) }
        var coordinates by remember {
            mutableStateOf(
                if (metaData.latitude != null && metaData.longitude != null) metaData.latitude to metaData.longitude
                else null
            )
        }
        var locationName by remember { mutableStateOf("") }
        var selectedCategory by remember { mutableStateOf(viewModel.categoryInfos.find { it.name == "일상" }!!) }

        NavHost(
            navController = navController,
            startDestination = "onboarding"
        ) {
            composable("onboarding") {
                var diaryContent by remember { mutableStateOf("") }
                var showCategoryDropdown by remember { mutableStateOf(false) }

                RecordScreen(
                    image = bitmap,
                    date = date,
                    location = locationName,
                    selectedCategoryColor = selectedCategory.color,
                    categoryDropdownProp = if (showCategoryDropdown) CategoryDropdownProp(
                        itemProps = viewModel.categoryInfos.map {
                            CategoryDropdownItemProp(
                                color = it.color,
                                name = it.name,
                                onClicked = {
                                    selectedCategory = it
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
                            coordinates?.let { location ->
                                viewModel.saveDiary(
                                    image = img,
                                    content = diaryContent,
                                    categoryId = selectedCategory.id,
                                    date = date,
                                    latitude = location.first,
                                    longitude = location.second,
                                    locationName = locationName,
                                    onSucceed = { onDone() },
                                    onFailed = { /* TODO */ }
                                )
                            }
                        },
                        onDiaryContentChanged = { diaryContent = it }
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
                        onSearchWordChanged = { searchWord = it },
                        onSearchButtonClicked = {
                            viewModel.searchLocation(
                                searchWord = searchWord,
                                onSucceed = { /* TODO */ },
                                onFailed = { /* TODO */ }
                            )
                        }
                    ),
                    bottomSheetProp = viewModel.currentPinnedLocationInfo?.let { info ->
                        LocationBottomSheetProp(
                            location = info.name,
                            onConfirm = {
                                coordinates = info.latitude to info.longitude
                                locationName = info.name
                                navController.popBackStack()
                            }
                        )
                    },
                    onLocationButtonClicked = {
                        viewModel.movePinToCurrentLocation(
                            onSucceed = { /* TODO */ },
                            onFailed = { /* TODO */ }
                        )
                    }
                )
            }
        }
    }
}
