package com.vcm.sensorkit.models

import kotlinx.serialization.Serializable

@Serializable
enum class HapticType {
    TRANSIENT,
    CONTINUOUS
}