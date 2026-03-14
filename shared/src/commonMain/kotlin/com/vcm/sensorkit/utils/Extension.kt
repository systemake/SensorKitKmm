package com.vcm.sensorkit.utils

import com.vcm.sensorkit.domain.models.HapticPattern
import com.vcm.sensorkit.domain.models.HapticType
import com.vcm.sensorkit.domain.models.MotionEvent
import com.vcm.sensorkit.domain.models.SensorEvent
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

fun HapticPattern.validate(): Boolean {

    return intensity in 0f..1f &&
            sharpness in 0f..1f &&
            attack in 0f..1f &&
            decay in 0f..1f &&
            duration > 0
}

val HapticType.Companion.TRANSIENT_TYPE: HapticType get() = HapticType.TRANSIENT