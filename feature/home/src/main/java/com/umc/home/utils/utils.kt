package com.umc.home.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun LocalDate.formatToKorean(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일")
    return this.format(formatter)
}
