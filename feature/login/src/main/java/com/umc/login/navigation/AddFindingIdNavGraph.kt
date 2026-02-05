package com.umc.login.navigation

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.umc.core.util.runWithScope
import com.umc.design.component.CustomPopup
import com.umc.design.component.LoadingModal
import com.umc.design.theme.LocalColorTheme
import com.umc.login.LoginViewModel
import com.umc.login.R
import com.umc.login.component.EmailDomainDropdown
import com.umc.login.model.prop.EmailDomainDropdownItemProp
import com.umc.login.component.emailDomains
import com.umc.login.component.form.CertificationForm
import com.umc.login.logic.certification.CertificationCodeValidationRequestForFindingId
import com.umc.login.logic.certification.CertificationMailRequestForFindingId
import com.umc.login.screen.DoneScreen
import com.umc.login.screen.FormScreen
import com.umc.login.screen.FormScreenDescriptionMessageProp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime

fun NavGraphBuilder.addFindingIdNavGraph(
    viewModel: LoginViewModel,
    onNavigatingBackToLogin: () -> Unit,
    onNavigatingToFindingIdDone: (id: String, name: String) -> Unit,
    onNavigatingToFindingPassword: () -> Unit,
) {
    composable(
        route = "find_id"
    ) {
        val context = LocalContext.current

        var name by remember { mutableStateOf("") }
        var emailLocal by remember { mutableStateOf("") }
        var emailDomain by remember { mutableStateOf("") }
        var certificationCode by remember { mutableStateOf("") }

        var startTime by remember { mutableStateOf<LocalTime?>(null) }
        var remainTime by remember { mutableStateOf<Duration?>(null) }

        var isEmailRequested by remember { mutableStateOf(false) }

        var isEmailDomainDropdownExpanded by remember { mutableStateOf(false) }
        var screenLayoutCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
        var formLayoutCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
        var emailDropdownButtonCenterOffset by remember { mutableStateOf(Offset.Zero) }

        var showLoading by remember { mutableStateOf(false) }
        var showCannotSendMailPopup by remember { mutableStateOf(false) }

        LaunchedEffect(key1 = Unit) {
            while (true) {
                remainTime = startTime?.let {
                    Duration.parse("PT10M") - Duration.between(
                        it, LocalTime.now()
                    )
                }
                if (remainTime?.isNegative == true) {
                    startTime = null
                    remainTime = null
                }
                delay(200L)
            }
        }

        LaunchedEffect(key1 = certificationCode) {
            if (certificationCode.length != 6 || !certificationCode.isDigitsOnly()) return@LaunchedEffect

            viewModel.runWithScope {
                showLoading = true
                isEmailDomainDropdownExpanded = false

                val isValid = runCatching {
                    requestCertificationCodeValidation(
                        request = CertificationCodeValidationRequestForFindingId(
                            email = "$emailLocal@$emailDomain",
                            code = certificationCode,
                        ),
                    )
                }.getOrNull()

                if (isValid == true) {
                    val (id, name) = viewModel.findId()
                    MainScope().launch { onNavigatingToFindingIdDone(id, name) }
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

        Box(
            modifier = Modifier
                .onGloballyPositioned { screenLayoutCoordinates = it }
                .let {
                    if (isEmailDomainDropdownExpanded) it.clickable(
                        indication = null,
                        interactionSource = null,
                        onClick = { isEmailDomainDropdownExpanded = false }
                    ) else it
                }
        ) {
            FormScreen(
                topLineMessage = stringResource(id = R.string.find_id),
                nextButtonLabel = if (isEmailRequested) stringResource(R.string.resend_email) else stringResource(R.string.send_email),
                nextButtonOverMessage = null,
                formScreenDescriptionMessageProp = FormScreenDescriptionMessageProp(
                    message = stringResource(id = R.string.find_id_description),
                    color = LocalColorTheme.current.grey[700],
                    content = {
                        Text(
                            text = stringResource(R.string.find_form_description),
                            color = LocalColorTheme.current.grey[600],
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.W400,
                            fontSize = 14.sp,
                        )
                    }
                ),
                animatedProgressBarProp = null,
                isNextButtonEnabled = isEmailRequested || (emailLocal.isNotBlank() && emailDomain.isNotBlank() && name.isNotBlank()),
                isLogoVisible = false,
                onNextButtonClicked = {
                    viewModel.runWithScope {
                        showLoading = true

                        runCatching {
                            requestCertificationMail(
                                request = CertificationMailRequestForFindingId(
                                    email = "$emailLocal@$emailDomain",
                                    name = name,
                                ),
                            )
                        }.onSuccess { isSucceed ->
                            if (isSucceed) {
                                isEmailRequested = true
                                startTime = LocalTime.now()
                            } else {
                                showCannotSendMailPopup = true
                            }
                        }

                        showLoading = false
                    }
                },
                onBackButtonClicked = onNavigatingBackToLogin,
            ) {
                Box(
                    modifier = Modifier.onGloballyPositioned { formLayoutCoordinates = it },
                ) {
                    CertificationForm(
                        name = name,
                        local = emailLocal,
                        domain = emailDomain,
                        code = certificationCode,
                        remainTime = remainTime,
                        isEditable = true,
                        isCodeFieldVisible = isEmailRequested,
                        onNameChanged = { name = it },
                        onLocalChanged = { emailLocal = it },
                        onDomainChanged = { emailDomain = it },
                        onDomainDropdownExpandedChanged = {
                            isEmailDomainDropdownExpanded = !isEmailDomainDropdownExpanded
                        },
                        onDomainDropdownButtonCenterOffsetCalculated = {
                            emailDropdownButtonCenterOffset = it
                        },
                        onCodeChanged = { if (it.length <= 6) certificationCode = it },
                    )
                }
            }

            if (isEmailDomainDropdownExpanded) {
                var dropdownWidth by remember { mutableIntStateOf(0) }
                val screen = screenLayoutCoordinates
                val form = formLayoutCoordinates

                if (screen != null && form != null) Box(
                    modifier = Modifier
                        .offset {
                            screen
                                .localPositionOf(form)
                                .plus(emailDropdownButtonCenterOffset)
                                .plus(
                                    Offset(
                                        x = 16.dp.toPx() - dropdownWidth.toFloat(),
                                        y = 16.dp.toPx(),
                                    )
                                )
                                .round()
                        }
                        .onSizeChanged { dropdownWidth = it.width },
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

        if (showCannotSendMailPopup) CustomPopup(
            title = stringResource(id = R.string.error_title_cannot_find_user),
            message = stringResource(id = R.string.error_content_cannot_find_user),
            cancelText = "확인",
            onDismiss = { showCannotSendMailPopup = false },
        )
    }

    composable(
        route = "find_id_done?id={id}&name={name}",
        arguments = listOf(
            navArgument("id") { type = NavType.StringType },
            navArgument("name") { type = NavType.StringType },
        ),
    ) { backStackEntry ->
        val id = backStackEntry.arguments!!.getString("id")!!
        val name = backStackEntry.arguments!!.getString("name")!!

        DoneScreen(
            nickname = name,
            message = "아이디 찾기\n완료!",
            centerChipContent = id,
            onGoToFindingPasswordButtonClicked = onNavigatingToFindingPassword,
            onBackButtonClicked = onNavigatingBackToLogin,
            onGoToLoginButtonClicked = onNavigatingBackToLogin,
        )
    }
}