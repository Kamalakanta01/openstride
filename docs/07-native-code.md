# Strava Android — Native Code

**Split:** `config.armeabi_v7a.apk` · 12 shared libraries · 25,873,088 bytes · **32-bit ARM only** (no arm64, no x86)

---

## 1. Library inventory

| Library | Bytes | SHA-256 | What it is | Build |
|---|---:|---|---|---|
| `libFatmapSdk.so` | 10,795,116 | `d84f1771cc832dac59a68bb4e62bacd07dc4e8d55e5931d9cd1fde07b09ba050` | **Strava's proprietary 3D map engine** | NDK r27, clang 19.0.1, LTO+PGO |
| `libmapbox-maps.so` | 5,738,088 | `4b8f326331398386a1f6efd61f023e4e994bf2f3962a9d22ea820abfa902b60b` | Mapbox Maps SDK v11.7.0 (2D renderer) | — |
| `librive-android.so` | 3,839,492 | `086568efa952df34515bea989a292181f7ec895f7c52327187cefc4226fbe717` | Rive animation runtime (C++ 10.1.0) | NDK r26, clang 18.0.3 |
| `libmapbox-common.so` | 3,505,116 | `9303b549bda225351e0fec676342e9df49be61e5fd5a248f5496e7b34a47300a` | Mapbox Common v24.7.0 | — |
| `libembrace-native.so` | 1,064,128 | `831c1600b6ca7c24e7c5ca57e6777dddfa9c60c2eeb59a5a444d0d6a547d3bd4` | Embrace native crash capture (libunwindstack-ndk) | clang 21.0.0, GitHub Actions |
| `libc++_shared.so` | 872,872 | `91b49f121b3120538faff020fcf535ade1d945e65ccd793fc0b815b5799bbdf0` | NDK C++ runtime (shared) | — |
| `libsentry.so` | 440,816 | `ad3cd37c71a935c788122e771ac98ab329ffde0bc0ac9fae571139ac4886876f` | Sentry native SDK 0.8.4 | — |
| `liblitr-jni.so` | 128,436 | `63e36b131b72be84783d8981c36872e38f1236cbe21a5427f955fd264b0e2d7e` | **LinkedIn LITR** media renderer 1.5.7 | clang 12.0.8 |
| `libsentry-android.so` | 12,080 | `987623d98285e22262dbfdfd3e1c72b6484ec18ca8e40992f9e81d911f7925f8` | Sentry Android JNI shim | — |
| `libandroid-tests-support-code.so` | 11,692 | `8ac9624d00bf596f22a5d9679b8784ecf73b6fc1e6728fd84ec6e185529fbbff` | Mapbox test-support helper (shipped in release) | — |
| `libandroidx.graphics.path.so` | 7,252 | `41399eba6fc2a60f6f14642375c1824f3cf25eb8fec7397d753730a3ceda3e2b` | AndroidX path iteration (Compose) | — |
| `libdatastore_shared_counter.so` | 4,416 | `716c5d8d2cac8ca0edf65da8f139c7886b726ac79d542a14edeb94994ba6d3dc` | DataStore multi-process counter | — |

### Hardening (all 12)

| | |
|---|---|
| RELRO | **Full** on all 12 |
| `BIND_NOW` | Yes on all 12 |
| NX (non-executable stack) | Yes on all 12 |
| Stack canaries (`__stack_chk_guard`) | Present on all except `libc++_shared.so` (the NDK runtime) |
| FORTIFY_SOURCE | **Not detected on any library** |

No library in this build is stripped of its hardening flags. `libandroid-tests-support-code.so` and `libandroidx.graphics.path.so` are the only libraries with a non-`.so` naming convention worth noting as test/debug leftovers in a release package.

---

## 2. Ghidra analysis of `libFatmapSdk.so`

| | |
|---|---|
| Format | ELF32, ARM, EABI5, shared object |
| SHA-256 | `d84f1771cc832dac59a68bb4e62bacd07dc4e8d55e5931d9cd1fde07b09ba050` |
| Image base | `0x10000` |
| Segments | 31 |
| **Procedures recovered** | **27,535** |
| Strings recovered | 23,462 |
| `.text` | 8,126,768 B (0x250a60–0xa10b90) |
| `.rodata` | 1,132,984 B |
| `.bss` | 333,841 B |
| `.ARM.exidx` / `.ARM.extab` | 162,808 B / 464,644 B (unwind tables present) |

