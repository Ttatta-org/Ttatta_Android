package com.umc.home.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.umc.home.HomeScreen
import com.umc.home.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class TestActivity : ComponentActivity() {
    private val testViewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            HomeScreen(
                viewModel =testViewModel,
                onFabClick = { /* Do nothing */ },
                onCalendarToggle = { /* Do nothing */ }
            )

        }
    }
}