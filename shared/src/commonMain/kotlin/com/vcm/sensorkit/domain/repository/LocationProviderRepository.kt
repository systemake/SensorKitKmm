package com.vcm.sensorkit.domain.repository

import com.vcm.sensorkit.domain.models.LocationEvent
import kotlinx.coroutines.flow.Flow

interface LocationProviderRepository {

    fun locationUpdates(): Flow<LocationEvent>

}