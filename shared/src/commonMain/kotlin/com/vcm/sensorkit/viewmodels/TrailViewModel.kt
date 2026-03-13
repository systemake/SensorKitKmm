package com.vcm.sensorkit.viewmodels

import com.vcm.sensorkit.models.HapticCommand
import com.vcm.sensorkit.models.SensorEvent
import com.vcm.sensorkit.models.TrailSession
import com.vcm.sensorkit.repository.LocationProviderRepository
import com.vcm.sensorkit.repository.SensorRepository
import com.vcm.sensorkit.utils.DistanceCoordinates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class TrailViewModel(
    private val locationProviderRepository: LocationProviderRepository,
    private val sensorRepository: SensorRepository
) {
    private var lastStepTimestamp: TimeMark = TimeSource.Monotonic.markNow()
    private var lastTimestamp = 0L
    private var isStopped = false
    private val _trail = MutableStateFlow(TrailSession())
    val trail: StateFlow<TrailSession> = _trail

    private val _hapticCommand = MutableSharedFlow<HapticCommand>()
    val hapticCommand: SharedFlow<HapticCommand> = _hapticCommand

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun startTracking() {

        scope.launch {

            locationProviderRepository.locationUpdates()
                .collect { coordinate ->

                    val current = _trail.value
                    val last = current.locationEvents.lastOrNull()

                    val distance =
                        if (last != null)
                            DistanceCoordinates.distance(last, coordinate)
                        else
                            0.0

                    val updated = current.copy(
                        locationEvents = current.locationEvents + coordinate,
                        distanceMeters = current.distanceMeters + distance
                    )
                    _trail.value = updated
                }
        }

        scope.launch {

            sensorRepository.sensorEvents()
                .collect { event ->
                    isStopped = false
                    lastStepTimestamp = TimeSource.Monotonic.markNow()
                    val cadence = calculateCadence(event)
                    val intensity = cadenceToIntensity(cadence)
                    println("Cadence: $cadence")


                    if (intensity > 0f) {
                        val command = HapticCommand.Cadence(
                            intensity = intensity,
                            duration = 80
                        )

                        _hapticCommand.emit(command)
                    }
                }
        }

        scope.launch {

            while (true) {
                if (lastStepTimestamp.elapsedNow().inWholeSeconds > 2 && !isStopped) {
                    _hapticCommand.emit(HapticCommand.Stop)
                    isStopped = true
                }
            }
        }
    }



    private fun calculateCadence(event: SensorEvent): Float {

        val now = event.timestamp

        if (lastTimestamp == 0L) {
            lastTimestamp = now
            return 0f
        }

        val deltaSeconds = (now - lastTimestamp) / 1000f

        lastTimestamp = now

        if (deltaSeconds == 0f) return 0f

        return 1f / deltaSeconds
    }

    private fun cadenceToIntensity(cadence: Float): Float {

        return when {
            cadence < 1f -> 0.2f
            cadence < 2f -> 0.5f
            cadence < 3f -> 0.8f
            else -> 1f
        }
    }
}