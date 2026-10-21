// The vector tile decoder.
//
// The tile is a real one, taken from the open tile set the engine draws by
// default, so the decoder is checked against what the engine actually
// receives rather than against something made up.

#include "mvt/decoder.h"

#include <cstdio>
#include <fstream>
#include <string>
#include <vector>

namespace {

int failures = 0;

void check(
    bool condition,
    const std::string& what) {
    if (!condition) {
        printf("FAILED: %s\n", what.c_str());
        failures++;
    }
}

std::vector<uint8_t> read(
    const std::string& path) {
    std::ifstream in(path, std::ios::binary);
    return std::vector<uint8_t>((std::istreambuf_iterator<char>(in)), std::istreambuf_iterator<char>());
}

}  // namespace

int main() {
    const auto bytes = read("tests/fixtures/water-and-roads.pbf");
    check(!bytes.empty(), "the fixture is there");
    if (bytes.empty()) {
        return 1;
    }

    fatmap::mvt::Tile tile;
    check(fatmap::mvt::decode(bytes.data(), bytes.size(), tile), "the tile decodes");
    if (tile.layers.empty()) {
        printf("FAILED: the tile has no layers\n");
        return 1;
    }

    // The layer names are the open ones the tiles use, and the ones the
    // renderer draws.
    const char* wanted[] = {"landuse", "transportation", "waterway"};
    for (const char* name : wanted) {
        const fatmap::mvt::Layer* layer = tile.layer(name);
        check(layer != nullptr, std::string("the tile has a ") + name + " layer");
        if (layer == nullptr) {
            continue;
        }
        check(!layer->features.empty(), std::string("the ") + name + " layer has features");
        check(layer->extent > 0, std::string("the ") + name + " layer has an extent");
    }

    // The roads carry the class the style draws them by.
    const fatmap::mvt::Layer* roads = tile.layer("transportation");
    if (roads != nullptr) {
        bool anyClass = false;
        for (const auto& feature : roads->features) {
            if (!feature.property("class").empty()) {
                anyClass = true;
            }
        }
        check(anyClass, "the roads say what class they are");
    }

    // The geometry has to be there: a feature with no points draws nothing.
    size_t points = 0;
    for (const auto& layer : tile.layers) {
        for (const auto& feature : layer.features) {
            for (const auto& path : feature.paths) {
                points += path.size();
            }
        }
    }
    // The tile has a polygon, two roads and a river in it.
    check(points > 50, "the features have geometry");

    printf("%d checks, %d failures\n", 6, failures);
    return failures == 0 ? 0 : 1;
}
