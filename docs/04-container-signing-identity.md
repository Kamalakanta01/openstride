# Strava Android — Container, Signing & Identity

**Subject:** `com.strava.apk` (XAPK v2 container) · **Package:** `com.strava` · **Version:** 456.10 (versionCode 12397026)

---

## 1. The outer file is not an APK

`com.strava.apk` (120,116,179 bytes) is **not** an installable Android package. It is an **XAPK v2 container** — a ZIP holding one base APK plus three *configuration splits* produced by Google's `bundletool`.

The outer ZIP has only **6 entries**, every one stored uncompressed (`method=0`):

| Entry | Size (bytes) | SHA-256 |
|---|---:|---|
| `com.strava.apk` (base) | 87,210,060 | `a273c91d9aba0471e4c145d5b795609964f53bb787e77462fd12490b52db3621` |
| `config.armeabi_v7a.apk` | 26,527,030 | `cc3bf0476b91777e58fb398fe3e6cf3833cb4f9137724a59cb0b2bd27172f9ce` |
| `config.mdpi.apk` | 6,167,141 | `e9f6155566a179342232902bedbbaf7610d0f89fd6b3d6cdb90b490143d6f479` |
| `config.en.apk` | 164,249 | `99c22eaa0f2cf45537ed2a2ef9746522190bb49ddbe772c301c1d7e56eb5f94c` |
| `icon.png` | 44,686 | `5eca650ebfcece7b35f881f1fa5531a6ea274fbc6b84584c6b7560121b19198c` |
| `manifest.json` | 2,269 | — |

Outer container SHA-256: `6325cde160519180e1f5c3200b53019dd57321a7a6abe7990c91472544110aed`

### `manifest.json` (verbatim, key fields)

```json
{"xapk_version":2,"package_name":"com.strava","name":"Strava",
 "version_code":"12397026","version_name":"456.10",
 "min_sdk_version":"26","target_sdk_version":"35",
 "split_configs":["config.armeabi_v7a","config.en","config.mdpi"],
 "total_size":120068480,"icon":"icon.png",
 "split_apks":[{"file":"com.strava.apk","id":"base"},
               {"file":"config.armeabi_v7a.apk","id":"config.armeabi_v7a"},
               {"file":"config.en.apk","id":"config.en"},
               {"file":"config.mdpi.apk","id":"config.mdpi"}]}
```

### What each split is

| Split | Contents | Role |
|---|---|---|
| `base` | 6,687 entries — manifest, 7 DEX, 2.68 MB `resources.arsc`, 5,773 `res/` entries, 12 `assets/`, 237 `META-INF/` | The app. `android:requiredSplitTypes="base__abi,base__density"` — **cannot be installed without an ABI and a density split.** |
| `config.armeabi_v7a` | 12 `.so` files only (26 MB) | Native code for 32-bit ARM |
| `config.mdpi` | 783 `res/` entries | Baseline density artwork + 24 locales of `welcome_carousel_ph_*.webp` |
| `config.en` | `resources.arsc` only (147 KB) | 220 `values-en` + 422 `values-en-rGB` strings |

**This XAPK is language-incomplete.** `res/xml/splits0.xml` declares **21** language splits (`config.cs`, `config.da`, `config.de`, `config.en`, `config.es`, `config.fr`, `config.in`, `config.it`, `config.ja`, `config.ms`, `config.nb`, `config.nl`, `config.pl`, `config.pt`, `config.ru`, `config.sv`, `config.th`, `config.tl`, `config.tr`, `config.vi`, `config.zh`) and `res/xml/locales_config.xml` lists **25** locales. Only `config.en` is present here — **20 language splits are absent**. On a non-English device this build falls back to the base APK's default (English) resources.

`res/xml/splits0.xml` also carries `com.android.vending.splits` / `com.android.vending.derived.apk.id = 4`, and the base manifest declares `com.android.stamp.source = https://play.google.com/store` with `com.android.stamp.type = STAMP_TYPE_DISTRIBUTION_APK` — this is a **Google Play-delivered build**, not a sideloaded repackage.

---

## 2. Signing

The base APK has **no `META-INF/*.RSA`** (no v1/JAR signature). Signing lives entirely in the **APK Signing Block**, which sits between the last local file entry and the central directory.

