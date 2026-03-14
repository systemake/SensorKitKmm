//
// Created by Gian on 3/13/26.
//

import Foundation
import SwiftUI
import Shared

struct HapticStudio : View {
    @StateObject private var observable = HapticStudioObservable()


    var body: some View {
        VStack {

            List(observable.patterns, id: \.id) { pattern in
                HStack {
                    Text(pattern.name)
                    Spacer()
                    if observable.selectedPattern?.id == pattern.id {
                        Text("Selected")
                            .foregroundColor(.blue)
                    }
                }
                .contentShape(Rectangle())
                .onTapGesture {
                    observable.selectPattern(pattern)
                }
            }

            VStack(spacing: 12) {

                Button("Play") {
                    observable.playSelectedPattern()
                }
                .disabled(observable.selectedPattern == nil)
                .frame(maxWidth: .infinity)
                .buttonStyle(.borderedProminent)

                Button("Add Text") {
                    observable.addTextPattern()
                }
                .frame(maxWidth: .infinity)
                .buttonStyle(.bordered)

                Button("Export JSON") {
                     observable.export()
                }
                .frame(maxWidth: .infinity)
                .buttonStyle(.bordered)

                Button("Import JSON") {
                    observable.importJson()
                }
                .frame(maxWidth: .infinity)
                .buttonStyle(.bordered)
            }
            .padding()
        }


        .alert(isPresented: $observable.showLogAlert) {
            Alert(title: Text("Info"), message: Text(observable.lastLogMessage), dismissButton: .default(Text("OK")))
        }
    }
}