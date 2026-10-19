# Strava Android — Code Architecture

**Subject:** `com.strava.apk` base split · 456.10 · 7 DEX files

---

## 1. Size of the codebase

| Metric | Value |
|---|---:|
| DEX files | 7 (49,357,560 B) |
| **Class definitions** (`class_defs_size`) | **72,470** |
| **Distinct class names** (deduplicated across DEX) | **43,827** |
| Method definitions | 331,446 |
| Field definitions | 207,113 |
| Type definitions | 101,597 |
| String pool entries (all DEX) | 281,218 |

The two class counts measure different things and both are correct: DEX files may hold several definitions that resolve to the same name across split boundaries, and JADX/REA report the deduplicated name set. The DEX header values are the authoritative definition count.

| DEX | Bytes | Classes | Methods |
|---|---:|---:|---:|
| `classes.dex` | 10,413,912 | 14,322 | 65,499 |
| `classes2.dex` | 1,840 | 2 | 12 |
| `classes3.dex` | 8,105,328 | 10,314 | 65,429 |
| `classes4.dex` | 9,732,292 | 16,605 | 65,530 |
| `classes5.dex` | 10,311,216 | 17,227 | 65,436 |
| `classes6.dex` | 10,021,188 | 13,233 | 64,241 |
| `classes7.dex` | 771,784 | 767 | 5,299 |

`classes2.dex` holds two classes and twelve methods — a vestigial file kept for the multidex shim.

---

## 2. First-party vs. library

Of 43,827 distinct class names:

