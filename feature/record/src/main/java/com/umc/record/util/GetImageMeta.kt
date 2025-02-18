package com.umc.record.util

import android.media.ExifInterface
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

    // 위도 & 경도 가져오기
    val lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)?.let { convertToDecimal(it) }
    val lon = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)?.let { convertToDecimal(it) }
    val latRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
    val lonRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)

    // 북위(N) / 남위(S), 동경(E) / 서경(W) 처리
    val latitude = if (lat != null && latRef == "S") -lat else lat
    val longitude = if (lon != null && lonRef == "W") -lon else lon

    // 촬영 날짜 가져오기
    val dateTaken = exif.getAttribute(ExifInterface.TAG_DATETIME)
    val formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")
    val date = try {
        LocalDateTime.parse(dateTaken, formatter)
    } catch (e: DateTimeParseException) {
        null  // 파싱 실패하면 null 반환
    }

    return ImageMetadata(latitude, longitude, date)
}

// DMS(Degrees, Minutes, Seconds) 형식 → 소수점 좌표 변환
private fun convertToDecimal(dms: String): Double {
    val parts = dms.split(",")
    if (parts.size != 3) return 0.0

    val degrees = parts[0].split("/").let { it[0].toDouble() / it[1].toDouble() }
    val minutes = parts[1].split("/").let { it[0].toDouble() / it[1].toDouble() }
    val seconds = parts[2].split("/").let { it[0].toDouble() / it[1].toDouble() }

    return degrees + (minutes / 60) + (seconds / 3600)
}