package com.umc.footprint.model.prop

import java.time.LocalDate

class DiaryCardBackProp(
    val date: LocalDate,
    val content: String,
    val diaryModificationModeProp: DiaryModificationModeProp?,
    val onModifyButtonClicked: () -> Unit
)