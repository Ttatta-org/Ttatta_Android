package com.umc.footprint.model.prop

data class DiaryCardProp(
    val clusterId: Long,
    val diaryCardLoadedPropMap: Map<Int, DiaryCardLoadedProp?>,
    val onNewDiaryRequested: (Int) -> Unit,
)