**The library is stripped.** Only 312 JNI exports, ~1,531 `std::` instantiations and 1,135 imported `EXTERNAL` symbols are named; roughly 26,000 procedures are `FUN_xxxxxxxx` with no symbol. The internal class hierarchy below was therefore recovered from **RTTI typeinfo strings** and the JNI type descriptors, not from the symbol table.

### 2.1 It is built on OGRE

`libFatmapSdk.so` embeds a full **OGRE (Object-Oriented Graphics Rendering Engine)** build with the GLES2 render system. Direct evidence from the binary's string and RTTI tables:

```
GLES2RenderSystem::bindGpuProgram
GLES2RenderSystem::initialiseContext
GLES2RenderSystem::_copyContentsToMemory
GLES2HardwareBuffer::createBuffer / lock / unlock / readData
GLES2HardwarePixelBuffer::blitFromMemory / blitToMemory
GLES2Texture::_createGLTexResource
GLES2FBOManager destructor called, but not all renderbuffers were released.
GLES2 does not support uniform buffer objects
[GLES2] : Valid FBO targets
// Created by Ogre RT Shader Generator. All rights reserved.
#include <OgreUnifiedShader.h>
N4Ogre7FrustumE   N4Ogre10CompositorE   N4Ogre10EGLContextE   N4Ogre10GpuProgramE
N4Ogre10ImageCodecE   N4Ogre10QuaternionE   N4Ogre10RenderableE   N4Ogre10SceneQueryE
```

`Ogre::Compositor` is the OGRE-**Next** compositing framework, so this is a modern OGRE build, not legacy OGRE 1.x. The `ogre` symbol namespace and `Fatmap::Ogre::*` RTTI entries confirm Strava's engine is a fork/extension of OGRE-Next.

### 2.2 Fatmap's own class hierarchy (263 classes recovered from RTTI)

Top classes by RTTI occurrence:

| Count | Class | Role |
|---:|---|---|
| 97 | `Fatmap::Sdk` | the engine façade |
| 18 | `Fatmap::QuadAction` | tile-quad render actions |
| 13 | `Fatmap::TestingContextImpl` | test harness |
| 8 | `Fatmap::CameraController` | camera state machine |
| 8 | `Fatmap::CameraInteractionHandler` | touch/gesture → camera |
| 8 | `Fatmap::Json` | JSON parsing |
| 8 | `Fatmap::Renderer` | render loop |
| 7 | `Fatmap::CommandLine` | scriptable commands |
| 7 | `Fatmap::QuadContentData` | tile content |
| 7 | `Fatmap::BackgroundTileDownloadProcessor` | prefetch queue |
| 5 | `Fatmap::BatchedRendererT` | batched draw |
| 5 | `Fatmap::GeometryGroupingByKey` | geometry batching |
| 5 | `Fatmap::Proto` | protobuf models |
| 4 | `Fatmap::QuadResourcePool` | resource pooling |
| 4 | `Fatmap::HeightmapProbeBase` | terrain height sampling |
| 3 | `Fatmap::Flythrough` | 3D flyover animation |
| 3 | `Fatmap::DynamicStyleParser` | runtime style parsing |
| 3 | `Fatmap::Async` | async task system |
| 2 | `Fatmap::VectorTile` | Mapbox vector tiles |
| 2 | `Fatmap::DownloadEngine` (+`Production`, `Debug`, `SimulatedFailure` variants) | tile fetching |
| 2 | `Fatmap::VisibleAreaCalculator` | what the user can see |
| 2 | `Fatmap::LabelRendererBase` | text labels |
| 2 | `Fatmap::AsyncVectorTileRasterizer` | rasterise vector tiles |
| 2 | `Fatmap::TileGrid` | slippy-map grid |
| 1 | `Fatmap::LuaBindings` | **Lua scripting bridge** |
| 1 | `Fatmap::MapboxStyle` | Mapbox style spec |
| 1 | `Fatmap::AssetManager` | asset loading |
| 1 | `Fatmap::LineRenderer`, `CircleMarker`, `FocusMarker`, `UserMarker`, `MarkerBase` | map annotation rendering |
| 1 | `Fatmap::CameraAction`, `Fatmap::FlyThroughAction` | camera scripting |
| 1 | `Fatmap::ClickHandler`, `MoveHandler`, `DragHandler`, `TouchHandler`, `InputHandler`, `GestureEvent`, `InputEvent`, `MouseEvent` | input |
| 1 | `Fatmap::BitmapFont`, `Fatmap::Expression`, `Fatmap::Json` | text & style expressions |
| 1 | `Fatmap::EngineConfig`, `Fatmap::UtilsManager`, `Fatmap::TrashBinImpl`, `Fatmap::Memory`, `Fatmap::NonCopyable` | infrastructure |
| 1 | `Fatmap::Flythrough::AccelDecelTimeline`, `SpeedGuidedTimeline`, `Timeline` | flyover camera curves |

