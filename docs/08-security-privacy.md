# Strava Android — Security, Privacy & Data-Flow

**Subject:** `com.strava.apk` base split · 456.10

---

## 1. Transport security

`res/xml/network_security_config.xml` (decoded):

```xml
<network-security-config>
  <debug-overrides>
    <trust-anchors><certificates src="user"/></trust-anchors>
  </debug-overrides>
  <domain-config cleartextTrafficPermitted="true">
    <domain includeSubdomains="false">10.0.2.2</domain>
  </domain-config>
</network-security-config>
```

- **There is no `<base-config>`.** Android's default applies: system CAs only, **TLS required**, cleartext HTTP refused.
- The single cleartext exemption is **`10.0.2.2`** — the standard Android-emulator alias for the host machine. It exists so a debug build can talk to a local dev server. It is not a production host and `includeSubdomains="false"`.
- `debug-overrides` trusting user-installed CAs only takes effect in **debuggable** builds. This release is not debuggable.

**Verdict:** cleartext HTTP is effectively disabled for all real endpoints.

`android:usesCleartextTraffic` is **not** set on `<application>` (it defaults to false for targetSdk ≥ 28 anyway).

### Certificate pinning

**Pinning is configured — but only for two hosts.**

The entire APK contains exactly **one** SPKI pin value:

```
sha256/q61HEYJJK/e1i0DQnyP6R6tGOrRC5MbbsTcI9ev6cZ4=
```

A DEX code-scan (`const-string` / `const-string/jumbo` reference tracing over all 7 DEX files) found **exactly one** method in the whole application that references it, and that method's string table is:

```
'cdn-1.strava.com', 'sha256/q61HEYJJK/e1i0DQnyP6R6tGOrRC5MbbsTcI9ev6cZ4=', 'graphql.strava.com',
'okHttpClient', ...
```

That is an OkHttp `CertificatePinner.Builder` configuration applying the pin to **`cdn-1.strava.com`** and **`graphql.strava.com`**. Both are CDN/static-asset and GraphQL hosts, not the main API host.

The pinning code lives in the obfuscated class `LFd/d2$a;` (package `LFd`, class `d2$a`), inside a dependency-injection module that also builds the app's `retrofitClient`, `remoteLogger` and `mreOfflineMapManager`, and whose string table also reveals the app's local database names:

```
'strava-database'  'challenge-database'  'experiments-database'  'recording-database'
'privacyZonesDatabase'  'recentsDatabase'  'athlete-workouts.db'
'map-style-json-database'  'activity-tag-database'
'stream_chat_client.db'  'exoplayer_internal.db'  'videoCache'
'com.strava.dynamicmapmre.offline_maps'
'https://yis-static.strava.com'  'https://yis.strava.com'
'<MAPBOX_ACCOUNT_ID_1>'  '<MAPBOX_ACCOUNT_ID_2>'
```

**Scope.** `https://www.strava.com/api/v3/` — the main REST API — is **not** pinned. `api.mapbox.com`, Sentry, Embrace, Firebase, Stream, Zendesk and Branch are **not** pinned. All of them rely on the platform CA store.

**Verdict:** the two hosts that serve **static assets** and **GraphQL** are strictly pinned, which is the right place to pin (those are the hosts an on-path attacker would use to inject content). The API host is not.

---

## 2. Backup

`res/xml/backupscheme.xml`:

```xml
<full-backup-content>
  <exclude domain="database" path="strava"/>
  <exclude domain="sharedpref" path="com.strava.preference.excludeFromBackup.xml"/>
</full-backup-content>
```

`android:allowBackup="true"`, so Android's auto-backup (Google) is enabled, but the primary local database `strava` and one preference file are explicitly excluded. The `strava` database is the local activity store (Room). Other app databases and shared preferences **are** eligible for cloud backup.

`androidx.security:security-crypto` 1.1.0 is present, so Tink 1.5.0 is available for encrypted local storage — whether it is used for the excluded preference file was not established.

---

## 3. Attack surface: exported components

**130 components are exported.** They are the app's deep-link surface, and each is reachable by any other app on the device.

### 3.1 The `strava://` scheme

Every `IntentCatcherActivity` listens on the custom scheme `strava://`. Known hosts and paths (from the manifest):

