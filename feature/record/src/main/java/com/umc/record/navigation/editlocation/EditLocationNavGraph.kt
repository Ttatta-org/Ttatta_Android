package com.umc.record.navigation.editlocation

import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.umc.core.util.runWithScope
import com.umc.design.component.CustomPopup
import com.umc.record.component.LocationSearchResultViewerSearchResult
import com.umc.record.screen.EditLocationScreen
import com.umc.record.screen.EditLocationSearchResultScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

object EditLocationNavGraph {
    @Serializable
    open class Route {

        @Serializable
        data class Map(
            val lat: Double? = null,
            val lng: Double? = null,
        ) : Route()

        @Serializable
        data object Search : Route()
    }

    @Parcelize
    data class Result(
        val latitude: Double,
        val longitude: Double,
        val address: String,
    ) : Parcelable {
        companion object {
            const val KEY = "edit_location_result"
        }
    }

    fun NavGraphBuilder.addEditLocationNavGraph(
        navController: NavController,
    ) = navigation<Route>(
        startDestination = Route.Map(),
    ) {
        composable<Route.Map> { backStackEntry ->
            val viewModel = hiltViewModel<EditLocationViewModel>(backStackEntry)

            val focusManager = LocalFocusManager.current
            val keyboard = LocalSoftwareKeyboardController.current

            val (resultLatitude, resultLongitude, resultAddress) = remember(backStackEntry) {
                backStackEntry.savedStateHandle.run {
                    Triple(get<Double>("lat"), get<Double>("lng"), get<String>("address"))
                }
            }

            val (initialLatitude, initialLongitude) = remember(backStackEntry) {
                backStackEntry
                    .toRoute<Route.Map>()
                    .let { it.lat to it.lng }
            }

            val searchValue by viewModel.searchValue.collectAsState()
            val searchResults by viewModel.searchResults.collectAsState()
            val currentPinInfo by viewModel.currentPinInfo.collectAsState()
            val editingLocationName by viewModel.editingLocationName.collectAsState()

            val logicalCurrentLocationName = remember(currentPinInfo, editingLocationName) {
                currentPinInfo.second ?: editingLocationName
            }

            var isTopBarExpanded by remember { mutableStateOf(false) }
            var showExitWithoutSettingLocationPopup by remember { mutableStateOf(false) }

            LaunchedEffect(initialLatitude, initialLongitude) {
                viewModel.runWithScope {
                    if (resultLatitude != null && resultLongitude != null && resultAddress != null) {
                        movePin(resultLatitude, resultLongitude)
                        viewModel.editingLocationName.value = resultAddress
                    } else if (initialLatitude != null && initialLongitude != null) {
                        movePin(initialLatitude, initialLongitude)
                    } else {
                        movePinToCurrentPosition()
                    }
                }
            }

            BackHandler {
                showExitWithoutSettingLocationPopup = true
            }

            BackHandler(enabled = isTopBarExpanded) {
                isTopBarExpanded = false
            }

            EditLocationScreen(
                mapView = {
                    viewModel.MapView()
                },
                searchValue = searchValue,
                searchResults = searchResults
                    ?.take(5)
                    ?.map {
                        LocationSearchResultViewerSearchResult(
                            name = it.title,
                            address = it.address,
                            onClick = {
                                keyboard?.hide()
                                focusManager.clearFocus()

                                viewModel.runWithScope { movePin(it.latitude, it.longitude) }
                                isTopBarExpanded = false
                            }
                        )
                    },
                location = logicalCurrentLocationName,
                isTopBarExpanded = isTopBarExpanded,
                isEnteringMode = currentPinInfo.second == null,
                isConfirmButtonEnabled = logicalCurrentLocationName.isNotBlank(),
                onSearchBarFocused = {
                    isTopBarExpanded = true
                },
                onSearchBarDismissed = {
                    isTopBarExpanded = false
                    keyboard?.hide()
                    focusManager.clearFocus()
                },
                onSearchButtonClicked = {
                    viewModel.runWithScope { runCatching { searchLocation() } }
                },
                onSearchValueChanged = {
                    viewModel.searchValue.value = it
                },
                onLocationChanged = {
                    viewModel.editingLocationName.value = it
                },
                onCurrentLocationButtonClicked = {
                    viewModel.runWithScope { movePinToCurrentPosition() }
                },
                onMoreResultsButtonClicked = if (searchResults?.isNotEmpty() == true) { ->
                    MainScope().launch { navController.navigate(Route.Search) }
                } else null,
                onConfirmButtonClicked = {
                    val result = Result(
                        latitude = currentPinInfo.first.first,
                        longitude = currentPinInfo.first.second,
                        address = logicalCurrentLocationName,
                    )

                    MainScope().launch {
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            Result.KEY,
                            result,
                        )

                        navController.popBackStack()
                    }
                },
            )

            if (showExitWithoutSettingLocationPopup) CustomPopup(
                title = "위치를 설정하지 않고 나가시겠습니까?",
                message = "위치를 설정하고 나가려면 \'설정하기\'를 눌러주세요.",
                cancelText = "아니오",
                confirmText = "네",
                onDismiss = {
                    showExitWithoutSettingLocationPopup = false
                },
                onConfirm = {
                    showExitWithoutSettingLocationPopup = false
                    MainScope().launch { navController.popBackStack() }
                },
            )
        }

        composable<Route.Search> { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.previousBackStackEntry!! }
            val viewModel = hiltViewModel<EditLocationViewModel>(parentEntry)

            val searchValue by viewModel.searchValue.collectAsState()
            val searchResults by viewModel.searchResults.collectAsState()

            EditLocationSearchResultScreen(
                searchValue = searchValue,
                searchResults = searchResults?.map {
                    LocationSearchResultViewerSearchResult(
                        name = it.title,
                        address = it.address,
                        onClick = {
                            MainScope().launch {
                                navController.previousBackStackEntry?.savedStateHandle?.apply {
                                    set("lat", it.latitude)
                                    set("lng", it.longitude)
                                    set("address", it.address)
                                }

                                navController.popBackStack()
                            }
                        }
                    )
                },
                onBackButtonClicked = {
                    MainScope().launch { navController.popBackStack() }
                },
                onSearchButtonClicked = {
                    viewModel.runWithScope { runCatching { searchLocation() } }
                },
                onSearchValueChanged = {
                    viewModel.searchValue.value = it
                }
            )
        }
    }
}
