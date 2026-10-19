# Strava on Android — A Plain-English Guide

*Everything in this document is explained without jargon. If you only read one file from this analysis, read this one.*

---

## 1. What you actually have in this file

You gave us a file called `com.strava.apk` and asked us to take it apart completely. Here's the first surprise:

**It isn't really an app file. It's a box containing an app file.**

Think of it like a travel box. The outer box (`com.strava.apk`, about 120 MB) holds:

- **the main app** (87 MB)
- **the graphics and maths engine** for phones with 32-bit chips (26 MB)
- **the plain-English text** of the app (164 KB)
- **the basic artwork** for screens (6 MB)
- **a picture of the app icon** (44 KB)
- **a note listing what's inside** (2 KB)

This packaging style is called an **XAPK**. It's what phone makers and app stores use to ship one download that adapts to your device. It means **you cannot install the outer file directly** — you need the inner app file and its matching pieces. That's a packaging detail, not a problem with your download.

Inside the main app file are **six more program files** (called DEX files). Together they hold **72,470 pieces of code** — about 331,000 individual functions. If you printed all the code out, it would run to millions of lines. This is a genuinely enormous app.

---

## 2. Who made it, and how old is it

| | |
|---|---|
| App name | **Strava** |
| Package name | `com.strava` |
| Version | **456.10** (internal number 12397026) |
| Works on | Android 8.0 and newer (version 26+) |
| Built for | Android 15 (version 35), compiled against Android 16 |
| Roughly built | around **March 2026** |
| Total code weight | ~120 MB downloaded, ~93 MB unpacked |

The build was made with Kotlin and Jetpack Compose — Google's modern way of building Android apps, where screens are described in code rather than drawn in a visual editor. It uses Google's **Hilt** tool for wiring parts together, **Room** for storing data on the phone, and **WorkManager** for scheduled background jobs.

**One genuine oddity:** the file was built with an internal revision number, `6a313c78…`, so it can be traced back to a specific commit in Strava's private source repository. That's normal for a well-run build system, and it means the app can be audited precisely if a question ever arises.

---

## 3. Is this the genuine article? — Yes, with one caveat

**The good news.** The file carries a special digital signature called a **source stamp** that only Google can create, and only when they personally publish an app through the Play Store. We extracted that stamp and checked its fingerprint against the copy embedded in the app itself — they match exactly. That means **this file came from Google Play and has not been altered since**. It is not a repackaged, modded, or pirated version.

**The caveat.** The app's own signing key was not cryptographically checked. In practice this is a formality — the Play stamp already proves provenance — but a truly complete audit would confirm it too.

**A side note.** The file also contains Google's "Android" test certificate (issued in 2020, valid until 2050). That's the certificate Google uses to sign apps delivered straight from the Play Store, so it's expected here.

---

## 4. What the app does, feature by feature

Strava is a social fitness network. At its core: you record a walk, run, ride, swim, or other activity, and it maps it, tracks it, and lets you share it.

**Recording.** The app records GPS, heart rate, and cycling or running sensors in the background while your screen is off. It shows a permanent notification while recording (Android requires this). If the phone reboots or the app is killed, it picks the recording back up.

**Maps.** This is the interesting part. Strava doesn't use Google Maps for its signature terrain view — it built its **own map engine** (more on that below), and also uses Mapbox for two-dimensional maps. You can draw routes, see elevation and gradient profiles, explore "flyover" 3D fly-throughs of a route, and search for places.

**Heatmaps.** Strava's famous heatmaps show where athletes have been. There are several kinds: the **global heatmap** (everyone, everywhere), **nightly** (recent activity, low-traffic times), **weekly** (the last week only), and your own **personal heatmap** (only your own activities, or shared selectively).

**Social.** A feed of your friends' activities, kudos (a "like"), comments, club feeds, and group chats for clubs.

**Challenges and competitions.** Time-limited challenges, group competitions, and leaderboards.

**Your body.** Fitness score, relative effort, VO₂ max estimates, power curves, training load and zones, best efforts, and monthly statistics.

**Connected devices.** Garmin, Fitbit, Wahoo and similar bike computers and watches; Google Fit; and Android's own Health Connect service.

**Clubs and events.** Create a club, run group rides and events, sign waivers, and manage rosters.

**Membership.** A paid subscription with a trial, a cancellation flow, a student plan, and even custom app icons (a Strava subscriber can change the launcher icon to a running, cycling, gravel, hiking or trophy graphic — all 15 variants are in this build).

**Help.** A built-in support system (Zendesk) with articles and ticket submission.

---

## 5. The parts Strava built that nobody else has

### The 3D map engine ("Fatmap")

The largest single piece of code Strava wrote themselves is their own map engine: **10.8 MB** of highly optimised native code — 12% of the app itself, and the biggest individual file in the whole package. We decompiled it and found:

