package com.umc.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier

class TestActivity : ComponentActivity() {
    private val viewModel :LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            Scaffold { innerpadding ->
                Box(modifier = Modifier.padding(innerpadding)){
                    LoginApp(viewModel = viewModel)
                }
            }
        }
    }
}