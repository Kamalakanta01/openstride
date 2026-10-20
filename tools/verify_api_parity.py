#!/usr/bin/env python3
"""Verify that the generated API layer is 1:1 with the original Fatmap SDK.

Four independent checks, all mechanical:

  1. JNI symbol parity - our shared object exports exactly the same symbol
     set as the original libFatmapSdk.so.
  2. Resolution - every native method the application's bytecode declares
     resolves to a symbol in our shared object.
  3. Public Java API parity - the compiled Kotlin exposes every public method
     the original Java API declares.
  4. Implementation coverage - how much of the engine is actually implemented.

Checks 1 and 2 need the original binary; they are skipped if it is absent.
Check 3 needs the compiled Kotlin; it is skipped if the build has not run.

Usage:
    python3 tools/verify_api_parity.py
    python3 tools/verify_api_parity.py --original /path/to/libFatmapSdk.so
"""

from __future__ import annotations

import argparse
import json
import os
import pathlib
import re
import subprocess
import sys

REPO = pathlib.Path(__file__).resolve().parent.parent
SPEC = REPO / "spec"
CLASSES = REPO / "app" / "build" / "tmp" / "kotlin-classes" / "debug"
ENGINE_SRC = REPO / "map-engine" / "src" / "engine_api.cpp"
ENGINE_SO = REPO / "map-engine" / "build" / "libfatmap_jni.so"
JNI_SYMBOLS = REPO / "spec" / "fatmap-jni.json"

GREEN, RED, YELLOW, RESET = "\033[32m", "\033[31m", "\033[33m", "\033[0m"


def exported_symbols(library: pathlib.Path) -> set[str]:
    if not library.exists():
        return set()
    out = subprocess.run(
        ["nm", "-D", "--defined-only", str(library)], capture_output=True, text=True
    ).stdout
    return {line.split()[-1] for line in out.splitlines() if "Java_" in line}


def declared_native_symbols() -> set[str]:
    """Every native method the original SDK declared, in JNI-mangled form."""
    with open(SPEC / "fatmap-native-api.json", encoding="utf-8") as fh:
        native = json.load(fh)
    symbols = set()
    for proxy, entry in native.items():
        simple = proxy.split("$")[0].rsplit(".", 1)[-1]
        for method in entry["native_methods"]:
            # JNI mangles '_' as '_1' and '$' as '_00024'.
            mangled = method["name"].replace("_", "_1")
            symbols.add(f"Java_com_fatmap_sdk_api_{simple}_00024CppProxy_{mangled}")
    return symbols


