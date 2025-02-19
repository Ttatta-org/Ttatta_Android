package com.umc.ttatta.test.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.umc.login.FindId.FindIdViewModel
import com.umc.login.Join.JoinViewModel
import com.umc.login.LoginApp
import com.umc.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestActivity : ComponentActivity() {
    private val loginviewModel : LoginViewModel by viewModels()
    private val joinviewModel : JoinViewModel by viewModels()
    private val findIdViewModel: FindIdViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            LoginApp(
                loginviewModel = loginviewModel,
                joinviewModel = joinviewModel,
                findIdViewModel = findIdViewModel,
                onNavigatingToHome = {})
        }
    }
}