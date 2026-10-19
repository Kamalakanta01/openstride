#!/usr/bin/env python3
"""Parse resources.arsc: build resource-id -> (type,name,value) and a global string pool."""
import struct, sys, zipfile, json, re

def parse_string_pool(data, pos):
    ctype, hsize, csize = struct.unpack_from('<HHI', data, pos)
    scount, styc, flags, sstart, stystart = struct.unpack_from('<IIIII', data, pos + 8)
    is_utf8 = bool(flags & (1 << 8))
    offs = [struct.unpack_from('<I', data, pos + 28 + 4 * i)[0] for i in range(scount)]
    base = pos + 28 + 4 * scount + (4 * scount if styc else 0)
    out = []
    for o in offs:
        p = base + o
        if is_utf8:
            n1 = data[p]; p += 1
            if n1 & 0x80: n1 = ((n1 & 0x7f) << 8) | data[p]; p += 1
            n2 = data[p]; p += 1
            if n2 & 0x80: n2 = ((n2 & 0x7f) << 8) | data[p]; p += 1
            out.append(data[p:p + n2].decode('utf-8', 'replace'))
        else:
            n = struct.unpack_from('<H', data, p)[0]; p += 2
            if n & 0x8000:
                n = ((n & 0x7fff) << 16) | struct.unpack_from('<H', data, p)[0]; p += 2
            out.append(data[p:p + n * 2].decode('utf-16-le', 'replace'))
    return out, pos + csize

def parse_arsc(data):
    global_strings = []
    res = {}            # rid -> (type_name, entry_name, value_str)
    type_names = {}     # typeId -> [name,...]
    pkg_strings = {}    # (pkgId, idx) -> name
    pos = 12
    while pos < len(data):
        ctype, hsize, csize = struct.unpack_from('<HHI', data, pos)
        if csize == 0: break
        if ctype == 0x0001:                       # global string pool
            global_strings, _ = parse_string_pool(data, pos)
        elif ctype == 0x0200:                     # package
            pkg_id = struct.unpack_from('<I', data, pos + 8)[0]
            name = data[pos + 12: pos + 12 + 256].decode('utf-16-le', 'replace').split('\x00')[0]
            type_str_off, last_pub_type, key_str_off, last_pub_key = struct.unpack_from('<IIII', data, pos + 268)
            if hsize >= 288:
                type_id_off = struct.unpack_from('<I', data, pos + 284)[0]
            else:
                type_id_off = 0
            p2 = pos + type_str_off
            ts, p2 = parse_string_pool(data, p2)
            for i, t in enumerate(ts):
                type_names.setdefault(pkg_id, {})[i + 1 + type_id_off] = t
            p3 = pos + key_str_off
            ks, _ = parse_string_pool(data, p3)
            for i, k in enumerate(ks):
                pkg_strings[(pkg_id, i)] = k
            # type chunks are nested inside the package chunk
            q = pos + hsize
            while q < pos + csize:
                t2, h2, s2 = struct.unpack_from('<HHI', data, q)
                if s2 == 0: break
                if t2 == 0x0201:                 # ResTable_type
                    type_id = data[q + 8]
                    entry_count, entries_start = struct.unpack_from('<II', data, q + 12)
                    tname = type_names.get(pkg_id, {}).get(type_id, f'type{type_id}')
                    base = q + hsize
                    if base + 4 * entry_count > len(data):
                        q += s2; continue
                    for i in range(entry_count):
                        off = struct.unpack_from('<I', data, base + 4 * i)[0]
                        if off == 0xFFFFFFFF: continue
                        vp = base + off + 8
                        if vp + 8 > len(data): continue
                        key = pkg_strings.get((pkg_id, i), f'entry{i}')
                        rid = (0x7f << 24) | (type_id << 16) | i
                        if tname == 'string':
                            # string type: entry holds an offset into the global string pool
                            soff = struct.unpack_from('<I', data, base + off)[0]
                            val = global_strings[soff] if soff < len(global_strings) else soff
                            res[rid] = (tname, key, val)
                        else:
                            vsize, vres0, vtype, vdata = struct.unpack_from('<HBBI', data, vp)
                            if vtype == 0x03 and vdata < len(global_strings):
                                res[rid] = (tname, key, global_strings[vdata])
                            else:
                                res[rid] = (tname, key, vdata)
                q += s2
        elif ctype == 0x0201:                     # type chunk at top level
            type_id = data[pos + 8]
            entry_count, entries_start = struct.unpack_from('<II', data, pos + 12)
            tname = type_names.get(pkg_id, {}).get(type_id, f'type{type_id}')
            base = pos + hsize
            for i in range(entry_count):
                off = struct.unpack_from('<I', data, base + 4 * i)[0]
                if off == 0xFFFFFFFF: continue
                vp = base + off + 8
                vsize, vres0, vtype, vdata = struct.unpack_from('<HBBI', data, vp)
                key = pkg_strings.get((pkg_id, i), f'entry{i}')
                rid = (0x7f << 24) | (type_id << 16) | i
                if vtype == 0x03 and vdata < len(global_strings):
                    res[rid] = (tname, key, global_strings[vdata])
                else:
                    res[rid] = (tname, key, vdata)
        pos += csize
    return res, global_strings, type_names

if __name__ == '__main__':
    z = zipfile.ZipFile(sys.argv[1])
    res, gstrings, tnames = parse_arsc(z.read('resources.arsc'))
    json.dump({str(k): v for k, v in res.items()}, open(sys.argv[2], 'w'))
    print('resources resolved:', len(res))
    print('global string pool:', len(gstrings))
    print('types:', {k: len(v) for k, v in tnames.items()})
