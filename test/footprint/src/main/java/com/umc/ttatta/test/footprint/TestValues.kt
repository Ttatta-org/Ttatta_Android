package com.umc.ttatta.test.footprint

import androidx.annotation.RawRes

data class TestPlace(
    val name: String,
    @RawRes val imageId: Int,
    val latitude: Double,
    val longitude: Double,
)

object TestValues {
    const val ID = "footprint_test"
    const val PASSWORD = "test1234"
    const val NAME = "kim"
    const val NICKNAME = "kim test"
    const val EMAIL = "tester_kim@test.com"

    val PLACE = listOf(
        TestPlace(
            name = "숭실대학교",
            imageId = R.raw.img_soongsil_univ,
            latitude = 37.4963,
            longitude = 126.9574
        ),
        TestPlace(
            name = "중앙대학교",
            imageId = R.raw.img_chungang_univ,
            latitude = 37.5056,
            longitude = 126.9586
        ),
        TestPlace(
            name = "서울시청",
            imageId = R.raw.img_seoul_city_hall,
            latitude = 37.5667,
            longitude = 126.9784
        ),
    )
}