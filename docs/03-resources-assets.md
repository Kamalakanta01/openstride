# 03 — Resource, Asset, Locale & UI-Surface Inventory
**Strava Android 456.10 (`com.strava`, versionCode 12397026) — XAPK at `/tmp/opencode/strava/`**

All figures derived directly from the ZIP central directories + `resources.arsc` parsed with a
locally-written AXML/ARSC reader (`axml_res.py`, `arsc_res.py`). The app was **not** run.
No MCP/RE tools were used.

| Artifact | ZIP entries | Uncompressed | `res/` entries | `resources.arsc` |
|---|---|---|---|---|
| `com.strava.apk` (base) | 6,687 | 88.7 MB | 5,773 (18.5 MB) | 2,682,012 B |
| `config.mdpi.apk` | 789 | 6.1 MB | 783 (5.4 MB) | 548,444 B |
| `config.en.apk` | 6 | 0.1 MB | 0 | 147,096 B |
| `config.armeabi_v7a.apk` | 17 | 25.2 MB | 0 | — (native only) |

---

## 1. `res/` tree census

### 1.1 Base APK — 5,773 entries across 54 directories

`res/` contains **no `values/` directory** (expected — all `values*` resources are compiled into
`resources.arsc`; only file-based resource types appear as directories).

#### By resource type (file count / uncompressed bytes)

| Type | Files | Bytes | Notes |
|---|---:|---:|---|
| `drawable` | 2,503 | 5,543,501 | 2,468 are binary-XML VectorDrawables; 28 `.webp`, 7 `.png` |
| `layout` | 1,504 | 2,283,284 | 1,833 distinct layout names |
| `raw` | 516 | 4,911,248 | 487 are Joda-Time tz files; 22 `.riv` (Rive); rest misc |
| `layout-v33` | 319 | 236,108 | API-33 overrides (Material3) |
| `color` | 255 | 161,844 | all `.xml` colour-state-list selectors (300 colour entries in total across `color`, `color-v23`, `color-v31`, `color-night-v8`) |
| `layout-v31` | 150 | 84,120 | API-31 overrides (Material3) |
| `drawable-night-v8` | 78 | 763,830 | dark-theme drawables |
| `anim` | 73 | 65,204 | |
| `menu` | 64 | 39,348 | |
| `xml` | 44 | 47,884 | all binary XML (see §6) |
| `animator` | 41 | 43,712 | |
| `color-v31` | 32 | 13,152 | |
| `mipmap-anydpi-v26` | 16 | 7,268 | adaptive launcher icons (§4) |
| `interpolator` | 14 | 4,388 | |
| `drawable-v21` | 12 | 9,632 | |
| `anim-v21` | 11 | 5,956 | |
| `drawable-xhdpi-v4` | 11 | 1,093,162 | |
| `font` | 11 | 938,084 | Boathouse OTF + Roboto TTF |
| `interpolator-v21` | 10 | 3,880 | |
| `color-v23` | 9 | 5,788 | |
| `drawable-xxhdpi-v4` | 9 | 847,406 | |
| `drawable-v23` | 8 | 5,932 | |
| `drawable-nodpi-v4` | 7 | 4,845 | |
| `drawable-mdpi-v4` | 6 | 272,303 | |
| `layout-v21` | 5 | 7,328 | |
| `color-night-v8` | 4 | 1,556 | |
| `drawable-anydpi-v24` | 4 | 3,552 | |
| `drawable-hdpi-v4` | 4 | 2,001 | |
| `drawable-watch-v20` | 4 | 3,192 | |
| `drawable-xxxhdpi-v4` | 3 | 209,514 | |
| `layout-land` | 3 | 4,440 | |
| `mipmap-{hdpi,mdpi,xhdpi,xxhdpi,xxxhdpi}-v4` | 3 each (15) | 514,522 | icon backgrounds (§4) |
| `animator-v21` | 2 | 2,432 | |
| `drawable-ldrtl-v17` | 2 | 1,488 | |
| `drawable-v19` / `drawable-v31` | 2 each | 2,008 | |
| `layout-{h540dp,h640dp,sw600dp}-v13` | 2 each | 12,988 | tablet/large-screen |
| `layout-v23` / `layout-v26` / `layout-watch-v20` | 2 each | 5,680 | |
| `drawable-anydpi-v21` | 1 | 1,040 | |
| `drawable-night-{xhdpi,xxhdpi,xxxhdpi}-v8` | 1 each | 91,158 | |
| `drawable-notnight-xhdpi-v8` | 1 | 549,517 | |
| `layout-h420dp-v13` | 1 | 700 | |
| `navigation` | 1 | 1,680 | `navigation_graph_new_nav.xml` (Jetpack Navigation) |
| `raw-night-v8` | 1 | 1,083 | `html_content_template.html` (dark) |
| **TOTAL** | **5,773** | **19,349,911 (18.5 MB)** | |

#### Qualifiers present in the base APK

| Qualifier class | Values present | Dir count |
|---|---|---:|
| **Density** | `mdpi`, `hdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`, `nodpi`, `anydpi` | 21 |
| **Night** | `night`, `notnight` | 9 |
| **API level** | `v19`, `v20`, `v21`, `v23`, `v24`, `v26`, `v31`, `v33` | 20 |
| **Screen size** | `h420dp`, `h540dp`, `h640dp`, `sw600dp` | 6 |
| **Orientation** | `land` | 1 |
| **Layout dir** | `ldrtl` | 2 |
| **Device** | `watch` | 4 |
| **Language** | **NONE** | **0** |

