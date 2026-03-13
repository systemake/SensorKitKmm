package com.vcm.sensorkit.utils

import com.vcm.sensorkit.models.HapticPattern
import com.vcm.sensorkit.models.MotionEvent
import com.vcm.sensorkit.models.SensorEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
