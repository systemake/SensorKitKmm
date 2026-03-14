package com.vcm.sensorkit.domain.models

sealed class HapticCommand {
    data class Cardinal(val direction: Int) : HapticCommand()

    data class Cadence(
        val intensity: Float,
        val duration: Long
    ) : HapticCommand()

    object Stop : HapticCommand()
}
