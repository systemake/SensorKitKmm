package com.vcm.sensorkit.repository

import com.vcm.sensorkit.models.HapticCommand

interface VibrationEffectRepository {
    fun vibrate(command: HapticCommand)
}