package com.umc.login.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.umc.design.Primary300
import com.umc.login.LoginViewModel
import com.umc.login.R
import com.umc.login.component.form.NicknameForm
import com.umc.login.logic.state.NicknameValidationState
import com.umc.login.logic.state.isNicknameValid
import com.umc.login.screen.FormScreen
import com.umc.login.screen.FormScreenDescriptionMessageProp

data class KakaoJoinEvent(
    val idToken: String,
)

fun NavGraphBuilder.addKakaoLoginNavGraph(
    viewModel: LoginViewModel,
    onNavigatingToHome: () -> Unit,
    onNavigatingBackToLogin: () -> Unit,
) {
    composable(
        route = "kakao_login"
    ) {
        val context = LocalContext.current
        val kakaoInstance = UserApiClient.instance

        var kakaoJoinEvent by remember { mutableStateOf<KakaoJoinEvent?>(null) }

        LaunchedEffect(key1 = Unit) {
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    onNavigatingBackToLogin()
                } else token?.idToken?.let {
                    viewModel.tryLoginWithKakaoOpenIdToken(
                        idToken = it,
                        onSucceed = { isLoggedIn: Boolean ->
                            if (isLoggedIn) onNavigatingToHome()
                            else kakaoJoinEvent = KakaoJoinEvent(idToken = it)
                        },
                        onFailed = {
                            onNavigatingBackToLogin()
                        },
                    )
                }
            }

            if (kakaoInstance.isKakaoTalkLoginAvailable(context = context)) {
                kakaoInstance.loginWithKakaoTalk(context = context, callback = callback)
            } else {
                kakaoInstance.loginWithKakaoAccount(context = context, callback = callback)
            }
        }

        kakaoJoinEvent?.let { event ->
            var nickname by remember { mutableStateOf("") }
            val state = remember(nickname) { isNicknameValid(nickname) }

            FormScreen(
                topLineMessage = null,
                nextButtonLabel = stringResource(id = R.string.start),
                nextButtonOverMessage = null,
                formScreenDescriptionMessageProp = FormScreenDescriptionMessageProp(
                    message = stringResource(id = R.string.join_nickname_description),
                    color = Color.Primary300,
                ),
                animatedProgressBarProp = null,
                isNextButtonEnabled = state == NicknameValidationState.VALID,
                isLogoVisible = true,
                onNextButtonClicked = {
                    viewModel.sendInfosForKakaoJoin(
                        idToken = event.idToken,
                        nickname = nickname,
                        onSucceed = onNavigatingToHome,
                    )
                },
                onBackButtonClicked = onNavigatingBackToLogin,
            ) {
                NicknameForm(
                    nickname = nickname,
                    state = state,
                    onNicknameChanged = { nickname = it },
                )
            }
        }
    }
}