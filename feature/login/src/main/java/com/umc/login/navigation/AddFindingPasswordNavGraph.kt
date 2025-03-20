package com.umc.login.navigation

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.umc.design.Grey400
import com.umc.login.LoginViewModel
import com.umc.login.R
import com.umc.login.component.CustomTextField
import com.umc.login.component.CustomTextFieldProp
import com.umc.login.component.CustomTextFieldTextAlignment
import com.umc.login.component.EmailDomainDropdown
import com.umc.login.component.EmailDomainDropdownItemProp
import com.umc.login.component.emailDomains
import com.umc.login.component.form.CertificationForm
import com.umc.login.component.form.PasswordForm
import com.umc.login.logic.certification.CertificationCodeValidationRequestForFindingPassword
import com.umc.login.logic.certification.CertificationMailRequestForFindingPassword
import com.umc.login.logic.state.IdValidationState
import com.umc.login.logic.state.PasswordValidationState
import com.umc.login.logic.state.isIdValid
import com.umc.login.logic.state.isPasswordValid
import com.umc.login.screen.FormScreen
import com.umc.login.screen.FormScreenDescriptionMessageProp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalTime

private enum class FindingPasswordNavGraphDestination(
    val route: String,
    @StringRes val topLineMessageId: Int,
    @StringRes val descriptionMessageId: Int,
    @StringRes val nextButtonLabelId: Int,
) {
    ID(
        route = "id",
        topLineMessageId = R.string.find_password,
        descriptionMessageId = R.string.find_password_id_description,
        nextButtonLabelId = R.string.next_button,
    ),
    CERTIFICATION(
        route = "certification",
        topLineMessageId = R.string.find_password,
        descriptionMessageId = R.string.find_form_description,
        nextButtonLabelId = R.string.find_password
    ),
    RESET_PASSWORD(
        route = "reset_password",
        topLineMessageId = R.string.reset_password,
        descriptionMessageId = R.string.find_password_reset_password_description,
        nextButtonLabelId = R.string.go_to_login
    )
}

private val startDestination = FindingPasswordNavGraphDestination.ID

