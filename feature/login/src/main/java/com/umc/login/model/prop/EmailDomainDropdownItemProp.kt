package com.umc.login.model.prop

data class EmailDomainDropdownItemProp(
    val onClicked: () -> Unit,
    val domain: String,
)