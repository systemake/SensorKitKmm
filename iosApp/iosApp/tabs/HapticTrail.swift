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
    @State private var firstLocationCentered = false

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
        Group {
            if observable.hasPermission {
                ZStack {
                    Map(position: $cameraPosition) {
                        UserAnnotation()
                        MapPolyline(
                            coordinates: observable.path
                        ).stroke(.blue, lineWidth: 6)

                    }
                    VStack {
                        HStack {
                            Spacer()
                            MapUserLocationButton()
                                .padding()
                        }
                        Spacer()
                    }

                    renderCommandButtons()

                }
                .onChange(of: observable.lastLocation?.latitude) { _, _ in
                    print("Location changed")

                    guard let last = observable.lastLocation else { return }

                    withAnimation {
                        cameraPosition = .region(
                            MKCoordinateRegion(
                                center: last,
                                span: MKCoordinateSpan(latitudeDelta: 0.005, longitudeDelta: 0.005)
                            )
                        )
                    }
                    firstLocationCentered = true

                }


            } else {
                ContentUnavailableView("Waiting for location permission...", systemImage: "location.slash")
            }

        }

    }

    @ViewBuilder
    private func renderCommandButtons() -> some View {
        VStack {
            Spacer()
            HStack {
                if observable.currentCommand is HapticCommand.Cadence {
                    Button("Walking") {
                    }
                    .buttonStyle(.borderedProminent)
                }
                Spacer()
                if observable.currentCommand is HapticCommand.Stop {
                    Button("Stopped") {
                    }
                    .buttonStyle(.borderedProminent).tint(.red)
                }
            }
            .padding()
        }
    }
}


class LocationManager: NSObject, ObservableObject, CLLocationManagerDelegate {

    private let manager = CLLocationManager()

    @Published var location: CLLocation?
    @Published var authorizationStatus: CLAuthorizationStatus

    override init() {
        self.authorizationStatus = manager.authorizationStatus
        super.init()
        manager.delegate = self
        manager.desiredAccuracy = kCLLocationAccuracyBest
        manager.requestWhenInUseAuthorization()
        manager.startUpdatingLocation()
    }

    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        DispatchQueue.main.async {
            self.authorizationStatus = manager.authorizationStatus
        }
    }

    func locationManager(_ manager: CLLocationManager,
                         didUpdateLocations locations: [CLLocation]) {
        location = locations.last
    }
    func requestPermission() {
        manager.requestWhenInUseAuthorization()
    }
}