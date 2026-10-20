// The proxy implementations.
//
// Each class here adapts one Java proxy class onto the shared engine state.
// The JNI marshalling lives in the generated layer; this file is plain C++.

#include <algorithm>
#include <cmath>
#include <fstream>
#include <sstream>

#include "fatmap_engine.h"
#include "geo.h"
#include "sha256.h"

namespace fatmap {

namespace {

constexpr double kPi = 3.14159265358979323846;
constexpr double kFieldOfViewDegrees = 60.0;

double tanHalfFov() {
    return std::tan(kFieldOfViewDegrees * kPi / 360.0);
}

/// The camera distance that shows the given zoom level in the canonical view.
double distanceForZoom(double zoom, double latitude) {
    const double extent = Engine::kViewPixels * geo::metersPerPixel(zoom, latitude);
    return (extent / 2.0) / tanHalfFov();
}

/// The zoom level that shows the given ground extent in the canonical view.
double zoomForDistance(double distance, double latitude) {
    const double extent = 2.0 * distance * tanHalfFov();
    const double resolution = extent / Engine::kViewPixels;
    const double world = 2.0 * kPi * geo::kEarthRadius *
                         std::cos(latitude * kPi / 180.0);
    return std::log2(world / (resolution * geo::kTileSize));
}

/// Starts a camera flight.
void beginFlight(Engine& engine, double latitude, double longitude, double zoom,
                 std::optional<float> bearing, std::optional<float> pitch, double duration) {
    CameraTransition& transition = engine.cameraTransition();
    transition.active = true;
    transition.elapsed = 0.0;
    transition.duration = std::max(0.0, static_cast<double>(duration));
    transition.from = engine.cameraTarget();
    transition.fromBearing = engine.cameraBearing();
    transition.fromPitch = engine.cameraPitch();
    transition.fromZoom = engine.cameraZoom();

    transition.to.latitude = latitude;
    transition.to.longitude = geo::wrapLongitude(longitude);
    transition.to.elevation = 0.0f;
    transition.toZoom = zoom;
    transition.toBearing = bearing.value_or(static_cast<float>(engine.cameraBearing()));
    transition.toPitch = pitch.value_or(static_cast<float>(engine.cameraPitch()));
}

ContentLine toContentLine(const WorldLine2& line, const std::string& identifier) {
    ContentLine content;
    content.identifier = identifier;
    content.points = line.points;
    return content;
}

}  // namespace

// ------------------------------------------------------------ ActivityContent

ActivityContentImpl::ActivityContentImpl(Engine& engine)
    : ActivityContentApi(), ProxyBase(ProxyKind::ActivityContent), mEngine(engine) {}

void ActivityContentImpl::nativeDestroy() {
    destroyIfOwned();
}

void ActivityContentImpl::native_displayActivity(std::string identifier, WorldLine2 line,
                                                 IntervalInt interval) {
    mEngine.activities()[identifier] = toContentLine(line, identifier);
}

void ActivityContentImpl::native_displayActivity2(std::string identifier, WorldLine2 line,
                                                   std::vector<ActivitySection> sections) {
    mEngine.activities()[identifier] = toContentLine(line, identifier);
}

void ActivityContentImpl::native_displayActivityTruncated(std::string identifier, WorldLine2 line,
                                                          IntervalInt interval) {
    mEngine.activities()[identifier] = toContentLine(line, identifier);
}

void ActivityContentImpl::native_displayActivityWithPrivacyIcons(std::string identifier,
                                                                  WorldLine2 line,
                                                                  IntervalInt interval) {
    mEngine.activities()[identifier] = toContentLine(line, identifier);
}

void ActivityContentImpl::native_removeActivity(std::string identifier) {
    mEngine.activities().erase(identifier);
}

void ActivityContentImpl::native_removeAllActivities() {
    mEngine.activities().clear();
}

void ActivityContentImpl::native_applyActivityGradient(std::string identifier,
                                                       std::vector<Color> colours) {
    auto it = mEngine.activities().find(identifier);
    if (it != mEngine.activities().end()) {
        it->second.colour = colours.empty() ? "" : "gradient";
    }
}

void ActivityContentImpl::native_clearActivityGradient(std::string identifier) {
    auto it = mEngine.activities().find(identifier);
    if (it != mEngine.activities().end()) {
        it->second.colour.clear();
    }
}

void ActivityContentImpl::native_applyActivityPolylineHighlights(
    std::string identifier, std::vector<PolylineHighlightSection> sections) {
    (void)sections;
    (void)identifier;
}

void ActivityContentImpl::native_clearActivityPolylineHighlights(std::string identifier) {
    (void)identifier;
}

// --------------------------------------------------------------------- Camera

CameraImpl::CameraImpl(Engine& engine)
    : CameraApi(), ProxyBase(ProxyKind::Camera), mEngine(engine) {}

void CameraImpl::nativeDestroy() {
    destroyIfOwned();
}

void CameraImpl::native_flyTo(double latitude, double longitude, float zoom,
                              std::optional<float> bearing, std::optional<float> pitch,
                              float duration, CameraMargins margins, jni::Opaque listener) {
    (void)margins;
    (void)listener;
    beginFlight(mEngine, latitude, longitude, zoom, bearing, pitch, duration);
}

void CameraImpl::native_flyToWithZoomLevel(double latitude, double longitude, float zoom,
                                           std::optional<float> bearing,
                                           std::optional<float> pitch, float duration,
                                           CameraMargins margins, jni::Opaque listener) {
    native_flyTo(latitude, longitude, zoom, bearing, pitch, duration, margins, listener);
}

void CameraImpl::native_translateTo(WorldPoint2 target, float duration, CameraMargins margins,
                                    jni::Opaque listener) {
    (void)listener;
    const double zoom = mEngine.cameraZoom();
    beginFlight(mEngine, target.latitude, target.longitude, zoom, std::nullopt, std::nullopt,
                duration);
}

void CameraImpl::native_flyToBounds(WorldBounds2 bounds, std::optional<float> bearing,
                                    std::optional<float> pitch, float duration,
                                    CameraMargins margins, jni::Opaque listener) {
    (void)listener;
    const double centreLat =
        (bounds.northEastCorner.latitude + bounds.southWestCorner.latitude) / 2.0;
    const double centreLon =
        (bounds.northEastCorner.longitude + bounds.southWestCorner.longitude) / 2.0;
    const double northSouth = std::abs(bounds.northEastCorner.latitude -
                                       bounds.southWestCorner.latitude);
    const double extent = northSouth * 111320.0;
    const double resolution = extent / std::max(1.0, Engine::kViewPixels);
    const double world = 2.0 * kPi * geo::kEarthRadius * std::cos(centreLat * kPi / 180.0);
    const double zoom = std::log2(world / (resolution * geo::kTileSize));
    beginFlight(mEngine, centreLat, centreLon, zoom, bearing, pitch, duration);
}

void CameraImpl::native_flyToTarget(CameraTarget target, float duration, bool animate,
                                    CameraMargins margins, jni::Opaque listener) {
    (void)listener;
    if (!animate) {
        mEngine.cameraTarget() = target.lookAtPoint;
        mEngine.mCameraBearing = target.bearing;
        mEngine.mCameraPitch = target.pitch;
        mEngine.mCameraZoom = zoomForDistance(target.distance, target.lookAtPoint.latitude);
        mEngine.mCameraDistance = target.distance;
        return;
    }
    beginFlight(mEngine, target.lookAtPoint.latitude, target.lookAtPoint.longitude,
                zoomForDistance(target.distance, target.lookAtPoint.latitude),
                target.bearing, target.pitch, duration);
}

void CameraImpl::native_setBearing(float bearing, float duration, CameraMargins margins) {
    (void)duration;
    (void)margins;
    mEngine.mCameraBearing = bearing;
}

void CameraImpl::native_followUserLocation(std::optional<float> bearing,
                                            std::optional<float> pitch,
                                            std::optional<float> zoom, FollowMode followMode,
                                            CameraMargins margins) {
    (void)margins;
    if (bearing) {
        mEngine.mCameraBearing = *bearing;
    }
    if (pitch) {
        mEngine.mCameraPitch = *pitch;
    }
    if (zoom) {
        mEngine.mCameraZoom = *zoom;
        mEngine.mCameraDistance = distanceForZoom(*zoom, mEngine.cameraTarget().latitude);
    }
    (void)followMode;
}

void CameraImpl::native_followUserLocationWithZoomLevel(WorldPoint2 position,
                                                         std::optional<float> bearing,
                                                         std::optional<float> pitch,
                                                         std::optional<float> zoom,
                                                         FollowMode followMode,
                                                         CameraMargins margins) {
    mEngine.cameraTarget().latitude = position.latitude;
    mEngine.cameraTarget().longitude = position.longitude;
    native_followUserLocation(bearing, pitch, zoom, followMode, margins);
}

void CameraImpl::native_flyThrough(std::vector<WorldPoint3> points, std::string identifier,
                                   std::string style, CameraMargins margins,
                                   jni::Opaque progressListener, CameraTargetPlus target,
                                   std::vector<std::optional<float>> speeds) {
    (void)progressListener;
    (void)target;
    FlyThroughPath& path = mEngine.flyThrough();
    path.points = std::move(points);
    path.identifier = identifier;
    path.style = style;
    path.cumulative.clear();
    path.cumulative.reserve(path.points.size());
    path.totalLength = 0.0;
    for (size_t i = 0; i < path.points.size(); ++i) {
        if (i > 0) {
            const WorldPoint3& a = path.points[i - 1];
            const WorldPoint3& b = path.points[i];
            path.totalLength += geo::haversineMeters(a.latitude, a.longitude, b.latitude,
                                                      b.longitude);
        }
        path.cumulative.push_back(path.totalLength);
    }
    path.progress = 0.0;
    path.active = path.points.size() > 1;
    mEngine.setFlyThroughControlsActive(path.active);
    if (!speeds.empty() && speeds[0]) {
        path.speedMultiplier = *speeds[0];
    }
}

CameraFlyThroughControlsImpl* CameraImpl::native_flyThroughInteractive(
    std::vector<WorldPoint3> points, std::string identifier, std::string style,
    CameraMargins margins, jni::Opaque progressListener, CameraTargetPlus target,
    bool interactive, jni::Opaque deltaTimeProvider,
    std::vector<std::optional<float>> speeds) {
    native_flyThrough(std::move(points), identifier, style, margins, progressListener, target,
                      speeds);
    (void)interactive;
    (void)deltaTimeProvider;
    return &mEngine.flyThroughControls();
}

CameraFlyThroughControlsImpl* CameraImpl::native_flyover(
    bool firstPerson, std::vector<WorldPoint3> points, CameraMargins margins,
    jni::Opaque progressListener, CameraTargetPlus target,
    jni::Opaque deltaTimeProvider,
    std::vector<std::optional<float>> speeds) {
    if (firstPerson && !points.empty()) {
        mEngine.cameraTarget() = points.front();
    }
    native_flyThrough(std::move(points), "flyover", "flyover", margins, progressListener, target,
                      speeds);
    (void)deltaTimeProvider;
    return &mEngine.flyThroughControls();
}

void CameraImpl::native_enterFirstPersonView(double latitude, double longitude,
                                              float elevation) {
    mEngine.cameraTarget().latitude = latitude;
    mEngine.cameraTarget().longitude = longitude;
    mEngine.cameraTarget().elevation = elevation;
    mEngine.mCameraViewType = static_cast<int>(CameraViewType::CAMERA3D);
}

LookAtPoint CameraImpl::native_getLookAtPoint() {
    LookAtPoint point;
    point.worldPoint = mEngine.cameraTarget();
    point.isPointingAtMap = true;
    return point;
}

LookAtPoint CameraImpl::native_getCustomLookAtPoint(float longitude, float latitude) {
    LookAtPoint point;
    point.worldPoint.latitude = latitude;
    point.worldPoint.longitude = longitude;
    point.isPointingAtMap = true;
    return point;
}

void CameraImpl::native_stop() {
    mEngine.cameraTransition().active = false;
    mEngine.flyThrough().active = false;
    mEngine.setFlyThroughControlsActive(false);
}

CameraTarget CameraImpl::native_getTarget() {
    CameraTarget target;
    target.lookAtPoint = mEngine.cameraTarget();
    target.bearing = static_cast<float>(mEngine.cameraBearing());
    target.pitch = static_cast<float>(mEngine.cameraPitch());
    target.distance = static_cast<float>(mEngine.cameraDistance());
    return target;
}

void CameraImpl::native_setCameraViewType(CameraViewType viewType) {
    mEngine.mCameraViewType = static_cast<int>(viewType);
}

CameraViewType CameraImpl::native_getCameraViewType() {
    return static_cast<CameraViewType>(mEngine.cameraViewType());
}

bool CameraImpl::native_isWorldPointVisible(WorldPoint3 point, bool ignoreOcclusion) {
    (void)ignoreOcclusion;
    const VisibleAreaBounds bounds = mEngine.computeVisibleBounds(CameraMargins{}, 0.0f);
    return point.latitude >= bounds.bottomLeftCorner.latitude &&
           point.latitude <= bounds.topLeftCorner.latitude &&
           point.longitude >= bounds.bottomLeftCorner.longitude &&
           point.longitude <= bounds.bottomRightCorner.longitude;
}

VisibleAreaBounds CameraImpl::native_getVisibleAreaBounds(CameraMargins margins, float rotation) {
    return mEngine.computeVisibleBounds(margins, rotation);
}

PointScreenPosition CameraImpl::native_getScreenPosition(WorldPoint2 point) {
    return mEngine.projectToScreen(point);
}

float CameraImpl::native_getScaleMetersPerPixel() {
    return static_cast<float>(geo::metersPerPixel(mEngine.cameraZoom(),
                                                    mEngine.cameraTarget().latitude));
}

void CameraImpl::native_setZoomSpeed(float speed) {
    mEngine.mCameraDistance = speed > 0.0f ? mEngine.cameraDistance() / speed
                                            : mEngine.cameraDistance();
}

void CameraImpl::native_setRotationSpeed(float speed, float duration) {
    (void)speed;
    (void)duration;
}

void CameraImpl::native_setTranslationSpeed(float speed, float duration) {
    (void)speed;
    (void)duration;
}

void CameraImpl::native_setDynamicExaggerationEnabled(bool enabled) {
    mEngine.mDynamicExaggerationEnabled = enabled;
}

void CameraImpl::native_setDynamicExaggerationMultiplier(float multiplier) {
    mEngine.mDynamicExaggerationMultiplier = multiplier;
}

// ------------------------------------------------------- CameraFlyThrough

CameraDeltaTimeProviderImpl::CameraDeltaTimeProviderImpl(Engine& engine)
    : CameraDeltaTimeProviderApi(), ProxyBase(ProxyKind::CameraDeltaTimeProvider),
      mEngine(engine) {}

void CameraDeltaTimeProviderImpl::nativeDestroy() {
    destroyIfOwned();
}

float CameraDeltaTimeProviderImpl::native_getDeltaTime() {
    return static_cast<float>(mEngine.lastFrameSeconds());
}

CameraFlyThroughControlsImpl::CameraFlyThroughControlsImpl(Engine& engine)
    : CameraFlyThroughControlsApi(), ProxyBase(ProxyKind::CameraFlyThroughControls),
      mEngine(engine) {}

void CameraFlyThroughControlsImpl::nativeDestroy() {
    destroyIfOwned();
}

bool CameraFlyThroughControlsImpl::native_isActive() {
    return mEngine.flyThroughControlsActive();
}

void CameraFlyThroughControlsImpl::native_stop() {
    mEngine.flyThrough().active = false;
    mEngine.setFlyThroughControlsActive(false);
}

bool CameraFlyThroughControlsImpl::native_isCameraDetached() {
    return false;
}

bool CameraFlyThroughControlsImpl::native_isRefocusPossible() {
    return mEngine.flyThrough().active;
}

void CameraFlyThroughControlsImpl::native_refocus() {
    mEngine.flyThrough().progress = 0.0;
}

void CameraFlyThroughControlsImpl::native_seekToAnimationStart() {
    mEngine.flyThrough().progress = 0.0;
}

void CameraFlyThroughControlsImpl::native_setAnimationSpeedMultiplier(float multiplier) {
    mEngine.flyThrough().speedMultiplier = multiplier;
}

void CameraFlyThroughControlsImpl::native_setCameraDistanceMultiplier(float multiplier) {
    mEngine.flyThrough().cameraDistanceMultiplier = multiplier;
}

void CameraFlyThroughControlsImpl::native_setTrackProgress(float progress) {
    mEngine.flyThrough().progress = std::clamp(progress, 0.0f, 1.0f);
}

void CameraFlyThroughControlsImpl::native_setTrackSpeedMultiplier(float multiplier) {
    mEngine.flyThrough().speedMultiplier = multiplier;
}

float CameraFlyThroughControlsImpl::native_getDurationSeconds() {
    return static_cast<float>(mEngine.flyThrough().totalLength / 10.0);
}

float CameraFlyThroughControlsImpl::native_getTrackProgress() {
    return static_cast<float>(mEngine.flyThrough().progress);
}

float CameraFlyThroughControlsImpl::native_getTrackSpeedMultiplier() {
    return mEngine.flyThrough().speedMultiplier;
}

// ----------------------------------------------------------------- Command

CommandServiceImpl::CommandServiceImpl(Engine& engine)
    : CommandServiceApi(), ProxyBase(ProxyKind::CommandService), mEngine(engine) {}

void CommandServiceImpl::nativeDestroy() {
    destroyIfOwned();
}

std::string CommandServiceImpl::native_execute(std::string command) {
    // The original engine exposes a scripting console here. This
    // implementation answers the handful of introspection commands the
    // debug tooling uses and reports the rest as unsupported.
    if (command == "engine.state") {
        return mEngine.debug().native_dumpEngineState();
    }
    if (command == "engine.tilesource.url") {
        return mEngine.tileSource().tileUrl(mEngine.tileSource().mapboxStreetsURLTemplate, 0, 0, 0);
    }
    return "unsupported: " + command;
}

// ------------------------------------------------------------------ Content

ContentImpl::ContentImpl(Engine& engine)
    : ContentApi(), ProxyBase(ProxyKind::Content), mEngine(engine) {}

void ContentImpl::nativeDestroy() {
    destroyIfOwned();
}

int64_t ContentImpl::native_addCustomLine(WorldLine2 line, std::string colour, std::string width,
                                           bool dashed, std::string identifier) {
    const int64_t handle = mEngine.nextContentHandle();
    ContentLine content = toContentLine(line, identifier.empty() ? std::to_string(handle)
                                                                 : identifier);
    content.colour = colour;
    content.width = width;
    content.dashed = dashed;
    mEngine.lines()[std::to_string(handle)] = content;
    return handle;
}

int64_t ContentImpl::native_addCustomGradientLine(WorldLine2 line, std::string colour,
                                                   std::string width, std::string identifier,
                                                   std::vector<Color> colours) {
    const int64_t handle = native_addCustomLine(line, colour, width, false, identifier);
    if (!colours.empty()) {
        mEngine.lines()[std::to_string(handle)].colour = "gradient";
    }
    return handle;
}

bool ContentImpl::native_removeCustomLine(int64_t handle) {
    return mEngine.lines().erase(std::to_string(handle)) > 0;
}

void ContentImpl::native_addDroppedPin(std::string identifier, WorldPoint2 position) {
    ContentMarker marker;
    marker.identifier = identifier;
    marker.latitude = position.latitude;
    marker.longitude = position.longitude;
    mEngine.markers()[identifier] = marker;
}

void ContentImpl::native_removeDroppedPin(std::string identifier) {
    mEngine.markers().erase(identifier);
}

void ContentImpl::native_removeAllDroppedPins() {
    mEngine.markers().clear();
}

void ContentImpl::native_addCircleMarker(std::string identifier, double latitude,
                                          double longitude, float radius) {
    ContentMarker marker;
    marker.identifier = identifier;
    marker.latitude = latitude;
    marker.longitude = longitude;
    marker.category = "circle";
    mEngine.markers()[identifier] = marker;
}

void ContentImpl::native_removeCircleMarker(std::string identifier) {
    mEngine.markers().erase(identifier);
}

OsmFilterImpl* ContentImpl::native_getOsmFilter() {
    return &mEngine.osmFilter();
}

StravaPoiFilterImpl* ContentImpl::native_getStravaPoiFilter() {
    return &mEngine.poiFilter();
}

ActivityContentImpl* ContentImpl::native_getActivityContent() {
    return &mEngine.activityContent();
}

RecordContentImpl* ContentImpl::native_getRecordContent() {
    return &mEngine.recordContent();
}

RouteContentImpl* ContentImpl::native_getRouteContent() {
    return &mEngine.routeContent();
}

SegmentsFilterImpl* ContentImpl::native_getSegmentsFilter() {
    return &mEngine.segmentsFilter();
}

ContentSelectionHandleImpl::ContentSelectionHandleImpl(Engine& engine)
    : ContentSelectionHandleApi(), ProxyBase(ProxyKind::ContentSelectionHandle), mEngine(engine) {}

void ContentSelectionHandleImpl::nativeDestroy() {
    destroyIfOwned();
}

// ------------------------------------------------------------------- Debug

DebugApiImpl::DebugApiImpl(Engine& engine)
    : DebugApiApi(), ProxyBase(ProxyKind::DebugApi), mEngine(engine) {}

void DebugApiImpl::nativeDestroy() {
    destroyIfOwned();
}

void DebugApiImpl::native_showFpsStatsOverlay(DebugOverlayCorner corner,
                                              DebugOverlayPadding padding) {
    (void)padding;
    mEngine.mFpsOverlayVisible = true;
    mEngine.mFpsOverlayCorner = static_cast<int>(corner);
}

void DebugApiImpl::native_hideFpsStatsOverlay() {
    mEngine.mFpsOverlayVisible = false;
}

std::string DebugApiImpl::native_dumpEngineState() {
    std::ostringstream out;
    out << "OpenStride map engine\n";
    out << "  camera target    : " << mEngine.cameraTarget().latitude << ", "
        << mEngine.cameraTarget().longitude << "\n";
    out << "  bearing/pitch    : " << mEngine.cameraBearing() << " / "
        << mEngine.cameraPitch() << "\n";
    out << "  zoom / distance  : " << mEngine.cameraZoom() << " / "
        << mEngine.cameraDistance() << "\n";
    out << "  meters per pixel : " << geo::metersPerPixel(mEngine.cameraZoom(),
                                                          mEngine.cameraTarget().latitude)
        << "\n";
    out << "  view type        : " << mEngine.cameraViewType() << "\n";
    out << "  map type         : " << mEngine.mapType() << "\n";
    out << "  imagery overlay  : " << mEngine.imageryOverlay() << "\n";
    out << "  3d terrain       : " << (mEngine.terrain3dEnabled() ? "on" : "off") << "\n";
    out << "  offline mode     : " << (mEngine.offlineMode() ? "on" : "off") << "\n";
    out << "  pixel ratio      : " << mEngine.pixelRatio() << "\n";
    out << "  paused           : " << (mEngine.paused() ? "yes" : "no") << "\n";
    out << "  lines / markers  : " << mEngine.lines().size() << " / "
        << mEngine.markers().size() << "\n";
    out << "  activities       : " << mEngine.activities().size() << "\n";
    out << "  routes           : " << mEngine.routes().size() << "\n";
    out << "  bundles          : " << mEngine.bundles().size() << "\n";
    out << "  feature overrides: " << mEngine.booleanProperties().size() << " bool, "
        << mEngine.integerProperties().size() << " int, "
        << mEngine.doubleProperties().size() << " double, "
        << mEngine.stringProperties().size() << " string\n";
    return out.str();
}

// -------------------------------------------------------------- listeners

DeviceOrientationListenerImpl::DeviceOrientationListenerImpl(Engine& engine)
    : DeviceOrientationListenerApi(), ProxyBase(ProxyKind::DeviceOrientationListener),
      mEngine(engine) {}

void DeviceOrientationListenerImpl::nativeDestroy() {
    destroyIfOwned();
}

void DeviceOrientationListenerImpl::native_onDeviceOrientationChanged(DeviceOrientation orientation) {
    mEngine.mDeviceOrientation = static_cast<int>(orientation);
}

GestureRecognizerControlImpl::GestureRecognizerControlImpl(Engine& engine)
    : GestureRecognizerControlApi(), ProxyBase(ProxyKind::GestureRecognizerControl),
      mEngine(engine) {}

void GestureRecognizerControlImpl::nativeDestroy() {
    destroyIfOwned();
}

void GestureRecognizerControlImpl::native_setDoubleTapGesturesEnabled(bool enabled) {
    mEngine.mDoubleTapGesturesEnabled = enabled;
}

void GestureRecognizerControlImpl::native_setAllGesturesEnabled(bool enabled) {
    mEngine.mAllGesturesEnabled = enabled;
}

LuaAnimationHandlerImpl::LuaAnimationHandlerImpl(Engine& engine)
    : LuaAnimationHandlerApi(), ProxyBase(ProxyKind::LuaAnimationHandler), mEngine(engine) {}

void LuaAnimationHandlerImpl::nativeDestroy() {
    destroyIfOwned();
}

bool LuaAnimationHandlerImpl::native_shouldAdvanceAnimation() {
    return mEngine.mLuaAnimationRunning;
}

void LuaAnimationHandlerImpl::native_onProgress(float progress) {
    mEngine.mLuaAnimationProgress = progress;
}

void LuaAnimationHandlerImpl::native_onError(LuaScriptError error) {
    mEngine.mLuaLastError = error.what;
}

MREHolderImpl::MREHolderImpl(Engine& engine)
    : MREHolderApi(), ProxyBase(ProxyKind::MREHolder), mEngine(engine) {}

void MREHolderImpl::nativeDestroy() {
    destroyIfOwned();
}

void MREHolderImpl::native_handleOnAppDidEnterForeground() {
    mEngine.mForegrounded = true;
}

void MREHolderImpl::native_handleOnAppDidEnterBackground() {
    mEngine.mForegrounded = false;
}

void MREHolderImpl::native_handleMemoryWarning() {
    mEngine.mReceivedMemoryWarning = true;
}

// ---------------------------------------------------------- configuration

EngineConfigurationImpl::EngineConfigurationImpl(Engine& engine)
    : EngineConfigurationApi(), ProxyBase(ProxyKind::EngineConfiguration), mEngine(engine) {}

void EngineConfigurationImpl::nativeDestroy() {
    destroyIfOwned();
}

void EngineConfigurationImpl::native_setCameraTarget(CameraTarget target) {
    mEngine.mConfigurationCameraTarget = target;
}

CameraTarget EngineConfigurationImpl::native_getCameraTarget() {
    return mEngine.mConfigurationCameraTarget;
}

void EngineConfigurationImpl::native_setClientName(std::string name) {
    mEngine.mClientName = name;
}

std::string EngineConfigurationImpl::native_getClientName() {
    return mEngine.mClientName;
}

void EngineConfigurationImpl::native_setEnableRasterMegaTiles(bool enabled) {
    mEngine.mEnableRasterMegaTiles = enabled;
}

bool EngineConfigurationImpl::native_getEnableRasterMegaTiles() {
    return mEngine.mEnableRasterMegaTiles;
}

void EngineConfigurationImpl::native_setLogLevel(LogLevel level) {
    mEngine.mLogLevel = static_cast<int>(level);
}

LogLevel EngineConfigurationImpl::native_getLogLevel() {
    return static_cast<LogLevel>(mEngine.logLevel());
}

void EngineConfigurationImpl::native_setLoggingToStdOut(bool enabled) {
    mEngine.mLoggingToStdOut = enabled;
}

bool EngineConfigurationImpl::native_getLoggingToStdOut() {
    return mEngine.mLoggingToStdOut;
}

void EngineConfigurationImpl::native_setBreadcrumbRecorder(jni::Opaque recorder) {
    mEngine.mBreadcrumbRecorder = recorder;
}

jni::Opaque EngineConfigurationImpl::native_getBreadcrumbRecorder() {
    return mEngine.mBreadcrumbRecorder;
}

void EngineConfigurationImpl::native_setReindexOnDiskResources(bool enabled) {
    mEngine.mReindexOnDiskResources = enabled;
}

bool EngineConfigurationImpl::native_getReindexOnDiskResources() {
    return mEngine.mReindexOnDiskResources;
}

void EngineConfigurationImpl::native_setUseOnDiskCache(bool enabled) {
    mEngine.mUseOnDiskCache = enabled;
}

bool EngineConfigurationImpl::native_getUseOnDiskCache() {
    return mEngine.mUseOnDiskCache;
}

bool EngineConfigurationImpl::native_loadFromFile(std::string path) {
    // The original engine reads a JSON configuration file. This
    // implementation accepts the same shape: flat "key": value pairs.
    std::ifstream file(path);
    if (!file.is_open()) {
        return false;
    }
    std::string line;
    while (std::getline(file, line)) {
        const size_t colon = line.find(':');
        if (colon == std::string::npos) {
            continue;
        }
        std::string key = line.substr(0, colon);
        std::string value = line.substr(colon + 1);
        while (!key.empty() && (key.back() == ' ' || key.back() == '\t')) {
            key.pop_back();
        }
        while (!value.empty() && (value.front() == ' ' || value.front() == '\t')) {
            value.erase(value.begin());
        }
        if (!value.empty() && value.front() == '"') {
            value = value.substr(1, value.size() - 2);
        }
        mEngine.stringProperties()[key] = value;
    }
    return true;
}

// ------------------------------------------------------------------ Offline

OfflineImpl::OfflineImpl(Engine& engine)
    : OfflineApi(), ProxyBase(ProxyKind::Offline), mEngine(engine) {}

void OfflineImpl::nativeDestroy() {
    destroyIfOwned();
}

void OfflineImpl::native_loadBundles(std::vector<std::string> identifiers, jni::Opaque listener) {
    (void)listener;
    for (const std::string& identifier : identifiers) {
        auto it = mEngine.bundles().find(identifier);
        if (it == mEngine.bundles().end()) {
            continue;
        }
        BundleDescriptor& bundle = it->second;
        if (!bundle.sha256.empty() && !bundle.path.empty()) {
            const std::string digest = sha256File(bundle.path);
            if (digest != bundle.sha256) {
                // A bundle whose digest does not match is not loaded.
                bundle.loaded = false;
                continue;
            }
        }
        bundle.loaded = true;
    }
}

void OfflineImpl::native_unloadBundles(std::vector<std::string> identifiers,
                                       jni::Opaque listener) {
    (void)listener;
    for (const std::string& identifier : identifiers) {
        auto it = mEngine.bundles().find(identifier);
        if (it != mEngine.bundles().end()) {
            it->second.loaded = false;
        }
    }
}

void OfflineImpl::native_setNonBundleResourcesAllowed(bool allowed) {
    mEngine.mNonBundleResourcesAllowed = allowed;
}

// ------------------------------------------------------ feature properties

OverriddenFeaturePropertiesImpl::OverriddenFeaturePropertiesImpl(Engine& engine)
    : OverriddenFeaturePropertiesApi(), ProxyBase(ProxyKind::OverriddenFeatureProperties),
      mEngine(engine) {}

void OverriddenFeaturePropertiesImpl::nativeDestroy() {
    destroyIfOwned();
}

void OverriddenFeaturePropertiesImpl::native_addBooleanProperty(std::string key, bool value) {
    mEngine.booleanProperties()[key] = value;
}

void OverriddenFeaturePropertiesImpl::native_addIntegerProperty(std::string key,
                                                                 int64_t value) {
    mEngine.integerProperties()[key] = value;
}

void OverriddenFeaturePropertiesImpl::native_addDoubleProperty(std::string key, double value) {
    mEngine.doubleProperties()[key] = value;
}

void OverriddenFeaturePropertiesImpl::native_addStringProperty(std::string key,
                                                               std::string value) {
    mEngine.stringProperties()[key] = value;
}

std::optional<bool> OverriddenFeaturePropertiesImpl::native_getBooleanProperty(std::string key) {
    auto it = mEngine.booleanProperties().find(key);
    return it == mEngine.booleanProperties().end() ? std::nullopt
                                                   : std::optional<bool>(it->second);
}

std::optional<int64_t> OverriddenFeaturePropertiesImpl::native_getIntegerProperty(
    std::string key) {
    auto it = mEngine.integerProperties().find(key);
    return it == mEngine.integerProperties().end() ? std::nullopt
                                                    : std::optional<int64_t>(it->second);
}

std::optional<double> OverriddenFeaturePropertiesImpl::native_getDoubleProperty(std::string key) {
    auto it = mEngine.doubleProperties().find(key);
    return it == mEngine.doubleProperties().end() ? std::nullopt
                                                  : std::optional<double>(it->second);
}

std::string OverriddenFeaturePropertiesImpl::native_getStringProperty(std::string key) {
    auto it = mEngine.stringProperties().find(key);
    return it == mEngine.stringProperties().end() ? std::string() : it->second;
}

void OverriddenFeaturePropertiesImpl::native_removeProperty(std::string key) {
    mEngine.booleanProperties().erase(key);
    mEngine.integerProperties().erase(key);
    mEngine.doubleProperties().erase(key);
    mEngine.stringProperties().erase(key);
}

// --------------------------------------------------------------- private

PrivateApiImpl::PrivateApiImpl(Engine& engine)
    : PrivateApiApi(), ProxyBase(ProxyKind::PrivateApi), mEngine(engine) {}

void PrivateApiImpl::nativeDestroy() {
    destroyIfOwned();
}

void PrivateApiImpl::native_setOfflineMode(bool offline) {
    mEngine.mOfflineMode = offline;
}

void PrivateApiImpl::native_setPixelRatio(float ratio) {
    mEngine.mPixelRatio = ratio > 0.0f ? ratio : 1.0f;
}

bool PrivateApiImpl::native_haveVisualContentLoadingWork(bool includeTransfers) {
    (void)includeTransfers;
    return mEngine.loadingWorkOutstanding();
}

bool PrivateApiImpl::native_haveGivenUpTileDownloads() {
    return false;
}

void PrivateApiImpl::native_resetTileDownloadHistory() {
    mEngine.mGivenUpDownloads = 0;
}

void PrivateApiImpl::native_resetOnlineAvailabilityData() {
    mEngine.mOnlineAvailable = true;
}

void PrivateApiImpl::native_setDiskCacheSize(int64_t bytes) {
    mEngine.mDiskCacheSize = bytes;
}

void PrivateApiImpl::native_setDownloadHeightmapsOnly(bool only) {
    mEngine.mDownloadHeightmapsOnly = only;
}

void PrivateApiImpl::native_setResourceLoadingFromDiskAllowed(bool allowed) {
    mEngine.mResourceLoadingFromDiskAllowed = allowed;
}

void PrivateApiImpl::native_setStylingZoomLevelOverride(std::optional<float> zoom) {
    mEngine.mStylingZoomOverride = zoom;
}

void PrivateApiImpl::native_reloadTerrain() {
    mEngine.mTerrainReloadCount += 1;
}

void PrivateApiImpl::native_setLuaGlobalString(std::string key, std::string value) {
    mEngine.luaGlobals()[key] = value;
}

void PrivateApiImpl::native_setLuaGlobalFloat(std::string key, float value) {
    mEngine.luaGlobals()[key] = std::to_string(value);
}

void PrivateApiImpl::native_setLuaGlobalInt(std::string key, int32_t value) {
    mEngine.luaGlobals()[key] = std::to_string(value);
}

void PrivateApiImpl::native_setLuaGlobalLine(std::string key, WorldLine2 line) {
    std::ostringstream out;
    out << line.points.size() << " points";
    mEngine.luaGlobals()[key] = out.str();
}

void PrivateApiImpl::native_clearLuaPersistedStore() {
    mEngine.luaGlobals().clear();
}

void PrivateApiImpl::native_refreshScreenInfo() {
    mEngine.mScreenInfoRefreshCount += 1;
}

bool PrivateApiImpl::native_isVsyncEnabled() {
    return mEngine.vsyncEnabled();
}

void PrivateApiImpl::native_setVsyncEnabled(bool enabled) {
    mEngine.mVsyncEnabled = enabled;
}

MemoryStats PrivateApiImpl::native_getMemoryStats() {
    MemoryStats stats;
    stats.currentAllocatedMb = 0.0;
    stats.currentAllocationCount = 0;
    stats.totalAllocatedMb = 0.0;
    stats.totalAllocationCount = 0;
    return stats;
}

// -------------------------------------------------------------- recording

RecordContentImpl::RecordContentImpl(Engine& engine)
    : RecordContentApi(), ProxyBase(ProxyKind::RecordContent), mEngine(engine) {}

void RecordContentImpl::nativeDestroy() {
    destroyIfOwned();
}

void RecordContentImpl::native_displayRecordingLine(WorldLine2 line) {
    mEngine.mRecordingLine = toContentLine(line, "recording");
}

void RecordContentImpl::native_showRecordingLineFinishMarker(bool show) {
    mEngine.mRecordingFinishMarkerVisible = show;
}

void RecordContentImpl::native_removeRecordingLine() {
    mEngine.mRecordingLine.points.clear();
}

// ------------------------------------------------------------------ routes

RouteContentImpl::RouteContentImpl(Engine& engine)
    : RouteContentApi(), ProxyBase(ProxyKind::RouteContent), mEngine(engine) {}

void RouteContentImpl::nativeDestroy() {
    destroyIfOwned();
}

void RouteContentImpl::native_displayRoute(std::string identifier, WorldLine2 line,
                                           std::vector<RouteSection> sections,
                                           std::vector<RouteWaypoint> waypoints,
                                           RouteSelectionStyle style, bool selected) {
    (void)sections;
    (void)waypoints;
    (void)style;
    (void)selected;
    mEngine.routes()[identifier] = toContentLine(line, identifier);
}

void RouteContentImpl::native_displayAnimatedRoute(std::string identifier, WorldLine2 line,
                                                   std::vector<RouteSection> sections,
                                                   std::vector<RouteWaypoint> waypoints,
                                                   RouteSelectionStyle style, bool selected,
                                                   float duration, float speed,
                                                   jni::Opaque listener) {
    (void)listener;
    native_displayRoute(identifier, line, sections, waypoints, style, selected);
}

void RouteContentImpl::native_removeRoutes(std::vector<std::string> identifiers) {
    for (const std::string& identifier : identifiers) {
        mEngine.routes().erase(identifier);
    }
}

void RouteContentImpl::native_removeAllRoutes() {
    mEngine.routes().clear();
}

void RouteContentImpl::native_selectRoute(std::string identifier, RouteSelectionStyle style) {
    (void)style;
    auto it = mEngine.routes().find(identifier);
    if (it != mEngine.routes().end()) {
        it->second.visible = true;
    }
}

void RouteContentImpl::native_unselectRoute(std::string identifier) {
    auto it = mEngine.routes().find(identifier);
    if (it != mEngine.routes().end()) {
        it->second.visible = false;
    }
}

void RouteContentImpl::native_fadeInUnselectedRoutes(float duration,
                                                     std::optional<float> delay,
                                                     jni::Opaque listener) {
    (void)duration;
    (void)delay;
    (void)listener;
    for (auto& entry : mEngine.routes()) {
        entry.second.visible = true;
    }
}

void RouteContentImpl::native_stopUnselectedRoutesFadeIn() {}

// ---------------------------------------------------------------- filters

OsmFilterImpl::OsmFilterImpl(Engine& engine)
    : OsmFilterApi(), ProxyBase(ProxyKind::OsmFilter), mEngine(engine) {}

void OsmFilterImpl::nativeDestroy() {
    destroyIfOwned();
}

void OsmFilterImpl::native_setShow(bool show) {
    mEngine.mOsmVisible = show;
}


SegmentsFilterImpl::SegmentsFilterImpl(Engine& engine)
    : SegmentsFilterApi(), ProxyBase(ProxyKind::SegmentsFilter), mEngine(engine) {}

void SegmentsFilterImpl::nativeDestroy() {
    destroyIfOwned();
}

void SegmentsFilterImpl::native_show(SegmentsFilterOptions options) {
    mEngine.mSegmentsVisible = true;
    mEngine.mSegmentsOptions = options;
}

void SegmentsFilterImpl::native_hide() {
    mEngine.mSegmentsVisible = false;
}

std::vector<SegmentFeature> SegmentsFilterImpl::native_getVisibleSegments() {
    return {};
}

void SegmentsFilterImpl::native_selectSegment(int64_t identifier) {
    mEngine.mSelectedSegment = identifier;
}

void SegmentsFilterImpl::native_unselectSegment() {
    mEngine.mSelectedSegment = 0;
}

StravaPoiFilterImpl::StravaPoiFilterImpl(Engine& engine)
    : StravaPoiFilterApi(), ProxyBase(ProxyKind::StravaPoiFilter), mEngine(engine) {}

void StravaPoiFilterImpl::nativeDestroy() {
    destroyIfOwned();
}

void StravaPoiFilterImpl::native_setShow(bool show) {
    mEngine.mPoisVisible = show;
}

void StravaPoiFilterImpl::native_selectPoi(jni::Opaque poi) {
    mEngine.mSelectedPoi = poi.get() != nullptr;
}

void StravaPoiFilterImpl::native_unselectPoi() {
    mEngine.mSelectedPoi = false;
}

void StravaPoiFilterImpl::native_enableBikeShares() {
    mEngine.mBikeSharesEnabled = true;
}

void StravaPoiFilterImpl::native_disableBikeShares() {
    mEngine.mBikeSharesEnabled = false;
}

void StravaPoiFilterImpl::native_enableStartPoints(StartPointFilterOptions options) {
    mEngine.mStartPointsEnabled = true;
    mEngine.mStartPointOptions = options;
}

void StravaPoiFilterImpl::native_disableStartPoints() {
    mEngine.mStartPointsEnabled = false;
}

// ------------------------------------------------------------ terrain engine

TerrainEngineImpl::TerrainEngineImpl(Engine& engine)
    : TerrainEngineApi(), ProxyBase(ProxyKind::TerrainEngine), mEngine(engine) {}

void TerrainEngineImpl::nativeDestroy() {
    destroyIfOwned();
}

void TerrainEngineImpl::native_update() {
    mEngine.mFrameCount += 1;
}

void TerrainEngineImpl::native_setPaused(bool paused) {
    mEngine.mPaused = paused;
}

void TerrainEngineImpl::native_receivedMemoryWarning() {
    mEngine.mReceivedMemoryWarning = true;
}

void TerrainEngineImpl::native_setServerConfig(ServerConfig config, bool apply) {
    if (!apply) {
        return;
    }
    TileSourceConfigData& source = mEngine.tileSource();
    const TileSourceConfig& tile = config.tileSourceConfig;
    if (!tile.mapboxStreetsURLTemplate.empty()) {
        source.mapboxStreetsURLTemplate = tile.mapboxStreetsURLTemplate;
    }
    if (!tile.mapboxStreetsTerrainURLTemplate.empty()) {
        source.mapboxStreetsTerrainURLTemplate = tile.mapboxStreetsTerrainURLTemplate;
    }
    if (!tile.stravaPlanetURLTemplate.empty()) {
        source.stravaPlanetURLTemplate = tile.stravaPlanetURLTemplate;
    }
    if (!tile.stravaPlanetSportURLTemplate.empty()) {
        source.stravaPlanetSportURLTemplate = tile.stravaPlanetSportURLTemplate;
    }
    if (!tile.stravaContoursURLTemplate.empty()) {
        source.stravaContoursURLTemplate = tile.stravaContoursURLTemplate;
    }
    if (!tile.stravaPoiURLTemplate.empty()) {
        source.stravaPoiURLTemplate = tile.stravaPoiURLTemplate;
    }
    if (!tile.segmentsTemplate.empty()) {
        source.segmentsTemplate = tile.segmentsTemplate;
    }
    if (!tile.heightmapURLTemplate.empty()) {
        source.heightmapURLTemplate = tile.heightmapURLTemplate;
    }
    if (!tile.gradientURLTemplate.empty()) {
        source.gradientURLTemplate = tile.gradientURLTemplate;
    }
    if (!tile.summerImageryURLTemplate.empty()) {
        source.summerImageryURLTemplate = tile.summerImageryURLTemplate;
    }
    if (!tile.winterImageryURLTemplate.empty()) {
        source.winterImageryURLTemplate = tile.winterImageryURLTemplate;
    }
    if (!tile.winterOverlayImageryURLTemplate.empty()) {
        source.winterOverlayImageryURLTemplate = tile.winterOverlayImageryURLTemplate;
    }
    if (!tile.globalHeatmapURLTemplate.empty()) {
        source.globalHeatmapURLTemplate = tile.globalHeatmapURLTemplate;
    }
    if (!tile.nightlyHeatmapURLTemplate.empty()) {
        source.nightlyHeatmapURLTemplate = tile.nightlyHeatmapURLTemplate;
    }
    if (!tile.weeklyHeatmapURLTemplate.empty()) {
        source.weeklyHeatmapURLTemplate = tile.weeklyHeatmapURLTemplate;
    }
    if (!tile.personalHeatmapURLTemplate.empty()) {
        source.personalHeatmapURLTemplate = tile.personalHeatmapURLTemplate;
    }
    if (!config.stravaTileServerURL.empty()) {
        mEngine.mStravaTileServerURL = config.stravaTileServerURL;
    }
    if (!config.stravaPoiServerURL.empty()) {
        mEngine.mStravaPoiServerURL = config.stravaPoiServerURL;
    }
    if (!config.segmentsServerURL.empty()) {
        mEngine.mSegmentsServerURL = config.segmentsServerURL;
    }
}

void TerrainEngineImpl::native_setStravaAuthData(int64_t accountId,
                                                 std::vector<HttpHeader> headers) {
    mEngine.mStravaAccountId = accountId;
    for (const HttpHeader& header : headers) {
        if (header.name == "Authorization") {
            mEngine.tileSource().stravaAuthToken = header.value;
        } else {
            mEngine.tileSource().extraHeaders[header.name] = header.value;
        }
    }
}

void TerrainEngineImpl::native_setStravaSourcesExtraHeaders(std::vector<HttpHeader> headers) {
    for (const HttpHeader& header : headers) {
        mEngine.tileSource().extraHeaders[header.name] = header.value;
    }
}

void TerrainEngineImpl::native_setImageryOverlay(ImageryOverlay overlay) {
    mEngine.mImageryOverlay = static_cast<int>(overlay);
}

void TerrainEngineImpl::native_setMapType(MapType mapType) {
    mEngine.mMapType = static_cast<int>(mapType);
}

void TerrainEngineImpl::native_setGlobalHeatmapConfig(GlobalHeatmapConfig config) {
    mEngine.mGlobalHeatmapConfig = config;
}

void TerrainEngineImpl::native_setNightlyHeatmapConfig(NightlyHeatmapConfig config) {
    mEngine.mNightlyHeatmapConfig = config;
}

void TerrainEngineImpl::native_setWeeklyHeatmapConfig(WeeklyHeatmapConfig config) {
    mEngine.mWeeklyHeatmapConfig = config;
}

void TerrainEngineImpl::native_setPersonalHeatmapConfig(PersonalHeatmapConfig config) {
    mEngine.mPersonalHeatmapConfig = config;
}

void TerrainEngineImpl::native_setIsTerrain3dEnabled(bool enabled) {
    mEngine.mTerrain3dEnabled = enabled;
}

void TerrainEngineImpl::native_setUnitSystem(MeasurementUnitSystem system) {
    mEngine.mUnitSystem = static_cast<int>(system);
}

void TerrainEngineImpl::native_didEnterForeground() {
    mEngine.mForegrounded = true;
}

void TerrainEngineImpl::native_didEnterBackground() {
    mEngine.mForegrounded = false;
}

void TerrainEngineImpl::native_setRenderingDisabled(bool disabled) {
    mEngine.mRenderingDisabled = disabled;
}

void TerrainEngineImpl::native_requestRender() {
    mEngine.mRenderRequested = true;
}

void TerrainEngineImpl::native_setAlwaysRender(bool always) {
    mEngine.mAlwaysRender = always;
}

float TerrainEngineImpl::native_getFps() {
    const double seconds = mEngine.lastFrameSeconds();
    return seconds > 0.0 ? static_cast<float>(1.0 / seconds) : 0.0f;
}

void TerrainEngineImpl::native_setEnableScreenSymbols(bool enabled) {
    mEngine.mScreenSymbolsEnabled = enabled;
}

void TerrainEngineImpl::native_setScreenSymbolRefreshInterval(float seconds) {
    mEngine.mScreenSymbolRefreshInterval = seconds;
}

void TerrainEngineImpl::native_setDesiredLanguage(Language language) {
    mEngine.mLanguage = static_cast<int>(language);
}

void TerrainEngineImpl::native_waitToLoadMap(jni::Opaque callback, bool blocking, float timeout) {
    (void)callback;
    (void)blocking;
    (void)timeout;
}

void TerrainEngineImpl::native_setLogLevel(LogLevel level) {
    mEngine.mLogLevel = static_cast<int>(level);
}

void TerrainEngineImpl::native_setOverriddenFeatureProperties(
    OverriddenFeatureProperties properties) {
    mEngine.mOverriddenFeatureProperties = properties;
}

OverriddenFeatureProperties TerrainEngineImpl::native_getOverriddenFeatureProperties() {
    return mEngine.mOverriddenFeatureProperties;
}

double TerrainEngineImpl::native_getElevation(WorldPoint2 point) {
    (void)point;
    // No elevation source is wired up: the engine reports sea level until a
    // heightmap provider is installed.
    return 0.0;
}

UnitsInfo TerrainEngineImpl::native_getGridUnits() {
    UnitsInfo units;
    const double resolution = geo::metersPerPixel(mEngine.cameraZoom(),
                                                   mEngine.cameraTarget().latitude);
    units.meters = resolution * 100.0;
    units.size = 100.0;
    units.unit = mEngine.unitSystem() == static_cast<int>(MeasurementUnitSystem::IMPERIAL)
                     ? MeasurementUnit::FEET
                     : MeasurementUnit::METER;
    return units;
}

CameraImpl* TerrainEngineImpl::native_getCamera() {
    return &mEngine.camera();
}

ContentImpl* TerrainEngineImpl::native_getContent() {
    return &mEngine.content();
}

OfflineImpl* TerrainEngineImpl::native_getOffline() {
    return &mEngine.offline();
}

UtilsImpl* TerrainEngineImpl::native_getUtils() {
    return &mEngine.utils();
}

DebugApiImpl* TerrainEngineImpl::native_getDebugApi() {
    return &mEngine.debug();
}

PrivateApiImpl* TerrainEngineImpl::native_getPrivateApi(std::string name) {
    (void)name;
    return &mEngine.privateApi();
}

// ------------------------------------------------------------------ testing

TestingContextEventListenerImpl::TestingContextEventListenerImpl(Engine& engine)
    : TestingContextEventListenerApi(), ProxyBase(ProxyKind::TestingContextEventListener),
      mEngine(engine) {}

void TestingContextEventListenerImpl::nativeDestroy() {
    destroyIfOwned();
}

TestingContextHandleImpl::TestingContextHandleImpl(Engine& engine)
    : TestingContextHandleApi(), ProxyBase(ProxyKind::TestingContextHandle), mEngine(engine) {}

void TestingContextHandleImpl::nativeDestroy() {
    destroyIfOwned();
}

TestingContextStatus TestingContextHandleImpl::native_status() {
    return TestingContextStatus::NOCONTEXT;
}

void TestingContextHandleImpl::native_stopEnteringOrInitiateLeaving() {}

TestsListenerImpl::TestsListenerImpl(Engine& engine)
    : TestsListenerApi(), ProxyBase(ProxyKind::TestsListener), mEngine(engine) {}

void TestsListenerImpl::nativeDestroy() {
    destroyIfOwned();
}

TouchListenerImpl::TouchListenerImpl(Engine& engine)
    : TouchListenerApi(), ProxyBase(ProxyKind::TouchListener), mEngine(engine) {}

void TouchListenerImpl::nativeDestroy() {
    destroyIfOwned();
}

TrackPerfTestCompletionListenerImpl::TrackPerfTestCompletionListenerImpl(Engine& engine)
    : TrackPerfTestCompletionListenerApi(), ProxyBase(ProxyKind::TrackPerfTestCompletionListener),
      mEngine(engine) {}

void TrackPerfTestCompletionListenerImpl::nativeDestroy() {
    destroyIfOwned();
}

// -------------------------------------------------------------------- utils

UtilsImpl::UtilsImpl(Engine& engine)
    : UtilsApi(), ProxyBase(ProxyKind::Utils), mEngine(engine) {}

void UtilsImpl::nativeDestroy() {
    destroyIfOwned();
}

void UtilsImpl::native_setUserMarker(double latitude, double longitude) {
    mEngine.mUserMarkerVisible = true;
    mEngine.mUserMarkerLatitude = latitude;
    mEngine.mUserMarkerLongitude = longitude;
}

void UtilsImpl::native_setUserMarkerAnimation(float animation) {
    mEngine.mUserMarkerAnimation = animation;
}

void UtilsImpl::native_setUserMarkerState(bool state) {
    mEngine.mUserMarkerState = state ? 1 : 0;
}

void UtilsImpl::native_setUserMarkerPrecision(float precision) {
    mEngine.mUserMarkerPrecision = static_cast<int>(precision);
}

void UtilsImpl::native_setUserMarkerDirection(float degrees) {
    mEngine.mUserMarkerDirection = degrees;
}

void UtilsImpl::native_removeUserMarker() {
    mEngine.mUserMarkerVisible = false;
}

bool UtilsImpl::native_isUserMarkerOnScreen() {
    if (!mEngine.userMarkerVisible()) {
        return false;
    }
    const VisibleAreaBounds bounds = mEngine.computeVisibleBounds(CameraMargins{}, 0.0f);
    return mEngine.userMarkerLatitude() >= bounds.bottomLeftCorner.latitude &&
           mEngine.userMarkerLatitude() <= bounds.topLeftCorner.latitude &&
           mEngine.userMarkerLongitude() >= bounds.bottomLeftCorner.longitude &&
           mEngine.userMarkerLongitude() <= bounds.bottomRightCorner.longitude;
}

float UtilsImpl::native_getUserMarkerLocationAngle() {
    return mEngine.userMarkerDirection();
}

int64_t UtilsImpl::native_addCustomMarker2(double latitude, double longitude,
                                            std::string identifier, std::string category,
                                            std::string customId) {
    const int64_t handle = mEngine.nextMarkerHandle();
    ContentMarker marker;
    marker.identifier = identifier.empty() ? std::to_string(handle) : identifier;
    marker.latitude = latitude;
    marker.longitude = longitude;
    marker.category = category.empty() ? customId : category;
    mEngine.markers()[std::to_string(handle)] = marker;
    return handle;
}

int64_t UtilsImpl::native_addCustomMarker3(double latitude, double longitude,
                                            std::string identifier, std::string category,
                                            std::string customId) {
    return native_addCustomMarker2(latitude, longitude, identifier, category, customId);
}

void UtilsImpl::native_updateCustomMarkerPosition(int64_t handle, double latitude,
                                                   double longitude) {
    auto it = mEngine.markers().find(std::to_string(handle));
    if (it != mEngine.markers().end()) {
        it->second.latitude = latitude;
        it->second.longitude = longitude;
    }
}

void UtilsImpl::native_removeCustomMarker(int64_t handle) {
    mEngine.markers().erase(std::to_string(handle));
}

void UtilsImpl::native_showTileGrid(int8_t mode, std::vector<HighlightedTile> tiles) {
    mEngine.mTileGridVisible = true;
    mEngine.mTileGridMode = mode;
    mEngine.mHighlightedTiles = std::move(tiles);
}

void UtilsImpl::native_hideTileGrid() {
    mEngine.mTileGridVisible = false;
}

void UtilsImpl::native_setHighlightedTiles(std::vector<HighlightedTile> tiles) {
    mEngine.mHighlightedTiles = std::move(tiles);
}

VisibleAreaSamplesSinkImpl::VisibleAreaSamplesSinkImpl(Engine& engine)
    : VisibleAreaSamplesSinkApi(), ProxyBase(ProxyKind::VisibleAreaSamplesSink), mEngine(engine) {}

void VisibleAreaSamplesSinkImpl::nativeDestroy() {
    destroyIfOwned();
}

void VisibleAreaSamplesSinkImpl::native_processVisiblePoint(WorldPoint2 point) {
    mEngine.mVisibleSampleCount += 1;
    (void)point;
}

WindowLifecycleListenerImpl::WindowLifecycleListenerImpl(Engine& engine)
    : WindowLifecycleListenerApi(), ProxyBase(ProxyKind::WindowLifecycleListener),
      mEngine(engine) {}

void WindowLifecycleListenerImpl::nativeDestroy() {
    destroyIfOwned();
}

}  // namespace fatmap
