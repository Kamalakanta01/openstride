#!/usr/bin/env python3
"""Minimal Android binary XML (AXML) decoder -> readable XML text."""
import struct, sys, zipfile, io

RES_STRING_POOL = 0x0001
RES_XML_START_NAMESPACE = 0x0100
RES_XML_END_NAMESPACE = 0x0101
RES_XML_START_ELEMENT = 0x0102
RES_XML_END_ELEMENT = 0x0103
RES_XML_CDATA = 0x0104
RES_XML_RESOURCE_MAP = 0x0180

TYPE_NULL, TYPE_REFERENCE, TYPE_ATTRIBUTE, TYPE_STRING, TYPE_FLOAT, TYPE_DIMENSION, TYPE_FRACTION = 0,1,2,3,4,5,6
TYPE_INT, TYPE_INT_HEX, TYPE_INT_BOOLEAN = 0x10, 0x11, 0x12
TYPE_BOOLEAN = TYPE_INT_BOOLEAN

def decode(data):
    out = io.StringIO()
    # file header
    magic, fsize = struct.unpack_from('<HH', data, 0)
    pos = 8
    strings = []
    ns_prefix = {}
    depth = 0
    while pos < len(data):
        ctype, hsize, csize = struct.unpack_from('<HHI', data, pos)
        if csize == 0:
            break
        chunk = data[pos:pos+csize]
        if ctype == RES_STRING_POOL:
            scount, styc, flags, sstart, stystart = struct.unpack_from('<IIIII', chunk, 8)
            is_utf8 = bool(flags & (1 << 8))
            offs = [struct.unpack_from('<I', chunk, 28 + 4*i)[0] for i in range(scount)]
            base = 28 + 4*scount
            if styc:
                base += 4*scount
            for o in offs:
                p = base + o
                if is_utf8:
                    n1 = chunk[p]; p += 1
                    if n1 & 0x80:
                        n1 = ((n1 & 0x7f) << 8) | chunk[p]; p += 1
                    n2 = chunk[p]; p += 1
                    if n2 & 0x80:
                        n2 = ((n2 & 0x7f) << 8) | chunk[p]; p += 1
                    strings.append(chunk[p:p+n2].decode('utf-8', 'replace'))
                else:
                    n = struct.unpack_from('<H', chunk, p)[0]; p += 2
                    if n & 0x8000:
                        n = ((n & 0x7fff) << 16) | struct.unpack_from('<H', chunk, p)[0]; p += 2
                    strings.append(chunk[p:p+n*2].decode('utf-16-le', 'replace'))
        elif ctype == RES_XML_START_NAMESPACE:
            prefix, uri = struct.unpack_from('<II', chunk, 16)
            ns_prefix[uri] = strings[prefix] if prefix != 0xffffffff else ''
        elif ctype == RES_XML_START_ELEMENT:
            ns, name, attr_start, attr_size, attr_count = struct.unpack_from('<IIHHH', chunk, 16)
            tag = strings[name] if name != 0xffffffff else '?'
            out.write('  ' * depth + '<' + tag)
            for i in range(attr_count):
                off = 16 + attr_start + i*attr_size
                a_ns, a_name, a_raw = struct.unpack_from('<III', chunk, off)
                a_type = chunk[off + 15]
                a_data = struct.unpack_from('<I', chunk, off + 16)[0]
                aname = strings[a_name] if a_name != 0xffffffff else '?'
                if a_type == TYPE_STRING:
                    val = strings[a_data] if a_data != 0xffffffff else ''
                elif a_type == TYPE_REFERENCE:
                    val = '@0x%08x' % a_data
                elif a_type == TYPE_ATTRIBUTE:
                    val = '?0x%08x' % a_data
                elif a_type == TYPE_INT:
                    val = str(a_data)
                elif a_type == TYPE_BOOLEAN:
                    val = 'true' if a_data else 'false'
                elif a_type == TYPE_FLOAT:
                    val = str(struct.unpack_from('<f', chunk, off+12)[0])
                else:
                    val = '0x%08x' % a_data
                pfx = ''
                if a_ns != 0xffffffff and a_ns in ns_prefix and ns_prefix[a_ns]:
                    pfx = ns_prefix[a_ns] + ':'
                out.write(' %s%s="%s"' % (pfx, aname, val))
            out.write('>\n')
            depth += 1
        elif ctype == RES_XML_END_ELEMENT:
            depth -= 1
            _, name = struct.unpack_from('<II', chunk, 16)
            tag = strings[name] if name != 0xffffffff else '?'
            out.write('  ' * depth + '</' + tag + '>\n')
        elif ctype == RES_XML_CDATA:
            # ResXMLTree_cdataExt: data(ResStringPool_ref @12), typedData, then raw utf8 text
            txt = chunk[20:].split(b'\x00')[0].decode('utf-8', 'replace')
            if not txt:
                idx = struct.unpack_from('<I', chunk, 12)[0]
                txt = strings[idx] if idx < len(strings) else ''
            out.write(txt + '\n')
        pos += csize
    return out.getvalue()

if __name__ == '__main__':
    apk, members = sys.argv[1], sys.argv[2:]
    z = zipfile.ZipFile(apk)
    for m in members:
        try:
            data = z.read(m)
        except KeyError:
            print(f'--- {m}: NOT PRESENT ---'); continue
        print(f'===== {m} =====')
        try:
            print(decode(data))
        except Exception as e:
            print(f'  decode error: {e}')
