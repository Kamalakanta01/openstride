// The Fatmap-compatible map engine.
//
// The generated API layer (map-engine/generated) declares one C++ interface
// per Java proxy class, with a default no-op body for every method. This
// header declares the concrete implementations; the ones that matter are
// overridden, and the rest keep the generated default.
//
// The generated layer handles JNI marshalling and dispatch. Everything below
// is ordinary C++ with no JNI in it, so it can be unit tested on the host.

#pragma once

#include <cstdint>
#include <map>
#include <optional>
#include <set>
#include <string>
#include <vector>

#include "fatmap_api.h"
#include "fatmap_types.h"

namespace fatmap {

/// Which proxy class a handle refers to.
enum class ProxyKind {
    ActivityContent,
    Camera,
    CameraDeltaTimeProvider,
    CameraFlyThroughControls,
    CommandService,
    Content,
    ContentSelectionHandle,
    DebugApi,
    DeviceOrientationListener,
    EngineConfiguration,
    GestureRecognizerControl,
    LuaAnimationHandler,
    MREHolder,
    Offline,
    OsmFilter,
    OverriddenFeatureProperties,
    PrivateApi,
    RecordContent,
    RouteContent,
    SegmentsFilter,
    StravaPoiFilter,
    TerrainEngine,
    TestingContextEventListener,
    TestingContextHandle,
    TestsListener,
    TouchListener,
    TrackPerfTestCompletionListener,
    Utils,
    VisibleAreaSamplesSink,
    WindowLifecycleListener,
};

/// Common base for every handle the Java layer holds.
struct ProxyBase {
    explicit ProxyBase(ProxyKind kind) : kind(kind) {}
    virtual ~ProxyBase() = default;

    /// True when the Java handle owns this object, rather than the engine.
    bool owned = false;

    /// Deletes the object when the Java handle owns it. Returns true if it did.
    bool destroyIfOwned() {
        if (!owned) {
            return false;
        }
        owned = false;
        delete this;
        return true;
    }

    ProxyKind kind;
};

class Engine;

// ------------------------------------------------------------- sub-engines

/// A configured tile source: the sixteen URL templates plus credentials.
struct TileSourceConfigData {
    std::string mapboxStreetsURLTemplate;
    std::string mapboxStreetsTerrainURLTemplate;
    std::string stravaPlanetURLTemplate;
    std::string stravaPlanetSportURLTemplate;
    std::string stravaContoursURLTemplate;
    std::string stravaPoiURLTemplate;
    std::string segmentsTemplate;
    std::string heightmapURLTemplate;
    std::string gradientURLTemplate;
    std::string summerImageryURLTemplate;
    std::string winterImageryURLTemplate;
    std::string winterOverlayImageryURLTemplate;
    std::string globalHeatmapURLTemplate;
    std::string nightlyHeatmapURLTemplate;
    std::string weeklyHeatmapURLTemplate;
    std::string personalHeatmapURLTemplate;

    /// Extra headers sent with every tile request.
    std::map<std::string, std::string> extraHeaders;
    /// The bearer token used for first-party tile endpoints.
    std::string stravaAuthToken;

