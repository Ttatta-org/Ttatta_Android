package com.umc.home.components

// DetailModal.kt
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// CustomBottomSheet가 있는 패키지 import
import com.umc.design.component.CustomBottomSheet
import com.umc.design.theme.LocalColorTheme

@OptIn(ExperimentalMaterial3Api::class)
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
                .padding(start = 22.dp, end = 22.dp, top = 8.dp, bottom = 32.dp) // 내부 여백 조정
        ) {
            // 1. 수정하기 버튼
            Text(
                text = "수정하기",
                fontSize = 15.sp,
                fontWeight = FontWeight.W700,
                lineHeight = 20.sp,
                color = LocalColorTheme.current.grey[700],
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onEdit()    // 수정 로직 실행
                        onDismiss() // 모달 닫기
                    }
                    .padding(vertical = 4.dp, horizontal = 12.dp) // 터치 영역 확보를 위한 패딩
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. 삭제하기 버튼
            Text(
                text = "삭제하기",
                fontSize = 15.sp,
                fontWeight = FontWeight.W700,
                color = LocalColorTheme.current.grey[700],
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDelete()  // 삭제 로직 실행
                        onDismiss() // 모달 닫기
                    }
                    .padding(vertical = 4.dp, horizontal = 12.dp)
            )
        }
    }
}