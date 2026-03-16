package com.vcm.sensorkit

import com.vcm.sensorkit.domain.models.LocationEvent
import com.vcm.sensorkit.utils.DistanceCoordinates
import com.vcm.sensorkit.utils.calculateCadence
import com.vcm.sensorkit.utils.toHapticIntensity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TrailLogicTest {

    @Test
    fun testCadenceToIntensityMapping() {
        assertEquals(0.2f, 0.5f.toHapticIntensity())
        assertEquals(0.5f, 1.5f.toHapticIntensity())
        assertEquals(0.8f, 2.5f.toHapticIntensity())
        assertEquals(1.0f, 4.0f.toHapticIntensity())
    }

    @Test
    fun testCalculateCadence() {
        val start = 1_000_000_000L
        val end = 2_000_000_000L

        val cadence = start.calculateCadence(end)
        assertEquals(1.0f, cadence)
    }

    @Test
    fun testDistanceCalculation() {

        val pointA = LocationEvent(-12.0464, -77.0428)
        val pointB = LocationEvent(-12.0474, -77.0428)

        val distance = DistanceCoordinates.distance(pointA, pointB)

        assertTrue(distance > 110.0 && distance < 112.0)
    }
}