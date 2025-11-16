package com.umc.ttatta.app.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.umc.design.theme.ThemeProvider
import com.umc.ttatta.app.R
import com.umc.ttatta.app.model.prop.RecordOptionPickerProp

private val cameraButtonSize = Size(141f, 40f)
private val galleryButtonSize = Size(141f, 51f)
private val buttonActualWidth = 160.dp

@Composable
fun RecordOptionPicker(
    prop: RecordOptionPickerProp,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        Text(
            text = buildAnnotatedString {
                appendLine(prop.userName + stringResource(id = R.string.record_top_line_message))
                appendLine(stringResource(id = R.string.record_body1_message))
                append(stringResource(id = R.string.record_body2_message))
            },
            color = Color.Primary500,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 33.sp,
            letterSpacing = 0.4.sp,
            textAlign = TextAlign.Center,
        )
        CharacterView(
            accessorySet = prop.accessories,
            width = 200.dp,
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            DpSize(
                width = buttonActualWidth,
                height = buttonActualWidth * cameraButtonSize.height / cameraButtonSize.width
            ).let { size ->
                Image(
                    painter = painterResource(id = R.drawable.btn_camera),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(size)
                        .clickable(
                            indication = null,
                            interactionSource = null,
                            onClick = prop.onCameraOptionClicked
                        )
                )
            }
            DpSize(
                width = buttonActualWidth,
                height = buttonActualWidth * galleryButtonSize.height / galleryButtonSize.width
            ).let { size ->
                Image(
                    painter = painterResource(id = R.drawable.btn_gallary),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(size)
                        .clickable(
                            indication = null,
                            interactionSource = null,
                            onClick = prop.onGalleryOptionClicked
                        )
                )
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