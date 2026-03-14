package com.vcm.sensorkit

import android.annotation.SuppressLint
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import com.vcm.sensorkit.domain.models.HapticPattern
import com.vcm.sensorkit.domain.repository.HapticPlayerRepository

class HapticPlayerRepositoryImpl(private val context: Context) : HapticPlayerRepository {
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    @SuppressLint("MissingPermission")
    override fun play(pattern: HapticPattern) {
        val intensity = (pattern.intensity * 255).toInt().coerceIn(1, 255)
        val duration = pattern.duration.coerceAtLeast(1L)

        val effect = VibrationEffect.createOneShot(duration, intensity)
        vibrator.vibrate(effect)
    }
}