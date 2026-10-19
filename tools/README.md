# Tools

Three parsers written for this project. **Python 3.9+, standard library only, no dependencies.**

They all take a path to an APK you supply. None of them need an Android SDK, and none of them execute the artifact.

---

## `axml.py` — Android binary XML decoder

Android stores `AndroidManifest.xml` and `res/xml/*.xml` in a compiled binary format (AXML). This decodes it to readable XML.

```bash
python3 axml.py com.strava.apk res/xml/network_security_config.xml res/xml/backupscheme.xml
```

```
===== res/xml/network_security_config.xml =====
<network-security-config>
  <debug-overrides>
    <trust-anchors>
      <certificates src="user">
      </certificates>
    </trust-anchors>
  </debug-overrides>
  <domain-config cleartextTrafficPermitted="true">
    <domain includeSubdomains="false">
    </domain>
  </domain-config>
</network-security-config>
```

Resolves `android:` attribute namespaces and decodes string, integer, boolean and float typed values. Resource references (`@0x7f14112f`) are printed unresolved — mapping those needs `arsc.py`.

**Limitation:** element *text* content (e.g. the body of `<domain>`) is not emitted. Recover it from the string pool, or use a full AXML implementation.

---

## `arsc.py` — resource table parser

Parses `resources.arsc` into a resource-id → (type, name, value) map.

```bash
python3 arsc.py com.strava.apk arsc.json
```

```
resources resolved: 24895
global string pool: 12492
types: {127: 23}
```

```json
"2131429887": ["string", "app_name", "Strava"]
```

**Limitations:**
- Resource *names* and *types* are correct for all 24,895 entries.
- *Values* of `string`-type resources are off by one entry — the `string` type stores pool offsets in a layout this parser does not fully model. Non-string values are correct.
- Only the default (unqualified) configuration is used. For an app with per-locale resources, later configuration chunks overwrite earlier ones for the same id.

---

## `derefx.py` — DEX string reference tracer

Finds **which class and which method** reference a given string-pool entry, by walking DEX bytecode and matching `const-string` / `const-string/jumbo` opcodes.

```bash
python3 derefx.py classes4.dex 52144
```

```
LFd/d2$a;  ::  LFd/d2$a;->a   <- const-string "sha256/q61HEYJJK/e1i0DQnyP6R6tGOrRC5MbbsTcI9ev6cZ4="
```

This is how the certificate pin and the hosts it applies to were found — a plain string search finds the pin but not its meaning.

To find the string index for a given literal first:

```bash
python3 - <<'EOF'
import struct
d = open('classes4.dex','rb').read()
n  = struct.unpack_from('<I', d, 56)[0]
off = struct.unpack_from('<I', d, 60)[0]
def uleb(d, p):
    r = s = 0
    while True:
        b = d[p]; p += 1; r |= (b & 0x7f) << s
        if not b & 0x80: return r, p
        s += 7
for i in range(n):
    so = struct.unpack_from('<I', d, off + 4*i)[0]
    _, p = uleb(d, so)
    e = d.index(b'\x00', p)
    s = d[p:e].decode('utf-8', 'replace')
    if 'sha256/q61' in s:
        print(i, s)
EOF
```

**Limitation:** matches only literal `const-string` opcodes. A string assembled at runtime from fragments, or passed as a `const` in a static field, will not be found.

---

## Notes

- All three are **read-only** and never execute the artifact.
- None of them handle APK Signature Scheme verification — use `apksigner verify` for that.
- For anything beyond these three, the REA MCP toolchain (JADX + Ghidra) is far more capable. See `docs/00-method-and-evidence.md`.

## Contributing a new tool

Keep it dependency-free, and make it work on **any** APK — not just the one this project was written against. Add a `--help`, handle truncated files, and document the limitations in the docstring.
