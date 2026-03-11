package com.vcm.sensorkit

import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.vcm.sensorkit.models.SensorEvent
import com.vcm.sensorkit.repository.SensorRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SensorRepositoryImpl(
    private val sensorManager: SensorManager
) : SensorRepository {

    override fun sensorEvents(): Flow<SensorEvent> = callbackFlow {

        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        val listener = object : SensorEventListener {

            override fun onSensorChanged(event: android.hardware.SensorEvent) {
                trySend(
                    SensorEvent(
                        x = event.values[0],
                        y = event.values[1],
                        z = event.values[2]
                    )
                )
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

}