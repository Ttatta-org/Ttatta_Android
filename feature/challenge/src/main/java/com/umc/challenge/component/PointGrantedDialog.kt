package com.umc.challenge.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.design.R
import com.umc.design.theme.LocalColorTheme

data class PointGrantedDialogProp(
    val onDismissed: () -> Unit,
    val onGoShop: () -> Unit
)

@Composable
fun PointGrantedDialog(
    prop: PointGrantedDialogProp
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 27.dp, vertical = 22.dp)
            .background(Color.White, RoundedCornerShape(28.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 헤더 이미지
        Box(
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_header_deco), // 헤더 데코 이미지 리소스
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp) // 이미지 크기 설정
            )
        }

        Text(
            text = "10포인트를 받았어요!",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
                color = Color.Black,
                textAlign = TextAlign.Center
            ),
        )

        Spacer(modifier = Modifier.height(13.dp))

        Text(
            text = "또또와 뚜뚜를 꾸밀 아이템을\n구경하러 가볼까요?",
            style = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.W400,
                color = LocalColorTheme.current.grey[700],
                textAlign = TextAlign.Center
            ),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 21.dp, bottom = 2.dp),
            horizontalArrangement =  Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(13.dp))
                    .clip(RoundedCornerShape(13.dp))
                    .border(1.dp, LocalColorTheme.current.primary[200], RoundedCornerShape(13.dp))
                    .clickable(role = Role.Button) { prop.onDismissed() }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "취소",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W600,
                        color = LocalColorTheme.current.primary[200],
                        textAlign = TextAlign.Center
                    ),
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(LocalColorTheme.current.primary[400], RoundedCornerShape(13.dp))
                    .clip(RoundedCornerShape(13.dp))
                    .clickable(role = Role.Button) { prop.onGoShop() }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "구경하러 가기",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W600,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    ),
                )
            }
        }
    }
}

val previewPointGrantedDialogProp = PointGrantedDialogProp(
    onDismissed = {},
    onGoShop = {}
)

@Preview(showBackground = true)
@Composable
fun PreviewPointGrantedDialog() {
    PointGrantedDialog(
        prop = previewPointGrantedDialogProp
    )
}