#!/usr/bin/env python3
"""Generate the 1:1 Fatmap-compatible API layer from the extracted contract.

Inputs (both produced by earlier analysis steps):
    spec/fatmap-java-api.json    public Java API: 130 classes, 823 methods
    spec/fatmap-native-api.json  native API: 30 CppProxy classes, 312 methods

Outputs:
    app/src/main/kotlin/com/fatmap/sdk/api/<Class>.kt   the Kotlin API
    map-engine/generated/fatmap_types.h                 C++ value structs + enums
    map-engine/generated/fatmap_api.h                   C++ API interfaces
    map-engine/generated/fatmap_jni.cpp                 312 JNI entry points

The native entry points carry the same names as the original library, so the
resulting shared object is a drop-in replacement at the JNI boundary:
`Java_com_fatmap_sdk_api_TerrainEngine_00024CppProxy_native_1setServerConfig`.

Everything here is derived from the *interface* only: class names, method
names, parameter types and field names. No implementation is copied.
"""

from __future__ import annotations

import argparse
import json
import os
import re
import shutil
import sys

PACKAGE = "com.fatmap.sdk.api"
JAVA_PKG_PATH = PACKAGE.replace(".", "/")

# java type -> (descriptor, Kotlin type, C++ type, JNI getter, JNI constructor)
SCALARS = {
    "double": ("D", "Double", "double", "GetDoubleValue", "NewDouble"),
    "float": ("F", "Float", "float", "GetFloatValue", "NewFloat"),
    "int": ("I", "Int", "int32_t", "GetIntValue", "NewInt"),
    "long": ("J", "Long", "int64_t", "GetLongValue", "NewLong"),
    "short": ("S", "Short", "int16_t", "GetShortValue", "NewShort"),
    "byte": ("B", "Byte", "int8_t", "GetByteValue", "NewByte"),
    "boolean": ("Z", "Boolean", "bool", "GetBooleanValue", "NewBoolean"),
}

# Boxed class name -> the primitive it wraps.
BOXED_BASE = {
    "Float": "float",
    "Double": "double",
    "Long": "long",
    "Integer": "int",
    "Boolean": "boolean",
    "Short": "short",
    "Byte": "byte",
}

BOXED_KOTLIN = {
    "java.lang.Float": "Float?",
    "java.lang.Double": "Double?",
    "java.lang.Long": "Long?",
    "java.lang.Integer": "Int?",
    "java.lang.Boolean": "Boolean?",
    "java.lang.String": "String",
}

ARRAYLIST = re.compile(r"^java\.util\.ArrayList<(.+)>$")
ENUMSET = re.compile(r"^java\.util\.EnumSet<(.+)>$")
RENAME = re.compile(r"^f\d{5}")


# --------------------------------------------------------------- Java types


