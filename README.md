# OpenStride

**A clean-room, open-source reimplementation of the Strava Android app — plus the static-analysis toolkit and interface specification that made it possible.**

> **This repository contains no Strava code and no Strava binaries.**
> Everything here was written from scratch, or is factual interface data derived by analysing an artifact you supply yourself. See [PROVENANCE.md](PROVENANCE.md) for the exact provenance of every file.

---

## Why this exists

Strava is a closed-source product with a large, capable, and genuinely useful feature set: GPS recording, 3D terrain maps, route planning, segment efforts, training load, power curves, and a social graph. None of it is available under a free licence, and its data model is opaque.

Rather than redistribute someone else's work, this project does two things:

1. **Documents the interface.** A complete, evidence-backed static analysis of Strava Android v456.10 — 10 documents covering the container format, the manifest, 72,470 class definitions, the native engine, the network surface, and the privacy posture. This is the part anyone can build against.
2. **Rebuilds the app.** A from-scratch implementation whose architecture is informed by that analysis, and whose data lives on your device.

The two halves are separable: the analysis is useful even if you never build the app; the app is useful even if you never read the analysis.

---

## The clean-room rule

This project operates under a **strict clean-room discipline**. It is the single most important rule in this repository.

> **Contributors must not copy, transliterate, or decompile Strava's implementation.**
> You may use the *interface specification* in [`spec/`](spec/) — endpoint shapes, field names, data model, native API signatures. You may **not** use Strava's *implementation* — its Kotlin source, its smali, its native code, its resources, its strings, its layout files.

Why this matters, concretely:

- Strava's code is copyrighted. Publishing translated copies of it is copyright infringement, regardless of intent.
- The upstream project has a history of taking legal action against people who reimplemented or repackaged its app.
- A clean-room implementation is *actually* better engineering: it forces you to design the data model properly rather than reproduce someone else's workarounds.

**How to stay clean:** work from `spec/` and this README. Do not open a decompiler while implementing. If you need a fact that isn't in the spec, add it to the spec — derived by analysis, documented with its evidence, written in your own words.

`docs/07-native-code.md` quotes a small number of decompiler outputs to substantiate specific factual claims. Those are research citations, not source material to copy.

---

## Repository layout

```
openstride/
├── docs/            10-document static analysis of Strava Android v456.10
├── tools/           parsers written for this project (AXML, ARSC, DEX refs)
├── data/            derived interface facts: classes, hosts, paths, symbols
├── spec/            machine-readable interface contract for reimplementation
├── map-engine/      the clean-room map engine (C++), 1:1 with the Fatmap API
├── app/             the Android application module (Kotlin)
├── settings.gradle.kts, build.gradle.kts, gradle/   Gradle root
└── .github/workflows/ci.yml                         build + lint + test on every push
```

### [`docs/`](docs/) — the analysis

| File | Subject |
|---|---|
| [00-method-and-evidence.md](docs/00-method-and-evidence.md) | Tooling, evidence model, observation vs. inference, what was *not* established |
| [01-network-surface.md](docs/01-network-surface.md) | 58 hosts, 182 API paths, embedded keys, per-recipient data flows |
| [02-dependencies.md](docs/02-dependencies.md) | 143 versioned coordinates, 471 modules, licences, build metadata |
| [03-resources-assets.md](docs/03-resources-assets.md) | Resource census, assets, locales, icons, layouts, split mechanism |
| [04-container-signing-identity.md](docs/04-container-signing-identity.md) | XAPK structure, APK Signing Block, source-stamp verification, build identity |
| [05-manifest-components.md](docs/05-manifest-components.md) | All 336 activities, 23 services, 28 receivers, 16 providers, 43 permissions |
| [06-code-architecture.md](docs/06-code-architecture.md) | DEX census, package map, decompiled key classes, debug surfaces |
| [07-native-code.md](docs/07-native-code.md) | 12 native libraries, Ghidra analysis, the 312-function Fatmap JNI surface |
| [08-security-privacy.md](docs/08-security-privacy.md) | Transport security, 130 exported components, keys, permissions, data flow |
| [09-NON-TECHNICAL-GUIDE.md](docs/09-NON-TECHNICAL-GUIDE.md) | **Plain-English guide — start here if you're new** |
| [10-local-only-feasibility.md](docs/10-local-only-feasibility.md) | Running with no backend: what works, what can't, and how |
| [11-fatmap-engine-deep-dive.md](docs/11-fatmap-engine-deep-dive.md) | **Inside Strava's 3D map engine — architecture, the 312-method API, and the clean-room rebuild** |

### [`tools/`](tools/) — the parsers

