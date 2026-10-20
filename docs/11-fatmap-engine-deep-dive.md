# 11 — Inside the Fatmap engine

A deep dive into `libFatmapSdk.so`, Strava's proprietary 3D map engine, and the
clean-room interface contract extracted from it.

**Companion artefact:** [`spec/fatmap-java-api.json`](../spec/fatmap-java-api.json)
(130 classes, 823 methods) and
[`spec/fatmap-native-api.json`](../spec/fatmap-native-api.json) (30 proxy classes,
312 native methods with exact JNI descriptors).

---

## 1. What the artefact is

| | |
|---|---|
| File | `libFatmapSdk.so` |
| Size | 10,795,116 bytes |
| Format | ELF 32-bit LSB shared object, ARM, EABI5, stripped |
| Built by | NDK r28c (13676358) |
| SHA-256 | `d84f1771cc832dac59a68bb4e62bacd07dc4e8d55e5931d9cd1fde07b09ba050` |
| Procedures | 27,535 |
| JNI exports | 312 |
| RTTI classes | 263 |

It links only `libandroid.so`, `liblog.so`, `libEGL.so`, `libm.so`, `libdl.so`,
`libc.so` — everything else (the renderer, the tile parser, the Lua VM) is
statically linked in.

### It is not Mapbox

Strava ships **both** `libFatmapSdk.so` and `libmapbox-maps.so` (5.7 MB).
They are different things:

- **Mapbox Maps SDK v11** — the general-purpose map renderer. Proprietary,
  commercial licence. Strava uses it for the non-3D map surfaces.
- **Fatmap** — Strava's *own* engine, built for 3D terrain and the "Flyover"
  route view. It consumes Mapbox vector tiles but renders them itself, through
  OGRE-Next.

The RTTI makes the lineage obvious: the `Ogre::` namespace, `Ogre::Compositor`,
the GLES2 render system, and `Ogre::Resource` subclasses such as
`TerrainQuadTree`, `TerrainQuadPool`, `TerrainMeshPool` and `TerrainTexturePool`.

### It embeds Lua

`LuaBindings`, `LuaBindingsImpl`, `LuaCamera`, `LuaException`, `LuaIconRenderer`
in the RTTI; **Lua 5.4.8** in the strings. The engine exposes a scripting
surface to the app — `PrivateApi.setLuaGlobalString/Float/Int/Line/Image/Animation`,
`startLuaAnimationScript`, `LuaAnimationHandler.shouldAdvanceAnimation` — which is
how the animated route playback and some icon effects are driven.

---

## 2. Architecture

### 2.1 The Java surface is un-obfuscated

This is the single most useful fact about the artefact, and it is what makes
a clean-room reimplementation tractable.

While the rest of the application is R8-obfuscated into names like `LFd/d2$a;`,
the Fatmap SDK's Java API ships **fully readable**, because it is a published
SDK that Strava's own application code has to compile against:

```
com.fatmap.sdk.api.TerrainEngine
com.fatmap.sdk.api.Camera
com.fatmap.sdk.api.Content
...
160 class descriptors across classes3.dex
```

Every one of the 312 native methods is declared in a nested `$CppProxy` class:

```java
class Camera$CppProxy {
    native void native_flyTo(double, double, float, Float, Float, float,
                             CameraMargins, CameraActionCompletionListener);
    native void nativeDestroy();
}
```

**This is an interface, not an implementation.** Reading it tells you what the
engine does, not how. That is exactly the material a clean-room
reimplementation is allowed to use, and it is what
[`spec/fatmap-java-api.json`](../spec/fatmap-java-api.json) captures.

### 2.2 The native architecture

Three layers, confirmed by decompilation of the JNI entry points:

```
Kotlin app
   │  com.fatmap.sdk.api.TerrainEngine.setServerConfig(...)
   ▼
CppProxy (Java)                ← holds a `long` native handle
   │  native_setServerConfig(long, ServerConfig, boolean)
   ▼
JNI wrapper                    ← unmarshals Java objects into C++ structs
   │  (**(code **)(*piVar1 + 0x14))(piVar1, auStack_170, param_6 != 0);
   ▼
C++ interface (vtable)         ← an abstract class, dispatched by vtable offset
   │
   ▼
Engine implementation
```

