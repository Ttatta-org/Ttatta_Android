package com.umc.login.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.umc.design.Grey400
import com.umc.login.LoginViewModel
import com.umc.login.R
import com.umc.login.component.form.CertificationForm
import com.umc.login.logic.certification.CertificationCodeValidationRequestForFindingId
import com.umc.login.logic.certification.CertificationMailRequestForFindingId
import com.umc.login.screen.FindingIdDoneScreen
import com.umc.login.screen.FormScreen
import com.umc.login.screen.FormScreenDescriptionMessageProp
import kotlinx.coroutines.delay
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
        var name by remember { mutableStateOf("") }
        var emailLocal by remember { mutableStateOf("") }
        var emailDomain by remember { mutableStateOf("") }
        var certificationCode by remember { mutableStateOf("") }

        var isCertificateButtonEnabled by remember { mutableStateOf(false) }
        var startTime by remember { mutableStateOf<LocalTime?>(null) }
        var remainTime by remember { mutableStateOf<Duration?>(null) }

        var isCertificationValid by remember { mutableStateOf(false) }

        LaunchedEffect(key1 = Unit) {
            while (true) {
                remainTime = startTime?.let { Duration.parse("PT10M") - Duration.between(it, LocalTime.now()) }
                if (remainTime?.isNegative == true) {
                    startTime = null
                    remainTime = null
                    isCertificateButtonEnabled = false
                }
                delay(200L)
            }
        }

        FormScreen(
            topLineMessage = stringResource(id = R.string.find_id),
            nextButtonLabel = stringResource(id = R.string.find_id),
            nextButtonOverMessage = null,
            formScreenDescriptionMessageProp = FormScreenDescriptionMessageProp(
                message = stringResource(id = R.string.find_form_description),
                color = Color.Grey400,
            ),
            animatedProgressBarProp = null,
            isNextButtonEnabled = isCertificationValid,
            isLogoVisible = false,
            onNextButtonClicked = {
                viewModel.findId(
                    onSucceed = { id, name ->
                        onNavigatingToFindingIdDone(id, name)
                    }
                )
            },
            onBackButtonClicked = onNavigatingBackToLogin,
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
                onCodeChanged = { certificationCode = it },
                onSendCodeButtonClicked = {
                    viewModel.requestCertificationMail(
                        request = CertificationMailRequestForFindingId(
                            email = "$emailLocal@$emailDomain",
                            name = name,
                        ),
                        onSucceed = {
                            startTime = LocalTime.now()
                            isCertificateButtonEnabled = true
                        }
                    )
                },
                onCertificateButtonClicked = {
                    viewModel.requestCertificationCodeValidation(
                        request = CertificationCodeValidationRequestForFindingId(
                            email = "$emailLocal@$emailDomain",
                            code = certificationCode,
                        ),
                        onSucceed = { isValid: Boolean ->
                            isCertificationValid = isValid
                        }
                    )
                },
            )
        }
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

        FindingIdDoneScreen(
            id = id,
            name = name,
            onBackToLoginButtonClicked = onNavigatingBackToLogin,
            onGoToFindPasswordButtonClicked = onNavigatingToFindingPassword,
        )
    }
}