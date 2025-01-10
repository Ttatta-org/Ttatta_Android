package com.umc.footprint

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun FootprintApp(
    viewModel: FootprintViewModel,
) {
    var isCategoryCardVisible by remember { mutableStateOf(false) }

    FootprintScreen(
        mapView = {
            MapView(
                onMapReady = { map -> viewModel.onMapReady(map) }
            )
        },
        categorySelectionCard = {
            if (isCategoryCardVisible) CategorySelectionCard(
                userName = "test",
                props = listOf(),
            )
        },
        onCategoryButtonClicked = { isCategoryCardVisible = !isCategoryCardVisible },
        onLocationButtonClicked = {}
    )
}
