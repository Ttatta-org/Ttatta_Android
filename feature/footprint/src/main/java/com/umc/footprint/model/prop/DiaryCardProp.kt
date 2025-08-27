package com.umc.footprint.model.prop

import com.umc.design.CategoryColor

data class DiaryCardProp(
    val key: Long,
    val description: String?,
    val defaultCategoryColor: CategoryColor?,
    val diaryCardLoadedPropMap: Map<Int, DiaryCardLoadedProp?>,
    val onNewDiaryRequested: (Int) -> Unit,
)