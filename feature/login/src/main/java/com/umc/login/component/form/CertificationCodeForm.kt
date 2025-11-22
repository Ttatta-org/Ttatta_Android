package com.umc.login.component.form

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.Primary500
import com.umc.design.theme.ThemeProvider
import com.umc.login.R
import com.umc.login.component.CustomTextField
import com.umc.login.component.CustomTextFieldProp
import java.time.Duration

@Composable
fun CertificationCodeForm(
    code: String,
    remainTime: Duration,
    onCodeChanged: (String) -> Unit,
) {
    CustomTextField(
        prop = CustomTextFieldProp(
            value = code,
            onValueChanged = onCodeChanged,
            placeholder = stringResource(id = R.string.certification_placeholder),
            keyboardType = KeyboardType.Number,
            tail = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(32.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "%02d:%02d".format(remainTime.seconds / 60, remainTime.seconds % 60),
                        color = Color.Primary500,
                        fontSize = 12.sp
                    )
                }
            },
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewCertificationCodeForm() {
    ThemeProvider {
        CertificationCodeForm(
            code = "",
            remainTime = Duration.parse("PT3M12S"),
            onCodeChanged = {},
        )
    }
}