- **27,535 separate functions** inside it
- **312 functions exposed to the rest of the app** — we have the complete list, with names
- It is built on top of **OGRE**, an open-source 3D graphics engine, adapted for phones
- It contains a full **Lua scripting interpreter** (version 5.4.8) so map animations can be programmed rather than hard-coded
- It has its own HTTP client with cookie handling, and can sign requests the way cloud storage providers do
- It is a **stripped** binary — the developers removed all the internal names — but we recovered 263 internal class names from leftover type information, giving a clear picture of its architecture

The exposed functions show exactly what the engine can do: fly a camera along a route in 3D, draw activity lines with gradients, place markers, filter by points of interest, and read terrain elevation.

**Also worth knowing:** this build ships a full **test harness inside the production library** — a fake tile server, screenshot tests, and performance tests. Useful for Strava's own engineers, but it's more machinery in the shipped app than most companies leave in.

### The "Modular UI" system

Strava's home feed isn't hard-coded. Instead, the server sends a description of what to show, and the app renders it. That means Strava can launch new cards, banners, and features in your feed **without releasing a new version of the app**. The code for this is the single largest part of Strava's own code (387 classes), plus another 113 that convert the server's instructions into on-screen elements.

### "Spandex"

Strava's internal name for its own design system — the buttons, avatars, banners, and bottom sheets that make the app look consistent. 56 classes of reusable building blocks.

---

## 6. Who else is in here, and what they get to know

This is the part most people care about. The app bundles **well over 100 third-party libraries**. Here's who they are and what they receive:

| Company | What it does | What it receives |
|---|---|---|
| **Google** (Firebase, Play Services, Maps) | Analytics, push messages, sign-in, maps | App activity, device ID, sign-in identity, Google Fit data |
| **Mapbox** | 2D maps and tiles | Your location, activity type, map-usage telemetry |
| **Sentry** | Crash reports | Crashes, device info, **and recordings of your screen sessions** |
| **Embrace** | Another crash/session monitor | Sessions, **and captured HTTP traffic** |
| **Snowplow** | Analytics platform | Which screens you visit, with location context |
| **Branch.io** | Tracks installs and link clicks | Where you came from, install referrer |
| **Iterable** | Marketing email/push campaigns | Push campaign engagement |
| **Facebook** | Social login (being phased out) | Friends, profile photos, sign-in identity |
| **Zendesk** | Customer support | Support tickets, your email address |
| **Stream.io** | Club chat | Chat messages, media you share, **location you choose to share in a chat** |
| **Google Ads** | Advertising and install attribution | Your advertising ID, install and link-click data |
| **reCAPTCHA** | Bot detection | A token proving you're not a bot |
| **LinkedIn (LITR)** | Media rendering | Media processing only |
| **Rive** | Animated graphics | Asset downloads only |
| **Runna** | Training plans | Plan usage, referrals |
| **Apple WeatherKit** | Weather data | Weather request data |

**Straight answer:** Strava's own servers see essentially everything — your routes, speeds, times, heart rate, who you follow, who follows you. The third parties above get narrower slices. Two of them (Sentry and Embrace) can capture **screens and network traffic** for debugging, which is the most invasive of the lot, and both are switched on in this build.

---

## 7. What the app asks permission for, and why

The app requests **43 permissions**. Most are ordinary and understandable:

- **Location** (fine and approximate) — to record where you are and show you on a map
- **Activity recognition** — to tell walking apart from cycling
- **Photos and video** — to attach pictures to your activities
- **Photo location** — to work out where a photo was taken
- **Camera** — optional
- **Microphone** — for spoken audio cues during recording
- **Contacts** — to find friends who use Strava
- **Bluetooth** — to talk to bike computers and heart-rate straps
- **Health data** — to read and write workouts to Android's Health Connect
- **Notifications** — to tell you about kudos, comments and challenges
- **Background location** — to keep recording with the screen off

Two deserve a second look:

- **`DETECT_SCREEN_CAPTURE`** — the app can tell when you take a screenshot. Usually used to hide sensitive information in the moment, or to flag abuse.
- **`com.android.vending.BILLING`** — the app can buy things (subscriptions) through Google Play, so Google handles the card details and Strava never sees them.

---

## 8. Heatmaps and privacy — how it works

Your personal heatmap is the feature people worry about most, so here is exactly what the code shows.

