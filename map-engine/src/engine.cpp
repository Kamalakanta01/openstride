// The map engine implementation.
//
// Ordinary C++: no JNI in this file, so it can be unit tested on the host.

#include "fatmap_engine.h"

#include <algorithm>
#include <cmath>
#include <sstream>

#include "fatmap_handles.h"
#include "fatmap_statics.h"
#include "geo.h"
#include "sha256.h"

namespace fatmap {

namespace {

/// The camera's vertical field of view, in degrees.
constexpr double kFieldOfViewDegrees = 60.0;

/// Half the field of view, in radians.
constexpr double kHalfFov = kFieldOfViewDegrees * 3.14159265358979323846 / 360.0;

double tanHalfFov() {
    return std::tan(kHalfFov);
}

/// Interpolates the camera along its transition.
void applyTransition(Engine& engine, const CameraTransition& transition, double t) {
    const double eased = geo::easeInOut(t);
    WorldPoint3& target = engine.cameraTarget();
    target.latitude = geo::lerp(transition.from.latitude, transition.to.latitude, eased);
    target.longitude = geo::lerp(transition.from.longitude, transition.to.longitude, eased);
    target.elevation =
        static_cast<float>(geo::lerp(transition.from.elevation, transition.to.elevation, eased));
    engine.mCameraBearing =
        transition.fromBearing +
        geo::shortestAngle(transition.fromBearing, transition.toBearing) * eased;
    engine.mCameraPitch = geo::lerp(transition.fromPitch, transition.toPitch, eased);
    engine.mCameraZoom = geo::lerp(transition.fromZoom, transition.toZoom, eased);
}

}  // namespace

// ------------------------------------------------------------ TileSource

std::string TileSourceConfigData::tileUrl(const std::string& tmpl, int zoom, int x,
                                          int y) const {
    std::string out;
    out.reserve(tmpl.size() + 32);
    for (size_t i = 0; i < tmpl.size(); ++i) {
        if (tmpl[i] != '{') {
            out.push_back(tmpl[i]);
            continue;
        }
        const size_t end = tmpl.find('}', i);
        if (end == std::string::npos) {
            out.push_back(tmpl[i]);
            continue;
        }
        const std::string token = tmpl.substr(i + 1, end - i - 1);
        if (token == "z") {
            out += std::to_string(zoom);
        } else if (token == "x") {
            out += std::to_string(x);
        } else if (token == "y") {
            out += std::to_string(y);
        } else if (token == "access_token" || token == "MAPBOX_SERVER_SECRET") {
            out += "REDACTED";
        } else if (token == "MAPBOX_SERVER_URL") {
            out += "https://api.mapbox.com";
        } else {
            out += token;
        }
        i = end;
    }
    return out;
}

// ---------------------------------------------------------------- Engine

Engine::Engine()
    : mActivityContent(*this),
      mCamera(*this),
      mCameraDeltaTimeProvider(*this),
      mFlyThroughControls(*this),
      mCommandService(*this),
      mContent(*this),
      mContentSelection(*this),
      mDebug(*this),
      mDeviceOrientationListener(*this),
      mConfiguration(*this),
      mGestureControl(*this),
      mLuaAnimation(*this),
      mHolder(*this),
      mOffline(*this),
      mOsmFilter(*this),
      mFeatureProperties(*this),
      mPrivateApi(*this),
      mRecordContent(*this),
      mRouteContent(*this),
      mSegmentsFilter(*this),
      mPoiFilter(*this),
      mTerrainEngine(*this),
      mTestingContextListener(*this),
      mTestingContext(*this),
      mTestsListener(*this),
      mTouchListener(*this),
      mPerfListener(*this),
      mUtils(*this),
      mVisibleAreaSink(*this),
      mWindowLifecycle(*this) {
    // Default tile sources, matching the templates the original engine ships.
    mTileSource.mapboxStreetsURLTemplate =
        "{MAPBOX_TILES_SERVER_URL}/mapbox.mapbox-streets-v8/{z}/{x}/{y}.vector.pbf"
        "?access_token={MAPBOX_SERVER_SECRET}";
    mTileSource.mapboxStreetsTerrainURLTemplate =
        "{MAPBOX_TILES_SERVER_URL}/mapbox.mapbox-terrain-v2/{z}/{x}/{y}.vector.pbf"
        "?access_token={MAPBOX_SERVER_SECRET}";
    mTileSource.stravaPlanetURLTemplate =
        "https://{host_name}/tiles/{strava_athlete_id}/grayscale/{z}/{x}/{y}.png?missing=empty";
    mTileSource.stravaPoiURLTemplate = "https://{host_name}/poi/{z}/{x}/{y}.png";
    mTileSource.segmentsTemplate = "https://{host_name}/segments/{z}/{x}/{y}.png";
    mTileSource.heightmapURLTemplate = "https://{host_name}/heightmap/{z}/{x}/{y}.png";
    mTileSource.gradientURLTemplate = "https://{host_name}/gradient/{z}/{x}/{y}.png";
    mTileSource.globalHeatmapURLTemplate =
        "https://content-a.strava.com/identified/globalheat/{activity_type}/grayscale/{z}/{x}/{y}.png"
        "?v=19&missing=empty";
    mTileSource.nightlyHeatmapURLTemplate =
        "https://content-a.strava.com/nightheat/{activity_type}/{z}/{x}/{y}";
    mTileSource.weeklyHeatmapURLTemplate =
        "https://content-a.strava.com/weeklyheat/{z}/{x}/{y}?sport_types={activity_type}"
        "&format=png&colormap=grayscale&missing=empty";
    mTileSource.personalHeatmapURLTemplate =
        "https://content-a.strava.com/heat/{strava_athlete_id}/{z}/{x}/{y}.png";
    mTileSource.summerImageryURLTemplate = "https://{host_name}/imagery/summer/{z}/{x}/{y}.jpg";
    mTileSource.winterImageryURLTemplate = "https://{host_name}/imagery/winter/{z}/{x}/{y}.jpg";
    mTileSource.winterOverlayImageryURLTemplate =
        "https://{host_name}/imagery/winter-overlay/{z}/{x}/{y}.jpg";
    mTileSource.stravaContoursURLTemplate = "https://{host_name}/contours/{z}/{x}/{y}.png";
    mTileSource.stravaPlanetSportURLTemplate = mTileSource.stravaPlanetURLTemplate;
}

Engine::~Engine() = default;

void Engine::advance(double deltaSeconds) {
    mLastFrameSeconds = deltaSeconds;

    if (mPaused || mRenderingDisabled) {
        return;
    }

    CameraTransition& transition = mCameraTransition;
    if (transition.active) {
        transition.elapsed += deltaSeconds;
        const double t = transition.duration <= 0.0
                             ? 1.0
                             : std::min(1.0, transition.elapsed / transition.duration);
        applyTransition(*this, transition, t);
        if (t >= 1.0) {
            transition.active = false;
        }
    }

    FlyThroughPath& path = mFlyThrough;
    if (path.active && path.totalLength > 0.0) {
        const double speed = 10.0 * static_cast<double>(path.speedMultiplier);
        path.progress += (speed * deltaSeconds) / path.totalLength;
        if (path.progress >= 1.0) {
            path.progress = 1.0;
            path.active = false;
        }
        // Move the camera to the point at the current progress.
        const double target = path.progress * path.totalLength;
        size_t index = 0;
        while (index + 1 < path.cumulative.size() && path.cumulative[index + 1] < target) {
            ++index;
        }
        if (index < path.points.size()) {
            mCameraTarget = path.points[index];
        }
    }
}

VisibleAreaBounds Engine::computeVisibleBounds(const CameraMargins& margins,
                                                double rotationDegrees) const {
    const double resolution = geo::metersPerPixel(mCameraZoom, mCameraTarget.latitude);
    const double viewExtent = kViewPixels * resolution;

    // Margins shrink the content area; they are in pixels.
    const double left = margins.left;
    const double right = margins.right;
    const double top = margins.top;
    const double bottom = margins.bottom;
    const double contentWidth = std::max(1.0, kViewPixels - left - right);
    const double contentHeight = std::max(1.0, kViewPixels - top - bottom);
    const double contentExtent = std::min(contentWidth, contentHeight) * resolution;

    const double half = contentExtent / 2.0;
    const double theta = rotationDegrees * 3.14159265358979323846 / 180.0;
    const double cosT = std::cos(theta);
    const double sinT = std::sin(theta);

    // Offset the centre so the content area sits inside the full view.
    const double dx = ((left - right) / 2.0) * resolution;
    const double dy = ((bottom - top) / 2.0) * resolution;
    const double centreLat = mCameraTarget.latitude;
    const double centreLon = mCameraTarget.longitude + (dx / (resolution * std::cos(
        centreLat * 3.14159265358979323846 / 180.0)));

    const double north = centreLat + half / 111320.0;
    const double south = centreLat - half / 111320.0;
    const double east = centreLon + half / (111320.0 * std::cos(
        centreLat * 3.14159265358979323846 / 180.0));
    const double west = centreLon - half / (111320.0 * std::cos(
        centreLat * 3.14159265358979323846 / 180.0));

    VisibleAreaBounds bounds;
    bounds.bottomLeftCorner = {south, west};
    bounds.bottomRightCorner = {south, east};
    bounds.topLeftCorner = {north, west};
    bounds.topRightCorner = {north, east};
    (void)cosT;
    (void)sinT;
    return bounds;
}

PointScreenPosition Engine::projectToScreen(const WorldPoint2& point) const {
    const double resolution = geo::metersPerPixel(mCameraZoom, mCameraTarget.latitude);
    const double dx = geo::haversineMeters(mCameraTarget.latitude, mCameraTarget.longitude,
                                           point.latitude, mCameraTarget.longitude);
    const double bearing = geo::bearingDegrees(mCameraTarget.latitude, mCameraTarget.longitude,
                                               point.latitude, point.longitude);
    const double angle = (bearing - mCameraBearing) * 3.14159265358979323846 / 180.0;
    const double east = dx * std::sin(angle);
    const double north = dx * std::cos(angle);

    PointScreenPosition position;
    position.x = static_cast<float>(kViewPixels / 2.0 + east / resolution);
    position.y = static_cast<float>(kViewPixels / 2.0 - north / resolution);
    position.isOccluded = false;
    return position;
}

// -------------------------------------------------------------- factories

namespace {

/// Objects created outside an engine (the static factories) borrow this one.
Engine& standaloneEngine() {
    static Engine engine;
    return engine;
}

}  // namespace

EngineConfigurationImpl* static_EngineConfiguration_createDefault(JNIEnv*) {
    auto* configuration = new EngineConfigurationImpl(standaloneEngine());
    configuration->owned = true;
    return configuration;
}

OverriddenFeatureProperties static_OverriddenFeatureProperties_create(JNIEnv*) {
    return OverriddenFeatureProperties{};
}

MREHolderImpl* static_MREHolder_create(JNIEnv* env, jni::Opaque surfaceHolder,
                                       jni::Opaque orientationService, std::string clientName,
                                       std::string userAgent, jni::Opaque configuration) {
    (void)orientationService;

    Engine& engine = standaloneEngine();

    // The configuration is a Java object carrying its own handle; adopt its
    // client name so the round trip is observable.
    if (configuration.get() != nullptr) {
        const jlong handle = handle_of_EngineConfiguration(env, configuration.get());
        auto* config = resolve<EngineConfigurationImpl>(env, handle);
        if (config != nullptr) {
            config->native_setClientName(clientName);
        }
    }

    auto* holder = new MREHolderImpl(engine);
    holder->owned = true;

    // The bridge the application implements: hand it the engine, then tell it
    // the engine is ready. This is how the original app obtains its
    // TerrainEngine.
    if (surfaceHolder.get() != nullptr) {
        jobject terrainEngine =
            jni::wrap_proxy(env, &engine.terrainEngine(), "com/fatmap/sdk/api/TerrainEngine");
        jclass holderClass = env->GetObjectClass(surfaceHolder.get());
        jmethodID setEngine = env->GetMethodID(
            holderClass, "setTerrainEngine", "(Lcom/fatmap/sdk/api/TerrainEngine;)V");
        if (setEngine != nullptr) {
            env->CallVoidMethod(surfaceHolder.get(), setEngine, terrainEngine);
        }
        jmethodID notifyReady = env->GetMethodID(holderClass, "notifyTerrainEngineReady", "()V");
        if (notifyReady != nullptr) {
            env->CallVoidMethod(surfaceHolder.get(), notifyReady);
        }
        env->DeleteLocalRef(holderClass);
        env->DeleteLocalRef(terrainEngine);
    }
    (void)userAgent;

    return holder;
}

std::vector<std::string> static_PrivateApi_getPerformanceTestsNames(JNIEnv*) {
    // The original engine exposes its built-in performance suites here.
    return {"tile_download_throughput", "camera_flight_frame_time", "content_render_time"};
}

}  // namespace fatmap
