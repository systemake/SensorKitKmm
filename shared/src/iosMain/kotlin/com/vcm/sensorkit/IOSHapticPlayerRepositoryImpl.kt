package com.vcm.sensorkit

import com.vcm.sensorkit.domain.models.HapticPattern
import com.vcm.sensorkit.domain.models.HapticType
import com.vcm.sensorkit.domain.repository.HapticPlayerRepository
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreHaptics.CHHapticEngine
import platform.CoreHaptics.CHHapticEvent
import platform.CoreHaptics.CHHapticEventParameter
import platform.CoreHaptics.CHHapticEventParameterIDHapticIntensity
import platform.CoreHaptics.CHHapticEventParameterIDHapticSharpness
import platform.CoreHaptics.CHHapticEventTypeHapticContinuous
import platform.CoreHaptics.CHHapticEventTypeHapticTransient
import platform.CoreHaptics.CHHapticPattern
import platform.Foundation.NSError

@OptIn(ExperimentalForeignApi::class)
class IOSHapticPlayerRepositoryImpl : HapticPlayerRepository {

    private var engine: CHHapticEngine? = null

    init {
        try {
            val hapticEngine = CHHapticEngine(andReturnError = null)
            hapticEngine.startAndReturnError(null)
            engine = hapticEngine
        } catch (e: Exception) {
            println("Haptic Engine failed to initialize: ${e.message}")
        }

    }


    @OptIn(BetaInteropApi::class)
    override fun play(pattern: HapticPattern) {
        val currentEngine = engine ?: run {
            println("HapticPlayer: Engine is null, cannot play")
            return
        }

        val intensityParam =
            CHHapticEventParameter(CHHapticEventParameterIDHapticIntensity, pattern.intensity)
        val sharpnessParam =
            CHHapticEventParameter(CHHapticEventParameterIDHapticSharpness, pattern.sharpness)

        val eventType = when (pattern.type) {
            HapticType.TRANSIENT -> CHHapticEventTypeHapticTransient
            HapticType.CONTINUOUS -> CHHapticEventTypeHapticContinuous
        }

        val event = CHHapticEvent(
            eventType = eventType,
            parameters = listOf(intensityParam, sharpnessParam),
            relativeTime = 0.0,
            duration = pattern.duration.toDouble() / 1000.0
        )

        val errorPtr = nativeHeap.alloc<ObjCObjectVar<NSError?>>()
        val eventsList = listOf(event)
        val paramsList = listOf<CHHapticEventParameter>()

        val patternObj = CHHapticPattern(
            events = eventsList,
            parameters = paramsList,
            error = errorPtr.ptr
        )


        if (errorPtr.value != null) {
            println("Error creating CHHapticPattern: ${errorPtr.value}")
        }


        val playerError = nativeHeap.alloc<ObjCObjectVar<NSError?>>()
        if (playerError.value != null) {
            println("Error creating CHHapticPlayer: ${playerError.value}")
        }


        val startError = nativeHeap.alloc<ObjCObjectVar<NSError?>>()

        val player = currentEngine.createPlayerWithPattern(patternObj, errorPtr.ptr)
        player?.startAtTime(0.0, error = startError.ptr)

        if (startError.value != null) {
            println("Error starting player: ${startError.value}")
        }
    }

}