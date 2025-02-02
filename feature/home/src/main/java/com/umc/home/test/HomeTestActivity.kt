package com.umc.home.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.umc.home.EditDiary
import com.umc.home.HomeEditRecordScreen
import java.time.LocalDateTime

class HomeTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeEditRecordScreen(
                diary = EditDiary(
                    id = 1,
                    date = LocalDateTime.now(),
                    imageUrl = null, // ✅ 테스트 데이터에서 이미지 URL을 null로 설정
                    content = "테스트 기록"
                ),
                onUpdateDiary = { updatedDiary ->
                    // ✅ 여기에서 업데이트된 데이터를 처리할 수 있음 (예: 로그 출력)
                    println("업데이트된 다이어리: $updatedDiary")
                }
            )
        }
    }
}
