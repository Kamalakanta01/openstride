# Can Strava Run With No Backend?

**Short answer: partly yes, partly no — and the split is not where you'd expect.**

Strava's Android app is a *thin client*. Almost nothing is computed on the phone; the phone is a renderer and a recorder for data that lives on Strava's servers. So "no backend" doesn't mean "switch off the network" — it means **you must replace Strava's server**, and the app expects that server to be Strava's.

What you can replace depends entirely on whether the feature needs *other people*.

---

## 0. Three very different projects hide inside the request

| | What it means | Effort | Result |
|---|---|---|---|
| **T1 — Private Strava** | Block telemetry; keep using Strava's real server | **Hours** | App works normally; analytics/crash/telemetry never leave the device |
| **T2 — Offline Strava** | Pre-download what you need, then fly | **Hours** | App already supports this — no patching needed |
| **T3 — Local Strava** | Replace Strava's servers with your own | **4–8 weeks** | Solo-athlete features work; **all social features are gone forever** |

Most people asking this actually want T1 or T2. Read those first.

---

## 1. The hard boundary: features that need other athletes

These **cannot** work locally, no matter how much you patch, because the data doesn't exist on your device:

| Feature | Why |
|---|---|
| Feed, followers/follows, clubs | Needs other users |
| Kudos, comments, mentions | Needs other users |
| Challenges, competitions, group events, leaderboards | Needs other users |
| Global segment leaderboards, segment discovery | Needs other users' efforts |
| Route discovery, route popularity | Server-side search over all Strava data |
| Share targets, year-in-sport comparison | Needs other users |
| **Strava heatmaps** | Computed from *every* athlete's tracks on Strava's servers. Your own heatmap can be synthesised; the global one cannot be recreated. |
| Push notifications (FCM), marketing push (Iterable) | Google + Iterable servers |
| Garmin / Fitbit / Wear cloud sync | Vendor servers |
| Subscription entitlement | Server-authoritative (see §6) |

**Everything else — recording, maps, routes, segments (your own efforts), fitness, insights, relative effort, power curves, best efforts, training log, training zones, goals, gear, photos, privacy zones, monthly stats, year-in-sport of *yourself* — can be made to work locally.**

That's still roughly 60% of the app's value for a solo athlete.

---

## 2. T1 — Private Strava (no patching required)

If the goal is *"my health and route data shouldn't go to Strava's analytics vendors"*, you do **not** need to modify the APK. The telemetry flows are separable from the functional ones.

### Blocklist

From the analysis (`01-network-surface.md`), these hosts carry analytics, crash reporting, advertising and attribution. Blocking them leaves recording, maps, syncing and the feed fully working.

**Crash & performance (these see your device + crash dumps):**
```
o352714.ingest.sentry.io
data.emb-api.com
config.emb-api.com
```

**Analytics & attribution:**
```
app-measurement.com
firebaseinstallations.googleapis.com
www.googleapis.com            (analytics only — breaks Maps/Play Services if over-blocked)
pagead2.googlesyndication.com
www.googleadservices.com
api2.branch.io
cdn.branch.io
api.iterable.com
events.mapbox.com
apps.mapbox.com
config.mapbox.com
```

**Support & marketing:**
```
www.zendesk.com
static.zdassets.com
public.rive.app
club.runna.com
web.runna.com
```

**Third-party APIs you may still want (map tiles, chat):**
```
api.mapbox.com
chat.stream-io-api.com
www.recaptcha.net
weatherkit.apple.com
```

### How to block them

| Method | Notes |
|---|---|
| **Private DNS / hosts file** on the device | A DNS-filtering app (e.g. a local-VPN blocker) that returns 0.0.0.0 for the above. Works without root. **Recommended.** |
| **Firewall app** | Same effect, per-app if you want Sentry blocked but Mapbox allowed. |
| `network_security_config` | ⚠️ **Cannot block.** NSC can only *add* trust anchors and *permit* cleartext. It has no deny mechanism. |
| **Patch `OkHttpClient`** | Viable but see §5 — and unnecessary if DNS works. |

### Expected effect

