package com.umc.footprint.model.prop

import com.umc.design.CategoryColor
import java.time.LocalDate

class DiaryCardBackProp(
    val date: LocalDate,
    val categoryColor: CategoryColor? = null,
    val content: String,
    val diaryModificationModeProp: DiaryModificationModeProp?,
    val onModifyButtonClicked: () -> Unit
)