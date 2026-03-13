package com.vcm.sensorkit.models

data class TrailSession(
    val locationEvents : List<LocationEvent> = emptyList(),
    val distanceMeters: Double = 0.0,
    val elapsedTimeMillis: Long = 0
)