| Host / path | Target |
|---|---|
| `strava://dashboard`, `strava://dashboardYou` | BottomNavigationActivity |
| `strava://settings` + `/profile`, `/privacy`, `/privacy/zones`, `/privacy/maps`, `/privacy/mentions`, `/privacy/messaging`, `/privacy/profile-page`, `/privacy/activities`, `/privacy/grouped-activities`, `/privacy/flyby`, `/privacy/local-legend`, `/privacy/metro-heatmap`, `/privacy/groups` | various settings screens |
| `strava://settings` + `/appearance`, `/data-permissions`, `/email-notifications`, `/connected-apps`, `/change-password`, `/feed-ordering`, `/notification-settings`, `/phone_number`, `/opt_in_to_otp`, `/sponsored-partners`, `/weather-visibility`, `/training_zones`, `/sponsored-partners/opt-out/*` | settings screens |
| `strava://athletes/*`, `strava://athlete`, `strava://pros/*` | AthletesIntentCatcherActivity |
| `strava://activities`, `strava://rides`, `strava://runs`, `strava://walks`, `strava://hikes` | ActivitiesIntentCatcherActivity |
| `strava://segments`, `strava://segmentslists/` | segment screens |
| `strava://clubs`, `strava://group_events` | ClubsIntentCatcherActivity |
| `strava://posts/*` | PostsIntentCatcherActivity |
| `strava://challenges/*`, `strava://challenges/activity-list`, `strava://challenges/challenge-celebration` | challenge screens |
| `strava://chats` | ChatIntentCatcherActivity |
| `strava://comments` | CommentsIntentCatcherActivity |
| `strava://competitions` | CompetitionsIntentCatcherActivity |
| `strava://routing/*`, `strava://routes`, `strava://maps/*` | RoutesIntentCatcherActivity |
| `strava://record`, `strava://record/settings/sensors` | recording screens |
| `strava://record_shortcut` | RecordShortcutIntentCatcher |
| `strava://beacon/settings`, `strava://unsynced-activities` | live tracking |
| `strava://groups` | GroupsIntentCatcherActivity |
| `strava://training/log`, `strava://training/relative-effort` | training log / insights |
| `strava://monthly-stats` | MonthlyStatsActivity |
| `strava://premium`, `strava://subscription`, `strava://student_plan/dialog` | subscription screens |
| `strava://runna/interstitial` | Runna promotion |
| `strava://runna-training-plans` | training plans |
| `strava://notifications`, `strava://external` | notifications |
| `strava://ugc-alert`, `strava://super-follows`, `strava://onboarding`, `strava://second_mile/complete_profile`, `strava://settings/connected-devices-updated`, `strava://save-screen`, `strava://age-assurance/birthday-input`, `strava://widget/config`, `strava://widget/config-detailed`, `strava://activity-search`, `strava://clear_map_cache`, `strava://flyover`, `strava://map-playground/*`, `strava://standalone-route`, `strava://support`, `strava://feedback`, `strava://google-fit-connect`, `strava://login`, `strava://signup`, `strava://facebook/deprecation`, `strava://email_verification`, `strava://modular-sheet` | assorted |
| `strava://record_shortcut?activityType=WALK\|RUN\|RIDE` | `res/xml/shortcuts.xml` |

**These are not permission-guarded.** Any app can invoke them. The risk is mostly navigational (a hostile app can drive the user into a particular screen); the paths worth attention are the ones that mutate state or expose data.

### 3.2 High-interest exported components