- The app functions normally. Recording, maps, uploading, the feed, chat, and Strava's own analytics all still work — **Strava's first-party server still sees everything**, because that's the product.
- Crash reports stop reaching Sentry/Embrace, so Strava loses visibility into your device's stability. That's the trade.
- Some screens may show a spinner or an error toast when a blocked SDK's callback never fires. The `superuser` network log will make this obvious.

---

## 3. T2 — Offline Strava (the feature already exists)

**Strava already ships offline maps.** The code is `com.strava.dynamicmapmre.offline` (56 classes) and the native engine has `Offline.loadBundles`, `setUseOnDiskCache`, `setReindexOnDiskResources`.

The catch is that Strava's offline bundles are **server-issued and integrity-checked**:

```
com.strava.dynamicmapmre.offline.data.remote.OfflineMapsApi
  getId(String)                          -> BundleIdResponse
  get(String)                            -> BundleResponse     (compressedSizeBytes, decompressedSizeBytes, sha256)
  order(BundleOrderBody)                 -> BundleOrderResponse (coverage, tileset)
  download(String)                       -> ResponseBody
```

So for a genuine offline trip:

1. Open the map region in Strava on Wi-Fi, with the offline-download UI.
2. Let it fetch and store the bundle (the `sha256` is verified against the server's response).
3. Go offline. The engine serves tiles from `OfflineMapBundleStorage` in the local Room DB.

**Limitations to plan around:** only the downloaded region works; **heatmaps are not part of the bundle** (`content-a.strava.com` heatmap tiles are live-server only); elevation terrain comes from Mapbox (`mapbox-terrain-v2`) and is not bundled — though the **EGM96 geoid model is** (`assets/database/geoid-height-map-v0.db`), so height correction still works offline.

**No patching required. This is the answer to "I want Strava in the mountains".**

---

## 4. T3 — Local Strava: the architecture

If you genuinely want the app to run with no Strava server at all, you are building **a local server that impersonates Strava**, plus shims for identity and assets.

### 4.1 The two choke points

Traffic leaves the app through exactly two doors. This is the good news: you don't need to touch 5,140 classes.

```
┌─ Kotlin/Java layer ────────────────────────────────┐
│  Retrofit interfaces (com.strava.net, dynamicmapmre) │
│        │                                             │
│        ▼                                             │
│  OkHttpClient  ← built in LFd/d2$a;                │
│        │   (okHttpClient, retrofitClient,            │
│        │    remoteLogger, RemoteImageHelper,         │
│        │    CertificatePinner → cdn-1/graphql)       │
└────────┼────────────────────────────────────────────┘
         │
┌────────▼────────────────────────────────────────────┐
│  Native layer — libFatmapSdk.so (OGRE + Lua)        │
│  Map tiles: tiles.strava.com, content-a.strava.com  │
│             api.mapbox.com                           │
└──────────────────────────────────────────────────────┘
```

**Java door.** `LFd/d2$a;` is a Dagger module that builds the `okHttpClient` and the `retrofitClient`. **You add one `okhttp3.Interceptor` here** and every HTTP call in the app funnels through you. (The app ships no Strava-authored OkHttp interceptor today — only `HttpLoggingInterceptor` and Mapbox's — so there's no existing hook to fight with.)

**Native door — and the good news.** You do **not** need to patch the `.so`. The Fatmap engine already exposes exactly the JNI you need:

```
Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setServerConfig
Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setStravaAuthData
Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setStravaSourcesExtraHeaders
Java_com_fatmap_sdk_api_EngineConfiguration_00024CppProxy_native_1loadFromFile
Java_com_fatmap_sdk_api_EngineConfiguration_00024CppProxy_native_1setUseOnDiskCache
Java_com_fatmap_sdk_api_Offline_00024CppProxy_native_1loadBundles
```

`setServerConfig` + `setStravaAuthData` + `setStravaSourcesExtraHeaders` are the engine's own supported hooks for pointing tile downloads at a different server with different auth. The compiled-in URL templates are just defaults.

### 4.2 The four layers you must build

| # | Layer | What you do | Where |
|---|---|---|---|
| 1 | **Transport cut** | One OkHttp `Interceptor` that (a) rejects any host not on your allowlist and (b) returns local responses. Also handles the native engine's tile requests. | `LFd/d2$a;` |
| 2 | **Local API** | Implement the REST v3 + GraphQL contracts the app calls. ~101 Strava-owned paths, 35 GraphQL operations. Backed by a local DB. The app's data layer is Retrofit + RxJava (`MN.f` = `Single<T>`), so you implement `Single<T>` returners, or run a real local HTTP server and rewrite the Retrofit base URL. | new module |
| 3 | **Identity shim** | Mint a local `AccessToken(accessToken, refreshToken, expiresAt, isSignUp)`; patch `TokenApi.refreshToken` / `refreshTokenV2`; make `StravaIdentityProvider` (exported, `authority=com.strava.identity`) serve a local identity; bypass the login activities into a local "create athlete" flow. | `com.strava.net.token.*`, `com.strava.authorization.*` |
| 4 | **Asset plane** | Bundle map tiles as MBTiles; point the engine at a local tile source; synthesise heatmap tiles from your own activities; ship the app's local Room DB schema. | `dynamicmapmre`, `EngineConfiguration` |

### 4.3 Redirect instead of intercept: the canary hook

Strava already ships a **per-service host-override mechanism**:

```
com.strava.net.superuser.ServiceCanaryOverride   (Parcelable, 4 strings)
com.strava.nettools.canaries.ServiceCanaryListActivity
com.strava.nettools.canaries.ServiceCanaryConfigurationActivity
```

This routes individual services to canary/staging hosts. If it accepts arbitrary hosts, it can point the app at `http://127.0.0.1:<port>` **with zero bytecode patching** — you'd run a local server and configure the override in the superuser UI. Worth checking first: it could turn a 4-week project into a 1-week one.

### 4.4 Map tiles without a Mapbox account

The engine fetches Mapbox vector tiles with a server secret:

```
{MAPBOX_TILES_SERVER_URL}/mapbox.mapbox-streets-v8/{z}/{x}/{y}.vector.pbf?access_token={MAPBOX_SERVER_SECRET}
{MAPBOX_TILES_SERVER_URL}/mapbox.mapbox-terrain-v2/{z}/{x}/{y}.vector.pbf?access_token={MAPBOX_SERVER_SECRET}
```

Options:
- **Bundle a region as MBTiles** (OpenMapTiles / OSM-derived) and serve it from your local server, rewriting the URL template via `setServerConfig`.
- **Convert the bundled Strava styles to a local style spec** — `assets/assets.zip` contains 15 Mapbox Style Spec v8 styles and 48 of Strava's own shaders, so the *styling* is already local; only the *tiles* aren't.
- Use `OverriddenFeatureProperties` to restyle vector tiles from a free source.

The 3D terrain (Fatmap's `mapbox-terrain-v2` + `GlobalAltitudeRange/9.gart`) and Strava's own POI set are **not** in the bundle, so a fully faithful 3D map is not reproducible without server data.

### 4.5 Local data model

The app's local databases are already Room, already on the device, and already hold most of the raw material:

```
strava-database          challenge-database     experiments-database
recording-database       privacyZonesDatabase   recentsDatabase
athlete-workouts.db      map-style-json-database
activity-tag-database    stream_chat_client.db  exoplayer_internal.db
com.strava.dynamicmapmre.offline_maps
```

The activity model is Thrift (`com.strava.geomodels.model.route.thrift.*`: `Route`, `LegacyRoute`, `Polyline`, `ActiveTime`, `CompletionTimePoint`, `DirectionAction`...). Local-only means these become the *source of truth* rather than a cache — which is actually the easy part, because the schemas are already there.

---

## 5. Tooling you'll need

**On this machine right now:**

| Tool | Status |
|---|---|
| `adb` | ✅ present (`~/.local/bin/adb`) — but **no device connected** and no emulator |
| `java` / `javac` / `keytool` | ✅ present (JDK 21) |
| `python3`, `openssl` | ✅ present |
| `apksigner`, `zipalign`, `d8` | ❌ missing — need **Android SDK build-tools** |
| `apktool` / `baksmali` / `smali` | ❌ missing — needed to edit DEX |
| emulator | ❌ missing |

**You will need to install:** Android SDK build-tools (`apksigner`, `zipalign`), `apktool` (or `smali`/`baksmali` for surgical edits), and either a physical device or an emulator (x86_64 image — note the app itself is 32-bit ARM, so an **ARM64 emulator image or an arm64 device** is required, or you must add an arm64 split).

**Re-signing.** Any modified APK must be re-signed with your own key. The Google Play source stamp will be destroyed (expected — you can't keep a stamp on a modified APK). For `minSdk 26`/`targetSdk 35` you want **v2 + v3** signing:

```bash
keytool -genkey -v -keystore my.keystore -alias local -keyalg RSA -keysize 4096 -validity 10000
apksigner sign --ks my.keystore --v1-signing-enabled true --v2-signing-enabled true --v3-signing-enabled true out.apk
zipalign -p -f 4 in.apk aligned.apk     # -p keeps uncompressed .so page-aligned (extractNativeLibs=false)
```

**`zipalign -p` matters here:** the app sets `android:extractNativeLibs="false"`, so the `.so` files are stored uncompressed and must stay 16-byte page-aligned or the app crashes on launch.

**Editing DEX.** `apktool d` → edit smali → `apktool b` → sign. The patches above are all in *your own* new code plus a handful of call-site swaps, so they're feasible in smali. Alternatively, a far more maintainable route: **extract the Strava-attributable logic into your own app** (see §7).

---

## 6. The one piece I won't help build

**Subscription entitlement.** Strava's subscription state is decided server-side:

```
com.strava.superuser.subscription.ChangeSubscriptionApi
  setSubscriptionOverride(String)   // a REMOTE call
```

and entitlement-gated features (subscription screens, `crossgrading`, `student plan`, `trial education`, custom app icons, `toggle subscription` in the superuser menu) all reflect whatever the server says. Forging that response to unlock paid features is circumventing a commercial payment, so **I won't help with that specific piece.**

Everything else in this guide — the transport cut, the local API, the identity shim, the local asset plane, the map tiles, the offline behaviour — is fine to build.

Note that a local build has a natural, legitimate side effect: with no server, there is no entitlement to check, so the app's subscription-gated screens simply won't have data. Whether that leaves features locked depends on how they're gated client-side, and **that's the part you should not rely on**. If you want the paid features legitimately, the app already supports a 30-day free trial and Strava's student plan.

---

## 7. If the real goal is "my own data, my own rules"

There's a cleaner path than patching Strava: **build a small local app on the same foundations.** From this analysis you already have everything you'd need:

- The **local data model**: `strava-database` Room schema, Thrift route/activity models, `com.strava.geomodels.*`
- The **recording pipeline**: `com.strava.recording.StravaActivityService` (foreground service, `foregroundServiceType="location"`, resumable via `RecoverActivityReceiver` on `BOOT_COMPLETED`, `UnsyncedActivity` recovery)
- The **sensor stack**: BLE + ANT+ integrations, `ActivityRecognition`
- The **map engine**: Fatmap's public JNI API (312 functions, documented in `07-native-code.md`) — camera, content, heatmap config, terrain, offline bundles
- The **analytics-free design system**: `com.strava.spandex` (56 Compose components)

A clean-room local app using the Fatmap JNI + Room + a local OSM tile pack gives you: recording, maps, routes, insights, and full data ownership — with no patching, no re-signing, no brittle smali, and no possibility of breaking when Strava ships a new build.

---

## 8. Effort summary

| Goal | What you actually do | Time |
|---|---|---|
| "Stop them seeing my usage data" | DNS/firewall blocklist, §2 | 1–2 hours |
| "Use Strava offline" | Pre-download offline maps, §3 | 30 minutes |
| "No Strava server, solo features only" | §4 in full | 4–8 weeks |
| "Point the app at my own server" | Canary-override hook, §4.3 | 1 week (if it accepts arbitrary hosts) |
| "Own my fitness data completely" | Clean-room local app, §7 | 2–4 weeks |
