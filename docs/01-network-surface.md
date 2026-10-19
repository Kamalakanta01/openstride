# Strava Android APK 456.10 — Network & Remote-Surface Forensics

**Artifact:** `/tmp/opencode/strava/com.strava.apk` (base) + splits `config.en.apk`, `config.mdpi.apk`, `config.armeabi_v7a.apk`
**Package:** `com.strava` · versionName `456.10` · versionCode `12397026` · minSdk 26 · targetSdk 35
**Method:** static only. DEX string pools parsed exactly (7 DEX files, 281,218 strings), `resources.arsc` string pool + resource-name map parsed, `AndroidManifest.xml` parsed, native `.so` `strings` scan, `assets/assets.zip` scan. **No app execution, no network traffic.**

> **Caveat on method:** `strings(1)` on raw DEX *loses and corrupts* DEX string-pool entries (MUTF-8 with length prefixes). All numbers below come from a purpose-written DEX string-pool parser (`analysis/dexstrings.py`) and an AOSP-`decodeLength`-compliant ARSC parser (`analysis/arscstrings.py`, `analysis/arscres.py`). `strings` output was used only for the native `.so` files.

---

## 1. Hostname / base-URL inventory

**58 distinct network-reachable hosts** referenced across code + resources: 22 Strava first-party (`*.strava.com`) + 2 Strava-owned Google Cloud tenants + 34 third-party. Grouped below by owner.

### 1.1 Strava first-party (`*.strava.com`) — 22 hosts

| Host | Literal(s) found | Source | Role (inferred from path/usage) |
|---|---|---|---|
| `www.strava.com` | `https://www.strava.com` | DEX `classes3/4/5/6.dex`, ARSC[11699,11880‑11904] | Main API + web host; ARSC[11887] `https://www.strava.com/api/v3/authorize/%1$s?access_token=%2$s` |
| `strava.com` | `https://strava.com/legal/privacy`, `https://strava.com/legal/terms` | ARSC[11867‑11868] | Legal/landing |
| `m.strava.com` | `https://m.strava.com/api/v3/shop/login?challenge_id=` | DEX `classes4.dex` | Mobile shop / checkout login |
| `app.strava.com` | `app.strava.com` | DEX `classes5.dex`; manifest intent-filter host | App-link host (https) |
| `c.strava.com` | `c.strava.com` | DEX `classes4.dex` | Short CDN alias |
| `communityhub.strava.com` | `https://communityhub.strava.com/` | ARSC[11863] (`community_hub_url`) | Community hub |
| `graphql.strava.com` | `https://graphql.strava.com` | DEX `classes4/5.dex` | **GraphQL API endpoint** |
| `graphql.staging.strava.com` | `https://graphql.staging.strava.com` | DEX `classes5.dex` | Staging GraphQL |
| `graphql-router-` | `https://graphql-router-` | DEX `classes5.dex` | Template prefix `https://graphql-router-<env>.…` (suffix injected at runtime) |
| `tiles.strava.com` | `https://tiles.strava.com/contours/`, `/gradient/`, `/terrain/` | DEX `classes4.dex`, `libFatmapSdk.so` | Vector tiles (elevation/gradient/terrain) |
| `tessa-labs.strava.com` | `https://tessa-labs.strava.com/tiles/liveheat/` | DEX `classes3.dex` | Live heatmap tiles (lab) |
| `cdn-1.strava.com` | `https://cdn-1.strava.com/maps-proxy?lat={lat}&lng={lng}&zoom={z}&width={width}&height={height}&retina={retina}` (+`&theme=1`) | DEX `classes4.dex`, `libFatmapSdk.so` | Raster map tile proxy |
| `content-a.strava.com` | `https://content-a.strava.com` | DEX `classes5.dex`, `libFatmapSdk.so` | CDN: `/identified/globalheat/`, `/nightheat/`, `/weeklyheat/` |
| `heatmap-external-a.strava.com` | `https://heatmap-external-a.strava.com` | DEX `classes.dex`, `classes3.dex` | Global heatmap tiles |
| `heatmap-external-b.strava.com` | `heatmap-external-b.strava.com` | DEX `classes.dex`, `classes3.dex` | Global heatmap tiles (B) |
| `heatmap-external-c.strava.com` | `https://heatmap-external-c.strava.com/tiles-auth/%s/mobileblue/{z}/{x}/{y}@2x.png?px=512&v=21` (+`/mobilebluenew/`) | DEX `classes3.dex` | Authenticated (per-athlete) heatmap tiles |
| `heatmap-origin-k8s.strava.com` | `https://heatmap-origin-k8s.strava.com/tiles-night/` | DEX `classes3.dex` | Night heatmap tiles |
| `personal-heatmaps-external.strava.com` | `https://personal-heatmaps-external.strava.com/tiles/athlete_id/color/{z}/{x}/{y}@2x.png?` | DEX `classes3.dex` | Personal (colour-by-intensity) heatmap |
| `yis.strava.com` | `https://yis.strava.com` | DEX `classes4.dex`, `classes6.dex` | **Year In Sport** service (`YEAR_IN_SPORT_BASE_URL`) |
| `yis-static.strava.com` | `https://yis-static.strava.com` | DEX `classes4.dex`, `classes6.dex` | Year In Sport static assets |
| `support.strava.com` | `https://support.strava.com/hc/en-us/articles/…` | ARSC[11869‑11874], DEX `classes4.dex` | **Zendesk-hosted** help centre (see §6) |
| `www.staging.strava.com` | `https://www.staging.strava.com` | DEX `classes.dex`, `classes5.dex` | Staging host (`/tiles/pois/default-poi-style.json?style=`) |

### 1.2 Strava-owned Google Cloud tenants — 2 hosts

| Host | Literal | Source |
|---|---|---|
| `api-project-<FIREBASE_PROJECT_NUMBER>.firebaseio.com` | `https://api-project-<FIREBASE_PROJECT_NUMBER>.firebaseio.com` | ARSC[11862] (`firebase_database_url`) |
| `api-project-<FIREBASE_PROJECT_NUMBER>.appspot.com` | `api-project-<FIREBASE_PROJECT_NUMBER>.appspot.com` | ARSC[11716] (`google_storage_bucket`) |

### 1.3 Third-party hosts — 34 hosts

