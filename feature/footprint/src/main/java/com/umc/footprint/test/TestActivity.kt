package com.umc.footprint.test

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.umc.design.CategoryColor
import com.umc.footprint.FootprintApp
import com.umc.footprint.FootprintViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestActivity : ComponentActivity() {
    private val viewModel: FootprintViewModel by viewModels()

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
                FootprintApp(
                    viewModel = viewModel,
                )
            }
        }

        viewModel.markPositionOnMap(
            latitude = 37.55324496403485,
            longitude = 126.97274865741072,
            category = CategoryColor.BLUE,
        )

        viewModel.markPositionOnMap(
            latitude = 37.553245,
            longitude = 126.972749,
            category = CategoryColor.NAVY,
        )

        viewModel.markPositionOnMap(
            latitude = 37.6,
            longitude = 126.97274865741072,
            category = CategoryColor.YELLOW,
        )

        viewModel.markPositionOnMap(
            latitude = 37.6,
            longitude = 127.0,
            category = null,
        )

        viewModel.markPositionOnMap(
            latitude = 37.6,
            longitude = 127.59,
            category = CategoryColor.PINK,
        )

        viewModel.markPositionOnMap(
            latitude = 37.6,
            longitude = 127.6,
            category = CategoryColor.BLACK,
        )
    }
}