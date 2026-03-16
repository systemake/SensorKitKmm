# 🏃‍♂️ Multiplatform Sensor Tracking Challenge (KMP)

This project is a **Kotlin Multiplatform (KMP)** solution for a technical challenge. It implements a real-time physical activity tracking system with haptic feedback. The application is fully functional for **Android and iOS**, with a high focus on sensor precision, low-latency UI updates, and Clean Architecture principles.

## 🏗️ Project Architecture

The project follows **Clean Architecture** adapted for KMP to maximize code reuse while maintaining direct access to native hardware APIs.

### Module Structure:
* **`shared/commonMain`**: Contains the core business logic (**Domain**), data models, and **ViewModels**. It defines repository interfaces and reactive logic for cadence and session management.
* **`shared/androidMain` & `shared/iosMain`**: Contains platform-specific implementations of repositories (`AndroidSensorRepository`, `IosSensorRepository`, etc.) utilizing native system APIs.
* **`composeApp` & `iosApp`**: Native presentation layers built with **Jetpack Compose** and **SwiftUI**, respectively.

---

## 🛠️ Tech Stack & Hardware Integration

To ensure a fluid and accurate experience, the app integrates with low-level hardware APIs on both platforms:

| Feature | 🤖 Android | 🍎 iOS |
| :--- | :--- | :--- |
| **Motion Sensors** | `SensorManager` | `CMMotionManager` & `CMPedometer` |
| **Location Tracking** | `FusedLocationProviderClient` | `CLLocationManager` |
| **Haptic Engine** | `VibrationEffect` | `CHHapticEngine` |
| **UI Framework** | Jetpack Compose | SwiftUI |

---

## 🚀 Key Technical Decisions

### 1. Multiplatform Reactivity
Native sensor updates are transformed into Kotlin data streams using **`callbackFlow`**. To bridge the gap with iOS:
* **Wrappers and Extensions**: Custom wrappers were created to expose `StateFlow` and `SharedFlow` to Swift, allowing the native UI to observe state changes reactively.
* This ensures that the UI remains "dumb" and only reacts to states emitted by the Shared ViewModel.

### 2. Latency Optimization (iOS)
To address the intrinsic delay of the `CMPedometer` API on iOS, a hybrid logic was implemented in the `IosSensorRepository`. By combining accelerometer data with pedometer events, the app triggers immediate haptic feedback, ensuring the vibration aligns perfectly with the user's foot impact.

### 3. Quality Assurance (Testing)
Comprehensive Unit Tests were implemented in `commonTest` to ensure logic reliability across platforms:
* **TrailViewModel**: Validated session tracking logic and state transitions.
* **Mocking**: Created mock implementations for sensor and location providers to isolate logic tests from hardware.
* **Virtual Time Dispatching**: Utilized `StandardTestDispatcher` and `runTest` to verify time-sensitive behaviors, such as the 3-second inactivity "Stop" command.
* **Serialization**: Verified "Round-trip" serialization for haptic commands to ensure data integrity between Kotlin and the native layers.

---

## 🧪 Running Tests
To validate the shared logic on both platforms, run the following command in your terminal:
```bash
./gradlew allTests

For platform-specific unit tests:

./gradlew :shared:testDebugUnitTest       # Android
./gradlew :shared:iosSimulationPickerTest # iOS

📈 Future Improvements
Implementation of local persistence using Room or SQLDelight for session history.

Battery life optimization via dynamic GPS sampling rates.

Integration of Map frameworks (Google Maps / Apple Maps) to visualize routes in real-time.

Developed by: Gianfranco Gutierrez - Android Tech Lead / Senior Developer