- **5,140** are under `com.strava.*` (Strava's own code)
- **30,043** sit in short (1–2 character) package roots — R8-renamed code
- The remainder are named third-party packages

**Important caveat:** a missing package name does **not** mean a library is absent. R8 relocates and renames aggressively; 45,669 of the 72,470 class definitions (63 %) were renamed into 1–2 letter packages. Library presence was therefore confirmed independently through `META-INF/*.version` coordinates, `assets/sentry-external-modules.txt` (471 entries) and symbol-level checks.

### Top-level package roots (distinct class names)

| Classes | Root | Owner |
|---:|---|---|
| 5,140 | `com.strava` | **Strava** |
| 2,548 | `com.google` | Google Play Services, Firebase, GMS, Maps |
| 928 | `com.mapbox` | Mapbox Maps / Common |
| 821 | `io.getstream.chat` | Stream Chat SDK |
| 555 | `com.facebook` | Facebook SDK + Stetho |
| 537 | `io.sentry` | Sentry |
| 476 | `androidx.compose` | Jetpack Compose |
| 382 | `zendesk.support` | Zendesk Support |
| 255 | `app.rive` | Rive animation runtime |
| 213 | `zendesk.core` | Zendesk Support |
| 165 | `org.joda` | Joda-Time |
| 156 | `androidx.health` | Health Connect |
| 130 | `com.fatmap` | **Fatmap SDK (Strava's own map engine)** |
| 130 | `okhttp3.internal` | OkHttp 5 |
| 127 | `androidx.appcompat` | AppCompat |
| 125 | `io.embrace` | Embrace observability |
| 81 | `androidx.glance` | Glance (home-screen widgets) |
| 72 | `androidx.datastore` | DataStore |
| 44 | `io.ktor` | Ktor |
| 40 | `io.branch` | Branch.io |

---

## 3. Strava's own feature areas

`com.strava.*` broken down by 3-segment package (5,140 classes total):

| Classes | Package | Area |
|---:|---|---|
| 511 | `com.strava.clubs` | Groups & clubs, group events, waivers, leaderboards |
| 387 | `com.strava.modularui` | **Server-driven UI framework** |
| 247 | `com.strava.subscriptionsui` | Subscription, checkout, trial, cancellation, cross-grading |
| 235 | `com.strava.settings` | Every settings screen |
| 144 | `com.strava.activitydetail` | Activity detail, kudos, photos, streams, power curves |
| 135 | `com.strava.activitysave` | Post-activity save/editor/upload flow |
| 114 | `com.strava.competitions` | Group competitions |
| 113 | `com.strava.modularcomponentsconverters` | Server-driven UI ↔ Compose converters |
| 110 | `com.strava.fitness` | Fitness, training load, progress analysis |
| 106 | `com.strava.authorization` | Login, OAuth, OTP, password, Facebook/Google |
| 105 | `com.strava.gear` | Bikes, shoes, retirement |
| 101 | `com.strava.challenges` | Challenges |
| 98 | `com.strava.photos` | Gallery, lightbox, picker |
| 97 | `com.strava.routing` | Route planning, Mapbox routing |
| 96 | `com.strava.segments` | Segments, leaderboards, efforts, local legends |
| 95 | `com.strava.recording` | Activity recording engine, beacon live tracking |
| 93 | `com.strava.recordingui` | Record screen, settings, audio cues, sensors |
| 85 | `com.strava.chats` | Club chat (Stream) |
| 76 | `com.strava.modularframework` | Modular UI data model |
| 73 | `com.strava.view` | Legacy View-based UI shell |
| 67 | `com.strava.sharing` | Share sheets, activity sharing |
| 62 | `com.strava.profile` | Athlete profile, connections, stats, reporting |
| 61 | `com.strava.comments` | Comments, reactions, UGC alerts |
| 59 | `com.strava.mapstab` | Map tab, route builder, POI, surface type |
| 58 | `com.strava.posts` | Feed posts, composer, kudos |
| 56 | `com.strava.dynamicmapmre` | **MRE** — Fatmap/offline map bundles (Room DB) |
| 56 | `com.strava.spandex` | **Strava's internal Compose design system** |
| 55 | `com.strava.mappreferences` | Heatmap preferences & API |
| 54 | `com.strava.placesearch` | Place search, coordinate picker |
| 51 | `com.strava.communitysearch` | Athlete & club search |
| 51 | `com.strava.feed` | Home feed |
| 47 | `com.strava.athleteworkouts` | Workout library |
| 45 | `com.strava.goals` | Goals + home-screen widgets |
| 43 | `com.strava.geomodels` | Geo/route Thrift models |
| 40 | `com.strava.monthlystats` | Monthly statistics |
| 39 | `com.strava.bestefforts` | Best efforts |
| 36 | `com.strava.subscriptions` | Subscription domain model |
| 34 | `com.strava.traininglog` | Training log |
| 34 | `com.strava.widget` | Glance + AppWidget home-screen widgets |
| 30 | `com.strava.net` | Networking core |
| 29 | `com.strava.mediauploading` | Photo/video upload |
| 29 | `com.strava.trainingzones` | Training zones |
| 28 | `com.strava.flyover` | 3D "flyover" of a route |
| 27 | `com.strava.sharinginterface` | Copy link, export, QR codes |
| 25 | `com.strava.graphing` | Charts |

### The three systems worth calling out

**a) "Modular UI" — server-driven feature delivery.**
`com.strava.modularframework` defines a generic, data-driven UI model: `GenericLayoutEntry`, `GenericAction`, `GenericActionState`, `Destination`, `EntryPosition`, `CardStyle`, `BorderGradientStyle`, `ColorToken`, `BaseModuleFields`. `com.strava.modularui` renders it (387 classes, mostly Compose + ViewBinding), and `com.strava.modularcomponentsconverters` (113 classes) bridges the wire model to Compose. `com.strava.modularui.ModularExperiment` confirms it is experiment-gated. This is how Strava ships new feed modules, carousels and upsells without an app release.

**b) "Spandex" — the internal design system.**
56 classes of Compose components: `SpandexButton`, `SpandexButtonCircularView`, `SpandexAvatarView`, `SpandexBannerConfig`/`SpandexBannerType`, `SpandexBottomSheetDialogFragment`, `BottomSheetUpsellView`.

**c) "MRE" (`dynamicmapmre`) — offline 3D maps.**
A Room database (`OfflineMapDatabase`, `OfflineMapDao`, `OfflineMapBundleStorage`, `OfflineMapBundleOrder`, `OfflineMapJson`) storing downloadable map bundles for the Fatmap engine.

---

## 4. Key class anatomy (from JADX decompilation)

### `com.strava.StravaApplication` — startup sequence

The decompiled `onCreate()` shows an explicitly instrumented boot order, each step wrapped in a named timer:

1. `ApplicationInitTimeBytecodeEntrypoint.applicationInitStart()`
2. Sentry performance tracing starts (`io.sentry.android.core.performance.h`)
3. `super.onCreate()`; the Application registers itself in a static field
4. `app_start_feed_load` — reads the `launch_activity` preference; if it equals `"feed"`, the feed is force-loaded at startup (`Sm.f.f39557g = true`) and a feed request is composed
5. `app_start_remote_logger` — starts the remote logger and calls `aVar3.c("19.0.5", "mre.version")` — **the Fatmap/MRE engine version is reported here**
6. `app_start_preferenceMigration` — walks every stored preference and migrates legacy types (String→String, Boolean, Int, Long, and a set type) into the new DataStore-backed preference layer
7. `app_start_branch` — initialises **Branch.io** with the live key `<BRANCH_LIVE_KEY>`

