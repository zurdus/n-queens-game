# N Queens

An Android puzzle game based on the N-Queens problem. Players choose a board size and place queens so that no two queens share a row, column, or diagonal.

## Architecture

- Use Jetpack Compose for UI and a screen-level `ViewModel` as the state holder.
- Expose durable renderable state as an immutable model through `StateFlow`.
- Keep one immutable domain session as the source of game state and derive render-only UI state from it.
- Expose a `SharedFlow` only when a screen has a genuine transient effect. Never add an empty event stream solely to satisfy a pattern.
- Keep screen content composables stateless: state flows down and named user callbacks flow up.
- Handle navigation-only clicks through UI callbacks. Involve the ViewModel only when navigation depends on business logic or durable state.
- Use Material 3 Adaptive window size classes for screen-level layout decisions.
- Add use cases only for user-recognizable domain operations, such as starting or resetting a game. Do not wrap one-line validation or UI state changes in use cases.
- Every use case has one public behavior method: `operator fun invoke(...)`.
- Prefer one app module organized feature-first until module boundaries solve a real build or ownership problem.
- Add abstractions when they protect a domain rule, enable testing, or remove meaningful duplication—not in anticipation of hypothetical needs.
- Keep the root `NavHost` in the app-level navigation package and each feature graph and its Kotlin-serialization destinations in that feature's `.navigation` package.
- Feature graphs receive the `NavController` and translate presentation callbacks into internal navigation. Cross-feature navigation is supplied to feature graphs as a root-owned callback.
- Keep presentation independent of Navigation Compose: screens expose named callbacks and never receive a `NavController`.
- Let each destination call one exposed screen composable. Keep ViewModel coordination, the private scaffold overload, and stateless screen content together in the screen file.
- Let each feature own its Koin module; the app-level DI package only aggregates feature modules.
- Create layer subpackages only when they contain real code. Do not add empty `data`, `model`, `usecase`, or `component` packages.
- Keep models with the layer whose meaning they represent: business models in domain, storage/transport models in data, and render-only models in presentation.

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
- `com.zurdus.nqueens.ui.theme`: branded Material 3 light/dark theme
- `com.zurdus.nqueens.ui.preview`: reusable theme, font-scale, and display-size previews