Sub-namespaces include `Fatmap::Sdk::Api` (93), `Fatmap::Ogre` (6), `Fatmap::DownloadHandleImpl`, `Fatmap::Pool`, `Fatmap::Android`, `Fatmap::ArenaAllocator`, `Fatmap::FarPlaneCameraSpaceClipper`.

### 2.3 Lua scripting is built in

```
$LuaVersion: Lua 5.4.8  Copyright (C) 1994-2025 Lua.org, PUC-Rio
/AnimatedImagePlayer.lua
default-script
fallback-script
atomic_script_run
```

Strava's map engine embeds a full **Lua 5.4.8** interpreter, exposed through `Fatmap::LuaBindings`, `LuaAnimationHandler` and `PrivateApi.startLuaAnimationScript` / `setLuaGlobalString` / `setLuaGlobalFloat` / `setLuaGlobalInt` / `setLuaGlobalLine` / `setLuaGlobalImage` / `setLuaGlobalAnimation` / `clearLuaPersistedStore`. This is how map animations (the "flyover" fly-through, animated routes) are scripted.

### 2.4 The complete 312-function JNI surface

Every JNI export of `libFatmapSdk.so`, grouped by Java class. This is Strava's proprietary map-engine API in full.

| Java class | # | Exported native methods |
|---|---:|---|
| `ActivityContent` | 11 | `nativeDestroy`, `displayActivity`, `displayActivityWithPrivacyIcons`, `displayActivityTruncated`, `displayActivity2`, `removeActivity`, `removeAllActivities`, `applyActivityGradient`, `clearActivityGradient`, `applyActivityPolylineHighlights`, `clearActivityPolylineHighlights` |
| `Camera` | 32 | `nativeDestroy`, `setDynamicExaggerationEnabled`, `setDynamicExaggerationMultiplier`, `enterFirstPersonView`, `flyTo`, `flyToWithZoomLevel`, `translateTo`, `flyToBounds`, `setBearing`, `followUserLocation`, `followUserLocationWithZoomLevel`, `flyToTarget`, `flyThrough`, `flyThroughInteractive`, **`flyover`**, `getLookAtPoint`, `getCustomLookAtPoint`, `setZoomSpeed`, `setRotationSpeed`, `setTranslationSpeed`, `stop`, `getTarget`, `setCameraViewType`, `getCameraViewType`, `isWorldPointVisible`, `getVisibleAreaBounds`, `addInteractionListener`, `removeInteractionListener`, `addStateListener`, `removeStateListener`, `getScreenPosition`, `getScaleMetersPerPixel` |
| `CameraDeltaTimeProvider` | 2 | `nativeDestroy`, `getDeltaTime` |
| `CameraFlyThroughControls` | 14 | `nativeDestroy`, `isActive`, `stop`, `isCameraDetached`, `isRefocusPossible`, `refocus`, `setTrackProgress`, `getTrackProgress`, `seekToAnimationStart`, `setAnimationSpeedMultiplier`, `setTrackSpeedMultiplier`, `setCameraDistanceMultiplier`, `getTrackSpeedMultiplier`, `getDurationSeconds` |
| `CommandService` | 2 | `nativeDestroy`, `execute` |
| `Content` | 15 | `nativeDestroy`, `addCustomLine`, `addCustomGradientLine`, `removeCustomLine`, `addDroppedPin`, `removeDroppedPin`, `removeAllDroppedPins`, `addCircleMarker`, `removeCircleMarker`, `getOsmFilter`, `getStravaPoiFilter`, `getActivityContent`, `getRecordContent`, `getRouteContent`, `getSegmentsFilter` |
| `ContentSelectionHandle` | 1 | `nativeDestroy` |
| `DebugApi` | 4 | `nativeDestroy`, `showFpsStatsOverlay`, `hideFpsStatsOverlay`, `dumpEngineState` |
| `DeviceOrientationListener` | 2 | `nativeDestroy`, `onDeviceOrientationChanged` |
| `EngineConfiguration` | 19 | `nativeDestroy`, `createDefault`, `loadFromFile`, `setCameraTarget`, `getCameraTarget`, `setClientName`, `getClientName`, `setEnableRasterMegaTiles`, `getEnableRasterMegaTiles`, `setLogLevel`, `getLogLevel`, `setLoggingToStdOut`, `getLoggingToStdOut`, `setBreadcrumbRecorder`, `getBreadcrumbRecorder`, `setReindexOnDiskResources`, `getReindexOnDiskResources`, `setUseOnDiskCache`, `getUseOnDiskCache` |
| `GestureRecognizerControl` | 3 | `nativeDestroy`, `setDoubleTapGesturesEnabled`, `setAllGesturesEnabled` |
| `LuaAnimationHandler` | 4 | `nativeDestroy`, `shouldAdvanceAnimation`, `onProgress`, `onError` |
| `MREHolder` | 5 | `nativeDestroy`, `create`, `handleOnAppDidEnterForeground`, `handleOnAppDidEnterBackground`, `handleMemoryWarning` |
| `Offline` | 4 | `nativeDestroy`, `loadBundles`, `unloadBundles`, `setNonBundleResourcesAllowed` |
| `OsmFilter` | 2 | `nativeDestroy`, `setShow` |
| `OverriddenFeatureProperties` | 11 | `nativeDestroy`, `create`, `addBooleanProperty`, `addIntegerProperty`, `addDoubleProperty`, `addStringProperty`, `removeProperty`, `getBooleanProperty`, `getIntegerProperty`, `getDoubleProperty`, `getStringProperty` |
| `PrivateApi` | **53** | `nativeDestroy`, `addPerformanceMetricsListener`, `removePerformanceMetricsListener`, `setOfflineMode`, `setPixelRatio`, `haveVisualContentLoadingWork`, `haveGivenUpTileDownloads`, `getGivenUpDownloadsInfo`, `resetTileDownloadHistory`, `resetOnlineAvailabilityData`, `setTileDownloadEventProcessor`, `setDownloadHeightmapsOnly`, `setResourceLoadingFromDiskAllowed`, `startEnteringTestingContext`, `setDiskCacheSize`, `setSymbolVisibilityStateful`, `setSymbolScreenPaddingEnabled`, `setShowSymbolBoundingBoxes`, `setTestAccountForPersonalHeatmap`, `setShaderHotReloadingEnabled`, `startTrackPerfTest`, `getPreparedCommandService`, `setStylingZoomLevelOverride`, `startVideoRenderingFirstPass`, `recordResourcesNeededForSecondVideoRenderingPass`, `startDownloadingResourcesForSecondVideoRenderingPass`, `startVideoRenderingSecondPass`, `finishTwoPassVideoRendering`, `getVisibleAreaBoundsExt`, `saveWindowScreenshot`, `runScreenshotTests`, `runPerformanceTests`, `getPerformanceTestsNames`, `setupDefaultMockTiles`, `startMockTileServer`, `stopMockTileServer`, `updateLodCalculationControlParams`, `setLodSubdivisionThresholdEnabled`, `reloadTerrain`, `startLuaAnimationScript`, `stopLuaAnimationScript`, `setLuaGlobalString`, `setLuaGlobalFloat`, `setLuaGlobalInt`, `setLuaGlobalLine`, `setLuaGlobalImage`, `setLuaGlobalAnimation`, `clearLuaPersistedStore`, `refreshScreenInfo`, `isVsyncEnabled`, `setVsyncEnabled`, `switchToCustomStyle`, `getMemoryStats` |
| `RecordContent` | 6 | `nativeDestroy`, `displayRecordingLine`, `showRecordingLineFinishMarker`, `removeRecordingLine`, `displayLiveSegments`, `removeAllLiveSegments` |
| `RouteContent` | 14 | `nativeDestroy`, `displayRoute`, `displayAnimatedRoute`, `fadeInUnselectedRoutes`, `stopUnselectedRoutesFadeIn`, `addWaypointToRoute`, `selectWaypointOnRoute`, `unselectWaypointOnRoute`, `selectRoute`, `unselectRoute`, `removeRoutes`, `removeAllRoutes`, `displayRouteScrubber`, `removeRouteScrubber` |
| `SegmentsFilter` | 6 | `nativeDestroy`, `show`, `hide`, `getVisibleSegments`, `selectSegment`, `unselectSegment` |
| `StravaPoiFilter` | 8 | `nativeDestroy`, `setShow`, `selectPoi`, `unselectPoi`, `enableStartPoints`, `disableStartPoints`, `enableBikeShares`, `disableBikeShares` |
| `TerrainEngine` | **53** | `nativeDestroy`, `update`, `setPaused`, `receivedMemoryWarning`, `setServerConfig`, `setSurfaceIdentifier`, **`setStravaAuthData`**, **`setStravaSourcesExtraHeaders`**, `setImageryOverlay`, `setMapType`, `setGlobalHeatmapConfig`, `setNightlyHeatmapConfig`, `setWeeklyHeatmapConfig`, `setPersonalHeatmapConfig`, `setIsTerrain3dEnabled`, `addOverlayPolygon`, `removeAllOverlayPolygons`, `setOverlayPolygons`, `getCamera`, `getContent`, `getOffline`, `getUtils`, `getDebugApi`, `getPrivateApi`, `addTileLoadingListener`, `removeTileLoadingListener`, `addOnTapListener`, `removeOnTapListener`, `addOnMouseMoveListener`, `removeOnMouseMoveListener`, `addPostUpdateListener`, `removePostUpdateListener`, `getElevation`, `setUnitSystem`, `getGridUnits`, `addOnGridChangeListener`, `removeOnGridChangeListener`, `didEnterForeground`, `didEnterBackground`, `setRenderingDisabled`, `requestRender`, `setAlwaysRender`, `getFps`, `setEnableScreenSymbols`, `setScreenSymbolRefreshInterval`, `setDesiredLanguage`, `waitToLoadMap`, `setLogLevel`, `getOverriddenFeatureProperties`, `setOverriddenFeatureProperties`, `addViewUpdateListener`, `removeViewUpdateListener`, `setAuthenticationErrorListener` |
| `TestingContextEventListener` | 4 | `nativeDestroy`, `onEntered`, `onErrorEntering`, `onLeft` |
| `TestingContextHandle` | 3 | `nativeDestroy`, `status`, `stopEnteringOrInitiateLeaving` |
| `TestsListener` | 4 | `nativeDestroy`, `onPerformanceTestResult`, `onTestFinished`, `onFinished` |
| `TouchListener` | 6 | `nativeDestroy`, `onSingleTap`, `onDoubleTap`, `onLongPress`, `onTouches`, `onTapAndMove` |
| `TrackPerfTestCompletionListener` | 2 | `nativeDestroy`, `onPerfTestFinished` |
| `Utils` | 16 | `nativeDestroy`, `setUserMarker`, `setUserMarkerAnimation`, `setUserMarkerState`, `setUserMarkerPrecision`, `setUserMarkerDirection`, `removeUserMarker`, `isUserMarkerOnScreen`, `getUserMarkerLocationAngle`, `addCustomMarker2`, `addCustomMarker3`, `updateCustomMarkerPosition`, `removeCustomMarker`, `showTileGrid`, `hideTileGrid`, `setHighlightedTiles` |
| `VisibleAreaSamplesSink` | 2 | `nativeDestroy`, `processVisiblePoint` |
| `WindowLifecycleListener` | 4 | `nativeDestroy`, `onWindowResized`, `onWindowStateChanged`, `onDisplayChanged` |

