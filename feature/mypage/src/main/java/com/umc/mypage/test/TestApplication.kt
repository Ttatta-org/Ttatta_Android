package com.umc.mypage.test

import android.app.Application

class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 테스트 환경을 위한 초기화 작업 추가 가능
    }
}
