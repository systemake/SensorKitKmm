package com.vcm.sensorkit.ui.viewmodels

import com.vcm.sensorkit.domain.models.HapticCommand
import com.vcm.sensorkit.domain.models.SensorEvent
import com.vcm.sensorkit.domain.models.TrailSession
import com.vcm.sensorkit.domain.repository.LocationProviderRepository
import com.vcm.sensorkit.domain.repository.SensorRepository
import com.vcm.sensorkit.utils.DistanceCoordinates
import com.vcm.sensorkit.utils.calculateCadence
import com.vcm.sensorkit.utils.toHapticIntensity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class TrailViewModel(
    private val locationProviderRepository: LocationProviderRepository,
    private val sensorRepository: SensorRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private var lastStepTimestamp: TimeMark = TimeSource.Monotonic.markNow()
    private var lastTimestamp = 0L
    private var isStopped = false
    private val _trail = MutableStateFlow(TrailSession())
    val trail: StateFlow<TrailSession> = _trail

    private val _hapticCommand = MutableSharedFlow<HapticCommand>()
    val hapticCommand: SharedFlow<HapticCommand> = _hapticCommand

    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    @OptIn(ExperimentalCoroutinesApi::class)
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
                .transformLatest<SensorEvent, HapticCommand> { event ->
                    lastStepTimestamp = TimeSource.Monotonic.markNow()
                    isStopped = false
                    val cadence = lastTimestamp.calculateCadence(event.timestamp)
                    lastTimestamp = event.timestamp
                    val intensity = cadence.toHapticIntensity()
                    println("Cadence: $cadence")
                    if (intensity > 0f) {
                        emit(HapticCommand.Cadence(intensity, 80))
                    }
                    delay(3000)
                    if (!isStopped) {
                        isStopped = true
                        emit(HapticCommand.Stop)
                        println("stopp: stooooop")
                    }
                }.collect { command ->

                    _hapticCommand.emit(command)
                }
        }

    }
}