# Local API Contract

What a local server must implement for the reimplementation to work with no backend.

**Source:** derived from static analysis of Strava Android v456.10. See `docs/01-network-surface.md` and `docs/06-code-architecture.md`.
**Confidence:** the *existence* and *shape* of these endpoints is observed; the exact request/response bodies are only partially recoverable, because the app's JSON models are R8-obfuscated and were not fully traced.

---

## Design principle

The original app is a Retrofit client. Its data layer is a set of interfaces returning RxJava `Single<T>` (obfuscated to `MN.f<T>`), each backed by a server response.

In this project there is **no server**. The equivalent shape is:

```
  UI / ViewModel
        │  suspend fun / Flow
        ▼
  Repository            ← single source of truth, reads the local DB
        │
        ├─▶ Room DAO (authoritative store)
        │
        └─▶ (optional) LocalApiServer  ← a real HTTP server on 127.0.0.1
                                          for parity with the original
                                          shape, useful for testing
```

**Start with the repository-over-DB shape.** It is simpler, faster, and has no serialisation layer to keep in sync. Only add the local HTTP server if you need to exercise the exact wire format.

---

## Endpoint families the app depends on

Extracted from the APK's string pool and Retrofit interface metadata. `data/api-paths.txt` has the raw list.

### Athlete & profile

| Path | Method | Purpose |
|---|---|---|
| `/api/mobile/users/me` | GET | the signed-in athlete |
| `/api/mobile/user_fields` | GET | profile field definitions |
| `/api/mobile/user_tags` | GET/POST | user tags |
| `/api/mobile/user_tags/destroy_many` | POST | remove tags |
| `/api/v3/athlete` | GET | the athlete record (REST v3) |

### Activities

| Path | Purpose |
|---|---|
| `/api/v3/athlete/activities` | list own activities |
| `/api/v3/activities/{id}` | one activity |
| `/api/v3/activities/{id}/streams/{type}` | time-series streams |
| `/api/mobile/uploads` | activity upload (multipart) |
| `/api/mobile/uploads/{token}` | upload status polling |
| `/api/mobile/requests` | segment efforts / requests |

### Segments & routes

| Path | Purpose |
|---|---|
| `/api/v3/segments/{id}` | segment definition |
| `/api/v3/segments/{id}/leaderboard` | leaderboard (**requires other users**) |
| `/api/v3/routes/{id}` | route definition |

### Feed, social, clubs — **out of scope locally**

`/api/v3/activities/{id}/comments`, kudos, followers, club feeds, club memberships, challenges, competitions, and the feed endpoints. These need other users. This project does not implement them.

### Support & help

| Path | Purpose |
|---|---|
| `/api/v2/help_center/{locale}/articles` | help articles |
| `/api/v2/help_center/{locale}/categories` | help categories |
| `/api/v2/ticket_forms/show_many` | support ticket forms |
| `/api/mobile/help_center`, `/api/mobile/{locale}/article_tree` | mobile help variants |

### Push registration

| Path | Purpose |
|---|---|
| `/api/mobile/push_notification_devices` | register a device token |
| `/api/mobile/push_notification_devices/{id}` | update/delete |

Locally meaningless — there is no push service. Registering a device can be a no-op.

### Offline map bundles

```
com.strava.dynamicmapmre.offline.data.remote.OfflineMapsApi
  getId(String)                     -> BundleIdResponse
  get(String)                       -> BundleResponse   (compressedSizeBytes, decompressedSizeBytes, sha256)
  order(BundleOrderBody)            -> BundleOrderResponse
  download(String)                  -> ResponseBody
```

`BundleOrderBody` carries a coverage area and a tileset list. The server returns a bundle id, then a bundle URL, and the app verifies the downloaded bytes against the `sha256` in `BundleResponse`.

**Locally:** the bundle source is a local directory or a `file://` URL. The sha256 check still applies — compute it locally over the packaged bundle. See `fatmap-native.md`.

---

## GraphQL

| Endpoint | Notes |
|---|---|
| `https://graphql.strava.com` | ~35 operation names recovered |

One operation was confirmed to be pinned:

```
sha256/q61HEYJJK/e1i0DQnyP6R6tGOrRC5MbbsTcI9ev6cZ4=
```

applied to `cdn-1.strava.com` and `graphql.strava.com`. **Both hosts are first-party Strava infrastructure.** In this project, GraphQL is not needed — the local repository replaces it. If you choose to expose a local GraphQL server, do **not** reuse Strava's operation names; define your own schema.

---

## Auth

The original model:

```kotlin
data class AccessToken(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long?,      // epoch seconds
    val isSignUp: Boolean,
)

data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long,
)
```

Endpoints: `com.strava.net.token.TokenApi.refreshToken(a, b, c)` and `.refreshTokenV2(a, b, c, d)`.

**Locally:** mint the token pair on device, store it in the local DB (or `EncryptedSharedPreferences` from `androidx.security:security-crypto`), and never expire it. The `isSignUp` flag distinguishes a fresh registration from a returning user — keep it, because the original app uses it to drive first-run onboarding.

**Also note:** the original app exports a `ContentProvider` (`com.strava.identity`, authority `com.strava.identity`) that serves the signed-in identity to other apps. If you implement one, **think hard about it** — it is open to every app on the device.

---

## Rate limiting, retries, timeouts

The original app's behaviour here is not fully recoverable. For a local server, pick sane defaults and document them:

- No rate limiting (single user)
- Short timeouts (local I/O is fast) — but generous enough for a first-run DB migration
- No retry on 4xx; bounded retry with backoff on 5xx
