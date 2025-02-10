package com.umc.ttatta.test.home

import java.time.LocalDateTime
import com.umc.design.CategoryColor

object TestValues {
    const val ID = "test_Na_id"
    const val PASSWORD = "1234"
    const val NAME = "test_Na"
    const val NICKNAME = "Na"
    const val EMAIL = "test_Na@test.com"
    val TODAY: LocalDateTime = LocalDateTime.parse("2025-02-11T13:15:30.000000")
    const val CONTENT = "테스트용 일기 내용입니당 (●'◡'●)"
    const val LATITUDE = 37.233585
    const val LONGITUDE = 126.626038
    const val CATEGORY_NAME = "test category"
    val CATEGORY_COLOR = CategoryColor.NAVY
}