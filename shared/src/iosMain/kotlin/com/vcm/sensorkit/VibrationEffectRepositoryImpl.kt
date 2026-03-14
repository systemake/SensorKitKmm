package com.vcm.sensorkit

import com.vcm.sensorkit.domain.models.HapticCommand
import com.vcm.sensorkit.domain.repository.VibrationEffectRepository
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType

class VibrationEffectRepositoryImpl : VibrationEffectRepository {

    override fun vibrate(command: HapticCommand) {

        when (command) {

            is HapticCommand.Cardinal -> {

                val generator =
                    UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy)

                generator.prepare()
                generator.impactOccurred()
            }

            is HapticCommand.Cadence -> {

                val generator =
                    UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)

                generator.prepare()
                generator.impactOccurred()
            }

            is HapticCommand.Stop -> {

                val generator = UINotificationFeedbackGenerator()

                generator.prepare()
                generator.notificationOccurred(
                    UINotificationFeedbackType.UINotificationFeedbackTypeWarning
                )
            }
        }
    }
}