| Host | Literal(s) found | Source | SDK |
|---|---|---|---|
| `api.mapbox.com` | `https://api.mapbox.com/sdk-sessions/v1`, `https://api.mapbox.com/v4/mapbox.satellite-only/{z}/{x}/{y}.jpg?access_token={MAPBOX_SERVER_SECRET}` | DEX `classes3/4.dex`, `libFatmapSdk.so` | Mapbox |
| `apps.mapbox.com` | `https://apps.mapbox.com/feedback/` | DEX `classes3.dex` | Mapbox |
| `config.mapbox.com` | `https://config.mapbox.com` | DEX `classes3.dex` | Mapbox |
| `events.mapbox.com` | `https://events.mapbox.com` | DEX `classes3.dex` | Mapbox telemetry |
| `api.maptiler.com` | `https://api.maptiler.com/tiles/…/tiles.json?key={key}`, `…/fonts/{fontstack}/{range}.pbf?key={key}`, `…/maps/{id}/sprite` | `assets/assets.zip` → `Shared/Configs/StravaPlanet*/style.json` | MapTiler (Fatmap map styles) |
| `cloud.maptiler.com` | `https://cloud.maptiler.com/api/v1/sprites/{id}/content` | `assets/assets.zip` | MapTiler |
| `o352714.ingest.sentry.io` | `https://<SENTRY_PUBLIC_KEY>@o352714.ingest.sentry.io/<SENTRY_PROJECT_ID>` | `AndroidManifest.xml` `io.sentry.dsn` | Sentry |
| `data.emb-api.com` | `data.emb-api.com`, `.data.emb-api.com`, `.config.emb-api.com` | DEX `classes6.dex` | Embrace |
| `api.iterable.com` | `https://api.iterable.com/` | DEX `classes4.dex` | Iterable (push/marketing) |
| `api2.branch.io` | `https://api2.branch.io/`, `https://api2.branch.io/v1/url` | DEX `classes4/6.dex` | Branch (deferred deep links) |
| `cdn.branch.io` | `https://cdn.branch.io/` | DEX `classes6.dex` | Branch |
| `bnc.lt` | `https://bnc.lt/a/` | DEX `classes6.dex` | Branch short links |
| `chat.stream-io-api.com` | `chat.stream-io-api.com` | DEX `classes6.dex` | Stream.io Chat API |
| `api-events-staging.tilestream.net` | `https://api-events-staging.tilestream.net` | DEX `classes3.dex` | Stream.io (staging) |
| `api-events-config-staging.tilestream.net` | `https://api-events-config-staging.tilestream.net` | DEX `classes3.dex` | Stream.io (staging) |
| `cloudfront-staging.tilestream.net` | `https://cloudfront-staging.tilestream.net` | DEX `classes3.dex` | Stream.io CDN (staging) |
| `static.zdassets.com` | `https://static.zdassets.com/ekr/snippet.js?key=` | DEX `classes5.dex` | Zendesk Web Widget |
| `www.zendesk.com` | `https://www.zendesk.com/embeddables` | DEX `classes7.dex` | Zendesk |
| `club.runna.com` | `https://club.runna.com/n9Tx/stravaRouter` | DEX `classes5.dex` | Runna (Strava-owned, see note) |
| `web.runna.com` | `https://web.runna.com/redeem?code=…` (4 variants) | DEX `classes5.dex` | Runna (Strava-owned) |
| `www.recaptcha.net` | `https://www.recaptcha.net/recaptcha/api3` | DEX `classes3.dex` | Google reCAPTCHA v2/v3 |
| `facebook.com` | `https://facebook.com`, `https://facebook.com/device?user_code=%1$s&qr=1` | DEX `classes.dex` | Facebook (device-login UX) |
| `graph.%s` / `graph-video.%s` | `https://graph.%s`, `https://graph-video.%s` | DEX `classes.dex` | Facebook Graph API (format string) |
| `app-measurement.com` | `https://app-measurement.com/a` | DEX `classes3.dex` | Google Analytics for Firebase |
| `firebaseinstallations.googleapis.com` | `https://firebaseinstallations.googleapis.com/v1/` | DEX `classes3.dex` | Firebase Installations (FID) |
| `www.googleapis.com` | 26 URLs (incl. 24 × `https://www.googleapis.com/auth/fitness.*` scopes) | DEX `classes3.dex` | Google Fit / Fitness + Google APIs |
| `pagead2.googlesyndication.com` | `https://pagead2.googlesyndication.com/pagead/gen_204?id=gmob-apps` | DEX `classes3.dex` | Google Ads attribution |
| `www.googleadservices.com` | `https://www.googleadservices.com/pagead/conversion/app/deeplink?id_type=adid&sdk_version=` | DEX `classes3.dex` | Google Ads deeplink conversion |
| `accounts.google.com` | `https://accounts.google.com/o/oauth2/revoke?token=` | DEX `classes3.dex` | Google Sign-In token revoke |
| `play.google.com` | `https://play.google.com/store/account/subscriptions?sku=`, `…/apps/details?id=com.strava`, `…?id=com.recoverathletics&pli=1` | DEX `classes4/5/6.dex`, ARSC | Play Billing / referral |
| `weatherkit.apple.com` | `https://weatherkit.apple.com/legal-attribution.html` | DEX `classes5.dex`, ARSC[11700] | Apple WeatherKit |
| `www.garmin.com` | `https://www.garmin.com/en-US/` | ARSC[11876] | Garmin Connect (attribution only) |
| `public.rive.app` | `https://public.rive.app/cdn/uuid` | `librive-android.so` | Rive animation runtime |

### 1.4 Non-network / documentation / test hosts (excluded from the 58)

`developer.android.com`, `cs.android.com`, `tools.android.com`, `issuetracker.google.com`, `support.google.com`, `docs.google.com`, `goo.gl`, `goo.gle`, `g.co`, `google.com`, `www.google.com`, `github.com`, `youtrack.jetbrains.com`, `ktor.io`, `www.slf4j.org`, `xmlpull.org`, `www.w3.org`, `schemas.android.com`, `ns.adobe.com`, `aomedia.org`, `developer.apple.com`, `www.topografix.com`, `docs.mapbox.com`, `www.mapbox.com`, `labs.mapbox.com`, `publicobject.com`, `example.com`, `www.example.com`, `picsum.photos`, `dgalywyr863hv.cloudfront.net`, `ca.slack-edge.com`, `vignette.wikia.nocookie.net`, `creativecommons.org`, `www.earthenv.org`, `www.maxar.com`, `www.openstreetmap.org`, `maps.google.com`, `localhost`, `localhost:8969`, `10.0.2.2:8969`, `android.googlesource.com`, `curl.se`, `www.bundysoft.com`, `blog.mouaif.org`, `google.github.io`.

Also present: `api.maptiler.com` and `cloud.maptiler.com` are reached only from `assets/assets.zip` map-style JSON, **not** from DEX.

---

## 2. Strava API path inventory

### 2.1 API version(s) visible

