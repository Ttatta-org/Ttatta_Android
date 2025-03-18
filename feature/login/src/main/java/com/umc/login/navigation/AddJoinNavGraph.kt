package com.umc.login.navigation

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.umc.design.Primary300
import com.umc.login.LoginViewModel
import com.umc.login.R
import com.umc.login.component.AnimatedProgressBarProp
import com.umc.login.component.CustomTextFieldTextAlignment
import com.umc.login.component.form.CertificationCodeForm
import com.umc.login.component.form.EmailForm
import com.umc.login.component.form.IdForm
import com.umc.login.component.form.NameForm
import com.umc.login.component.form.NicknameForm
import com.umc.login.component.form.PasswordForm
import com.umc.login.logic.certification.CertificationCodeValidationRequestForJoin
import com.umc.login.logic.certification.CertificationMailRequestForJoin
import com.umc.login.logic.state.EmailValidationState
import com.umc.login.logic.state.IdValidationState
import com.umc.login.logic.state.NameValidationState
import com.umc.login.logic.state.NicknameValidationState
import com.umc.login.logic.state.PasswordValidationState
import com.umc.login.logic.state.isEmailValid
import com.umc.login.logic.state.isIdValid
import com.umc.login.logic.state.isNameValid
import com.umc.login.logic.state.isNicknameValid
import com.umc.login.logic.state.isPasswordValid
import com.umc.login.screen.FormScreen
import com.umc.login.screen.FormScreenDescriptionMessageProp
import com.umc.login.screen.JoinDoneScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalTime

private enum class JoinNavGraphDestination(
    val route: String,
    @StringRes val descriptionMessageId: Int,
    @StringRes val nextButtonOverMessageId: Int? = null,
    @StringRes val nextButtonLabelId: Int = R.string.next_button,
) {
    NICKNAME(
        route = "nickname",
        descriptionMessageId = R.string.join_nickname_description,
    ),
    ID(
        route = "id",
        descriptionMessageId = R.string.join_id_description,
    ),
    PASSWORD(
        route = "password",
        descriptionMessageId = R.string.join_password_description,
    ),
    NAME(
        route = "name",
        descriptionMessageId = R.string.join_name_description,
    ),
    EMAIL(
        route = "email",
        descriptionMessageId = R.string.join_email_description,
    ),
    CERTIFICATION(
        route = "certification",
        descriptionMessageId = R.string.join_certification_description,
        nextButtonOverMessageId = R.string.certification_mail_did_not_arrived,
        nextButtonLabelId = R.string.resend_email,
    ),
}

private const val totalStepCount = 6
private val startDestination = JoinNavGraphDestination.NICKNAME
private val emailDuration = Duration.parse("PT10M")

