# NOTICE

## This work

OpenStride — a clean-room, open-source reimplementation of the Strava Android app, together with a static-analysis toolkit and interface specification.

Copyright the contributors to this repository.

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE).

## Provenance and exclusions

This repository contains **no Strava source code, no Strava binaries, and no Strava assets.** The complete statement of what is included, what is excluded, and how to regenerate the excluded material is in [PROVENANCE.md](PROVENANCE.md).

The `spec/` and `data/` directories contain *factual interface descriptions* — class names, method signatures, endpoint path shapes, field names, hostnames, and counts — derived by static analysis of an artifact the reader supplies. They are intended to permit independent reimplementation. They are not a substitute for the original work, and no part of the original implementation is reproduced here.

## Trademarks

"Strava" and the Strava logo are trademarks of Strava, Inc. This project is not affiliated with, endorsed by, sponsored by, or approved by Strava, Inc. It is an independent, clean-room project that documents an interface for interoperability and study purposes and offers an original alternative implementation.

Third-party product and company names referenced in the analysis (Mapbox, Sentry, Embrace, Stream, Zendesk, Branch, Iterable, Snowplow, Rive, LITR, Garmin, Fitbit, Firebase, Google Play Services, Facebook, reCAPTCHA, Runna, WeatherKit) are the trademarks of their respective owners and are used for identification only.

## Third-party components referenced by the analysis

The analysis in `docs/02-dependencies.md` catalogues the components that Strava's application bundles. **None of them are included in this repository.** They remain under their own licences, held by their respective copyright holders.

The following licence texts were recovered verbatim from the analysed artifact's `META-INF/` directory and are reproduced in `docs/02-dependencies.md` for attribution purposes:

| Component | Licence |
|---|---|
| RxJava | Apache-2.0 (as stated in `META-INF/COPYRIGHT`) |
| Eclipse Jakarta Dependency Injection | Apache-2.0 (as stated in `META-INF/NOTICE.md`) |
| AndroidX `core-location-altitude-external-protobuf` | BSD-3-Clause |
| AndroidX `health/connect/connect-client-external-protobuf` | BSD-3-Clause |
| 10 × `androidx.*/LICENSE.txt` | Apache-2.0 |

All other licences noted in the analysis are marked **"not evidenced in artifact"** — they are inferences from known upstream projects and are not reproductions of licence text.

### Components that are proprietary and are not options for reuse

The Strava Fatmap SDK, Mapbox Maps SDK v11, the Facebook SDK, the Embrace SDK, the Branch SDK and the Garmin FIT SDK are distributed under proprietary terms. They are **not** included here and must not be added. See `PROVENANCE.md` §5.

## No warranty

This project is provided "AS IS", without warranties or conditions of any kind, as stated in Sections 7 and 8 of the Apache-2.0 licence.