| Component | Why it matters |
|---|---|
| `com.strava.authorization.sharedidentity.StravaIdentityProvider` (provider, `authority=com.strava.identity`) | **Exported content provider that serves Strava's login identity to other apps.** A shared-credential/SSO provider. It implements `query`/`insert`/`update`/`delete`/`getType` against a `UriMatcher`. Any app on the device can query it. This is the single most sensitive exported component. |
| `com.strava.authorization.loginorsignup.LogInOrSignUpActivity` | Exported with no intent filter — reachable by explicit component name; can be launched into a login flow by another app. |
| `com.strava.authorization.otp.OtpAndPasswordAuthActivity` | Same. |
| `com.strava.mapplayground.MapPlaygroundActivity` | Exported map-renderer debug playground. |
| `com.strava.routing.utils.MapCacheClearingActivity` | Exported; wipes the map cache. |
| `com.strava.view.superuser.*` (via `strava://`?) | `SuperUserToolsActivity` is **not** exported. `ToggleSubscriptionFragmentActivity` and `OverrideExperimentCohortActivity` are also not exported, but `ManageFeatureSwitchActivity`/`OverrideExperimentCohortActivity` are reachable through the modular-sheet deep link family. |
| `com.strava.util.RecordingIntentCatcherActivity` | Exported, listens for `android.nfc.action.NDEF_DISCOVERED` and Google-Fit activity MIME types — an NFC-tag-to-start-recording entry point. |
| `com.strava.view.activities.TextShareCatcherActivity` / `ImageShareCatcherActivity` | Exported `SEND` handlers for `text/plain` and `image/*` — the standard "share to Strava" targets. |
| `com.google.firebase.iid.FirebaseInstanceIdReceiver` | Exported with `com.google.android.c2dm.permission.SEND` — signature-protected, so safe. |
| `androidx.work.impl.diagnostics.DiagnosticsReceiver` | Exported but guarded by `android.permission.DUMP` (system only). |
| `androidx.profileinstaller.ProfileInstallReceiver` | Exported, guarded by `android.permission.DUMP`. |
| `com.google.assistant.appactions.widgets.pinappwidget.PinAppWidgetService` | Exported `PIN_APP_WIDGET` — binds an assistant widget. |
| `com.strava.WearMessageListener` | Exported `MESSAGE_RECEIVED` on scheme `wear` — Wear OS integration. |
| `com.strava.onboarding.service.OnboardingService` | Exported but guarded by `android.permission.BIND_JOB_SERVICE` (system only). |
| `com.strava.iterable.IterableNotificationTrackingService` | Same `BIND_JOB_SERVICE` guard. |
| `androidx.health.platform.client.impl.sdkservice.HealthDataSdkService` | Health Connect binder; Android's Health Connect permission model gates the actual data. |

### 3.3 Verified App Links

Strava registers `https://app.strava.com`, `https://www.strava.com` and `https://strava.com` with `pathPrefix`/`path`/`pathPattern` rules across ~45 activities, and `https://strava.app.link` (Branch.io). Whether `assetlinks.json` is actually published for these domains was **not verified** (that requires a network fetch) — so it is unknown whether these are true verified App Links (which would prevent link hijacking) or merely unverified HTTPS intents (which any app could intercept).

### 3.4 NFC

`RecordingIntentCatcherActivity` declares `android.nfc.action.NDEF_DISCOVERED` with `mimeType="vnd.google.fitness.activity/biking|running|other"` and a `strava.com/nfc` path. An NFC tag in range can therefore start a recording.

---

## 4. Embedded credentials and identifiers

> **Note on redaction.** The values in this table were originally published literally. They
> have been replaced with placeholders before publication, because republishing live
> credentials for a commercial product creates an abuse channel against Strava's and its
> vendors' quotas. The **existence, type, location and capability** of each credential is
> unchanged — that is the analytically useful part. The literal values are recoverable
> from your own copy of the APK; `docs/00-method-and-evidence.md` §4 documents how.

Every value below was recovered from the artifact. They are **public by definition** (they ship in the APK) and are not secrets in the cryptographic sense — but each grants a capability.

| Value | What it is | Capability |
|---|---|---|
| `<GOOGLE_MAPS_API_KEY>` | Google Maps API key (manifest `com.google.android.geo.API_KEY`) | Maps, **restricted to the package by Play's API-key scoping** |
| `<GOOGLE_API_KEY_2>` | Google API / Crashlytics key | Crash reporting, app-check |
| `<GOOGLE_OAUTH_CLIENT_ID>` | Google OAuth client id | Sign-in scope |
| `<FIREBASE_APP_ID>` | Firebase App ID | Firebase project |
| `<FIREBASE_PROJECT_NUMBER>` | Firebase / GCP project number | — |
| `https://api-project-<FIREBASE_PROJECT_NUMBER>.firebaseio.com` | Firebase Realtime Database | — |
| `api-project-<FIREBASE_PROJECT_NUMBER>.appspot.com` | Firebase Storage bucket | — |
| `https://<SENTRY_PUBLIC_KEY>@o352714.ingest.sentry.io/<SENTRY_PROJECT_ID>` | **Sentry DSN** (manifest `io.sentry.dsn`) | Submit crash reports, events, session replays to Strava's Sentry org |
| `fb284597785309` | Facebook App ID | Facebook Login |
| `<FACEBOOK_CLIENT_TOKEN>` | Facebook Client Token | Facebook Graph API |
| `<ZENDESK_APP_ID>` | Zendesk application id | Create Zendesk tickets |
| `<ZENDESK_OAUTH_CLIENT_ID>` | Zendesk OAuth client id | Zendesk authenticated endpoints |
| `com.strava.connect.app` | Strava OAuth client id | Start Strava's OAuth flow |
| `com.strava` | Stream.io `hostAppId` | Stream Chat |
| `<BRANCH_LIVE_KEY>` | **Branch.io live key** (in `StravaApplication.onCreate`) | Branch attribution, deep links, install referrers |
| `<MAPBOX_PUBLIC_TOKEN_1>` | Mapbox public token (`u=strava`) | Mapbox API |
| `<MAPBOX_PUBLIC_TOKEN_2>` | Mapbox public token | Mapbox API |
| `<MAPBOX_PUBLIC_TOKEN_3>` | Mapbox public token | Mapbox API |
| `<MAPBOX_PUBLIC_TOKEN_4>` | Mapbox public token | Mapbox API |

