// Reading a Mapbox Vector Tile.
//
// See decoder.h.

#include "mvt/decoder.h"

#include <cmath>
#include <cstring>

namespace fatmap::mvt {

namespace {

/// A reader over the protobuf wire format.
class Reader {
  public:
    Reader(
        const uint8_t* data,
        size_t size) :
        mData(data), mSize(size) {}

    bool done() const { return mOffset >= mSize; }
    size_t offset() const { return mOffset; }

    /// Reads a varint.
    bool varint(uint64_t& out) {
        uint64_t result = 0;
        int shift = 0;
        while (mOffset < mSize) {
            const uint8_t byte = mData[mOffset++];
            result |= static_cast<uint64_t>(byte & 0x7F) << shift;
            if ((byte & 0x80) == 0) {
                out = result;
                return true;
            }
            shift += 7;
            if (shift > 63) {
                return false;
            }
        }
        return false;
    }

    /// Reads a fixed 32-bit value.
    bool fixed32(uint32_t& out) {
        if (mOffset + 4 > mSize) {
            return false;
        }
        out = static_cast<uint32_t>(mData[mOffset]) |
              (static_cast<uint32_t>(mData[mOffset + 1]) << 8) |
              (static_cast<uint32_t>(mData[mOffset + 2]) << 16) |
              (static_cast<uint32_t>(mData[mOffset + 3]) << 24);
        mOffset += 4;
        return true;
    }

    /// Reads a fixed 64-bit value.
    bool fixed64(uint64_t& out) {
        if (mOffset + 8 > mSize) {
            return false;
        }
        out = 0;
        for (int i = 7; i >= 0; --i) {
            out = (out << 8) | mData[mOffset + static_cast<size_t>(i)];
        }
        mOffset += 8;
        return true;
    }

    /// Reads a length-delimited field.
    bool bytes(const uint8_t*& out, size_t& length) {
        uint64_t size = 0;
        if (!varint(size)) {
            return false;
        }
        if (mOffset + size > mSize) {
            return false;
        }
        out = mData + mOffset;
        length = static_cast<size_t>(size);
        mOffset += length;
        return true;
    }

    /// Skips a field the decoder does not want.
    bool skip(uint32_t wireType) {
        switch (wireType) {
            case 0: {
                uint64_t ignored = 0;
                return varint(ignored);
            }
            case 1: {
                uint64_t ignored = 0;
                return fixed64(ignored);
            }
            case 2: {
                const uint8_t* ignored = nullptr;
                size_t length = 0;
                return bytes(ignored, length);
            }
            case 5: {
                uint32_t ignored = 0;
                return fixed32(ignored);
            }
            default:
                return false;
        }
    }

