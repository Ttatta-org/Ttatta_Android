package com.umc.ttatta.model.prop

import com.umc.ttatta.component.NavigationItem

data class NavigationBarProp(
    val currentNavigationItem: NavigationItem?,
    val onNavigate: (NavigationItem) -> Unit,
    val onCenterButtonClicked: () -> Unit,
)