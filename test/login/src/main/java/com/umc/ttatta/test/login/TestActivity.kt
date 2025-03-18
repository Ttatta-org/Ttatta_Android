package com.umc.ttatta.test.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.umc.core.repository.UserRepository
import com.umc.login.LoginApp
import com.umc.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TestActivity: ComponentActivity() {
    @Inject lateinit var userRepository: UserRepository
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        prepareTest()
        setContent {
            LoginApp(
                viewModel = viewModel,
                onNavigatingToHome = {},
            )
        }
    }

    private fun prepareTest() {
        CoroutineScope(Dispatchers.IO).launch {
            if (userRepository.isIdAlreadyOccupied(id = "chocho")) {
                userRepository.login(id = "chocho", password = "qwer1234")
                userRepository.leaveUser()
            }
        }
    }
}