**Notable:** a full test/QA harness ships inside the release library — `startMockTileServer`, `setupDefaultMockTiles`, `runScreenshotTests`, `runPerformanceTests`, `startEnteringTestingContext`, `setTestAccountForPersonalHeatmap`, `startTrackPerfTest`, `dumpEngineState`. These are reachable only through `PrivateApi`/`DebugApi`, which the production UI does not call, but they are present in the shipped binary.

### 2.5 Network endpoints inside the map engine

Literal URLs and templates compiled into `libFatmapSdk.so`:

```
https://api.mapbox.com/v4
https://tiles.strava.com
https://cdn-1.strava.com
https://www.strava.com
https://content-a.strava.com/identified/globalheat/{activity_type}/grayscale/{z}/{x}/{y}.png?v=19&missing=empty
https://content-a.strava.com/nightheat/{activity_type}/{z}/{x}/{y}
https://content-a.strava.com/weeklyheat/{z}/{x}/{y}?sport_types={activity_type}&format=png&colormap=grayscale&missing=empty
https://{host_name}/tiles/{strava_athlete_id}/grayscale/{z}/{x}/{y}.png?missing=empty
  &filter_type={filter_type}{filter_start}{filter_end}
  &include_everyone={include_everyone}
  &include_followers_only={include_followers_only}
  &include_only_me={include_only_me}
  &respect_privacy_zones={respect_privacy_zones}
  &include_commutes={include_commutes}{maybe_access_token}
{MAPBOX_TILES_SERVER_URL}/mapbox.mapbox-streets-v8/{z}/{x}/{y}.vector.pbf?access_token={MAPBOX_SERVER_SECRET}
{MAPBOX_TILES_SERVER_URL}/mapbox.mapbox-terrain-v2/{z}/{x}/{y}.vector.pbf?access_token={MAPBOX_SERVER_SECRET}
```

