package com.umc.ttatta.app.component

import androidx.annotation.DrawableRes
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.umc.ttatta.app.R

enum class NavigationItem(
    val title: String,
    @field:DrawableRes val unselectedIcon: Int,
    @field:DrawableRes val selectedIcon: Int,
    val size: DpSize,
) {
    DIARY(
        title = "일기 보관함",
        unselectedIcon = R.drawable.ic_diary_locker,
        selectedIcon = R.drawable.ic_diary_locker,
        size = DpSize(23.02.dp, 21.02.dp),
    ),
    FOOTPRINT(
        title = "나의 발자국",
        unselectedIcon = R.drawable.ic_my_footprint,
        selectedIcon = R.drawable.ic_my_footprint_filled,
        size = DpSize(24.5.dp, 22.dp),
    ),
    CHALLENGE(
        title = "나의 챌린지",
        unselectedIcon = R.drawable.ic_my_challenge,
        selectedIcon = R.drawable.ic_my_challenge,
        size = DpSize(22.dp, 22.dp),
    ),
    MY_PAGE(
        title = "마이페이지",
        unselectedIcon = R.drawable.ic_my_page,
        selectedIcon = R.drawable.ic_my_page,
        size = DpSize(24.dp, 22.dp),
    )
}