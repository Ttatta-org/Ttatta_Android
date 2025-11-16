package com.umc.ttatta.app.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileManager {
    fun Context.createImageUri(): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "photo_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    fun Context.uriToFile(uri: Uri): File? {
        val file = File(cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")

        try {
            contentResolver
                .openInputStream(uri)
                ?.use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

            return file
        } catch (e: IOException) {
            Log.e("uriToFile", "Error converting URI to File: ${e.message}")
            return null
        }
    }
}