The vtable offsets are stable and observable in the decompiled wrappers:

| `TerrainEngine` vtable offset | Method |
|---:|---|
| `+0x14` | `setServerConfig` |
| `+0x1c` | `setStravaAuthData` |
| `+0x20` | `setStravaSourcesExtraHeaders` |
| `+0x3c` | `setIsTerrain3dEnabled` |
| `+0x84` | `getElevation` |

So `setServerConfig` is the **fourth** virtual in the `TerrainEngine` interface.
A reimplementation that declares its interface in the same order produces the
same dispatch.

### 2.3 `ServerConfig` is five strings and a nested object

`setServerConfig` unmarshals into a 0x160-byte struct:

```
+0x00  std::string   mapboxServerSecret
+0x10  std::string   mapboxServerURL
+0x20  std::string   stravaTileServerURL
+0x30  std::string   stravaPoiServerURL
+0x40  std::string   segmentsServerURL
+0x50  TileSourceConfig  (0x100 bytes, nested)
+0x150 bool          "tileSourceConfig present"
```

This matches the Java class exactly — `ServerConfig` has five `String` fields
and one `TileSourceConfig` field. `TileSourceConfig` in turn holds **sixteen
URL templates**:

| | |
|---|---|
| `mapboxStreetsURLTemplate` | `mapboxStreetsTerrainURLTemplate` |
| `stravaPlanetURLTemplate` | `stravaPlanetSportURLTemplate` |
| `stravaContoursURLTemplate` | `stravaPoiURLTemplate` |
| `segmentsTemplate` | `heightmapURLTemplate` |
| `gradientURLTemplate` | `summerImageryURLTemplate` |
| `winterImageryURLTemplate` | `winterOverlayImageryURLTemplate` |
| `globalHeatmapURLTemplate` | `nightlyHeatmapURLTemplate` |
| `weeklyHeatmapURLTemplate` | `personalHeatmapURLTemplate` |

**These three methods — `setServerConfig`, `setStravaAuthData`,
`setStravaSourcesExtraHeaders` — are the officially supported redirection
points.** Point them at your own server and the engine fetches from it. That
is the single most important fact for anyone who wants to run this engine
without Strava.

### 2.4 Terrain operations are actions

The engine's mutations are a command pattern. The RTTI shows the whole set:

```
TerrainAction_SetServerConfig      TerrainAction_SwitchImagery
TerrainAction_LoadBundles          TerrainAction_UnloadBundles
TerrainAction_LoadHeightmaps       TerrainAction_LoadHeightmapsFromDisk
TerrainAction_ReloadHeightmaps     TerrainAction_ReloadImagery
TerrainAction_DownloadHeightmaps   TerrainAction_LOD
TerrainAction_QuadActions          TerrainAction_RefreshContent
TerrainAction_SyncFunction
```

Every one is queued and applied on the engine thread, which is why the API is
split between "set" methods (queue an action) and "get" methods (read the
current state).

### 2.5 The rest of the class inventory

263 RTTI classes, grouped:

| Group | Examples |
|---|---|
| **Terrain** | `Terrain`, `TerrainQuadTree`, `TerrainQuadPool`, `TerrainMeshPool`, `TerrainTexturePool`, `TerrainContentRendererPool`, `TerrainElevationInterface`, `TerrainHeightmapInterface` |
| **Tiles** | `TileSource`, `TileSourceImpl`, `TileSourceRepo`, `TileSourceAnyThreadApi`, `TileSourceEngineThreadApi`, `TileParameterPack`, `TileDownloadAuthorizer(Impl)`, `TileGrid`, `TemplateTileUrlProvider` |
| **Camera** | `CameraController`, `CameraMovement`, `CameraOrbitMovement`, `CameraRotateMovement`, `CameraZoomMovement`, `CameraDragMovement`, `CameraGestureMovement`, `CameraDistanceEstimator`, `CameraAction*`, `FlyThroughAction`, `Flythrough`, `QuadAction` |
| **Rendering** | `BatchedRenderer`, `CustomContentRenderer`, `LineRenderer`, `LabelRenderer`, `BillboardRenderer`, `QuadResource*`, `ResourcePool*`, `SymbolObject*`, `PointSymbolObject`, `LineSymbolObject`, `ScreenSymbolObject` |
| **Styles** | `MapboxStyle`, `MapboxLineStyle`, `MapboxIconStyle*`, `MapboxLabelStyle`, `MapboxCircleStyle`, `AnimatedMapboxStyleInterface`, `DynamicStyleParser`, `ImmutableStyleFilter` |
| **Heatmaps** | `GlobalHeatmapReadynessLogic`, `NightlyHeatmapReadynessLogic`, `WeeklyHeatmapReadynessLogic`, `HeatmapReadynessLogic`, `PersonalHeatmapAuthDataStorage` |
| **Downloads** | `DownloadEngine`, `ProductionDownloadEngine`, `SimulatedFailureDownloadEngine`, `BackgroundDownloadHandler`, `BackgroundTileDownloadProcessor`, `CachedFileDownloader` |
| **Fonts** | `BitmapFont(Impl)`, `BitmapFontRepo(Impl)`, `AsyncGlyphRasterizer`, `GlyphsLoadingStatus(Impl)`, `TextLineBuilder(Impl)` |
| **Elevation** | `HeightmapDataPool`, `HeightmapProbe*`, `AltitudeRangeReadException` |
| **Lua** | `LuaBindings(Impl)`, `LuaCamera`, `LuaException`, `LuaIconRenderer` |
| **Strava-specific** | `StravaContoursTileSource`, `StravaPlanetTileSource` |
| **Infra** | `Sdk`, `UtilsManager`, `Singleton(Base)`, `Proto`, `Json`, `KeyValueReader` |

---

## 2b. The asset bundle, in full

`assets/assets.zip` is 20,367,814 bytes and 962 entries. Its own `README.md`
gives the provenance:

> * **Main, RTShaderLib** — copied from Ogre (Ogre3D `Media/` directory)
> * **Shared** — formerly a submodule pointing at the `FatmapSDKSharedAssets` repository
> * **shaders, textures** — FatmapMobileSDK assets not shared with the web version

The OGRE attribution is therefore stated *by the artefact itself*, not inferred.

| Group | Count | What it is |
|---|---:|---|
| `Shared/Configs/GeneratedIcons/X1`, `/X2` | 388 + 388 | Generated POI, medal and badge icons, each at 1x and 2x |
| `Shared/Configs/Strava*/style.json` | 15 | Mapbox Style Spec v8 styles |
| `Shared/Shaders/Terrain`, `/Content`, `/` | 48 | Strava's own shaders, in `.fs`/`.vs` form |
| `RTShaderLib/GLSL/` | 14 | OGRE's own shader library (`FFPLib_*`, `SGXLib_*`) |
| `Main/` | 14 | OGRE's default resources, including `DefaultShaders.metal` |
| `Shared/Textures/Heatmap` | 13 | Heatmap colour ramps |
| `Shared/Textures/Terrain` | 5 | Terrain overlay textures |
| `Shared/SDFonts` | 8 | Signed-distance-field fonts (`.pbfont`, protobuf) |
| `Shared/VectorFonts` | 10 | TrueType fonts (Noto Sans, Boathouse) |
| `Shared/Skybox` | 3 | `Skybox.obj` plus high and low-resolution textures |
| `Data/Packages/GlobalAltitudeRange/9.gart` | 1 | 1,048,596 bytes — the global altitude range, zoom level 9 |
| `lua/` | 3 | The Lua runtime's standard library plus two scripts |
| `myrootcert.pem` | 1 | **A bundled Mozilla CA bundle** |

Two details in that table are worth more than the rest.

### The heatmap ramps and terrain textures match the API exactly