    /// Expands a template for one tile.
    std::string tileUrl(const std::string& tmpl, int zoom, int x, int y) const;
};

/// One offline map bundle on disk.
struct BundleDescriptor {
    std::string identifier;
    std::string path;
    /// Expected SHA-256 of the bundle contents, lowercase hex.
    std::string sha256;
    int64_t compressedSizeBytes = 0;
    int64_t decompressedSizeBytes = 0;
    bool loaded = false;
};

/// A polyline drawn on the map.
struct ContentLine {
    std::string identifier;
    std::vector<WorldPoint2> points;
    std::string colour;
    std::string width;
    bool dashed = false;
    bool visible = true;
};

/// A marker placed on the map.
struct ContentMarker {
    std::string identifier;
    double latitude = 0.0;
    double longitude = 0.0;
    std::string category;
    bool selected = false;
};

/// A camera flight along a path.
struct FlyThroughPath {
    std::vector<WorldPoint3> points;
    /// Cumulative distance along the path, in metres.
    std::vector<double> cumulative;
    double totalLength = 0.0;
    std::string identifier;
    std::string style;
    bool active = false;
    double progress = 0.0;
    float speedMultiplier = 1.0f;
    float cameraDistanceMultiplier = 1.0f;
};

/// The camera's animated transition state.
struct CameraTransition {
    bool active = false;
    double elapsed = 0.0;
    double duration = 1.0;
    WorldPoint3 from{};
    WorldPoint3 to{};
    double fromBearing = 0.0;
    double toBearing = 0.0;
    double fromPitch = 0.0;
    double toPitch = 0.0;
    double fromZoom = 0.0;
    double toZoom = 0.0;
};

// ------------------------------------------------------------- proxy impls

class ActivityContentImpl : public ActivityContentApi, public ProxyBase {
public:
    explicit ActivityContentImpl(Engine& engine);
    void nativeDestroy() override;
    void native_displayActivity(std::string identifier, WorldLine2 line,
                                IntervalInt interval) override;
    void native_displayActivity2(std::string identifier, WorldLine2 line,
                                  std::vector<ActivitySection> sections) override;
    void native_displayActivityTruncated(std::string identifier, WorldLine2 line,
                                         IntervalInt interval) override;
    void native_displayActivityWithPrivacyIcons(std::string identifier, WorldLine2 line,
                                                 IntervalInt interval) override;
    void native_removeActivity(std::string identifier) override;
    void native_removeAllActivities() override;
    void native_applyActivityGradient(std::string identifier,
                                       std::vector<Color> colours) override;
    void native_clearActivityGradient(std::string identifier) override;
    void native_applyActivityPolylineHighlights(
        std::string identifier, std::vector<PolylineHighlightSection> sections) override;
    void native_clearActivityPolylineHighlights(std::string identifier) override;

protected:
    Engine& mEngine;
};

class CameraImpl : public CameraApi, public ProxyBase {
public:
    explicit CameraImpl(Engine& engine);
    void nativeDestroy() override;
    void native_flyTo(double latitude, double longitude, float zoom,
                      std::optional<float> bearing, std::optional<float> pitch,
                      float duration, CameraMargins margins,
                      jni::Opaque listener) override;
    void native_flyToWithZoomLevel(double latitude, double longitude, float zoom,
                                   std::optional<float> bearing, std::optional<float> pitch,
                                   float duration, CameraMargins margins,
                                   jni::Opaque listener) override;
    void native_translateTo(WorldPoint2 target, float duration, CameraMargins margins,
                            jni::Opaque listener) override;
    void native_flyToBounds(WorldBounds2 bounds, std::optional<float> bearing,
                            std::optional<float> pitch, float duration, CameraMargins margins,
                            jni::Opaque listener) override;
    void native_flyToTarget(CameraTarget target, float duration, bool animate,
                            CameraMargins margins, jni::Opaque listener) override;
    void native_setBearing(float bearing, float duration, CameraMargins margins) override;
    void native_followUserLocation(std::optional<float> bearing, std::optional<float> pitch,
                                   std::optional<float> zoom, FollowMode followMode,
                                   CameraMargins margins) override;
    void native_followUserLocationWithZoomLevel(WorldPoint2 position,
                                                std::optional<float> bearing,
                                                std::optional<float> pitch,
                                                std::optional<float> zoom,
                                                FollowMode followMode,
                                                CameraMargins margins) override;
    void native_flyThrough(std::vector<WorldPoint3> points, std::string identifier,
                           std::string style, CameraMargins margins, jni::Opaque progressListener,
                           CameraTargetPlus target,
                           std::vector<std::optional<float>> speeds) override;
    CameraFlyThroughControlsImpl* native_flyThroughInteractive(
        std::vector<WorldPoint3> points, std::string identifier, std::string style,
        CameraMargins margins, jni::Opaque progressListener, CameraTargetPlus target,
        bool interactive, jni::Opaque deltaTimeProvider,
        std::vector<std::optional<float>> speeds) override;
    CameraFlyThroughControlsImpl* native_flyover(bool firstPerson, std::vector<WorldPoint3> points,
                                                CameraMargins margins,
                                                jni::Opaque progressListener,
                                                CameraTargetPlus target,
                                                jni::Opaque deltaTimeProvider,
                                                std::vector<std::optional<float>> speeds) override;
    void native_enterFirstPersonView(double latitude, double longitude,
                                      float elevation) override;
    LookAtPoint native_getLookAtPoint() override;
    LookAtPoint native_getCustomLookAtPoint(float longitude, float latitude) override;
    void native_stop() override;
    CameraTarget native_getTarget() override;
    void native_setCameraViewType(CameraViewType viewType) override;
    CameraViewType native_getCameraViewType() override;
    bool native_isWorldPointVisible(WorldPoint3 point, bool ignoreOcclusion) override;
    VisibleAreaBounds native_getVisibleAreaBounds(CameraMargins margins,
                                                   float rotation) override;
    PointScreenPosition native_getScreenPosition(WorldPoint2 point) override;
    float native_getScaleMetersPerPixel() override;
    void native_setZoomSpeed(float speed) override;
    void native_setRotationSpeed(float speed, float duration) override;
    void native_setTranslationSpeed(float speed, float duration) override;
    void native_setDynamicExaggerationEnabled(bool enabled) override;
    void native_setDynamicExaggerationMultiplier(float multiplier) override;

protected:
    Engine& mEngine;
};

class CameraDeltaTimeProviderImpl : public CameraDeltaTimeProviderApi, public ProxyBase {
public:
    explicit CameraDeltaTimeProviderImpl(Engine& engine);
    void nativeDestroy() override;
    float native_getDeltaTime() override;

protected:
    Engine& mEngine;
};

class CameraFlyThroughControlsImpl : public CameraFlyThroughControlsApi, public ProxyBase {
public:
    explicit CameraFlyThroughControlsImpl(Engine& engine);
    void nativeDestroy() override;
    bool native_isActive() override;
    void native_stop() override;
    bool native_isCameraDetached() override;
    bool native_isRefocusPossible() override;
    void native_refocus() override;
    void native_seekToAnimationStart() override;
    void native_setAnimationSpeedMultiplier(float multiplier) override;
    void native_setCameraDistanceMultiplier(float multiplier) override;
    void native_setTrackProgress(float progress) override;
    void native_setTrackSpeedMultiplier(float multiplier) override;
    float native_getDurationSeconds() override;
    float native_getTrackProgress() override;
    float native_getTrackSpeedMultiplier() override;

protected:
    Engine& mEngine;
};

class CommandServiceImpl : public CommandServiceApi, public ProxyBase {
public:
    explicit CommandServiceImpl(Engine& engine);
    void nativeDestroy() override;
    std::string native_execute(std::string command) override;

protected:
    Engine& mEngine;
};

class ContentImpl : public ContentApi, public ProxyBase {
public:
    explicit ContentImpl(Engine& engine);
    void nativeDestroy() override;
    int64_t native_addCustomLine(WorldLine2 line, std::string colour, std::string width,
                                 bool dashed, std::string identifier) override;
    int64_t native_addCustomGradientLine(WorldLine2 line, std::string colour,
                                         std::string width, std::string identifier,
                                         std::vector<Color> colours) override;
    bool native_removeCustomLine(int64_t handle) override;
    void native_addDroppedPin(std::string identifier, WorldPoint2 position) override;
    void native_removeDroppedPin(std::string identifier) override;
    void native_removeAllDroppedPins() override;
    void native_addCircleMarker(std::string identifier, double latitude, double longitude,
                                 float radius) override;
    void native_removeCircleMarker(std::string identifier) override;
    OsmFilterImpl* native_getOsmFilter() override;
    StravaPoiFilterImpl* native_getStravaPoiFilter() override;
    ActivityContentImpl* native_getActivityContent() override;
    RecordContentImpl* native_getRecordContent() override;
    RouteContentImpl* native_getRouteContent() override;
    SegmentsFilterImpl* native_getSegmentsFilter() override;

protected:
    Engine& mEngine;
};

class ContentSelectionHandleImpl : public ContentSelectionHandleApi, public ProxyBase {
public:
    explicit ContentSelectionHandleImpl(Engine& engine);
    void nativeDestroy() override;

protected:
    Engine& mEngine;
};

class DebugApiImpl : public DebugApiApi, public ProxyBase {
public:
    explicit DebugApiImpl(Engine& engine);
    void nativeDestroy() override;
    void native_showFpsStatsOverlay(DebugOverlayCorner corner,
                                    DebugOverlayPadding padding) override;
    void native_hideFpsStatsOverlay() override;
    std::string native_dumpEngineState() override;

protected:
    Engine& mEngine;
};

class DeviceOrientationListenerImpl : public DeviceOrientationListenerApi, public ProxyBase {
public:
    explicit DeviceOrientationListenerImpl(Engine& engine);
    void nativeDestroy() override;
    void native_onDeviceOrientationChanged(DeviceOrientation orientation) override;

protected:
    Engine& mEngine;
};

class EngineConfigurationImpl : public EngineConfigurationApi, public ProxyBase {
public:
    explicit EngineConfigurationImpl(Engine& engine);
    void nativeDestroy() override;
    void native_setCameraTarget(CameraTarget target) override;
    CameraTarget native_getCameraTarget() override;
    void native_setClientName(std::string name) override;
    std::string native_getClientName() override;
    void native_setEnableRasterMegaTiles(bool enabled) override;
    bool native_getEnableRasterMegaTiles() override;
    void native_setLogLevel(LogLevel level) override;
    LogLevel native_getLogLevel() override;
    void native_setLoggingToStdOut(bool enabled) override;
    bool native_getLoggingToStdOut() override;
    void native_setBreadcrumbRecorder(jni::Opaque recorder) override;
    jni::Opaque native_getBreadcrumbRecorder() override;
    void native_setReindexOnDiskResources(bool enabled) override;
    bool native_getReindexOnDiskResources() override;
    void native_setUseOnDiskCache(bool enabled) override;
    bool native_getUseOnDiskCache() override;
    bool native_loadFromFile(std::string path) override;

protected:
    Engine& mEngine;
};

class GestureRecognizerControlImpl : public GestureRecognizerControlApi, public ProxyBase {
public:
    explicit GestureRecognizerControlImpl(Engine& engine);
    void nativeDestroy() override;
    void native_setDoubleTapGesturesEnabled(bool enabled) override;
    void native_setAllGesturesEnabled(bool enabled) override;

protected:
    Engine& mEngine;
};

class LuaAnimationHandlerImpl : public LuaAnimationHandlerApi, public ProxyBase {
public:
    explicit LuaAnimationHandlerImpl(Engine& engine);
    void nativeDestroy() override;
    bool native_shouldAdvanceAnimation() override;
    void native_onProgress(float progress) override;
    void native_onError(LuaScriptError error) override;

protected:
    Engine& mEngine;
};

class MREHolderImpl : public MREHolderApi, public ProxyBase {
public:
    explicit MREHolderImpl(Engine& engine);
    void nativeDestroy() override;
    void native_handleOnAppDidEnterForeground() override;
    void native_handleOnAppDidEnterBackground() override;
    void native_handleMemoryWarning() override;

protected:
    Engine& mEngine;
};

class OfflineImpl : public OfflineApi, public ProxyBase {
public:
    explicit OfflineImpl(Engine& engine);
    void nativeDestroy() override;
    void native_loadBundles(std::vector<std::string> identifiers, jni::Opaque listener) override;
    void native_unloadBundles(std::vector<std::string> identifiers,
                              jni::Opaque listener) override;
    void native_setNonBundleResourcesAllowed(bool allowed) override;

protected:
    Engine& mEngine;
};

class OsmFilterImpl : public OsmFilterApi, public ProxyBase {
public:
    explicit OsmFilterImpl(Engine& engine);
    void nativeDestroy() override;
    void native_setShow(bool show) override;

protected:
    Engine& mEngine;
};

class OverriddenFeaturePropertiesImpl : public OverriddenFeaturePropertiesApi, public ProxyBase {
public:
    explicit OverriddenFeaturePropertiesImpl(Engine& engine);
    void nativeDestroy() override;
    void native_addBooleanProperty(std::string key, bool value) override;
    void native_addIntegerProperty(std::string key, int64_t value) override;
    void native_addDoubleProperty(std::string key, double value) override;
    void native_addStringProperty(std::string key, std::string value) override;
    std::optional<bool> native_getBooleanProperty(std::string key) override;
    std::optional<int64_t> native_getIntegerProperty(std::string key) override;
    std::optional<double> native_getDoubleProperty(std::string key) override;
    std::string native_getStringProperty(std::string key) override;
    void native_removeProperty(std::string key) override;

protected:
    Engine& mEngine;
};

class PrivateApiImpl : public PrivateApiApi, public ProxyBase {
public:
    explicit PrivateApiImpl(Engine& engine);
    void nativeDestroy() override;
    void native_setOfflineMode(bool offline) override;
    void native_setPixelRatio(float ratio) override;
    bool native_haveVisualContentLoadingWork(bool includeTransfers) override;
    bool native_haveGivenUpTileDownloads() override;
    void native_resetTileDownloadHistory() override;
    void native_resetOnlineAvailabilityData() override;
    void native_setDiskCacheSize(int64_t bytes) override;
    void native_setDownloadHeightmapsOnly(bool only) override;
    void native_setResourceLoadingFromDiskAllowed(bool allowed) override;
    void native_setStylingZoomLevelOverride(std::optional<float> zoom) override;
    void native_reloadTerrain() override;
    void native_setLuaGlobalString(std::string key, std::string value) override;
    void native_setLuaGlobalFloat(std::string key, float value) override;
    void native_setLuaGlobalInt(std::string key, int32_t value) override;
    void native_setLuaGlobalLine(std::string key, WorldLine2 line) override;
    void native_clearLuaPersistedStore() override;
    void native_refreshScreenInfo() override;
    bool native_isVsyncEnabled() override;
    void native_setVsyncEnabled(bool enabled) override;
    MemoryStats native_getMemoryStats() override;

protected:
    Engine& mEngine;
};

class RecordContentImpl : public RecordContentApi, public ProxyBase {
public:
    explicit RecordContentImpl(Engine& engine);
    void nativeDestroy() override;
    void native_displayRecordingLine(WorldLine2 line) override;
    void native_showRecordingLineFinishMarker(bool show) override;
    void native_removeRecordingLine() override;

protected:
    Engine& mEngine;
};

class RouteContentImpl : public RouteContentApi, public ProxyBase {
public:
    explicit RouteContentImpl(Engine& engine);
    void nativeDestroy() override;
    void native_displayRoute(std::string identifier, WorldLine2 line,
                             std::vector<RouteSection> sections,
                             std::vector<RouteWaypoint> waypoints,
                             RouteSelectionStyle style, bool selected) override;
    void native_displayAnimatedRoute(std::string identifier, WorldLine2 line,
                                     std::vector<RouteSection> sections,
                                     std::vector<RouteWaypoint> waypoints,
                                     RouteSelectionStyle style, bool selected,
                                     float duration, float speed,
                                     jni::Opaque listener) override;
    void native_removeRoutes(std::vector<std::string> identifiers) override;
    void native_removeAllRoutes() override;
    void native_selectRoute(std::string identifier, RouteSelectionStyle style) override;
    void native_unselectRoute(std::string identifier) override;
    void native_fadeInUnselectedRoutes(float duration, std::optional<float> delay,
                                       jni::Opaque listener) override;
    void native_stopUnselectedRoutesFadeIn() override;

protected:
    Engine& mEngine;
};

class SegmentsFilterImpl : public SegmentsFilterApi, public ProxyBase {
public:
    explicit SegmentsFilterImpl(Engine& engine);
    void nativeDestroy() override;
    void native_show(SegmentsFilterOptions options) override;
    void native_hide() override;
    std::vector<SegmentFeature> native_getVisibleSegments() override;
    void native_selectSegment(int64_t identifier) override;
    void native_unselectSegment() override;

protected:
    Engine& mEngine;
};

class StravaPoiFilterImpl : public StravaPoiFilterApi, public ProxyBase {
public:
    explicit StravaPoiFilterImpl(Engine& engine);
    void nativeDestroy() override;
    void native_setShow(bool show) override;
    void native_selectPoi(jni::Opaque poi) override;
    void native_unselectPoi() override;
    void native_enableBikeShares() override;
    void native_disableBikeShares() override;
    void native_enableStartPoints(StartPointFilterOptions options) override;
    void native_disableStartPoints() override;

protected:
    Engine& mEngine;
};

class TerrainEngineImpl : public TerrainEngineApi, public ProxyBase {
public:
    explicit TerrainEngineImpl(Engine& engine);
    void nativeDestroy() override;
    void native_update() override;
    void native_setPaused(bool paused) override;
    void native_receivedMemoryWarning() override;
    void native_setServerConfig(ServerConfig config, bool apply) override;
    void native_setStravaAuthData(int64_t accountId, std::vector<HttpHeader> headers) override;
    void native_setStravaSourcesExtraHeaders(std::vector<HttpHeader> headers) override;
    void native_setImageryOverlay(ImageryOverlay overlay) override;
    void native_setMapType(MapType mapType) override;
    void native_setGlobalHeatmapConfig(GlobalHeatmapConfig config) override;
    void native_setNightlyHeatmapConfig(NightlyHeatmapConfig config) override;
    void native_setWeeklyHeatmapConfig(WeeklyHeatmapConfig config) override;
    void native_setPersonalHeatmapConfig(PersonalHeatmapConfig config) override;
    void native_setIsTerrain3dEnabled(bool enabled) override;
    void native_setUnitSystem(MeasurementUnitSystem system) override;
    void native_didEnterForeground() override;
    void native_didEnterBackground() override;
    void native_setRenderingDisabled(bool disabled) override;
    void native_requestRender() override;
    void native_setAlwaysRender(bool always) override;
    float native_getFps() override;
    void native_setEnableScreenSymbols(bool enabled) override;
    void native_setScreenSymbolRefreshInterval(float seconds) override;
    void native_setDesiredLanguage(Language language) override;
    void native_waitToLoadMap(jni::Opaque callback, bool blocking, float timeout) override;
    void native_setLogLevel(LogLevel level) override;
    void native_setOverriddenFeatureProperties(OverriddenFeatureProperties properties) override;
    OverriddenFeatureProperties native_getOverriddenFeatureProperties() override;
    double native_getElevation(WorldPoint2 point) override;
    UnitsInfo native_getGridUnits() override;
    CameraImpl* native_getCamera() override;
    ContentImpl* native_getContent() override;
    OfflineImpl* native_getOffline() override;
    UtilsImpl* native_getUtils() override;
    DebugApiImpl* native_getDebugApi() override;
    PrivateApiImpl* native_getPrivateApi(std::string name) override;

protected:
    Engine& mEngine;
};

class TestingContextEventListenerImpl : public TestingContextEventListenerApi, public ProxyBase {
public:
    explicit TestingContextEventListenerImpl(Engine& engine);
    void nativeDestroy() override;

protected:
    Engine& mEngine;
};

class TestingContextHandleImpl : public TestingContextHandleApi, public ProxyBase {
public:
    explicit TestingContextHandleImpl(Engine& engine);
    void nativeDestroy() override;
    TestingContextStatus native_status() override;
    void native_stopEnteringOrInitiateLeaving() override;

protected:
    Engine& mEngine;
};

class TestsListenerImpl : public TestsListenerApi, public ProxyBase {
public:
    explicit TestsListenerImpl(Engine& engine);
    void nativeDestroy() override;

protected:
    Engine& mEngine;
};

class TouchListenerImpl : public TouchListenerApi, public ProxyBase {
public:
    explicit TouchListenerImpl(Engine& engine);
    void nativeDestroy() override;

protected:
    Engine& mEngine;
};

class TrackPerfTestCompletionListenerImpl : public TrackPerfTestCompletionListenerApi,
                                           public ProxyBase {
public:
    explicit TrackPerfTestCompletionListenerImpl(Engine& engine);
    void nativeDestroy() override;

protected:
    Engine& mEngine;
};

class UtilsImpl : public UtilsApi, public ProxyBase {
public:
    explicit UtilsImpl(Engine& engine);
    void nativeDestroy() override;
    void native_setUserMarker(double latitude, double longitude) override;
    void native_setUserMarkerAnimation(float animation) override;
    void native_setUserMarkerState(bool state) override;
    void native_setUserMarkerPrecision(float precision) override;
    void native_setUserMarkerDirection(float degrees) override;
    void native_removeUserMarker() override;
    bool native_isUserMarkerOnScreen() override;
    float native_getUserMarkerLocationAngle() override;
    int64_t native_addCustomMarker2(double latitude, double longitude, std::string identifier,
                                    std::string category, std::string customId) override;
    int64_t native_addCustomMarker3(double latitude, double longitude, std::string identifier,
                                    std::string category, std::string customId) override;
    void native_updateCustomMarkerPosition(int64_t handle, double latitude,
                                           double longitude) override;
    void native_removeCustomMarker(int64_t handle) override;
    void native_showTileGrid(int8_t mode, std::vector<HighlightedTile> tiles) override;
    void native_hideTileGrid() override;
    void native_setHighlightedTiles(std::vector<HighlightedTile> tiles) override;

protected:
    Engine& mEngine;
};

class VisibleAreaSamplesSinkImpl : public VisibleAreaSamplesSinkApi, public ProxyBase {
public:
    explicit VisibleAreaSamplesSinkImpl(Engine& engine);
    void nativeDestroy() override;
    void native_processVisiblePoint(WorldPoint2 point) override;

protected:
    Engine& mEngine;
};

class WindowLifecycleListenerImpl : public WindowLifecycleListenerApi, public ProxyBase {
public:
    explicit WindowLifecycleListenerImpl(Engine& engine);
    void nativeDestroy() override;

protected:
    Engine& mEngine;
};

// ------------------------------------------------------------------ engine

/// The map engine.
class Engine {
public:
    Engine();
    ~Engine();

