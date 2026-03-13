package com.vcm.sensorkit

import android.annotation.SuppressLint
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.vcm.sensorkit.models.LocationEvent
import com.vcm.sensorkit.repository.LocationProviderRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class LocationProviderRepositoryImpl(
    private val fusedClient: FusedLocationProviderClient
) : LocationProviderRepository {

    @SuppressLint("MissingPermission")
    override fun locationUpdates(): Flow<LocationEvent> = callbackFlow {

        val callback = object : LocationCallback() {

            override fun onLocationResult(result: LocationResult) {

                val location = result.lastLocation ?: return

                trySend(
                    LocationEvent(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                )
            }
        }

        fusedClient.requestLocationUpdates(
            LocationRequest.Builder(1000).build(),
            callback,
            Looper.getMainLooper()
        )

        awaitClose {
            fusedClient.removeLocationUpdates(callback)
        }
    }
}