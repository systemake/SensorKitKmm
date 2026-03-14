package com.vcm.sensorkit.domain.repository

import com.vcm.sensorkit.domain.models.HapticCommand

interface VibrationEffectRepository {
    fun vibrate(command: HapticCommand)
}