Three purpose-built parsers, no dependencies beyond the Python standard library:

| Tool | Purpose |
|---|---|
| `axml.py` | Android binary XML → readable text (manifests, `res/xml` configs) |
| `arsc.py` | `resources.arsc` → resource-id → (type, name, value) map |
| `derefx.py` | DEX `const-string` reference tracer — finds which class/method uses a string |

They run against **your own copy** of any APK. See [tools/README.md](tools/README.md).

### [`data/`](data/) and [`spec/`](spec/) — the interface

Facts extracted from the artifact, in machine-readable form. These are the inputs to a reimplementation:

| File | Contents |
|---|---|
| `data/classes.txt` | 43,827 distinct class names |
| `data/class-roots.txt` | class counts per top-level package |
| `data/fatmap-procedures.txt` | 27,535 native procedure addresses and names |
| `data/fatmap-cpp-classes.txt` | 263 recovered C++ class names |
| `data/hosts.txt` | every network host the app references |
| `data/api-paths.txt` | every API path the app references |
| `data/local-databases.txt` | the app's on-device database names |
| `spec/manifest-summary.json` | package identity, permissions, every component, machine-readable |
| `spec/fatmap-jni.json` | the complete Fatmap native API, by class and method |
| `spec/fatmap-java-api.json` | the complete public Java API: 130 classes, 823 methods |
| `spec/fatmap-native-api.json` | the native API: 30 proxy classes, 312 methods with exact JNI descriptors |
| `spec/fatmap-styles.json` | the 15 bundled map styles: layers, sources, fonts, sprites |
| `data/fatmap-assets.txt` | every entry in the Fatmap asset bundle, grouped |
| `spec/api-contract.md` | what a local server must implement |
| `spec/data-model.md` | the on-device data model |
| `spec/fatmap-native.md` | how to use the native engine without its proprietary bits |

### [`map-engine/`](map-engine/) — the map engine

A clean-room C++ implementation of the Fatmap map API.

**The API surface is 1:1 with Strava's proprietary `libFatmapSdk.so`** — all 312 JNI entry points, with identical symbol names, so the shared object is a drop-in replacement at the JNI boundary. **234 of the 312 methods are implemented**; the rest are the original's test harness and server-dependent features.

The interface was extracted from the artefact with `tools/dex_native_api.py` (a from-scratch DEX reader) and is published as machine-readable JSON, so the whole API layer is *generated* rather than hand-written — see [map-engine/README.md](map-engine/README.md).

```bash
cmake -S map-engine -B map-engine/build -G Ninja && cmake --build map-engine/build
./map-engine/build/fatmap_tests
```

### [`app/`](app/) — the implementation

Kotlin, targeting Android 8.0+ (minSdk 26). A scaffold, not a finished app — see [app/README.md](app/README.md) for the roadmap and current status.

**Status:** the build compiles for `arm64-v8a` and `armeabi-v7a`, the unit tests pass, and CI runs it on every push. The UI does not exist yet.

The app carries the **generated 1:1 Fatmap API** (`com.fatmap.sdk.api`, 130 classes) and loads the native engine from it — `libfatmap_jni.so`, 312 JNI entry points, the same symbol names as Strava's own library. `MapEngineLoader` loads it; `EngineSmokeTest` exercises the whole native boundary on a device.

The Gradle root is the repository root; `app/` is the single application module (`dev.openstride`).

---

## Quick start

### Read the analysis

```bash
git clone https://github.com/Kamalakanta01/openstride.git
cd openstride
less docs/09-NON-TECHNICAL-GUIDE.md      # plain-English overview
less docs/10-local-only-feasibility.md    # running without a backend
```

### Reproduce the analysis on your own copy of an APK

```bash
# Decode the manifest and every res/xml config
python3 tools/axml.py com.strava.apk res/xml/network_security_config.xml res/xml/backupscheme.xml

# Build the resource table
python3 tools/arsc.py com.strava.apk arsc.json

# Find which class/method references a given string
python3 tools/derefx.py classes4.dex 52144
```

You need the APK yourself. This project deliberately does not host it.

### Build the app

Requires **JDK 17+** (21 recommended) and the **Android SDK** (compileSdk 35, build-tools 35). Point the build at your SDK with a `local.properties` in the repository root:

```properties
sdk.dir=/path/to/Android/Sdk
```

Then:

```bash
./gradlew :app:assembleDebug     # APK -> app/build/outputs/apk/debug/
./gradlew ktlintCheck             # style
./gradlew test                    # unit tests
./gradlew :app:connectedDebugAndroidTest   # on-device engine tests
```

