# Contributing

## The clean-room rule comes first

This project is clean-room. It is the rule everything else depends on.

> **Do not copy, transliterate, decompile, or transliterate-by-hand any part of Strava's implementation.**
> Do **not** paste Strava's Kotlin, smali, native code, resources, strings, layouts, or assets into this repository — not in a source file, not in a comment, not in a test fixture, not in a "reference" directory.

**You may use:**
- The interface specification in [`spec/`](spec/) — endpoint shapes, field names, data model, native API signatures
- The factual data in [`data/`](data/) — class names, hosts, paths, symbol names
- The behaviour described in [`docs/`](docs/) — what the app does, from the user's point of view
- Public documentation of Strava's public APIs and public product behaviour

**You may not use:**
- Decompiled output, in any form
- Strava's string resources, layout XML, drawables, or copy
- Strava's class or method bodies, in any language
- Anything you obtained by running a decompiler against a Strava APK

**Why:** Strava's code is copyrighted. Translating it is infringement, and Strava has a history of pursuing people who reimplemented or repackaged its app. A clean-room implementation is also better engineering — you get to design the data model instead of inheriting someone else's workarounds.

**How to stay clean:** implement from `spec/`. If you need a fact that isn't in the spec, **add it to the spec** — derived by analysis, written in your own words, with the evidence noted. Don't open a decompiler while implementing.

If you're unsure whether something is allowed, open an issue and ask before writing the code. Asking costs nothing; a takedown costs the project.

---

## How to contribute

1. **Read [`docs/09-NON-TECHNICAL-GUIDE.md`](docs/09-NON-TECHNICAL-GUIDE.md)** and [`docs/10-local-only-feasibility.md`](docs/10-local-only-feasibility.md)** to understand the scope.
2. **Read [`spec/`](spec/)** — especially `api-contract.md` and `data-model.md`.
3. **Pick a task** from [`app/README.md`](app/README.md) or open an issue.
4. **Branch, commit, open a PR.**

### Pull requests

- One logical change per PR.
- `ktlint` must pass (`./gradlew ktlintCheck`).
- New public API needs a KDoc comment explaining *why*, not what.
- Room schema changes need a migration and a test.
- Anything touching the recording service needs to survive process death — the original app recovers from `BOOT_COMPLETED` and from a null service intent, and so must yours.

### Commit messages

```
<area>: <imperative summary>

<why the change is needed, and what problem it solves>
```

### Adding a dependency

1. Check it's permissively licensed (Apache-2.0, MIT, BSD) — see `PROVENANCE.md` §5.
2. Add it to `NOTICE.md` with its licence.
3. Add it to `app/gradle/libs.versions.toml` with the version, and say where the version came from.
4. **Do not** add anything whose licence forbids redistribution.

### Adding to `spec/`

Spec entries must be **facts with evidence**, written in your own words:

```
### <feature name>

**Source:** <tool + artifact, e.g. "REA inspect_android_method on com.strava.net.token.TokenApi">
**Confidence:** observed | derived | inferred
**Contract:**

    <field> : <type>   # <meaning>
```

If you can't point to evidence, don't add it to the spec. Put it in an issue as a question.

---

## Reporting a security issue

If you find a vulnerability **in this project's code**, open a private security advisory on the repository, or email the maintainer. Please don't open a public issue for an unfixed vulnerability.

If you find a vulnerability **in the Strava app**, report it to Strava directly — not here. This project has no relationship with them and can't act on it.

---

## Code of conduct

Participation is governed by [`CODE_OF_CONDUCT.md`](CODE_OF_CONDUCT.md).
