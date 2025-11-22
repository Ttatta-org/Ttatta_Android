package com.umc.ttatta.app.model.event

import com.umc.ttatta.app.intent.IntentType

data class IntentEvent(
    val intentType: IntentType,
    val onDismissed: () -> Unit,
)