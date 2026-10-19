#!/usr/bin/env python3
"""Find which DEX methods reference a given string index (const-string / const-string/jumbo).

Usage: python3 dexref.py <dexfile> <string-index> [more indices...]
"""
import struct, sys

def uleb(d, p):
    r = 0; s = 0
    while True:
        b = d[p]; p += 1; r |= (b & 0x7f) << s
        if not b & 0x80: return r, p
        s += 7

def sleb(d, p):
    r = 0; s = 0
    while True:
        b = d[p]; p += 1; r |= (b & 0x7f) << s
        s += 7
        if not b & 0x80:
            if s < 64 and b & 0x40: r |= -(1 << s)
            return r, p

def read_class_data(d, off):
    p = off
    sf, p = uleb(d, p)
    inf, p = uleb(d, p)
    dm, p = uleb(d, p)
    vm, p = uleb(d, p)
    for _ in range(sf):
        _, p = uleb(d, p); _, p = uleb(d, p)
    for _ in range(inf):
        _, p = uleb(d, p); _, p = uleb(d, p)
    out = []
    for _ in range(dm):
        _, p = uleb(d, p); _, p = uleb(d, p); _, p = uleb(d, p)
    for _ in range(vm):
        _, p = uleb(d, p); _, p = uleb(d, p); _, p = uleb(d, p)
    return out, p

def scan(dexpath, targets):
    d = open(dexpath, 'rb').read()
    n_str = struct.unpack_from('<I', d, 56)[0]
    str_off = struct.unpack_from('<I', d, 60)[0]
    n_type = struct.unpack_from('<I', d, 64)[0]
    type_off = struct.unpack_from('<I', d, 68)[0]
    n_cls = struct.unpack_from('<I', d, 96)[0]
    cls_off = struct.unpack_from('<I', d, 100)[0]
    n_meth = struct.unpack_from('<I', d, 88)[0]
    meth_off = struct.unpack_from('<I', d, 92)[0]

    def S(i):
        so = struct.unpack_from('<I', d, str_off + 4*i)[0]
        _, p = uleb(d, so)
        e = d.index(b'\x00', p)
        return d[p:e].decode('utf-8', 'replace')

    def T(i):
        di = struct.unpack_from('<I', d, type_off + 4*i)[0]
        return S(di)

    def M(i):
        cls, proto, name = struct.unpack_from('<HHI', d, meth_off + 8*i)
        return f'{T(cls)}->{S(name)}'

    hits = []
    for ci in range(n_cls):
        cdoff = struct.unpack_from('<I', d, cls_off + 32*ci + 24)[0]
        if not cdoff: continue
        cname = T(struct.unpack_from('<I', d, cls_off + 32*ci)[0])
        try:
            _, p = read_class_data(d, cdoff)
        except Exception:
            continue
        sf, p = uleb(d, cdoff)
        inf, p = uleb(d, p)
        dm, p = uleb(d, p)
        vm, p = uleb(d, p)
        for _ in range(sf):
            _, p = uleb(d, p); _, p = uleb(d, p)
        for _ in range(inf):
            _, p = uleb(d, p); _, p = uleb(d, p)
        for _ in range(dm):
            _, p = uleb(d, p); _, p = uleb(d, p); _, p = uleb(d, p)
        for _ in range(vm):
            m_idx, p = uleb(d, p)
            access, p = uleb(d, p)
            code_off, p = uleb(d, p)
            if not code_off: continue
            insns_size = struct.unpack_from('<I', d, code_off + 12)[0]
            insns = d[code_off + 16: code_off + 16 + insns_size*2]
            for t in targets:
                # const-string vAA, string@BBBB  (opcode 0x1a)
                for j in range(0, len(insns) - 3, 2):
                    if insns[j] == 0x1a:
                        idx = struct.unpack_from('<H', insns, j+2)[0]
                        if idx == t:
                            hits.append((cname, M(m_idx), S(t), 'const-string'))
                    elif insns[j] == 0x1b:
                        if j + 5 < len(insns):
                            idx = struct.unpack_from('<I', insns, j+2)[0]
                            if idx == t:
                                hits.append((cname, M(m_idx), S(t), 'const-string/jumbo'))
    return hits

if __name__ == '__main__':
    dex = sys.argv[1]
    targets = [int(x) for x in sys.argv[2:]]
    for c, m, s, k in scan(dex, targets):
        print(f'{c}  ::  {m}   <- {k} "{s}"')
