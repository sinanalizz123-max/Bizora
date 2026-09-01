# AGENTS.md

Guidance for AI coding agents and maintainers working in this repository.

## Project

Offline-first Android **Business Manager** (`com.bizmanager`): selling/POS,
products, inventory, customers, expenses, and (planned) reports, receipts,
Bluetooth ESC/POS printing, QR label codes, and backup/export. Kotlin +
Jetpack Compose (Material 3), MVVM with Room + DataStore + StateFlow.

## Toolchain (do not change casually)

- Gradle wrapper `8.10.2`, AGP `8.7.3`, Kotlin `2.0.21`, KSP `2.0.21-1.0.28`
- `compileSdk`/`targetSdk` 35, `minSdk` 26, Java 17 target
- Package/applicationId: `com.bizmanager`
- Remote: `https://github.com/sinanalizz123-max/Bizora.git`

## Build and verify

Run everything from the repository root:

```bash
./gradlew assembleDebug testDebugUnitTest
```

- Local JVM for unit tests: OpenJDK 21. CI uses Java 17.
- Robolectric is NOT supported on this machine (Linux aarch64) — do not add
  Robolectric/Gradle Managed Device tests here.

## Release & signing

- Distribution is GitHub Releases only — no Google Play.
- `.github/workflows/release.yml` runs on tag pushes `v*`: it decodes the
  keystore from `BIZMANAGER_KEYSTORE_BASE64`, builds `assembleRelease` (signed
  via env vars), verifies the signature, and attaches the APK to the release.
- The release signing config in `app/build.gradle.kts` activates only when
  `BIZMANAGER_KEYSTORE_PATH/PASSWORD`, `BIZMANAGER_KEY_ALIAS`,
  `BIZMANAGER_KEY_PASSWORD` are present (env or `~/.gradle/gradle.properties`).
- NEVER commit keystores (`*.jks`, `*.keystore`, `keystore.properties`) — they
  are gitignored; never print or log any signing secret.
- When preparing a release: bump `versionCode`/`versionName`, update
  `CHANGELOG.md`, commit, tag `vX.Y.Z`, push the tag; confirm the release
  workflow run is green.

## Architecture notes

- `AppContainer` (hosted on `BusinessManagerApp, the `Application`) is the
  single source of truth wiring repositories to Room DAOs + DataStore settings.
- `SettingsManager` stores module toggles and onboarding state in DataStore;
  screens read module enablement reactively (StateFlow).
- Business logic lives in repositories; ViewModels bridge to Compose UI. Do NOT
  put Room/database operations directly inside Compose composables.
- Sale items snapshot product names so historical sales survive edits/deletes.

## Conventions

- No code comments unless they explain *why*.
- Keep commits small and scoped; only commit/push when the user asks.
- Before finishing a task: run `./gradlew assembleDebug testDebugUnitTest` and,
  if a change was pushed, confirm the CI run is green.