The personal-heatmap tile template is the most privacy-relevant string in the application. It encodes, as query parameters: who the heatmap is visible to (`include_everyone`, `include_followers_only`, `include_only_me`), whether privacy zones are honoured (`respect_privacy_zones`), whether commute activities are included (`include_commutes`), a date window (`filter_start`/`filter_end`), a sport filter (`filter_type`), and an optional bearer token.

**Auth handling in the engine:** `Authorization: Bearer %s`, `Authorization: %s4-HMAC-SHA256 Credential=%s/%s, SignedHeaders=%s, Signature=%s` (an AWS-SigV4-style signer is compiled in, used for the Mapbox server secret), plus a full **cURL** HTTP stack (cookie jar, `cookie gen callback failure`, `cookie '%s' for domain '%s' dropped`, `Included max number of cookies (%zu) in request!`).

`setStravaAuthData` and `setStravaSourcesExtraHeaders` are the JNI points through which the app hands Strava credentials and extra HTTP headers to the native tile downloader.

### 2.6 Decompiled samples (Ghidra / C pseudocode)

**`Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setStravaAuthData`** — forwards the Java-supplied auth blob into the C++ engine via a vtable call:

```c
void Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setStravaAuthData(
        undefined4 env, undefined4 cls, int thiz, undefined4 a4,
        undefined4 a5, undefined4 a6, undefined4 a7)
{
  int *vtable_owner = *(int **)(thiz + 8);
  FUN_0044b76c(&local_28, env, a7);              // build a std::string from the Java string
  code *vfn = *(code **)(*vtable_owner + 0x1c);
  (*vfn)(vtable_owner, vfn, a5, a6, &local_28);  // engine->setStravaAuthData(a5, a6, str)
  // ... std::string destructor with the usual Ogre/std::ndk1 shared_ptr teardown
}
```

