package com.vcm.sensorkit

import com.vcm.sensorkit.domain.models.SensorEvent
import com.vcm.sensorkit.domain.models.SensorTypes
import com.vcm.sensorkit.domain.repository.SensorRepository
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.CoreMotion.CMMotionManager
import platform.CoreMotion.CMPedometer
import platform.Foundation.NSOperationQueue
import kotlin.math.sqrt
import kotlin.time.Clock

class IOSSensorRepositoryImpl(
    private val sensorType: Int
) : SensorRepository {

    private val motionManager = CMMotionManager()
    private val pedometer = CMPedometer()

    @OptIn(ExperimentalForeignApi::class)
    override fun sensorEvents(): Flow<SensorEvent> = callbackFlow {
        var lastStepCount = 0
        when (sensorType) {

            SensorTypes.TYPE_ROTATION_VECTOR -> {

                motionManager.deviceMotionUpdateInterval = 0.1
                motionManager.startDeviceMotionUpdatesToQueue(
                    NSOperationQueue.mainQueue()
                ) { motion, error ->

                    motion?.attitude?.let {

                        val event = SensorEvent(
                            x = it.roll.toFloat(),
                            y = it.pitch.toFloat(),
                            z = it.yaw.toFloat(),
                            timestamp = Clock.System.now().toEpochMilliseconds()
                        )

                        trySend(event)
                    }

                    error?.let {
                        println("Motion error: $it")
                    }
                }
            }

            SensorTypes.TYPE_STEP_DETECTOR -> {

                if (CMPedometer.isStepCountingAvailable()) {

                    pedometer.startPedometerUpdatesFromDate(
                        platform.Foundation.NSDate()
                    ) { data, error ->

                        data?.let {

                            println("Pedometer data: ${it.numberOfSteps.floatValue}")
                            val currentTotal = it.numberOfSteps.intValue
                            val newSteps = currentTotal - lastStepCount

                            if (newSteps > 0) {
                                lastStepCount = currentTotal

                                repeat(newSteps) { index ->
                                    val simulatedDelay = index * 400L
                                    val event = SensorEvent(
                                        x = 1f,
                                        y = 0f,
                                        z = 0f,
                                        timestamp = Clock.System.now()
                                            .toEpochMilliseconds() + simulatedDelay
                                    )

                                    trySend(event)
                                }
                            }
                        }
                        error?.let {
                            println("Pedometer error: $it")
                        }
                    }
                }
                if (motionManager.isAccelerometerAvailable()) {
                    motionManager.accelerometerUpdateInterval = 0.1
                    motionManager.startAccelerometerUpdatesToQueue(NSOperationQueue.mainQueue()) { data, _ ->
                        data?.acceleration?.let { acc ->
                            acc.useContents {
                                val magnitude = sqrt(x * x + y * y + z * z)
                                if (magnitude > 1.3) {
                                    trySend(
                                        SensorEvent(
                                            x = 1f,
                                            y = 0f,
                                            z = 0f,
                                            timestamp = Clock.System.now().toEpochMilliseconds()
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        awaitClose {
            pedometer.stopPedometerUpdates()
            motionManager.stopAccelerometerUpdates()
            motionManager.stopDeviceMotionUpdates()
        }
    }

}