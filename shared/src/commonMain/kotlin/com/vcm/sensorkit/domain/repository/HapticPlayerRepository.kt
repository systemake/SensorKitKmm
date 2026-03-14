package com.vcm.sensorkit.domain.repository

import com.vcm.sensorkit.domain.models.HapticPattern

interface HapticPlayerRepository {
    fun play(pattern: HapticPattern)
}