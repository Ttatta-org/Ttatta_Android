package com.umc.login.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.Grey300
import com.umc.design.Secondary100
import com.umc.design.theme.ThemeProvider

data class EmailDomainDropdownItemProp(
    val onClicked: () -> Unit,
    val domain: String,
)

val emailDomains = listOf(
    "naver.com",
    "gmail.com",
)

@Composable
fun EmailDomainDropdown(
    props: List<EmailDomainDropdownItemProp>,
) {
    Box(
        modifier = Modifier.Companion.shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
            ).background(
                color = Color.Companion.Secondary100,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            ),
    ) {
        Column(
            modifier = Modifier.Companion.padding(vertical = 16.dp).width(IntrinsicSize.Max)
        ) {
            repeat(props.size * 2 + 1) {
                if (it and 1 > 0) {
                    val prop = props[it / 2]

                    Row(
                        verticalAlignment = Alignment.Companion.CenterVertically,
                        modifier = Modifier.Companion.fillMaxWidth().clickable { prop.onClicked() },
                    ) {
                        Text(
                            text = prop.domain,
                            fontSize = 12.sp,
                            modifier = Modifier.Companion.padding(
                                vertical = 8.dp,
                                horizontal = 16.dp
                            ),
                        )
                    }
                } else {
                    HorizontalDivider(
                        color = Color.Companion.Grey300,
                        thickness = 1.dp,
                        modifier = Modifier.Companion.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

val previewEmailDomainDropdownItemProps = listOf(
    EmailDomainDropdownItemProp(
        onClicked = {},
        domain = "gmail.com",
    ),
    EmailDomainDropdownItemProp(
        onClicked = {},
        domain = "naver.com",
    ),
)

@Preview
@Composable
fun PreviewEmailDomainDropdown() {
    ThemeProvider {
        EmailDomainDropdown(
            props = previewEmailDomainDropdownItemProps,
        )
    }
}