class Type:
    """One parameter or return type, resolved across all four languages."""

    def __init__(self, java: str, cat: "Catalog", as_field: bool = False,
                 as_return: bool = False):
        self.java = java.strip()
        self.cat = cat
        self.kind = "primitive"
        self.element = None
        self.as_field = as_field
        self.as_return = as_return

        if self.java.endswith("[]"):
            base = self.java[:-2]
            if base == "byte":
                self.kind = "bytearray"
                self.desc = "[B"
                self.kotlin = "ByteArray"
                self.cpp = "std::vector<uint8_t>"
                self.read = "jni::read_byte_array(env, {ARG})"
                self.write = "jni::write_byte_array(env, {VALUE})"
                return
            if base.startswith(PACKAGE + "."):
                simple = base[len(PACKAGE) + 1 :]
                self.kind = "array"
                self.simple = simple
                self.desc = f"[L{JAVA_PKG_PATH}/{simple};"
                self.kotlin = f"Array<{simple}>"
                self.cpp = f"std::vector<{simple}>"
                element = Type(base, cat)
                self.read = (
                    f"jni::read_object_array<{self.cpp}>({{ENV}}, {{ARG}}, "
                    f"[](JNIEnv* e, jobject o) "
                    f"{{ return {_subst(element.read, 'e', 'o')}; }})"
                )
                self.write = (
                    f"jni::write_object_array<{self.cpp}>({{ENV}}, {{VALUE}}, "
                    f"[](JNIEnv* e, const {self.cpp}& v) "
                    f"{{ return {_subst(element.write, 'e', 'v', 'v')}; }})"
                )
                return

        if self.java == "void":
            self.kind = "void"
            self.desc, self.kotlin, self.cpp = "V", "Unit", "void"
            self.read = ""
            self.write = ""
            return

        if self.java in SCALARS:
            d, k, c, getter, ctor = SCALARS[self.java]
            self.desc, self.kotlin, self.cpp = d, k, c
            # A primitive arrives as a JNI scalar: no conversion needed.
            self.read = "{ARG}"
            self.write = "{VALUE}"
            self.getter = getter
            return

        if self.java in BOXED_KOTLIN:
            base = self.java.rsplit(".", 1)[1]
            if base == "String":
                self.kind = "string"
                self.desc = "Ljava/lang/String;"
                self.kotlin = "String"
                self.cpp = "std::string"
                self.read = "jni::read_string({ENV}, {ARG})"
                self.write = "jni::write_string({ENV}, {VALUE})"
                return
            self.kind = "boxed"
            prim = BOXED_BASE[base]
            self.desc = f"Ljava/lang/{base};"
            self.kotlin = BOXED_KOTLIN[self.java]
            self.cpp = f"std::optional<{SCALARS[prim][2]}>"
            self.read = f"jni::read_boxed_{prim}({{ENV}}, {{ARG}})"
            self.write = f"jni::write_boxed_{prim}({{ENV}}, {{VALUE}})"
            return

        m = ARRAYLIST.match(self.java)
        if m:
            self.kind = "list"
            self.element = Type(m.group(1), cat)
            self.desc = "Ljava/util/ArrayList;"
            self.kotlin = f"MutableList<{self.element.kotlin}>"
            self.cpp = f"std::vector<{self.element.cpp}>"
            self.read = (
                f"jni::read_list<{self.element.cpp}>({{ENV}}, {{ARG}}, "
                f"[](JNIEnv* e, jobject o) "
                f"{{ return {_subst(self.element.read, 'e', 'o')}; }})"
            )
            self.write = (
                f"jni::write_list<{self.element.cpp}>({{ENV}}, {{VALUE}}, "
                f"[](JNIEnv* e, const {self.element.cpp}& v) "
                f"{{ return {_subst(self.element.write, 'e', 'v', 'v')}; }})"
            )
            return

        m = re.match(r"^java\.util\.(HashMap|LinkedHashMap|TreeMap)<(.+?),\s*(.+)>$", self.java)
        if m:
            self.kind = "map"
            self.element = Type(m.group(3), cat)
            self.desc = "Ljava/util/HashMap;"
            self.kotlin = f"MutableMap<String, {self.element.kotlin}>"
            self.cpp = f"std::map<std::string, {self.element.cpp}>"
            self.read = (
                f"jni::read_map<{self.element.cpp}>({{ENV}}, {{ARG}}, "
                f"[](JNIEnv* e, jobject o) {{ return {_subst(self.element.read, 'e', 'o')}; }})"
            )
            self.write = (
                f"jni::write_map<{self.element.cpp}>({{ENV}}, {{VALUE}}, "
                f"[](JNIEnv* e, const {self.element.cpp}& v) "
                f"{{ return {_subst(self.element.write, 'e', 'v', 'v')}; }})"
            )
            return

        m = ENUMSET.match(self.java)
        if m:
            self.kind = "enumset"
            self.element = Type(m.group(1), cat)
            self.desc = "Ljava/util/EnumSet;"
            self.kotlin = f"java.util.EnumSet<{self.element.kotlin}>"
            self.cpp = f"std::set<{self.element.cpp}>"
            self.read = (
                f"jni::read_enumset<{self.element.cpp}>({{ENV}}, {{ARG}}, "
                f"[](JNIEnv* e, jobject o) "
                f"{{ return {_subst(self.element.read, 'e', 'o')}; }})"
            )
            self.write = (
                f"jni::write_enumset<{self.element.cpp}>({{ENV}}, {{VALUE}}, "
                f"[](JNIEnv* e, const {self.element.cpp}& v) "
                f"{{ return {_subst(self.element.write, 'e', 'v', 'v')}; }})"
            )
            return

        if self.java.startswith(PACKAGE + "."):
            simple = self.java[len(PACKAGE) + 1 :]
            self.desc = f"L{JAVA_PKG_PATH}/{simple};"
            self.simple = simple
            if simple in cat.value_names:
                self.kind = "value"
                self.kotlin = simple
                self.cpp = simple
                self.read = f"jni::read_{simple}({{ENV}}, {{ARG}})"
                self.write = f"jni::write_{simple}({{ENV}}, {{VALUE}})"
            elif simple in cat.proxy_names:
                self.kind = "proxy"
                self.kotlin = simple
                self.cpp = f"{simple}Impl*" if as_return else "jni::Opaque"
                self.read = "jni::read_opaque({ENV}, {ARG})"
                self.write = "jni::write_opaque({ENV}, {VALUE})"
            elif simple in cat.enum_names:
                self.kind = "enum"
                self.kotlin = simple
                self.cpp = simple
                self.read = f"jni::read_{simple}({{ENV}}, {{ARG}})"
                self.write = f"jni::write_{simple}({{ENV}}, {{VALUE}})"
            else:
                self.kind = "opaque"
                self.kotlin = simple
                self.cpp = "jni::Opaque"
                self.read = "jni::read_opaque({ENV}, {ARG})"
                self.write = "jni::write_opaque({ENV}, {VALUE})"
            return

        self.kind = "opaque"
        self.simple = self.java
        self.desc = "L" + self.java.replace(".", "/") + ";"
        self.kotlin = self.java
        self.cpp = "jni::Opaque"
        self.read = "jni::read_opaque({ENV}, {ARG})"
        self.write = "jni::write_opaque({ENV}, {VALUE})"


def subst(expr: str, env: str = "env", arg: str = "arg", value: str = "value") -> str:
    """Bind the placeholders in a marshalling expression."""
    return (expr.replace("{ENV}", env).replace("{ARG}", arg).replace("{VALUE}", value))


def read_with(t: "Type", expr: str, env_var: str = "env") -> str:
    """The read expression for `expr` instead of the default argument."""
    return subst(t.read, env_var, expr)


def _subst(expr: str, env_var: str, arg_var: str, value_var: str = "value") -> str:
    """Rebind a marshalling expression to a lambda's own parameters."""
    return subst(expr, env_var, arg_var, value_var)


def _jni_param(t, name: str) -> str:
    """The JNI parameter declaration for one argument."""
    if t.kind == "primitive":
        return f"{SCALARS[t.java][2]} {name}"
    if t.kind == "bytearray":
        return f"jbyteArray {name}"
    return f"jobject {name}"


def split_parameters(text: str) -> list[str]:
    """Split a parameter list on commas that are not inside <>."""
    out = []
    depth = 0
    current = ""
    for ch in text:
        if ch == "<":
            depth += 1
        elif ch == ">":
            depth -= 1
        if ch == "," and depth == 0:
            out.append(current)
            current = ""
            continue
        current += ch
    if current.strip():
        out.append(current)
    return out


def parse_signature(sig: str, cat: "Catalog", as_field: bool = False,
                    as_return: bool = False):
    """'flyTo(double, double): void' -> ('flyTo', [Type...], Type)."""
    m = re.match(r"^(?:<init>|([\w$]+))\((.*?)\):\s*(.+)$", sig)
    if not m:
        return None
    name = m.group(1) or "<init>"
    raw = [p.strip() for p in split_parameters(m.group(2)) if p.strip()]
    return (name, [Type(p, cat, as_field) for p in raw],
            Type(m.group(3), cat, as_field, as_return))


def descriptor_of(params, ret) -> str:
    return "(" + "".join(p.desc for p in params) + ")" + ret.desc


