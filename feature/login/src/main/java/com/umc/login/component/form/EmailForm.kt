package com.umc.login.component.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toOffset
import com.umc.design.Grey300
import com.umc.login.R
import com.umc.login.component.CustomTextField
import com.umc.login.component.CustomTextFieldLabelScope
import com.umc.login.component.CustomTextFieldProp
import com.umc.login.component.CustomTextFieldTextAlignment
import com.umc.login.component.PreviewEmailDomainDropdown
import com.umc.login.component.toCustomTextFieldUnderMessageProp
import com.umc.login.logic.state.EmailValidationState

@Composable
fun EmailForm(
    local: String,
    domain: String,
    state: EmailValidationState,
    onLocalChanged: (String) -> Unit,
    onDomainChanged: (String) -> Unit,
    onDomainDropdownExpandedChanged: () -> Unit,
    onDomainDropdownButtonCenterOffsetCalculated: (Offset) -> Unit,
) {
    var totalLayoutCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var buttonLayoutCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    LaunchedEffect(key1 = totalLayoutCoordinates, key2 = buttonLayoutCoordinates) {
        val total = totalLayoutCoordinates
        val button = buttonLayoutCoordinates

        if (total != null && button != null) onDomainDropdownButtonCenterOffsetCalculated(
            total.localPositionOf(button) + button.size.center.toOffset()
        )
    }

    CustomTextFieldLabelScope(
        underMessageProp = state.message?.toCustomTextFieldUnderMessageProp(),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.onGloballyPositioned { totalLayoutCoordinates = it }
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
                        textAlignment = CustomTextFieldTextAlignment.FLEX_CENTER,
                        tail = {
                            Box(
                                modifier = Modifier
                                    .padding(start = 4.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
                                    .clip(CircleShape)
                                    .clickable { onDomainDropdownExpandedChanged() }
                                    .onGloballyPositioned { buttonLayoutCoordinates = it }
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
                        },
                    ),
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 300)
@Composable
fun PreviewEmailForm() {
    var isExpanded by remember { mutableStateOf(false) }
    var buttonCenterOffset by remember { mutableStateOf(Offset.Zero) }

    Box {
        EmailForm(
            local = "ddadda",
            domain = "naver.com",
            state = EmailValidationState.VALID,
            onLocalChanged = {},
            onDomainChanged = {},
            onDomainDropdownExpandedChanged = { isExpanded = !isExpanded },
            onDomainDropdownButtonCenterOffsetCalculated = { buttonCenterOffset = it },
        )

        if (isExpanded) Box(
            modifier = Modifier.offset { buttonCenterOffset.round() }
        ) {
            PreviewEmailDomainDropdown()
        }
    }
}