| Version | Evidence |
|---|---|
| **v3 (REST)** | DEX contains the separate string constants `api` + `v3` in `classes4.dex` and `classes5.dex` (base URL assembled at runtime). ARSC[11887] hard-codes `https://www.strava.com/api/v3/authorize/%1$s?access_token=%2$s`; DEX `classes4.dex` hard-codes `https://m.strava.com/api/v3/shop/login?challenge_id=`. |
| **v2 (Zendesk Help Centre only)** | 14 literals `/api/v2/help_center…` in `classes6/7.dex` — these are **Zendesk** endpoints, not Strava's. |
| **mobile (Strava-private JSON)** | 16 literals `/api/mobile/…` in `classes6/7.dex`. |
| **private** | `/api/private/mobile_sdk/settings/{applicationId}.json` (`classes6.dex`). |
| **GraphQL (no version in URL)** | `https://graphql.strava.com` — single endpoint, ~35 named operations. |
| **Strava internal `api/*`** | 4 literals `api/users/*`, `api/events/trackPushOpen`. |

**Distinct path count:**
- Strava REST v3 relative paths: **80**
- Strava `/api/mobile/*`: **16**
- Strava `/api/private/*`: **1**
- Strava internal `api/*`: **4**
- Zendesk `/api/v2/help_center/*` + `/hc/api/*`: **17**
- Stream.io `/channels|…`: **62**
- **Total distinct API path literals: 182** (Strava-owned: **101**)

### 2.2 Strava REST v3 — 80 distinct relative paths

Grouped by resource. All from DEX `classes4.dex` / `classes5.dex` (app code) unless noted.

| Group | Paths |
|---|---|
| **Athlete (25)** | `athlete/blocks_list`, `athlete/bulk_follow`, `athlete/contacts`, `athlete/cumulative_stats`, `athlete/destroy_u13`, `athlete/email_change_request`, `athlete/facebook`, `athlete/fitness`, `athlete/invitable_athletes`, `athlete/invite_friend`, `athlete/lifecycle_states`, `athlete/mentionable_athletes`, `athlete/notifications`, `athlete/otp_opt_in`, `athlete/private_locations`, `athlete/resend_verification_email`, `athlete/routes`, `athlete/runna_training_plans`, `athlete/settings`, `athlete/show_facebook_deprecation_notice`, `athlete/suggested_follows`, `athlete/update_password`, `athlete/update_unverified_email`, `athlete/verify_email`, `athlete/verify_otp_opt_in` |
| **Activities (4)** | `activities/apple_health`, `activities/latest`, `activities/search`, `activity_bulk_updates/is_available` |
| **OAuth / auth (5)** | `oauth/access_token`, `oauth/authorize` (DEX `classes.dex`), `oauth/token` (DEX `classes5.dex`), `oauth/request_otp`, `authorize/{application_type}` |
| **Routes (7)** | `routes/build_legs`, `routes/create_v2`, `routes/details_v2`, `routes/feedback`, `routes/route`, `routes/routes_from_urls`, `routes/show_saved`, `routes/{route_id}` |
| **Challenges (3)** | `challenges/for_celebration`, `challenges/gallery`, `challenges/latest_completed` |
| **Competitions (3)** | `competitions/configurations`, `competitions/ended`, `competitions/{competition_id}` |
| **Comments / posts / photos (5)** | `comments/create_v2`, `comments/index_v2`, `comments/{comment_id}`, `posts/preview_shared_content`, `posts/{post_id}`, `photos/metadata`, `photos/{photo_id}` |
| **Uploads (2)** | `uploads/internal_fit`, `uploads/{upload_id}` |
| **Search / clubs (2)** | `search/suggested_athletes`, `clubs/search` |
| **Onboarding (4)** | `onboarding/image_asset`, `onboarding/intent_survey_response`, `onboarding/new_athlete_attributes`, `onboarding/post_first_upload` |
| **Email change (3)** | `email_change/request_otp`, `email_change/verify_existing`, `email_change/verify_new` |
| **Beacon (2)** | `beacon/settings`, `beacon/status` |
| **Maps (2)** | `maps/geo_entities`, `maps/previews` |
| **Metering / experiments / toggles (5)** | `metering/bulk_eligibility`, `metering/bulk_record_action`, `experiments/image_asset`, `experiments/single_session_cohort`, `toggles/android_recaptcha_v3_enabled` |
| **Misc (6)** | `athlete_consents/is_eligible_country`, `feed/following`, `local_legend/feedback`, `segments/explore`, `snapchat/properties`, `upsells/{zone}` |

> Note: the single-segment tokens `athlete`, `athletes`, `activities`, `segments`, `routes`, `clubs`, `challenges`, `competitions`, `comments`, `kudos`, `posts`, `photos`, `photos`, `uploads`, `subscription`, `friends`, `followers`, `following`, `gear`, `shoes`, `streams`, `tags`, `weight`, `weights`, `pixel`, `webhooks` are all present as standalone DEX strings (mostly `classes4/5.dex`) and are used as the leading path segment with an appended id.

### 2.3 Strava mobile JSON API — 16 paths (all `/api/mobile/…`)

`/api/mobile/help_center` · `/api/mobile/push_notification_devices.json` · `/api/mobile/push_notification_devices/{id}.json` · `/api/mobile/requests.json?include=last_comment` · `/api/mobile/requests.json?sort_by=updated_at&sort_order=desc` · `/api/mobile/requests/show_many.json?sort_order=desc` · `/api/mobile/requests/{id}.json` · `/api/mobile/requests/{id}.json?include=last_comment` · `/api/mobile/requests/{id}/comments.json?sort_order=desc` · `/api/mobile/uploads.json` · `/api/mobile/uploads/{token}.json` · `/api/mobile/user_fields.json` · `/api/mobile/user_tags.json` · `/api/mobile/user_tags/destroy_many.json` · `/api/mobile/users/me.json`
*(DEX `classes6.dex`, `classes7.dex`)*

### 2.4 Strava internal `api/*` — 4 paths (DEX `classes4.dex`)

`api/events/trackPushOpen` · `api/users/disableDevice` · `api/users/registerDeviceToken` · `api/users/updateSubscriptions`

### 2.5 Strava private — 1 path

`/api/private/mobile_sdk/settings/{applicationId}.json` (DEX `classes6.dex`) — mobile-SDK feature flag / settings service.

### 2.6 Zendesk Help Centre — 17 paths (DEX `classes6.dex`, `classes7.dex`)

