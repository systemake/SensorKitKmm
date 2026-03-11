package com.vcm.sensorkit.utils

import com.vcm.sensorkit.models.MotionEvent
import com.vcm.sensorkit.models.SensorEvent
import kotlin.math.atan2
import kotlin.math.sqrt

fun SensorEvent.toMotionEvent(heading : Float): MotionEvent {

    val roll = atan2(y, z)
    val pitch = atan2(-x, sqrt(y * y + z * z))
    val yaw = 0f

    return MotionEvent(
        roll = roll,
        pitch = pitch,
        yaw = yaw,
        heading = heading
    )
}