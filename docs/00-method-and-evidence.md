# Method, Evidence & Limitations

---

## 1. What was done

Static analysis only. **The APK was never executed, no network request was made, no server was contacted, and no device was used.** Every statement in this analysis is derived from bytes inside the supplied file.

### Tooling

| Tool | Used for |
|---|---|
| **REA MCP** — Android provider (`jadx-headless-mcp` 0.7.1) | Manifest decode, class inventory, per-class and per-method decompilation |
| **REA MCP** — native provider (**Ghidra 12.1.4**, headless) | ELF import, auto-analysis, procedure inventory, C pseudocode decompilation of `libFatmapSdk.so`, `libmapbox-maps.so`, `librive-android.so` |
| **REA evidence ledger** | Every result retained as an Evidence record; exported to `evidence-android.json` and `evidence-native.json` |
| `readelf` / `objdump` (binutils) | ELF headers, dynamic dependencies, symbol counts, hardening flags |
| `strings` + `c++filt` | Native string recovery, Itanium C++ name demangling |
| `python3` + `zipfile` | Container traversal, DEX header parsing, ARSC and AXML parsing |

### Two parsers written for this analysis

Both are included in this directory and are re-runnable.

**`axml.py`** — decodes Android binary XML (AXML) to readable text. Used to recover all 44 `res/xml/*.xml` files (network security config, backup scheme, locale config, split declarations, and the 22 `settings_*.xml` preference trees) plus `res/xml/file_provider_paths.xml` and `sharing_file_provider_paths.xml`.

```
python3 axml.py com.strava.apk res/xml/network_security_config.xml res/xml/backupscheme.xml
```

**`arsc.py`** — parses `resources.arsc` into a resource-id → (type, name, value) map. Recovered **24,895 resource entries** across 21 types (7,533 strings, 3,824 ids, 2,861 attrs, 2,620 drawables, 2,232 layouts, 1,642 colors, 1,624 styles, 1,570 dimens, 515 raws, 116 plurals, 90 integers, 71 anims, 51 menus, 40 arrays, 32 animators, 19 mipmaps, 18 interpolators, 16 bools, 11 xmls, 9 fonts, 1 navigation) and the 12,492-entry global string pool.

```
python3 arsc.py com.strava.apk arsc.json
```

**`derefx.py`** — traces `const-string` / `const-string/jumbo` references in DEX bytecode to find **which class and method** reference a given string-pool entry. This is what located Strava's single certificate pin (`sha256/q61HEYJJK/e1i0DQnyP6R6tGOrRC5MbbsTcI9ev6cZ4=`) inside the obfuscated class `LFd/d2$a;`, and from whose string table the pinned hosts `cdn-1.strava.com` and `graphql.strava.com` were read.

```
python3 derefx.py classes4.dex 52144
# -> LFd/d2$a;  ::  LFd/d2$a;->a  <- const-string "sha256/q61HEYJJK/e1i0DQnyP6R6tGOrRC5MbbsTcI9ev6cZ4="
```

> **Known limitation of `arsc.py`:** the *names* and *types* of all 24,895 resources are correct. The *values* of `string`-type resources are misaligned by one entry (the `string` type stores pool offsets in a layout this parser does not fully model), so individual string values should not be relied on. The `axml.py` output was cross-checked against `res/xml/network_security_config.xml` by hand — the `<domain>` value `10.0.2.2` was established from the string pool independently (it is the only string in the pool not used as an element or attribute name).

---

## 2. Evidence

| Evidence ID | Operation | Content |
|---|---|---|
| `ev_b6b62188bb6358c1d1f61362fa0902e2769c149e7ef6d2ea07299cc128d96371` | `inspect_android_package` | The base APK record: package identity, 43 permissions, manifest, 43,827 classes, 6,687 resources. **Parent for the manifest view.** |
| `ev_4c66a18595af127905ca69e0cfa510fe2542e1a882e1fab7b31d4915a9e64cac` | `list_procedures` | 27,535 procedures in `libFatmapSdk.so` (10.5 MB — exceeded the MCP transport budget, so it was exported rather than inlined) |
| `ev_d18396b791ea78c9d4461e64e5ec52263bd466fa717eed2d938d1c6b4273f802` | `inspect_analysis_view` | Summary projection of the Android package record |

Both evidence bundles are exported in this directory:
- `evidence-android.json` — 630 KB, 3 records, contains the full 194,810-character manifest
- `evidence-native.json` — 12.7 MB, 11 records, contains the full 27,535-procedure list

