# Data Model

The on-device data model. This is the part of the original app that already *is* local — it just used to be a cache of a server, rather than the source of truth.

**Source:** derived from static analysis of Strava Android v456.10 — Room database names, DAO class names, and the Thrift model classes.
**Confidence:** the *entity and field names* below are observed. The original schema details (column types, indices, nullability) were not fully recovered; treat this as a design starting point informed by the original, not a faithful clone of it.

---

## 1. Databases

The original app ships these Room databases. The names are observed; the split in this project is a design decision.

| Original database | Contents | In this project |
|---|---|---|
| `strava-database` | activities, athletes, the core graph | `strava.db` |
| `recording-database` | in-progress and unsynced recordings | `recording.db` |
| `challenge-database` | challenges, participations | *dropped* — out of scope |
| `experiments-database` | A/B cohort assignments | `experiments.db` |
| `privacyZonesDatabase` | user-defined private areas | `privacy.db` |
| `recentsDatabase` | recent-search history | `recents.db` |
| `athlete-workouts.db` | workout library | `workouts.db` |
| `map-style-json-database` | cached map styles | `styles.db` |
| `activity-tag-database` | user tags on activities | *merged into* `strava.db` |
| `stream_chat_client.db` | Stream chat state | *dropped* — chat is out of scope |
| `exoplayer_internal.db` | Media3/ExoPlayer playback state | *dropped* — no video player initially |
| `com.strava.dynamicmapmre.offline_maps` | offline map bundles | `offline_maps.db` |
| `videoCache` | cached video | *dropped* |

---

## 2. Core entities

### Athlete

```
id            Long      local identity, not a Strava id
name          String
email         String?   local only; never leaves the device
city          String?
state         String?
country       String?
timezone      String
gender        Gender?
weightKg      Double?
premiumUntil  Instant?  local-only notion; see README scope note
createdAt     Instant
```

### Activity

```
id              Long
athleteId       Long
name            String
description      String?
type             ActivityType      RUN, RIDE, VIRTUALRIDE, WALK, HIKE, SWIM, WORKOUT, ...
startedAt        Instant
elapsedSeconds   Int
movingSeconds    Int
distanceMeters   Double
elevationGainM   Double
elevationLossM   Double
averageSpeedMs   Double
maxSpeedMs       Double
averageHeartRate Int?
maxHeartRate     Int?
calories         Int?
commute          Boolean
trainer          Boolean
manual           Boolean
privacy          Visibility       PUBLIC, PRIVATE  (see note)
gearId           Long?
workoutType      WorkoutType?     for strength workouts
routeId          Long?
startLat/Lng     Double?
endLat/Lng       Double?
polyline         String?           encoded polyline
streams          ActivityStreams?  time-series, stored separately
createdAt        Instant
```

`ActivityType` and `Visibility` are the two enums that matter most.

**Note on `Visibility`.** The original app distinguishes `PUBLIC`, `FOLLOWERS` and `ONLY_ME`, because it has followers. A single-user local app has no followers, so this project collapses the enum to `PUBLIC` / `PRIVATE`. `PRIVATE` means the activity is excluded from heatmap rendering and from any export by default — the same place the original enforces it server-side.

### ActivityStreams

Time-series, stored apart from the activity row because they are large and are read at a different cadence.

```
activityId   Long
type         StreamType    LATITUDE, LONGITUDE, DISTANCE, HEART_RATE, CADENCE, POWER, ALTITUDE, SPEED, GRADE, TEMPERATURE
data         ByteArray     packed samples
resolution   Int          seconds per sample
```

### Route

The original models routes with **Apache Thrift** (`com.strava.geomodels.model.route.thrift.*`: `Route`, `LegacyRoute`, `Polyline`, `ActiveTime`, `CompletionTimePoint`, `DirectionAction`, `Comment`). Thrift was a reasonable choice for a compact wire format, but it is now legacy and adds a code-generation step.