**`Java_com_fatmap_sdk_api_Camera_00024CppProxy_native_1flyover`** — the 3D fly-through camera. It marshals three optional Java objects (a target, a second target, and a completion listener) into `std::shared_ptr` C++ objects by resolving each through the JNI vtable (`GetObjectClass` / `GetFieldID` / `GetObjectField` equivalents at vtable offsets `0x7c`, `0x60`, `0x194`), then invokes the engine's flyover entry point at vtable offset `0x3c`, and finally converts the returned handle back into a Java object via `FUN_006ee1c8(..., 0x431525)` (a JNI object-construction helper with a method signature ID). The `__shared_weak_count` increment/decrement pairs and `ExclusiveAccess`/`DataMemoryBarrier` sequences throughout are libstdc++'s `std::shared_ptr` thread-safety bookkeeping.

**`Java_com_fatmap_sdk_api_PrivateApi_00024CppProxy_native_1startLuaAnimationScript`** — the Lua bridge entry point; it constructs the script handle and hands it to the engine's animation subsystem.

> **Limit:** the decompiler produced Ghidra C pseudocode with unresolved `FUN_xxxxxxxx` callees (the binary is stripped). The pseudocode is faithful to the control flow and JNI marshalling but **does not** recover original source, parameter names, or C++ types. 26,000 of 27,535 procedures have no name and no type information.

