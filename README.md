# N Queens

An Android puzzle game based on the N-Queens problem. Players choose a board size and place queens so that no two queens share a row, column, or diagonal.

## Features

- Select any supported board size from 4 × 4 through 12 × 12.
- Place and remove queens on a responsive chessboard.
- See conflicts highlighted immediately and track the number of queens left.
- Undo the latest move or restart the current puzzle.
- Celebrate a valid solution, replay the same size, or return to board selection.
- Use adaptive layouts, light and dark themes, large font scales, and reduced system motion.

## Prerequisites

- Android Studio with Android SDK 37 installed
- JDK 17
- An Android device or emulator running API 24 or newer

The project includes the Gradle wrapper, so a separate Gradle installation is not required.

## Build and run

Open the repository root in Android Studio, let Gradle sync, select the `app` run configuration and a device, then click **Run**.

From PowerShell on Windows:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`. On macOS or Linux, use `./gradlew` in place of `.\gradlew.bat`.

## Test and lint

Run the JVM unit tests:

```powershell
.\gradlew.bat testDebugUnitTest
```

Run the Compose UI tests on a connected device or running emulator:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

Run Android lint:

```powershell
.\gradlew.bat lintDebug
```

The test strategy is layered: pure JVM tests cover board rules, immutable game sessions, user operations, ViewModels, and Koin wiring; connected Compose tests cover board interaction, adaptive layouts, navigation, reset and undo behavior, conflict feedback, counters, and the victory flow.

## Architecture

- **Feature-first single module:** board selection and gameplay own their presentation, navigation, domain, and DI code. A single module keeps the assessment lean while package boundaries preserve ownership and dependency direction.
- **Unidirectional state flow:** screen-level ViewModels expose immutable `StateFlow` UI state. Stateless content composables receive state and named callbacks.
- **Domain-owned game changes:** an immutable `GameSession` is the source of truth. User-recognizable operations such as changing a queen placement, undoing, and restarting are implemented as use cases with `operator fun invoke(...)`.
- **Typed navigation:** Kotlin-serialization routes live inside feature navigation packages. The root `NavHost` creates the controller and installs feature graphs; each graph exposes a typed entry route, owns its destinations, and handles transitions initiated within that feature. Presentation code never receives a `NavController`.
- **Dependency injection:** Koin modules are feature-owned and aggregated at the application level.
- **Adaptive Material 3 UI:** Compose Material 3 and its Adaptive window size classes choose compact, medium, and expanded layouts without manual width breakpoints.
- **State-driven motion:** named transitions in `ui.motion` animate queens and celebration content directly from rendered state. Animation flags and transient navigation events are not stored in ViewModels.
- **Pragmatic abstractions:** shared components and rules are extracted only when they have multiple consumers or protect meaningful behavior.

## Package structure

- `com.zurdus.nqueens`: application and activity entry points
- `com.zurdus.nqueens.di`: aggregation of feature-owned Koin modules
- `com.zurdus.nqueens.navigation`: root navigation host
- `com.zurdus.nqueens.domain`: rules shared by multiple features
- `com.zurdus.nqueens.feature.boardsize.presentation`: board-size screen, state, and ViewModel
- `com.zurdus.nqueens.feature.boardsize.navigation`: typed destinations and nested feature graph
- `com.zurdus.nqueens.feature.boardsize.di`: board-size Koin module
- `com.zurdus.nqueens.feature.game.presentation`: game screen state, ViewModel, and Compose UI
- `com.zurdus.nqueens.feature.game.domain`: N-Queens game models and user-recognizable operations
- `com.zurdus.nqueens.feature.game.navigation`: typed game destination and nested feature graph
- `com.zurdus.nqueens.feature.game.di`: game Koin module
- `com.zurdus.nqueens.ui.component`: shared Compose components with multiple feature consumers
- `com.zurdus.nqueens.ui.motion`: reusable named Compose transitions and entrance behavior
- `com.zurdus.nqueens.ui.theme`: branded Material 3 light/dark theme
- `com.zurdus.nqueens.ui.preview`: reusable theme, font-scale, and display-size previews

## AI assistance

OpenAI Codex assisted throughout design exploration, project scaffolding, implementation, refactoring, testing, and review. All decisions and code were reviewed and are owned by the author.
