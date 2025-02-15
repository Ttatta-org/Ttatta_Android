package com.umc.ttatta

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import com.umc.core.repository.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var userRepository: UserRepository
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableDebugMode()

        setStatusBarTransparent()
        setContent {
            MainApp(
                viewModel = viewModel,
            )
        }
    }

    private fun enableDebugMode() {
        CoroutineScope(Dispatchers.IO).launch {
            if (!userRepository.isIdAlreadyOccupied(DebugConfig.ID)) {
                userRepository.join(
                    id = DebugConfig.ID,
                    password = DebugConfig.PASSWORD,
                    name = DebugConfig.NAME,
                    nickname = DebugConfig.NICKNAME,
                    email = DebugConfig.EMAIL
                )
            }

            userRepository.login(
                id = DebugConfig.ID,
                password = DebugConfig.PASSWORD
            )

            viewModel.checkLogin()
        }
    }
}

private fun ComponentActivity.setStatusBarTransparent() {
    window.setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )
    WindowCompat.setDecorFitsSystemWindows(window, false)
}