**Not present:** no Strava `client_secret`, no Strava `client_id` (the OAuth client id is `com.strava.connect.app`), no AWS keys, no `sk.*` secret tokens, no MapTiler key, no Iterable API key, no Snowplow collector URL, no private signing key.

The Mapbox tokens are `pk.` (public) tokens with `u=strava` — they are for **style/tile access**, and the map tile URLs confirm the Mapbox **server secret** is injected at runtime by the app and never shipped.

---

## 5. Permissions → what they unlock

| Permission | Data |
|---|---|
| `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` | GPS during recording and map use |
| `FOREGROUND_SERVICE` + `FOREGROUND_SERVICE_LOCATION` | Continuous background recording with a persistent notification |
| `ACTIVITY_RECOGNITION` | Motion-based activity detection |
| `BODY_SENSORS` *(not requested)* | — |
| `READ_MEDIA_IMAGES` / `READ_MEDIA_VIDEO` / `READ_MEDIA_AUDIO` / `READ_MEDIA_VISUAL_USER_SELECTED` | Photo and video attachment picking |
| `READ_EXTERNAL_STORAGE` (maxSdk 32) / `WRITE_EXTERNAL_STORAGE` (maxSdk 29) | Legacy file access |
| `ACCESS_MEDIA_LOCATION` | **Photo EXIF GPS** — the location a photo was taken at |
| `CAMERA` | Optional (not a required feature) |
| `RECORD_AUDIO` | Voice cues during recording; Oboe audio path in LITR |
| `READ_CONTACTS` | Friend-finding / contact sync |
| `GET_ACCOUNTS` / `USE_CREDENTIALS` | Account lookup for sign-in |
| `health.WRITE_EXERCISE` / `WRITE_DISTANCE` / `WRITE_TOTAL_CALORIES_BURNED` / `READ_WEIGHT` | Health Connect writes |
| `BLUETOOTH` / `BLUETOOTH_ADMIN` / `BLUETOOTH_SCAN` / `BLUETOOTH_CONNECT` | Bluetooth sensors, bike computers |
| `POST_NOTIFICATIONS` | Push + the ongoing recording notification |
| `RECEIVE_BOOT_COMPLETED` | Resume recording after reboot |
| `SCHEDULE_EXACT_ALARM` | Exact alarms (reminders) |
| `VIBRATE`, `WAKE_LOCK` | Audio cues, background work |
| `DETECT_SCREEN_CAPTURE` | Detect screenshots (used for security/anti-fraud or screenshot-aware UI) |
| `com.android.vending.BILLING` | In-app subscriptions (Play Billing 7.0.0) |
| `com.google.android.gms.permission.AD_ID` | Advertising ID |
| `com.google.android.finsky.permission.BIND_GET_INSTALL_REFERRER_SERVICE` | Install referrer |
| `ACCESS_ADSERVICES_ATTRIBUTION` / `ACCESS_ADSERVICES_AD_ID` | Ad attribution (Privacy Sandbox) |
| `com.google.android.providers.gsf.permission.READ_GSERVICES` | Google services state |
| `com.strava.permission.C2D_MESSAGE` | Strava's own push channel (signature-protected) |
| `com.strava.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` | AndroidX internal |

`res/xml/ga_ad_services_config.xml` sets `<attribution allowAllToAccess="true"/>` — the app allows ad-attribution APIs broadly.

