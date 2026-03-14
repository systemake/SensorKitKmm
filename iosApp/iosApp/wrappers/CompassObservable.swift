//
//  CompassObservable.swift
//  iosApp
//
//  Created by Gian on 3/13/26.
//

import Foundation
import SwiftUI
import Shared

class CompassObservable: ObservableObject {

    private let viewModel: CompassViewModel

    @Published var sensorX: Float = 0
    @Published var sensorY: Float = 0
    @Published var sensorZ: Float = 0

    @Published var roll: Float = 0
    @Published var pitch: Float = 0
    @Published var yaw: Float = 0

    @Published var heading: Float = 0

    init() {

        let sensorRepository =
            IOSSensorRepositoryImpl(sensorType: SensorTypes().TYPE_ROTATION_VECTOR)

        let vibrationRepository = IOSVibrationEffectRepositoryImpl()

        viewModel = CompassViewModel(sensorRepository: sensorRepository)

        viewModel.startSensors()

        FlowUtils().collectStateFlow(flow: viewModel.sensorState) { state in
            let sensor = state as? SensorEvent
            DispatchQueue.main.async {
                self.sensorX = sensor?.x ?? 0
                self.sensorY = sensor?.y ?? 0
                self.sensorZ = sensor?.z ?? 0
            }
        }

        FlowUtils().collectStateFlow(flow: viewModel.motionEvent) { event in
            let motion = event as? MotionEvent
            DispatchQueue.main.async {
                self.roll = motion?.roll ?? 0
                self.pitch = motion?.pitch ?? 0
                self.yaw = motion?.yaw ?? 0
                self.heading = motion?.heading ?? 0
            }
        }

        FlowUtils().collectSharedFlow(flow: viewModel.hapticCommand ){ command in
            let cmd = command as? HapticCommand
            vibrationRepository.vibrate(command: cmd!)
        }
    }

    func stop() {
        viewModel.stop()
    }
}