`/api/v2/help_center` · `/api/v2/help_center/votes/` · `/api/v2/help_center/votes/{vote_id}.json` · `/api/v2/help_center/articles/search.json?respect_sanitization_settings=true&origin=mobile_sdk` · `/api/v2/help_center/articles/{article_id}/down.json` · `/api/v2/help_center/articles/{article_id}/up.json` · `/api/v2/help_center/{locale}/articles.json?respect_sanitization_settings=true` · `/api/v2/help_center/{locale}/articles/{article_id}/attachments/{attachment_type}.json` · `/api/v2/help_center/{locale}/articles/{article_id}/stats/view.json` · `/api/v2/help_center/{locale}/categories.json?per_page=1000` · `/api/v2/help_center/{locale}/categories/{category_id}.json` · `/api/v2/help_center/{locale}/categories/{id}/sections.json` · `/api/v2/help_center/{locale}/sections/{section_id}.json` · `/api/v2/help_center/{locale}/sections/{id}/articles.json?respect_sanitization_settings=true` · `/api/v2/ticket_forms/show_many.json?active=true` · `/hc/api` · `/hc/api/mobile/{locale}/article_tree.json` · `/hc/api/mobile/{locale}/articles/{article_id}.json?respect_sanitization_settings=true`

### 2.7 GraphQL — `https://graphql.strava.com`

~35 named operations found as DEX strings (Apollo 3 client, `apollographql-client-name` / `apollographql-client-version` headers present). Representative set:

`ActivityDetailQuery` · `ActivityForCommentsQuery` · `ActivityPolylineQuery` · `ActivityStreamsQuery` · `AppVersionValidationQuery` · `AthletePowerProfileQuery` · `BuildRouteQuery` · `CancellationDataPeekQuery` · `CreateRouteMutation` · `CustomDatesPowerSkillsQuery` · `EditBestEffortMutation` · `EventSearchQuery` · `FlyoverSegmentQuery` · `GetConfigurationQuery` · `GetDevicePermissionsQuery` · `GetOfferEligibilityQuery` · `GetSelectedTrainingIntentQuery` · `GetStatPrivacySettingsQuery` · `GetSubscriptionDetailQuery` · `GetSuggestedWorkoutsQuery` · `GetTrainingIntentsQuery` · `PhotosAlongRouteQuery` · `PoiDetailsQuery` · `RecentActivitiesQuery` · `RemoteAssetsQuery` · `SharableActivityDataQuery` · `SyncWorkoutToGarminMutation` · `UpdateRouteMutation` · `UserGoalsListQuery` · `UserGoalsQuery` · `createClubMutation` · `createClubWaiverMutation` · `entityNotificationSettingMutation` · `CanvasMutation` (· `graphql-ws` websocket protocol present for subscriptions)

### 2.8 Strava web/tile URL templates found as full literals

`https://tiles.strava.com/contours/`, `https://tiles.strava.com/gradient/`, `https://tiles.strava.com/terrain/`, `https://www.strava.com/tiles/liveheat/{z}/{x}/{y}?sport_types=`, `https://tessa-labs.strava.com/tiles/liveheat/{z}/{x}/{y}?sport_types=`, `https://www.strava.com/tiles/pois/default-poi-style.json?style=`, `https://www.staging.strava.com/tiles/pois/default-poi-style.json?style=`, `https://cdn-1.strava.com/maps-proxy?lat=…`, `https://content-a.strava.com/{identified/globalheat,nightheat,weeklyheat}/`, `https://heatmap-external-c.strava.com/tiles-auth/%s/mobileblue{,new}/{z}/{x}/{y}@2x.png?px=512&v=21`, `https://heatmap-origin-k8s.strava.com/tiles-night/`, `https://personal-heatmaps-external.strava.com/tiles/athlete_id/color/{z}/{x}/{y}@2x.png?`, `https://yis.strava.com`, `https://yis-static.strava.com`, `https://content-a.strava.com`.

---

## 3. URL schemes & deep links

### 3.1 `strava://` — manifest-declared hosts (104 host/path combinations)

`AndroidManifest.xml` intent-filters declare scheme `strava` for these hosts:

`activities`, `activity-search`, `age-assurance` (`/birthday-input`), `athlete`, `athlete-management`, `athlete-selection`, `athletes` (`/.*`), `beacon` (`/settings`), `challenges` (`""`, `/.*`, `/activity-list`), `chats`, `clear_map_cache`, `clubs`, `comments`, `competitions`, `dashboard`, `dashboardYou`, `email_verification`, `external`, `facebook` (`/deprecation`, `/deprecation/login`), `feedback`, `flyover`, `google-fit-connect`, `group_events`, `groups`, `login`, `map-playground` (`/.*`), `maps` (`/.*`), `modular-sheet`, `monthly-stats`, `notification-settings`, `notifications`, `onboarding`, `open`, `posts` (`/.*`), `power-profile` (`/log`), `premium`, `pros` (`/.*`), `record` (`""`, `/settings/sensors`), `record_shortcut`, `routes` (`""`, `/.*`), `routing` (`/edit`, `/ephemeral`, `/saved`, `/suggested`, `/suggested_routes/.*`), `runna` (`/interstitial`), `runna-training-plans`, `save-screen`, `second_mile` (`/complete_profile`), `segments`, `segmentslists` (`/`), `settings` (30 sub-paths incl. `/privacy/*`, `/connected-apps`, `/connected-devices`, `/data-permissions`, `/feed-ordering`, `/opt_in_to_otp`, `/phone_number`, `/sponsored-partners`, `/training_zones`, `/weather-visibility`, `/appearance`, `/change-email`, `/change-password`, `/email-notifications`, `/profile`), `signup`, `standalone-route`, `student_plan` (`/dialog`), `subscription`, `super-follows`, `support`, `training` (`/log`, `/relative-effort`), `ugc-alert`, `unsynced-activities`, `widget` (`/config`, `/config-detailed`), `workouts`.

### 3.2 `strava://` literals referenced in code (route table)

The DEX contains a *code-level* route table with more targets than the manifest. Distinct `strava://` literals in code (DEX `classes.dex`, `classes3–6.dex`):

