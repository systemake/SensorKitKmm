package com.vcm.sensorkit

import com.vcm.sensorkit.models.Coordinate
import com.vcm.sensorkit.repository.LocationRepository
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.darwin.NSObject

class LocationRepositoryImpl : LocationRepository {

    @OptIn(ExperimentalForeignApi::class)
    override fun locationUpdates(): Flow<Coordinate> = callbackFlow {

        val manager = CLLocationManager()

        val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {

            override fun locationManager(
                manager: CLLocationManager,
                didUpdateLocations: List<*>
            ) {

                val location = didUpdateLocations.last() as CLLocation

                trySend(
                    location.coordinate.useContents {
                        Coordinate(
                            latitude = latitude,
                            longitude = longitude
                        )
                    }
                )
            }
        }

        manager.delegate = delegate
        manager.startUpdatingLocation()

        awaitClose {
            manager.stopUpdatingLocation()
        }
    }
}