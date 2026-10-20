# The app

A clean-room, local-only implementation of a Strava-like fitness app.

**Status: scaffolding.** The data model, the API contract, and the recording service skeleton exist. The UI does not. The build compiles, the unit tests pass, and CI runs it on every push.

---

## Module layout

The Gradle root is the **repository root**; `app/` is the single application module (`dev.openstride`, minSdk 26, targetSdk 35).

```
repo root/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── gradle/libs.versions.toml
├── gradlew, gradlew.bat, gradle/wrapper/
└── app/
    ├── build.gradle.kts
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   ├── res/{values,drawable}/
        │   └── kotlin/dev/openstride/
        │       ├── OpenStrideApplication.kt
        │       ├── model/Models.kt              ← the data model
        │       ├── local/LocalDatabase.kt       ← Room entities, DAOs, converters
        │       ├── api/LocalApiContract.kt      ← the contract the UI will talk to
        │       └── recording/
        │           ├── RecordingService.kt      ← foreground recording service
        │           └── RecoverActivityReceiver.kt ← reboot recovery
        └── test/kotlin/dev/openstride/local/    ← unit tests
```

There is one module. Split into `:core`, `:feature-recording`, `:feature-map`, `:feature-insights` only when there are two or more consumers — not before.

---

## Roadmap

Ordered by dependency, not by excitement.

### 1. Location pipeline — *not started*

`LocationRecorder` using `FusedLocationProviderClient` (or the platform `LocationManager` if you want zero Play Services), writing samples into `UnsyncedActivityDao` as they arrive.

Requirements:
- Sample at a rate proportional to speed (the original does this)
- Drop samples with poor accuracy rather than smoothing them
- Detect the "stopped moving" case and auto-pause after a configurable window
- Never lose a sample: the buffer is written to the database, not held in memory

### 2. Activity finalisation — *not started*

When a recording stops: compute distance, moving time, elevation gain, speeds, then persist an `Activity` and delete the `UnsyncedActivity` row.

### 3. Insights — *not started*

Fitness/freshness, relative effort, power curve, training load. All computed **from the local database** after each activity. See `spec/data-model.md` §3.

### 4. Map — *not started*, and the open question

See `spec/fatmap-native.md`. The recommendation is **MapLibre Native** with your own tiles, behind the `MapEngine` facade in `api/LocalApiContract.kt`, so the renderer stays swappable.

The personal heatmap is the interesting part: render it from your own activities, client-side, with privacy zones applied locally. The original serves this from its servers with a visibility scope in the query string; doing it here means it cannot be bypassed.

### 5. UI — *not started*

Jetpack Compose. A first pass at:
- Activity list (grouped by month)
- Activity detail with a map, a stream chart, and splits
- Record screen
- Gear, goals, privacy zones
- Settings

### 6. Import/export — *not started*

GPX/FIT import, and GPX/TCX/JSON export. **Export must apply privacy zones** — it is the single most likely way for a user to leak data about a place they marked private.

---

## Things that are easy to get wrong

**Privacy zones must be applied before rendering, not after.** The original app enforces them server-side for heatmaps, which a modified client can ask to be widened. Here they are local, so the temptation will be to apply them lazily. Apply them at the point where geometry becomes pixels or bytes.

**The recording service must survive process death.** Android will kill the process under memory pressure and will restart the service with a null intent. Recording state in the database, not in memory. The null-intent path must resume, never start fresh.

**`startForeground` must be called promptly.** A location foreground service that doesn't post its notification within a few seconds gets killed with `ForegroundServiceDidNotStartInTimeException`. Post the notification first, then do the work.

**Do not use `fallbackToDestructiveMigration()`.** The database holds the user's training history. Write real migrations.

**`unsynced_activities` is not a cache.** It is the only copy of an in-flight recording. Treat it accordingly.

---

## Build

Requires **JDK 17+** (21 recommended) and the **Android SDK** (compileSdk 35, build-tools 35). The Gradle wrapper is committed, so no local Gradle install is needed.

Point the build at your SDK with a `local.properties` in the **repository root**:

```properties
sdk.dir=/path/to/Android/Sdk
```

```bash
./gradlew :app:assembleDebug     # APK -> app/build/outputs/apk/debug/
./gradlew ktlintCheck
./gradlew test
```

**The build is verified.** It compiles, the unit tests pass, and CI runs the same three commands on every push.

---

## Dependencies

Kept deliberately small and permissively licensed. See `NOTICE.md` and `PROVENANCE.md` §5 before adding anything.

| Dependency | Why | Licence |
|---|---|---|
| `androidx.room` | the local database | Apache-2.0 |
| `androidx.lifecycle` | `LifecycleService` for the recording service | Apache-2.0 |
| `androidx.security:security-crypto` | encrypted storage for the local token | Apache-2.0 |
| `kotlinx-coroutines` | async everywhere | Apache-2.0 |
| `androidx.core:core-ktx` | AndroidX core | Apache-2.0 |

**Not included, and not to be added:** the original Fatmap SDK, Mapbox Maps SDK v11, the Facebook SDK, the Embrace SDK, the Branch SDK, the Garmin FIT SDK. All are proprietary.

---

## Scope

This app covers the **solo-athlete** half of the original: recording, maps, routes, segments, training load, insights, power curves, gear, goals, photos, and privacy controls.

It does **not** cover: a global feed, clubs, challenges, competitions, kudos, comments, global leaderboards, server-computed heatmaps, push notifications, or device cloud sync. These need other users or a server, and a single-user local app has neither. See `docs/10-local-only-feasibility.md` for the reasoning.
