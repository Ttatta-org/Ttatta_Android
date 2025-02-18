package com.umc.challenge.util

fun String.hasFinalConsonant(): Boolean {
    val lastChar = this.lastOrNull() ?: return false

    if (lastChar in '가'..'힣') {
        val unicodeOffset = lastChar.code - 44032
        val jongseongIndex = unicodeOffset % 28
        return jongseongIndex != 0  // 종성이 있으면 true
    }

    return false // 한글이 아니면 받침 없음
}