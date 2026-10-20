#!/usr/bin/env python3
"""Extract the declared native methods of a package from DEX bytecode.

Used to recover the exact native surface of the Fatmap SDK's `CppProxy`
classes: which methods are `native`, their JNI-descriptor signatures, and the
`native` field that carries the C++ pointer.

Reads the DEX format directly, so it needs no tooling beyond the standard
library. Only the class definitions of the requested package are decoded, so
memory use stays proportional to the output rather than to the DEX size.

Usage:
    python3 tools/dex_native_api.py dex/classes3.dex com.fatmap.sdk.api
    python3 tools/dex_native_api.py dex/*.dex com.fatmap.sdk.api --json out.json
"""

from __future__ import annotations

import argparse
import json
import struct
import sys

ACC_NATIVE = 0x0100
ACC_PUBLIC = 0x0001
ACC_STATIC = 0x0008
ACC_ABSTRACT = 0x0400


class Dex:
    """Minimal read-only DEX reader."""

    def __init__(self, path: str):
        self.path = path
        with open(path, "rb") as fh:
            self.data = fh.read()
        d = self.data
        if d[:4] != b"dex\n":
            raise ValueError(f"{path}: not a DEX file")
        (self.string_ids_size, self.string_ids_off) = struct.unpack_from("<II", d, 0x38)
        (self.type_ids_size, self.type_ids_off) = struct.unpack_from("<II", d, 0x40)
        (self.proto_ids_size, self.proto_ids_off) = struct.unpack_from("<II", d, 0x48)
        (self.method_ids_size, self.method_ids_off) = struct.unpack_from("<II", d, 0x58)
        (self.class_defs_size, self.class_defs_off) = struct.unpack_from("<II", d, 0x60)
        self._strings: dict[int, str] = {}

    # -- primitive readers -------------------------------------------------
    def uleb128(self, off: int) -> tuple[int, int]:
        result = shift = 0
        while True:
            byte = self.data[off]
            off += 1
            result |= (byte & 0x7F) << shift
            if not byte & 0x80:
                return result, off
            shift += 7

    # -- tables ------------------------------------------------------------
    def string(self, idx: int) -> str:
        if idx in self._strings:
            return self._strings[idx]
        (data_off,) = struct.unpack_from("<I", self.data, self.string_ids_off + 4 * idx)
        _, p = self.uleb128(data_off)
        end = self.data.index(b"\x00", p)
        # MUTF-8: good enough for the ASCII identifiers we care about.
        text = self.data[p:end].decode("utf-8", "replace")
        self._strings[idx] = text
        return text

    def type_descriptor(self, idx: int) -> str:
        (desc_idx,) = struct.unpack_from("<I", self.data, self.type_ids_off + 4 * idx)
        return self.string(desc_idx)

    def type_list(self, off: int) -> list[str]:
        if off == 0:
            return []
        (size,) = struct.unpack_from("<I", self.data, off)
        out = []
        for i in range(size):
            (tidx,) = struct.unpack_from("<H", self.data, off + 4 + 2 * i)
            out.append(self.type_descriptor(tidx))
        return out

    def proto(self, idx: int) -> str:
        _shorty, return_type_idx, params_off = struct.unpack_from(
            "<III", self.data, self.proto_ids_off + 12 * idx
        )
        params = self.type_list(params_off)
        return "(" + "".join(params) + ")" + self.type_descriptor(return_type_idx)

    def method(self, idx: int) -> tuple[str, str, str]:
        class_idx, proto_idx, name_idx = struct.unpack_from(
            "<HHI", self.data, self.method_ids_off + 8 * idx
        )
        return (
            self.type_descriptor(class_idx),
            self.string(name_idx),
            self.proto(proto_idx),
        )

    # -- class definitions -------------------------------------------------
    def classes(self):
        for i in range(self.class_defs_size):
            off = self.class_defs_off + 32 * i
            class_idx, _flags, _super, _ifaces, _src, _ann, class_data_off, _stat = (
                struct.unpack_from("<IIIIIIII", self.data, off)
            )
            yield self.type_descriptor(class_idx), class_data_off

    def class_data(self, off: int):
        """Yield (method_idx, access_flags) for every method in the class."""
        if off == 0:
            return
        p = off
        static_fields, p = self.uleb128(p)
        instance_fields, p = self.uleb128(p)
        direct_methods, p = self.uleb128(p)
        virtual_methods, p = self.uleb128(p)
        for _ in range(static_fields + instance_fields):
            _, p = self.uleb128(p)  # field_idx_diff
            _, p = self.uleb128(p)  # access_flags
        # method_idx_diff is relative to the previous method in the *same*
        # list; the direct and virtual lists are numbered independently.
        for count in (direct_methods, virtual_methods):
            idx = 0
            for _ in range(count):
                diff, p = self.uleb128(p)
                flags, p = self.uleb128(p)
                _, p = self.uleb128(p)  # code_off
                idx = idx + diff if idx else diff
                yield idx, flags


def collect(dex_path: str, package: str) -> dict:
    dex = Dex(dex_path)
    out: dict[str, dict] = {}
    prefix = "L" + package.replace(".", "/") + "/"
    for descriptor, class_data_off in dex.classes():
        if not descriptor.startswith(prefix):
            continue
        name = descriptor[1:-1].replace("/", ".")
        methods = []
        fields = []
        for method_idx, flags in dex.class_data(class_data_off):
            cls, mname, proto = dex.method(method_idx)
            if cls != descriptor:
                continue
            if flags & ACC_NATIVE:
                methods.append(
                    {
                        "name": mname,
                        "descriptor": proto,
                        "static": bool(flags & ACC_STATIC),
                    }
                )
        if methods:
            out[name] = {"native_methods": methods}
    return out


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("dex", nargs="+", help="DEX files to scan")
    ap.add_argument("package", help="package prefix, e.g. com.fatmap.sdk.api")
    ap.add_argument("--json", help="write the result to this file as JSON")
    args = ap.parse_args()

    merged: dict[str, dict] = {}
    for path in args.dex:
        for name, entry in collect(path, args.package).items():
            merged.setdefault(name, {"native_methods": []})["native_methods"].extend(
                entry["native_methods"]
            )

    for name in sorted(merged):
        natives = merged[name]["native_methods"]
        native_names = {n["name"] for n in natives}
        print(f"\n## {name}  ({len(natives)} native)")
        for m in natives:
            print(f"   {'static ' if m['static'] else ''}{m['name']}{m['descriptor']}")
        if len(native_names) != len(natives):
            print("   !! duplicate native names - overloads present")

    if args.json:
        with open(args.json, "w", encoding="utf-8") as fh:
            json.dump(merged, fh, indent=1, sort_keys=True)
        print(f"\nwrote {args.json}")

    return 0


if __name__ == "__main__":
    sys.exit(main())
