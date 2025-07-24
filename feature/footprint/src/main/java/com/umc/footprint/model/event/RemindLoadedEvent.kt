package com.umc.footprint.model.event

import com.umc.design.CategoryColor
import java.time.LocalDate

/**
 * 위치 기반 리마인드 이벤트가 발생한 후, 해당 이벤트에 대한 정보가 전부 불러와졌을 때 발생하는 이벤트
 */
data class RemindLoadedEvent(
    val description: String,
    val date: LocalDate,
    val isBook: Boolean,
    val color: CategoryColor,
    val imageUrl: String,
    val content: String,
)