`procedures.json` and `classes_all.json` are flat extractions of the two largest tables (procedure addresses + names; 43,827 class names). `fatmap_demangled.txt` holds the 1,010 demangled Itanium names recovered from `libFatmapSdk.so`.

---

## 3. Observation, inference, and the boundary between them

Everything asserted in this analysis is one of three things:

**Observed** — read directly out of the file: file sizes, SHA-256 digests, the manifest XML, class and method signatures, decompiled method bodies, Ghidra pseudocode, string literals, ELF headers, the contents of the APK Signing Block, the decoded X.509 certificate.

**Derived** — computed from observed data: the 72,470 class-definition count (DEX header field), the 27,535 procedure count (Ghidra), the 24,895 resource count (ARSC parse), the 312 JNI export list (symbol table), the 263 Fatmap class names (RTTI demangling), the 130 exported-component list (manifest).

**Inferred** — a judgement that goes beyond the bytes:
- The build date (2026-03-23) comes from the Sentry Gradle plugin's recorded date, not a dedicated build stamp. It is an **upper bound**.
- "Strava's own code" vs "third-party code" is inferred from package names, since R8 renamed 63% of class definitions into 1–2 letter packages. Library presence was therefore confirmed independently through `META-INF/*.version` coordinates and `assets/sentry-external-modules.txt`.
- The `https://www.strava.com/api/v3/` base URL is assembled at runtime from separate `api` and `v3` constants. High confidence, not proven.
- The `strava://` deep-link table in §3.1 of document 08 is assembled from the manifest's `<data>` rules; the app's runtime route table was not dumped.

**Unknown / not established:**

- The **v2/v3 signer identity** was not cryptographically verified. The Android provider reports `signature_verification: "not_performed"`. The **Google Play source stamp was** verified — its certificate fingerprint matches the APK's own `stamp-cert-sha256` byte for byte.
- **Runtime behaviour** of the app is entirely unknown. Nothing here establishes what the app does when it runs, only what is compiled into it.
- **Certificate pinning** was not found. A pinning configuration assembled purely at runtime would not be visible statically.
- **`assetlinks.json`** was not fetched, so it is unknown whether Strava's HTTPS links are verified App Links or interceptable.
- **Server-side behaviour** is unknown, including whether heatmap privacy filters are enforced correctly.
- The **Snowplow collector URL**, **Iterable API key** and **MapTiler key** are not embedded in the binary and could not be recovered.
- **FORTIFY_SOURCE** hardening was not detected in the native libraries.
- The 26,000 unnamed procedures in `libFatmapSdk.so` have no recovered type information — the library is stripped and Ghidra could not reconstruct C++ class types from the remaining RTTI alone.
- `libmapbox-maps.so` and `librive-android.so` were opened and inventoried (procedure and string counts) but **not** decompiled function-by-function; the decompilation effort was spent on Strava's proprietary engine.

---

## 4. Reproducing this analysis

```bash
# 1. Unpack the XAPK
python3 -c "import zipfile; zipfile.ZipFile('com.strava.apk').extractall('unpacked/')"

# 2. Decode every binary-XML config
python3 axml.py unpacked/com.strava.apk res/xml/network_security_config.xml res/xml/backupscheme.xml ...

# 3. Build the resource table
python3 arsc.py unpacked/com.strava.apk arsc.json

# 4. Read the APK Signing Block and extract the source-stamp certificate
python3 - <<'EOF'
import struct
d=open('unpacked/com.strava.apk','rb').read()
i=d.rfind(b'PK\x05\x06'); cd=struct.unpack('<I', d[i+16:i+20])[0]
sz=struct.unpack('<Q', d[cd-24:cd-16])[0]; p=cd-8-sz+8
while p < cd-24:
    ln=struct.unpack('<Q', d[p:p+8])[0]
    bid=struct.unpack('<I', d[p+8:p+12])[0]
    print(hex(bid), ln)
    p += 8+ln
EOF

# 5. Native hardening survey
for f in unpacked/native/*.so; do readelf -d "$f" | grep -E 'NEEDED|SONAME'; done
```

For the Ghidra-based work, REA performs the analysis and retains the result in its evidence ledger; `export_evidence_bundle` writes the complete record to disk, which is how the 27,535-procedure list and the decompiled pseudocode were obtained without exceeding the MCP transport budget.
