// The engine's rendering.
//
// See map_host.h.

#include "render/map_host.h"

#include <chrono>

#if defined(__ANDROID__)
#include <android/log.h>
#include <android/native_window.h>
#endif

namespace fatmap::render {

MapHost::MapHost() :
    mRenderer(std::make_unique<Renderer>()) {
    mRenderer->setTileCache(&mCache);
    mRenderer->setTileUrlTemplate(mUrlTemplate);
}

MapHost::~MapHost() {
    stop();
}

void MapHost::setCamera(
    double latitude,
    double longitude,
    double zoom,
    double bearing) {
    mLatitude.store(latitude);
    mLongitude.store(longitude);
    mZoom.store(zoom);
    mBearing.store(bearing);
}

void MapHost::start(
    void* window,
    int width,
    int height) {
    if (mRunning.exchange(true)) {
        return;
    }
    mWidth.store(width);
    mHeight.store(height);

#if defined(__ANDROID__)
    gl::Context::Callbacks callbacks;
    callbacks.draw = [this] { drawFrame(); };
    callbacks.resized = [this](int w, int h) {
        mWidth.store(w);
        mHeight.store(h);
        if (mRenderer) {
            mRenderer->resize(w, h);
        }
    };
    mContext = std::make_unique<gl::Context>(window, width, height, callbacks);
    mContext->start();
#else
    (void)window;
#endif
}

void MapHost::stop() {
    if (!mRunning.exchange(false)) {
        return;
    }
#if defined(__ANDROID__)
    if (mContext) {
        mContext->stop();
        mContext.reset();
    }
#endif
}

void MapHost::drawFrame() {
    if (!mRenderer) {
        return;
    }
    resolveTileTemplate();

    // The engine's own state moves on: a camera flight is a transition, and
    // a transition only moves when the engine is told the time has passed.
    if (mAdvance) {
        const auto now = std::chrono::steady_clock::now();
        double seconds = 1.0 / 60.0;
        if (mLastFrame.time_since_epoch().count() != 0) {
            seconds = std::chrono::duration<double>(now - mLastFrame).count();
        }
        mLastFrame = now;
        mAdvance(seconds);
    }
    Camera camera;
    if (mCameraProvider) {
        // The engine's own camera is the truth when there is one.
        camera = mCameraProvider();
    } else {
        camera.latitude = mLatitude.load();
        camera.longitude = mLongitude.load();
        camera.zoom = mZoom.load();
        camera.bearing = mBearing.load();
    }
    camera.width = mWidth.load();
    camera.height = mHeight.load();
    mRenderer->setCamera(camera);
    mRenderer->setTileUrlTemplate(mUrlTemplate);
    mRenderer->draw();
    mTilesDrawn.store(mRenderer->tilesDrawn());

    // A line every couple of seconds, so the engine can be seen working.
    if (++mFrames % 60 == 0) {
        __android_log_print(
            ANDROID_LOG_INFO,
            "fatmap",
            "the map: %d tiles, the camera at %.4f,%.4f zoom %.1f, the tiles at %s",
            mRenderer->tilesDrawn(),
            camera.latitude,
            camera.longitude,
            camera.zoom,
            mUrlTemplate.c_str());
    }
}

void MapHost::resolveTileTemplate() {
    if (mTemplateResolved || mTileSetUrl.empty()) {
        return;
    }
    mTemplateResolved = true;

    // The tile set's own description has the real address of the tiles.
    std::vector<uint8_t> bytes;
    if (!mCache.get(mTileSetUrl, bytes)) {
        return;
    }
    const std::string json(reinterpret_cast<const char*>(bytes.data()), bytes.size());
    const size_t at = json.find("\"tiles\"");
    if (at == std::string::npos) {
        return;
    }
    const size_t open = json.find('[', at);
    if (open == std::string::npos) {
        return;
    }
    const size_t quote = json.find('"', open);
    if (quote == std::string::npos) {
        return;
    }
    const size_t end = json.find('"', quote + 1);
    if (end == std::string::npos) {
        return;
    }
    mUrlTemplate = json.substr(quote + 1, end - quote - 1);
    if (mRenderer) {
        mRenderer->setTileUrlTemplate(mUrlTemplate);
    }
}

std::string MapHost::lastError() const {
#if defined(__ANDROID__)
    if (mContext) {
        return mContext->lastError();
    }
#endif
    return {};
}

}  // namespace fatmap::render
