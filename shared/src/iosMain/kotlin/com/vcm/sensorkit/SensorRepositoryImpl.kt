package com.vcm.sensorkit

import com.vcm.sensorkit.models.SensorEvent
import com.vcm.sensorkit.repository.SensorRepository
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.CoreMotion.CMMotionManager
import platform.Foundation.NSOperationQueue

class SensorRepositoryImpl : SensorRepository {

    private val motionManager = CMMotionManager()

    @OptIn(ExperimentalForeignApi::class)
    override fun sensorEvents(): Flow<SensorEvent> = callbackFlow {

        motionManager.startAccelerometerUpdatesToQueue(
            NSOperationQueue.mainQueue()
        ) { data, error ->
            val acceleration = data?.acceleration

            acceleration?.useContents {

                val event = SensorEvent(
                    x = x.toFloat(),
                    y = y.toFloat(),
                    z = z.toFloat()
                )

                trySend(event)

            }

           print("error  $error")
        }

        awaitClose {
            motionManager.stopAccelerometerUpdates()
        }
    }

}