def public_methods_of(simple: str) -> set[str]:
    """The public methods of a compiled class, read from its bytecode.

    The static factories live on the companion object, so that is checked too.
    """
    if not (CLASSES / "com" / "fatmap" / "sdk" / "api" / f"{simple}.class").exists():
        return set()
    pattern = (
        r"\s+(?:public|protected)\s+(?:static\s+)?(?:final\s+)?(.+?)\s+(\w+)\((.*?)\);"
    )
    methods: set[str] = set()
    for name in (simple, f"{simple}$Companion"):
        out = subprocess.run(
            ["javap", "-cp", str(CLASSES), f"com.fatmap.sdk.api.{name}"],
            capture_output=True,
            text=True,
        ).stdout
        methods |= {
            m.group(2) for m in (re.match(pattern, line) for line in out.splitlines()) if m
        }
    return methods


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument(
        "--original",
        default="/home/kamal/Projects/Strave_opensource/work/lib/libFatmapSdk.so",
        help="path to the original libFatmapSdk.so, for the symbol parity check",
    )
    ap.add_argument("--quiet", action="store_true")
    args = ap.parse_args()

    failures = 0
    notes = []

    def report(label: str, ok: bool, detail: str, skipped: bool = False) -> None:
        nonlocal failures
        if skipped:
            notes.append(f"{label}: SKIPPED - {detail}")
            return
        if not ok:
            failures += 1
        if not args.quiet:
            mark = f"{GREEN}PASS{RESET}" if ok else f"{RED}FAIL{RESET}"
            print(f"  [{mark}] {label}: {detail}")

    print("Fatmap API parity\n")

    # ---- 1. symbol parity
    original = pathlib.Path(args.original)
    if original.exists() and ENGINE_SO.exists():
        want = exported_symbols(original)
        got = exported_symbols(ENGINE_SO)
        missing = sorted(want - got)
        extra = sorted(got - want)
        report(
            "1. JNI symbol parity",
            not missing and not extra,
            f"original {len(want)}, ours {len(got)}, "
            f"missing {len(missing)}, extra {len(extra)}",
        )
        for name in missing[:5]:
            print(f"        missing: {name}")
        for name in extra[:5]:
            print(f"        extra:   {name}")
    else:
        report(
            "1. JNI symbol parity",
            True,
            "original library or our build is missing",
            skipped=True,
        )

    # ---- 2. every declared native method resolves
    declared = declared_native_symbols()
    exported = exported_symbols(ENGINE_SO)
    if exported:
        unresolved = sorted(declared - exported)
        report(
            "2. DEX native declarations resolve",
            not unresolved,
            f"{len(declared)} declared, {len(unresolved)} unresolved",
        )
        for name in unresolved[:5]:
            print(f"        unresolved: {name}")
    else:
        report("2. DEX native declarations resolve", True, "no build to check", skipped=True)

    # ---- 3. public Java API parity
    with open(SPEC / "fatmap-java-api.json", encoding="utf-8") as fh:
        public = json.load(fh)
    if CLASSES.exists():
        checked = 0
        missing = []
        for fq, entry in public.items():
            simple = fq.split(".")[-1]
            have = public_methods_of(simple)
            for signature in entry["m"]:
                m = re.match(r"^(?:<init>|([\w$]+))\((.*?)\):", signature)
                if not m or not m.group(1):
                    continue
                name = m.group(1)
                # '$values' is a synthetic accessor the Java compiler emits for
                # enums; Kotlin does not, and nothing can call it.
                if name in ("toString", "hashCode", "equals", "$values"):
                    continue
                checked += 1
                if name not in have:
                    missing.append(f"{simple}.{name}")
        report(
            "3. Public Java API parity",
            not missing,
            f"{checked} methods checked, {len(missing)} missing",
        )
        for name in missing[:5]:
            print(f"        missing: {name}")
    else:
        report("3. Public Java API parity", True, "Kotlin not compiled", skipped=True)

    # ---- 4. implementation coverage
    with open(SPEC / "fatmap-native-api.json", encoding="utf-8") as fh:
        native = json.load(fh)
    if ENGINE_SRC.exists():
        source = ENGINE_SRC.read_text(encoding="utf-8")
        implemented = set(re.findall(r"(\w+Impl)::(\w+)\s*\(", source))
        statics = set(re.findall(r"(\w+)\s+static_(\w+)_(\w+)\s*\(", source))
        total = 0
        done = 0
        for proxy, entry in native.items():
            simple = proxy.split("$")[0].rsplit(".", 1)[-1]
            for method in entry["native_methods"]:
                total += 1
                if (simple + "Impl", method["name"]) in implemented:
                    done += 1
                elif any(s[1] == simple and s[2] == method["name"] for s in statics):
                    done += 1
        report(
            "4. Engine implementation coverage",
            True,
            f"{done}/{total} native methods implemented ({100 * done // max(1, total)}%)",
        )
    else:
        report("4. Engine implementation coverage", True, "no engine source", skipped=True)

    # ---- 5. the generated Kotlin externals match the declared native signatures
    # The JVM links a native call by name AND signature of the declaring class,
    # so a parameter count mismatch passes every name-based check and then
    # crashes on a device with the C++ reading a garbage argument.
    KNOWN = REPO / "app" / "src" / "main" / "kotlin" / "com" / "fatmap" / "sdk" / "api"

    def jni_arg_count(descriptor: str) -> int:
        count = 0
        i = 0
        while i < len(descriptor):
            char = descriptor[i]
            if char == "[":
                i += 1
                continue
            if char == "L":
                i = descriptor.index(";", i) + 1
                count += 1
                continue
            count += 1
            i += 1
        return count

    if KNOWN.exists():
        checked = 0
        mismatched = []
        for proxy, entry in native.items():
            simple = proxy.split("$")[0].rsplit(".", 1)[-1]
            source = (KNOWN / f"{simple}.kt").read_text(encoding="utf-8")
            declared_sigs = dict(
                re.findall(r"external fun (\w+)\(([^)]*)\)", source)
            )
            for method in entry["native_methods"]:
                checked += 1
                params = declared_sigs.get(method["name"])
                if params is None:
                    mismatched.append(f"{simple}.{method['name']} not declared")
                    continue
                got = 0 if not params.strip() else len([p for p in params.split(", ") if p])
                want = jni_arg_count(
                    method["descriptor"][1 : method["descriptor"].index(")")]
                )
                if got != want:
                    mismatched.append(
                        f"{simple}.{method['name']}: {got} params, contract {want}"
                    )
        report(
            "5. Kotlin externals match native signatures",
            not mismatched,
            f"{checked} natives checked, {len(mismatched)} mismatched",
        )
        for name in mismatched[:5]:
            print(f"        mismatched: {name}")
    else:
        report(
            "5. Kotlin externals match native signatures",
            True,
            "generated Kotlin missing",
            skipped=True,
        )

    # ---- 6. the C++ marshalling references methods our compiled classes have
    # JNI marshalling for value classes calls getters and constructors by
    # name AND descriptor; if the two sides disagree the call aborts on a
    # device. The compiled bytecode is the arbiter of what our classes
    # actually declare.
    VALUES_CPP = REPO / "map-engine" / "generated" / "fatmap_jni_values.cpp"
    if VALUES_CPP.exists() and CLASSES.exists():
        source = VALUES_CPP.read_text(encoding="utf-8")

        def expected_calls(simple: str) -> set[tuple[str, str]]:
            start = source.find(f"read_{simple}(JNIEnv* env, jobject obj) {{")
            pairs = set()
            if start >= 0:
                body = source[start : source.find("\n}\n", start)]
                pairs.update(
                    re.findall(r'GetMethodID\(cls, "(\w+)", "([^"]+)"\)', body)
                )
            start = source.find(f"write_{simple}(JNIEnv* env, const {simple}& value) {{")
            if start >= 0:
                body = source[start : source.find("\n}\n", start)]
                ctor = re.search(r'"<init>", "(\([^)]*\)V)"', body)
                if ctor:
                    pairs.add(("<init>", ctor.group(1)))
            return pairs

        def declared_methods(simple: str) -> set[tuple[str, str]]:
            out = subprocess.run(
                ["javap", "-s", "-cp", str(CLASSES), f"com.fatmap.sdk.api.{simple}"],
                capture_output=True,
                text=True,
            ).stdout
            pairs = set()
            for caller, desc in re.findall(
                r"^\s+.*?\s+(\S+)\(.*?\);\s*descriptor: (\S+)", out, re.M
            ):
                name = caller.rsplit(".", 1)[-1]
                name = "<init>" if name == simple else name
                pairs.add((name, desc))
            return pairs

        bad = []
        checked_cls = 0
        for kt in sorted(KNOWN.glob("*.kt")):
            head = kt.read_text(encoding="utf-8")
            if re.search(r"^(interface|enum class) ", head, re.M):
                continue
            simple = kt.stem
            want = expected_calls(simple)
            if not want:
                continue
            checked_cls += 1
            have = declared_methods(simple)
            for name, desc in sorted(want):
                if (name, desc) not in have:
                    bad.append(f"{simple}.{name}{desc}")
        report(
            "6. C++ marshalling matches compiled classes",
            not bad,
            f"{checked_cls} classes checked, {len(bad)} stray references",
        )
        for name in bad[:5]:
            print(f"        stray: {name}")
    else:
        report(
            "6. C++ marshalling matches compiled classes",
            True,
            "not built",
            skipped=True,
        )

    for note in notes:
        print(f"  note: {note}")

    print()
    if failures:
        print(f"{RED}{failures} check(s) failed{RESET}")
        return 1
    print(f"{GREEN}all checks passed{RESET}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
