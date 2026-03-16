package com.vcm.sensorkit.repository

import com.vcm.sensorkit.domain.models.SensorEvent
import com.vcm.sensorkit.domain.repository.SensorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class MockSensorRepository : SensorRepository {
    private val _events = MutableSharedFlow<SensorEvent>(replay = 1)
    suspend fun emit(event: SensorEvent) = _events.emit(event)
    override fun sensorEvents(): Flow<SensorEvent> = _events
}