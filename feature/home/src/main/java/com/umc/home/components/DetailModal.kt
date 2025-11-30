package com.umc.home.components

// DetailModal.kt
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// CustomBottomSheet가 있는 패키지 import
import com.umc.design.component.CustomBottomSheet

@Composable
fun DetailModal(
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    // 사용자가 제공한 CustomBottomSheet 사용
    CustomBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        // CustomBottomSheet의 content 영역
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 30.dp) // 내부 여백 조정
        ) {
            // 1. 수정하기 버튼
            Text(
                text = "수정하기",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF4B4B4B),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onEdit()    // 수정 로직 실행
                        onDismiss() // 모달 닫기
                    }
                    .padding(vertical = 8.dp) // 터치 영역 확보를 위한 패딩
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 2. 삭제하기 버튼
            Text(
                text = "삭제하기",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B4B4B),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDelete()  // 삭제 로직 실행
                        onDismiss() // 모달 닫기
                    }
                    .padding(vertical = 8.dp)
            )
        }
    }
}