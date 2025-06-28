package com.umc.footprint.model.prop

import com.umc.design.CategoryColor

data class DiaryCardProp(
    val clusterId: Long,
    val defaultCategoryColor: CategoryColor?,
    val diaryCardLoadedPropMap: Map<Int, DiaryCardLoadedProp?>,
    val onNewDiaryRequested: (Int) -> Unit,
)