13 heatmap ramps, and the `HeatmapColorScheme` enum has 7 values — BLUE,
BLUE_RED, ORANGE, PINK, PURPLE, SUNSET, WEEKLY_RAINBOW. Six schemes have a
`_dark` and a `_light` variant (12 files) plus one `weekly_rainbow.png` = 13.
The 5 terrain textures are `TerrainAspect`, `TerrainAvalanche`,
`TerrainGradient`, `TerrainAltitude` and `TerrainFlats`, matching the
`ImageryOverlay` enum (ASPECT, AVALANCHE, GRADIENT, GRID, NONE) and the
`OverlayPolygon` gradient/aspect ranges.

The API and the asset bundle are two views of the same design.

### The engine carries its own TLS trust store

`myrootcert.pem` is the **complete Mozilla CA bundle — 144 root certificates,
dated 2025-12-02** — not a pinning bundle. Cross-checking against the host's
own CA store shows the bundle carries 24 roots the host's current set does not
have (Entrust, QuoVadis, SwissSign, COMODO, certSIGN, GLOBALTRUST, Certigna
and others).

This means **the engine's TLS trust is independent of the operating system's.**
The engine does its own HTTPS with its own root set, so a root that the OS has
retired may still be trusted by the engine, and a root the OS trusts but this
bundle omits would be rejected by it. For anyone reimplementing the engine,
this file is the trust configuration to reproduce — and it is a supply-chain
surface worth reviewing.

### The Lua scripts

- `lua/protoc.lua` — 38 KB. Lua has no native Protocol Buffers, so the engine
  ships a **pure-Lua protobuf runtime**. This is what makes `VectorTile`
  decoding possible in the scripting layer.
- `lua/AnimatedImagePlayer.lua` — 3 KB. A module (`--- @module MRE`) that
  drives animated map markers, matching the `AnimatedImage`, `CustomImage` and
  `setLuaGlobalAnimation` API.

So Lua is used for two things: protobuf encoding, and the MRE animation
framework.

---

## 2c. The tile pipeline's actual sources

The 15 styles split into two families, and they fetch from completely
different places.

**Strava classic** — `StravaTopo`, `StravaTopoDark`, `StravaTopoWinter`,
`StravaHybrid`, `StravaSatellite` (409–522 layers each). Their sources are
`mapbox://` URLs naming **Mapbox Studio style IDs** under the `cerilewis`
account — Mapbox's account for Strava — plus
`mapbox://mapbox.mapbox-streets-v8`. Glyphs come from
`mapbox://fonts/cerilewis/{fontstack}/{range}.pbf` and the sprite from a
`mapbox://sprites/cerilewis/...` URL.

**Strava Planet** — the `StravaPlanet*` family (212–548 layers each). These
fetch from **MapTiler** (`api.maptiler.com`, `cloud.maptiler.com`) with a
runtime-injected `?key={key}`, **and from a public AWS S3 bucket**:

```
https://strava-map-tiles-planet-tilejson-public.s3.us-east-1.amazonaws.com/planet/tilejson.json
```

That bucket name says what it is: a *public* bucket, no credentials in the URL.
The Strava Planet tile set is therefore fetchable without a Strava account —
which is directly useful for a local-only build, and is the one part of the
map stack that is genuinely public.

**Five of the shipped styles are named `[Staging] ...`** — `[Staging]
[Topo Dark]`, `[Staging] [Topo Light]`, `[Staging] [Hybrid]`,
`[Staging] [Satellite]`, `[Staging] [Winter Hybrid]`. Staging
configurations are shipped in the production APK. Their `*Experiment`
siblings are named `Hiking Dash ...`.

---

## 2d. Both native map stacks are loaded

The DEX loads **nine** native libraries, and both map engines among them:

| Library | Size | |
|---|---:|---|
| `fatmap` | 10.8 MB | Strava's 3D engine |
| `mapbox-maps` | 5.7 MB | Mapbox Maps SDK v11 |
| `mapbox-common` | 3.5 MB | Mapbox Common SDK |
| `rive-android` | 3.8 MB | Rive animation |
| `embrace-native` | 1.1 MB | Embrace SDK |
| `sentry` | 441 KB | Sentry |
| `sentry-android` | 12 KB | Sentry |
| `litr-jni` | 128 KB | LITR log transport |
| `datastore_shared_counter` | 4.4 KB | AndroidX DataStore |

