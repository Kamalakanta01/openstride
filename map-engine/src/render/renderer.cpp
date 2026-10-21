// Drawing the map.
//
// See renderer.h. The OpenGL calls are guarded, so the file compiles on the
// host, where the renderer can still be tested for the parts that are not
// graphics: which tiles the camera needs, what the URLs are, and how the
// geometry is turned into triangles.

#include "render/renderer.h"

#include <algorithm>
#include <cmath>
#include <cstdio>
#include <sstream>

#include "geo.h"
#include "render/polygon.h"

#if defined(__ANDROID__)
#include <GLES2/gl2.h>
#endif

namespace fatmap::render {

namespace {

/// The colour of a layer's features, as the shader wants it.
struct Colour {
    float r = 0.0f;
    float g = 0.0f;
    float b = 0.0f;
    float a = 1.0f;
};

/// The colour of a layer's features, as the shader wants it.
///
/// The colours are written the way Android writes them: alpha first, then
/// red, green and blue.
Colour unpack(uint32_t argb) {
    return Colour{
        static_cast<float>((argb >> 16) & 0xFF) / 255.0f,
        static_cast<float>((argb >> 8) & 0xFF) / 255.0f,
        static_cast<float>(argb & 0xFF) / 255.0f,
        static_cast<float>((argb >> 24) & 0xFF) / 255.0f,
    };
}

#if defined(__ANDROID__)
const char* kVertexShader = R"(
attribute vec2 aPosition;
uniform vec2 uProjection;
void main() {
    gl_Position = vec4(aPosition * uProjection, 0.0, 1.0);
}
)";

const char* kFragmentShader = R"(
precision mediump float;
uniform vec4 uColour;
void main() {
    gl_FragColor = uColour;
}
)";

/// Compiles a shader.
unsigned int compileShader(
    unsigned int type,
    const char* source) {
    const unsigned int shader = glCreateShader(type);
    glShaderSource(shader, 1, &source, nullptr);
    glCompileShader(shader);
    GLint compiled = 0;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
    if (compiled == 0) {
        glDeleteShader(shader);
        return 0;
    }
    return shader;
}
#endif

/// The tile coordinates a point falls in.
std::pair<int, int> tileFor(
    double latitude,
    double longitude,
    int zoom) {
    const double n = std::pow(2.0, zoom);
    const double x = std::floor((longitude + 180.0) / 360.0 * n);
    const double latRad = latitude * M_PI / 180.0;
    const double y = std::floor(
        (1.0 - std::log(std::tan(latRad) + 1.0 / std::cos(latRad)) / M_PI) / 2.0 * n);
    return {static_cast<int>(x), static_cast<int>(y)};
}

/// A key for the decoded tiles.
std::string tileKey(
    int zoom,
    int x,
    int y) {
    return std::to_string(zoom) + "/" + std::to_string(x) + "/" + std::to_string(y);
}

}  // namespace

Renderer::Renderer() = default;

Renderer::~Renderer() = default;

void Renderer::resize(
    int width,
    int height) {
    mCamera.width = width;
    mCamera.height = height;
}

std::string Renderer::tileUrl(
    const std::string& template_,
    int zoom,
    int x,
    int y) const {
    // The same expansion the tile source does, so the engine and the
    // renderer agree on what a tile is.
    std::string out;
    out.reserve(template_.size() + 32);
    for (size_t i = 0; i < template_.size(); ++i) {
        if (template_[i] != '{') {
            out.push_back(template_[i]);
            continue;
        }
        const size_t end = template_.find('}', i);
        if (end == std::string::npos) {
            out.push_back(template_[i]);
            continue;
        }
        const std::string token = template_.substr(i + 1, end - i - 1);
        if (token == "z") {
            out += std::to_string(zoom);
        } else if (token == "x") {
            out += std::to_string(x);
        } else if (token == "y") {
            out += std::to_string(y);
        } else if (token == "scale" || token == "ratio") {
            out += "1";
        } else if (token == "quadkey") {
            std::string key;
            for (int i = zoom; i >= 0; --i) {
                char digit = '0';
                const int mask = 1 << i;
                if (x & mask) {
                    digit = '1';
                }
                if (y & mask) {
                    digit = static_cast<char>(digit == '1' ? '3' : '2');
                }
                key.push_back(digit);
            }
            out += key;
        } else {
            out += template_.substr(i, end - i + 1);
        }
        i = end;
    }
    return out;
}

