package com.umc.record.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.umc.record.RecordApp
import com.umc.record.RecordViewModel

class RecordTestActivity : ComponentActivity() {
    private val viewModel: RecordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets(0))
                    .background(Color.White)
            ) {
                val categories = viewModel.categories.collectAsState().value
                val selectedCategory = viewModel.selectedCategory.collectAsState().value
                val diaryText = viewModel.diaryText.collectAsState().value

                RecordApp(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    diaryText = diaryText,
                    onCategorySelect = { viewModel.selectCategory(it) },
                    onDiaryTextUpdate = { viewModel.updateDiaryText(it) },
                    mapView = viewModel.getMapView(),
                    onLocationButtonClicked = { viewModel.moveMapToCurrentPosition() }
                )
            }
        }
    }
}