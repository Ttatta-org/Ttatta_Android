package com.umc.home.test

import java.time.LocalDateTime
import com.umc.design.CategoryColor

object TestValues {
    const val ID = "test_Na_id"
    const val PASSWORD = "1234"
    const val NAME = "test_Na"
    const val NICKNAME = "Na"
    const val EMAIL = "test_Na@test.com"
    val TODAY: LocalDateTime = LocalDateTime.parse("2025-01-29T01:39:42.814468")
    const val CONTENT = "서울시청의 한 사진입니다."
    const val LATITUDE = 37.566535
    const val LONGITUDE = 126.9779692
    const val CATEGORY_NAME = "test category"
    val CATEGORY_COLOR = CategoryColor.NAVY
}