# ------------------------------------------------------------- normalisation


def normalise_field(name: str) -> str:
    """Undo R8's clash-rename prefix: f70446DE -> DE, f70443mA -> mA."""
    return RENAME.sub("", name)


def property_of(field: str) -> str:
    f = normalise_field(field)
    if f.startswith("m") and len(f) > 1 and f[1].isupper():
        return f[1].lower() + f[2:]
    return f


def getter_of(field: str) -> str:
    p = property_of(field)
    return "get" + p[0].upper() + p[1:]


def jni_mangle(class_fq: str, method: str) -> str:
    """JNI's name mangling, so the symbols match the original library.

    The order matters: escape the special characters in the binary name
    first, then turn the '/' separators into '_'.
    """
    binary = class_fq.replace(".", "/")
    binary = binary.replace("_", "_1").replace("$", "_00024")
    binary = binary.replace(";", "_2").replace("[", "_3")
    mname = method.replace("_", "_1").replace(";", "_2").replace("[", "_3")
    return "Java_" + binary.replace("/", "_") + "_" + mname


# ------------------------------------------------------------------ catalog


class Catalog:
    def __init__(self, pub: dict, nat: dict):
        self.pub = pub
        self.nat = nat
        self.simple = {fq: fq.rsplit(".", 1)[-1] for fq in pub}
        self.kind: dict[str, str] = {}
        for fq, entry in pub.items():
            self.kind[fq] = self._classify(entry)
        self.value_names = {self.simple[fq] for fq in pub if self.kind[fq] == "value"}
        self.enum_names = {self.simple[fq] for fq in pub if self.kind[fq] == "enum"}
        self.proxy_names = {
            fq.split("$")[0].rsplit(".", 1)[-1] for fq in nat
        }
        # Interfaces the application implements rather than constructs.
        self.interface_names = {
            self.simple[fq] for fq in pub if self.kind[fq] == "interface"
        }

    @staticmethod
    def _classify(entry) -> str:
        methods = entry.get("m", [])
        names = set()
        ctor_params = []
        for m in methods:
            mm = re.match(r"^(?:<init>|([\w$]+))\((.*?)\):", m)
            if not mm:
                continue
            name = mm.group(1) or "<init>"
            names.add(name)
            if name == "<init>":
                ctor_params = [p for p in mm.group(2).split(",") if p.strip()]
        if "values" in names and "valueOf" in names:
            return "enum"
        if ctor_params:
            return "value"
        if entry.get("f"):
            return "value"
        return "interface"

    def value_class(self, fq: str) -> dict:
        """The properties of a value class, in declaration order."""
        entry = self.pub[fq]
        getters = {}
        for m in entry.get("m", []):
            parsed = parse_signature(m, self, as_field=True)
            if parsed and parsed[0].startswith("get") and not parsed[1]:
                getters[parsed[0]] = parsed[2]
        props = []
        for f in entry.get("f", []):
            fname = f.split(":", 1)[0]
            g = getter_of(fname)
            if g in getters:
                props.append((property_of(fname), getters[g], g))
        return props

    def ctor_order(self, fq: str):
        """Constructor parameters, matched to properties by type then order."""
        entry = self.pub[fq]
        ctor = None
        for m in entry.get("m", []):
            if m.startswith("<init>("):
                parsed = parse_signature(m, self)
                if parsed and parsed[1]:
                    ctor = parsed
                    break
        if ctor is None:
            return []
        remaining = list(self.value_class(fq))
        out = []
        for t in ctor[1]:
            pick = None
            for i, (p, rt, g) in enumerate(remaining):
                if rt.java == t.java:
                    pick = i
                    break
            if pick is None:
                for i, (p, rt, g) in enumerate(remaining):
                    if rt.kind == t.kind:
                        pick = i
                        break
            if pick is None:
                out.append(("value", t))
                continue
            p, rt, g = remaining.pop(pick)
            out.append((p, t))
        return out


# -------------------------------------------------------------------- Kotlin


def unique_names(types) -> list[str]:
    """Kotlin forbids duplicate parameter names in one function."""
    bases = []
    for i, t in enumerate(types):
        if t.kind == "value":
            bases.append("v" + t.simple[0].lower() + t.simple[1:])
        elif t.kind == "list":
            bases.append("items")
        elif t.kind == "enumset":
            bases.append("flags")
        elif t.kind == "boxed":
            bases.append("opt")
        elif t.kind == "string":
            bases.append("text")
        elif t.kind == "enum":
            bases.append("v" + t.simple[0].lower() + t.simple[1:])
        elif t.kind == "primitive":
            bases.append({"double": "d", "float": "f", "int": "i",
                          "long": "l", "boolean": "flag"}.get(t.java, "a"))
        else:
            bases.append("obj")
    seen: dict[str, int] = {}
    out = []
    for i, b in enumerate(bases):
        if b in seen:
            seen[b] += 1
            out.append(f"{b}{seen[b]}")
        else:
            seen[b] = 0
            out.append(b)
    return out


def gen_enum(simple: str, entry) -> str:
    consts = [
        normalise_field(f.split(":", 1)[0])
        for f in entry.get("f", [])
        if f.split(":", 1)[0] != "$VALUES"
    ]
    body = ",\n    ".join(consts)
    return f"""package {PACKAGE}

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
enum class {simple} {{
    {body}
}}
"""


def gen_interface(simple: str, entry, cat: Catalog) -> str:
    lines = []
    for m in entry.get("m", []):
        if m.startswith("<init>") or "$values" in m or m.startswith("<clinit>"):
            continue
        parsed = parse_signature(m, cat)
        if not parsed:
            continue
        name, params, ret = parsed
        if name in ("toString", "hashCode", "equals", "getClass"):
            continue
        names = unique_names(params)
        p = ", ".join(f"{n}: {kotlin_for(t, cat)}" for n, t in zip(names, params))
        r = "Unit" if ret.java == "void" else kotlin_for(ret, cat, "param")
        lines.append(f"    fun {name}({p}): {r}")
    body = "\n".join(lines)
    return f"""package {PACKAGE}

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
interface {simple} {{
{body}
}}
"""