    Engine(const Engine&) = delete;
    Engine& operator=(const Engine&) = delete;

    ActivityContentImpl& activityContent() { return mActivityContent; }
    CameraImpl& camera() { return mCamera; }
    CameraFlyThroughControlsImpl& flyThroughControls() { return mFlyThroughControls; }
    ContentImpl& content() { return mContent; }
    DebugApiImpl& debug() { return mDebug; }
    EngineConfigurationImpl& configuration() { return mConfiguration; }
    OfflineImpl& offline() { return mOffline; }
    OverriddenFeaturePropertiesImpl& featureProperties() { return mFeatureProperties; }
    PrivateApiImpl& privateApi() { return mPrivateApi; }
    RouteContentImpl& routeContent() { return mRouteContent; }
    RecordContentImpl& recordContent() { return mRecordContent; }
    TerrainEngineImpl& terrainEngine() { return mTerrainEngine; }
    UtilsImpl& utils() { return mUtils; }
    OsmFilterImpl& osmFilter() { return mOsmFilter; }
    StravaPoiFilterImpl& poiFilter() { return mPoiFilter; }
    SegmentsFilterImpl& segmentsFilter() { return mSegmentsFilter; }

    TileSourceConfigData& tileSource() { return mTileSource; }
    const TileSourceConfigData& tileSource() const { return mTileSource; }