**Recommendation for this project: use a plain JSON or protobuf schema.** Keep the *semantics* — a route is an ordered list of points, with per-point distance, elapsed time, elevation, and grade — and drop the Thrift runtime. The field names below are the ones worth preserving.

```
id             Long
athleteId      Long
name           String
description    String?
distanceMeters Double
elevationGainM Double
estimatedSeconds Int?
polyline       String?          encoded
points         List<RoutePoint>  (stored as a separate table, not a blob)
visibility     Visibility
isPublic       Boolean
createdAt      Instant

RoutePoint
  lat, lng        Double
  distanceMeters  Double
  elapsedSeconds  Int
  elevationMeters Double?
  gradePercent    Double?
  tempoZone       Int?
```

### Segment

```
id                Long
name              String
activityType      ActivityType
distanceMeters    Double
elevationGainM    Double
climbCategory     Int?        0..5, as in the original's local legends
effortCount       Int?
athleteCount      Int?        global popularity — requires other users; nullable locally
starCount         Int?        likewise
localLegend       LocalLegend?
```

**Your own efforts** on a segment are what matters locally. Effort records link a segment to an activity with a time, distance and power data.

### Gear

```
id           Long
athleteId    Long
name         String
brand        String?
model        String?
type         GearType    BIKE, SHOES, OTHER
distanceMeters Double
retiredAt    Instant?
bikes        List<BikeComponent>   nested: frame, groupset, wheels
shoes        List<ShoeComponent>
```

### Goal

```
id            Long
type          GoalType     WEEKLY_DISTANCE, MONTHLY_DISTANCE, ANNUAL_ELEVATION, ...
targetValue   Double
periodStart   LocalDate
periodEnd     LocalDate
```

### TrainingZone

```
id          Long
type        ZoneType    POWER, HEART_RATE, SPEED, CADENCE
zoneNumber  Int        1..5
minValue    Double
maxValue    Double
```

### PrivacyZone

```
id           Long
name        String
centerLat   Double
centerLng   Double
radiusMeters Double
type        ZoneType    PRIVATE, HIDDEN_START, HIDDEN_END, ENTIRE_MAP
```

**Privacy zones are the most safety-relevant local feature.** They must be applied *before* any map render, any export, and any share. In the original app they are enforced server-side for heatmaps; in this project they are enforced locally, which is strictly better because it cannot be bypassed by a modified client.

### UnsyncedActivity

What the recording service writes while a recording is in progress, and what it reads back after a process death.

```
guid        String     stable id, survives restart
athleteId   Long
type        ActivityType
startedAt   Instant
samples     ByteArray  in-flight points
state       RecordingState
```

The original app's recording service (`com.strava.recording.StravaActivityService`) is a **foreground service with `foregroundServiceType="location"`**, recovers from `BOOT_COMPLETED` via `RecoverActivityReceiver`, and handles a **null service intent** as "the process was restarted — resume the most recent unsynced activity or stop". Reproduce that behaviour; it is the part users notice when it breaks.

---

## 3. Derived values (compute locally, don't store)

| Value | How |
|---|---|
| Fitness / Freshness | Time-weighted load from activities over ~6 weeks |
| Relative effort | Normalised power/cadence effort per activity, scaled to your history |
| VO₂ max estimate | From HR + pace + weight + age |
| Power curve | Best mean-maximal powers over 1 s … 5 min |
| Training load (TSS/CTL/ATL/FTP) | From streams |
| Monthly stats | Aggregation over activities |
| Streaks | Consecutive days with an activity |
| Segments matched | Geometric intersection of your route with the segment polyline |

The original app computes these from the local database after each activity sync. Same approach here: compute on write, cache, invalidate on activity change.

---

## 4. Migrations

Room migrations must be explicit and tested. The original app's `OfflineMapDatabaseMigrationsKt` shows they maintain a migration chain rather than using destructive fallback — do the same.

Never use `fallbackToDestructiveMigration()` on a database holding the user's training history.
