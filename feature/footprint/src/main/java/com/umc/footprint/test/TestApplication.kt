package com.umc.footprint.test

import android.app.Application
import com.naver.maps.map.NaverMapSdk
import com.umc.footprint.BuildConfig

class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(BuildConfig.NAVER_SDK_CLIENT_ID)
    }
}