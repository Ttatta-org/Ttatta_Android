package com.umc.category.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.umc.category.CategoryApp
import com.umc.category.CategoryViewModel
import com.umc.core.repository.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TestActivity: ComponentActivity() {
    @Inject
    lateinit var userRepository: UserRepository
    private val viewModel: CategoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareTest()

        enableEdgeToEdge()
        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                CategoryApp(viewModel = viewModel)
            }
        }
    }

    private fun prepareTest() {
        CoroutineScope(Dispatchers.IO).launch {
            if (!userRepository.isIdAlreadyOccupied(id = TestValues.ID)) {
                userRepository.join(
                    id = TestValues.ID,
                    password = TestValues.PASSWORD,
                    name = TestValues.NAME,
                    nickname = TestValues.NICKNAME,
                    email = TestValues.EMAIL
                )
            }

            userRepository.login(
                id = TestValues.ID,
                password = TestValues.PASSWORD
            )
        }
    }
}