    WorldPoint3& cameraTarget() { return mCameraTarget; }
    double cameraBearing() const { return mCameraBearing; }
    double cameraPitch() const { return mCameraPitch; }
    double cameraZoom() const { return mCameraZoom; }
    double cameraDistance() const { return mCameraDistance; }
    int cameraViewType() const { return mCameraViewType; }
    CameraTransition& cameraTransition() { return mCameraTransition; }

    FlyThroughPath& flyThrough() { return mFlyThrough; }
    bool flyThroughControlsActive() const { return mFlyThroughControlsActive; }
    void setFlyThroughControlsActive(bool active) { mFlyThroughControlsActive = active; }

    std::map<std::string, ContentLine>& lines() { return mLines; }
    std::map<std::string, ContentMarker>& markers() { return mMarkers; }
    std::map<std::string, ContentLine>& activities() { return mActivities; }
    std::map<std::string, ContentLine>& routes() { return mRoutes; }
    std::map<std::string, BundleDescriptor>& bundles() { return mBundles; }

    std::map<std::string, bool>& booleanProperties() { return mBooleanProperties; }
    std::map<std::string, int64_t>& integerProperties() { return mIntegerProperties; }
    std::map<std::string, double>& doubleProperties() { return mDoubleProperties; }
    std::map<std::string, std::string>& stringProperties() { return mStringProperties; }
    std::map<std::string, std::string>& luaGlobals() { return mLuaGlobals; }