def kotlin_for(t: "Type", cat: "Catalog", position: str = "param") -> str:
    """The Kotlin type, nullable when the application cannot supply it.

    A parameter of a type the application cannot construct may be null: the
    original SDK treats its listeners as optional. A *return* value from the
    engine is never null, except for an interface the application itself
    implements.
    """
    if t.kind in ("value", "enum", "primitive", "string", "boxed", "list", "enumset"):
        return t.kotlin
    simple = getattr(t, "simple", "")
    # A proxy class wins over the "interface" classification: some classes are
    # both (EngineConfiguration has no fields, so it classifies as an interface,
    # but the engine provides it).
    if simple in cat.proxy_names:
        # The engine provides these. An application-implemented interface may
        # not be able to supply one, so those stay nullable.
        if position == "proxy_return":
            return t.kotlin
        return t.kotlin + "?"
    if simple in cat.interface_names:
        return t.kotlin + "?"
    return t.kotlin


def _property_decl(prop: str, java_getter: str, type: "Type") -> str:
    """One `val` property, with a pinned JVM getter name when Kotlin's
    convention would not produce the Java one."""
    if prop.startswith("is") and len(prop) > 2 and prop[2].isupper():
        kotlin_getter = prop
    else:
        kotlin_getter = "get" + prop[0].upper() + prop[1:]
    annotation = "" if kotlin_getter == java_getter else f'@get:JvmName("{java_getter}") '
    return f"{annotation}val {prop}: {type.kotlin}"


def gen_value(simple: str, fq: str, cat: Catalog) -> str:
    props = cat.value_class(fq)
    order = cat.ctor_order(fq)
    getter_name = {p: g for p, rt, g in props}
    decl = ",\n    ".join(
        _property_decl(p, getter_name.get(p, "get" + p[0].upper() + p[1:]), t)
        for p, t in order
    )
    args = ", ".join(f"{p}" for p, _ in order)
    inter = ", ".join(f"{p}=${{{p}}}" for p, _ in order)
    return f"""package {PACKAGE}

/** Generated from the Fatmap SDK interface contract. Do not edit by hand. */
class {simple}(
    {decl}
) {{
    override fun toString(): String = "{simple}({inter})"
}}
"""


def match_native(public_name, public_desc, natives):
    """Find the native method backing a public method."""
    for nm in natives:
        d = nm["descriptor"]
        close = d.index(")")
        stripped = ("(" + d[2:close] + ")" + d[close + 1 :]) if d[1] == "J" else d
        if stripped != public_desc:
            continue
        for cand in ("native_" + public_name, public_name):
            if nm["name"] == cand:
                return nm
    return None


def gen_proxy(simple: str, fq: str, cat: Catalog, natives) -> str:
    delegations = []
    externals = []
    statics = []
    for m in cat.pub[fq].get("m", []):
        parsed = parse_signature(m, cat)
        if not parsed:
            continue
        name, params, ret = parsed
        if name in ("<init>", "toString", "hashCode", "equals"):
            continue
        nm = match_native(name, descriptor_of(params, ret), natives)
        if nm is None:
            continue
        native_name = nm["name"]
        names = unique_names(params)
        psig = ", ".join(f"{n}: {kotlin_for(t, cat)}" for n, t in zip(names, params))
        r = "Unit" if ret.java == "void" else kotlin_for(ret, cat, "proxy_return")
        # A native with no handle is static: it belongs to the companion.
        if nm["descriptor"].startswith("(J"):
            delegations.append(
                f"    fun {name}({psig}): {r} = mProxy.{native_name}({', '.join(names)})"
            )
        else:
            statics.append(
                f"        fun {name}({psig}): {r} = CppProxy(0L).{native_name}({', '.join(names)})"
            )
        externals.append(
            f"        external fun {native_name}({psig}): {r}"
        )
    ext = "\n".join(externals + ["        external fun nativeDestroy()"])
    dele = "\n".join(delegations)
    static_block = "\n".join(statics)

    # Assemble the body so that empty blocks do not leave double blank lines.
    blocks = [
        "    private val mProxy = CppProxy(handle)",
        "    /** The native half of the API. One instance per [ " + simple + " ]. */\n"
        "    class CppProxy internal constructor(private val mNativeHandle: Long) {\n"
        + ext + "\n    }",
    ]
    if dele:
        blocks.append(dele)
    if static_block:
        blocks.append(
            "    companion object {\n" + static_block + "\n    }"
        )
    blocks.append("    override fun close() {\n        mProxy.nativeDestroy()\n    }")
    body = "\n\n".join(blocks)

    return f"""package {PACKAGE}

/**
 * Generated from the Fatmap SDK interface contract. Do not edit by hand.
 *
 * The nested [CppProxy] carries the native handle and declares the `external`
 * functions, so the JNI symbol names match the original library exactly.
 */
class {simple} internal constructor(handle: Long) : AutoCloseable {{
{body}
}}
"""


# ------------------------------------------------------------------------ C++


