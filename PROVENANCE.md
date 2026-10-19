# Provenance

This document states exactly where every part of this repository came from, and what is deliberately **not** here.

Read it before contributing.

---

## 1. The source artifact

The analysis in [`docs/`](docs/) was produced by static analysis of one file:

| | |
|---|---|
| File | `com.strava.apk` — an **XAPK v2** container |
| SHA-256 | `6325cde160519180e1f5c3200b53019dd57321a7a6abe7990c91472544110aed` |
| Contents | Strava Android **v456.10** (versionCode 12397026), package `com.strava` |
| Base APK SHA-256 | `a273c91d9aba0471e4c145d5b795609964f53bb787e77462fd12490b52db3621` |
| Provenance | Google Play source stamp verified (cert fingerprint matches the APK's own `stamp-cert-sha256`) |

**The artifact itself is not in this repository, and must not be added.** It is Strava's copyrighted work and it embeds third-party licensed components (Mapbox Maps v11 is proprietary; the Fatmap SDK is proprietary; Rive, Stream, Sentry, Embrace, Facebook and Zendesk SDKs carry their own licences). Redistributing it would be copyright infringement regardless of the licence applied to this repository.

You can obtain your own copy from the Google Play Store. Everything in `tools/` runs against it.

---

## 2. What is in this repository, and where it came from

### 2.1 `docs/` — written by me

Eleven documents, all written from scratch for this project. They contain my analysis, my prose, and factual observations about the artifact.

Two documents contain **short excerpts of decompiler output** (`docs/07-native-code.md` quotes the pseudocode of two JNI functions; `docs/06-code-architecture.md` quotes decompiled Kotlin method bodies). These are research citations — included to substantiate specific factual claims about control flow and behaviour, not as source material. They are not enough to reconstruct Strava's implementation, and they must not be transliterated into the app.

### 2.2 `tools/` — written by me

Three parsers, original work, Python standard library only:

| File | Lines | What it does |
|---|---:|---|
| `axml.py` | ~150 | Decodes Android binary XML (AXML) to text |
| `arsc.py` | ~130 | Parses `resources.arsc` into a resource-id → (type, name, value) map |
| `derefx.py` | ~110 | Traces `const-string` references in DEX bytecode to find the class and method that uses a given string |

### 2.3 `data/` and `spec/` — derived factual data

Machine-readable interface descriptions extracted from the artifact by the tools and by the REA analysis toolchain. These consist of **names, shapes, counts, and addresses** — not code:
| File | Nature |
|---|---|
| `data/classes.txt`, `data/classes.json` | Class *names* only |
| `data/class-roots.txt` | Class *counts* per package root |
| `data/fatmap-procedures.txt` | Native procedure *names and addresses* |
| `data/fatmap-cpp-classes.txt` | C++ class *names* recovered from RTTI |
| `data/hosts.txt` | Hostnames |
| `data/api-paths.txt` | URL path patterns |
| `data/local-databases.txt` | Database *names* |
| `spec/manifest-summary.json` | Component names, exported flags, intent actions, authorities, permissions |
| `spec/fatmap-jni.json` | Native API *signatures* (class, method, JNI symbol, address) |

These are the same category as a published API reference or a `.h` header file: they describe an interface so others can implement against it. They contain no implementation.

`spec/api-contract.md`, `spec/data-model.md` and `spec/fatmap-native.md` are written by me from the analysis.

### 2.4 `app/` — written by me

The clean-room implementation scaffold. Original Kotlin, written against the spec. Currently scaffolding.

### 2.5 Credential redaction policy

`docs/01-network-surface.md` and `docs/08-security-privacy.md` originally published Strava's embedded credentials **literally**: Google Maps and Firebase API keys, four Mapbox public tokens, the Sentry DSN, the Facebook client token, the Branch.io live key, and the Zendesk application and OAuth client IDs.

Those values were **redacted to placeholders before publication.** The reason is not legal but practical: republishing live credentials for a commercial product in a public repository creates an open abuse channel against Strava's and its vendors' quotas — Mapbox's, Google's, Sentry's, Branch's and Zendesk's. GitHub's push-protection rules independently flag them.

What is preserved, and is the analytically useful part:

- the **existence** of each credential,
- its **type** and **format**,
- its **exact location** in the artifact (manifest key, `resources.arsc` entry, or DEX),
- **what capability it grants**,
- and whether it is a genuine secret or a public identifier.

The literals are recoverable by anyone with their own copy of the APK. `docs/00-method-and-evidence.md` §4 documents how.

The same policy applies to anything added later: **document credentials; do not republish them.**

---

## 3. What is deliberately excluded

| Excluded | Where it still lives | Why excluded |
|---|---|---|
| The XAPK / APK itself | Your own Play Store copy | Copyrighted, and embeds proprietary third-party components |
| Extracted `classes*.dex` | Regenerate with `tools/` | Strava's compiled implementation |
| Extracted `lib*.so` | Regenerate from the APK | Strava's and Mapbox's compiled implementation |
| `resources.arsc`, `res/`, `assets/` | Regenerate with `tools/arsc.py` | Strava's and its vendors' creative + compiled assets |
| Decompiled Kotlin / smali | Regenerate with JADX | Strava's implementation — **publishing this is infringement** |
| Ghidra pseudocode for the Fatmap SDK | Regenerate with the REA toolchain | Strava's implementation |
| The REA evidence bundles (`evidence-*.json`, 13 MB) | Regenerate with the REA toolchain | Contain decompiled implementation |
| The verbatim decoded `AndroidManifest.xml` (194 KB) | Regenerate with `tools/axml.py` | Verbatim Strava file. `spec/manifest-summary.json` is the factual equivalent, in my own format |
| `docs/02-dependencies.md` embedded licence texts | — | Quoted from `META-INF/` in the artifact, for attribution. Standard practice |

**If you add any of the excluded material to this repository, the project stops being clean-room.** That is a licensing and legal problem for the contributors and the project, not a style preference. Please don't.

---

## 4. Reproducing the excluded material locally

Everything above can be regenerated on your own machine from your own copy of the APK.

```bash
# 1. Unpack
python3 -c "import zipfile; zipfile.ZipFile('com.strava.apk').extractall('unpacked/')"

# 2. Decode the manifest
python3 tools/axml.py unpacked/com.strava.apk res/xml/network_security_config.xml

# 3. Build the resource table
python3 tools/arsc.py unpacked/com.strava.apk arsc.json

# 4. Find the class/method that references a string
python3 tools/derefx.py unpacked/dex/classes4.dex 52144

# 5. Native analysis — requires the REA MCP toolchain with Ghidra
#    (open_binary -> list_procedures -> batch_decompile -> export_evidence_bundle)
```

Decompiling the Kotlin/smali additionally requires JADX. That output is for **your own reading**; it is not for this repository.

---

## 5. Third-party components

The analysis catalogues the components Strava bundles. None of them are included here. Any that a contributor wants to *use* in the reimplementation must be added as a normal dependency, with its own licence and attribution, via `NOTICE.md`.

Components that are **proprietary and cannot be redistributed** — and are therefore not options for the reimplementation:

- **Strava's Fatmap SDK** (`libFatmapSdk.so`) — the 3D map engine
- **Mapbox Maps SDK v11** (`libmapbox-maps.so`, `libmapbox-common.so`) — commercial licence
- **Facebook SDK**, **Embrace SDK**, **Branch SDK**, **Garmin FIT SDK** — proprietary

Components that *are* permissively licensed and are reasonable candidates for the reimplementation include OGRE (MIT), Rive (MIT), LITR (Apache-2.0), Sentry (MIT), OkHttp / Retrofit / Moshi (Apache-2.0), Room / WorkManager / Compose (Apache-2.0), Joda-Time (Apache-2.0), Tink (Apache-2.0).

> **Note on the Fatmap engine.** The reimplementation needs a map renderer. `spec/fatmap-native.md` documents the 312-function native API so that a *clean-room* native renderer can be built to the same contract — but the shipped binary itself may not be redistributed, and its functionality (Strava's POI set, Strava's server-computed heatmaps, 3D terrain) depends on Strava server data that is not available.

---

## 6. Licence of the contents here

| Part | Licence | Copyright |
|---|---|---|
| `tools/`, `app/`, `spec/*.md` | Apache-2.0 | the contributors to this repository |
| `docs/` | Apache-2.0 | the contributors to this repository |
| `data/`, `spec/*.json` | Apache-2.0 | factual interface data, derived by analysis of a third-party artifact |

Copyright in the analysed Strava application remains with Strava, Inc. and its licensors. Nothing in this repository transfers, licenses, or restricts any of it.
