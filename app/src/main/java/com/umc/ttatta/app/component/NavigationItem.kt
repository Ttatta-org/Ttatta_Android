package com.umc.ttatta.app.component

import androidx.annotation.DrawableRes
import androidx.compose.ui.geometry.Size
import com.umc.ttatta.app.R

enum class NavigationItem(
    val title: String,
    @field:DrawableRes val icon: Int,
    val size: Size,
    val magnification: Float,
) {
    DIARY(
        title = "일기 보관함",
        icon = R.drawable.ic_diary_locker,
        size = Size(23.02f, 21.02f),
        magnification = 1f,
    ),
    FOOTPRINT(
        title = "나의 발자국",
        icon = R.drawable.ic_my_footprint,
        size = Size(24.5f, 22f),
        magnification = 1f
    ),
    CHALLENGE(
        title = "나의 챌린지",
        icon = R.drawable.ic_my_challenge,
        size = Size(22f, 22f),
        magnification = 1f,
    ),
    MY_PAGE(
        title = "마이페이지",
        icon = R.drawable.ic_my_page,
        size = Size(24f, 22f),
        magnification = 1f
    )
}