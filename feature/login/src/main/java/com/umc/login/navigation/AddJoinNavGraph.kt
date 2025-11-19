package com.umc.login.navigation

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.umc.core.util.runWithScope
import com.umc.design.component.CustomPopup
import com.umc.design.component.LoadingModal
import com.umc.design.theme.LocalColorTheme
import com.umc.login.LoginViewModel
import com.umc.login.R
import com.umc.login.component.AnimatedProgressBarProp
import com.umc.login.component.CustomTextFieldTextAlignment
import com.umc.login.component.EmailDomainDropdown
import com.umc.login.component.EmailDomainDropdownItemProp
import com.umc.login.component.emailDomains
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
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        rememberCoroutineScope()
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

        var screenLayoutCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
        var emailFormLayoutCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
        var emailDropdownButtonCenterOffset by remember { mutableStateOf(Offset.Zero) }
        var isEmailDomainDropdownExpanded by remember { mutableStateOf(false) }

        var showLoading by remember { mutableStateOf(false) }
        var showEmailDuplicatedPopup by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier.onGloballyPositioned { screenLayoutCoordinates = it },
        ) {
            FormScreen(
                topLineMessage = stringResource(id = R.string.join),
                nextButtonLabel = stringResource(id = currentDestination.nextButtonLabelId),
                nextButtonOverMessage = currentDestination.nextButtonOverMessageId?.let {
                    stringResource(id = it)
                },
                formScreenDescriptionMessageProp = FormScreenDescriptionMessageProp(
                    message = stringResource(id = currentDestination.descriptionMessageId),
                    color = LocalColorTheme.current.grey[700],
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
                isLogoVisible = false,
                onNextButtonClicked = {
                    when (currentDestination) {
                        JoinNavGraphDestination.CERTIFICATION, JoinNavGraphDestination.EMAIL -> run {
                            viewModel.runWithScope {
                                showLoading = true

                                runCatching {
                                    requestCertificationMail(
                                        request = CertificationMailRequestForJoin(
                                            email = "${emailLocal.trim()}@${emailDomain.trim()}"
                                        ),
                                    )
                                }.onSuccess { isSucceed ->
                                    if (isSucceed) {
                                        emailSentTime = LocalTime.now()
                                        if (currentDestination == JoinNavGraphDestination.EMAIL) MainScope().launch {
                                            navController.navigate(route = JoinNavGraphDestination.CERTIFICATION.route)
                                        }
                                    } else {
                                        showEmailDuplicatedPopup = true
                                    }
                                }

                                showLoading = false
                            }
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
                            onNicknameChanged = { if (it.length <= 9) nickname = it },
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
                                if (idValidationState == IdValidationState.DID_NOT_CHECKED_DUPLICATED) {
                                    viewModel.runWithScope {
                                        showLoading = true

                                        runCatching {
                                            val isDuplicated = checkIdDuplication(id = id)

                                            idValidationState = if (isDuplicated)
                                                IdValidationState.DUPLICATED
                                            else
                                                IdValidationState.VALID
                                        }

                                        showLoading = false
                                    }
                                }
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
                            textAlignment = CustomTextFieldTextAlignment.START,
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
                        Box(
                            modifier = Modifier.onGloballyPositioned {
                                emailFormLayoutCoordinates = it
                            },
                        ) {
                            EmailForm(
                                local = emailLocal,
                                domain = emailDomain,
                                state = emailValidationState,
                                onLocalChanged = { emailLocal = it },
                                onDomainChanged = { emailDomain = it },
                                onDomainDropdownExpandedChanged = {
                                    isEmailDomainDropdownExpanded = !isEmailDomainDropdownExpanded
                                },
                                onDomainDropdownButtonCenterOffsetCalculated = {
                                    emailDropdownButtonCenterOffset = it
                                },
                            )
                        }
                    }

                    composable(
                        route = JoinNavGraphDestination.CERTIFICATION.route
                    ) {
                        val context = LocalContext.current
                        var code by remember { mutableStateOf("") }
                        var remainTime by remember { mutableStateOf(emailDuration) }

                        LaunchedEffect(key1 = Unit) {
                            while (true) {
                                remainTime = emailDuration - Duration.between(
                                    emailSentTime, LocalTime.now()
                                )
                                if (remainTime.seconds <= 0)
                                    navController.popBackStack()
                                delay(500L)
                            }
                        }

                        LaunchedEffect(key1 = code) {
                            code.let { code ->
                                if (code.length == 6 && code.isDigitsOnly()) viewModel.runWithScope {
                                    showLoading = true

                                    val isValid = runCatching {
                                        requestCertificationCodeValidation(
                                            request = CertificationCodeValidationRequestForJoin(
                                                email = "$emailLocal@$emailDomain",
                                                code = code,
                                            ),
                                        )
                                    }.getOrDefault(null)

                                    if (isValid == true) {
                                        runCatching {
                                            viewModel.join(
                                                nickname = nickname,
                                                id = id,
                                                password = password,
                                                name = name,
                                                email = "$emailLocal@$emailDomain",
                                            )
                                        }.onSuccess {
                                            onNavigatingToJoinDone(name)
                                        }
                                    } else if (isValid == false) {
                                        MainScope().launch {
                                            val toast = Toast.makeText(
                                                context,
                                                "인증번호가 올바르지 않습니다.",
                                                Toast.LENGTH_SHORT,
                                            )

                                            toast.show()
                                        }
                                    }

                                    showLoading = false
                                }
                            }
                        }

                        CertificationCodeForm(
                            code = code,
                            remainTime = remainTime,
                            onCodeChanged = { if (it.length <= 6) code = it },
                        )
                    }
                }
            }

            if (isEmailDomainDropdownExpanded) {
                var dropdownWidth by remember { mutableIntStateOf(0) }
                val screen = screenLayoutCoordinates
                val emailForm = emailFormLayoutCoordinates

                if (screen != null && emailForm != null) Box(
                    modifier = Modifier
                        .offset {
                            screen
                                .localPositionOf(emailForm)
                                .plus(emailDropdownButtonCenterOffset)
                                .plus(Offset(x = -dropdownWidth.toFloat(), y = 16.dp.toPx()))
                                .round()
                        }
                        .onSizeChanged { dropdownWidth = it.width / 2 },
                ) {
                    EmailDomainDropdown(
                        props = emailDomains.map {
                            EmailDomainDropdownItemProp(
                                domain = it,
                                onClicked = {
                                    emailDomain = it
                                    isEmailDomainDropdownExpanded = false
                                },
                            )
                        },
                    )
                }
            }
        }

        if (showLoading) LoadingModal()

        if (showEmailDuplicatedPopup) CustomPopup(
            onDismiss = { showEmailDuplicatedPopup = false },
            title = stringResource(id = R.string.error_title_email_duplicated),
            message = stringResource(id = R.string.error_content_email_duplicated),
            cancelText = "확인",
        )
    }

    composable(
        route = "join_done?name={name}",
        arguments = listOf(
            navArgument("name") { type = NavType.StringType },
        ),
    ) { backStackEntry ->
        val name = backStackEntry.arguments!!.getString("name")!!

        JoinDoneScreen(
            name = name,
            onBackToLoginButtonClicked = onNavigatingBackToLogin,
        )
    }
}