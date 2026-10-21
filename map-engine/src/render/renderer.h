// Drawing the map.
//
// The renderer draws the tile geometry: the land and the water as filled
// shapes, the roads as lines, each in the colour the style gives them.
//
// The style is the app's own, not the original's: the original's styles are
// in their package and cannot be copied. The layer names and the classes
// are the open ones the vector tiles use, so any source with the same
// schema can be drawn.

#pragma once

#include <cstdint>
#include <map>
#include <string>
#include <vector>

#include "mvt/decoder.h"
#include "tiles/tile_cache.h"

namespace fatmap::render {

/// Where the camera is.
struct Camera {
    double latitude = 0.0;
    double longitude = 0.0;
    double zoom = 2.0;
    double bearing = 0.0;
    double pitch = 0.0;
    /// The size of the surface, in pixels.
    int width = 0;
    int height = 0;
    /// How many pixels a tile edge is.
    double tileSize = 256.0;
};

/// The colours the layers are drawn in.
struct Style {
    /// The colour under everything.
    uint32_t background = 0xFFF2EFE9;
    /// The water.
    uint32_t water = 0xFFAAD3DF;
    /// The green areas.
    uint32_t landuse = 0xFFE8E4D8;
    /// The buildings.
    uint32_t building = 0xFFE0DCD2;
    /// The roads, from the biggest to the smallest.
    std::vector<std::pair<std::string, uint32_t>> roads = {
        {"motorway", 0xFFE9A77A},
        {"trunk", 0xFFEFB183},
        {"primary", 0xFFF6C79B},
        {"secondary", 0xFFF8D9B4},
        {"tertiary", 0xFFF6E2C4},
        {"minor", 0xFFFAEED8},
        {"service", 0xFFF6F0E4},
        {"path", 0xFFC9C0A8},
    };
    /// The line widths, in pixels, from the biggest to the smallest.
    std::vector<double> roadWidths = {7.0, 6.0, 5.0, 4.0, 3.0, 2.0, 1.5, 1.0};
};

/// A renderer, and the state it keeps between frames.
class Renderer {
  public:
    Renderer();
    ~Renderer();

    /// Sets the surface size.
    void resize(
        int width,
        int height);

    /// Moves the camera.
    void setCamera(const Camera& camera) { mCamera = camera; }

    /// The camera.
    const Camera& camera() const { return mCamera; }

    /// The tiles this renderer has.
    void setTileCache(tiles::TileCache* cache) { mCache = cache; }

    /// The style.
    void setStyle(const Style& style) { mStyle = style; }

    /// The URL of a tile, for the given zoom and tile coordinates.
    std::string tileUrl(
        const std::string& template_,
        int zoom,
        int x,
        int y) const;

    /// Draws one frame. Called on the rendering thread, with the context
    /// current.
    void draw();

    /// How many tiles the last frame drew.
    int tilesDrawn() const { return mTilesDrawn; }

    /// The layers the last frame drew, and how many features of each.
    const std::map<std::string, int>& layerCounts() const { return mLayerCounts; }

    /// The URL template of the tiles.
    void setTileUrlTemplate(const std::string& template_) { mUrlTemplate = template_; }

    /// The URL template of the tiles.
    const std::string& tileUrlTemplate() const { return mUrlTemplate; }

  private:
    /// The tiles the camera needs.
    std::vector<std::pair<std::pair<int, int>, int>> visibleTiles() const;

    /// The vertices of one feature, in clip space.
    void featureVertices(
        const mvt::Feature& feature,
        int zoom,
        int tileX,
        int tileY,
        uint32_t extent,
        std::vector<float>& out) const;

    /// Draws the whole thing.
    void drawFrame();

    Camera mCamera;
    Style mStyle;
    tiles::TileCache* mCache = nullptr;
    std::string mUrlTemplate;

    /// The decoded tiles, by zoom and coordinates.
    std::map<std::string, mvt::Tile> mDecoded;

    int mTilesDrawn = 0;
    std::map<std::string, int> mLayerCounts;

    // The OpenGL objects, created with the context.
    unsigned int mProgram = 0;
    unsigned int mBuffer = 0;
    int mProjectionUniform = -1;
    int mColourUniform = -1;
    bool mGlReady = false;
};

}  // namespace fatmap::render
