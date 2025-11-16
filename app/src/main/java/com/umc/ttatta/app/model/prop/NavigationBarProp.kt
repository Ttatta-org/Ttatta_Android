package com.umc.ttatta.app.model.prop

import com.umc.ttatta.app.component.NavigationItem

data class NavigationBarProp(
    val currentNavigationItem: NavigationItem?,
    val onNavigate: (NavigationItem) -> Unit,
    val onCenterButtonClicked: () -> Unit,
)