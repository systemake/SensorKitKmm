//
// Created by Gian on 3/13/26.
//

import Foundation
import SwiftUI

struct MotionCompass: View {
    @StateObject private var observable = CompassObservable()

    var body: some View {


            VStack(spacing: 20) {

                Text("Accelerometer")
                    .font(.headline)
                    .foregroundColor(.red)

                Text("X: \(observable.sensorX)")
                Text("Y: \(observable.sensorY)")
                Text("Z: \(observable.sensorZ)")

                Spacer().frame(height: 20)

                Text("Orientation")
                    .font(.headline)
                    .foregroundColor(.red)

                Text("Roll: \(observable.roll)")
                Text("Pitch: \(observable.pitch)")
                Text("Yaw: \(observable.yaw)")

                Spacer().frame(height: 20)

                Text("Vibration")
                    .font(.headline)
                    .foregroundColor(.red)

                Text("vibrate angle: \(observable.heading)")
            }
            .padding()
            .onDisappear {
                observable.stop()
            }
        }
}
