package com.umc.ttatta.util

import android.app.Activity
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun Activity.uriToFile(uri: Uri): File? {
    val file = File(cacheDir, "temp_image_${System.currentTimeMillis()}.jpg") // 내부 캐시 디렉토리에 저장
    try {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream) // 스트림을 복사
            }
        }
        return file
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}