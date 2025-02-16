package com.umc.ttatta.test.record

import android.app.Application
import com.naver.maps.map.NaverMapSdk
import com.umc.record.BuildConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RecordTestApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(BuildConfig.NAVER_SDK_CLIENT_ID)
    }
}