def gen_cpp_types(cat: Catalog) -> str:
    """The C++ value structs and enums, in dependency order."""
    out = [
        "// Generated from the Fatmap SDK interface contract. Do not edit by hand.",
        "#pragma once",
        "",
        "#include <cstdint>",
        "#include <optional>",
        "#include <set>",
        "#include <string>",
        "#include <vector>",
        "",
        '#include "fatmap_jni_types.h"',
        "",
        "namespace fatmap {",
        "",
    ]

    # Enums first: the structs use them by value.
    for fq in sorted(cat.pub):
        if cat.kind[fq] != "enum":
            continue
        simple = cat.simple[fq]
        consts = [
            normalise_field(f.split(":", 1)[0])
            for f in cat.pub[fq].get("f", [])
            if f.split(":", 1)[0] != "$VALUES"
        ]
        out.append(f"enum class {simple} {{")
        out.append("    " + ",\n    ".join(consts))
        out.append("};")
        out.append("")

    value_classes = [
        fq for fq in sorted(cat.pub) if cat.kind[fq] == "value"
    ]

    # The structs hold each other by value, so they must be defined in
    # dependency order.
    deps: dict[str, set[str]] = {}
    for fq in value_classes:
        simple = cat.simple[fq]
        needed: set[str] = set()
        fields = cat.value_class(fq)
        if not fields:
            fields = []
        for f in cat.pub[fq].get("f", []):
            jtype = f.split(":", 1)[1]
            inner = jtype
            m = ARRAYLIST.match(jtype)
            if m:
                inner = m.group(1)
            if inner.startswith(PACKAGE + "."):
                other = inner[len(PACKAGE) + 1 :]
                if other in cat.value_names and other != simple:
                    needed.add(other)
        for p, rt, g in cat.value_class(fq):
            if rt.kind == "value" and rt.simple != simple:
                needed.add(rt.simple)
        deps[simple] = needed

    ordered: list[str] = []
    pending = {cat.simple[fq]: set(deps[cat.simple[fq]]) for fq in value_classes}
    while pending:
        ready = sorted(name for name, need in pending.items() if not (need - set(ordered)))
        if not ready:
            # A cycle: break it deterministically.
            ready = [sorted(pending)[0]]
        for name in ready:
            ordered.append(name)
            pending.pop(name)

    by_simple = {cat.simple[fq]: fq for fq in value_classes}
    value_classes = [by_simple[name] for name in ordered]

    for fq in value_classes:
        simple = cat.simple[fq]
        props = cat.value_class(fq)
        out.append(f"struct {simple} {{")
        if props:
            for p, rt, g in props:
                out.append(f"    {rt.cpp} {p};")
        else:
            # A class whose fields are all constants still needs a type.
            for f in cat.pub[fq].get("f", []):
                fname, jtype = f.split(":", 1)
                out.append(
                    f"    {Type(jtype, cat, as_field=True).cpp} {property_of(fname)};"
                )
        out.append("};")
        out.append("")

    out.append("}  // namespace fatmap")
    out.append("")
    return "\n".join(out)


def gen_cpp_api(cat: Catalog) -> str:
    out = [
        "// Generated from the Fatmap SDK interface contract. Do not edit by hand.",
        "#pragma once",
        "",
        "#include <cstdint>",
        "#include <optional>",
        "#include <set>",
        "#include <string>",
        "#include <vector>",
        "",
        '#include "fatmap_types.h"',
        '#include "fatmap_jni_types.h"',
        '#include "fatmap_proxy.h"',
        '#include "fatmap_statics.h"',
        '#include "fatmap_handles.h"',
        "",
        "namespace fatmap {",
        "",
    ]
    for fq in sorted(cat.nat):
        simple = fq.split("$")[0].rsplit(".", 1)[-1]
        natives = cat.nat[fq]["native_methods"]
        out.append(f"// ---- {simple} " + "-" * max(4, 58 - len(simple)))
        out.append(f"class {simple}Api {{")
        out.append("public:")
        out.append(f"    virtual ~{simple}Api() = default;")
        out.append("    virtual void nativeDestroy() {}")
        for sig in api_signatures(cat, fq):
            name, params, ret = sig
            ps = ", ".join(f"{t.cpp} a{i}" for i, t in enumerate(params))
            r = "void" if ret.java == "void" else ret.cpp
            body = " {}" if ret.java == "void" else " { return {}; }"
            out.append(f"    virtual {r} {name}({ps}){body}")
        out.append("};")
        out.append("")
    out.append("}  // namespace fatmap")
    out.append("")
    return "\n".join(out)


def api_signatures(cat: Catalog, proxy_fq: str):
    """(name, params, ret) for each public method backed by a native call."""
    simple = proxy_fq.split("$")[0]
    natives = cat.nat[proxy_fq]["native_methods"]
    out = []
    for m in cat.pub[simple].get("m", []):
        parsed = parse_signature(m, cat)
        if not parsed:
            continue
        name, params, ret = parsed
        if name in ("<init>", "toString", "hashCode", "equals"):
            continue
        nm = match_native(name, descriptor_of(params, ret), natives)
        if nm is None:
            continue
        out.append((nm["name"], params, Type(ret.java, cat, as_return=True)))
    return out


def gen_cpp_jni(cat: Catalog) -> str:
    out = [
        "// Generated from the Fatmap SDK interface contract. Do not edit by hand.",
        "//",
        "// One JNI entry point per native method of the original Fatmap SDK, with",
        "// identical symbol names, so the shared object is a drop-in replacement.",
        "",
        '#include <jni.h>',
        "",
        '#include "fatmap_api.h"',
        '#include "fatmap_engine.h"',
        '#include "fatmap_jni_types.h"',
        '#include "fatmap_jni_values.h"',
        "",
        "using namespace fatmap;",
        "",
        "namespace {",
        "",
    ]
    for fq in sorted(cat.nat):
        simple = fq.split("$")[0].rsplit(".", 1)[-1]
        natives = cat.nat[fq]["native_methods"]
        out.append(f"// ---- {simple} " + "-" * max(4, 58 - len(simple)))
        for nm in natives:
            name = nm["name"]
            symbol = jni_mangle(f"{PACKAGE}.{simple}$CppProxy", name)
            out.append(gen_jni_function(cat, simple, nm, symbol))
        out.append("")
    out.append("}  // namespace")
    out.append("")
    return "\n".join(out)