std::vector<std::pair<std::pair<int, int>, int>> Renderer::visibleTiles() const {
    std::vector<std::pair<std::pair<int, int>, int>> tiles;
    const int zoom = static_cast<int>(std::lround(mCamera.zoom));
    if (zoom < 0 || zoom > 18) {
        return tiles;
    }
    const auto centre = tileFor(mCamera.latitude, mCamera.longitude, zoom);
    // How many tiles fit across the screen, and a ring around them so the
    // edges are covered while the camera moves.
    const double tileSpan =
        std::max(
            1.0,
            static_cast<double>(mCamera.width) / mCamera.tileSize);
    const int across = static_cast<int>(std::ceil(tileSpan / 2.0)) + 1;
    const int down = static_cast<int>(std::ceil(
                           static_cast<double>(mCamera.height) / mCamera.tileSize / 2.0)) + 1;
    const int limit = 1 << zoom;
    for (int dy = -down; dy <= down; ++dy) {
        for (int dx = -across; dx <= across; ++dx) {
            int x = centre.first + dx;
            int y = centre.second + dy;
            // The world wraps around; the poles do not.
            x = ((x % limit) + limit) % limit;
            if (y < 0 || y >= limit) {
                continue;
            }
            tiles.emplace_back(std::make_pair(x, y), zoom);
        }
    }
    return tiles;
}

void Renderer::featureVertices(
    const mvt::Feature& feature,
    int zoom,
    int tileX,
    int tileY,
    uint32_t extent,
    std::vector<float>& out) const {
    // The world position of the tile, and the scale of the view.
    const double tilesPerAxis = std::pow(2.0, zoom);
    const double tileWorldX = static_cast<double>(tileX) / tilesPerAxis;
    const double tileWorldY = static_cast<double>(tileY) / tilesPerAxis;
    const double tileWorldSpan = 1.0 / tilesPerAxis;

    const double centreX = geo::normalizeX(mCamera.longitude);
    const double centreY = geo::normalizeY(mCamera.latitude);
    const double spanX = (static_cast<double>(mCamera.width) / mCamera.tileSize) * tileWorldSpan;
    const double spanY = (static_cast<double>(mCamera.height) / mCamera.tileSize) * tileWorldSpan;

    const double cosBearing = std::cos(mCamera.bearing * M_PI / 180.0);
    const double sinBearing = std::sin(mCamera.bearing * M_PI / 180.0);

    // A point in the tile becomes a point on the screen.
    auto toClip = [&](double px, double py) {
        const double wx = tileWorldX + (px / extent) * tileWorldSpan;
        const double wy = tileWorldY + (py / extent) * tileWorldSpan;
        double dx = (wx - centreX) / spanX;
        double dy = (wy - centreY) / spanY;
        // The screen's y grows downwards; the clip space's grows upwards.
        dy = -dy;
        if (mCamera.bearing != 0.0) {
            const double rx = dx * cosBearing - dy * sinBearing;
            const double ry = dx * sinBearing + dy * cosBearing;
            dx = rx;
            dy = ry;
        }
        return std::make_pair(static_cast<float>(dx), static_cast<float>(dy));
    };

    if (feature.type == mvt::GeomType::Polygon) {
        // The rings of a polygon: the first is the outline, the rest are
        // holes in it.
        std::vector<std::vector<std::pair<float, float>>> rings;
        for (const auto& path : feature.paths) {
            if (path.size() < 3) {
                continue;
            }
            std::vector<std::pair<float, float>> ring;
            ring.reserve(path.size());
            for (const auto& point : path) {
                ring.push_back(toClip(point.first, point.second));
            }
            rings.push_back(std::move(ring));
        }
        if (rings.empty()) {
            return;
        }
        const auto triangles = polygon::triangulate(rings);
        for (const auto& triangle : triangles) {
            for (const auto& index : triangle) {
                const auto& point = rings[0][index];
                out.push_back(point.first);
                out.push_back(point.second);
            }
        }
        return;
    }

    // Lines and points are drawn as strips.
    for (const auto& path : feature.paths) {
        if (path.size() < 2) {
            continue;
        }
        for (const auto& point : path) {
            const auto clip = toClip(point.first, point.second);
            out.push_back(clip.first);
            out.push_back(clip.second);
        }
        // The strip has to be broken between one path and the next, or the
        // line joins them. Repeating the last point does it: the segment
        // between the two copies has no length, so the line does not move.
        const auto& last = path.back();
        out.push_back(toClip(last.first, last.second).first);
        out.push_back(toClip(last.first, last.second).second);
    }
}

void Renderer::draw() {
    mTilesDrawn = 0;
    mLayerCounts.clear();
#if defined(__ANDROID__)
    drawFrame();
#endif
}

