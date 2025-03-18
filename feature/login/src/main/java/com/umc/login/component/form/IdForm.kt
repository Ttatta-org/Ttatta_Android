package com.umc.login.component.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.Primary200
import com.umc.login.R
import com.umc.login.component.CustomTextField
import com.umc.login.component.CustomTextFieldLabelScope
import com.umc.login.component.CustomTextFieldProp
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
        CustomTextField(
            prop = CustomTextFieldProp(
                value = id,
                onValueChanged = onIdChanged,
                placeholder = stringResource(id = R.string.id),
                tail = {
                    if (state == IdValidationState.VALID) Spacer(
                        modifier = Modifier.height(32.dp)
                    ) else Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .height(32.dp)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .clickable { onDuplicationCheckButtonClicked() }
                    ) {
                        Text(
                            text = stringResource(id = R.string.duplicate_check),
                            color = Color.Primary200,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewIdForm() {
    IdForm(
        id = "",
        state = IdValidationState.VALID,
        onIdChanged = {},
        onDuplicationCheckButtonClicked = {}
    )
}