---

## 6. Where the data goes

### First-party Strava endpoints (22 hosts)

`www.strava.com` · `strava.com` · `m.strava.com` · `app.strava.com` · `c.strava.com` · `communityhub.strava.com` · **`graphql.strava.com`** · `graphql.staging.strava.com` · `tiles.strava.com` · `tessa-labs.strava.com` · `cdn-1.strava.com` · **`content-a.strava.com`** · `heatmap-external-{a,b,c}.strava.com` · `heatmap-origin-k8s.strava.com` · `personal-heatmaps-external.strava.com` · `yis.strava.com` · `yis-static.strava.com` · `support.strava.com` · `www.staging.strava.com` · plus two Strava-owned Google Cloud tenants.

**182 distinct API paths** were recovered (101 Strava-owned, 62 Stream.io, 17 Zendesk). Strava exposes both a **REST v3** surface (~80 relative paths under `/api/mobile`, `/api/private`, `/api/v3/...`) and a **GraphQL** surface (`graphql.strava.com`, ~35 operation names).

Two **staging** hosts (`graphql.staging.strava.com`, `www.staging.strava.com`) ship in the production binary.

### Third-party recipients and what each receives

| Recipient | Hosts | Data implied |
|---|---|---|
| **Mapbox** | `api.mapbox.com`, `apps./config./events.mapbox.com` | Device location, activity recognition, map telemetry |
| **MapTiler** | `api.maptiler.com`, `cloud.maptiler.com` | Map tiles & fonts |
| **Sentry** | `o352714.ingest.sentry.io` | Crashes, **native NDK crashes**, user id, **session replay** (`sentry-android-replay` is in the APK) |
| **Embrace** | `data.emb-api.com` | Sessions, **HTTP capture**, user info, NDK crash data |
| **Snowplow 6.3.1** | *(collector not embedded — remote config)* | Screen views, geolocation context |
| **Firebase / Google** | `app-measurement.com`, `firebaseinstallations.googleapis.com`, `www.googleapis.com` | Analytics, Firebase Installations ID, push, Google Fit data |
| **Google Ads** | `pagead2.googlesyndication.com`, `www.googleadservices.com` | Advertising ID, install & deep-link conversion |
| **Facebook** | `facebook.com`, `graph.%s` | Friends, profile photos, events; login (the `facebook.deprecation` package shows Facebook login is being retired) |
| **Zendesk** | `static.zdassets.com`, `www.zendesk.com` | Support tickets, **user email** |
| **Branch.io** | `api2.branch.io`, `cdn.branch.io`, `bnc.lt` | Referrer, install attribution, deep links |
| **Iterable** | `api.iterable.com` | Push campaigns, deep-link attribution |
| **Stream.io Chat** | `chat.stream-io-api.com`, `*.tilestream.net` | Messages, **user-shared location**, media uploads, Agora tokens |
| **reCAPTCHA 18.7.0** | `www.recaptcha.net` | Bot token |
| **Apple WeatherKit** | `weatherkit.apple.com` | Weather attribution |
| **Rive** | `public.rive.app` | Animation assets |
| **Runna** | `club.runna.com`, `web.runna.com` | Training plans, referrals |
| **Garmin Connect** | *(no host embedded)* | Activity push, live tracking |
| **Age Signals 0.0.2** | `age-signals.properties` | Age-assurance signal (the `com.strava.ageassurance.BirthdayInputActivity` screen handles birthday entry) |

### The heatmap path specifically

Strava's heatmaps are the most sensitive first-party data flow. The native engine's tile template shows the *server* decides what each viewer sees, using the requesting user's own settings:

```
include_everyone / include_followers_only / include_only_me
respect_privacy_zones / include_commutes / filter_type / filter_start / filter_end
```

The tile is fetched from `{host_name}/tiles/{strava_athlete_id}/grayscale/{z}/{x}/{y}.png` — the **athlete id is in the URL path**, and the visibility scope is a query parameter. Two implications: (a) viewing a personal heatmap reveals that a viewer looked at a specific athlete's routes, and (b) the scope parameters are **server-enforced**, not client-enforced — a modified client could request a wider scope, and only the server prevents it. There is no evidence of client-side enforcement.

---

## 7. Debug and diagnostic surfaces in the production build

