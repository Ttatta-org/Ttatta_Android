package com.umc.login.component.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toOffset
import com.umc.design.Grey300
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider
import com.umc.login.R
import com.umc.login.component.CustomTextField
import com.umc.login.model.prop.CustomTextFieldProp
import java.time.Duration

@Composable
fun CertificationForm(
    name: String,
    local: String,
    domain: String,
    code: String,
    remainTime: Duration?,
    isEditable: Boolean,
    isCodeFieldVisible: Boolean,
    onNameChanged: (String) -> Unit,
    onLocalChanged: (String) -> Unit,
    onDomainChanged: (String) -> Unit,
    onDomainDropdownExpandedChanged: () -> Unit,
    onDomainDropdownButtonCenterOffsetCalculated: (Offset) -> Unit,
    onCodeChanged: (String) -> Unit,
) {
    var totalLayoutCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var buttonLayoutCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    LaunchedEffect(key1 = null) {
        val total = totalLayoutCoordinates
        val button = buttonLayoutCoordinates

        if (total != null && button != null) onDomainDropdownButtonCenterOffsetCalculated(
            total.localPositionOf(button) + button.size.center.toOffset()
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.onGloballyPositioned { totalLayoutCoordinates = it },
    ) {
        CustomTextField(
            prop = CustomTextFieldProp(
                value = name,
                onValueChanged = onNameChanged,
                placeholder = stringResource(id = R.string.name_placeholder),
                isEditable = isEditable,
            )
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier.weight(1.5f)
                ) {
                    CustomTextField(
                        prop = CustomTextFieldProp(
                            value = local,
                            onValueChanged = onLocalChanged,
                            placeholder = stringResource(id = R.string.email_placeholder),
                            isEditable = isEditable,
                        )
                    )
                }
                Text(
                    text = "@",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.W600,
                    color = LocalColorTheme.current.primary[600],
                )
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    CustomTextField(
                        prop = CustomTextFieldProp(
                            value = domain,
                            onValueChanged = onDomainChanged,
                            placeholder = stringResource(id = R.string.enter_yourself),
                            isEditable = isEditable,
                            tail = {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 4.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
                                        .clip(CircleShape)
                                        .clickable { onDomainDropdownExpandedChanged() }
                                        .onGloballyPositioned { buttonLayoutCoordinates = it },
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
        if (isCodeFieldVisible) Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                CustomTextField(
                    prop = CustomTextFieldProp(
                        value = code,
                        onValueChanged = onCodeChanged,
                        placeholder = stringResource(id = R.string.certification_placeholder),
                        isEditable = isEditable,
                        keyboardType = KeyboardType.Number,
                        tail = {
                            if (remainTime != null) Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .height(32.dp)
                                    .padding(horizontal = 16.dp)
                            ) {
                                Text(
                                    text = "%02d:%02d".format(remainTime.seconds / 60, remainTime.seconds % 60),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.W700,
                                    color = LocalColorTheme.current.primary[600],
                                )
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
fun PreviewCertificationForm() {
    ThemeProvider {
        CertificationForm(
            name = "",
            local = "",
            domain = "",
            code = "",
            remainTime = Duration.parse("PT3M12S"),
            isEditable = true,
            isCodeFieldVisible = true,
            onNameChanged = {},
            onLocalChanged = {},
            onDomainChanged = {},
            onDomainDropdownExpandedChanged = {},
            onDomainDropdownButtonCenterOffsetCalculated = {},
            onCodeChanged = {},
        )
    }
}