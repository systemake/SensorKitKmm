//
// Created by Gian on 3/13/26.
//

import Foundation
import Shared
import SwiftUI

class HapticStudioObservable: ObservableObject {

    private let viewModel: HapticStudioViewModel
    @Published var patterns: [HapticPattern] = []
    @Published var selectedPattern: HapticPattern? = nil
    @Published var showLogAlert: Bool = false
    @Published var lastLogMessage: String = ""

    init() {

        let hapticPlayer = HapticPlayerRepositoryImpl()
        let storage = IosPatternStorage()
        viewModel = HapticStudioViewModel(hapticPlayer: hapticPlayer, storage: storage)

        FlowUtils().collectStateFlow(flow:viewModel.patterns) { [weak self] data in

            let list = data as? [HapticPattern]
            DispatchQueue.main.async {
                self?.patterns = list!
            }
        }

        FlowUtils().collectStateFlow(flow:viewModel.selectedPattern) { [weak self] data in
            let pattern = data as? HapticPattern
            self?.selectedPattern = pattern
        }

        FlowUtils().collectSharedFlow(flow: viewModel.log) { message in
            self.showLogAlert = true
            self.lastLogMessage = (message as? String) ?? ""
            print("Log: \(message)")
        }
    }

    func addTextPattern() {
        let newPattern = HapticPattern(
            id: "p\(patterns.count + 1)",
            name: "Text \(patterns.count + 1)",
            intensity: 0.8,
            sharpness: 0.5,
            duration: 200,
            attack: 0.1,
            decay: 0.1,
            type: HapticType.companion.TRANSIENT_TYPE
        )
        viewModel.addPattern(pattern: newPattern)
    }

    func selectPattern(_ pattern: HapticPattern) {
        viewModel.selectPattern(pattern: pattern)
    }

    func playSelectedPattern() {
        viewModel.playSelectedPattern()
    }

    func export()  {
        return viewModel.save()
    }

    func importJson() {
        viewModel.load()
    }
}