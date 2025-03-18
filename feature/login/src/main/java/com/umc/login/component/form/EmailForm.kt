package com.umc.login.component.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umc.design.Grey300
import com.umc.login.R
import com.umc.login.component.CustomTextField
import com.umc.login.component.CustomTextFieldLabelScope
import com.umc.login.component.CustomTextFieldProp
import com.umc.login.component.toCustomTextFieldUnderMessageProp
import com.umc.login.logic.state.EmailValidationState

@Composable
fun EmailForm(
    local: String,
    domain: String,
    state: EmailValidationState,
    onLocalChanged: (String) -> Unit,
    onDomainChanged: (String) -> Unit,
) {
    CustomTextFieldLabelScope(
        underMessageProp = state.message?.toCustomTextFieldUnderMessageProp(),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1.5f)
            ) {
                CustomTextField(
                    prop = CustomTextFieldProp(
                        value = local,
                        onValueChanged = onLocalChanged,
                        placeholder = stringResource(id = R.string.email_placeholder),
                    )
                )
            }
            Text(text = "@")
            Box(
                modifier = Modifier.weight(1f)
            ) {
                CustomTextField(
                    prop = CustomTextFieldProp(
                        value = domain,
                        onValueChanged = onDomainChanged,
                        placeholder = stringResource(id = R.string.enter_yourself),
                        tail = {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .clip(CircleShape)
                                    .clickable { /* TODO: 드롭다운 */ }
                            ) {
                                Box(
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_dropdown),
                                        contentDescription = null,
                                        tint = Color.Grey300,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmailForm() {
    EmailForm(
        local = "ddadda",
        domain = "naver.com",
        state = EmailValidationState.VALID,
        onLocalChanged = {},
        onDomainChanged = {},
    )
}