package com.umc.footprint.model.prop

import java.time.LocalDate

class DiaryCardFrontProp(
    val date: LocalDate,
    val imageUrl: String,
    val onModifyButtonClicked: () -> Unit,
)