    bool offlineMode() const { return mOfflineMode; }
    float pixelRatio() const { return mPixelRatio; }
    bool renderingDisabled() const { return mRenderingDisabled; }
    bool alwaysRender() const { return mAlwaysRender; }
    bool paused() const { return mPaused; }
    bool terrain3dEnabled() const { return mTerrain3dEnabled; }
    int mapType() const { return mMapType; }
    int imageryOverlay() const { return mImageryOverlay; }
    int logLevel() const { return mLogLevel; }
    int unitSystem() const { return mUnitSystem; }
    int language() const { return mLanguage; }
    bool fpsOverlayVisible() const { return mFpsOverlayVisible; }
    int fpsOverlayCorner() const { return mFpsOverlayCorner; }
    bool screenSymbolsEnabled() const { return mScreenSymbolsEnabled; }
    bool vsyncEnabled() const { return mVsyncEnabled; }
    bool tileGridVisible() const { return mTileGridVisible; }
    bool userMarkerVisible() const { return mUserMarkerVisible; }
    double userMarkerLatitude() const { return mUserMarkerLatitude; }
    double userMarkerLongitude() const { return mUserMarkerLongitude; }
    float userMarkerDirection() const { return mUserMarkerDirection; }
    int userMarkerState() const { return mUserMarkerState; }
    int userMarkerPrecision() const { return mUserMarkerPrecision; }
    float userMarkerAnimation() const { return mUserMarkerAnimation; }
    bool recordingFinishMarkerVisible() const { return mRecordingFinishMarkerVisible; }
    bool nonBundleResourcesAllowed() const { return mNonBundleResourcesAllowed; }
    bool loadingWorkOutstanding() const { return mLoadingWorkOutstanding; }
    std::optional<float> stylingZoomOverride() const { return mStylingZoomOverride; }
    int64_t diskCacheSize() const { return mDiskCacheSize; }
    double lastFrameSeconds() const { return mLastFrameSeconds; }
    int64_t nextContentHandle() { return ++mNextContentHandle; }
    int64_t nextMarkerHandle() { return ++mNextMarkerHandle; }

