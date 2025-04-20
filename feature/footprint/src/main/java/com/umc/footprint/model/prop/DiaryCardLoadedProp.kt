package com.umc.footprint.model.prop

import java.time.LocalDate

data class DiaryCardLoadedProp(
    val id: Long,
    val date: LocalDate,
    val imageUrl: String,
    val content: String,
    val isFlipped: Boolean,
    val diaryModificationModeProp: DiaryModificationModeProp?,
    val onCardClicked: () -> Unit,
    val onModifyButtonClicked: () -> Unit,
)