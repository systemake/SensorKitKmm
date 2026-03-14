//
// Created by Gian on 3/13/26.
//

import Foundation
import SwiftUI

struct ContainerView: View {

    var body: some View {

        TabView {

            MotionCompass()
                .tabItem {
                    Image(systemName: "location.north.line")
                    Text("MotionCompass")
                }

            HapticTrail()
                .tabItem {
                    Image(systemName: "map")
                    Text("HapticTrail")
                }

            HapticStudio()
                .tabItem {
                    Image(systemName: "waveform.path")
                    Text("HapticStudio")
                }
        }
    }
}