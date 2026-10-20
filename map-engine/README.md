# The map engine

A clean-room implementation of the Fatmap-compatible map API.

**The API surface is 1:1 with Strava's proprietary `libFatmapSdk.so`:** all 312
JNI entry points, with identical symbol names, so the shared object is a drop-in
replacement at the JNI boundary.

**234 of the 312 methods are implemented** (75%). The rest are the original's
test harness and server-dependent features; they keep the generated default
body. See [COVERAGE.md](COVERAGE.md).

---

## Layout

```
map-engine/
├── CMakeLists.txt
├── generated/            <- produced by tools/fatmap_codegen.py; do not edit
│   ├── fatmap_types.h        value structs and enums
│   ├── fatmap_api.h          one C++ interface per proxy class
│   ├── fatmap_proxy.h        implementation aliases
│   ├── fatmap_jni.h/.cpp     the 312 JNI entry points
│   └── fatmap_jni_values.*   marshalling for the value types
├── src/
│   ├── fatmap_engine.h    the engine and the 30 proxy implementations
│   ├── fatmap_jni_types.h JNI helpers
│   ├── engine.cpp         engine state, camera maths, tile URL templating
│   ├── engine_api.cpp     the proxy implementations
│   ├── geo.h / geo.cpp    Web Mercator projection and geodesy
│   └── sha256.h / .cpp    offline bundle verification
└── tests/test_engine.cpp  host tests
```

## Build

Needs a C++17 compiler, CMake 3.16+, and a JDK (for `jni.h`).

```bash
cmake -S map-engine -B map-engine/build -G Ninja -DCMAKE_BUILD_TYPE=Debug
cmake --build map-engine/build
./map-engine/build/fatmap_tests
```

Or with CTest:

```bash
cd map-engine/build && ctest --output-on-failure
```

The build produces:

- `libfatmap_core.a` — the engine, with no JNI dependency at run time
- `libfatmap_jni.so` — the shared object the Android app loads
- `fatmap_tests` — the host test binary

## Regenerating the API layer

The API layer is generated from the extracted contract, so it can never drift
from it:

```bash
python3 tools/fatmap_codegen.py --clean
```

This rewrites:

- `app/src/main/kotlin/com/fatmap/sdk/api/*.kt` — 130 Kotlin classes
- `map-engine/generated/*` — the C++ types, interfaces and JNI entry points
- `map-engine/COVERAGE.md` — the implementation coverage report

## How it fits together

```
Kotlin app
   │  com.fatmap.sdk.api.TerrainEngine.setServerConfig(cfg, true)
   ▼
TerrainEngine$CppProxy            holds a `long` handle
   │  external fun native_setServerConfig(cfg: ServerConfig, flag: Boolean)
   ▼
libfatmap_jni.so
   │  Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setServerConfig
   ▼
TerrainEngineImpl                 overrides the generated interface
   ▼
Engine                            shared state
```

The generated `fatmap_api.h` gives every method a default body, so the engine
only has to override what it implements.

## What is actually implemented

| Area | What works |
|---|---|
| **Projection** | Web Mercator, ground resolution, haversine distance, bearings, easing |
| **Camera** | `flyTo` with easing, `flyToBounds`, `flyToTarget`, `translateTo`, follow mode, first-person view, look-at point, screen projection, visible-area bounds, meters-per-pixel, fly-through path with arc-length parameterisation |
| **Content** | Custom lines and gradient lines with handles, dropped pins, circle markers, activity and route overlays, recording line |
| **Configuration** | All 19 accessors, camera target, client name, log level, on-disk cache flags, `loadFromFile` |
| **Tile source** | The 16 URL templates, `{z}/{x}/{y}` expansion, secret redaction, extra headers, bearer auth |
| **Offline bundles** | Load/unload by identifier, SHA-256 verification against the expected digest |
| **Feature properties** | Typed overrides (boolean, integer, double, string) with add/get/remove |
| **Debug** | FPS overlay state, a real engine-state dump |
| **Private API** | Offline mode, pixel ratio, disk cache size, Lua globals map, styling zoom override, vsync, memory stats |

## What is not implemented, and why

The original engine's test harness and its Strava-server-dependent features:

- the performance-test and screenshot-test runners
- the mock tile server
- the two-pass video rendering pipeline
- the testing contexts
- the Lua interpreter (the globals map is kept; the VM is not)

## Licence

Apache-2.0, like the rest of this repository. See [../LICENSE](../LICENSE).

The engine implements a published interface. It contains no Strava code.