def gen_jni_function(cat: Catalog, simple: str, nm: dict, symbol: str) -> str:
    name = nm["name"]
    desc = nm["descriptor"]
    close = desc.index(")")
    args_desc = desc[1:close]
    ret_desc = desc[close + 1 :]
    has_handle = args_desc.startswith("J")
    if has_handle:
        args_desc = args_desc[1:]

    # Recover the typed parameter list from the public signature.
    typed = None
    for sig in api_signatures(cat, f"{PACKAGE}.{simple}$CppProxy"):
        if sig[0] == name:
            typed = sig
            break
    if typed is None:
        # nativeDestroy and anything else without a public counterpart.
        params, ret = [], Type("void", cat)
        if ret_desc != "V":
            ret = Type("java.lang.String", cat)
    else:
        params, ret = typed[1], typed[2]

    # JNI parameter list.
    jni_params = ["JNIEnv* env", "jobject thiz"]
    if has_handle:
        jni_params.append("jlong handle")
    for i, t in enumerate(params):
        if t.kind == "primitive":
            jni_params.append(f"{SCALARS[t.java][2]} a{i}")
        else:
            jni_params.append(f"jobject a{i}")

    body = []
    if not has_handle:
        # A static factory: it creates a fresh implementation object and
        # hands it to the Java layer. Its parameters still have to be
        # marshalled - MREHolder.create takes the surface holder, the
        # orientation service and the configuration.
        for i, tp in enumerate(params):
            body.append(f"    const auto p{i} = {subst(tp.read, 'env', f'a{i}')};")
        call_args = ", ".join(["env"] + [f"p{i}" for i in range(len(params))])
        if ret.kind == "proxy":
            body.append(f"    auto* result = fatmap::static_{simple}_{name}({call_args});")
            body.append("    if (result == nullptr) {")
            body.append("        return nullptr;")
            body.append("    }")
            body.append(
                f'    return jni::wrap_proxy(env, result, "{JAVA_PKG_PATH}/{ret.simple}");'
            )
        else:
            body.append(f"    const auto result = fatmap::static_{simple}_{name}({call_args});")
            body.append(f"    return {subst(ret.write, 'env', 'arg', 'result')};")
        return (
            "extern \"C\" JNIEXPORT " + _jni_ret(ret) + " JNICALL " + symbol + "(\n    "
            + ",\n    ".join(["JNIEnv* env", "jobject thiz"]
                              + [f"{_jni_param(t, f'a{i}')}" for i, t in enumerate(params)])
            + ") {\n"
            + "\n".join(body)
            + "\n}"
        )

    body.append(f"    auto* proxy = fatmap::resolve<Proxy::{simple}>(env, handle);")
    body.append("    if (proxy == nullptr) {")
    body.append(f"        {_default_return(ret)}")
    body.append("    }")

    # Unmarshal.
    for i, t in enumerate(params):
        body.append(f"    const auto p{i} = {subst(t.read, 'env', f'a{i}')};")

    call_args = []
    for i, t in enumerate(params):
        call_args.append(f"p{i}")
    call = f"    proxy->{name}({', '.join(call_args)});"

    if ret.java == "void":
        body.append(call)
    elif ret.kind == "proxy":
        body.append(f"    const auto result = proxy->{name}({', '.join(call_args)});")
        body.append(
            f'    return jni::wrap_proxy(env, result, "{JAVA_PKG_PATH}/{ret.simple}");'
        )
    else:
        body.append(f"    const auto result = proxy->{name}({', '.join(call_args)});")
        body.append(f"    return {subst(ret.write, 'env', 'arg', 'result')};")

    return (
        "extern \"C\" JNIEXPORT " + _jni_ret(ret) + " JNICALL " + symbol + "(\n    "
        + ",\n    ".join(jni_params)
        + ") {\n"
        + "\n".join(body)
        + "\n}"
    )


def _jni_ret(ret: Type) -> str:
    if ret.java == "void":
        return "void"
    if ret.kind == "primitive":
        return SCALARS[ret.java][2]
    return "jobject"


def _default_return(ret: Type) -> str:
    """The statement to return when the handle does not resolve."""
    if ret.java == "void":
        return "return;"
    if ret.kind == "primitive":
        return "return 0;"
    return "return nullptr;"



def gen_cpp_values_header(cat: Catalog) -> str:
    """Declarations for the value marshalling, shared by both .cpp files."""
    out = [
        "// Generated from the Fatmap SDK interface contract. Do not edit by hand.",
        "#pragma once",
        "",
        "#include <jni.h>",
        "",
        '#include "fatmap_api.h"',
        "",
        "namespace jni {",
        "",
    ]
    for fq in sorted(cat.pub):
        simple = cat.simple[fq]
        if cat.kind[fq] == "value":
            out.append(f"fatmap::{simple} read_{simple}(JNIEnv* env, jobject obj);")
            out.append(
                f"jobject write_{simple}(JNIEnv* env, const fatmap::{simple}& value);"
            )
        elif cat.kind[fq] == "enum":
            out.append(f"fatmap::{simple} read_{simple}(JNIEnv* env, jobject obj);")
            out.append(
                f"jobject write_{simple}(JNIEnv* env, fatmap::{simple} value);"
            )
    out.append("")
    out.append("}  // namespace jni")
    out.append("")
    return "\n".join(out)


