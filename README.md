# RepSync

A simple, **offline-first** Android workout app. Log workouts, build templates, track progress—all on your device. No account required.

## Features

- **Guest-first** — Use the app immediately; optional local profile (name, avatar) only.
- **Workout templates** — Create and save workouts with exercises and sets; start from a template anytime.
- **Quick Workout** — Add exercises on the fly without a template.
- **Calendar** — Month view with completed workouts; tap a day to see details, copy workouts to other days, or save a day’s workout as a template.
- **Progress** — “Previous” weight/reps per exercise from your history.
- **Local only** — All data stays on your device (Room/SQLite). No cloud, no sign-in in v1.

## Requirements

- **Android** only (no iOS in initial scope).
- Min SDK 26; target SDK 35 (latest stable Android).
- **Architecture:** Kotlin, Jetpack Compose, single-activity, MVVM.

## Building the app

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (or Android SDK + Gradle).
- JDK 17+.

### Build commands

**Debug (development):**
```bash
./gradlew assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

**Release (distribution):**
```bash
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/app-release.apk`

For a signed release, configure signing in `app/build.gradle.kts` (see [Android docs](https://developer.android.com/studio/publish/app-signing)). Do not commit keystores or passwords.

### Run on a device

Install the debug APK:
```bash
./gradlew installDebug
```
Or open the project in Android Studio and run on an emulator or connected device.

## Distribution via Obtainium

[Obtainium](https://github.com/ImranR98/Obtainium) can install and update the app from GitHub Releases.

**For users:**

1. Install [Obtainium](https://github.com/ImranR98/Obtainium).
2. Add source: this repo’s URL (e.g. `https://github.com/<user>/RepSync`).
3. Obtainium will use the latest release and its APK (or AAB) asset.
4. Install or update from there.

**For maintainers (publishing a release):**

1. Build the release APK: `./gradlew assembleRelease`.
2. Create a new **GitHub Release** (tag, e.g. `v1.0.0`).
3. Attach `app/build/outputs/apk/release/app-release.apk` (or the signed APK) to the release.
4. Obtainium will pick up the new release and APK for users who added this repo.

## Repo structure

```
RepSync/
├── docs/
│   └── plan.md             # Full product and technical plan
├── prompts.md               # Phased prompts for implementation (e.g. Claude Code)
├── assets/
│   ├── repSyncLogo.png     # App logo
│   └── references/         # Design reference screens
├── app/                     # Android app module
├── build.gradle.kts
└── settings.gradle.kts
```

- **Spec:** See [docs/plan.md](docs/plan.md) for the single source of truth (flows, data model, UI, distribution).
- **Implementing from scratch:** Use [prompts.md](prompts.md) for step-by-step prompts keyed to the plan.

## License

See [LICENSE](LICENSE) in this repo, if present.