### `com.strava.recording.StravaActivityService` — background recording

`foregroundServiceType="location"`. `onStartCommand()`:

- Always calls `startForeground(...)` with a persistent "recording" notification
- **Null intent** → process-restart recovery path: looks up an `UnsyncedActivity` by GUID, and if one exists and is not indoor, resumes it; otherwise logs *"No Abandoned activity found"* and calls `stopSelf()`
- `start_mode == "record"` → starts a new recording. Intent extras: `live_activity_url`, `live_activity_id`, `is_indoor_sub_type`, `workoutData` (a serializable `Workout`)
- `start_mode == "recover_activity"` → resumes a specific activity by `activityId` GUID

### `com.strava.net.token.TokenApi`

```java
RefreshTokenResponse refreshToken(String, String, String);
RefreshTokenResponse refreshTokenV2(String, String, String, String);
```

Two token-refresh generations are present; `refreshToken` is implemented as a native/abstract bridge with no decompiled body, `refreshTokenV2` is the current path.

### `com.strava.authorization.sharedidentity.StravaIdentityProvider`

An **exported** `ContentProvider` (`authority = com.strava.identity`) exposing Strava's login identity to other apps on the device — a shared-credential / SSO provider. It implements `query`, `insert`, `update`, `delete`, `getType` against a `UriMatcher`.

---

## 5. Third-party surfaces visible in first-party code

| Package | Purpose | Evidence |
|---|---|---|
| `com.strava.net.apierror.ApiErrors` | Typed API error mapping | class list |
| `com.strava.net.token.TokenApi` | OAuth token refresh | decompiled |
| `com.strava.net.superuser.NetworkLogDatabase` | **HTTP request log stored in a local DB** (superuser debug menu) | class list + `res/xml/file_provider_paths.xml` (`network_log` dir) |
| `com.strava.net.superuser.ServiceCanaryOverride` | Per-endpoint canary routing (staging overrides) | class list |
| `com.strava.mappreferences.data.HeatmapApi` / `HeatmapRepository` | Global/nightly/weekly/personal heatmap fetching | class list |
| `com.strava.dynamicmapmre.offline.*` | Offline map bundle download & storage | class list |
| `com.strava.photos.picker.MediaPickerActivity` | Photo picker (Android 13+ `READ_MEDIA_VISUAL_USER_SELECTED`) | manifest |
| `com.strava.yearinsport.*` | Year-in-Sport recap | manifest + class list |
| `com.strava.subscriptionsui.*` | Play Billing checkout (`ProxyBillingActivity` v1 + v2) | manifest |

---

## 6. Debug / "superuser" surface

Strava ships a developer-facing menu called **Super User**, reachable from `com.strava.view.superuser.SuperUserToolsActivity`. It contains:

| Screen | Capability |
|---|---|
| `com.strava.superuser.DebugToolsSettingsActivity` | debug toggles |
| `com.strava.superuser.AnalyticsCacheActivity` | inspect the analytics cache |
| `com.strava.superuser.ChangeLanguageActivity` | override the app language |
| `com.strava.superuser.subscription.ToggleSubscriptionFragmentActivity` | **toggle subscription state** |
| `com.strava.superuser.metering.ManageMeteringActivity` | manage usage metering |
| `com.strava.nettools.NetworkSettingsActivity` | network settings |
| `com.strava.nettools.NetworkLogActivity` / `NetworkLogDetailActivity` | full HTTP request/response log |
| `com.strava.nettools.canaries.ServiceCanaryListActivity` | canary service routing |
| `com.strava.nettools.language.OverrideLanguageHeaderActivity` | override the `Accept-Language` header |
| `com.strava.featureswitchtools.ManageFeatureSwitchActivity` | feature-switch management |
| `com.strava.featureswitchtools.experiments.OverrideExperimentCohortActivity` | **force an experiment cohort** |
| `com.strava.mapplayground.MapPlaygroundActivity` | map renderer playground |
| `com.strava.routing.utils.MapCacheClearingActivity` | clear the map cache |
| `com.strava.modularframework.tools.DebugToolsActivity` | modular-UI tooling |

These are **not exported** (except `MapPlaygroundActivity` and `FeatureSwitch` screens are `exported="true"` in the manifest, reachable via `strava://` deep links only). They are nevertheless compiled into the production build — a normal consequence of a single-flavour release pipeline, but worth noting: `ToggleSubscriptionFragmentActivity` and `OverrideExperimentCohortActivity` are powerful if a user can reach them through a deep link.
