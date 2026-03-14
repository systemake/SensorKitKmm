//
// Created by Gian on 3/13/26.
//

import Foundation
import SwiftUI
import Shared
import MapKit

class TrailObservable: ObservableObject {

    private let viewModel: TrailViewModel
    private let vibrationRepository = VibrationEffectRepositoryImpl()

    @Published var path: [CLLocationCoordinate2D] = []
    @Published var currentCommand: HapticCommand?

    init() {

        let locationRepository = LocationProviderRepositoryImpl()
        let sensorRepository =
            SensorRepositoryImpl(sensorType: SensorTypes().TYPE_STEP_DETECTOR)

        viewModel = TrailViewModel(
            locationProviderRepository: locationRepository,
            sensorRepository: sensorRepository
        )

        viewModel.startTracking()

        FlowUtils().collectStateFlow(flow: viewModel.trail) { trail in

            guard let t = trail as? TrailSession else { return }

            DispatchQueue.main.async {

                self.path = t.locationEvents.map {
                    CLLocationCoordinate2D(
                        latitude: $0.latitude,
                        longitude: $0.longitude
                    )
                }
            }
        }

        FlowUtils().collectSharedFlow(flow: viewModel.hapticCommand) { command in

            let cmd = command as? HapticCommand

            DispatchQueue.main.async {
                self.currentCommand = cmd
            }

            self.vibrationRepository.vibrate(command: cmd!)
        }
    }
}