---

## 3. Ghidra analysis of the other large libraries

| Library | Procedures | Strings | Segments |
|---|---:|---:|---:|
| `libmapbox-maps.so` | 22,254 | 12,658 | 30 |
| `librive-android.so` | 10,123 | 8,422 | 30 |
| `libFatmapSdk.so` | 27,535 | 23,462 | 31 |

`librive-android.so` is the Rive C++ runtime (269 JNI exports) — a vector animation engine used for the subscription/upsell screens (`SubscriptionsRiveSuperUserActivity`, `SubPreviewWelcomeSheetFragmentActivity`).

`liblitr-jni.so` is **LinkedIn's LITR** SDK (3 JNI exports: `OboeAudioProcessor_initProcessor`, `processAudioFrame`, `releaseProcessor`). It renders media (video thumbnails/effects) with an Oboe audio path.

`libembrace-native.so` is Embrace's crash-capture NDK — it vendors `libunwindstack-ndk` (`ArmExidx.cpp`, `DwarfMemory.cpp`, `Unwinder.cpp`, `GlobalDebugImpl.h`).

---

## 4. The map asset bundle

`assets/assets.zip` (20.4 MB, 962 entries) is the Fatmap runtime bundle:

- **15 Mapbox Style Spec v8 styles**, in two families:
  - *Strava classic* — `StravaTopo`, `StravaTopoDark`, `StravaTopoWinter`,
    `StravaHybrid`, `StravaSatellite` (409–522 layers each). Their sources are
    `mapbox://` URLs pointing at Mapbox Studio style IDs under the `cerilewis`
    account, plus `mapbox://mapbox.mapbox-streets-v8`.
  - *Strava Planet* — the `StravaPlanet*` family (212–548 layers each). Their
    sources are **MapTiler** (`api.maptiler.com`) plus a **public AWS S3
    bucket**, `strava-map-tiles-planet-tilejson-public.s3.us-east-1.amazonaws.com`.
- 388 generated icons (PNG), each shipped at 1x and 2x — 776 files in total
- 48 of Strava's own shaders (`.fs`/`.vs`) and 14 of OGRE's GLSL files
- 13 heatmap colour ramps, 5 terrain overlay textures, 8 SDFonts, 10 TTFs
- OGRE 3D resources: `Main` + `RTShaderLib`
- SDFonts
- a skybox
- `GlobalAltitudeRange` / `9.gart` — altitude range data

`assets/database/geoid-height-map-v0.db` (106,496 B) is a **SQLite3** database containing the **EGM96 geoid undulation model** — a 512×512 grid of protobuf-framed JPEG tiles used to convert GPS ellipsoidal height to mean-sea-level height, i.e. the terrain elevation the 3D map renders.
