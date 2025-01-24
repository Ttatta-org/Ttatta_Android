package com.umc.footprint.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.umc.footprint.FootprintApp
import com.umc.footprint.FootprintViewModel
import com.umc.design.CategoryColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestActivity : ComponentActivity() {
    private val viewModel: FootprintViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            Scaffold { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    FootprintApp(
                        viewModel = viewModel,
                    )
                }
            }
        }

        viewModel.markPositionOnMap(
            latitude = 37.55324496403485,
            longitude = 126.97274865741072,
            category = CategoryColor.BLUE,
        )
    }
}