Mapbox is **not** vestigial: the application loads it alongside Fatmap. The
Fatmap engine renders the 2D/3D map; the Mapbox SDK is present for the
components that use it. `assets/sdk_versions/` records
`com.mapbox.common.core = 24.7.0` and `com.mapbox.maps.core = 11.7.0`.

The `com.fatmap.sdk` Java package contains **only** `com.fatmap.sdk.api.*` —
160 classes. The `Sdk` class seen in the native RTTI is a C++ class, not a
Java one, so the entire Java-visible surface of the engine is the API package
this project has captured in full.

---

## 3. The tile pipeline

The engine compiles in these URL shapes:

```
{MAPBOX_TILES_SERVER_URL}/mapbox.mapbox-streets-v8/{z}/{x}/{y}.vector.pbf
    ?access_token={MAPBOX_SERVER_SECRET}
{MAPBOX_TILES_SERVER_URL}/mapbox.mapbox-terrain-v2/{z}/{x}/{y}.vector.pbf
    ?access_token={MAPBOX_SERVER_SECRET}

https://{host_name}/tiles/{strava_athlete_id}/grayscale/{z}/{x}/{y}.png?missing=empty
  &filter_type={filter_type}{filter_start}{filter_end}
  &include_everyone={include_everyone}
  &include_followers_only={include_followers_only}
  &include_only_me={include_only_me}
  &respect_privacy_zones={respect_privacy_zones}
  &include_commutes={include_commutes}{maybe_access_token}

https://content-a.strava.com/identified/globalheat/{activity_type}/grayscale/{z}/{x}/{y}.png?v=19&missing=empty
https://content-a.strava.com/nightheat/{activity_type}/{z}/{x}/{y}
https://content-a.strava.com/weeklyheat/{z}/{x}/{y}?sport_types={activity_type}&format=png&colormap=grayscale&missing=empty
```

Note the heatmap visibility parameters. In the original, those are **enforced
by Strava's server**. In a local implementation they can be enforced locally —
which is strictly stronger, because a modified client cannot ask the server
to widen the scope.

### Offline bundles

`Offline.loadBundles(ArrayList<String>, BundleLoadListener)` takes bundle
identifiers. Each bundle is a directory on disk; the app verifies the contents
against a `sha256` supplied by `BundleResponse` (which carries
`compressedSizeBytes`, `decompressedSizeBytes` and `sha256`). The engine
verifies the digest before accepting the bundle.

---

## 4. What is reproducible, and what is not

| Feature | Reproducible? | Why |
|---|---|---|
| Vector map rendering | ✅ | Mapbox Vector Tile is an open format |
| 2D camera, pan/zoom/rotate | ✅ | Trivial maths over the projection |
| 3D terrain flyover | ⚠️ partially | Needs a terrain dataset and an OGRE-class renderer |
| Route and activity overlays | ✅ | Pure geometry |
| Personal heatmap | ✅ | Computed from your own tracks |
| **Global / weekly / nightly heatmaps** | ❌ | Strava computes these on its servers from every athlete's tracks |
| **Strava's POI set** (388 icons, at 1x and 2x) | ❌ | Proprietary assets |
| **3D terrain** (`mapbox-terrain-v2`) | ❌ | Mapbox commercial dataset |
| **Strava's map styles** | ⚠️ editable | They reference Strava-specific layers; they need editing for OSM data |

---

## 5. The clean-room reimplementation

[`map-engine/`](../map-engine/) implements the extracted interface.

### What "1:1" means here

**The API surface is 1:1, exactly.** All 312 JNI entry points exist, with the
same symbol names as the original library:

```
Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setServerConfig
```

The verification is mechanical — the generated symbol table is compared
against the symbol table extracted from `libFatmapSdk.so`:

```bash
python3 - <<'EOF'
import json, re
gen = set(re.findall(r'Java_[A-Za-z0-9_]+',
                     open('map-engine/generated/fatmap_jni.cpp').read()))
spec = json.load(open('spec/fatmap-jni.json'))
orig = {it['jni'] for v in spec.values() for it in v}
print(len(gen), len(orig), gen == orig)
EOF
# 312 312 True
```

So the resulting shared object is a **drop-in replacement** for the original at
the JNI boundary: the same app code, compiled against the same Kotlin API,
loads it and works.

**The behaviour is 1:1 where it can be.** 234 of the 312 methods are
implemented — the camera, the content and route overlays, the configuration, the
tile source and URL templating, the feature-property overrides, the offline
bundle verification, the debug dump. The remaining 78 are the original's test
harness and its server-dependent features, which have no meaning without
Strava's infrastructure. They keep the generated default body: the entry point
exists and does nothing.

See [`map-engine/COVERAGE.md`](../map-engine/COVERAGE.md) for the per-class
breakdown.

### How it is built

The contract is **machine-readable**, so the API layer is *generated* rather
than hand-written:

```
spec/fatmap-java-api.json   ─┐
spec/fatmap-native-api.json ─┴─→ tools/fatmap_codegen.py ─┬─→ app/src/main/kotlin/com/fatmap/sdk/api/*.kt   (130 classes)
                                                            ├─→ map-engine/generated/fatmap_types.h              (value structs)
                                                            ├─→ map-engine/generated/fatmap_api.h                (C++ interfaces)
                                                            ├─→ map-engine/generated/fatmap_jni.cpp              (312 entry points)
                                                            ├─→ map-engine/generated/fatmap_jni_values.cpp        (marshalling)
                                                            └─→ map-engine/COVERAGE.md
```

The generated C++ interfaces give every method a default no-op body. The
hand-written engine overrides the ones it implements. That is why 312 methods
cost ~250 lines of boilerplate rather than 312 hand-written JNI wrappers.

### The discipline

Only the **interface** was used: class names, method names, parameter types,
field names, and the structure of the structs visible in the decompiled
marshalling code. No implementation was transliterated.

The one thing that was studied rather than inferred is *how the engine is put
together* — the vtable dispatch, the action pattern, the tile pipeline. Knowing
that a `TerrainAction_SetServerConfig` queues an action on the engine thread
is architecture, and architecture is how an interoperating implementation is
supposed to be designed.

---

## 6. Method and limits

| | |
|---|---|
| Tool | REA, Ghidra 12.1.4 + JADX 0.7.1 (Android provider) |
| Static analysis only | The artefact was never executed |
| Decompiled | ~25 JNI entry points and their immediate callees, in depth |
| Inventoried | all 312 JNI exports, all 27,535 procedures, all 263 RTTI classes |
| DEX parsing | `tools/dex_native_api.py` — a from-scratch DEX reader, so the native declarations could be extracted with exact descriptors |

**What was not established:**

- The internal behaviour of the renderer. The OGRE integration is inferred
  from the RTTI, the bundled `assets.zip` (15 styles, 388 icons at 1x and 2x,
  48 Strava shaders, 14 OGRE GLSL files, `Main`/`RTShaderLib`), and the class
  names.
- The exact tile-LOD algorithm. `LodCalculationControlParams`
  (`getTileSplitThresholdOffset`, `getMaxLodDifferenceForOffset`) exposes two
  tunables, but their effect was not measured.
- Whether the heatmap readyness logic differs between the global, weekly,
  nightly and personal variants beyond their names.
- The behaviour of `loadFromFile` on the configuration: the decompiled version
  hit bad instruction data and was not recovered. The reimplementation accepts
  flat `key: value` pairs and documents that as its own format.

**One class of licence note.** The extracted contract is the Fatmap SDK's
*published Java interface*, captured from an APK that ships it. Package names,
method names and parameter types are not copyrightable expression, and the
contract is the same category as a `.h` file or an API reference: it describes
an interface so that others can implement against it. The implementation
behind it is Strava's, and none of it is reproduced here.
