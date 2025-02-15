package com.umc.home.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.bumptech.glide.Glide
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.formatToKorean(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일")
    return this.format(formatter)
}
fun getFileFromUri(context: Context, uri: Uri): File? {
    return try {
        // ✅ URL인지 확인
        if (uri.toString().startsWith("https://") || uri.toString().startsWith("http://")) {
            Log.d("FileUtil", "📌 URL 기반 이미지 감지: ${uri}")

            // ✅ Glide를 이용해서 URL을 파일로 변환
            val futureTarget = Glide.with(context)
                .downloadOnly()
                .load(uri)
                .submit()

            val downloadedFile = futureTarget.get() // 다운로드 완료된 파일
            Log.d("FileUtil", "✅ 다운로드된 파일 경로: ${downloadedFile.absolutePath}")

            return downloadedFile
        }

        // ✅ 기존의 로컬 파일 처리 로직
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("temp_", ".jpg", context.cacheDir)
        inputStream?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        tempFile
    } catch (e: Exception) {
        Log.e("FileUtil", "❌ 파일 변환 실패: ${e.message}")
        null
    }
}

//fun getFileFromUri(context: Context, uri: Uri): File? {
//    val contentResolver = context.contentResolver
//    val inputStream = contentResolver.openInputStream(uri) ?: return null
//    val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg") // ✅ 캐시 디렉토리에 임시 파일 생성
//    tempFile.outputStream().use { outputStream ->
//        inputStream.copyTo(outputStream)
//    }
//    return tempFile
//}

