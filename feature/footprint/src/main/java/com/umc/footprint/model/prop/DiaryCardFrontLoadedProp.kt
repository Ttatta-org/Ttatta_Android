package com.umc.footprint.model.prop

import com.umc.design.CategoryColor
import java.time.LocalDate

data class DiaryCardFrontLoadedProp(
    val date: LocalDate,
    val categoryColor: CategoryColor? = null,
    val imageUrl: String,
    val onModifyButtonClicked: (() -> Unit)?,
)
