#!/usr/bin/env python3
"""Extract the public static constants of a package from DEX bytecode.

The Java API contract records a class's fields but not whether they are
static, nor what they are set to. The DEX does know both: the class data
lists the static fields separately, and the class's static values give their
constant values.

This reads the constant values with the SDK's own `dexdump`, which is the
only tool that decodes the DEX static-value encoding without a third-party
library, and merges them into the Java API contract under the key "sf".

Usage:
    python3 tools/dex_static_fields.py dex/*.dex com.fatmap.sdk.api
    python3 tools/dex_static_fields.py dex/*.dex com.fatmap.sdk.api \
        --spec spec/fatmap-java-api.json --write
"""

from __future__ import annotations

import argparse
import json
import pathlib
import re
import shutil
import subprocess
import sys

DEXDUMP = shutil.which("dexdump") or next(
    (str(p) for p in sorted(pathlib.Path.home().glob("Android/Sdk/build-tools/*/dexdump"))),
    None,
)

CLASS = re.compile(r"^\s*Class descriptor\s+: 'L([^;]+);'")
STATIC_FIELD = re.compile(r"^\s*name\s+: '(\w+)'")
VALUE = re.compile(r"^\s*value\s+: (.*)$")


def constants(dexdump: str, dex: str, package: str) -> dict[str, dict[str, str]]:
    """The public static final constants of every class in the package."""
    out: dict[str, dict[str, str]] = {}
    result = subprocess.run(
        [dexdump, "-f", dex], capture_output=True, text=True, errors="replace"
    )
    current: str | None = None
    in_static = False
    pending: str | None = None
    for line in result.stdout.splitlines():
        m = CLASS.match(line)
        if m:
            # The descriptor uses '/' where the package prefix uses '.'.
            current = m.group(1).replace("/", ".")
            in_static = False
            pending = None
            continue
        if current is None or not current.startswith(package):
            continue
        if line.strip().startswith("Static fields"):
            in_static = True
            continue
        if line.strip().startswith("Instance fields"):
            in_static = False
            continue
        if not in_static:
            continue
        m = STATIC_FIELD.match(line)
        if m:
            pending = m.group(1)
            continue
        m = VALUE.match(line)
        if m and pending:
            raw = m.group(1).strip()
            if raw.startswith('"') and raw.endswith('"'):
                out.setdefault(current, {})[pending] = raw[1:-1]
            elif raw.startswith("'") and raw.endswith("'"):
                out.setdefault(current, {})[pending] = raw[1:-1]
            pending = None
    return out


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("dex", nargs="+", help="DEX files to scan")
    ap.add_argument("package", help="package prefix, e.g. com.fatmap.sdk.api")
    ap.add_argument("--spec", help="the Java API contract to merge into")
    ap.add_argument("--write", action="store_true", help="write the spec back")
    args = ap.parse_args()

    if DEXDUMP is None:
        print("dexdump not found; install the Android SDK build tools", file=sys.stderr)
        return 2

    merged: dict[str, dict[str, str]] = {}
    for path in args.dex:
        for cls, fields in constants(DEXDUMP, path, args.package).items():
            merged.setdefault(cls, {}).update(fields)

    if not args.spec:
        print(json.dumps(merged, indent=2, sort_keys=True))
        return 0

    spec_path = pathlib.Path(args.spec)
    spec = json.loads(spec_path.read_text(encoding="utf-8"))
    for cls, fields in merged.items():
        if cls in spec:
            spec[cls]["sf"] = fields
    if args.write:
        spec_path.write_text(
            json.dumps(spec, indent=2, sort_keys=True) + "\n", encoding="utf-8"
        )
    print(f"{len(merged)} classes carry static constants")
    return 0


if __name__ == "__main__":
    sys.exit(main())