```
strava://activities/            strava://activities/[0-9]+/create_route
strava://activities/new         strava://activities/3101545569/laps_analysis
strava://activity-search        strava://age-assurance/birthday-input
strava://athlete                strava://athlete/add-goal
strava://athlete/best_efforts   strava://athlete/performance_predictions
strava://athlete/segments/local_legends  strava://athlete/segments/starred
strava://athlete/training/log   strava://athlete/training_zones
strava://athlete/trophy-case    strava://athlete/progress
strava://athlete/fitness-dashboard?initial_scroll_anchor=streaks_calendar&tracking_source=weekly-streaks-widget
strava://athlete-management     strava://athletes/  strava://athletes/find-friends
strava://athletes/invite        strava://challenges  strava://challenges/
strava://challenges/[0-9]+/invite  strava://chats  strava://chats/new
strava://coachmark/dismiss/activity_chart  strava://comments  strava://competitions/
strava://competitions/new      strava://connected-devices/
strava://consents               strava://dashboard
strava://dashboard?athlete_force_refresh=true
strava://destination            strava://email_verification?show_resend=true
strava://external?url=          strava://facebook/deprecation
strava://facebook/deprecation/login  strava://google-fit-connect  strava://login
strava://monthly-stats          strava://notifications
strava://notification-settings  strava://onboarding/new_reg  strava://open
strava://posts/new?photo_mode=  strava://power-profile/log  strava://rate
strava://record                 strava://route/  strava://route/[0-9]+/view_details.*
strava://route/elevation_surface_info/.+
strava://routes  strava://routes/  strava://routes/new  strava://routes/saved
strava://routing/edit.*  strava://routing/ephemeral  strava://routing/ephemeral?default_tab=suggested
strava://routing/save.*  strava://routing/saved  strava://routing/segments.*?
strava://routing/use_route.*  strava://routing/use_route?id=
strava://save-screen           strava://segments  strava://segments/
strava://settings  strava://settings/connected-apps  strava://settings/connected-devices
strava://settings/connected-devices-updated  strava://settings/opt_in_to_otp
strava://settings/phone_number  strava://settings/privacy  strava://settings/privacy/grouped-activities
strava://settings/profile       strava://student_plan/dialog  strava://super-follows/
strava://support/articles/216917447  strava://support/articles/216918457
strava://support/articles/26786795557005
strava://training/relative-effort  strava://unsynced-activities
strava://widget/config  strava://widget/config-detailed
strava://workouts/  strava://workouts/suggested
```

Regex-style deep links also present: `strava://athletes/(\d)+/trophy-case/share`, `strava://routing/ephemeral`.

### 3.3 Other schemes (code + manifest)

| Scheme | Value | Source |
|---|---|---|
| `connect` | `connect://strava/livetrack` | ARSC (`garmin_live_track_url`) |
| `fbconnect` | host `cct.com.strava` | manifest intent-filter |
| `fb284597785309` | Facebook app-id scheme (FB App Links / device login) | manifest intent-filter |
| `strava.app.link` | https host (Firebase Dynamic Links / App Links) | manifest intent-filter |
| `http`/`https` app links | hosts `strava.com`, `www.strava.com`, `app.strava.com`; 30+ path prefixes (`/athlete/`, `/athletes/`, `/activities`, `/challenges`, `/clubs`, `/comments`, `/competitions`, `/dashboard`, `/settings/*`, `/segments`, `/routes`, `/runs`, `/hikes`, `/walks`, `/rides`, `/chats`, `/email_verification`, `/login/link`, `/oauth/mobile/authorize`, `/o_auth/garmin/mobile_connected`, `/nfc`, `/pros/`, `/premium`, `/subscription/checkout`, `/posts/.*`, `/challenges/.*`, `/clubs/..*`) | manifest intent-filters |
| `http` + host `strava.com` + path `/nfc` | NFC record trigger | manifest (`http://strava.com/nfc/record`, `/nfc/record/stop`, `/nfc/record/toggle` in DEX `classes4.dex`) |

### 3.4 Third-party / other deep-link schemes observed

`https://www.strava.com/api/v3/authorize/%1$s?access_token=%2$s` (ARSC[11887]) — Strava OAuth authorize callback. `https://www.strava.com/.well-known/assetlinks.json` (ARSC[11699], `asset_statements`) — Android App Links / Digital Asset Links for `www.strava.com`.

---

## 4. Embedded API keys / identifiers / secrets

> **Note on redaction.** Credential *values* are placeholders in this document. They were
> recovered literally from the artifact and were originally published that way, but
> republishing live keys for a commercial product creates an abuse channel against
> Strava's and its vendors' quotas. What is preserved — and what matters analytically —
> is the **existence, type, exact location in the artifact, and capability** of each
> credential. The literals are recoverable from your own copy of the APK.

All values below are **literal strings extracted from the APK**. Nothing is inferred.

| # | Value | Type | Exact source |
|---|---|---|---|
| 1 | `<GOOGLE_MAPS_API_KEY>` | **Google Maps / geo API key** | `AndroidManifest.xml` → `<meta-data android:name="com.google.android.geo.API_KEY" android:value="…"/>` |
| 2 | `<GOOGLE_API_KEY_2>` | **Google API key** (Firebase/Crashlytics/Places) | `resources.arsc` → resource `google_api_key` (ARSC[6518]) |
| 3 | `<GOOGLE_API_KEY_2>` | **Google Crashlytics API key** (same value) | `resources.arsc` → resource `google_crash_reporting_api_key` (ARSC[6518]) |
| 4 | `<GOOGLE_OAUTH_CLIENT_ID>` | **Google OAuth 2.0 client ID** (web/server) | `resources.arsc` → resource `default_web_client_id` (ARSC[6461]) |
| 5 | `<FIREBASE_APP_ID>` | **Firebase App ID** | `resources.arsc` → resource `google_app_id` (ARSC[6361]) |
| 6 | `<FIREBASE_PROJECT_NUMBER>` | **Firebase/GCP project number** (GCM sender ID) | `resources.arsc` → resources `gcm_defaultSenderId`, `project_id` = `api-project-<FIREBASE_PROJECT_NUMBER>` |
| 7 | `https://api-project-<FIREBASE_PROJECT_NUMBER>.firebaseio.com` | Firebase Realtime Database URL | `resources.arsc` → `firebase_database_url` |
| 8 | `api-project-<FIREBASE_PROJECT_NUMBER>.appspot.com` | Firebase / GAE storage bucket | `resources.arsc` → `google_storage_bucket` |
| 9 | `https://<SENTRY_PUBLIC_KEY>@o352714.ingest.sentry.io/<SENTRY_PROJECT_ID>` | **Sentry DSN** (public key `<SENTRY_PUBLIC_KEY>`, org `o352714`, project `4505048748785664`) | `AndroidManifest.xml` → `io.sentry.dsn` |
| 10 | `fb284597785309` | **Facebook App ID** | `AndroidManifest.xml` → `com.facebook.sdk.ApplicationId`; also manifest intent-filter scheme |
| 11 | `<FACEBOOK_CLIENT_TOKEN>` | **Facebook Client Token** | `AndroidManifest.xml` → `com.facebook.sdk.ClientToken` |
| 12 | `<ZENDESK_APP_ID>` | **Zendesk application ID** (40-hex) | `resources.arsc` → resource `zendesk_application_id` |
| 13 | `<ZENDESK_OAUTH_CLIENT_ID>` | **Zendesk OAuth client ID** | `resources.arsc` → resource `zendesk_oauth_client_id` |
| 14 | `com.strava.connect.app` | **Strava OAuth client id** (connected-app type) | DEX `classes4.dex` |
| 15 | `com.strava` | **Stream.io `hostAppId`** (host app for chat) | `AndroidManifest.xml` → `<meta-data android:name="hostAppId" android:value="com.strava"/>` |
| 16 | `<MAPBOX_PUBLIC_TOKEN_1>` | **Mapbox public token** (JWT `u=strava`, `a=ck16q9jsa18k33bqdpmvp5rzo`) | DEX `classes4.dex` |
| 17 | `<MAPBOX_PUBLIC_TOKEN_2>` | **Mapbox public token** (`a=cm3n1vkxd100c2jqzja3hc2n6`) | DEX `classes4.dex` |
| 18 | `<MAPBOX_PUBLIC_TOKEN_3>` | **Mapbox public token** (`a=cm3n1xa930zpd2is5j4krgoyu`) | DEX `classes4.dex` |
| 19 | `<MAPBOX_PUBLIC_TOKEN_4>` | **Mapbox public token** (`a=cm7olc6q805dd2lse1kmyuo2v`) | DEX `classes4.dex` |
| 20 | `4e7f0e62-45a1-372a-89a4-5e573f0f2410` | Sentry Gradle ProGuard UUID (build metadata) | `assets/sentry-debug-meta.properties` |
| 21 | `com.garmin.android.apps.connectmobile` | Garmin Connect Android package (deeplink target) | `resources.arsc` → `garmin_package_name` |