When Strava serves a heatmap tile, the request includes:
- whose heatmap it is (an athlete ID in the web address)
- **who is allowed to see it** — everyone, only your followers, or only you
- whether your **privacy zones** (places you've marked as private) are respected
- whether your **commutes** (regular journeys) are included
- a **date range** and **sport filter**
- sometimes your **access token**

The important detail: **these visibility rules are enforced on Strava's servers, not on your phone.** Your phone asks for what it wants; the server decides what it gets. That is the safer arrangement — a modified app can't easily widen the scope — but it also means the rules are exactly as good as the server's implementation, and we could not test the server from here.

We also found Strava's **"privacy zones"** feature in the app: you mark an area on the map, and the app asks the server to respect it.

---

## 9. What the app can be tricked into doing

**130 components are "exported"** — meaning any other app on your phone can send them commands. Almost all of these are deep links: `strava://settings/privacy`, `strava://clubs`, `strava://record`, and around 100 more. The practical risk is low (mostly, another app could yank you to a particular Strava screen), but a few deserve attention:

- **Strava's "shared identity" provider** is exported. It is designed to let other apps share your Strava login. Because it's open to any app, exactly what it hands over is worth checking on a real device.
- **The login and OTP screens** can be launched directly by another app.
- **A map-playground screen** and a **"clear the map cache" screen** are exported — the latter can make the app delete cached map data.
- **NFC tags can start a recording** if a matching tag is scanned.

**One thing that is *not* a problem:** unencrypted ("cleartext") web traffic is switched off for every real website. The only exception is `10.0.2.2`, which is the special address Android emulators use to mean "the computer I'm running on" — a leftover for developers.

**A partial extra lock.** The app carries a digital fingerprint ("certificate pin") for two of Strava's servers — the one that serves photos and graphics, and the one that answers its newer "GraphQL" queries. That means, for those two, the app will refuse to talk to a server that doesn't present the exact expected encryption key — a strong defence against anyone trying to intercept those connections by impersonating Strava. The main API server, and all the outside services (crash reporting, maps, chat, support), do **not** have that extra lock and rely on the usual system-wide trust.

---

## 10. Keys and passwords that are visible in the file

Any app that ships with a secret key is shipping that key to everyone. We found these in the file, and you should know what they are:

- **Google Maps key** — used for the small map previews
- **Facebook app ID** and client token — for Facebook sign-in
- **Sentry DSN** — the address crashes are sent to
- **Firebase app ID** and project number — Strava's Google Cloud project
- **Four Mapbox public tokens** (`pk.…`) — for map tiles
- **A Branch.io live key** — for install attribution
- **A Zendesk app ID and OAuth client**
- **Strava's own OAuth client ID** (`com.strava.connect.app`)

**None of these are passwords.** A public API key is like a restaurant's phone number — it's meant to be known, and the servers check *who* is asking, not *whether* you know the number.

**What is notably absent:** no Strava account password, no secret that would let someone log in as you, no private key, and no map-server secret. Those all live on Strava's servers, not in your phone.

---

## 11. Features for developers that shipped in the released app

Strava includes a hidden developer menu, usually called "Super User" inside the app. In this build it contains:

- a **full log of every web request and response** the app makes, viewable on screen
- a viewer for the raw analytics event cache
- a screen to **force a different A/B test group**
- a screen to **toggle your subscription state**
- a language override, including a way to **fake the language header sent to Strava's servers**
- a routing switcher to send requests to **staging servers** instead of production
- a map playground

None of these can be opened by another app — they are not exported. But they are all compiled into the app you downloaded, and on a phone you have full control of (or with a debugging tool), they are all reachable. The HTTP log in particular would show your session tokens and personal data in plain text.

---

## 12. A few small but telling details

- **The app icon changes.** A paid subscriber can swap the icon on their home screen. Fifteen alternate variants are bundled alongside the default: running, cycling, hiking, gravel, road, trophy, medal, pride, kudos, sticker, black, red, teal, local legend, and "xom". All are in this build.
- **Strava has an Easter egg.** There's an activity class literally named `HuevoDePascua` — Spanish for "Easter egg."
- **Only 32-bit phones.** The download contains code for older 32-bit processors only. Newer 64-bit phones will still run it, but a separate 64-bit download exists elsewhere.
- **Only English is included.** The file lists 21 language versions and declares support for 25, but only the English one is actually present. On a non-English phone you'd get English text unless a separate language file is downloaded.
- **Screenshots are detected.** The app knows when you screenshot it.
- **Backups work, mostly.** Android's cloud backup is on, but the main local activity database is deliberately excluded so your activity history isn't copied to Google's servers.

---

## 13. A fair summary

**This is a serious, well-engineered, and essentially unmodified copy of the real Strava app.** The Google Play stamp proves it. The code is hardened in every native library (full memory protection, non-executable stacks, stack overflow guards). Encrypted web traffic is enforced.

**The trade-offs are the normal ones for a large commercial social-fitness app.** It asks for a lot of permissions because recording your movement accurately requires them. It embeds a large number of analytics and crash-reporting services, two of which (Sentry and Embrace) can capture your screen and your network traffic. It contains a visible set of developer tools, including a plain-text network log, that most apps would strip from a release build.

**None of this is a red flag on its own** — it is the standard shape of a large, well-funded consumer app. But if you are deciding whether to install it, the things actually worth your attention are the ones you can act on:

1. **Sentry and Embrace can capture your screen and network traffic.** Turn them off if that bothers you, or use a build that does.
2. **The "shared identity" provider is open to other apps.** Worth testing on your own device if you're security-focused.
3. **Your personal heatmap is served with your access token in the URL.** Understand who can see it before you turn it on.
4. **Strava's servers — not your phone — enforce heatmap privacy.** Your settings are a request to the server, not a lock on the data.
5. **The app is mostly English only** in this particular file.

---

*Compiled from static analysis of `com.strava.apk` (XAPK v2, Strava 456.10, base split SHA-256 `a273c91d…`). The app was never run, no network traffic was captured, and no server was contacted. Everything above is read from the shipped file itself.*
