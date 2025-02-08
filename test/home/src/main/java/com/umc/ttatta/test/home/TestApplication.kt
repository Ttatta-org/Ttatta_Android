package com.umc.ttatta.test.home

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 테스트 환경을 위한 초기화 작업 추가 가능
    }
}