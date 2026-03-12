package com.vcm.sensorkit.models

sealed class HapticCommand {
    data class Cardinal(val direction: Int) : HapticCommand()

    data class Cadence(
        val intensity: Float,
        val duration: Long
    ) : HapticCommand()
}
