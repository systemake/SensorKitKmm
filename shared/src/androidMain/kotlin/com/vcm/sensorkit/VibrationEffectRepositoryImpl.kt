package com.vcm.sensorkit

import android.annotation.SuppressLint
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import com.vcm.sensorkit.domain.models.HapticCommand
import com.vcm.sensorkit.domain.repository.VibrationEffectRepository

class VibrationEffectRepositoryImpl(
    private val context: Context
) : VibrationEffectRepository {

    @SuppressLint("MissingPermission")
    override fun vibrate(command: HapticCommand) {

        val vibrator =
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        when (command) {

            is HapticCommand.Cardinal -> {

                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        200,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            }

            is HapticCommand.Cadence -> {
                val amplitude = (command.intensity * 255).toInt()

                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        command.duration,
                        amplitude
                    )
                )
            }

            is HapticCommand.Stop -> {
                println("Stopped")
            }
        }
    }
}