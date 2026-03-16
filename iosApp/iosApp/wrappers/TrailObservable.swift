//
// Created by Gian on 3/13/26.
//

import Foundation
import SwiftUI
import Shared
import Combine
import MapKit

class TrailObservable: ObservableObject {
    private let viewModel: TrailViewModel
    private let vibrationRepository = IOSVibrationEffectRepositoryImpl()
    @Published var lastLocation: CLLocationCoordinate2D? = nil
    @Published var path: [CLLocationCoordinate2D] = []
    @Published var currentCommand: HapticCommand?

    @Published var hasPermission: Bool = false
    private var locationManager = LocationManager()
    private var cancellables = Set<AnyCancellable>()

    init() {


        let locationRepository = IOSLocationProviderRepositoryImpl(nativeProvider: locationManager)
        let sensorRepository =
            IOSSensorRepositoryImpl(sensorType: SensorTypes().TYPE_STEP_DETECTOR)

        viewModel = TrailViewModel(
            locationProviderRepository: locationRepository,
            sensorRepository: sensorRepository,
            dispatcher: DispatcherProvider.shared.default_
        )

        locationManager.$hasPermission
        .receive(on: RunLoop.main)
        .sink { [weak self] granted in
            self?.hasPermission = granted
            if granted {
                self?.viewModel.startTracking()
            }
        }
        .store(in: &cancellables)
        locationManager.request()


        FlowUtils().collectStateFlow(flow: viewModel.trail) { trail in
            guard let t = trail as? TrailSession else {
                return
            }
            DispatchQueue.main.async {
                let newPath = t.locationEvents.map {
                    CLLocationCoordinate2D(latitude: $0.latitude, longitude: $0.longitude)
                }
                self.path = newPath
                if let lastEvent = t.locationEvents.last {
                    print("🚀 Última ubicación: Lat \(lastEvent.latitude), Lon \(lastEvent.longitude)")
                    self.lastLocation = CLLocationCoordinate2D(
                        latitude: lastEvent.latitude,
                        longitude: lastEvent.longitude
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