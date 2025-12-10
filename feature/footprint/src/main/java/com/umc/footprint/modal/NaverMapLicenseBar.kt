package com.umc.footprint.modal

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naver.maps.map.app.LegalNoticeActivity
import com.naver.maps.map.app.OpenSourceLicenseActivity
import com.umc.design.component.CustomBottomSheet
import com.umc.design.theme.LocalColorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NaverMapLicenseBar(
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current

    CustomBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(start = 22.dp, end = 22.dp, top = 8.dp, bottom = 32.dp),
        ) {
            listOf(
                "네이버 지도 SDK 법적 공지" to {
                    val intent = Intent(context, LegalNoticeActivity::class.java)
                    context.startActivity(intent)
                },
                "오픈소스 라이선스" to {
                    val intent = Intent(context, OpenSourceLicenseActivity::class.java)
                    context.startActivity(intent)
                },
            ).forEach { (text, onClicked) ->
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .clickable { onClicked() }
                ) {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 4.dp, horizontal = 12.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = text,
                            fontWeight = FontWeight.W700,
                            fontSize = 15.sp,
                            lineHeight = 20.sp,
                            color = LocalColorTheme.current.grey[700],
                        )
                    }
                }
            }
        }
    }
}