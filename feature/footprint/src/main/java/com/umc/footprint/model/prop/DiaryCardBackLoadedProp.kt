package com.umc.footprint.model.prop

import com.umc.design.CategoryColor
import java.time.LocalDate

data class DiaryCardBackLoadedProp(
    val date: LocalDate,
    val categoryColor: CategoryColor? = null,
    val content: String,
    val diaryModificationModeProp: DiaryModificationModeProp?,
    val onModifyButtonClicked: () -> Unit
)
