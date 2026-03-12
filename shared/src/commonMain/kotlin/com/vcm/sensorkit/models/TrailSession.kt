package com.vcm.sensorkit.models

data class TrailSession(
    val coordinates : List<Coordinate> = emptyList(),
    val distanceMeters: Double = 0.0
)