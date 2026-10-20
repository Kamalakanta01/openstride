# The Native Map Engine — Interface and the Open Question

Strava ships its own 3D map engine, `libFatmapSdk.so` (10.8 MB, 312 exported JNI functions). The original binary is proprietary and **cannot** be redistributed or reused here.

This document records the *interface* so a clean-room renderer can be built to the same contract, and states the open question plainly.

**Source:** Ghidra 12.1.4 analysis of `libFatmapSdk.so` (27,535 procedures recovered; the binary is stripped, so class names were recovered from RTTI and JNI type descriptors).
**Confidence:** the JNI surface is *observed* and complete. The internal behaviour is *derived* from RTTI, embedded URL templates, and embedded library strings.

---

## 1. What the engine is

| | |
|---|---|
| Base | OGRE-Next (the `Ogre::` namespace, `Ogre::Compositor`, GLES2 render system) |
| Scripting | Lua 5.4.8, embedded |
| Geometry | Mapbox Vector Tile (`.vector.pbf`), plus terrain rasters |
| Packaging | `assets/assets.zip` — 3 Mapbox-GL styles, 776 POI icons, 48 GLSL shaders, OGRE `Main` + `RTShaderLib`, SDFonts, skybox, `GlobalAltitudeRange/9.gart` |
| Elevation | EGM96 geoid model, bundled as `assets/database/geoid-height-map-v0.db` (SQLite, protobuf-framed JPEG tiles) |

---

## 2. The complete JNI surface

`spec/fatmap-jni.json` has the machine-readable version (class → method → JNI symbol → address). Here is the shape of it:

| Class | # | Role |
|---|---:|---|
| `TerrainEngine` | 53 | the root object: config, content, camera, listeners, terrain, heatmaps |
| `PrivateApi` | 53 | internal controls: offline mode, disk cache, Lua scripting, perf tests, mock tile server |
| `Camera` | 32 | fly-to, fly-through, follow, zoom/rotation/translation speed, first-person view |
| `EngineConfiguration` | 19 | camera target, client name, log level, disk cache, tile source config |
| `Utils` | 16 | user marker, custom markers, tile grid, highlighted tiles |
| `Content` | 15 | custom lines, dropped pins, circle markers, filter accessors |
| `ActivityContent` | 11 | draw activities, gradients, polyline highlights, privacy icons |
| `OverriddenFeatureProperties` | 11 | runtime feature-flag overrides (bool/int/double/string) |
| `RouteContent` | 14 | draw routes, animated routes, waypoints, route scrubber |
| `CameraFlyThroughControls` | 14 | scrub, seek, speed multipliers, duration |
| `RecordContent` | 6 | live recording line, finish marker, live segments |
| `SegmentsFilter`, `StravaPoiFilter`, `OsmFilter` | 6, 8, 2 | map filters |
| `TouchListener` | 6 | tap, double tap, long press, touch, tap-and-move |
| `MREHolder` | 5 | engine lifecycle: create, foreground, background, memory warning |
| `DebugApi` | 4 | FPS overlay, dump engine state |
| `LuaAnimationHandler` | 4 | animation advance/progress/error |
| `ContentSelectionHandle`, `TestingContext*`, `TestsListener`, `TrackPerfTestCompletionListener` | 1–4 | test harness |
| `WindowLifecycleListener`, `DeviceOrientationListener`, `VisibleAreaSamplesSink`, `CameraDeltaTimeProvider`, `CommandService` | 2–4 | environment events |

### The most useful methods for a clean-room implementation

These are the hooks the original app uses to configure the engine — and they tell you what a replacement needs to support:

```
TerrainEngine.setServerConfig(...)              ← point the engine at a different tile server
TerrainEngine.setStravaAuthData(...)            ← credentials for that server
TerrainEngine.setStravaSourcesExtraHeaders(...) ← extra HTTP headers
TerrainEngine.setImageryOverlay(...)
TerrainEngine.setMapType(...)
TerrainEngine.setGlobalHeatmapConfig(...)
TerrainEngine.setNightlyHeatmapConfig(...)
TerrainEngine.setWeeklyHeatmapConfig(...)
TerrainEngine.setPersonalHeatmapConfig(...)
TerrainEngine.setIsTerrain3dEnabled(...)
TerrainEngine.getElevation(...)
EngineConfiguration.loadFromFile(...)
EngineConfiguration.setUseOnDiskCache(...)
EngineConfiguration.setEnableRasterMegaTiles(...)
Offline.loadBundles(...) / unloadBundles(...)
```

**`setServerConfig` + `setStravaAuthData` + `setStravaSourcesExtraHeaders` are the officially supported redirection points.** A clean-room engine should offer the same three, so the app layer does not care where tiles come from.

### Tile URL templates the original compiles in