The Gradle wrapper is committed, so no local Gradle install is needed.

CI does exactly this on every push, and uploads the debug APK as a build artifact.

### Verifying the API is 1:1 with the original

The map engine's Java surface is generated from a contract extracted from the
original library. `tools/verify_api_parity.py` checks that the generated
layer still matches, in six independent ways:

| Check | What it proves |
|---|---|
| 1. JNI symbol parity | Our `.so` exports exactly the original's 312 symbols |
| 2. DEX declarations resolve | Every native the contract declares exists in our `.so` |
| 3. Public Java API parity | The compiled Kotlin exposes every public method |
| 4. Implementation coverage | How much of the engine is actually implemented |
| 5. Native signatures | The Kotlin `external` declarations match the contract's arity |
| 6. Marshalling | Every getter and constructor the C++ calls exists on our compiled classes |

Checks 5 and 6 exist because the JVM links a native call by name **and**
signature: a parameter count or descriptor mismatch passes every name-based
check and then crashes on a device, where the C++ reads a garbage argument.

```bash
python3 tools/verify_api_parity.py
```

---

## Project status

| Area | Status |
|---|---|
| Static analysis | ✅ Complete for the shipped artifact; 10 documents |
| Interface specification | ✅ Machine-readable for manifest, native API, hosts, paths, data model |
| Local data model | 🟡 Scaffolded (Room entities + DAOs) |
| Local API server | 🟡 Contract defined; implementation stubbed |
| Recording service | 🟡 Foreground-service skeleton |
| Map engine | 🟡 API 1:1 (312/312 symbols); 234/312 methods implemented — see `map-engine/` |
| Social features | ⬜ Not started — see the scope note below |
| Build | ✅ Compiles for arm64-v8a and armeabi-v7a; unit tests pass; CI green |
| Map engine build | ✅ Compiles; 45 host checks pass; ships in the APK with all 312 symbols |
| On-device tests | ✅ 7 instrumented tests pass on a physical device (Galaxy A30F) |

### Scope note

This project targets the **solo-athlete** half of the app: recording, maps, routes, segments, training load, insights, power curves, gear, goals, photos, and privacy controls.

Features that inherently require a shared server — a global feed, clubs, challenges, competitions, kudos, comments, global leaderboards, and server-computed heatmaps — are **out of scope**, because a single-user local app has no other users to show you. Where the original app would call a server, this app computes locally or does not offer the feature. See [docs/10-local-only-feasibility.md](docs/10-local-only-feasibility.md) for the full boundary.

### Design decisions

These were settled before the first line of app code, and they constrain everything after them.

| Decision | Choice | Consequence |
|---|---|---|
| **Data source** | Starts empty | No import of existing history. The app is useful only from the first new recording onward. A GPX/FIT importer is a later, self-contained addition — the seam is left in `spec/data-model.md`. |
| **Interface** | Deliberately Strava-like — same information architecture, same screens, same interactions | Familiar to anyone who has used a fitness app. We do **not** copy Strava's icons, illustrations, logo, or written copy; those are their assets. Structure and behaviour are not theirs to own. |
| **Maps** | Undecided, pending a decision on the native engine | See `spec/fatmap-native.md`. The `MapEngine` facade exists so either answer works without rework. |
| **Backend** | None, ever | No account, no sync, no telemetry, no analytics, no crash reporting. The local database is the source of truth. |
| **Privacy enforcement** | Local, at the point geometry becomes pixels or bytes | The original enforces privacy zones server-side, which a modified client can ask to widen. Here it cannot be bypassed. |
| **Naming** | `OpenStride` / `dev.openstride` | Strava's trademarks are not used as the project's identity. |

---

## Legal

- This project is licensed under **Apache-2.0** — see [LICENSE](LICENSE).
- **Strava, the Strava logo, and Strava's product names are trademarks of Strava, Inc.** This project is not affiliated with, endorsed by, or sponsored by Strava, Inc. It exists to study an interface and to offer an independent alternative.
- **No Strava source code, binaries, or assets are included.** If you add any, the repository is no longer clean-room and you have created a legal problem for yourself and for the project. See [PROVENANCE.md](PROVENANCE.md).
- Third-party components referenced by the analysis remain under their own licences. See [NOTICE.md](NOTICE.md).
- The `spec/` and `data/` directories contain *factual interface descriptions* — names, endpoint shapes, field names — derived by analysis. They are intended to be independently reimplemented, and are not a substitute for the original work.

## Contributing

Read [CONTRIBUTING.md](CONTRIBUTING.md) — in particular the clean-room rule, which is not optional — and [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).
