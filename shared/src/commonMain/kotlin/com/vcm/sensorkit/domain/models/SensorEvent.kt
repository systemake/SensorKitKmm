package com.vcm.sensorkit.domain.models

data class SensorEvent(
    val x: Float,
    val y: Float,
    val z: Float,
    val timestamp: Long = 0L
)

