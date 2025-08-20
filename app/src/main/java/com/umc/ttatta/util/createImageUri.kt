package com.umc.ttatta.util

import android.app.Activity
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore

fun Activity.createImageUri(): Uri? {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "photo_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
}