package com.vcm.sensorkit

import com.vcm.sensorkit.repository.VibrationEffectRepository
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

class VibrationEffectRepositoryImpl : VibrationEffectRepository {

    override fun vibrate() {
        val generator =
            UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)
        generator.impactOccurred()
    }

}