def gen_cpp_values(cat: Catalog) -> str:
    """read_X / write_X for every value class and enum."""
    out = [
        "// Generated from the Fatmap SDK interface contract. Do not edit by hand.",
        "",
        '#include <jni.h>',
        "",
        '#include "fatmap_api.h"',
        '#include "fatmap_jni_types.h"',
        '#include "fatmap_jni_values.h"',
        "",
        "using namespace fatmap;",
        "",
        "namespace jni {",
        "",
    ]
    for fq in sorted(cat.pub):
        simple = cat.simple[fq]
        if cat.kind[fq] == "value":
            props = cat.value_class(fq)
            order = cat.ctor_order(fq)
            if not props:
                # A class whose fields are all constants: it has a no-argument
                # constructor and nothing to read back.
                lines = [f"{simple} read_{simple}(JNIEnv* env, jobject obj) {{",
                         f"    (void)env;",
                         f"    (void)obj;",
                         f"    return {simple}{{}};",
                         "}",
                         "",
                         f"jobject write_{simple}(JNIEnv* env, const {simple}& value) {{",
                         f'    jclass cls = env->FindClass("{JAVA_PKG_PATH}/{simple}");',
                         '    jmethodID ctor = env->GetMethodID(cls, "<init>", "()V");',
                         "    return env->NewObject(cls, ctor);",
                         "}",
                         ""]
                out.extend(lines)
                continue
            lines = [f"{simple} read_{simple}(JNIEnv* env, jobject obj) {{",
                     f"    {simple} v;",
                     "    jclass cls = env->GetObjectClass(obj);"]
            for p, rt, g in props:
                sig = rt.desc if rt.kind != "primitive" else rt.desc
                if rt.kind == "primitive":
                    call = f'env->Call{rt.java.capitalize()}Method(obj, env->GetMethodID(cls, "{g}", "(){rt.desc}"))'
                else:
                    call = f'env->CallObjectMethod(obj, env->GetMethodID(cls, "{g}", "(){rt.desc}"))'
                    call = read_with(rt, call)
                lines.append(f"    v.{p} = {call};")
            lines.append("    env->DeleteLocalRef(cls);")
            lines.append("    return v;")
            lines.append("}")
            lines.append("")
            lines.append(f"jobject write_{simple}(JNIEnv* env, const {simple}& value) {{")
            lines.append(f'    jclass cls = env->FindClass("{JAVA_PKG_PATH}/{simple}");')
            ctor_desc = "(" + "".join(t.desc for _, t in order) + ")V"
            lines.append(f'    jmethodID ctor = env->GetMethodID(cls, "<init>", "{ctor_desc}");')
            # NewObject is variadic, so only JNI scalars and jobjects may be
            # passed through it. Anything else has to be marshalled first.
            args = []
            for index, (prop, tp) in enumerate(order):
                if tp.kind == "primitive":
                    if tp.java == "boolean":
                        args.append(f"static_cast<jboolean>(value.{prop} ? JNI_TRUE : JNI_FALSE)")
                    else:
                        args.append(f"static_cast<{SCALARS[tp.java][2]}>(value.{prop})")
                else:
                    lines.append(f"    const jobject a{index} = {subst(tp.write, 'env', 'arg', f'value.{prop}')};")
                    args.append(f"a{index}")
            call = ", ".join(args)
            lines.append(f"    return env->NewObject(cls, ctor{', ' if call else ''}{call});")
            lines.append("}")
            lines.append("")
            out.extend(lines)
        elif cat.kind[fq] == "enum":
            consts = [
                normalise_field(f.split(":", 1)[0])
                for f in cat.pub[fq].get("f", [])
                if f.split(":", 1)[0] != "$VALUES"
            ]
            lines = [f"{simple} read_{simple}(JNIEnv* env, jobject obj) {{",
                     "    if (obj == nullptr) {",
                     f"        return static_cast<{simple}>(0);",
                     "    }",
                     '    jclass cls = env->GetObjectClass(obj);',
                     '    jmethodID ordinal = env->GetMethodID(cls, "ordinal", "()I");',
                     "    const int value = env->CallIntMethod(obj, ordinal);",
                     f"    return static_cast<{simple}>(value);",
                     "}",
                     ""]
            lines.append(f"jobject write_{simple}(JNIEnv* env, {simple} value) {{")
            lines.append(f'    jclass cls = env->FindClass("{JAVA_PKG_PATH}/{simple}");')
            lines.append("    const char* names[] = {" + ", ".join(f'"{c}"' for c in consts) + "};")
            lines.append("    const int index = static_cast<int>(value);")
            lines.append("    if (index < 0 || index >= static_cast<int>(sizeof(names) / sizeof(names[0]))) {")
            lines.append("        return nullptr;")
            lines.append("    }")
            lines.append(f'    jfieldID field = env->GetStaticFieldID(cls, names[index], "{JAVA_PKG_PATH}/{simple};");')
            lines.append("    return env->GetStaticObjectField(cls, field);")
            lines.append("}")
            lines.append("")
            out.extend(lines)
    out.append("}  // namespace jni")
    out.append("")
    return "\n".join(out)


def gen_cpp_handles(cat: Catalog) -> str:
    """Per-class helpers that read a proxy object's native handle."""
    out = [
        "// Generated from the Fatmap SDK interface contract. Do not edit by hand.",
        "#pragma once",
        "",
        "#include <jni.h>",
        "",
        '#include "fatmap_jni_types.h"',
        "",
        "namespace fatmap {",
        "",
    ]
    for fq in sorted(cat.nat):
        simple = fq.split("$")[0].rsplit(".", 1)[-1]
        sig = f"L{JAVA_PKG_PATH}/{simple}$CppProxy;"
        out.append(f"inline jlong handle_of_{simple}(JNIEnv* env, jobject obj) {{")
        out.append(f'    return jni::read_handle(env, obj, "{sig}");')
        out.append("}")
        out.append("")
    out.append("}  // namespace fatmap")
    out.append("")
    return "\n".join(out)


def gen_cpp_statics(cat: Catalog) -> str:
    """Declarations for the static entry points the JNI layer calls."""
    out = [
        "// Generated from the Fatmap SDK interface contract. Do not edit by hand.",
        "#pragma once",
        "",
        "#include <jni.h>",
        "",
        "#include <string>",
        "#include <vector>",
        "",
        '#include "fatmap_api.h"',
        '#include "fatmap_jni_types.h"',
        "",
        "namespace fatmap {",
        "",
    ]
    for fq in sorted(cat.nat):
        simple = fq.split("$")[0].rsplit(".", 1)[-1]
        for nm in cat.nat[fq]["native_methods"]:
            desc = nm["descriptor"]
            close = desc.index(")")
            if desc[1:close].startswith("J"):
                continue  # not static
            typed = None
            for sig in api_signatures(cat, fq):
                if sig[0] == nm["name"]:
                    typed = sig
                    break
            params = typed[1] if typed else []
            ret = typed[2] if typed else Type("void", cat)
            ps = ", ".join(["JNIEnv* env"] + [f"{tp.cpp} p{i}" for i, tp in enumerate(params)])
            r = "void" if ret.java == "void" else ret.cpp
            out.append(f"{r} static_{simple}_{nm['name']}({ps});")
    out.append("")
    out.append("}  // namespace fatmap")
    out.append("")
    return "\n".join(out)


