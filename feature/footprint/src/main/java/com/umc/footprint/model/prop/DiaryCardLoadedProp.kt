package com.umc.footprint.model.prop

import com.umc.design.CategoryColor
import java.time.LocalDate

data class DiaryCardLoadedProp(
    val id: Long,
    val date: LocalDate,
    val categoryColor: CategoryColor? = null,
    val imageUrl: String,
    val content: String,
    val isFlipped: Boolean,
    val diaryModificationModeProp: DiaryModificationModeProp?,
    val onCardClicked: () -> Unit,
    val onModifyButtonClicked: () -> Unit,
)