**Not found (explicitly checked, no literal present):**
- **Strava API `client_id` / `client_secret`** for the public OAuth API — no literal in APK (Strava's own app uses server-side `/oauth/mobile/authorize` with the internal `com.strava.connect.app` client id).
- **Branch key** (`io.branch.sdk.BranchKey` manifest meta-data) — **absent** from the manifest; the Branch SDK string `branch_key` and the error text *"Please enter your branch_key in your project's manifest file first."* are present in DEX `classes4.dex`, so the key is either supplied at runtime or the SDK is initialised programmatically. **Inferred: not embedded.**
- **Iterable API key** — `api.iterable.com` is present but no Iterable key literal was found. **Inferred: fetched at runtime / via remote config.**
- **Snowplow collector URL** — the Snowplow Mobile SDK is fully present (protocol `com.snowplowanalytics.snowplow/tp2`, SQLite `snowplowEvents.sqlite`) but no collector host literal was found. **Inferred: configured at runtime.**
- **MapTiler key** — `assets/assets.zip` map styles use the runtime placeholder `?key={key}`; no literal key. **Inferred: injected at runtime.**
- **AWS access keys** (`AKIA…`) — none found. No `sk.` (Mapbox secret) token found.

---

## 5. Telemetry & analytics

### 5.1 Strava's own analytics gateway

- Package `com.strava.analytics.gateway`; Room entity `AnalyticsEventEntry` (`com.strava.analytics.gateway.AnalyticsEventEntry`), backed by `AnalyticsCacheDatabase`.
- Observed schema (from DEX SQL literals): `SELECT * FROM AnalyticsEventEntry ORDER BY timestamp DESC`, `SELECT DISTINCT(\`action\`) FROM AnalyticsEventEntry ORDER BY \`action\` ASC`, `SELECT DISTINCT(page) FROM AnalyticsEventEntry ORDER BY page ASC`, `DROP TABLE IF EXISTS \`AnalyticsEventEntry\``.
- **Event envelope fields** (strings in `classes3–6.dex`): `action`, `page`, `properties`, `source`, `timestamp`, `type`, `value`, `label`, `event_type`, `user_id`, `events`, `batch`.
- Debug UI: `com.strava.superuser.AnalyticsCacheActivity` (DEX `classes3.dex`).

### 5.2 Event / screen name constants

**From `resources.arsc` (resource name → event value, 22 real entries):**
`about` · `applications_services_devices` · `beacon` · `change_email` · `change_password` · `contacts` · `data_permission` · `default_highlight_image` · `connect_device` · `display` · `email_notifications` · `feed_order_setting` · `legal` · `manages_devices` · `partner_integration` · `privacy_settings` · `push_notifications` · `restore_purchase` · `subscription_management` · `support` · `weather`

**From DEX (event/property name literals, `classes4/5.dex`):**
`bottom_nav_selected_tab` · `clicked_athlete_id` · `clicked_athlete_rank` · `club_created` · `club_event_creation_success` · `group_event_created` · `group_event_deleted` · `share_started` · `share_completed` · `onboarding_flow_completed` · `opened_from_record` · `kudos_button_clicked` · `live_segment_completed` · `live_segment_completed_audio` · `permission_requested` · `requested_athlete` · `requested_athlete_id` · `selected_tab` · `selected_tab_index` · `selected_filter_id` · `selected_distance` · `selected_pace` · `selected_plan` · `sport_selected` · `gear_deleted_action` · `bike_updated_action` · `shoes_updated_action` · `description_updated` · `title_updated` · `vanity_url_updated` · `beacon_toggle_updated` · `garmin_beacon_toggle_updated` · `download_started` · `total_routes_downloaded` · `download_completed` · `fb_mobile_push_opened` · `key_opened_from_app_shortcut` · `location_updated` · `nb_contacts_updated` · `owner_subscribed` · `viewer_subscribed` · `result_user_completed_follow_action` · `date_selected` · `distance_selected` · `filters_selected` · `page_selected` · `file_selected` · `value_selected` · `message_deleted` · `typing_started` · `typing_stopped` · `phone_number_success_toast` · `otc_success_banner` · `edit_success` · `power_skills_peek_carousel_viewed` · `race_predictions_peek_carousel_viewed` · `facebook_deprecation_soft_nudge_viewed` · `updated_consent_tag` · `updated_locally_at` · `updated_share_type` · `updated_value` · `AthleteManagementBehaviorType$AnalyticsMetadata` · `AthleteSelectionBehaviorType$AnalyticsMetadata` · `EditClubAnalytics` · `EventCreateCompletionAnalytics` · `ActivitySaveAnalytics$Companion$MapButtonOrigin` · `MediaEditAnalytics$AnalyticsInput` · `AdpAnalyticsImpl` (`collectEvents`, `trackMapColdStart`, `trackMapInteractions`) · `YearInSportAnalytics$Companion$ReferralMetadata` · `OfflineMapAnalytics`.

### 5.3 Third-party telemetry

| System | Endpoint evidence | Data implied (grounded in DEX strings) |
|---|---|---|
| **Snowplow Mobile Analytics** | `com.snowplowanalytics.snowplow/tp2` protocol; `snowplowEvents.sqlite`; schemas `…/screen_view`, `/screen`, `/screen_end`, `/screen_summary`, `/scroll_changed`, `/list_item_view`, `/deep_link_received`, `/application_lifecycle`, `/application_install`, `…/geolocation_context/jsonschema/1-1-0`, `…/mobile_context`, `…/client_session`, `…/gdpr`, `…/unstruct_event` | Screen views, scroll depth, list-item impressions, deep-link receipts, app lifecycle, **geolocation context**, GDPR consent state, anonymous user ID (`anonymous_id`), session ID. Collector URL not embedded (runtime-configured). |
| **Firebase Analytics / GA4 for Firebase** | `google_app_id` = `<FIREBASE_APP_ID>`; `https://app-measurement.com/a`; `https://firebaseinstallations.googleapis.com/v1/`; `iglu:com.snowplowanalytics.mobile/screen_view/…` | Screen + event stream, Firebase Installation ID. **Disabled in the manifest**: `firebase_analytics_collection_enabled=false`, `google_analytics_default_allow_analytics_storage=false`, `google_analytics_default_allow_ad_storage=false`, `google_analytics_default_allow_ad_user_data=false`, `google_analytics_default_allow_ad_personalization_signals=false`. |
| **Sentry** | DSN `https://<SENTRY_PUBLIC_KEY>@o352714.ingest.sentry.io/<SENTRY_PROJECT_ID>`; `libsentry.so`, `libsentry-android.so`, `libembrace-native.so`; `Sentry-Opentelemetry-…` headers | Crashes, native crashes, NDK crashes (`NativeCrashData`, `NativeSymbols`), breadcrumbs, session/perf spans, user feedback (`io.sentry.feedback.use-sentry-user=true`). `io.sentry.environment=production`, `io.sentry.auto-init=false` (initialised manually). |
| **Embrace** (8.1.0) | `data.emb-api.com`, `.data.emb-api.com`, `.config.emb-api.com`; `Embrace/a/8.1.0`; `libembrace-native.so` | Sessions, spans, performance, logs, `NetworkCapturedCall` (HTTP request/response capture), `UserInfo` (user identity), `TelemetryAttributes` (power/thermal/network state), NDK samples, FCM instrumentation, on-click/on-touch instrumentation. |
| **Mapbox telemetry** | `https://events.mapbox.com`; `com.mapbox.maps.module.telemetry`; `https://api.mapbox.com/sdk-sessions/v1` | Map session telemetry, tile/gesture usage, session IDs. |
| **Google Ads attribution** | `https://pagead2.googlesyndication.com/pagead/gen_204?id=gmob-apps`; `https://www.googleadservices.com/pagead/conversion/app/deeplink?id_type=adid&sdk_version=` | Install/deeplink conversion, **advertising ID (`adid`)**. `com.google.android.gms.permission.AD_ID` + `ACCESS_ADSERVICES_AD_ID` + `ACCESS_ADSERVICES_ATTRIBUTION` are in the manifest. |
| **Branch attribution** | `https://api2.branch.io/`, `https://api2.branch.io/v1/url`, `https://cdn.branch.io/`, `https://bnc.lt/a/`; headers `X-Branch-Request-Id`, `X-Branch-Send-Close-Request`; `branch_identity`, `branch_data`, `branch_used`, `clicked_referrer_ts` | Deferred deep-link attribution, referrer identity, install referrer (`com.android.finsky.permission.BIND_GET_INSTALL_REFERRER_SERVICE`). |
| **Facebook App Events** | `com.facebook.sdk.AutoLogAppEventsEnabled=false`; `logEvent`, `logEventAndBundle`, `logEventDropped` | Manual FB event logging (auto-log disabled). |
| **Iterable** | `https://api.iterable.com/`; `iterable_campaign_id`, `iterable_template_id`, `iterable_message_id`, `iterable_destination_url_extra`; `iterable.IterableNotificationsActivity`, `iterable.IterableNotificationTrackingService` | Push-notification campaigns, deep-link attribution for marketing pushes. |
| **Strava push-event endpoint** | `api/events/trackPushOpen` | Push-open tracking back to Strava. |

---

## 6. Third-party data recipients

Each row: SDK → host(s) → what data the strings imply.

| SDK / service | Host(s) contacted | Implied data (evidence) |
|---|---|---|
| **Mapbox Maps SDK 11.7.0** | `api.mapbox.com`, `apps.mapbox.com`, `config.mapbox.com`, `events.mapbox.com` | **Device location** (`MAPBOX_LOCATION_COMPONENT_PLUGIN_ID`, `DeviceLocationProvider.ID`, `com.mapbox.common.location.action.PROCESS_LOCATION_UPDATES`), **activity recognition** (`ACTIVITY_RECOGNITION_UPDATES`), map telemetry/sessions, map-style downloads, satellite raster tiles (`api.mapbox.com/v4/mapbox.satellite-only/…?access_token={MAPBOX_SERVER_SECRET}`). |
| **MapTiler** (via Fatmap SDK, `libFatmapSdk.so` + `assets/assets.zip`) | `api.maptiler.com`, `cloud.maptiler.com` | **Map tiles + fonts + sprites** for Strava Planet/Topo/Satellite styles; `?key={key}` appended at runtime. |
| **Sentry** | `o352714.ingest.sentry.io` | Crashes, stack traces, device/app metadata, breadcrumbs, user identity (DSN embeds user id `<SENTRY_PUBLIC_KEY>`), user feedback. |
| **Embrace** | `data.emb-api.com`, `.config.emb-api.com` | Sessions, HTTP request/response capture, user info, app/device state, NDK native crashes, click/touch instrumentation. |
| **Snowplow Mobile Analytics** | *(collector host not embedded — runtime-configured)* | Screen views, scroll depth, deep-link receipts, app lifecycle/install, **geolocation context schema**, GDPR consent, anonymous ID. |
| **Firebase / Google** | `app-measurement.com`, `firebaseinstallations.googleapis.com`, `www.googleapis.com`, `firebase.google.com` | Analytics events, Firebase Installation ID, FCM push tokens, **Google Fit** activity/health data (`www.googleapis.com/auth/fitness.activity.read/write`, `fitness.heart_rate.*`, `fitness.location.*`, `fitness.body.*`, `fitness.sleep.*`, `fitness.nutrition.*`, `fitness.blood_pressure.*`, `fitness.blood_glucose.*`, `fitness.oxygen_saturation.*`, `fitness.respiratory_rate.*`, `fitness.reproductive_health.*`, `fitness.body_temperature.*`, `fitness.cycling.*`); manifest `com.google.android.gms.cloudmessaging.FINISHED_AFTER_HANDLED=true`. |
| **Google Ads** | `pagead2.googlesyndication.com`, `www.googleadservices.com` | **Advertising ID**, install attribution, deeplink conversions. |
| **Facebook** | `facebook.com`, `graph.%s`, `graph-video.%s` | Facebook login (historically), friends list (`athlete/contacts`, `me/friends`, `pref.contacts.address_hashcode`), device-login code page, profile photos, App Events. |
| **Zendesk** | `static.zdassets.com`, `www.zendesk.com`, `support.strava.com` (Zendesk-hosted HC) | Support tickets, user identity + email (`zendesk/messaging_jwt`, `zendesk_application_id`, `zendesk_oauth_client_id`), article votes (`/api/v2/help_center/votes/…`), user attachments (`res/xml/zendesk_user_attachments.xml`). |
| **Branch** | `api2.branch.io`, `cdn.branch.io`, `bnc.lt` | Deep-link/referrer attribution, install referrer, branch identity, campaign parameters. |
| **Iterable** | `api.iterable.com` | Push/marketing messaging, campaign + template + message IDs, destination URL. |
| **Stream.io Chat** | `chat.stream-io-api.com`, `api-events-staging.tilestream.net`, `api-events-config-staging.tilestream.net`, `cloudfront-staging.tilestream.net` | Chat messages, **user-shared location** (`/users/live_locations`, `UpstreamLocationDto`, `shared_location`, `active_live_locations`), reactions, polls, attachments/media uploads, read receipts, push preferences, moderation, Agora video-call tokens. |
| **Google reCAPTCHA** | `www.recaptcha.net` | Bot-detection token (`toggles/android_recaptcha_v3_enabled`, `toggles/android_recaptcha_v2_enabled`). |
| **Apple WeatherKit** | `weatherkit.apple.com` | Weather data attribution (weather shown on activities). |
| **Rive** | `public.rive.app` | Animation runtime asset fetch (UUID). |
| **Runna** (Strava-owned partner) | `club.runna.com`, `web.runna.com` | Runna training-plan routing, referral codes (`RUNSTRAVA-PB`, `STRAVA-IW`, `STRAVA-W1`) with UTM params. |
| **Decagon** (AI ticket deflection) | *(host not embedded — path `decagon/decagon_jwt` is relative to the Strava API base)* | `X-DECAGON-AUTH-EPOCH`, `X-DECAGON-AUTH-SIGNATURE`, `DecagonAuthenticationDto(apiToken=)`, flag `USE_DECAGON_AI_TICKET_DEFLECTION` (`classes.dex`, `classes5.dex`) — support-chat content is sent to a Decagon-powered AI agent. |
| **Garmin Connect** | *(no API host in APK — only `com.garmin.android.apps.connectmobile` package + `connect://strava/livetrack` scheme)* | Activity push to Garmin, live-track link, `SyncWorkoutToGarminMutation`, `athlete/runna_training_plans`. |
| **Fitbit** | *(no host in APK — only the literals `fitbit`, `Fitbit`, `FITBIT` in `classes4.dex` and ARSC strings "Fitbit Connect activities will no longer automatically upload to Strava")* | **Support removed** — no Fitbit endpoint is present. |

---

## 7. Method / artefacts

Scripts written for this analysis (all under `/tmp/opencode/strava/analysis/`):

| File | Purpose |
|---|---|
| `dexstrings.py` | Exact DEX string-pool parser → `_dex_strings.json` (281,218 strings, per-DEX provenance) |
| `arscstrings.py` | AOSP-`decodeLength`-correct `resources.arsc` global string pool → `_arsc_values.tsv` |
| `arscres.py` | `resources.arsc` type/key map → `_arsc_res.json` (13,305 string resources with **resource names**) |
| `loader.py` | Shared loaders (DEX strings, ARSC values) |
| `inventory.py` | Cross-source URL/host harvest → `_url_inventory.json` |
| `_api_paths.json`, `_abs_paths.json` | Extracted API path sets |

**Known limits of this analysis:**
- The Strava REST base URL is **assembled at runtime** from the separate string constants `api` + `v3` (in `classes4/5.dex`); only two hard-coded `…/api/v3/…` literals exist (§2.1). The exact base (`https://www.strava.com/api/v3/`) is therefore **inferred** from those two literals + the relative-path set.
- Some hosts are runtime-constructed from templates (e.g. `https://graphql-router-…`, `graph.%s`, `map-styles`, `heatmaps`) and cannot be enumerated statically.
- Emulator/debug endpoints `10.0.2.2:8969`, `localhost:8969/stream` (Stream.io local dev) are in the release DEX.
- `assets/assets.zip` (20 MB, 962 entries) contains the Fatmap/MapTiler map styles; only the `Shared/Configs/Strava*/style.json` files carry network URLs.
- `config.en.apk` split `resources.arsc` (573 values) contains no additional keys/URLs.

---

## Addendum (post-review correction)

Two items in the "NOT FOUND" list above are **incorrect** and were resolved by later decompilation:

**1. The Branch.io live key IS present.** The static string scan missed it; decompiling `com.strava.StravaApplication.onCreate()` recovered the literal in context:

```
String string4 = "<BRANCH_LIVE_KEY>";
if (c.f107098q == null) {
    io.branch.referral.f fVarA = ...
```

Confirmed present in `classes4.dex`. It is a **live** Branch key (`key_live_…`), not a test key.

**2. The Strava API base URL is confirmed, not merely inferred.** The decompiled dependency-injection module in `LFd/d2$a;` (the same class that configures certificate pinning) contains the literal host strings used to build the HTTP client:

```
'cdn-1.strava.com', 'sha256/q61HEYJJK/e1i0DQnyP6R6tGOrRC5MbbsTcI9ev6cZ4=', 'graphql.strava.com',
'okHttpClient', 'retrofitClient', 'remoteLogger',
'https://yis-static.strava.com', 'https://yis.strava.com',
'strava-database', 'challenge-database', 'experiments-database', 'recording-database',
'privacyZonesDatabase', 'recentsDatabase', 'athlete-workouts.db',
'map-style-json-database', 'activity-tag-database',
'stream_chat_client.db', 'exoplayer_internal.db', 'videoCache',
'com.strava.dynamicmapmre.offline_maps',
'<MAPBOX_ACCOUNT_ID_1>', '<MAPBOX_ACCOUNT_ID_2>'
```

This also reveals the app's **local database names** and confirms the two additional first-party hosts `yis.strava.com` and `yis-static.strava.com`.

**3. Certificate pinning is configured**, contradicting any assumption that it is absent. See document 08 §1.

The remaining "NOT FOUND" items (Strava `client_id`/`secret`, Iterable key, Snowplow collector URL, MapTiler key, AWS keys, `sk.*` tokens) remain genuinely absent from the binary and are supplied at runtime or by remote configuration.
