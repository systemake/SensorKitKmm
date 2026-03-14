package com.vcm.sensorkit.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class HapticPattern(
    val id: String,
    val name: String,
    val intensity: Float,
    val sharpness: Float,
    val duration: Long,
    val attack: Float,
    val decay: Float,
    val type: HapticType
)