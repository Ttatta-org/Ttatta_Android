package com.umc.footprint.model.event

/**
 * 일기 카드의 미트볼 메뉴가 눌렸을 떄 발생하는 이벤트
 */
data class DiaryModificationBarOpenEvent(
    val targetDiaryId: Long
)