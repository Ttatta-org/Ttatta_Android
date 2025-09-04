package com.umc.ttatta.model.routing

class RecordEntryInfo(
    val mode: RecordRoutingOption,
    val challengeId: Long?,
) {
    enum class RecordRoutingOption {
        CAMERA,
        GALLERY,
    }
}