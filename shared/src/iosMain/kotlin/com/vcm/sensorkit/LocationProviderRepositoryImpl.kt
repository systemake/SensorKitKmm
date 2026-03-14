package com.vcm.sensorkit

import com.vcm.sensorkit.domain.models.LocationEvent
import com.vcm.sensorkit.domain.repository.LocationProviderRepository
import com.vcm.sensorkit.domain.repository.NativeLocationProvider
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLDistanceFilterNone
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.CoreLocation.kCLLocationAccuracyBestForNavigation
import platform.Foundation.NSError
import platform.darwin.NSObject

class LocationProviderRepositoryImpl(private val nativeProvider: NativeLocationProvider) :
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