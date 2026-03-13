//
// Created by Gian on 3/13/26.
//

import Foundation
import SwiftUI
import MapKit
import CoreLocation

struct HapticTrail: View {

    @StateObject private var locationManager = LocationManager()

    @State private var cameraPosition: MapCameraPosition =
        .region(MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: -12.0464, longitude: -77.0428),
            span: MKCoordinateSpan(latitudeDelta: 0.01, longitudeDelta: 0.01)
        ))

    var body: some View {

        Map(position: $cameraPosition)
            .onChange(of: locationManager.location) { location in

                guard let coordinate = location?.coordinate else { return }

                cameraPosition = .region(
                    MKCoordinateRegion(
                        center: coordinate,
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