package com.umc.login.component.form

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider
import com.umc.login.R
import com.umc.login.component.CustomTextField
import com.umc.login.component.CustomTextFieldLabelScope
import com.umc.login.component.CustomTextFieldProp
import com.umc.login.component.CustomTextFieldTextAlignment
import com.umc.login.component.toCustomTextFieldUnderMessageProp
import com.umc.login.logic.state.IdValidationState

@Composable
fun IdForm(
    id: String,
    state: IdValidationState,
    onIdChanged: (String) -> Unit,
    onDuplicationCheckButtonClicked: () -> Unit,
) {
    CustomTextFieldLabelScope(
        underMessageProp = state.message?.toCustomTextFieldUnderMessageProp(),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                CustomTextField(
                    prop = CustomTextFieldProp(
                        value = id,
                        onValueChanged = onIdChanged,
                        placeholder = stringResource(id = R.string.id),
                        textAlignment = CustomTextFieldTextAlignment.START,
                        tail = {
                            if (id.isNotEmpty()) ClearButton(onClick = { onIdChanged("") })
                        }
                    )
                )
            }
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = LocalColorTheme.current.primary[300],
                ),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(width = 1.dp, color = LocalColorTheme.current.primary[300]),
                modifier = Modifier.height(46.dp),
                onClick = onDuplicationCheckButtonClicked,
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.duplicate_check),
                    color = LocalColorTheme.current.primary[300],
                    fontWeight = FontWeight.W700,
                    fontSize = 13.sp,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewIdForm() {
    ThemeProvider {
        IdForm(
            id = "asd",
            state = IdValidationState.VALID,
            onIdChanged = {},
            onDuplicationCheckButtonClicked = {},
        )
    }
}