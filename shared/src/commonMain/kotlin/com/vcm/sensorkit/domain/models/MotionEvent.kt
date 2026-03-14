package com.vcm.sensorkit.domain.models

data class MotionEvent(
    val roll: Float,
    val pitch: Float,
    val yaw: Float,
    val heading: Float
)