fun NavGraphBuilder.addFindingPasswordNavGraph(
    viewModel: LoginViewModel,
    onNavigatingBackToLogin: () -> Unit,
) {
    composable(
        route = "find_password"
    ) {
        val scope = rememberCoroutineScope()
        val navController = rememberNavController()
        var currentDestination by remember { mutableStateOf(startDestination) }

        LaunchedEffect(key1 = Unit) {
            navController.addOnDestinationChangedListener { _, destination, _ ->
                currentDestination = FindingPasswordNavGraphDestination.entries.find {
                    it.route == destination.route
                } ?: startDestination
            }
        }

        var id by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var isCertificationValid by remember { mutableStateOf(false) }
        var isResetPasswordValid by remember { mutableStateOf(false) }

        var emailDomain by remember { mutableStateOf("") }

        var isEmailDomainDropdownExpanded by remember { mutableStateOf(false) }
        var screenLayoutCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
        var formLayoutCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
        var emailDropdownButtonCenterOffset by remember { mutableStateOf<Offset>(Offset.Zero) }

        Box(
            modifier = Modifier.onGloballyPositioned { screenLayoutCoordinates = it },
        ) {
            FormScreen(
                topLineMessage = stringResource(id = currentDestination.topLineMessageId),
                nextButtonLabel = stringResource(id = currentDestination.nextButtonLabelId),
                nextButtonOverMessage = null,
                formScreenDescriptionMessageProp = FormScreenDescriptionMessageProp(
                    message = stringResource(id = currentDestination.descriptionMessageId),
                    color = Color.Grey400,
                ),
                animatedProgressBarProp = null,
                isNextButtonEnabled = when (currentDestination) {
                    FindingPasswordNavGraphDestination.ID -> isIdValid(id = id) == IdValidationState.DID_NOT_CHECKED_DUPLICATED
                    FindingPasswordNavGraphDestination.CERTIFICATION -> isCertificationValid
                    FindingPasswordNavGraphDestination.RESET_PASSWORD -> isResetPasswordValid
                },
                isLogoVisible = false,
                onNextButtonClicked = lambda@{
                    if (currentDestination == FindingPasswordNavGraphDestination.ID) {
                        viewModel.checkIdExist(
                            id = id,
                            onSucceed = { isExist ->
                                if (isExist) scope.launch {
                                    withContext(context = Dispatchers.Main) {
                                        navController.navigate(FindingPasswordNavGraphDestination.CERTIFICATION.route)
                                    }
                                }
                            },
                        )
                        return@lambda
                    }

                    if (currentDestination == FindingPasswordNavGraphDestination.RESET_PASSWORD) {
                        viewModel.changePassword(
                            password = password,
                            onSucceed = onNavigatingBackToLogin,
                        )
                        return@lambda
                    }

                    val index = FindingPasswordNavGraphDestination.entries.indexOf(
                        currentDestination
                    )
                    navController.navigate(FindingPasswordNavGraphDestination.entries[index + 1].route)
                },
                onBackButtonClicked = {
                    if (!navController.popBackStack()) onNavigatingBackToLogin()
                },
            ) {
                NavHost(
                    navController = navController,
                    startDestination = "id",
                ) {
                    composable(
                        route = FindingPasswordNavGraphDestination.ID.route
                    ) {
                        CustomTextField(
                            prop = CustomTextFieldProp(
                                value = id,
                                onValueChanged = { id = it },
                                placeholder = stringResource(id = R.string.id),
                            )
                        )
                    }

                    composable(
                        route = FindingPasswordNavGraphDestination.CERTIFICATION.route
                    ) {
                        var name by remember { mutableStateOf("") }
                        var emailLocal by remember { mutableStateOf("") }
                        var certificationCode by remember { mutableStateOf("") }

                        var isCertificateButtonEnabled by remember { mutableStateOf(false) }
                        var emailSentTime by remember { mutableStateOf<LocalTime?>(null) }
                        var remainTime by remember { mutableStateOf<Duration?>(null) }

                        LaunchedEffect(key1 = Unit) {
                            while (true) {
                                remainTime = emailSentTime?.let {
                                    Duration.parse("PT10M") - Duration.between(it, LocalTime.now())
                                }
                                if (remainTime?.isNegative == true) {
                                    emailSentTime = null
                                    remainTime = null
                                    isCertificateButtonEnabled = false
                                }
                                delay(200L)
                            }
                        }

                        Box(
                            modifier = Modifier.onGloballyPositioned { formLayoutCoordinates = it },
                        ) {
                            CertificationForm(
                                name = name,
                                local = emailLocal,
                                domain = emailDomain,
                                code = certificationCode,
                                remainTime = remainTime,
                                isCertificateButtonEnabled = isCertificateButtonEnabled,
                                isEditable = !isCertificationValid,
                                onNameChanged = { name = it },
                                onLocalChanged = { emailLocal = it },
                                onDomainChanged = { emailDomain = it },
                                onDomainDropdownExpandedChanged = { isEmailDomainDropdownExpanded = !isEmailDomainDropdownExpanded },
                                onDomainDropdownButtonCenterOffsetCalculated = { emailDropdownButtonCenterOffset = it },
                                onCodeChanged = { certificationCode = it },
                                onSendCodeButtonClicked = {
                                    viewModel.requestCertificationMail(
                                        request = CertificationMailRequestForFindingPassword(
                                            email = "${emailLocal}@${emailDomain}",
                                            id = id,
                                            name = name,
                                        ),
                                        onSucceed = {
                                            emailSentTime = LocalTime.now()
                                            isCertificateButtonEnabled = true
                                        },
                                    )
                                },
                                onCertificateButtonClicked = {
                                    viewModel.requestCertificationCodeValidation(
                                        request = CertificationCodeValidationRequestForFindingPassword(
                                            email = "${emailLocal}@${emailDomain}",
                                            code = certificationCode,
                                        ),
                                        onSucceed = { isValid: Boolean ->
                                            isCertificationValid = isValid
                                        },
                                    )
                                },
                            )
                        }
                    }

                    composable(
                        route = FindingPasswordNavGraphDestination.RESET_PASSWORD.route
                    ) {
                        var isPasswordVisible by remember { mutableStateOf(false) }
                        var isConfirmPasswordVisible by remember { mutableStateOf(false) }

                        val state = remember(password, confirmPassword) {
                            isPasswordValid(password, confirmPassword).also {
                                isResetPasswordValid = it == PasswordValidationState.VALID
                            }
                        }

                        PasswordForm(
                            password = password,
                            confirmPassword = confirmPassword,
                            state = state,
                            passwordPlaceholder = stringResource(id = R.string.new_password_placeholder),
                            confirmPasswordPlaceholder = stringResource(id = R.string.new_password_confirm_placeholder),
                            textAlignment = CustomTextFieldTextAlignment.START,
                            isPasswordVisible = isPasswordVisible,
                            isConfirmPasswordVisible = isConfirmPasswordVisible,
                            isConfirmPasswordFieldShowing = true,
                            onPasswordChanged = { password = it },
                            onConfirmPasswordChanged = { confirmPassword = it },
                            onPasswordVisibilityChanged = { isPasswordVisible = it },
                            onConfirmPasswordVisibilityChanged = { isConfirmPasswordVisible = it },
                        )
                    }
                }
            }

            if (isEmailDomainDropdownExpanded) {
                var dropdownWidth by remember { mutableIntStateOf(0) }
                val screen = screenLayoutCoordinates
                val form = formLayoutCoordinates

                if (screen != null && form != null) Box(
                    modifier = Modifier
                        .offset {
                            screen.localPositionOf(form)
                                .plus(emailDropdownButtonCenterOffset)
                                .plus(Offset(x = -dropdownWidth.toFloat(), y = 16.dp.toPx()))
                                .round()
                        }
                        .onSizeChanged { dropdownWidth = it.width  / 2 },
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
    }
}