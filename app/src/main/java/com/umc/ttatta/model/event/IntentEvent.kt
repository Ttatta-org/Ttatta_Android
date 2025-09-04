package com.umc.ttatta.model.event

import com.umc.ttatta.intent.IntentType

data class IntentEvent(
    val intentType: IntentType,
    val onDismissed: () -> Unit,
)