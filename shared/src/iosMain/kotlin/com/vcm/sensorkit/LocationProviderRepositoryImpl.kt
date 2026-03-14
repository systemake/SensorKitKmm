package com.vcm.sensorkit

import com.vcm.sensorkit.models.LocationEvent
import com.vcm.sensorkit.repository.LocationProviderRepository
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.darwin.NSObject

class LocationProviderRepositoryImpl : LocationProviderRepository {

    @OptIn(ExperimentalForeignApi::class)
    override fun locationUpdates(): Flow<LocationEvent> = callbackFlow {

        val manager = CLLocationManager()
        manager.requestWhenInUseAuthorization()
        manager.startUpdatingLocation()

        val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {

            override fun locationManager(
                manager: CLLocationManager,
                didUpdateLocations: List<*>
            ) {

                val location = didUpdateLocations.last() as CLLocation

                trySend(
                    location.coordinate.useContents {
                        LocationEvent(
                            latitude = latitude,
                            longitude = longitude
                        )
                    }
                )
            }
        }

        manager.delegate = delegate

        awaitClose {
            manager.stopUpdatingLocation()
        }
    }
}