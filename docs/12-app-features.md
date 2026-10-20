# The app: what it does, and how it maps to the original

**Status: working.** The app builds, installs, runs on a physical device, and
records activities. This document lists what it does, how each part maps to
the original app, and what is deliberately missing.

The guiding rule: **the information architecture is the original's, the data
is the athlete's own, and nothing leaves the device.**

---

## The five screens

The original app's navigation is a bottom bar with a map, a record button, a
training log and the athlete's own profile. This app has the same shape:

| Screen | What it does |
|---|---|
| **Map** | Browse the map, follow a recording in progress, and toggle the personal heatmap |
| **Record** | Start and stop a recording; live time, distance, pace and ascent |
| **Log** | A calendar of the month and the list of activities, filterable by sport |
| **Fitness** | Fitness and freshness, weekly load, personal records, achievements |
| **You** | Profile, gear, goals, local segments, routes, privacy zones, settings |

---

## Feature by feature

### Recording

The original records with the fused location provider and uploads the result
to its servers. This app uses the platform `LocationManager` — so it works on
a phone without Google Play services — and writes the activity straight into
the local database.

Every point is kept, not summarised: the latitude, longitude, altitude and
time streams are stored separately, and the polyline is encoded in Google's
polyline format, which is what the original uses too.

A point that jumps more than a kilometre from the last one is dropped: that is
the receiver losing its fix, not the athlete moving. The distance total
ignores implausible steps for the same reason.

The recording runs in a foreground service with a `location` service type, and
the notification carries the live time and distance. The service is started
again by the system after a process restart with a null intent, which means
"resume the recording or stop quietly" — never "start a new one".

### Activities

List, filter by sport, and a detail screen with the map, the numbers, the
splits and the elevation profile. The original's detail screen also has
comments, kudos and photographs; those need other people, so they are gone.

### Training log and calendar

The original's log is a list with a sport filter. This one is a calendar of
the month with the list underneath, because a month of training reads better
as a shape. The day cells are coloured by load.

### Fitness, load and freshness

The original computes these on its servers from its own models. Here they are
computed on the device from the activities that are stored.

- **Load** — a day's training stress. The original multiplies duration by the
  square of the intensity relative to the athlete's threshold. There is no
  threshold without a power meter or a heart-rate strap, so the intensity is
  measured against the athlete's own median pace over the last thirty days.
  The squaring is what makes a short hard effort count for more than a long
  easy one.
- **Fitness** (CTL) — a 42-day exponential average of the load. Capacity.
- **Fatigue** (ATL) — a 7-day exponential average. The cost of recent work.
- **Form** (TSB) — fitness minus fatigue.
- **Weekly load, streaks, consistency** — the same numbers the original shows
  on its training screens.

The time constants are the original's, so the shape of the chart is
comparable.

### Zones

The original has heart-rate, power and speed zones. Without a strap or a
meter, speed is the only one that can be measured, so the zones here are
relative to the athlete's own best recent speed.

### Personal records

Computed from the stored activities: fastest kilometre, five kilometres and
ten kilometres, longest run, longest ride, biggest climb, most time moving
and highest average speed.

### Achievements

The original awards a large set of badges, many of which depend on other
athletes. These are the ones a single athlete's own history can decide.

### Local segments

Strava's segments are crowdsourced: users mark them, everyone competes on
them, and the leaderboards are the reason people come back. With no
community, a segment is **a stretch of road the athlete has covered more than
once**.

The app finds them by walking two tracks together and looking for the longest
run of samples that stay within 35 metres of each other. A stretch of at
least 300 metres becomes a segment, and every later activity over it is an
effort. The geometry is stored in its own table; the original keeps its
segments on its servers.

### Personal heatmap

The original's heatmap is where the athlete has been. Here it is built from
the activities on the device: every point of every track falls into a cell,
and the cells are coloured by how often they were visited.

### Gear

Bikes and shoes, with the distance computed from the activities that used
them. The original also tracks retiring gear and service history.

### Goals

Weekly, monthly and annual targets, with the progress computed from the
activities. The original shares goals with the community and shows the
leaderboard; that part needs other people.

### Routes

The original's route planner is a rich map editor with turn-by-turn
directions from a routing service. There is no routing service here, so a
route is the shape of an activity the athlete has done: saved, drawn on the
map, and followable by recording over it.

### Privacy zones

The most safety-relevant feature in the original: a circle around a home or
a school that is blanked out of any activity passing through it, with
"hidden start" and "hidden end" zones that keep the start and finish of a
run out of the open. The zones are stored and drawn here. There is nowhere
to publish an activity, but the zones still matter: they are what keeps an
activity's start and end points from sitting in a database unhidden.

---

## What is deliberately missing, and why

| Feature | Why |
|---|---|
| Feed, clubs, challenges, kudos, comments | They exist only to show you other people |
| Global segment leaderboards | Same |
| Server-side heatmaps, POIs, terrain | Strava's private data, not reproducible |
| Turn-by-turn directions | Needs a routing service; there is no local one |
| OAuth, account recovery, friends | There is no account |
| Analytics, crash reporting, A/B tests | There is no server to send them to |
| Strava's orange, its icons, its copy | Their trademarks, not ours |

The full boundary is in [10-local-only-feasibility.md](10-local-only-feasibility.md).

---

## The map

The basemap is drawn by **MapLibre**, using OpenFreeMap's "liberty" style,
which needs no API key and no account. The original's map is the Fatmap
engine, which this project also builds as a 1:1 clean-room reimplementation
(see [11-fatmap-engine-deep-dive.md](11-fatmap-engine-deep-dive.md)) — but
that engine has no GL context, no tile fetching and no rasteriser yet, so it
renders nothing. Until that work lands, the app draws its basemap with
MapLibre and talks to the engine through the generated Fatmap API.

Two map layers are drawn by the app itself: the recorded track, and the
personal heatmap.

---

## The data

Everything is in one SQLite database, through Room. The schema and the
DAOs are in `local/LocalDatabase.kt`; the app's own facade over them is
`data/AppRepository.kt`.

The analysis — fitness, load, records, splits, zones, segments, heatmap — is
in `analysis/`, written as plain Kotlin with no Android types, so it can be
unit tested on the host.

The recording pipeline is in `recording/`, and the user interface is in
`ui/`.