**Key finding: the base APK contains zero language-qualified resource directories.**

#### Largest individual resource files (base)

| File | Bytes |
|---|---:|
| `res/raw/subscriber_welcome_screen.riv` | 1,214,072 |
| `res/drawable/streak_shareable_background.webp` | 974,618 |
| `res/raw/route_builder_ftux_animation.riv` | 742,350 |
| `res/raw/post_upload_animation.riv` | 718,528 |
| `res/raw/badge_milestone_010.riv` | 633,003 |
| `res/drawable-xhdpi-v4/surface_type_ftux.webp` | 565,060 |
| `res/drawable-night-xhdpi-v8/privacy_data_heatmap.png` | 561,213 |
| `res/drawable-notnight-xhdpi-v8/privacy_data_heatmap.png` | 549,517 |
| `res/raw/generative_routes_pin_drop_upsell_animation.riv` | 412,314 |
| `res/drawable-night-v8/surface_type_ftux.webp` | 378,846 |
| `res/drawable-xhdpi-v4/poi_ftux.webp` | 373,706 |
| `res/drawable/link_background_asset.png` | 265,776 |
| `res/drawable-mdpi-v4/end_card_placeholder.webp` | 254,302 |
| `res/raw/loader.riv` | 237,000 |
| `res/font/stream_roboto_{bold,medium,regular}.ttf` | ~170,000 each |

### 1.2 `config.mdpi.apk` — 783 `res/` entries (density split)

| Directory | Entries | What it adds |
|---|---:|---|
| `res/drawable-anydpi-v24` | 316 | **Vector POI / startpoint / segment markers**: `poi_pharmacy_{xsmall,small,medium,large}`, `poi_restaurant_*`, `poi_restroom_*`, `poi_rest_area_*`, `poi_trailhead_*`, `poi_viewpoint_*`, `poi_water_source_*`, `poi_transition_zone_*`, `poi_trail_network_*`, `poi_segment_start/end_*`, `poi_startpoint_{ride,run,walk,hike,mtb,gravel,trail_run}_*`, `sports_e_skateboard_normal_*` (4 sizes each) |
| `res/drawable-anydpi-v21` | 32 | Vector drawables (anydpi, API-21) |
| `res/drawable-mdpi-v4` | 191 | mdpi raster/vector art: `community_motivation_img1..5`, `insight_tex_z_0..5`, `welcome_carousel_bg_0..3`, `welcome_carousel_ph_0..3`, `club_banner_empty`, `fitness_tracker_list` (79 KB), `image_student_plan` (160 KB), `record_ask`, `trail_edu`, `secondmile_*`, `runna_community_validation`, `chat_ftux_image`, `club_ftux_image`, `secondmile_*`, plus 25 `abc_*` AppCompat 9-patches and `com_facebook_*` assets |
| `res/drawable-night-mdpi-v8` | 24 | dark variants: `base_layer_*`, `basemap_standard`, `heat_personal_*`, `heat_global/night/weekly`, `detailed_goals_widget`, `goals_widget`, `club_banner_empty`, `spandex_avatar_*`, `streaks_opt_in_notif` |
| `res/drawable-night-xhdpi-v8` | 45 | **muscle-anatomy PNGs** (male/female/non-binary: `biceps_*`, `quadriceps_*`, `glutes_*`, `hamstrings_*`, `calves_*`, `triceps_*`, `shoulders_*`, `lats_*`, `chest_*`, `abdominals_*`, `forearms_*`, `adductors_*`, `abductors_*`, `trapezius_*`, `lower_back_*`, `adductors_*`) |
| `res/drawable-notnight-xhdpi-v8` | 45 | same anatomy set, light theme |
| `res/drawable-notnight-mdpi-v8` | 1 | `base_layer_poi.webp` |
| `res/drawable-night-xxhdpi-v8` | 1 | `map_loading_placeholder.webp` (100 KB) |
| `res/drawable-hdpi-v4` | 19 | `garmin_with_polyline`, `other_devices_with_polyline`, `ico_health`, `trial_education_*`, `streaks_opt_in_notif`, `crop_image_menu_*` |
| `res/drawable-xhdpi-v4` | 7 | `family_plan_upsell_hero`, `heatmap_color_icon_*_small` (6) |
| `res/drawable-xxhdpi-v4` | 3 | `continue_to_runna` (227 KB), `map_loading_placeholder` (107 KB), `runna_plan_upsell_default` (116 KB) |
| `res/drawable-ldrtl-mdpi-v17` | 3 | `abc_spinner_mtrl_am_alpha`, `zs_request_bubble_{agent,user}` 9-patches |
| **24 language dirs** (`drawable-{cs,da,de,en-rGB,es-rES,es,fr,in,it,ja,ms,nb,nl,pl,pt-rPT,pt,ru,sv,th,tl,tr,vi,zh,b+zh+Hant}-xhdpi-v4`) | 4 each (96) | **Localised welcome-carousel photos** `welcome_carousel_ph_0.webp … ph_3.webp` — the only *language-qualified* raster assets in the whole XAPK |

Extensions in the split: **348 `.xml` (binary), 264 `.webp`, 171 `.png`**.

`config.mdpi.apk` ships its own `resources.arsc` (548,444 B) with **690 non-empty default-config
entries** (687 drawable + 3 dimen) plus **23 locale configs × 4 drawables** and `zh` × 8.

### 1.3 `config.en.apk` — 6 entries, **no `res/` files at all**

```
AndroidManifest.xml     984 B
resources.arsc      147,096 B   <-- the entire payload
stamp-cert-sha256       32 B
META-INF/BNDLTOOL.{SF,RSA,MF}
```

