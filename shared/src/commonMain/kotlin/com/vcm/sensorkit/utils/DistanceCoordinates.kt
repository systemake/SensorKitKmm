package com.vcm.sensorkit.utils

import com.vcm.sensorkit.models.LocationEvent
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object DistanceCoordinates {

    fun distance(a: LocationEvent, b: LocationEvent): Double {

        val earthRadius = 6371000.0

        val dLat = (b.latitude - a.latitude) * PI / 180
        val dLon = (b.longitude - a.longitude) * PI / 180

        val lat1 = a.latitude * PI / 180
        val lat2 = b.latitude * PI / 180

        val h =
            sin(dLat / 2).pow(2) +
                    cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(h), sqrt(1 - h))

        return earthRadius * c
    }
}