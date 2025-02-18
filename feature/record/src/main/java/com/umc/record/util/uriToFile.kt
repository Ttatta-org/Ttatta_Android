package com.umc.record.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun uriToFile(context: Context, uri: Uri): File {
    return File(context.cacheDir, "image").apply {
        FileOutputStream(this).use {
            context.contentResolver.openInputStream(uri)?.copyTo(it)
        }
    }
}