    /// Advances every animated part of the engine by one frame.
    void advance(double deltaSeconds);

    /// Computes the visible world rectangle for the current camera.
    VisibleAreaBounds computeVisibleBounds(const CameraMargins& margins,
                                            double rotationDegrees) const;

    /// Projects a world point to screen pixels in the canonical 1024 px view.
    PointScreenPosition projectToScreen(const WorldPoint2& point) const;

    /// The canonical view edge length in pixels used for all projections.
    static constexpr double kViewPixels = 1024.0;

    // The engine's state is public: the implementation classes below mutate it
    // directly. The class is internal to the library, so there is no API to
    // protect.
    ActivityContentImpl mActivityContent;
    CameraImpl mCamera;
    CameraDeltaTimeProviderImpl mCameraDeltaTimeProvider;
    CameraFlyThroughControlsImpl mFlyThroughControls;
    CommandServiceImpl mCommandService;
    ContentImpl mContent;
    ContentSelectionHandleImpl mContentSelection;
    DebugApiImpl mDebug;
    DeviceOrientationListenerImpl mDeviceOrientationListener;
    EngineConfigurationImpl mConfiguration;
    GestureRecognizerControlImpl mGestureControl;
    LuaAnimationHandlerImpl mLuaAnimation;
    MREHolderImpl mHolder;
    OfflineImpl mOffline;
    OsmFilterImpl mOsmFilter;
    OverriddenFeaturePropertiesImpl mFeatureProperties;
    PrivateApiImpl mPrivateApi;
    RecordContentImpl mRecordContent;
    RouteContentImpl mRouteContent;
    SegmentsFilterImpl mSegmentsFilter;
    StravaPoiFilterImpl mPoiFilter;
    TerrainEngineImpl mTerrainEngine;
    TestingContextEventListenerImpl mTestingContextListener;
    TestingContextHandleImpl mTestingContext;
    TestsListenerImpl mTestsListener;
    TouchListenerImpl mTouchListener;
    TrackPerfTestCompletionListenerImpl mPerfListener;
    UtilsImpl mUtils;
    VisibleAreaSamplesSinkImpl mVisibleAreaSink;
    WindowLifecycleListenerImpl mWindowLifecycle;

