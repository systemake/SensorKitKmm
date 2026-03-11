package com.vcm.sensorkit.models

sealed class HapticCommand {
    data class Cardinal(val direction: Int) : HapticCommand()
}