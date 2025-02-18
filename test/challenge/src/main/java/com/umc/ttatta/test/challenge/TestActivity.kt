package com.umc.ttatta.test.challenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.rememberCoroutineScope
import com.umc.challenge.ChallengeViewModel
import com.umc.challenge.ChallengeApp
import com.umc.core.repository.ChallengeRepository
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
    @Inject
    lateinit var challengeRepository: ChallengeRepository

    private val viewModel: ChallengeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prepareTest()

        setContent {
            val scope = rememberCoroutineScope()

            ChallengeApp(
                viewModel = viewModel,
                onNavigationBarVisibilityChanged = {},
                onChallengeCompletionRequired = { id, onSuccess ->
                    scope.launch {
                        challengeRepository.completeChallenge(id = id)
                        onSuccess()
                    }
                }
            )
        }
    }

    private fun prepareTest() {
        CoroutineScope(Dispatchers.Main).launch {
            if (!userRepository.isIdAlreadyOccupied(id = TestValues.ID)) {
                userRepository.join(
                    id = TestValues.ID,
                    password = TestValues.PASSWORD,
                    name = TestValues.NAME,
                    nickname = TestValues.NICKNAME,
                    email = TestValues.EMAIL,
                )
            }

            userRepository.login(
                id = TestValues.ID,
                password = TestValues.PASSWORD,
            )
        }
    }
}