| Block ID | Meaning | Length | Contents |
|---|---|---:|---|
| `0x7109871a` | APK Signature Scheme **v2** | 2,582 B | signer certificate + signatures |
| `0xf05368c0` | APK Signature Scheme **v3** | 2,582 B | signer certificate + signatures (same signer) |
| `0x6dff800d` | **Google Play source-stamp** certificate | 3,061 B | one DER X.509 cert, 1,417 B |
| `0x2146444e` | **Source-stamp attestation** | 1,558 B | protobuf attestation blob |
| `0x42726577` | `"Brew"` padding | 2,433 B | zeroes |

**The source-stamp certificate is Google's Android stamp cert.** Verified two independent ways:

```
subject = C=US, ST=California, L=Mountain View, O=Google Inc., OU=Android, CN=Android
issuer  = (self-signed, same subject)
notBefore = May 19 10:35:21 2020 GMT   notAfter = May 19 10:35:21 2050 GMT
serial  = BF53FABB0B4E907F8F2A22A1B0E0D77F6DEF1060
SHA-256 = 32:57:D5:99:A4:9D:2C:96:1A:47:1C:A9:84:3F:59:D3:41:A4:05:88:45:83:FC:08:7D:F4:23:7B:73:3B:BD:6D
```

That fingerprint is **byte-identical** to the APK's own `stamp-cert-sha256` file (`3257d599a49d2c961a471ca9843f59d341a405884583fc087df4237b733bbd6d`). The stamp proves the APK was issued by Google Play and has not been modified since.

**Not verified:** the v2/v3 *signer* identity (Strava's own upload key) was not cryptographically validated — REA's Android provider explicitly reports `signature_verification: "not_performed"`. The presence of the Google stamp plus the Play source metadata is strong evidence of an unmodified Play build, but the signing chain itself was not verified here.

The splits are signed independently by `bundletool` (`config.armeabi_v7a.apk` carries `META-INF/BNDLTOOL.RSA` + `BNDLTOOL.SF`).

---

## 3. Build identity

Recovered from `META-INF/version-control-info.textproto` and `assets/sentry-debug-meta.properties`:

| Field | Value |
|---|---|
| Git revision | `6a313c7826f7fd58b8203a5eabedfbe6e8a05072` |
| Android Gradle Plugin | 8.12.2 |
| Gradle | 8.13 |
| Kotlin plugin | 2.1.21 |
| JVM target | 17 |
| Build timestamp | 2026-03-23 20:16:14 GMT *(inferred upper bound — read from the Sentry Gradle plugin's recorded date, not a dedicated build stamp)* |
| Sentry ProGuard UUID | `4e7f0e62-45a1-372a-89a4-5e573f0f2410` |
| compileSdkVersion | 36 (Android 16) · platformBuildVersionName `16` |
| minSdkVersion | 26 (Android 8.0) · targetSdkVersion | 35 (Android 15) |

`THIRD-PARTY.txt` in the APK root reads *"The project has no dependencies."* — that is a **stale Gradle template string**, contradicted by the 143 `META-INF/*.version` files and 471 dependency coordinates in `assets/sentry-external-modules.txt`. It is not a licence statement.

---

## 4. Byte-level census

| Layer | Count | Notes |
|---|---:|---|
| Outer XAPK members | 6 | all STORED, no compression |
| Base APK zip entries | 6,687 | |
| Base APK uncompressed payload | 92,958,379 B | |
| DEX files | 7 | 49,357,560 B total |
| DEX class definitions | 72,470 | 281,218 strings · 331,446 methods · 207,113 fields · 101,597 types |
| Distinct class *names* | 43,827 | REA/JADX view (deduplicated) |
| `res/` entries in base | 5,773 | + 783 in `config.mdpi` |
| `META-INF/` entries in base | 237 | 143 `.version`, ~70 licences, 22 `.kotlin_module` |
| `assets/` entries in base | 12 | incl. a 20.4 MB `assets.zip` |
| Native libraries | 12 | 25,873,088 B total (armeabi-v7a only) |
| `resources.arsc` | 2,682,012 B | 24,895 resource entries, 12,492-entry global string pool |
| Manifest (binary XML) | 236,168 B | decodes to 194,810 characters of XML |

**No arm64-v8a or x86/x86_64 splits are present.** This build is **32-bit ARM only**.