It is a **pure language split**: the only content is a resource table holding
**220 `en` + 422 `en-rGB` string values** (§3). No layouts, no drawables, no strings file
other than the ARSC.

### 1.4 `config.armeabi_v7a.apk` — 17 entries, no `res/`

`AndroidManifest.xml` + 12 native libraries (`libFatmapSdk.so` 10.8 MB, `libmapbox-maps.so` 5.7 MB,
`libmapbox-common.so` 3.5 MB, `librive-android.so` 3.8 MB, `libsentry.so`, `libembrace-native.so`,
`libc++_shared.so`, `liblitr-jni.so`, `libandroidx.graphics.path.so`, `libdatastore_shared_counter.so`,
`libandroid-tests-support-code.so`, `libsentry-android.so`) + signature.

---

## 2. `assets/` inventory (12 entries in the base APK)

| Asset | Size | Description |
|---|---:|---|
| `assets/assets.zip` | 20,367,814 | **Fatmap 3D-map SDK asset bundle** (see §2.1) |
| `assets/PublicSuffixDatabase.list` | 132,737 | Mozilla Public Suffix list (9,772 lines) — used by OkHttp/Guava for cookie domain scoping |
| `assets/database/geoid-height-map-v0.db` | 106,496 | **SQLite3** geoid undulation (elevation) tile DB (see §2.2) |
| `assets/dexopt/baseline.prof` | 29,524 | **ART baseline profile** (see §2.3) |
| `assets/dexopt/baseline.profm` | 3,304 | **ART baseline-profile metadata** (see §2.3) |
| `assets/help_center_article_style.css` | 8,231 | Zendesk Help Center article stylesheet (rendered in the in-app web help centre) |
| `assets/mock_segment_efforts.json` | 2,799 | **Debug mock data** — 1 fake "Strava Athlete" effort on a segment (altitude/grade/HR streams) used for local segment-effort UI testing |
| `assets/prime.properties` | 27 | `cache.diskLocation=external` — Firebase/Glide cache location override |
| `assets/sdk_versions/com.mapbox.common.core` | 16 | `common/24.7.0` — Mapbox Common SDK version marker |
| `assets/sdk_versions/com.mapbox.maps.core` | 13 | `sdk/11.7.0` — Mapbox Maps SDK version marker |
| `assets/sentry-debug-meta.properties` | 162 | Sentry Gradle plugin build metadata + ProGuard UUID (`4e7f0e62-…`), build date `Mon Mar 23 20:16:14 GMT 2026` |
| `assets/sentry-external-modules.txt` | 21,242 | Sentry "external modules" list — frames not belonging to app code (system, SDK) for symbolication |

### 2.1 `assets.zip` — 962 entries (Fatmap SDK)

The archive's own `README.md` states the provenance:

> * **Main, RTShaderLib** — copied from Ogre (Ogre3D `Media/` directory)
> * **Shared** — formerly a submodule pointing at the `FatmapSDKSharedAssets` repository
> * **shaders, textures** — FatmapMobileSDK assets not shared with the web version

| Path | Files | Contents |
|---|---:|---|
| `Shared/Configs/` | 811 | Map style + icon bundle: 776 `.png` POI/medal/badge markers, 16 `.json` |
| ├ `Shared/Configs/StravaTopo/style.json` | | 310,827 B — **Mapbox-GL style** (`version`, `name`, `center`, `zoom`, `sources`, `sprite`, `glyphs`, `layers`, `created`) |
| ├ `Shared/Configs/StravaTopoDark/style.json` | | 309,887 B — dark map style |
| ├ `Shared/Configs/StravaTopoWinter/style.json` | | 353,456 B — winter map style |
| └ `Shared/Configs/GeneratedIcons/Icons.json` + `X1/*.png` | | Generated POI/medal/topo icons (`dark-topo_poi_alpine_hut`, `dark-topo_medal_02_2x`, `dark-topo_local_legend_2x`, …) |
| `Shared/Shaders/` | 48 | `.fs`/`.vs` GLSL for the 3D scene (`Content/Circle`, `FocusMarker`, `Icon-DEFAULT`, …) |
| `Shared/Textures/` | 19 | `Heatmap/{blue,bluered,gray,orange,purple,red}_{dark,light}.png`, `Clear.png` |
| `Shared/SDFonts/` | 10 | `.pbfont` — pre-rasterised SDF glyph atlases (Boathouse, Noto Sans) |
| `Shared/VectorFonts/` | 11 | `Boathouse*.ttf`, `Noto Sans*.ttf` used to bake the SDF fonts |
| `Shared/Skybox/` | 3 | `Skybox.obj` + `SkyboxHigh.png` / `SkyboxLow.png` |
| `RTShaderLib/GLSL/` | 13 | Ogre3D `RTShaderLib` fixed-function shader emulation (`FFPLib_*.glsl`) |
| `Main/` | 14 | Ogre3D `Media/` shader sources + `spot_shadow_fade.dds` |
| `Data/Packages/GlobalAltitudeRange/9.gart` | 1 | 1,048,596 B — **global altitude range pack** (zoom level 9) |
| `shaders/`, `textures/` | 10 | Mobile-only shader/texture set (`LineDashed6.png`, `unknown.png`) |
| `lua/` | 3 | `AnimatedImagePlayer.lua`, `protoc.lua` (scripting hooks) |
| `myrootcert.pem` | 1 | Root CA for the embedded TLS stack of the 3D SDK |