```
{MAPBOX_TILES_SERVER_URL}/mapbox.mapbox-streets-v8/{z}/{x}/{y}.vector.pbf?access_token={MAPBOX_SERVER_SECRET}
{MAPBOX_TILES_SERVER_URL}/mapbox.mapbox-terrain-v2/{z}/{x}/{y}.vector.pbf?access_token={MAPBOX_SERVER_SECRET}

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

Note the heatmap visibility parameters. In the original app those are **enforced by Strava's server**; in this project they are enforced locally, which is both simpler and more correct.

---

## 3. The open question — now answered

> **Resolved.** The interface contract has been extracted in full
> (`spec/fatmap-java-api.json`, `spec/fatmap-native-api.json`) and a
> clean-room engine implementing it now exists in `map-engine/`. Its API
> surface is 1:1 with the original library: all 312 JNI entry points, with
> identical symbol names, verified by `tools/verify_api_parity.py`.
> The reimplementation lives behind the `MapEngine` facade, so the renderer
> remains swappable.
>
> The two routes below are still the choice for the *renderer*. Route A has
> been taken for the first cut; Route B remains the fidelity path.

Two viable routes for the renderer. **Route A has been chosen for the first
cut**; the choice drives most of the map work.

### Route A — a different renderer, not a reimplementation

Use an existing open-source map stack and implement only the *Strava-shaped* layer on top:

- **MapLibre Native** (LGPL-3.0 / BSD-3) — a Mapbox GL fork with the same vector-tile and style-spec model, so the bundled `StravaTopo` / `StravaDark` / `StravaWinter` styles keep working
- **OSM tiles** — or your own MBTiles pack
- Implement the `TerrainEngine` API surface as a Kotlin facade over MapLibre

**Pros:** months of work instead of years; mature rendering; you inherit the style spec and the tile format.
**Cons:** not 3D terrain in the same sense; no Flyover; the "engine" becomes a thin wrapper.

### Route B — a clean-room 3D renderer matching the 312-function contract

Rebuild the engine against the documented API using OGRE-Next (MIT) or a modern alternative.

**Pros:** full fidelity to the contract; Flyover, terrain and the compositor are achievable.
**Cons:** very large. The original is 27,535 procedures of C++ and was built by a team over years. A faithful reimplementation is a multi-year project.

### What the asset bundle settles

The 15 bundled Mapbox Style Spec v8 styles show where the tiles come from
(`spec/fatmap-styles.json`):

- The **Strava Planet** family fetches from MapTiler *and* from
  `strava-map-tiles-planet-tilejson-public.s3.us-east-1.amazonaws.com` — a
  **public** bucket with no credentials in the URL. The planet tile set is
  genuinely public.
- The **Strava classic** family fetches from Mapbox Studio style IDs under
  Mapbox's `cerilewis` account, which needs a Mapbox token.

So the "replace the tile source" part of Route A is easier than expected for
the Planet styles and needs a Mapbox token for the classic ones.

The engine also ships its **own Mozilla CA bundle** (144 roots, 2025-12-02) in
`assets/assets.zip` as `myrootcert.pem`. The engine's TLS trust is
independent of the operating system's, and that file is the trust
configuration to reproduce.

### What is not reproducible either way

| Missing | Why |
|---|---|
| Strava's **global/weekly/nightly heatmaps** | Computed on Strava's servers from every athlete's tracks. Not in the artifact; not derivable. **Your personal heatmap can be synthesised from your own activities** — that is a different feature, and a good one. |
| Strava's **POI set** (776 icons) | Proprietary assets. Substituting OSM POIs loses Strava's curated places (water fountains, trailheads, Strava segments). |
| **3D terrain** | `mapbox-terrain-v2` is a Mapbox commercial dataset. Use open terrain (e.g. Copernicus, SRTM) and accept the difference. |
| **Vector tile styles tuned to Strava's data** | The three bundled styles reference Strava-specific layers that only Strava's tiles have. They'll need editing to work on OSM data. |

---

## 4. Recommendation

Start with **Route A**. Get a working vector map with your own tiles, your own heatmap overlay, and a clean `TerrainEngine`-shaped Kotlin API. Leave the 3D/flyover work as a later, optional module behind the same interface.

The important thing is to keep the **facade** stable:

```kotlin
interface MapEngine {
    suspend fun setServerConfig(config: ServerConfig)
    suspend fun setAuthData(auth: AuthData)
    fun camera(): Camera
    fun content(): Content
    fun terrain(): Terrain
    fun offline(): Offline
    fun debug(): DebugApi
}
```

Then the choice of renderer is an implementation detail, and you can swap MapLibre for a custom OGRE backend later without touching anything else.

---

## 5. Licensing note

OGRE: **MIT** — usable, include the licence text.
Rive: **MIT** — usable.
Mapbox Maps SDK v11: **proprietary** — not usable, not redistributable.
MapLibre Native: **LGPL-3.0 / BSD-3** — usable, with the LGPL obligations for the library itself.
The original Fatmap SDK: **proprietary** — not usable, not redistributable.

If a contributor adds a dependency whose licence forbids redistribution, the repository stops being distributable. Check `PROVENANCE.md` §5 before adding anything.
