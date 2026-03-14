package com.vcm.sensorkit.domain.repository

import com.vcm.sensorkit.domain.models.SensorEvent
import kotlinx.coroutines.flow.Flow

interface SensorRepository {
    fun sensorEvents(): Flow<SensorEvent>
}