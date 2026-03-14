//
// Created by Gian on 3/13/26.
//

import Foundation
import SwiftUI
import MapKit
import CoreLocation
import Shared

struct HapticTrail: View {

    @StateObject private var observable = TrailObservable()

    @State private var cameraPosition: MapCameraPosition =
        .region(
            MKCoordinateRegion(
                center: CLLocationCoordinate2D(
                    latitude: -12.0464,
                    longitude: -77.0428
                ),
                span: MKCoordinateSpan(
                    latitudeDelta: 0.01,
                    longitudeDelta: 0.01
                )
            )
        )


    var body: some View {
        ZStack {
            Map(position: $cameraPosition) {
                MapPolyline(
                    coordinates: observable.path
                ).stroke(.blue, lineWidth: 6)
            }

            if observable.currentCommand is HapticCommand.Cadence {

                VStack {
                    Spacer()

                    HStack {
                        Button("Walking") {}
                        Spacer()
                    }
                    .padding()
                }
            }

            if observable.currentCommand is HapticCommand.Stop {

                VStack {
                    Spacer()

                    HStack {
                        Spacer()
                        Button("Stopped") {}
                    }
                    .padding()
                }
            }

        }.onChange(of: observable.path.last?.latitude) { _ in

            guard let last = observable.path.last else { return }

            cameraPosition = .region(
                MKCoordinateRegion(
                    center: last,
                    span: MKCoordinateSpan(
                        latitudeDelta: 0.01,
                        longitudeDelta: 0.01
                    )
                )
            )
        }

    }

}

class LocationManager: NSObject, ObservableObject, CLLocationManagerDelegate {

    private let manager = CLLocationManager()

    @Published var location: CLLocation?

    override init() {
        super.init()
        manager.delegate = self
        manager.desiredAccuracy = kCLLocationAccuracyBest
        manager.requestWhenInUseAuthorization()
        manager.startUpdatingLocation()
    }

    func locationManager(_ manager: CLLocationManager,
                         didUpdateLocations locations: [CLLocation]) {
        location = locations.last
    }
}