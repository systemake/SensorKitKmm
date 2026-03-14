package com.vcm.sensorkit

import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.vcm.sensorkit.domain.models.SensorEvent
import com.vcm.sensorkit.domain.repository.SensorRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SensorRepositoryImpl(
    private val sensorManager: SensorManager,
    private val sensorType: Int
) : SensorRepository {

    override fun sensorEvents(): Flow<SensorEvent> = callbackFlow {

        val sensor = sensorManager.getDefaultSensor(sensorType)

        val listener = object : SensorEventListener {

            override fun onSensorChanged(event: android.hardware.SensorEvent) {

                val sensorEvent = when (sensorType) {

                    Sensor.TYPE_STEP_DETECTOR -> {

                        SensorEvent(
                            x = event.values[0],
                            y = 0f,
                            z = 0f,
                            timestamp = event.timestamp / 1_000_000
                        )
                    }

                    Sensor.TYPE_ROTATION_VECTOR -> {

                        SensorEvent(
                            x = event.values[0],
                            y = event.values[1],
                            z = event.values[2],
                            timestamp = event.timestamp / 1_000_000
                        )
                    }

                    else -> {

                        SensorEvent(
                            x = event.values[0],
                            y = event.values[1],
                            z = event.values[2],
                            timestamp = event.timestamp / 1_000_000
                        )
                    }

                }
                trySend(sensorEvent)

            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

}