fun NavGraphBuilder.addJoinNavGraph(
    viewModel: LoginViewModel,
    onNavigatingBackToLogin: () -> Unit,
    onNavigatingToJoinDone: (name: String) -> Unit,
) {
    composable(
        route = "join"
    ) {
        val scope = rememberCoroutineScope()
        val navController = rememberNavController()
        var currentDestination by remember { mutableStateOf(startDestination) }

        LaunchedEffect(key1 = Unit) {
            navController.addOnDestinationChangedListener { _, destination, _ ->
                currentDestination = JoinNavGraphDestination.entries.find {
                    it.route == destination.route
                } ?: startDestination
            }
        }

        var nickname by remember { mutableStateOf("") }
        var id by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var name by remember { mutableStateOf("") }
        var emailLocal by remember { mutableStateOf("") }
        var emailDomain by remember { mutableStateOf("") }

        val nicknameValidationState = remember(nickname) { isNicknameValid(nickname) }
        var idValidationState by remember(id) { mutableStateOf(isIdValid(id)) }
        val passwordValidationState = remember(
            password,
            confirmPassword,
        ) { isPasswordValid(password, confirmPassword) }
        val nameValidationState = remember(name) { isNameValid(name) }
        val emailValidationState = remember(
            emailLocal, emailDomain
        ) { isEmailValid("$emailLocal@$emailDomain") }

        var emailSentTime by remember { mutableStateOf<LocalTime>(LocalTime.now()) }

        // 이메일 입력 창에서 이메일 입력 후 다음 버튼 클릭 시
        LaunchedEffect(key1 = emailSentTime) {
            Log.d("JoinNavGraph", "emailSentTime: $emailSentTime, currentDestination: $currentDestination")
            // if (currentDestination == JoinNavGraphDestination.EMAIL)
            //     navController.navigate(route = JoinNavGraphDestination.CERTIFICATION.route)
        }

        FormScreen(
            topLineMessage = null,
            nextButtonLabel = stringResource(id = currentDestination.nextButtonLabelId),
            nextButtonOverMessage = currentDestination.nextButtonOverMessageId?.let {
                stringResource(id = it)
            },
            formScreenDescriptionMessageProp = FormScreenDescriptionMessageProp(
                message = stringResource(id = currentDestination.descriptionMessageId),
                color = Color.Primary300,
            ),
            animatedProgressBarProp = AnimatedProgressBarProp(
                currentStep = JoinNavGraphDestination.entries.indexOf(currentDestination) + 1,
                totalSteps = totalStepCount
            ),
            isNextButtonEnabled = when (currentDestination) {
                JoinNavGraphDestination.NICKNAME -> nicknameValidationState == NicknameValidationState.VALID
                JoinNavGraphDestination.ID -> idValidationState == IdValidationState.VALID
                JoinNavGraphDestination.PASSWORD -> passwordValidationState == PasswordValidationState.VALID
                JoinNavGraphDestination.NAME -> nameValidationState == NameValidationState.VALID
                JoinNavGraphDestination.EMAIL -> emailValidationState == EmailValidationState.VALID
                JoinNavGraphDestination.CERTIFICATION -> true
            },
            isLogoVisible = true,
            onNextButtonClicked = {
                when (currentDestination) {
                    JoinNavGraphDestination.CERTIFICATION, JoinNavGraphDestination.EMAIL -> run {
                        viewModel.requestCertificationMail(
                            request = CertificationMailRequestForJoin(email = "$emailLocal@$emailDomain"),
                            onSucceed = {
                                emailSentTime = LocalTime.now()
                                if (currentDestination == JoinNavGraphDestination.EMAIL) scope.launch {
                                    withContext(Dispatchers.Main) {
                                        navController.navigate(route = JoinNavGraphDestination.CERTIFICATION.route)
                                    }
                                }
                            },
                        )
                    }
                    else -> run {
                        val index = JoinNavGraphDestination.entries.indexOf(currentDestination)
                        navController.navigate(JoinNavGraphDestination.entries[index + 1].route)
                    }
                }
            },
            onBackButtonClicked = {
                if (!navController.popBackStack()) onNavigatingBackToLogin()
            },
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination.route,
            ) {
                composable(
                    route = JoinNavGraphDestination.NICKNAME.route
                ) {
                    NicknameForm(
                        nickname = nickname,
                        state = nicknameValidationState,
                        onNicknameChanged = { nickname = it },
                    )
                }

                composable(
                    route = JoinNavGraphDestination.ID.route
                ) {
                    IdForm(
                        id = id,
                        state = idValidationState,
                        onIdChanged = { id = it },
                        onDuplicationCheckButtonClicked = {
                            viewModel.checkIdDuplication(
                                id = id,
                                onSucceed = { isDuplicated ->
                                    idValidationState = if (isDuplicated) IdValidationState.DUPLICATED
                                    else IdValidationState.VALID
                                },
                            )
                        },
                    )
                }

                composable(
                    route = JoinNavGraphDestination.PASSWORD.route
                ) {
                    var isPasswordVisible by remember { mutableStateOf(false) }
                    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

                    val isConfirmPasswordFieldShowing = remember(passwordValidationState) {
                        passwordValidationState in setOf(
                            PasswordValidationState.VALID,
                            PasswordValidationState.NOT_MATCH,
                            PasswordValidationState.CONFIRM_PASSWORD_NOT_ENTERED,
                        )
                    }

                    PasswordForm(
                        password = password,
                        confirmPassword = confirmPassword,
                        state = passwordValidationState,
                        passwordPlaceholder = stringResource(id = R.string.password),
                        confirmPasswordPlaceholder = stringResource(id = R.string.password_check),
                        textAlignment = CustomTextFieldTextAlignment.CENTER,
                        isPasswordVisible = isPasswordVisible,
                        isConfirmPasswordVisible = isConfirmPasswordVisible,
                        isConfirmPasswordFieldShowing = isConfirmPasswordFieldShowing,
                        onPasswordChanged = { password = it },
                        onConfirmPasswordChanged = { confirmPassword = it },
                        onPasswordVisibilityChanged = { isPasswordVisible = it },
                        onConfirmPasswordVisibilityChanged = { isConfirmPasswordVisible = it },
                    )
                }

                composable(
                    route = JoinNavGraphDestination.NAME.route
                ) {
                    NameForm(
                        name = name,
                        state = nameValidationState,
                        onNameChanged = { name = it },
                    )
                }

                composable(
                    route = JoinNavGraphDestination.EMAIL.route
                ) {
                    EmailForm(
                        local = emailLocal,
                        domain = emailDomain,
                        state = emailValidationState,
                        onLocalChanged = { emailLocal = it },
                        onDomainChanged = { emailDomain = it },
                    )
                }

                composable(
                    route = JoinNavGraphDestination.CERTIFICATION.route
                ) {
                    var code by remember { mutableStateOf("") }
                    var remainTime by remember { mutableStateOf(emailDuration) }

                    LaunchedEffect(key1 = Unit) {
                        while (true) {
                            remainTime = emailDuration - Duration.between(emailSentTime, LocalTime.now())
                            if (remainTime.seconds <= 0) navController.popBackStack()
                            delay(500L)
                        }
                    }

                    LaunchedEffect(key1 = code) {
                        code.let { code ->
                            if (code.length == 6 && code.isDigitsOnly()) viewModel.requestCertificationCodeValidation(
                                request = CertificationCodeValidationRequestForJoin(
                                    email = "$emailLocal@$emailDomain",
                                    code = code,
                                ),
                                onSucceed = { isValid ->
                                    if (isValid) viewModel.join(
                                        nickname = nickname,
                                        id = id,
                                        password = password,
                                        name = name,
                                        email = "$emailLocal@$emailDomain",
                                        onSucceed = { onNavigatingToJoinDone(name) },
                                    )
                                },
                            )
                        }
                    }

                    CertificationCodeForm(
                        code = code,
                        remainTime = remainTime,
                        onCodeChanged = { code = it },
                    )
                }
            }
        }
    }

    composable(
        route = "join_done?name={name}",
        arguments = listOf(
            navArgument("name") { type = NavType.StringType }
        ),
    ) { backStackEntry ->
        val name = backStackEntry.arguments!!.getString("name")!!

        JoinDoneScreen(
            name = name,
            onBackToLoginButtonClicked = onNavigatingBackToLogin,
        )
    }
}