// Reading a Mapbox Vector Tile.
//
// A vector tile is a protobuf message holding a list of layers, each with a
// list of features, and each feature a geometry and a set of properties. The
// geometry is a list of paths of points in the tile's own coordinate space.
//
// The decoder is written here rather than pulled in, because the engine
// needs only this message and the alternative is a protobuf library the size
// of the whole project.

#pragma once

#include <cstdint>
#include <map>
#include <string>
#include <vector>

namespace fatmap::mvt {

/// How a feature is drawn.
enum class GeomType : uint32_t {
    Unknown = 0,
    Point = 1,
    LineString = 2,
    Polygon = 3,
};

/// One property of a feature.
struct Value {
    enum class Kind { String, Float, Double, Int, Uint, Sint, Bool } kind = Kind::String;
    std::string stringValue;
    double numberValue = 0.0;
    bool boolValue = false;

    /// The value as a string, whichever kind it is.
    std::string asString() const;
    /// The value as a number, whichever kind it is.
    double asNumber() const;
};

/// One feature: a geometry and its properties.
struct Feature {
    uint64_t id = 0;
    GeomType type = GeomType::Unknown;
    std::vector<std::vector<std::pair<int32_t, int32_t>>> paths;
    std::map<std::string, Value> properties;

    /// A property, as a string.
    std::string property(
        const std::string& key,
        const std::string& fallback = "") const;
};

/// One layer of a tile.
struct Layer {
    std::string name;
    uint32_t extent = 4096;
    std::vector<Feature> features;
};

/// A whole tile.
struct Tile {
    std::vector<Layer> layers;

    /// The layer with this name, or null.
    const Layer* layer(const std::string& name) const;
};

/// Reads a tile.
///
/// @return false when the bytes are not a tile this decoder understands.
bool decode(
    const uint8_t* data,
    size_t size,
    Tile& out);

}  // namespace fatmap::mvt
