package com.umc.category.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.umc.category.R
import com.umc.design.Primary300
import com.umc.design.R as Res

data class CategoryDeletionDialogProp(
    val onDismissed: () -> Unit,
    val onConfirmed: () -> Unit,
)

@Composable
fun CategoryDeletionDialog(
    prop: CategoryDeletionDialogProp
) {
    Dialog(
        onDismissRequest = prop.onDismissed,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        )
    ) {
        val dialog = LocalView.current.parent as DialogWindowProvider
        LaunchedEffect(key1 = Unit) { dialog.window.setDimAmount(0f) }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        0f to Color.White,
                        1f to Color.Primary300
                    ),
                    alpha = 0.2f,
                )
                .clickable(
                    indication = null,
                    interactionSource = null,
                    onClick = prop.onDismissed,
                )
                .padding(32.dp)
        ) {
            ElevatedCard(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.widthIn(max = 360.dp)
            ) {
                Box(
                    contentAlignment = Alignment.TopEnd,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        modifier = Modifier
                            .padding(top = 12.dp, bottom = 24.dp)
                            .fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = Res.drawable.ic_header_deco),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.delete_all_alert_message_1))
                                append("\n")
                                append(stringResource(id = R.string.delete_all_alert_message_2))
                            },
                            style = TextStyle(
                                lineBreak = LineBreak.Heading,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                            ),
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                        ElevatedButton(
                            onClick = prop.onConfirmed,
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = Color.Primary300
                            ),
                            elevation = ButtonDefaults.elevatedButtonElevation(
                                defaultElevation = 8.dp
                            ),
                            modifier = Modifier.fillMaxWidth(0.6f)
                        ) {
                            Text(
                                text = stringResource(id = R.string.delete),
                                color = Color.White
                            )
                        }
                    }
                    Box(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        IconButton(
                            onClick = prop.onDismissed,
                            modifier = Modifier.size(32.dp)

                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_x),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                            )
                        }
                    }
                }
            }
        }
    }
}

val previewCategoryDeletionDialogProp = CategoryDeletionDialogProp(
    onDismissed = {},
    onConfirmed = {}
)

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun PreviewCategoryDeletionDialog() {
    CategoryDeletionDialog(prop = previewCategoryDeletionDialogProp)
}