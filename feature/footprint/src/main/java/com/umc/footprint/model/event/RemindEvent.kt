package com.umc.footprint.model.event

/**
 * 위치 기반 리마인드 이벤트
 */
data class RemindEvent(
    val diaryId: Long,
    val description: String,
    val onDismissed: () -> Unit,
)
