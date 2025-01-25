package com.umc.record.test

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RecordTestApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
