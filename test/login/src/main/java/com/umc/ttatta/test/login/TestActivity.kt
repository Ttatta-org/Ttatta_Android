package com.umc.ttatta.test.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.umc.core.repository.UserRepository
import com.umc.login.FindId.FindIdViewModel
import com.umc.login.Join.JoinViewModel
import com.umc.login.LoginApp
import com.umc.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TestActivity : ComponentActivity() {
    @Inject
    lateinit var userRepository: UserRepository
    private val loginviewModel : LoginViewModel by viewModels()
    private val joinviewModel : JoinViewModel by viewModels()
    private val findIdViewModel: FindIdViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        prepareTest()
        setContent {
            LoginApp(
                loginviewModel = loginviewModel,
                joinviewModel = joinviewModel,
                findIdViewModel = findIdViewModel,
                onNavigatingToHome = {})
        }
    }

    private fun prepareTest() {
        CoroutineScope(Dispatchers.IO).launch {
            if (userRepository.isIdAlreadyOccupied(id = "jjh11031")) {
                userRepository.login(
                    id = "jjh110031",
                    password = "jjh110031!"
                )
                userRepository.leaveUser()
            }
        }
    }
}