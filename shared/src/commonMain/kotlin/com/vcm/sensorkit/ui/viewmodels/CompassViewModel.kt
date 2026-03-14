package com.vcm.sensorkit.ui.viewmodels

import com.vcm.sensorkit.domain.models.HapticCommand
import com.vcm.sensorkit.domain.models.MotionEvent
import com.vcm.sensorkit.domain.models.SensorEvent
import com.vcm.sensorkit.domain.repository.SensorRepository
import com.vcm.sensorkit.utils.toMotionEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2


class CompassViewModel(
    private val sensorRepository: SensorRepository
) {

    private var lastDirection: Int? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _sensorState = MutableStateFlow<SensorEvent?>(null)
    val sensorState: StateFlow<SensorEvent?> = _sensorState

    private val _motionEvent = MutableStateFlow<MotionEvent?>(null)
    val motionEvent: StateFlow<MotionEvent?> = _motionEvent

    private val _hapticCommand = MutableSharedFlow<HapticCommand>()
    val hapticCommand: SharedFlow<HapticCommand> = _hapticCommand


    @OptIn(FlowPreview::class)
    fun startSensors() {
        scope.launch {
            sensorRepository.sensorEvents().sample(50)
                .collect { event ->
                    _sensorState.value = event
                    val heading = calculateHeading(event)
                    val motionEvent = event.toMotionEvent(heading)
                    _motionEvent.value = motionEvent
                    checkCardinalDirection(heading)
                }
        }
    }

    private fun calculateHeading(event: SensorEvent): Float {

        val heading = atan2(event.y, event.x)
        var degrees = (heading * 180f / PI).toFloat()

        if (degrees < 0) {
            degrees += 360f
        }
        return degrees
    }

    private suspend fun checkCardinalDirection(heading: Float) {

        val directions = listOf(0f, 90f, 180f, 270f)

        for (direction in directions) {

            if (abs(heading - direction) < 5f) {

                if (lastDirection != direction.toInt()) {
                    lastDirection = direction.toInt()
                    _hapticCommand.emit(HapticCommand.Cardinal(direction.toInt()))

                }

                return
            }
        }
        lastDirection = null
    }

    fun stop() {
        scope.cancel()
    }

}