package com.umc.ttatta.test.mypage

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.umc.core.repository.UserRepository
import com.umc.mypage.MyPageApp
import com.umc.mypage.MyPageScreen
import com.umc.mypage.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class TestActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository  // ✅ UserRepository 주입

    private val viewModel: MyPageViewModel by viewModels()  // ✅ ViewModel 주입

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MyPageActivity", "🔥 onCreate() 시작됨")

        setContent {
            MyPageApp(
                viewModel = viewModel,
                onLoginCanceled  = {})  // ✅ MyPageApp을 실행하여 UI 확인
        }

        // ✅ 테스트 실행
        prepareTest()
    }

    private fun prepareTest() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // ✅ 사용자 로그인 (회원가입 체크 후 로그인)
                if (!userRepository.isIdAlreadyOccupied(id = TestValues.ID)) {
                    userRepository.join(
                        id = TestValues.ID,
                        password = TestValues.PASSWORD,
                        name = TestValues.NAME,
                        nickname = TestValues.NICKNAME,
                        email = TestValues.EMAIL
                    )
                }
                userRepository.login(id = TestValues.ID, password = TestValues.PASSWORD)

                Log.d("MyPageActivity", "🚀 유저 정보 로드 테스트 시작...")

//                // ✅ 유저 정보 불러오기 테스트
//                val userInfo = userRepository.getUserInfo()
//                Log.d("MyPageActivity", "✅ 유저 정보: $userInfo")

                // ✅ 유저 정보 불러오기 테스트
                val userInfo = try {
                    Log.d("MyPageActivity", "🔄 userRepository.getUserInfo() 호출")
                    userRepository.getUserInfo()

                } catch (e: Exception) {
                    Log.e("MyPageActivity", "❌ 유저 정보 불러오기 실패: ${e.message}")
                    return@launch // 실패 시 테스트 중단
                }
                Log.d("MyPageActivity", "✅ 유저 정보: $userInfo")

                // ✅ ViewModel을 사용해서 유저 정보 불러오기
                withContext(Dispatchers.Main) {
                    viewModel.loadUserInfo()
                }

//                // ✅ 로그아웃 테스트
//                userRepository.logout()
//                Log.d("MyPageActivity", "✅ 로그아웃 완료")
//
//                // ✅ 유저 탈퇴 테스트
//                userRepository.leaveUser()
//                Log.d("MyPageActivity", "✅ 유저 탈퇴 완료")

            } catch (e: Exception) {
                Log.e("MyPageActivity", "❌ 테스트 실패: ${e.message}")
            }
        }
    }
}

