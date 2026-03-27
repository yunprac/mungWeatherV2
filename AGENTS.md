# Repository Guidelines

## Project Structure & Module Organization
This repository is a single Android app module, `:app`. Kotlin source lives in `app/src/main/java/com/example/myapp`, grouped by feature (`login`, `signup`, `choice`, `main`), shared data access (`data`), DI setup (`di`), and Compose theme files (`ui/theme`). Resources are in `app/src/main/res`. Local unit tests go in `app/src/test`, and instrumented or UI tests go in `app/src/androidTest`.

## Build, Test, and Development Commands
Use the Gradle wrapper from the repository root.

- `.\gradlew.bat assembleDebug` builds a debug APK.
- `.\gradlew.bat testDebugUnitTest` runs JVM unit tests in `app/src/test`.
- `.\gradlew.bat connectedDebugAndroidTest` runs device or emulator tests from `app/src/androidTest`.
- `.\gradlew.bat lint` runs Android lint checks for the module.

Keep generated outputs under `app/build/` out of commits.

## Coding Style & Naming Conventions
Follow existing Kotlin and Compose patterns: 4-space indentation, one top-level declaration per file when practical, and concise composable functions. Use `PascalCase` for classes, screens, and view models (`LoginViewModel`), `camelCase` for methods and state fields, and lowercase package names. Name screen files as `FeatureScreen.kt`, UI state as `FeatureUiState.kt`, and view models as `FeatureViewModel.kt`.

This project does not currently include `ktlint` or `detekt`, so match the surrounding style and rely on Android Studio formatting before opening a PR.

## Testing Guidelines
Write fast business-logic tests in `app/src/test` with JUnit 4. Put Compose, navigation, and Android integration coverage in `app/src/androidTest` using AndroidX test and Espresso/Compose test APIs already declared in Gradle. Name tests after the behavior they verify, for example `login_withInvalidPassword_showsError`.

## Commit & Pull Request Guidelines
Git history is not available in this workspace snapshot, so no repository-specific commit pattern could be verified. Use short, imperative commit messages such as `Add signup validation`. Keep PRs focused and include:

- a clear summary of behavior changes
- linked issue or task reference when applicable
- screenshots or screen recordings for UI changes
- test notes covering unit, device, or manual verification

## Security & Configuration Tips
Do not commit secrets or machine-specific files. `local.properties` should stay local, and changes to `app/google-services.json` should be reviewed carefully because they affect Firebase configuration.
