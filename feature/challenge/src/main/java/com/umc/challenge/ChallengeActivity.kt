package com.umc.challenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.umc.challenge.screen.ChallengeScreen
import com.umc.challenge.screen.ChallengeScreenTopBarProp

@Deprecated("Jetpack Compose 사용중")
class ChallengeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 더미 값들 생성 (필요시 Intent extras 통해 실제 값 주입도 가능)
        // ChallengeScreenTopBarProp 구성
        val topBarProp = ChallengeScreenTopBarProp(
            point = 0, // 기본 포인트 값, 필요시 Intent로 주입 가능
            onShopIconClicked = {
                // TODO: Shop 아이콘 클릭 시 행동 정의 (필요 시 finish()나 다른 동작)
            },
        )

        val challengeCompletionDialogProp = null // 혹은 기본 dialog 상태

        val pointGrantedCardDialogProp = null // 혹은 default

        val defaultView: @Composable () -> Unit = {
            // 아무것도 없는 더미 뷰 or 실제 컴포저블
        }

        // 2. Compose 화면 설정
        setContent {
            ChallengeScreen(
                topBarProp = topBarProp,
                challengeCompletionDialogProp = challengeCompletionDialogProp,
                pointGrantedCardDialogProp = pointGrantedCardDialogProp,
                view = defaultView
            )
        }
    }
}