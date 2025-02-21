package com.umc.record.util

import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

data class ImageMetadata(
    val latitude: Double?,
    val longitude: Double?,
    val date: LocalDateTime?
)

fun getImageMetadata(file: File): ImageMetadata {
    if (!file.exists()) return ImageMetadata(null, null, null)

    val exif = ExifInterface(file.absolutePath)

    // 촬영 날짜 가져오기
    val dateTaken = exif.getAttribute(ExifInterface.TAG_DATETIME)
    val formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")
    val date = try {
        LocalDateTime.parse(dateTaken, formatter)
    } catch (e: Exception) {
        null  // 파싱 실패하면 null 반환
    }

    return ImageMetadata(exif.latLong?.get(0), exif.latLong?.get(1), date)
}
