package com.vcm.sensorkit.repository

import com.vcm.sensorkit.domain.models.LocationEvent
import com.vcm.sensorkit.domain.repository.LocationProviderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class MockLocationRepository : LocationProviderRepository {
    private val _locations = MutableSharedFlow<LocationEvent>(replay = 1)
    override fun locationUpdates(): Flow<LocationEvent> = _locations
}