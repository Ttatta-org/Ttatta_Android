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
        size = Size(21f, 20f),
        magnification = 1f,
    ),
    FOOTPRINT(
        title = "나의 발자국",
        icon = R.drawable.ic_my_footprint,
        size = Size(39f, 31f),
        magnification = 1.1f
    ),
    CHALLENGE(
        title = "나의 챌린지",
        icon = R.drawable.ic_my_challenge,
        size = Size(31f, 32f),
        magnification = 1f,
    ),
    MY_PAGE(
        title = "마이페이지",
        icon = R.drawable.ic_my_page,
        size = Size(33f, 31f),
        magnification = 1f
    )
}