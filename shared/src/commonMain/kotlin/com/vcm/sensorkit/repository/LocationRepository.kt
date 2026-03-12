package com.vcm.sensorkit.repository

import com.vcm.sensorkit.models.Coordinate
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun locationUpdates(): Flow<Coordinate>

}