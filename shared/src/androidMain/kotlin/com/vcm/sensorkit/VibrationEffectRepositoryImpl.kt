package com.vcm.sensorkit

import android.annotation.SuppressLint
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import com.vcm.sensorkit.repository.VibrationEffectRepository

class VibrationEffectRepositoryImpl(
    private val context: Context
) : VibrationEffectRepository {

    @SuppressLint("MissingPermission")
    override fun vibrate(){

        val vibrator =
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        vibrator.vibrate(
            VibrationEffect.createOneShot(
                200,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )

    }
}