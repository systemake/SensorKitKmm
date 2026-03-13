package com.vcm.sensorkit.repository

import com.vcm.sensorkit.models.HapticPattern

interface HapticPlayerRepository {
    fun play(pattern: HapticPattern)
}