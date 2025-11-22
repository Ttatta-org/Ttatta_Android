package com.umc.ttatta.app.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.Primary500
import com.umc.design.character.Accessory
import com.umc.design.character.AccessorySet
import com.umc.design.character.CharacterView
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider
import com.umc.ttatta.app.R
import com.umc.ttatta.app.model.prop.RecordOptionPickerProp

private val cameraButtonSize = Size(150f, 42f)
private val galleryButtonSize = Size(150f, 42f)
private val buttonActualWidth = 150.dp

@Composable
fun RecordOptionPicker(
    prop: RecordOptionPickerProp,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            contentAlignment = Alignment.TopCenter,
        ) {
            ShadowedImage(
                id = R.drawable.img_chat_bubble,
                contentDescription = null,
                width = 277.dp,
                height = 141.dp,
                offsetY = 4.dp,
                shadowBlur = 10.dp,
                shadowColor = Color(0xFFD7806F).copy(alpha = 0.35f),
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.height(109.dp),
            ) {
                Text(
                    text = buildAnnotatedString {
                        appendLine(prop.userName + stringResource(id = R.string.record_top_line_message))
                        appendLine(stringResource(id = R.string.record_body1_message))
                        append(stringResource(id = R.string.record_body2_message))
                    },
                    color = LocalColorTheme.current.primary[600],
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    letterSpacing = (-0.4).sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
        CharacterView(
            accessorySet = prop.accessories,
            width = 270.dp,
        )
        Spacer(modifier = Modifier.height(32.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DpSize(
                width = buttonActualWidth,
                height = buttonActualWidth * cameraButtonSize.height / cameraButtonSize.width
            ).let { size ->
                Box(
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = null,
                        onClick = prop.onCameraOptionClicked
                    ),
                ) {
                    ShadowedImage(
                        id = R.drawable.btn_camera,
                        contentDescription = null,
                        width = size.width,
                        height = size.height,
                        offsetY = 4.dp,
                        shadowBlur = 10.dp,
                        shadowColor = Color(0xFFD7806F).copy(alpha = 0.35f),
                    )
                }
            }
            DpSize(
                width = buttonActualWidth,
                height = buttonActualWidth * galleryButtonSize.height / galleryButtonSize.width
            ).let { size ->
                Box(
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = null,
                        onClick = prop.onGalleryOptionClicked
                    ),
                ) {
                    ShadowedImage(
                        id = R.drawable.btn_gallery,
                        contentDescription = null,
                        width = size.width,
                        height = size.height,
                        offsetY = 4.dp,
                        shadowBlur = 10.dp,
                        shadowColor = Color(0xFFD7806F).copy(alpha = 0.35f),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewRecordDialog() {
    ThemeProvider {
        RecordOptionPicker(
            prop = RecordOptionPickerProp(
                userName = "test",
                accessories = AccessorySet.create(
                    Accessory.TTOTTO_BAG,
                    Accessory.TTOTTO_HAT,
                    Accessory.TTUTTU_BAG,
                    Accessory.TTUTTU_HAT,
                ),
                onCameraOptionClicked = {},
                onGalleryOptionClicked = {},
            )
        )
    }
}