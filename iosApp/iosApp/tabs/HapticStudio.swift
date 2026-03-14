//
// Created by Gian on 3/13/26.
//

import Foundation
import SwiftUI
import Shared

struct HapticStudio : View {
    @StateObject private var observable = HapticStudioObservable()

    @State private var showAlert = false
    @State private var alertMessage = ""

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
                     showAlert = true
                }
                .frame(maxWidth: .infinity)
                .buttonStyle(.bordered)

                Button("Import JSON") {
                    let sampleJson = """
                                     [{"id":"p1","name":"Imported 1","intensity":0.5,"sharpness":0.3,"duration":200,"attack":0.1,"decay":0.1,"type":"TRANSIENT"}]
                                     """
                    observable.importJson(sampleJson)
                }
                .frame(maxWidth: .infinity)
                .buttonStyle(.bordered)
            }
            .padding()
        }
        .alert(isPresented: $showAlert) {
            Alert(title: Text("Info"), message: Text(alertMessage), dismissButton: .default(Text("OK")))
        }
    }
}