**Purpose:** this is the payload for `libFatmapSdk.so` — Strava's **"Flyover" 3D flyover map**,
rendered by an Ogre3D-derived engine on top of Mapbox GL vector tiles, with terrain shading,
heatmaps, POI sprites, local legends and elevation shading.

### 2.2 `geoid-height-map-v0.db` — SQLite3, elevation data

```
magic: "SQLite format 3\0"   (verified header, 16-byte SQLite file header)
tables:
  MapParams  (id INTEGER, value BLOB)   -> 1 row
  Tiles      (token TEXT, tile BLOB)    -> 6 rows
index:  sqlite_autoindex_Tiles_1
```

`MapParams.value` decodes as a protobuf (33 B): `field1=9` (varint), `field2=5` (varint),
`field3=0` (varint), `field4=193.0` (double), `field5=-107.0` (double), `field6=0.27` (double)
— the tile-zoom level and the height range of the map.

Each `Tiles.tile` is a protobuf-framed **JPEG** (`FF D8 FF E0 … JFIF`), 512×512, at byte offset 6
inside the blob; tile tokens are `1, 3, 5, 7, 9, b` (hex — a hexagonal/quad-index tile scheme).

**Purpose:** a low-resolution **geoid undulation** (EGM96-style) height raster, 6 tiles at 512².
The app uses it to convert GPS ellipsoidal altitude into orthometric/mean-sea-level elevation for
the 3D terrain rendering and for elevation-gain profiles of recorded activities.

### 2.3 `baseline.prof` / `baseline.profm` — ART baseline profiles

| File | First bytes | Interpretation |
|---|---|---|
| `baseline.prof` | `70 72 6F 00` (`"pro\0"`) + `"010\0"` | ART **baseline profile**, format version **010** (Android 12+), 29,524 B |
| `baseline.profm` | `70 72 6D 00` (`"prm\0"`) + `"002\0"` | ART **baseline-profile metadata**, version **002**, 3,304 B |

Both are confirmed by magic. The `.prof` holds the class/method hot-path profile plus per-dex
checksums; the `.profm` holds the dex-file list and their checksums so the platform can detect a
changed APK and invalidate the profile.

**Purpose:** at install time ART AOT-compiles the profiled methods, so that `com.strava`'s
startup path (SplashActivity → auth → feed) is pre-compiled — measurably faster cold start
and less JIT/verification work at runtime. Play also uses this file for its own install-time
optimisation and for App Bundle "baseline profile" delivery.

---

## 3. Localised resources — the locale situation

### 3.1 Evidence from `resources.arsc`

| ARSC | Non-empty entries | Locales present | Breakdown |
|---|---:|---|---|
| **base** (`com.strava.apk`) | 22,882 | **`(default)` only** | anim 84, animator 43, bool 19, color 2,254, dimen 1,676, **drawable 2,712**, font 11, id 3,837, integer 97, interpolator 24, **layout 2,394**, menu 64, mipmap 31, navigation 1, raw 517, **string 7,546**, style 1,528, xml 44 |
| **config.en** | 642 | `en` (220), `en-rGB` (422) | string 220 + 422 only |
| **config.mdpi** | 690 + 96 | `(default)` 690; 23 locale configs × 4 drawable; `zh` × 8 | drawable + 3 dimen only |

Global value-string pool: base = **12,492** strings; key-string pool = **24,882** resource names.

### 3.2 Declared language splits — `res/xml/splits0.xml` (decoded)

```xml
<splits>
  <module name="">
    <language>
      <entry key="cs" split="config.cs"/>    <entry key="da" split="config.da"/>
      <entry key="de" split="config.de"/>    <entry key="en" split="config.en"/>
      <entry key="es" split="config.es"/>    <entry key="fr" split="config.fr"/>
      <entry key="in" split="config.in"/>    <entry key="it" split="config.it"/>
      <entry key="ja" split="config.ja"/>    <entry key="ms" split="config.ms"/>
      <entry key="nb" split="config.nb"/>    <entry key="nl" split="config.nl"/>
      <entry key="pl" split="config.pl"/>    <entry key="pt" split="config.pt"/>
      <entry key="ru" split="config.ru"/>    <entry key="sv" split="config.sv"/>
      <entry key="th" split="config.th"/>    <entry key="tl" split="config.tl"/>
      <entry key="tr" split="config.tr"/>    <entry key="vi" split="config.vi"/>
      <entry key="zh" split="config.zh"/>
    </language>
  </module>
</splits>
```

**21 language splits** are declared. The `<module name="">` attribute is empty in the compiled
binary form.

### 3.3 Per-app language config — `res/xml/locales_config.xml` (decoded)

25 locales: `cs, da, de, en, en-rGB, es, es-rES, fr, in, it, ja, ms, nb, nl, pl, pt, pt-rPT, ru, sv,
th, tl, tr, vi, zh, b+zh+Hant`.

### 3.4 Conclusion on the locale question

- **The base APK ships NO localised resources.** Every resource is in the default configuration,
  whose strings are the English source strings (7,546 of them).
