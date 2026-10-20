// Generated from the Fatmap SDK interface contract. Do not edit by hand.
#pragma once

#include <cstdint>
#include <optional>
#include <set>
#include <string>
#include <vector>

#include "fatmap_types.h"
#include "fatmap_jni_types.h"
#include "fatmap_proxy.h"
#include "fatmap_statics.h"
#include "fatmap_handles.h"

namespace fatmap {

// ---- ActivityContent -------------------------------------------
class ActivityContentApi {
public:
    virtual ~ActivityContentApi() = default;
    virtual void nativeDestroy() {}
    virtual void native_applyActivityGradient(std::string a0, std::vector<Color> a1) {}
    virtual void native_applyActivityPolylineHighlights(std::string a0, std::vector<PolylineHighlightSection> a1) {}
    virtual void native_clearActivityGradient(std::string a0) {}
    virtual void native_clearActivityPolylineHighlights(std::string a0) {}
    virtual void native_displayActivity(std::string a0, WorldLine2 a1, IntervalInt a2) {}
    virtual void native_displayActivity2(std::string a0, WorldLine2 a1, std::vector<ActivitySection> a2) {}
    virtual void native_displayActivityTruncated(std::string a0, WorldLine2 a1, IntervalInt a2) {}
    virtual void native_displayActivityWithPrivacyIcons(std::string a0, WorldLine2 a1, IntervalInt a2) {}
    virtual void native_removeActivity(std::string a0) {}
    virtual void native_removeAllActivities() {}
};

// ---- Camera ----------------------------------------------------
class CameraApi {
public:
    virtual ~CameraApi() = default;
    virtual void nativeDestroy() {}
    virtual void native_addInteractionListener(jni::Opaque a0) {}
    virtual void native_addStateListener(jni::Opaque a0) {}
    virtual void native_enterFirstPersonView(double a0, double a1, float a2) {}
    virtual void native_flyThrough(std::vector<WorldPoint3> a0, std::string a1, std::string a2, CameraMargins a3, jni::Opaque a4, CameraTargetPlus a5, std::vector<std::optional<float>> a6) {}
    virtual CameraFlyThroughControlsImpl* native_flyThroughInteractive(std::vector<WorldPoint3> a0, std::string a1, std::string a2, CameraMargins a3, jni::Opaque a4, CameraTargetPlus a5, bool a6, jni::Opaque a7, std::vector<std::optional<float>> a8) { return {}; }
    virtual void native_flyTo(double a0, double a1, float a2, std::optional<float> a3, std::optional<float> a4, float a5, CameraMargins a6, jni::Opaque a7) {}
    virtual void native_flyToBounds(WorldBounds2 a0, std::optional<float> a1, std::optional<float> a2, float a3, CameraMargins a4, jni::Opaque a5) {}
    virtual void native_flyToTarget(CameraTarget a0, float a1, bool a2, CameraMargins a3, jni::Opaque a4) {}
    virtual void native_flyToWithZoomLevel(double a0, double a1, float a2, std::optional<float> a3, std::optional<float> a4, float a5, CameraMargins a6, jni::Opaque a7) {}
    virtual CameraFlyThroughControlsImpl* native_flyover(bool a0, std::vector<WorldPoint3> a1, CameraMargins a2, jni::Opaque a3, CameraTargetPlus a4, jni::Opaque a5, std::vector<std::optional<float>> a6) { return {}; }
    virtual void native_followUserLocation(std::optional<float> a0, std::optional<float> a1, std::optional<float> a2, FollowMode a3, CameraMargins a4) {}
    virtual void native_followUserLocationWithZoomLevel(WorldPoint2 a0, std::optional<float> a1, std::optional<float> a2, std::optional<float> a3, FollowMode a4, CameraMargins a5) {}
    virtual CameraViewType native_getCameraViewType() { return {}; }
    virtual LookAtPoint native_getCustomLookAtPoint(float a0, float a1) { return {}; }
    virtual LookAtPoint native_getLookAtPoint() { return {}; }
    virtual float native_getScaleMetersPerPixel() { return {}; }
    virtual PointScreenPosition native_getScreenPosition(WorldPoint2 a0) { return {}; }
    virtual CameraTarget native_getTarget() { return {}; }
    virtual VisibleAreaBounds native_getVisibleAreaBounds(CameraMargins a0, float a1) { return {}; }
    virtual bool native_isWorldPointVisible(WorldPoint3 a0, bool a1) { return {}; }
    virtual void native_removeInteractionListener(jni::Opaque a0) {}
    virtual void native_removeStateListener(jni::Opaque a0) {}
    virtual void native_setBearing(float a0, float a1, CameraMargins a2) {}
    virtual void native_setCameraViewType(CameraViewType a0) {}
    virtual void native_setDynamicExaggerationEnabled(bool a0) {}
    virtual void native_setDynamicExaggerationMultiplier(float a0) {}
    virtual void native_setRotationSpeed(float a0, float a1) {}
    virtual void native_setTranslationSpeed(float a0, float a1) {}
    virtual void native_setZoomSpeed(float a0) {}
    virtual void native_stop() {}
    virtual void native_translateTo(WorldPoint2 a0, float a1, CameraMargins a2, jni::Opaque a3) {}
};

// ---- CameraDeltaTimeProvider -----------------------------------
class CameraDeltaTimeProviderApi {
public:
    virtual ~CameraDeltaTimeProviderApi() = default;
    virtual void nativeDestroy() {}
    virtual float native_getDeltaTime() { return {}; }
};

// ---- CameraFlyThroughControls ----------------------------------
class CameraFlyThroughControlsApi {
public:
    virtual ~CameraFlyThroughControlsApi() = default;
    virtual void nativeDestroy() {}
    virtual float native_getDurationSeconds() { return {}; }
    virtual float native_getTrackProgress() { return {}; }
    virtual float native_getTrackSpeedMultiplier() { return {}; }
    virtual bool native_isActive() { return {}; }
    virtual bool native_isCameraDetached() { return {}; }
    virtual bool native_isRefocusPossible() { return {}; }
    virtual void native_refocus() {}
    virtual void native_seekToAnimationStart() {}
    virtual void native_setAnimationSpeedMultiplier(float a0) {}
    virtual void native_setCameraDistanceMultiplier(float a0) {}
    virtual void native_setTrackProgress(float a0) {}
    virtual void native_setTrackSpeedMultiplier(float a0) {}
    virtual void native_stop() {}
};

// ---- CommandService --------------------------------------------
class CommandServiceApi {
public:
    virtual ~CommandServiceApi() = default;
    virtual void nativeDestroy() {}
    virtual std::string native_execute(std::string a0) { return {}; }
};

// ---- Content ---------------------------------------------------
class ContentApi {
public:
    virtual ~ContentApi() = default;
    virtual void nativeDestroy() {}
    virtual void native_addCircleMarker(std::string a0, double a1, double a2, float a3) {}
    virtual int64_t native_addCustomGradientLine(WorldLine2 a0, std::string a1, std::string a2, std::string a3, std::vector<Color> a4) { return {}; }
    virtual int64_t native_addCustomLine(WorldLine2 a0, std::string a1, std::string a2, bool a3, std::string a4) { return {}; }
    virtual void native_addDroppedPin(std::string a0, WorldPoint2 a1) {}
    virtual ActivityContentImpl* native_getActivityContent() { return {}; }
    virtual OsmFilterImpl* native_getOsmFilter() { return {}; }
    virtual RecordContentImpl* native_getRecordContent() { return {}; }
    virtual RouteContentImpl* native_getRouteContent() { return {}; }
    virtual SegmentsFilterImpl* native_getSegmentsFilter() { return {}; }
    virtual StravaPoiFilterImpl* native_getStravaPoiFilter() { return {}; }
    virtual void native_removeAllDroppedPins() {}
    virtual void native_removeCircleMarker(std::string a0) {}
    virtual bool native_removeCustomLine(int64_t a0) { return {}; }
    virtual void native_removeDroppedPin(std::string a0) {}
};

// ---- ContentSelectionHandle ------------------------------------
class ContentSelectionHandleApi {
public:
    virtual ~ContentSelectionHandleApi() = default;
    virtual void nativeDestroy() {}
};

// ---- DebugApi --------------------------------------------------
class DebugApiApi {
public:
    virtual ~DebugApiApi() = default;
    virtual void nativeDestroy() {}
    virtual std::string native_dumpEngineState() { return {}; }
    virtual void native_hideFpsStatsOverlay() {}
    virtual void native_showFpsStatsOverlay(DebugOverlayCorner a0, DebugOverlayPadding a1) {}
};

// ---- DeviceOrientationListener ---------------------------------
class DeviceOrientationListenerApi {
public:
    virtual ~DeviceOrientationListenerApi() = default;
    virtual void nativeDestroy() {}
    virtual void native_onDeviceOrientationChanged(DeviceOrientation a0) {}
};

// ---- EngineConfiguration ---------------------------------------
class EngineConfigurationApi {
public:
    virtual ~EngineConfigurationApi() = default;
    virtual void nativeDestroy() {}
    virtual EngineConfigurationImpl* createDefault() { return {}; }
    virtual jni::Opaque native_getBreadcrumbRecorder() { return {}; }
    virtual CameraTarget native_getCameraTarget() { return {}; }
    virtual std::string native_getClientName() { return {}; }
    virtual bool native_getEnableRasterMegaTiles() { return {}; }
    virtual LogLevel native_getLogLevel() { return {}; }
    virtual bool native_getLoggingToStdOut() { return {}; }
    virtual bool native_getReindexOnDiskResources() { return {}; }
    virtual bool native_getUseOnDiskCache() { return {}; }
    virtual bool native_loadFromFile(std::string a0) { return {}; }
    virtual void native_setBreadcrumbRecorder(jni::Opaque a0) {}
    virtual void native_setCameraTarget(CameraTarget a0) {}
    virtual void native_setClientName(std::string a0) {}
    virtual void native_setEnableRasterMegaTiles(bool a0) {}
    virtual void native_setLogLevel(LogLevel a0) {}
    virtual void native_setLoggingToStdOut(bool a0) {}
    virtual void native_setReindexOnDiskResources(bool a0) {}
    virtual void native_setUseOnDiskCache(bool a0) {}
};

// ---- GestureRecognizerControl ----------------------------------
class GestureRecognizerControlApi {
public:
    virtual ~GestureRecognizerControlApi() = default;
    virtual void nativeDestroy() {}
    virtual void native_setAllGesturesEnabled(bool a0) {}
    virtual void native_setDoubleTapGesturesEnabled(bool a0) {}
};

// ---- LuaAnimationHandler ---------------------------------------
class LuaAnimationHandlerApi {
public:
    virtual ~LuaAnimationHandlerApi() = default;
    virtual void nativeDestroy() {}
    virtual void native_onError(LuaScriptError a0) {}
    virtual void native_onProgress(float a0) {}
    virtual bool native_shouldAdvanceAnimation() { return {}; }
};

// ---- MREHolder -------------------------------------------------
class MREHolderApi {
public:
    virtual ~MREHolderApi() = default;
    virtual void nativeDestroy() {}
    virtual MREHolderImpl* create(jni::Opaque a0, jni::Opaque a1, std::string a2, std::string a3, jni::Opaque a4) { return {}; }
    virtual void native_handleMemoryWarning() {}
    virtual void native_handleOnAppDidEnterBackground() {}
    virtual void native_handleOnAppDidEnterForeground() {}
};

// ---- Offline ---------------------------------------------------
class OfflineApi {
public:
    virtual ~OfflineApi() = default;
    virtual void nativeDestroy() {}
    virtual void native_loadBundles(std::vector<std::string> a0, jni::Opaque a1) {}
    virtual void native_setNonBundleResourcesAllowed(bool a0) {}
    virtual void native_unloadBundles(std::vector<std::string> a0, jni::Opaque a1) {}
};

// ---- OsmFilter -------------------------------------------------
class OsmFilterApi {
public:
    virtual ~OsmFilterApi() = default;
    virtual void nativeDestroy() {}
    virtual void native_setShow(bool a0) {}
};

// ---- OverriddenFeatureProperties -------------------------------
class OverriddenFeaturePropertiesApi {
public:
    virtual ~OverriddenFeaturePropertiesApi() = default;
    virtual void nativeDestroy() {}
    virtual void native_addBooleanProperty(std::string a0, bool a1) {}
    virtual void native_addDoubleProperty(std::string a0, double a1) {}
    virtual void native_addIntegerProperty(std::string a0, int64_t a1) {}
    virtual void native_addStringProperty(std::string a0, std::string a1) {}
    virtual OverriddenFeaturePropertiesImpl* create() { return {}; }
    virtual std::optional<bool> native_getBooleanProperty(std::string a0) { return {}; }
    virtual std::optional<double> native_getDoubleProperty(std::string a0) { return {}; }
    virtual std::optional<int64_t> native_getIntegerProperty(std::string a0) { return {}; }
    virtual std::string native_getStringProperty(std::string a0) { return {}; }
    virtual void native_removeProperty(std::string a0) {}
};

// ---- PrivateApi ------------------------------------------------
class PrivateApiApi {
public:
    virtual ~PrivateApiApi() = default;
    virtual void nativeDestroy() {}
    virtual void native_addPerformanceMetricsListener(jni::Opaque a0) {}
    virtual void native_clearLuaPersistedStore() {}
    virtual void native_finishTwoPassVideoRendering() {}
    virtual GivenUpDownloadsInfo native_getGivenUpDownloadsInfo(int64_t a0) { return {}; }
    virtual MemoryStats native_getMemoryStats() { return {}; }
    virtual std::vector<std::string> getPerformanceTestsNames() { return {}; }
    virtual CommandServiceImpl* native_getPreparedCommandService(jni::Opaque a0) { return {}; }
    virtual VisibleAreaBounds native_getVisibleAreaBoundsExt(CameraMargins a0, float a1, jni::Opaque a2) { return {}; }
    virtual bool native_haveGivenUpTileDownloads() { return {}; }
    virtual bool native_haveVisualContentLoadingWork(bool a0) { return {}; }
    virtual bool native_isVsyncEnabled() { return {}; }
    virtual void native_recordResourcesNeededForSecondVideoRenderingPass() {}
    virtual void native_refreshScreenInfo() {}
    virtual void native_reloadTerrain() {}
    virtual void native_removePerformanceMetricsListener(jni::Opaque a0) {}
    virtual void native_resetOnlineAvailabilityData() {}
    virtual void native_resetTileDownloadHistory() {}
    virtual void native_runPerformanceTests(std::vector<std::string> a0, std::string a1, jni::Opaque a2) {}
    virtual void native_runScreenshotTests(std::vector<std::string> a0, std::string a1, jni::Opaque a2) {}
    virtual void native_saveWindowScreenshot(std::string a0) {}
    virtual void native_setDiskCacheSize(int64_t a0) {}
    virtual void native_setDownloadHeightmapsOnly(bool a0) {}
    virtual void native_setLodSubdivisionThresholdEnabled(bool a0) {}
    virtual void native_setLuaGlobalAnimation(std::string a0, AnimatedImage a1, bool a2) {}
    virtual void native_setLuaGlobalFloat(std::string a0, float a1) {}
    virtual void native_setLuaGlobalImage(std::string a0, CustomImage a1, bool a2) {}
    virtual void native_setLuaGlobalInt(std::string a0, int32_t a1) {}
    virtual void native_setLuaGlobalLine(std::string a0, WorldLine2 a1) {}
    virtual void native_setLuaGlobalString(std::string a0, std::string a1) {}
    virtual void native_setOfflineMode(bool a0) {}
    virtual void native_setPixelRatio(float a0) {}
    virtual void native_setResourceLoadingFromDiskAllowed(bool a0) {}
    virtual void native_setShaderHotReloadingEnabled(bool a0) {}
    virtual void native_setShowSymbolBoundingBoxes(bool a0) {}
    virtual void native_setStylingZoomLevelOverride(std::optional<float> a0) {}
    virtual void native_setSymbolScreenPaddingEnabled(bool a0) {}
    virtual void native_setSymbolVisibilityStateful(bool a0) {}
    virtual void native_setTestAccountForPersonalHeatmap(PersonalHeatmapTestAccount a0) {}
    virtual void native_setTileDownloadEventProcessor(jni::Opaque a0) {}
    virtual void native_setVsyncEnabled(bool a0) {}
    virtual void native_setupDefaultMockTiles() {}
    virtual void native_startDownloadingResourcesForSecondVideoRenderingPass() {}
    virtual TestingContextHandleImpl* native_startEnteringTestingContext(jni::Opaque a0, std::string a1, std::string a2, jni::Opaque a3) { return {}; }
    virtual void native_startLuaAnimationScript(std::string a0, std::optional<float> a1, jni::Opaque a2) {}
    virtual void native_startMockTileServer() {}
    virtual void native_startTrackPerfTest(std::vector<WorldPoint3> a0, float a1, int64_t a2, bool a3, jni::Opaque a4) {}
    virtual void native_startVideoRenderingFirstPass() {}
    virtual void native_startVideoRenderingSecondPass() {}
    virtual void native_stopLuaAnimationScript() {}
    virtual void native_stopMockTileServer() {}
    virtual void native_switchToCustomStyle(std::string a0, bool a1, bool a2, bool a3, bool a4, bool a5, bool a6, std::optional<float> a7) {}
    virtual LodCalculationControlParams native_updateLodCalculationControlParams(LodCalculationControlParams a0) { return {}; }
};

// ---- RecordContent ---------------------------------------------
class RecordContentApi {
public:
    virtual ~RecordContentApi() = default;
    virtual void nativeDestroy() {}
    virtual void native_displayLiveSegments(std::vector<CandidateSegment> a0) {}
    virtual void native_displayRecordingLine(WorldLine2 a0) {}
    virtual void native_removeAllLiveSegments() {}
    virtual void native_removeRecordingLine() {}
    virtual void native_showRecordingLineFinishMarker(bool a0) {}
};

// ---- RouteContent ----------------------------------------------
class RouteContentApi {
public:
    virtual ~RouteContentApi() = default;
    virtual void nativeDestroy() {}
    virtual void native_addWaypointToRoute(std::string a0, RouteWaypoint a1) {}
    virtual void native_displayAnimatedRoute(std::string a0, WorldLine2 a1, std::vector<RouteSection> a2, std::vector<RouteWaypoint> a3, RouteSelectionStyle a4, bool a5, float a6, float a7, jni::Opaque a8) {}
    virtual void native_displayRoute(std::string a0, WorldLine2 a1, std::vector<RouteSection> a2, std::vector<RouteWaypoint> a3, RouteSelectionStyle a4, bool a5) {}
    virtual void native_displayRouteScrubber(double a0, double a1) {}
    virtual void native_fadeInUnselectedRoutes(float a0, std::optional<float> a1, jni::Opaque a2) {}
    virtual void native_removeAllRoutes() {}
    virtual void native_removeRouteScrubber() {}
    virtual void native_removeRoutes(std::vector<std::string> a0) {}
    virtual void native_selectRoute(std::string a0, RouteSelectionStyle a1) {}
    virtual void native_selectWaypointOnRoute(std::string a0, std::string a1) {}
    virtual void native_stopUnselectedRoutesFadeIn() {}
    virtual void native_unselectRoute(std::string a0) {}
    virtual void native_unselectWaypointOnRoute(std::string a0, std::string a1) {}
};

// ---- SegmentsFilter --------------------------------------------
class SegmentsFilterApi {
public:
    virtual ~SegmentsFilterApi() = default;
    virtual void nativeDestroy() {}
    virtual std::vector<SegmentFeature> native_getVisibleSegments() { return {}; }
    virtual void native_hide() {}
    virtual void native_selectSegment(int64_t a0) {}
    virtual void native_show(SegmentsFilterOptions a0) {}
    virtual void native_unselectSegment() {}
};

// ---- StravaPoiFilter -------------------------------------------
class StravaPoiFilterApi {
public:
    virtual ~StravaPoiFilterApi() = default;
    virtual void nativeDestroy() {}
    virtual void native_disableBikeShares() {}
    virtual void native_disableStartPoints() {}
    virtual void native_enableBikeShares() {}
    virtual void native_enableStartPoints(StartPointFilterOptions a0) {}
    virtual void native_selectPoi(jni::Opaque a0) {}
    virtual void native_setShow(bool a0) {}
    virtual void native_unselectPoi() {}
};

// ---- TerrainEngine ---------------------------------------------
class TerrainEngineApi {
public:
    virtual ~TerrainEngineApi() = default;
    virtual void nativeDestroy() {}
    virtual void native_addOnGridChangeListener(jni::Opaque a0) {}
    virtual void native_addOnMouseMoveListener(jni::Opaque a0) {}
    virtual void native_addOnTapListener(jni::Opaque a0) {}
    virtual void native_addOverlayPolygon(OverlayPolygon a0) {}
    virtual void native_addPostUpdateListener(jni::Opaque a0) {}
    virtual void native_addTileLoadingListener(jni::Opaque a0) {}
    virtual void native_addViewUpdateListener(jni::Opaque a0) {}
    virtual void native_didEnterBackground() {}
    virtual void native_didEnterForeground() {}
    virtual CameraImpl* native_getCamera() { return {}; }
    virtual ContentImpl* native_getContent() { return {}; }
    virtual DebugApiImpl* native_getDebugApi() { return {}; }
    virtual double native_getElevation(WorldPoint2 a0) { return {}; }
    virtual float native_getFps() { return {}; }
    virtual UnitsInfo native_getGridUnits() { return {}; }
    virtual OfflineImpl* native_getOffline() { return {}; }
    virtual OverriddenFeaturePropertiesImpl* native_getOverriddenFeatureProperties() { return {}; }
    virtual PrivateApiImpl* native_getPrivateApi(std::string a0) { return {}; }
    virtual UtilsImpl* native_getUtils() { return {}; }
    virtual void native_receivedMemoryWarning() {}
    virtual void native_removeAllOverlayPolygons() {}
    virtual void native_removeOnGridChangeListener(jni::Opaque a0) {}
    virtual void native_removeOnMouseMoveListener(jni::Opaque a0) {}
    virtual void native_removeOnTapListener(jni::Opaque a0) {}
    virtual void native_removePostUpdateListener(jni::Opaque a0) {}
    virtual void native_removeTileLoadingListener(jni::Opaque a0) {}
    virtual void native_removeViewUpdateListener(jni::Opaque a0) {}
    virtual void native_requestRender() {}
    virtual void native_setAlwaysRender(bool a0) {}
    virtual void native_setAuthenticationErrorListener(jni::Opaque a0) {}
    virtual void native_setDesiredLanguage(Language a0) {}
    virtual void native_setEnableScreenSymbols(bool a0) {}
    virtual void native_setGlobalHeatmapConfig(GlobalHeatmapConfig a0) {}
    virtual void native_setImageryOverlay(ImageryOverlay a0) {}
    virtual void native_setIsTerrain3dEnabled(bool a0) {}
    virtual void native_setLogLevel(LogLevel a0) {}
    virtual void native_setMapType(MapType a0) {}
    virtual void native_setNightlyHeatmapConfig(NightlyHeatmapConfig a0) {}
    virtual void native_setOverlayPolygons(std::vector<OverlayPolygon> a0) {}
    virtual void native_setOverriddenFeatureProperties(jni::Opaque a0) {}
    virtual void native_setPaused(bool a0) {}
    virtual void native_setPersonalHeatmapConfig(PersonalHeatmapConfig a0) {}
    virtual void native_setRenderingDisabled(bool a0) {}
    virtual void native_setScreenSymbolRefreshInterval(float a0) {}
    virtual void native_setServerConfig(ServerConfig a0, bool a1) {}
    virtual void native_setStravaAuthData(int64_t a0, std::vector<HttpHeader> a1) {}
    virtual void native_setStravaSourcesExtraHeaders(std::vector<HttpHeader> a0) {}
    virtual void native_setSurfaceIdentifier(std::string a0) {}
    virtual void native_setUnitSystem(MeasurementUnitSystem a0) {}
    virtual void native_setWeeklyHeatmapConfig(WeeklyHeatmapConfig a0) {}
    virtual void native_update() {}
    virtual void native_waitToLoadMap(jni::Opaque a0, bool a1, float a2) {}
};

// ---- TestingContextEventListener -------------------------------
class TestingContextEventListenerApi {
public:
    virtual ~TestingContextEventListenerApi() = default;
    virtual void nativeDestroy() {}
    virtual void native_onEntered() {}
    virtual void native_onErrorEntering(std::string a0) {}
    virtual void native_onLeft() {}
};

// ---- TestingContextHandle --------------------------------------
class TestingContextHandleApi {
public:
    virtual ~TestingContextHandleApi() = default;
    virtual void nativeDestroy() {}
    virtual TestingContextStatus native_status() { return {}; }
    virtual void native_stopEnteringOrInitiateLeaving() {}
};

// ---- TestsListener ---------------------------------------------
class TestsListenerApi {
public:
    virtual ~TestsListenerApi() = default;
    virtual void nativeDestroy() {}
    virtual void native_onFinished() {}
    virtual void native_onPerformanceTestResult(PerformanceTestResult a0) {}
    virtual void native_onTestFinished(std::string a0) {}
};

// ---- TouchListener ---------------------------------------------
class TouchListenerApi {
public:
    virtual ~TouchListenerApi() = default;
    virtual void nativeDestroy() {}
    virtual void native_onDoubleTap(OsTouch a0) {}
    virtual void native_onLongPress(OsTouch a0) {}
    virtual void native_onSingleTap(OsTouch a0) {}
    virtual void native_onTapAndMove(OsTouch a0, OsTouchAction a1) {}
    virtual void native_onTouches(std::vector<OsTouch> a0) {}
};

// ---- TrackPerfTestCompletionListener ---------------------------
class TrackPerfTestCompletionListenerApi {
public:
    virtual ~TrackPerfTestCompletionListenerApi() = default;
    virtual void nativeDestroy() {}
    virtual void native_onPerfTestFinished(TrackPerfTestResults a0) {}
};

// ---- Utils -----------------------------------------------------
class UtilsApi {
public:
    virtual ~UtilsApi() = default;
    virtual void nativeDestroy() {}
    virtual int64_t native_addCustomMarker2(double a0, double a1, std::string a2, std::string a3, std::string a4) { return {}; }
    virtual int64_t native_addCustomMarker3(double a0, double a1, std::string a2, std::string a3, std::string a4) { return {}; }
    virtual float native_getUserMarkerLocationAngle() { return {}; }
    virtual void native_hideTileGrid() {}
    virtual bool native_isUserMarkerOnScreen() { return {}; }
    virtual void native_removeCustomMarker(int64_t a0) {}
    virtual void native_removeUserMarker() {}
    virtual void native_setHighlightedTiles(std::vector<HighlightedTile> a0) {}
    virtual void native_setUserMarker(double a0, double a1) {}
    virtual void native_setUserMarkerAnimation(float a0) {}
    virtual void native_setUserMarkerDirection(float a0) {}
    virtual void native_setUserMarkerPrecision(float a0) {}
    virtual void native_setUserMarkerState(bool a0) {}
    virtual void native_showTileGrid(int8_t a0, std::vector<HighlightedTile> a1) {}
    virtual void native_updateCustomMarkerPosition(int64_t a0, double a1, double a2) {}
};

// ---- VisibleAreaSamplesSink ------------------------------------
class VisibleAreaSamplesSinkApi {
public:
    virtual ~VisibleAreaSamplesSinkApi() = default;
    virtual void nativeDestroy() {}
    virtual void native_processVisiblePoint(WorldPoint2 a0) {}
};

// ---- WindowLifecycleListener -----------------------------------
class WindowLifecycleListenerApi {
public:
    virtual ~WindowLifecycleListenerApi() = default;
    virtual void nativeDestroy() {}
    virtual void native_onDisplayChanged(float a0) {}
    virtual void native_onWindowResized(int32_t a0, int32_t a1) {}
    virtual void native_onWindowStateChanged(WindowState a0) {}
};

}  // namespace fatmap
