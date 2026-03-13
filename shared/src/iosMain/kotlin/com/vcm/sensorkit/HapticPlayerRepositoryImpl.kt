package com.vcm.sensorkit

import com.vcm.sensorkit.models.HapticPattern
import com.vcm.sensorkit.models.HapticType
import com.vcm.sensorkit.repository.HapticPlayerRepository
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
class HapticPlayerRepositoryImpl : HapticPlayerRepository {

    private val engine: CHHapticEngine = CHHapticEngine()

    init {
        engine.startAndReturnError(null)
    }


    @OptIn(BetaInteropApi::class)
    override fun play(pattern: HapticPattern) {
        // Crear evento haptic según tipo
        val intensityParam =
            CHHapticEventParameter(CHHapticEventParameterIDHapticIntensity, pattern.intensity)
        val sharpnessParam = CHHapticEventParameter(CHHapticEventParameterIDHapticSharpness, pattern.sharpness)

        val eventType = when(pattern.type) {
            HapticType.TRANSIENT -> CHHapticEventTypeHapticTransient
            HapticType.CONTINUOUS -> CHHapticEventTypeHapticContinuous
        }

        val event = CHHapticEvent(
            eventType = eventType,
            parameters = listOf(intensityParam, sharpnessParam),
            relativeTime = 0.0, // empezar ahora
            duration = pattern.duration.toDouble() / 1000.0
        )

        val errorPtr = nativeHeap.alloc<ObjCObjectVar<NSError?>>()
        val eventsList = listOf(event) as List<CHHapticEvent>
        val paramsList = listOf<CHHapticEventParameter>()

        val patternObj = CHHapticPattern(
            events = eventsList,
            parameters = paramsList,
            error = errorPtr.ptr
        )


        if (errorPtr.value != null) {
            println("Error creating CHHapticPattern: ${errorPtr.value}")
        }

// Crear el player
        val playerError = nativeHeap.alloc<ObjCObjectVar<NSError?>>()
        val player = engine.createPlayerWithPattern(patternObj, error = playerError.ptr)

        if (playerError.value != null) {
            println("Error creating CHHapticPlayer: ${playerError.value}")
        }

// Reproducir
        val startError = nativeHeap.alloc<ObjCObjectVar<NSError?>>()
        player?.startAtTime(0.0, error = startError.ptr)

        if (startError.value != null) {
            println("Error starting player: ${startError.value}")
        }
    }

}