#if defined(__ANDROID__)
void Renderer::drawFrame() {
    glViewport(0, 0, mCamera.width, mCamera.height);
    const Colour background = unpack(mStyle.background);
    glClearColor(background.r, background.g, background.b, background.a);
    glClear(GL_COLOR_BUFFER_BIT);

    if (!mGlReady) {
        const unsigned int vertex = compileShader(GL_VERTEX_SHADER, kVertexShader);
        const unsigned int fragment = compileShader(GL_FRAGMENT_SHADER, kFragmentShader);
        if (vertex == 0 || fragment == 0) {
            return;
        }
        mProgram = glCreateProgram();
        glAttachShader(mProgram, vertex);
        glAttachShader(mProgram, fragment);
        // The attribute has to be bound before the program is linked, or its
        // place is whatever the driver decides and the geometry is read from
        // the wrong memory.
        glBindAttribLocation(mProgram, 0, "aPosition");
        glLinkProgram(mProgram);
        glDeleteShader(vertex);
        glDeleteShader(fragment);
        mProjectionUniform = glGetUniformLocation(mProgram, "uProjection");
        mColourUniform = glGetUniformLocation(mProgram, "uColour");
        glGenBuffers(1, &mBuffer);
        mGlReady = true;
    }

    if (mProgram == 0) {
        return;
    }
    glUseProgram(mProgram);
    // The geometry is already in clip space: the scale of the view is
    // worked out when the points are made.
    glUniform2f(mProjectionUniform, 1.0f, 1.0f);

    // The buffer has to be bound before the pointer is set: the pointer
    // takes the buffer that is bound at the time, not the one that is bound
    // afterwards.
    glBindBuffer(GL_ARRAY_BUFFER, mBuffer);
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(0, 2, GL_FLOAT, GL_FALSE, 0, nullptr);

    const auto tiles = visibleTiles();
    for (const auto& entry : tiles) {
        const int zoom = entry.second;
        const int tileX = entry.first.first;
        const int tileY = entry.first.second;
        const std::string key = tileKey(zoom, tileX, tileY);

        auto decoded = mDecoded.find(key);
        if (decoded == mDecoded.end()) {
            if (mCache == nullptr || mUrlTemplate.empty()) {
                continue;
            }
            std::vector<uint8_t> bytes;
            if (!mCache->get(tileUrl(mUrlTemplate, zoom, tileX, tileY), bytes)) {
                continue;
            }
            mvt::Tile tile;
            if (!mvt::decode(bytes.data(), bytes.size(), tile)) {
                continue;
            }
            decoded = mDecoded.emplace(key, std::move(tile)).first;
        }

        mTilesDrawn++;

        // The polygons first, so the roads are on top of them.
        struct LayerStyle {
            const char* name;
            uint32_t colour;
            bool line;
        };
        const std::vector<LayerStyle> fills = {
            {"landuse", mStyle.landuse, false},
            {"water", mStyle.water, false},
            {"building", mStyle.building, false},
        };
        for (const LayerStyle& layer : fills) {
            const mvt::Layer* found = decoded->second.layer(layer.name);
            if (found == nullptr) {
                continue;
            }
            std::vector<float> vertices;
            for (const mvt::Feature& feature : found->features) {
                featureVertices(feature, zoom, tileX, tileY, found->extent, vertices);
            }
            if (vertices.empty()) {
                continue;
            }
            const Colour colour = unpack(layer.colour);
            glUniform4f(mColourUniform, colour.r, colour.g, colour.b, colour.a);
            glBufferData(
                GL_ARRAY_BUFFER,
                static_cast<GLsizeiptr>(vertices.size() * sizeof(float)),
                vertices.data(),
                GL_STREAM_DRAW);
            glDrawArrays(GL_TRIANGLES, 0, static_cast<GLsizei>(vertices.size() / 2));
            mLayerCounts[layer.name] += static_cast<int>(found->features.size());
        }

        // The roads, the biggest first.
        const mvt::Layer* transport = decoded->second.layer("transportation");
        if (transport != nullptr) {
            for (size_t index = 0; index < mStyle.roads.size(); ++index) {
                const std::string& className = mStyle.roads[index].first;
                std::vector<float> vertices;
                int count = 0;
                for (const mvt::Feature& feature : transport->features) {
                    if (feature.property("class") != className) {
                        continue;
                    }
                    featureVertices(feature, zoom, tileX, tileY, transport->extent, vertices);
                    count++;
                }
                if (vertices.empty()) {
                    continue;
                }
                const Colour colour = unpack(mStyle.roads[index].second);
                glUniform4f(mColourUniform, colour.r, colour.g, colour.b, colour.a);
                glLineWidth(static_cast<GLfloat>(mStyle.roadWidths[index]));
                glBufferData(
                    GL_ARRAY_BUFFER,
                    static_cast<GLsizeiptr>(vertices.size() * sizeof(float)),
                    vertices.data(),
                    GL_STREAM_DRAW);
                glDrawArrays(GL_LINE_STRIP, 0, static_cast<GLsizei>(vertices.size() / 2));
                mLayerCounts["transportation"] += count;
            }
        }
    }

    glDisableVertexAttribArray(0);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
}
#endif

}  // namespace fatmap::render