    TileSourceConfigData mTileSource;
    WorldPoint3 mCameraTarget;
    double mCameraBearing = 0.0;
    double mCameraPitch = 0.0;
    double mCameraZoom = 2.0;
    double mCameraDistance = 1000.0;
    int mCameraViewType = 0;
    CameraTransition mCameraTransition;

    FlyThroughPath mFlyThrough;
    bool mFlyThroughControlsActive = false;

    std::map<std::string, ContentLine> mLines;
    std::map<std::string, ContentMarker> mMarkers;
    std::map<std::string, ContentLine> mActivities;
    std::map<std::string, ContentLine> mRoutes;
    std::map<std::string, BundleDescriptor> mBundles;

    std::map<std::string, bool> mBooleanProperties;
    std::map<std::string, int64_t> mIntegerProperties;
    std::map<std::string, double> mDoubleProperties;
    std::map<std::string, std::string> mStringProperties;
    std::map<std::string, std::string> mLuaGlobals;

    bool mOfflineMode = false;
    float mPixelRatio = 1.0f;
    bool mRenderingDisabled = false;
    bool mAlwaysRender = false;
    bool mPaused = false;
    bool mTerrain3dEnabled = false;
    int mMapType = 0;
    int mImageryOverlay = 0;
    int mLogLevel = 3;
    int mUnitSystem = 0;
    int mLanguage = 0;
    bool mFpsOverlayVisible = false;
    int mFpsOverlayCorner = 0;
    bool mScreenSymbolsEnabled = true;
    bool mVsyncEnabled = true;
    bool mTileGridVisible = false;
    bool mUserMarkerVisible = false;
    double mUserMarkerLatitude = 0.0;
    double mUserMarkerLongitude = 0.0;
    float mUserMarkerDirection = 0.0f;
    int mUserMarkerState = 0;
    int mUserMarkerPrecision = 0;
    float mUserMarkerAnimation = 0.0f;
    bool mRecordingFinishMarkerVisible = false;
    bool mNonBundleResourcesAllowed = true;
    bool mLoadingWorkOutstanding = false;
    std::optional<float> mStylingZoomOverride;
    int64_t mDiskCacheSize = 0;
    double mLastFrameSeconds = 0.0;
    int64_t mNextContentHandle = 0;
    int64_t mNextMarkerHandle = 0;

