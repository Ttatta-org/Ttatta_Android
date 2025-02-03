package com.umc.data

import com.umc.design.CategoryColor
import java.time.LocalDateTime

object TestValue {
    const val ID = "test_id"
    const val PASSWORD = "test1234"
    const val NAME = "kim"
    const val NICKNAME = "tester"
    const val EMAIL = "tester_kim@test.com"
    val TODAY: LocalDateTime = LocalDateTime.parse("2025-01-29T01:39:42.814468")
    const val CONTENT = "서울시청의 한 사진입니다."
    const val LATITUDE = 37.566535
    const val LONGITUDE = 126.9779692
    const val CATEGORY_NAME = "test category"
    val CATEGORY_COLOR = CategoryColor.NAVY
}