| Surface | What it exposes |
|---|---|
| `com.strava.net.superuser.NetworkLogDatabase` + `NetworkLogActivity` / `NetworkLogDetailActivity` | A local database of HTTP requests and responses, with a UI to read them. `res/xml/file_provider_paths.xml` reserves a `network_log` directory. |
| `com.strava.superuser.AnalyticsCacheActivity` | Raw analytics event cache |
| `com.strava.nettools.language.OverrideLanguageHeaderActivity` | Overrides the `Accept-Language` header sent to Strava servers |
| `com.strava.nettools.canaries.*` | Per-endpoint canary/staging routing |
| `com.strava.featureswitchtools.ManageFeatureSwitchActivity` | Flip feature switches locally |
| `com.strava.featureswitchtools.experiments.OverrideExperimentCohortActivity` | Force an A/B cohort |
| `com.strava.superuser.subscription.ToggleSubscriptionFragmentActivity` | Toggle subscription state |
| `com.strava.superuser.ChangeLanguageActivity` | Override app language |
| `com.strava.mapplayground.MapPlaygroundActivity` | Map renderer playground |
| `com.strava.modularframework.tools.DebugToolsActivity` | Modular-UI inspection |
| `com.strava.superuser.metering.ManageMeteringActivity` | Usage metering |
| Native `PrivateApi` / `DebugApi` (53 + 4 JNI functions) | Mock tile server, screenshot tests, performance tests, `dumpEngineState`, `setTestAccountForPersonalHeatmap` |
| `assets/mock_segment_efforts.json` | Sample test data shipped in release |
| `com.strava.settings.view.HuevoDePascua` | Easter-egg screen |
| `com.strava.mapplayground` / `com.strava.map.tools.MapSettingsActivity` | Map settings |
| `res/xml/debug_tools_preferences.xml`, `res/xml/modular_ui_tools_preferences.xml` | Debug preference trees |

None of the Super User screens are exported. They are, however, **fully compiled into the production binary** and reachable through the app's own navigation in some builds. Their presence means the request/response log, the analytics cache and the feature-switch state are all locally readable on a rooted device or via the debug tools.

---

## 8. Other observations

| Item | Finding |
|---|---|
| `android:allowBackup="true"` | Cloud backup on, with `database/strava` and one preference file excluded |
| `android:supportsRtl="false"` | No right-to-left layout support |
| `android:largeHeap="true"` | Requests an oversized heap (needed for the 3D map engine) |
| `android:extractNativeLibs="false"` | `.so` files are stored uncompressed and mapped directly — the standard for API 23+ and a small integrity benefit |
| `android:resizeableActivity="false"` | Not compatible with split-screen / foldables in the usual way |
| 32-bit only | No arm64 split; the 10.8 MB Fatmap engine and 5.7 MB Mapbox renderer are 32-bit builds |
| `DETECT_SCREEN_CAPTURE` | The app can detect when the user takes a screenshot — used to hide sensitive content or to flag abuse |
| `com.strava.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` | AndroidX's guard on dynamically-registered receivers |
| Google Play stamp present | The APK carries a valid Google Play source stamp; it has not been repackaged since Google issued it |
| App Links not verified | `assetlinks.json` was not fetched; HTTPS link handling may be interceptable by other apps |

---

## 9. What was **not** established

- The **v2/v3 signer identity** was not cryptographically verified (`signature_verification: "not_performed"` by the Android provider). The Google Play stamp *was* verified.
- **Certificate pinning** is configured, but only for `cdn-1.strava.com` and `graphql.strava.com` (one SPKI pin: `sha256/q61HEYJJK/e1i0DQnyP6R6tGOrRC5MbbsTcI9ev6cZ4=`). The main API host and every third-party host are unpinned. The scan was static over all 7 DEX files and covered every `const-string` reference, so a *second* pinning configuration assembled purely at runtime from computed strings would not be visible.
- **Runtime behaviour** was never observed. The APK was never executed; no network traffic was captured; no request/response was inspected.
- **`assetlinks.json`** and the **Snowplow / Iterable endpoints** are not embedded in the binary and could not be enumerated.
- **Strava's OAuth client secret** is not in the APK; it is server-side only.
- Whether `StravaIdentityProvider` actually returns credentials to arbitrary callers, or enforces a caller allowlist, requires runtime testing.
- FORTIFY_SOURCE hardening was not detected in the native libraries; this may be a toolchain default rather than a deliberate choice.
