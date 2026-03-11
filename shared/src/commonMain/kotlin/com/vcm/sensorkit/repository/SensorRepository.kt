package com.vcm.sensorkit.repository

import com.vcm.sensorkit.models.SensorEvent
import kotlinx.coroutines.flow.Flow

interface SensorRepository {
    fun sensorEvents(): Flow<SensorEvent>
}