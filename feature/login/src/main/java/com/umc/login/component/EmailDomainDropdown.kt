package com.umc.login.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.theme.ThemeProvider
import com.umc.login.model.prop.EmailDomainDropdownItemProp

val emailDomains = listOf(
    "naver.com",
    "gmail.com",
    "apple.com",
)

@Composable
fun EmailDomainDropdown(
    props: List<EmailDomainDropdownItemProp>,
) {
    Box(
        modifier = Modifier
            .shadow(
                elevation = 4.dp,
                spotColor = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp),
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
            ),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 4.dp)
                .width(IntrinsicSize.Max)
        ) {
            repeat(props.size) { index ->
                val prop = props[index]

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { prop.onClicked() },
                ) {
                    Text(
                        text = prop.domain,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(
                            vertical = 4.dp,
                            horizontal = 8.dp
                        ),
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