def gen_cpp_proxy(cat: Catalog) -> str:
    out = [
        "// Generated from the Fatmap SDK interface contract. Do not edit by hand.",
        "#pragma once",
        "",
        '#include "fatmap_api.h"',
        "",
        "namespace fatmap {",
        "",
    ]
    for fq in sorted(cat.nat):
        simple = fq.split("$")[0].rsplit(".", 1)[-1]
        out.append(f"class {simple}Impl;")
    out.append("")
    out.append("// The concrete implementation type behind each Java proxy class.")
    out.append("namespace Proxy {")
    out.append("")
    for fq in sorted(cat.nat):
        simple = fq.split("$")[0].rsplit(".", 1)[-1]
        out.append(f"using {simple} = {simple}Impl;")
    out.append("")
    out.append("}  // namespace Proxy")
    out.append("}  // namespace fatmap")
    out.append("")
    return "\n".join(out)

def gen_coverage(cat: Catalog, impl_source: str) -> str:
    """Which of the 312 native methods the engine actually implements."""
    import re as _re
    implemented = set(_re.findall(r"(\w+Impl)::(\w+)\s*\(", impl_source))
    rows = []
    total = 0
    done = 0
    for fq in sorted(cat.nat):
        simple = fq.split("$")[0].rsplit(".", 1)[-1]
        natives = cat.nat[fq]["native_methods"]
        have = 0
        for nm in natives:
            total += 1
            if (simple + "Impl", nm["name"]) in implemented:
                have += 1
                done += 1
        rows.append((simple, have, len(natives)))
    out = [
        "# Engine coverage",
        "",
        "Generated by `tools/fatmap_codegen.py`. Lists how much of the",
        "Fatmap-compatible API the engine implements, against the 312 native",
        "methods of the original library.",
        "",
        f"**{done} of {total} native methods implemented ({100 * done // max(1, total)}%).**",
        "",
        "A method that is not implemented keeps the generated default body: it",
        "does nothing and returns a zero value. The JNI entry point still",
        "exists, so the API surface is complete and source-compatible.",
        "",
        "| Proxy class | Implemented | Total |",
        "|---|---:|---:|",
    ]
    for simple, have, count in rows:
        out.append(f"| `{simple}` | {have} | {count} |")
    out.append(f"| **Total** | **{done}** | **{total}** |")
    out.append("")
    out.append("## What is deliberately not implemented")
    out.append("")
    out.append("These are the original engine's test harness and its")
    out.append("server-dependent features. They have no meaning without")
    out.append("Strava's infrastructure, so they are left as no-ops:")
    out.append("")
    for item in [
        "the performance-test and screenshot-test runners",
        "the mock tile server",
        "the two-pass video rendering pipeline",
        "the testing contexts",
        "the Lua scripting runtime (the globals map is kept, the interpreter is not)",
    ]:
        out.append(f"- {item}")
    out.append("")
    return "\n".join(out)


# --------------------------------------------------------------------- driver


def write(path: str, text: str) -> None:
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as fh:
        fh.write(text)


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--spec", default="spec")
    ap.add_argument("--kotlin-out", default="app/src/main/kotlin/com/fatmap/sdk/api")
    ap.add_argument("--cpp-out", default="map-engine/generated")
    ap.add_argument("--clean", action="store_true")
    args = ap.parse_args()

    with open(os.path.join(args.spec, "fatmap-java-api.json"), encoding="utf-8") as fh:
        pub = json.load(fh)
    with open(os.path.join(args.spec, "fatmap-native-api.json"), encoding="utf-8") as fh:
        nat = json.load(fh)
    cat = Catalog(pub, nat)

    if args.clean and os.path.isdir(args.kotlin_out):
        shutil.rmtree(args.kotlin_out)

    counts = {"enum": 0, "interface": 0, "value": 0, "proxy": 0}
    for fq, entry in sorted(pub.items()):
        simple = cat.simple[fq]
        proxy_key = f"{PACKAGE}.{simple}$CppProxy"
        if proxy_key in nat:
            text = gen_proxy(simple, fq, cat, nat[proxy_key]["native_methods"])
            counts["proxy"] += 1
        elif cat.kind[fq] == "enum":
            text = gen_enum(simple, entry)
            counts["enum"] += 1
        elif cat.kind[fq] == "interface":
            text = gen_interface(simple, entry, cat)
            counts["interface"] += 1
        else:
            text = gen_value(simple, fq, cat)
            counts["value"] += 1
        write(os.path.join(args.kotlin_out, f"{simple}.kt"), text)

    write(os.path.join(args.cpp_out, "fatmap_types.h"), gen_cpp_types(cat))
    write(os.path.join(args.cpp_out, "fatmap_api.h"), gen_cpp_api(cat))
    write(os.path.join(args.cpp_out, "fatmap_proxy.h"), gen_cpp_proxy(cat))
    write(os.path.join(args.cpp_out, "fatmap_statics.h"), gen_cpp_statics(cat))
    write(os.path.join(args.cpp_out, "fatmap_handles.h"), gen_cpp_handles(cat))
    write(os.path.join(args.cpp_out, "fatmap_jni_values.h"), gen_cpp_values_header(cat))
    write(os.path.join(args.cpp_out, "fatmap_jni_values.cpp"), gen_cpp_values(cat))
    write(os.path.join(args.cpp_out, "fatmap_jni.cpp"), gen_cpp_jni(cat))

    impl = ""
    impl_path = os.path.join(os.path.dirname(args.spec), "map-engine", "src", "engine_api.cpp")
    if os.path.exists(impl_path):
        with open(impl_path, encoding="utf-8") as fh:
            impl = fh.read()
        write(os.path.join(os.path.dirname(args.cpp_out), "COVERAGE.md"),
              gen_coverage(cat, impl))

    print(f"kotlin classes : {sum(counts.values())}  {counts}")
    print(f"native classes : {len(nat)}")
    print(f"native methods : {sum(len(v['native_methods']) for v in nat.values())}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
