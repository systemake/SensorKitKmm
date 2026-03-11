import SwiftUI
import Shared

struct ContentView: View {
    @State private var showContent = false
    var body: some View {

        let sensorRepository = IOSSensorRepository()
        let hapticRepository = IOSHapticRepository()
        let viewModel = SensorViewModel(repository: sensorRepository, hapticRepository: hapticRepository)


        VStack {
            Button("Click me!") {
                withAnimation {
                    showContent = !showContent
                }
            }

            if showContent {
                VStack(spacing: 16) {
                    Image(systemName: "swift")
                        .font(.system(size: 200))
                        .foregroundColor(.accentColor)
                    Text("SwiftUI: \(Greeting().greet())")
                }
                .transition(.move(edge: .top).combined(with: .opacity))
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .padding()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
