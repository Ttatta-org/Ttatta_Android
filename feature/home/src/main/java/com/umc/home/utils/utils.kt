package com.umc.home.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.formatToKorean(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일")
    return this.format(formatter)
}

fun getFileFromUri(context: Context, uri: Uri): File? {
    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(uri) ?: return null
    val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg") // ✅ 캐시 디렉토리에 임시 파일 생성
    tempFile.outputStream().use { outputStream ->
        inputStream.copyTo(outputStream)
    }
    return tempFile
}