  private:
    const uint8_t* mData;
    size_t mSize;
    size_t mOffset = 0;
};

/// The zigzag encoding the tile format uses for its numbers.
int32_t zigzag(uint64_t value) {
    return static_cast<int32_t>((value >> 1) ^ (~(value & 1) + 1));
}

/// Reads a packed array of varints.
bool readPacked(
    Reader& reader,
    std::vector<uint64_t>& out) {
    const uint8_t* data = nullptr;
    size_t length = 0;
    if (!reader.bytes(data, length)) {
        return false;
    }
    Reader inner(data, length);
    while (!inner.done()) {
        uint64_t value = 0;
        if (!inner.varint(value)) {
            return false;
        }
        out.push_back(value);
    }
    return true;
}

/// Reads the value of a property.
Value decodeValue(Reader& reader) {
    Value value;
    uint64_t tag = 0;
    if (!reader.varint(tag)) {
        return value;
    }
    const uint32_t field = static_cast<uint32_t>(tag >> 3);
    const uint32_t wireType = tag & 0x7;
    switch (field) {
        case 1: {
            const uint8_t* data = nullptr;
            size_t length = 0;
            if (reader.bytes(data, length)) {
                value.kind = Value::Kind::String;
                value.stringValue.assign(reinterpret_cast<const char*>(data), length);
            }
            break;
        }
        case 2: {
            uint32_t bits = 0;
            if (reader.fixed32(bits)) {
                value.kind = Value::Kind::Float;
                float number = 0.0f;
                std::memcpy(&number, &bits, sizeof(number));
                value.numberValue = number;
            }
            break;
        }
        case 3: {
            uint64_t bits = 0;
            if (reader.fixed64(bits)) {
                value.kind = Value::Kind::Double;
                double number = 0.0;
                std::memcpy(&number, &bits, sizeof(number));
                value.numberValue = number;
            }
            break;
        }
        case 4: {
            uint64_t number = 0;
            if (reader.varint(number)) {
                value.kind = Value::Kind::Int;
                value.numberValue = static_cast<double>(static_cast<int64_t>(number));
            }
            break;
        }
        case 5: {
            uint64_t number = 0;
            if (reader.varint(number)) {
                value.kind = Value::Kind::Uint;
                value.numberValue = static_cast<double>(number);
            }
            break;
        }
        case 6: {
            uint64_t number = 0;
            if (reader.varint(number)) {
                value.kind = Value::Kind::Sint;
                value.numberValue = static_cast<double>(zigzag(number));
            }
            break;
        }
        case 7: {
            uint64_t number = 0;
            if (reader.varint(number)) {
                value.kind = Value::Kind::Bool;
                value.boolValue = number != 0;
                value.numberValue = number != 0 ? 1.0 : 0.0;
            }
            break;
        }
        default:
            reader.skip(wireType);
            break;
    }
    return value;
}

/// Reads the geometry of a feature.
bool decodeGeometry(
    Reader& reader,
    Feature& feature) {
    std::vector<uint64_t> commands;
    if (!readPacked(reader, commands)) {
        return false;
    }
    int32_t cursorX = 0;
    int32_t cursorY = 0;
    size_t index = 0;
    while (index < commands.size()) {
        const uint64_t header = commands[index++];
        const uint32_t command = static_cast<uint32_t>(header & 0x7);
        const uint32_t count = static_cast<uint32_t>(header >> 3);
        if (command == 1) {
            // MoveTo: a new path starts here.
            for (uint32_t i = 0; i < count && index + 1 < commands.size(); ++i) {
                cursorX += zigzag(commands[index++]);
                cursorY += zigzag(commands[index++]);
                feature.paths.emplace_back();
                feature.paths.back().emplace_back(cursorX, cursorY);
            }
        } else if (command == 2) {
            // LineTo: more points in this path.
            if (feature.paths.empty()) {
                feature.paths.emplace_back();
            }
            for (uint32_t i = 0; i < count && index + 1 < commands.size(); ++i) {
                cursorX += zigzag(commands[index++]);
                cursorY += zigzag(commands[index++]);
                feature.paths.back().emplace_back(cursorX, cursorY);
            }
        } else if (command == 7) {
            // ClosePath: the path joins back to its first point.
            if (!feature.paths.empty() && feature.paths.back().size() > 1) {
                feature.paths.back().push_back(feature.paths.back().front());
            }
        } else {
            return false;
        }
    }
    return true;
}

/// Reads one feature.
bool decodeFeature(
    Reader& reader,
    const std::vector<std::string>& keys,
    const std::vector<Value>& values,
    Feature& feature) {
    std::vector<uint64_t> tags;
    while (!reader.done()) {
        uint64_t tag = 0;
        if (!reader.varint(tag)) {
            return false;
        }
        const uint32_t field = static_cast<uint32_t>(tag >> 3);
        const uint32_t wireType = static_cast<uint32_t>(tag & 0x7);
        switch (field) {
            case 1: {
                uint64_t id = 0;
                if (!reader.varint(id)) {
                    return false;
                }
                feature.id = id;
                break;
            }
            case 2: {
                if (!readPacked(reader, tags)) {
                    return false;
                }
                break;
            }
            case 3: {
                uint64_t type = 0;
                if (!reader.varint(type)) {
                    return false;
                }
                feature.type = static_cast<GeomType>(type);
                break;
            }
            case 4: {
                if (!decodeGeometry(reader, feature)) {
                    return false;
                }
                break;
            }
            default:
                if (!reader.skip(wireType)) {
                    return false;
                }
                break;
        }
    }
    // The tags come in pairs: a key index and a value index.
    for (size_t i = 0; i + 1 < tags.size(); i += 2) {
        const size_t key = static_cast<size_t>(tags[i]);
        const size_t value = static_cast<size_t>(tags[i + 1]);
        if (key < keys.size() && value < values.size()) {
            feature.properties[keys[key]] = values[value];
        }
    }
    return true;
}

/// Reads one layer.
bool decodeLayer(
    Reader& reader,
    Layer& layer) {
    std::vector<std::string> keys;
    std::vector<Value> values;
    // The features are read after the keys and the values, which come
    // later in the message, so they are kept aside with their lengths.
    std::vector<uint8_t> featureBytes;
    std::vector<size_t> featureLengths;

    while (!reader.done()) {
        uint64_t tag = 0;
        if (!reader.varint(tag)) {
            return false;
        }
        const uint32_t field = static_cast<uint32_t>(tag >> 3);
        const uint32_t wireType = tag & 0x7;
        switch (field) {
            case 1: {
                const uint8_t* data = nullptr;
                size_t length = 0;
                if (!reader.bytes(data, length)) {
                    return false;
                }
                layer.name.assign(reinterpret_cast<const char*>(data), length);
                break;
            }
            case 2: {
                const uint8_t* data = nullptr;
                size_t length = 0;
                if (!reader.bytes(data, length)) {
                    return false;
                }
                const size_t at = featureBytes.size();
                featureBytes.insert(featureBytes.end(), data, data + length);
                featureLengths.push_back(length);
                (void)at;
                break;
            }
            case 3: {
                const uint8_t* data = nullptr;
                size_t length = 0;
                if (!reader.bytes(data, length)) {
                    return false;
                }
                keys.emplace_back(reinterpret_cast<const char*>(data), length);
                break;
            }
            case 4: {
                const uint8_t* data = nullptr;
                size_t length = 0;
                if (!reader.bytes(data, length)) {
                    return false;
                }
                Reader valueReader(data, length);
                values.push_back(decodeValue(valueReader));
                break;
            }
            case 5: {
                uint64_t extent = 0;
                if (!reader.varint(extent)) {
                    return false;
                }
                layer.extent = static_cast<uint32_t>(extent);
                break;
            }
            case 15: {
                uint64_t version = 0;
                if (!reader.varint(version)) {
                    return false;
                }
                break;
            }
            default:
                if (!reader.skip(wireType)) {
                    return false;
                }
                break;
        }
    }

    // The features are now read, with the keys and the values known.
    size_t offset = 0;
    for (const size_t length : featureLengths) {
        if (offset + length > featureBytes.size()) {
            break;
        }
        Reader body(featureBytes.data() + offset, length);
        Feature feature;
        if (decodeFeature(body, keys, values, feature)) {
            layer.features.push_back(std::move(feature));
        }
        offset += length;
    }
    return true;
}

}  // namespace

std::string Value::asString() const {
    switch (kind) {
        case Kind::String:
            return stringValue;
        case Kind::Bool:
            return boolValue ? "true" : "false";
        case Kind::Float:
        case Kind::Double:
            return std::to_string(numberValue);
        default:
            return std::to_string(static_cast<long long>(numberValue));
    }
}

double Value::asNumber() const {
    return numberValue;
}

std::string Feature::property(
    const std::string& key,
    const std::string& fallback) const {
    const auto it = properties.find(key);
    if (it == properties.end()) {
        return fallback;
    }
    return it->second.asString();
}

const Layer* Tile::layer(const std::string& name) const {
    for (const Layer& candidate : layers) {
        if (candidate.name == name) {
            return &candidate;
        }
    }
    return nullptr;
}

bool decode(
    const uint8_t* data,
    size_t size,
    Tile& out) {
    if (data == nullptr || size == 0) {
        return false;
    }
    Reader reader(data, size);
    while (!reader.done()) {
        uint64_t tag = 0;
        if (!reader.varint(tag)) {
            return false;
        }
        const uint32_t field = static_cast<uint32_t>(tag >> 3);
        const uint32_t wireType = tag & 0x7;
        if (field == 3 && wireType == 2) {
            const uint8_t* layerData = nullptr;
            size_t length = 0;
            if (!reader.bytes(layerData, length)) {
                return false;
            }
            Layer layer;
            Reader layerReader(layerData, length);
            if (decodeLayer(layerReader, layer)) {
                out.layers.push_back(std::move(layer));
            }
        } else if (!reader.skip(wireType)) {
            return false;
        }
    }
    return !out.layers.empty();
}

}  // namespace fatmap::mvt
