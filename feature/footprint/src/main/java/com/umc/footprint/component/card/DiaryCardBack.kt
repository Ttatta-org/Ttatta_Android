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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.Grey500
import com.umc.design.Primary400
import com.umc.design.theme.ThemeProvider
import com.umc.footprint.model.prop.DiaryCardBackProp
import com.umc.footprint.model.prop.DiaryCardFrameProp
import java.time.LocalDate

@Composable
fun DiaryCardBack(
    prop: DiaryCardBackProp?,
) {
    DiaryCardFrame(
        prop = DiaryCardFrameProp(
            date = prop?.date,
            borderColor = Color(0xFFE5E5E5),
            backgroundColor = Color(0xFFFFFFFF),
            contentContainerColor = Color(0xFFF5F5F5),
            onModifyButtonClicked = prop?.onModifyButtonClicked,
            content = {
                // 본문
                if (prop != null) Box(
                    contentAlignment = Alignment.Companion.CenterStart,
                    modifier = Modifier.Companion.padding(16.dp).fillMaxSize(),
                ) {
                    if (prop.diaryModificationModeProp != null) {
                        val focusRequester = remember { FocusRequester() }

                        BasicTextField(
                            value = prop.diaryModificationModeProp.contentValue,
                            onValueChange = prop.diaryModificationModeProp.onContentValueChanged,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Companion.Done,
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { prop.diaryModificationModeProp.onModificationDone() },
                            ),
                            textStyle = TextStyle(
                                color = Color.Companion.Grey500,
                                fontSize = 13.sp,
                            ),
                            modifier = Modifier.Companion.focusRequester(focusRequester)
                        )

                        LaunchedEffect(key1 = Unit) { focusRequester.requestFocus() }
                    } else Text(
                        text = prop.content, style = TextStyle(
                            color = Color.Companion.Grey500,
                            fontSize = 13.sp,
                        ), modifier = Modifier.Companion.fillMaxWidth()
                    )
                } else Box(
                    contentAlignment = Alignment.Companion.Center,
                    modifier = Modifier.Companion.size(220.dp),
                ) {
                    CircularProgressIndicator(
                        color = Color.Companion.Primary400,
                        modifier = Modifier.Companion.size(32.dp),
                    )
                }
            },
        ),
    )
}

private val previewDiaryCardBackProp = DiaryCardBackProp(
    date = LocalDate.now(),
    content = "This is diary.",
    diaryModificationModeProp = null,
    onModifyButtonClicked = {},
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