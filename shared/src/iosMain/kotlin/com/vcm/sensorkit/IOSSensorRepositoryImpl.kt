package com.vcm.sensorkit

import com.vcm.sensorkit.domain.models.SensorEvent
import com.vcm.sensorkit.domain.models.SensorTypes
import com.vcm.sensorkit.domain.repository.SensorRepository
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.CoreMotion.CMMotionManager
import platform.CoreMotion.CMPedometer
import platform.Foundation.NSOperationQueue
import kotlin.time.Clock

class IOSSensorRepositoryImpl(
    private val sensorType: Int
) : SensorRepository {

    private val motionManager = CMMotionManager()
    private val pedometer = CMPedometer()

    @OptIn(ExperimentalForeignApi::class)
    override fun sensorEvents(): Flow<SensorEvent> = callbackFlow {

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

                            val event = SensorEvent(
                                x = it.numberOfSteps.floatValue,
                                y = 0f,
                                z = 0f,
                                timestamp = Clock.System.now().toEpochMilliseconds()
                            )

                            trySend(event)
                        }

                        error?.let {
                            println("Pedometer error: $it")
                        }
                    }
                }
            }
        }

        awaitClose {
            motionManager.stopAccelerometerUpdates()
        }
    }

}