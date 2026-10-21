// The engine's rendering.
//
// This is what puts the pieces together: a thread with an OpenGL context on
// the surface the application gave us, the renderer that draws the tiles,
// and the tile cache the renderer fetches through.
//
// The application never sees this: it gives the engine a surface through the
// Fatmap API and moves the camera, and the map appears.

#pragma once

#include <atomic>
#include <chrono>
#include <functional>
#include <memory>
#include <string>

#include "gl/gl_context.h"
#include "render/renderer.h"
#include "tiles/tile_cache.h"

namespace fatmap::render {

/// The URL template of the tiles the engine draws.
/// The tile set the engine draws by default.
inline constexpr const char* kOpenFreeMapTileSet = "https://tiles.openfreemap.org/planet";

/// The engine's rendering.
class MapHost {
  public:
    MapHost();
    ~MapHost();

    MapHost(const MapHost&) = delete;
    MapHost& operator=(const MapHost&) = delete;

    /// Starts rendering into a surface. The window is an ANativeWindow*.
    void start(
        void* window,
        int width,
        int height);

    /// Stops rendering.
    void stop();

    /// Whether the renderer is running.
    bool isRunning() const { return mRunning.load(); }

    /// Moves the camera. Safe to call from any thread.
    void setCamera(
        double latitude,
        double longitude,
        double zoom,
        double bearing);

    /// The tiles that were drawn in the last frame.
    int tilesDrawn() const { return mTilesDrawn.load(); }

    /// The URL template of the tiles.
    void setTileUrlTemplate(const std::string& template_) { mUrlTemplate = template_; }

    /// The address the tile template is read from.
    ///
    /// A tile set is versioned: the address of the tiles changes when the
    /// set is republished, so the real template has to be read from the
    /// tile set's own description rather than guessed.
    void setTileSetUrl(const std::string& url) { mTileSetUrl = url; }

    /// Sets the fetcher the tiles are fetched with.
    void setFetcher(tiles::Fetcher fetcher) { mCache.setFetcher(std::move(fetcher)); }

    /// Sets where the camera is. The engine's camera changes, and the
    /// renderer asks for it every frame.
    void setCameraProvider(std::function<Camera()> provider) { mCameraProvider = std::move(provider); }

    /// Sets what the engine does each frame, so its camera transitions
    /// actually run.
    void setAdvance(std::function<void(double seconds)> advance) { mAdvance = std::move(advance); }

    /// The last error, if there was one.
    std::string lastError() const;

  private:
    /// Reads the real address of the tiles from the tile set.
    void resolveTileTemplate();

  public:

  private:
    void drawFrame();

    tiles::TileCache mCache;
    std::unique_ptr<Renderer> mRenderer;
    std::string mUrlTemplate;
    std::string mTileSetUrl = kOpenFreeMapTileSet;

#if defined(__ANDROID__)
    std::unique_ptr<gl::Context> mContext;
#endif

    std::atomic<bool> mRunning{false};
    std::atomic<int> mTilesDrawn{0};
    std::function<Camera()> mCameraProvider;
    std::function<void(double seconds)> mAdvance;
    std::atomic<double> mLatitude{0.0};
    std::atomic<double> mLongitude{0.0};
    std::atomic<double> mZoom{2.0};
    std::atomic<double> mBearing{0.0};
    std::atomic<bool> mTemplateResolved{false};
    int mFrames = 0;
    std::chrono::steady_clock::time_point mLastFrame{};
    std::atomic<int> mWidth{0};
    std::atomic<int> mHeight{0};
};

}  // namespace fatmap::render
