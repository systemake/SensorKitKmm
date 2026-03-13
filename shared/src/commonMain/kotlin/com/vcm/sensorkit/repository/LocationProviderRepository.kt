package com.vcm.sensorkit.repository

import com.vcm.sensorkit.models.LocationEvent
import kotlinx.coroutines.flow.Flow

interface LocationProviderRepository {

    fun locationUpdates(): Flow<LocationEvent>

}