package com.umc.footprint.component.card

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.theme.LocalColorTheme
import com.umc.design.theme.ThemeProvider
import com.umc.footprint.model.prop.DiaryCardBackLoadedProp
import com.umc.footprint.model.prop.DiaryCardBackProp
import com.umc.footprint.model.prop.DiaryCardFrameProp
import java.time.LocalDate

@Composable
fun DiaryCardBack(
    prop: DiaryCardBackProp,
) {
    val colors = LocalColorTheme.current
    val categoryColor = prop.prop?.categoryColor ?: prop.defaultColor

    DiaryCardFrame(
        prop = DiaryCardFrameProp(
            date = prop.prop?.date,
            borderColor = categoryColor?.b ?: colors.primary[400],
            backgroundColor = categoryColor?.c ?: colors.secondary[200],
            contentContainerColor = categoryColor?.a ?: colors.secondary[300],
            onModifyButtonClicked = prop.prop?.onModifyButtonClicked,
            content = {
                if (prop.prop != null) Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                ) {
                    if (prop.prop.diaryModificationModeProp != null) {
                        val focusRequester = remember { FocusRequester() }

                        BasicTextField(
                            value = prop.prop.diaryModificationModeProp.contentValue,
                            onValueChange = prop.prop.diaryModificationModeProp.onContentValueChanged,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done,
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { prop.prop.diaryModificationModeProp.onModificationDone() },
                            ),
                            textStyle = TextStyle(
                                color = colors.grey[700],
                                fontSize = 13.sp,
                            ),
                            modifier = Modifier.focusRequester(focusRequester),
                        )

                        LaunchedEffect(key1 = Unit) { focusRequester.requestFocus() }
                    } else Text(
                        text = prop.prop.content,
                        style = TextStyle(
                            color = colors.grey[700],
                            fontSize = 13.sp,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    CircularProgressIndicator(
                        color = colors.primary[400],
                        modifier = Modifier.size(32.dp),
                    )
                }
            },
        ),
    )
}

private val previewDiaryCardBackProp = DiaryCardBackProp(
    prop = DiaryCardBackLoadedProp(
        date = LocalDate.now(),
        content = "This is diary.",
        diaryModificationModeProp = null,
        onModifyButtonClicked = {},
    ),
)

@Preview
@Composable
fun PreviewDiaryCardBack() {
    ThemeProvider {
        DiaryCardBack(
            prop = previewDiaryCardBackProp
        )
    }
}