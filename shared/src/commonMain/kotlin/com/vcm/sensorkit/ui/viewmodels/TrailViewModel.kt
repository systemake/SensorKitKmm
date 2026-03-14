package com.vcm.sensorkit.ui.viewmodels

import com.vcm.sensorkit.domain.models.HapticCommand
import com.vcm.sensorkit.domain.models.TrailSession
import com.vcm.sensorkit.domain.repository.LocationProviderRepository
import com.vcm.sensorkit.domain.repository.SensorRepository
import com.vcm.sensorkit.utils.DistanceCoordinates
import com.vcm.sensorkit.utils.calculateCadence
import com.vcm.sensorkit.utils.toHapticIntensity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
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
                    val cadence = lastTimestamp.calculateCadence(event.timestamp)
                    lastTimestamp = event.timestamp
                    val intensity = cadence.toHapticIntensity()
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
                delay(500)
            }
        }
    }
}