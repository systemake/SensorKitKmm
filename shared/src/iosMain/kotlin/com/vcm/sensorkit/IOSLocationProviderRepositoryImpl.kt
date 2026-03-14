package com.vcm.sensorkit

import com.vcm.sensorkit.domain.models.LocationEvent
import com.vcm.sensorkit.domain.repository.LocationProviderRepository
import com.vcm.sensorkit.domain.repository.NativeLocationProvider
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class IOSLocationProviderRepositoryImpl(private val nativeProvider: NativeLocationProvider) :
    LocationProviderRepository {

    @OptIn(ExperimentalForeignApi::class)
    override fun locationUpdates(): Flow<LocationEvent> = callbackFlow {

        nativeProvider.setListener { event ->
            trySend(event)
        }

        awaitClose {
            nativeProvider.setListener { /* cleann */ }
        }
    }
}