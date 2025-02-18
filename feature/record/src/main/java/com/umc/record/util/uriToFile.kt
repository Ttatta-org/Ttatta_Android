package com.umc.record.util

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream

fun uriToFile(context: Context, uri: Uri): File {
    val resolver = context.contentResolver
    val mime = resolver.getType(uri)!!
    val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)
    return File(context.cacheDir, "image.${extension}").apply {
        FileOutputStream(this).use {
            resolver.openInputStream(uri)?.copyTo(it)
        }
    }
}