    // --- state added by the proxy implementations
    bool mDynamicExaggerationEnabled = false;
    float mDynamicExaggerationMultiplier = 1.0f;
    int mDeviceOrientation = 0;
    bool mDoubleTapGesturesEnabled = true;
    bool mAllGesturesEnabled = true;
    bool mLuaAnimationRunning = false;
    float mLuaAnimationProgress = 0.0f;
    std::string mLuaLastError;
    bool mForegrounded = true;
    bool mReceivedMemoryWarning = false;
    CameraTarget mConfigurationCameraTarget;
    std::string mClientName;
    bool mEnableRasterMegaTiles = false;
    bool mLoggingToStdOut = false;
    bool mReindexOnDiskResources = false;
    bool mUseOnDiskCache = true;
    jni::Opaque mBreadcrumbRecorder;
    int64_t mGivenUpDownloads = 0;
    bool mOnlineAvailable = true;
    bool mDownloadHeightmapsOnly = false;
    bool mResourceLoadingFromDiskAllowed = true;
    int64_t mTerrainReloadCount = 0;
    int64_t mScreenInfoRefreshCount = 0;
    ContentLine mRecordingLine;
    bool mSegmentsVisible = false;
    SegmentsFilterOptions mSegmentsOptions;
    int64_t mSelectedSegment = 0;
    bool mOsmVisible = true;
    bool mPoisVisible = true;
    bool mSelectedPoi = false;
    bool mBikeSharesEnabled = true;
    bool mStartPointsEnabled = true;
    StartPointFilterOptions mStartPointOptions;
    int64_t mFrameCount = 0;
    int64_t mStravaAccountId = 0;
    std::string mStravaTileServerURL;
    std::string mStravaPoiServerURL;
    std::string mSegmentsServerURL;
    GlobalHeatmapConfig mGlobalHeatmapConfig;
    NightlyHeatmapConfig mNightlyHeatmapConfig;
    WeeklyHeatmapConfig mWeeklyHeatmapConfig;
    PersonalHeatmapConfig mPersonalHeatmapConfig;
    OverriddenFeatureProperties mOverriddenFeatureProperties;
    float mScreenSymbolRefreshInterval = 1.0f;
    bool mRenderRequested = false;
    int8_t mTileGridMode = 0;
    std::vector<HighlightedTile> mHighlightedTiles;
    int64_t mVisibleSampleCount = 0;
};

/**
 * Resolves a Java handle back to its implementation object.
 *
 * The handle is the address of the object. The dynamic cast checks that the
 * caller passed the right kind of proxy, so a handle from one class cannot be
 * used as another.
 */
template <typename T>
inline T* resolve(JNIEnv*, jlong handle) {
    if (handle == 0) {
        return nullptr;
    }
    auto* base = reinterpret_cast<ProxyBase*>(handle);
    return dynamic_cast<T*>(base);
}

/// The static entry points the generated JNI layer calls.
EngineConfigurationImpl* static_EngineConfiguration_createDefault();
OverriddenFeatureProperties static_OverriddenFeatureProperties_create();
MREHolderImpl* static_MREHolder_create();
std::vector<std::string> static_PrivateApi_getPerformanceTestsNames();

}  // namespace fatmap