- `config.en` is **not** a duplicate of the base — it carries the explicit `values-en/`
  (220 strings: Strava's own `stream_chat_*`, `stream_compose_*` overrides) and `values-en-rGB/`
  (422 strings: AndroidX AppCompat `abc_*`) override sets, i.e. the *English* localisation
  variants of the base's default English.
- The other 20 language splits (`config.cs` … `config.zh`) are **not present in this XAPK**; they
  are delivered on demand by Google Play based on the device locale.
- The only language-qualified *files* present anywhere in the XAPK are the 96
  `welcome_carousel_ph_*.webp` images inside `config.mdpi.apk` (24 locales × 4).

---

## 4. App icon / launcher

### 4.1 Container-level `icon.png`

`/tmp/opencode/strava/icon.png` — **44,686 B, PNG, 512×512, 8-bit palette (colour type 3),
non-interlaced**. This is the XAPK/Play-Store listing icon (the "icon" field of
`manifest.json`), not an in-APK resource.

### 4.2 Launcher activity-aliases (16)

All 15 launcher aliases point at the same target `com.strava.SplashActivity`, are
`exported="true"` and carry `<intent-filter><action MAIN/><category LAUNCHER/>`. The 16th alias
(`com.strava.healthconnect.ViewPermissionUsageActivity`) is a Health Connect alias, not a launcher.

| # | Alias | `android:icon` | Mipmap resource present? |
|---:|---|---|---|
| 1 | `SplashActivityLocalLegendIcon` | `@mipmap/ic_launcher_locallegend` | yes |
| 2 | `SplashActivityXomIcon` | `@mipmap/ic_launcher_xom` | yes |
| 3 | `SplashActivityTrophyIcon` | `@mipmap/ic_launcher_trophy` | yes |
| 4 | `SplashActivityMedalIcon` | `@mipmap/ic_launcher_medal` | yes |
| 5 | `SplashActivityHikingIcon` | `@mipmap/ic_launcher_hike` | yes |
| 6 | `SplashActivityCyclingIcon` | `@mipmap/ic_launcher_ride` | yes |
| 7 | `SplashActivityRunningIcon` | `@mipmap/ic_launcher_run` | yes |
| 8 | `SplashActivityKudosIcon` | `@mipmap/ic_launcher_kudos` | yes |
| 9 | `SplashActivityStickerIcon` | `@mipmap/ic_launcher_sticker` | yes |
| 10 | `SplashActivityPrideIcon` | `@mipmap/ic_launcher_pride` | yes |
| 11 | `SplashActivityRoadIcon` | `@mipmap/ic_launcher_road` | yes |
| 12 | `SplashActivityGravelIcon` | `@mipmap/ic_launcher_gravel` | yes |
| 13 | `SplashActivityTealIcon` | `@mipmap/ic_launcher_teal` | yes |
| 14 | `SplashActivityRedIcon` | `@mipmap/ic_launcher_red` | yes |
| 15 | `SplashActivityBlackIcon` | `@mipmap/ic_launcher_black` | yes |
| 16 | `healthconnect.ViewPermissionUsageActivity` | *(none)* | — |

This is Strava's **subscription-gated "custom app icon"** feature: 14 alternate launcher icons +
the default.

### 4.3 Actual icon resource files

**`res/mipmap-anydpi-v26/` — 16 adaptive icons** (base APK):

| File | Bytes | Background | Foreground | Monochrome |
|---|---:|---|---|---|
| `ic_launcher_default.xml` | 548 | `@color/global_brand` → `@color/core_o3` = `#FFFC5200` (Strava orange) | `ic_launcher_foreground` | yes |
| `ic_launcher_black.xml` | 448 | `@color/global_dark` → `@color/core_asphalt` = `#FF000000` | `ic_launcher_foreground` | — |
| `ic_launcher_teal.xml` | 448 | `@color/expanded_navy` (#0b1527) | `ic_launcher_foreground_teal` | — |
| `ic_launcher_red.xml` | 448 | `@color/expanded_maroon` (#4d0000) | `ic_launcher_foreground_daybreak` | — |
| `ic_launcher_trophy.xml` | 448 | `@color/expanded_lemon` (#ffd800) | `ic_launcher_foreground_trophy` | — |
| `ic_launcher_medal.xml` | 448 | `@color/expanded_maroon` (#4d0000) | `ic_launcher_foreground_medal` | — |
| `ic_launcher_locallegend.xml` | 448 | `@color/expanded_moss` (#082929) | `ic_launcher_foreground_locallegend` | — |
| `ic_launcher_xom.xml` | 448 | `@color/expanded_navy` (#0b1527) | `ic_launcher_foreground_xom` | — |
| `ic_launcher_hike.xml` | 448 | `@color/global_brand` | `ic_launcher_foreground_hike` | — |
| `ic_launcher_kudos.xml` | 448 | `@color/global_brand` | `ic_launcher_foreground_kudos` | — |
| `ic_launcher_ride.xml` | 448 | `@color/global_brand` | `ic_launcher_foreground_ride` | — |
| `ic_launcher_run.xml` | 448 | `@color/global_brand` | `ic_launcher_foreground_run` | — |
| `ic_launcher_sticker.xml` | 448 | `@color/global_dark` | `ic_launcher_foreground_sticker` | — |
| `ic_launcher_pride.xml` | 448 | `ic_launcher_pride_background` (raster) | `ic_launcher_foreground` | — |
| `ic_launcher_road.xml` | 448 | `ic_launcher_road_background` (raster) | `ic_launcher_foreground` | — |
| `ic_launcher_gravel.xml` | 448 | `ic_launcher_gravel_background` (raster) | `ic_launcher_foreground` | — |

**Vector foregrounds** — `res/drawable/` (13 files, 792–3,844 B):
`ic_launcher_foreground.xml` (792), `…_daybreak` (800), `…_hike` (2,204), `…_kudos` (1,476),
`…_locallegend` (3,844), `…_medal` (1,460), `…_ride` (1,844), `…_run` (2,224), `…_sticker` (1,812),
`…_teal` (800), `…_trophy` (1,528), `…_xom` (1,472), plus `$ic_launcher_foreground_sticker__0.xml` (888).

**Raster adaptive-icon backgrounds** — `res/mipmap-<density>-v4/ic_launcher_{gravel,pride,road}_background.webp`
(the only 15 density-qualified mipmaps in the base; **nothing of this kind in `config.mdpi.apk`**):

| Background | mdpi | hdpi | xhdpi | xxhdpi | xxxhdpi |
|---|---:|---:|---:|---:|---:|
| `ic_launcher_gravel_background.webp` | 2,970 | 5,558 | 11,660 | 17,440 | 24,142 |
| `ic_launcher_pride_background.webp` | 7,056 | 15,244 | 22,784 | 47,978 | 81,482 |
| `ic_launcher_road_background.webp` | 13,456 | 29,126 | 54,734 | 103,808 | 77,084 |

---

## 5. UI surface — layout inventory

**1,994 layout files** (1,504 in `res/layout` + 490 in qualified dirs `v21/v23/v26/v31/v33/land/
h420dp/h540dp/h640dp/sw600dp/watch`) resolving to **1,833 distinct layout names**;
159 names exist in more than one qualified directory (API-level / size variants).

### 5.1 Ownership split

| Bucket | Distinct names | % |
|---|---:|---:|
| Strava app-owned (Android View XML) | **694** | 37.9% |
| Strava Compose `@Preview` scaffolding (`radio_*`, `column_*`, `row_*`, `module_*`, `complex_*`) | 337 | 18.4% |
| AndroidX Glance app-widget previews (`glance_*`) | 300 | 16.4% |
| Compose `Box` previews (`box_*`) | 200 | 10.9% |
| Stream Chat SDK (`stream_*`) | 123 | 6.7% |
| Material / MDC / M3 / design system (`mtrl_*`, `material_*`, `m3_*`, `design_*`) | 66 | 3.6% |
| Zendesk (`zs_*` 36 + `zui_*` 39) | 75 | 4.1% |
| AndroidX AppCompat (`abc_*`) | 28 | 1.5% |
| ExoPlayer (`exo_*`) | 10 | 0.5% |

### 5.2 App-owned layouts grouped by feature area (mutually exclusive, priority order)

| # | Feature area | Layouts |
|---:|---|---:|
| 1 | Segments & leaderboards | 70 |
| 2 | Photos / videos / media | 59 |
| 3 | Settings & preferences | 58 |
| 4 | Route / navigation / maps | 46 |
| 5 | Feed / social / posts | 44 |
| 6 | Clubs & groups | 40 |
| 7 | Insights & analytics | 32 |
| 8 | Profile / athlete / social graph | 30 |
| 9 | Events & training | 29 |
| 10 | Challenges & badges | 27 |
| 11 | Profile editor / creation | 18 |
| 12 | Messaging / chat | 13 |
| 13 | Subscription / monetization | 12 |
| 14 | Search & discovery | 11 |
| 15 | Record / activity capture | 9 |
| 16 | Notifications | 9 |
| 17 | Onboarding / auth / registration | 7 |
| 18 | Widgets / watch / glance | 2 |
| — | (unclassified app layouts) | 178 |
| | **total app-owned** | **694** |

### 5.3 Top 20 keyword hits over all 1,833 distinct layout names

| # | Keyword | Layouts | | # | Keyword | Layouts |
|---:|---|---:|---|---|---:|---:|
| 1 | `group` | 171 | | 11 | `search` | 27 |
| 2 | `list_` | 94 | | 12 | `map` | 27 |
| 3 | `log` | 83 | | 13 | `setting` | 23 |
| 4 | `message` | 48 | | 14 | `effort` | 23 |
| 5 | `media` | 35 | | 15 | `notification` | 20 |
| 6 | `segment` | 33 | | 16 | `edit` | 19 |
| 7 | `detail` | 31 | | 17 | `challenge` | 18 |
| 8 | `athlete` | 30 | | 18 | `share` | 15 |
| 9 | `leaderboard` | 29 | | 19 | `route` | 15 |
| 10 | `club` | 29 | | 20 | `photo` | 14 |

Other notable app-owned layout names: `activity_add_gear`, `activity_add_privacy_zone`,
`activity_email_change_verification`, `activity_password_change`, `activity_service_canary_list`,
`fragment_add_goal`, `fragment_bike_form`, `fragment_club_detail_modular`,
`activity_activity_power_curve`, `training_log_activity_v2`, `training_log_sidebar_*_v2`,
`month_breakdown_calendar_day`, `leaderboard_club_filter_*`, `achievements_segment_view`,
`post_detail_v2`, `chat_list_empty_state`, `fullscreen_{media,photo,video_controls}`,
`insight_activity_item`, `profile_edit`, `media_details`, `photo_crop`, `photo_category_picker`.

**Full list of 1,833 layout names:** `/tmp/opencode/strava/layout_names_all.txt`.

### 5.4 Menus & navigation (secondary UI surface)

64 menus in `res/menu/` (e.g. `feed_menu_additions.xml`, `clubs_post_detail_menu_additions_{admin,author,viewer_v2}.xml`,
`segment_menu.xml`, `local_legends_menu.xml`, `training_log_menu_v2.xml`, `bottom_navigation_menu_new_nav.xml`,
`zs_fragment_help_menu_conversations.xml`) plus one `res/navigation/navigation_graph_new_nav.xml`
(1,680 B) — the Jetpack Navigation graph for the new bottom-nav shell.

---

## 6. `res/xml/` and `res/raw/`

### 6.1 `res/xml/` — 44 files, all binary Android XML (magic `03 00 08 00`)

All 44 were **decoded successfully** with a locally-written AXML reader. Highlights:

| File | Bytes | Decoded content |
|---|---:|---|
| `network_security_config.xml` | 708 | `<network-security-config>` → `<debug-overrides><trust-anchors><certificates src="user"/>`; `<domain-config cleartextTrafficPermitted="true"><domain includeSubdomains="false">` + CDATA **`10.0.2.2`** (the emulator loopback alias) |
| `backupscheme.xml` | 464 | `<full-backup-content><exclude domain="database" path="strava"/><exclude domain="sharedpref" path="com.strava.preference.excludeFromBackup.xml"/>` |
| `locales_config.xml` | 2,508 | `<locale-config>` with 25 `<locale name=…>` (§3.3) |
| `splits0.xml` | 2,964 | `<splits><module name=""><language>` + 21 `<entry key=… split=config.XX/>` (§3.2) |
| `file_provider_paths.xml` | 992 | `<paths>`: `cache-path/gpxfiles` (gpx), `cache-path/crashscreenshots` (bugreport), `external-files-path/profile_pictures` (profile), `files-path/analytics_files`, `files-path/network_log_files` |
| `image_share_filepaths.xml` | 308 | `<files-path name="image_provider_images" path="image_provider"/>` |
| `sharing_file_provider_paths.xml` | 788 | FileProvider paths for shared content |
| `stream_filepaths.xml` | 980 | Stream Chat SDK file paths |
| `zendesk_user_attachments.xml` | 320 | Zendesk attachment paths |
| `shortcuts.xml` | 4,708 | Static shortcuts: `WALK`, `RUN`, `RIDE`, `PAUSE_EXERCISE`… each an `intent VIEW` with `data="strava://record_shortcut?activityType=…"`; capabilities `actions.intent.START_EXERCISE / PAUSE_EXERCISE / RESUME_EXERCISE / GET_EXERCISE_OBSERVATION` |
| `ga_ad_services_config.xml` | 252 | `<ad-services-config><attribution allowAllToAccess="true"/>` (Google Ads attribution) |
| `com_android_billingclient_phenotype.xml` | 492 | Play Billing phenotype registration |
| `strava_appwidget_info.xml` | 544 | `appwidget-provider` 250×110 dp, 24 h period, `com.strava.providers.StravaAppWidgetProvider` |
| `goal_widget_info.xml` | 748 | Glance widget, 100×100 dp, `…widget.glance.configuration.GoalWidgetConfigurationActivity` |
| `detailed_goal_widget_info.xml` | 712 | Glance widget, 150×100 dp, `…DetailedGoalWidgetConfigurationActivity` |
| `streaks_snapshot_widget_info.xml` | 860 | Glance widget, 170×164 dp, resize 4×2 cells |
| `settings_*.xml` (18 files) | 480–3,536 | `PreferenceScreen` trees for every Settings sub-screen (about, aggregated_photos, audio_cues, auto_pause, contacts_sync, default_maps, direct_promotion, display, health_data_permissions, legal, live_segment, metro_heatmap, notification_generic, override_cohorts, personal_information, privacy_center, route_alerts, screen_display, sponsored_partners, third_party_connect, weather) |
| `debug_tools_preferences.xml` | 1,140 | Debug: Leak Canary, debug font, show Activity name |
| `modular_ui_tools_preferences.xml` | 1,092 | Debug: highlight modules, module binding perf, module data logging |
| `network_preferences.xml` | 2,664 | Debug: canary, on-demand gateway, staging.strava.com, sandbox, localhost (`10.0.2.2:3000`), network debugging (disable SSL pinning) |
| `featureswitch_preferences.xml` | 752 | Feature-switch categories |
| `promotions_preferences.xml` | 652 | Promotions screen |
| `quick_edit_settings_preferences.xml` | 780 | Quick-edit settings |
| `live_tracking_preference_screen.xml` | 1,440 | Live tracking preferences (`com.strava.recordingui.legacy.beacon.*` custom prefs) |

Manifest references confirmed: `android:networkSecurityConfig="@xml/network_security_config"`,
`com.android.vending.splits → @xml/splits0`.

### 6.2 `res/raw/` — 516 files + 1 night variant

| Group | Count | Content |
|---|---:|---|
| `joda_*` timezone data | 487 | 486 extension-less Joda-Time zone files (`joda_america_new_york`, `joda_etc_gmt+5`, …) + `joda_zoneinfomap` + `joda_keep.xml` — the legacy tz database used for activity time rendering |
| Rive animations (`.riv`) | 22 | `subscriber_welcome_screen` (1.2 MB), `route_builder_ftux_animation` (725 KB), `post_upload_animation` (702 KB), `badge_milestone_010` (618 KB), `generative_routes_{pin_drop_upsell,route_card_loading,map_overlay_loading}_animation` (3), `loader` (231 KB), `upgrade_subscription_persistent_nav`, `masc/fem/nonbinary_muscle_anatomy_{front,back}` (6), `checkout`, `kudos_002`, `route_builder_{move_waypoint,empty_state,draw}_animation` (3), `lossaversion_polyline_001`, `standard_loading_spinner` |
| JSON | 1 | `country_bounding_boxes.json` (12,297 B) — country polygon bounds for map framing |
| HTML | 2 | `html_content_template.html` (light + `res/raw-night-v8/` dark) — WebView template for help/privacy content |
| Binary protobuf | 1 | `com_android_billingclient_registration_info.binarypb` |
| XML | 3 | `firebase_common_keep.xml`, `glance_appwidget_keep.xml`, `joda_keep.xml` |
| Audio | 1 | `rts_chime.m4a` (50,621 B) — "ride to summit"/interval chime |
| Plain text | 1 | `com_android_billingclient_heterodyne_info` (45 B) |

### 6.3 Other non-`res/` APK top-level files worth noting

`leap_second_dates.csv` (223 B — leap-second table), `DebugProbesKt.bin`, `THIRD-PARTY.txt`,
`age-signals.properties`, `billing.properties`, `build-data.properties`,
`beacon_activity.proto`, `beacon_contact_list.proto`, `messaging_event{,_extension}.proto`,
`recent_searches.proto` (protobuf schemas for the record-UI beacon and messaging),
`kotlin-tooling-metadata.json`, ~45 Firebase/Play-Services `*.properties` version markers,
and 237 `META-INF/` entries (143 `*.version` marker files for AndroidX/Play/Compose libs,
58 `*.kotlin_module`, 12 `*.txt`).

---

## 7. Google Play split / delivery mechanism

### 7.1 `manifest.json` (XAPK container)

```json
{
  "xapk_version": 2,
  "package_name": "com.strava",
  "name": "Strava",
  "version_code": "12397026",
  "version_name": "456.10",
  "min_sdk_version": "26",
  "target_sdk_version": "35",
  "split_configs": ["config.armeabi_v7a", "config.en", "config.mdpi"],
  "total_size": 120068480,
  "icon": "icon.png",
  "split_apks": [
    { "file": "com.strava.apk",              "id": "base" },
    { "file": "config.armeabi_v7a.apk",      "id": "config.armeabi_v7a" },
    { "file": "config.en.apk",               "id": "config.en" },
    { "file": "config.mdpi.apk",             "id": "config.mdpi" }
  ]
}
```

### 7.2 Evidence in the base manifest

```xml
<manifest android:versionCode="12397026" android:versionName="456.10"
          android:requiredSplitTypes="base__abi,base__density"
          android:splitTypes=""
          package="com.strava" ...>
  ...
  <meta-data android:name="com.android.vending.splits.required"  android:value="true"/>
  <meta-data android:name="com.android.vending.splits"            android:resource="@xml/splits0"/>
  <meta-data android:name="com.android.vending.derived.apk.id"    android:value="4"/>
  <meta-data android:name="com.android.stamp.source"  android:value="https://play.google.com/store"/>
  <meta-data android:name="com.android.stamp.type"    android:value="STAMP_TYPE_DISTRIBUTION_APK"/>
```

### 7.3 Split manifests (all decoded)

| Split APK | `android:split` | `android:splitTypes` | `application` |
|---|---|---|---|
| `com.strava.apk` | *(base)* | `""` | `hasCode=true` (full app) |
| `config.armeabi_v7a.apk` | `config.armeabi_v7a` | `base__abi` | `hasCode=false`, `extractNativeLibs=false` |
| `config.mdpi.apk` | `config.mdpi` | `base__density` | `hasCode=false`, `extractNativeLibs=false` |
| `config.en.apk` | `config.en` | `""` (empty) | `hasCode=false`, `extractNativeLibs=false` |

All three splits carry `com.android.vending.derived.apk.id = 4` — the "derived APK" generation
marker that lets Play know the split set must be re-derived when the base changes.

### 7.4 Facts this supports

1. **The APK came from an Android App Bundle (AAB)**, not a universal APK: the base declares
   `requiredSplitTypes="base__abi,base__density"` and a `com.android.vending.splits` resource
   listing 21 optional language splits. On install, Play fetches **only** the ABI split matching
   the device, the density split matching the screen, and the language split matching the locale.
2. **`base__abi` and `base__density` are REQUIRED** — the base APK cannot be installed without
   `config.armeabi_v7a.apk` and `config.mdpi.apk`. This is why the XAPK bundles all three.
3. **`config.en` is OPTIONAL** (language split, `splitTypes=""`): it is delivered only for
   English-locale devices and carries only the `values-en` / `values-en-rGB` string overrides.
   The other 20 language splits were not fetched for this build.
4. **No code in the splits** — all three have `hasCode=false`; `com.strava.apk` holds all 7 DEX
   files (49.3 MB) and `resources.arsc` (2.68 MB).
5. `stamp-cert-sha256` + `com.android.stamp.*` metadata identify the set as a
   `STAMP_TYPE_DISTRIBUTION_APK` downloaded from `play.google.com`.

---

## 8. Method notes / limitations

- `res/xml/*` and `res/mipmap-anydpi-v26/*` were decoded from binary XML using a purpose-written
  AXML reader; all 44 `res/xml` files decoded cleanly — **none are left as "binary XML – not decoded"**.
- `resources.arsc` was parsed with a purpose-written ARSC reader to obtain the true
  per-config, per-type, non-empty entry counts and the per-locale string values. The
  "entry slots" figure (142,765 for the base) is larger than the non-empty count (22,882) because
  a type chunk reserves one slot per resource name per configuration.
- The `network_security_config.xml` domain name is the CDATA payload `10.0.2.2`; the decoder
  renders the CDATA chunk body as whitespace, but the value is unambiguous — it is the only unused
  string in the file's string pool.
- The `MapParams` protobuf field semantics (193.0 / −107.0 / 0.27) are reported as decoded raw
  values; their exact meaning in the Fatmap SDK is not stated in the file.
- Rive (`.riv`) animation contents were not parsed beyond file names and sizes.
- `assets.zip` was listed to one level of recursion per the task; `Shared/Configs` was sampled
  (776 PNG + 16 JSON) rather than individually enumerated.
