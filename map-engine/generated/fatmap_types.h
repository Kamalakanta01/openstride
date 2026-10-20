// Generated from the Fatmap SDK interface contract. Do not edit by hand.
#pragma once

#include <cstdint>
#include <optional>
#include <set>
#include <string>
#include <vector>

#include "fatmap_jni_types.h"

namespace fatmap {

enum class CameraStateFlags {
    TARGET,
    VIEW_TYPE,
    ZOOM_LEVEL
};

enum class CameraViewType {
    CAMERA2D,
    CAMERA3D
};

enum class ClickEventType {
    CONTENT,
    MAP,
    USERMARKER
};

enum class ClickSourceType {
    MOUSE,
    TOUCH
};

enum class ContentGroup {
    ACTIVITIES,
    CUSTOMLINE,
    CUSTOMMARKER,
    OSM,
    RECORD,
    ROUTES,
    SEGMENTS,
    STRAVAPOI
};

enum class DebugOverlayCorner {
    LOWERLEFT,
    LOWERRIGHT,
    UPPERLEFT,
    UPPERRIGHT
};

enum class DeviceOrientation {
    LANDSCAPE_LEFT,
    LANDSCAPE_RIGHT,
    PORTRAIT,
    PORTRAIT_UPSIDE_DOWN
};

enum class FollowMode {
    HEADING,
    POSITION
};

enum class HeatmapColorScheme {
    BLUE,
    BLUE_RED,
    ORANGE,
    PINK,
    PURPLE,
    SUNSET,
    WEEKLY_RAINBOW
};

enum class ImageFormat {
    JPG,
    PNG,
    RGB,
    RGBA
};

enum class ImageryOverlay {
    ASPECT,
    AVALANCHE,
    GRADIENT,
    GRID,
    NONE
};

enum class Language {
    ES,
    FR,
    IT,
    LOCAL,
    PT,
    RU,
    ZH_HANS,
    ZH_HANT,
    DE,
    EN,
    JA
};

enum class LogLevel {
    DEBUG,
    DISABLED,
    ERROR,
    INFO,
    WARNING
};

enum class MapType {
    STRAVA_HYBRID,
    STRAVA_PLANET_HYBRID,
    STRAVA_PLANET_HYBRID_EXPERIMENT,
    STRAVA_PLANET_SATELLITE_SUMMER,
    STRAVA_PLANET_SATELLITE_SUMMER_EXPERIMENT,
    STRAVA_PLANET_TOPO_DARK,
    STRAVA_PLANET_TOPO_DARK_EXPERIMENT,
    STRAVA_PLANET_TOPO_LIGHT,
    STRAVA_PLANET_TOPO_LIGHT_EXPERIMENT,
    STRAVA_PLANET_TOPO_WINTER,
    STRAVA_PLANET_TOPO_WINTER_EXPERIMENT,
    STRAVA_PLANET_TOPO_WINTER_HYBRID,
    STRAVA_PLANET_TOPO_WINTER_HYBRID_EXPERIMENT,
    STRAVA_SATELLITE_SUMMER,
    STRAVA_SATELLITE_WINTER,
    STRAVA_TOPO_DARK,
    STRAVA_TOPO_LIGHT,
    STRAVA_TOPO_WINTER,
    STRAVA_TOPO_WINTER_HYBRID
};

enum class MeasurementUnit {
    FEET,
    KILOMETER,
    METER,
    MILE
};

enum class MeasurementUnitSystem {
    IMPERIAL,
    METRIC
};

enum class MouseButtonCode {
    LEFT,
    MIDDLE,
    RIGHT
};

enum class OsTouchAction {
    CANCEL,
    END,
    MOVE,
    START
};

enum class PolylineHighlightSectionIcon {
    BEST_EFFORT_CLIMB,
    BEST_EFFORT_PACE,
    BEST_EFFORT_POWER,
    BRONZE_PR,
    GOLD_PR,
    LOCAL_LEGEND,
    SILVER_PR,
    TOP10_1,
    TOP10_10,
    TOP10_2,
    TOP10_3,
    TOP10_4,
    TOP10_5,
    TOP10_6,
    TOP10_7,
    TOP10_8,
    TOP10_9
};

enum class PolylineHighlightSectionType {
    BLUE,
    BRONZE,
    GOLD,
    PURPLE,
    SILVER,
    TEAL
};

enum class RoadSurfaceType {
    PAVED,
    UNPAVED,
    UNSPECIFIED
};

enum class RouteSelectionStyle {
    CANONICAL,
    GRADIENT
};

enum class RouteWaypointCategory {
    AID_STATION,
    ALERT,
    ART,
    ATTRACTION,
    BAR,
    BEACH,
    BIKE_SHOP,
    CAMP_SITE,
    CHECKPOINT,
    COFFEE,
    DISTANCE_MARKER,
    GAS_STATION,
    GENERIC,
    GROCERY,
    LODGING,
    MEETING_SPOT,
    MONUMENT,
    PARK,
    PARKING,
    PEAK,
    PHARMACY,
    RESTAURANT,
    RESTROOM,
    REST_AREA,
    ROUTE_PLANNED_FINISH,
    ROUTE_PLANNED_START,
    ROUTE_PLANNED_WAYPOINT,
    SEGMENT_END,
    SEGMENT_START,
    TRAILHEAD,
    TRANSITION_ZONE,
    VIEWPOINT,
    WATER_SOURCE
};

enum class TestingContextStatus {
    ENTERED,
    ENTERING,
    ENTERINGFAILED,
    LEAVING,
    NOCONTEXT
};

enum class WindowState {
    ACTIVE,
    CREATED,
    DESTROYED,
    INACTIVE,
    NONE
};

struct ActivitySection {
    int64_t numPolylineEdges;
    bool privateSection;
    RoadSurfaceType surfaceType;
};

struct CameraMargins {
    float bottom;
    float left;
    float right;
    float top;
};

struct Color {
    double a;
    double b;
    double g;
    double r;
};

struct CustomImage {
    jni::Opaque data;
    ImageFormat format;
    int32_t height;
    float pixelRatio;
    int32_t width;
};

struct DebugOverlayPadding {
    float horizontal;
    float vertical;
};

struct GivenUpDownloadsInfo {
    int64_t currentSequenceNumber;
    bool haveGivenUpDownloads;
    int64_t newGivenUpDownloads;
};

struct GlobalHeatmapConfig {
    std::string activityType;
    HeatmapColorScheme colorScheme;
    std::optional<float> opacityMultiplier;
};

struct HttpHeader {
    std::string name;
    std::string value;
};

struct IntervalFloat {
    float from;
    float to;
};

struct IntervalInt {
    int64_t from;
    int64_t to;
};

struct LodCalculationControlParams {
    int32_t maxLodDifferenceForOffset;
    float tileSplitThresholdOffset;
};

struct LuaScriptError {
    std::string what;
};

struct MemoryStats {
    double currentAllocatedMb;
    int64_t currentAllocationCount;
    double totalAllocatedMb;
    int64_t totalAllocationCount;
};

struct NightlyHeatmapConfig {
    std::string activityType;
    std::optional<float> opacityMultiplier;
};

struct OsTouchPosition {
    float normalizedX;
    float normalizedY;
};

struct OverriddenFeatureProperties {
    std::string STRAVA_BIKE_LANES_DISABLED_PROPERTY;
    std::string STRAVA_BIKE_MAP_ENABLED_PROPERTY;
    std::string STRAVA_HIKE_MAP_ENABLED_PROPERTY;
};

struct PerformanceMetrics {
    int64_t frameCountFromStart;
    float frameTime;
};

struct PerformanceTestResult {
    std::string name;
    std::string unit;
    double value;
};

struct PersonalHeatmapConfig {
    HeatmapColorScheme colorScheme;
    std::string filterEnd;
    std::string filterStart;
    std::string filterType;
    bool includeActivitiesVisibleOnlyToMe;
    bool includeActivitiesVisibleToEveryone;
    bool includeActivitiesVisibleToFollowersOnly;
    bool includeCommutes;
    std::optional<float> opacityMultiplier;
    bool respectPrivacyZones;
};

struct PersonalHeatmapTestAccount {
    std::string accessToken;
    std::string stravaAthleteId;
    std::string tileEndpointHostName;
};

struct PointScreenPosition {
    bool isOccluded;
    float x;
    float y;
};

struct PolylineHighlightSection {
    int64_t endIndex;
    PolylineHighlightSectionIcon icon;
    std::string iconText;
    std::string identifier;
    int64_t startIndex;
    PolylineHighlightSectionType type;
};

struct QuadIndex {
    int32_t x;
    int32_t y;
    int8_t z;
};

struct RouteSection {
    int64_t numPolylineEdges;
    RoadSurfaceType surfaceType;
};

struct SegmentFeature {
    int64_t activityType;
    std::optional<int64_t> athletesAllTime;
    std::optional<int64_t> attemptsAllTime;
    std::optional<float> averageGrade;
    std::string darkSparklineUrl;
    std::string darkThumbnailUrl;
    std::optional<double> distance;
    int64_t id;
    std::string name;
    double score;
    std::string sparklineUrl;
    std::optional<float> startXYLat;
    std::optional<float> startXYLng;
    std::string thumbnailUrl;
    std::string verifiedStatus;
};

struct SegmentsFilterOptions {
    std::string activityTypes;
    std::optional<bool> creator;
    std::string distanceMax;
    std::string distanceMin;
    std::string elevationFilter;
    std::optional<bool> overall;
    std::optional<bool> starred;
    std::string surfaceTypes;
    std::optional<bool> top10;
    std::optional<bool> verified;
};

struct StartPointFilterOptions {
    int64_t difficulty;
    int64_t distance;
    float elevation;
    int64_t routeType;
    int64_t surfaceType;
};

struct TileSourceConfig {
    std::string globalHeatmapURLTemplate;
    std::string gradientURLTemplate;
    std::string heightmapURLTemplate;
    std::string mapboxStreetsTerrainURLTemplate;
    std::string mapboxStreetsURLTemplate;
    std::string nightlyHeatmapURLTemplate;
    std::string personalHeatmapURLTemplate;
    std::string segmentsTemplate;
    std::string stravaContoursURLTemplate;
    std::string stravaPlanetSportURLTemplate;
    std::string stravaPlanetURLTemplate;
    std::string stravaPoiURLTemplate;
    std::string summerImageryURLTemplate;
    std::string weeklyHeatmapURLTemplate;
    std::string winterImageryURLTemplate;
    std::string winterOverlayImageryURLTemplate;
};

struct TrackPerfTestResults {
    int64_t avgFrameTimeMs;
    int64_t maxFrameTimeMs;
    int64_t minFrameTimeMs;
};

struct UnitsInfo {
    double meters;
    double size;
    MeasurementUnit unit;
};

struct UserMarkerClickEvent {
    MouseButtonCode button;
    ClickSourceType source;
    ClickEventType type;
};

struct VersionInfo {
    std::string branchName;
    std::string buildDate;
    std::string buildHash;
    std::string buildId;
    int32_t buildVersion;
    int8_t environment;
    int16_t majorVersion;
    int16_t minorVersion;
    int16_t patchVersion;
    std::string versionString;
};

struct WeeklyHeatmapConfig {
    std::string activityType;
    HeatmapColorScheme colorScheme;
    std::optional<float> opacityMultiplier;
};

struct WorldPoint2 {
    double latitude;
    double longitude;
};

struct WorldPoint3 {
    float elevation;
    double latitude;
    double longitude;
};

struct AnimatedImage {
    float fps;
    std::vector<CustomImage> frames;
    bool shouldLoop;
};

struct CameraTarget {
    float bearing;
    float distance;
    WorldPoint3 lookAtPoint;
    float pitch;
};

struct ContentClickEventObject {
    ContentGroup contentGroup;
    jni::Opaque contentSelectionHandle;
    std::string customTapId;
    std::string featureCategory;
    int64_t id;
    std::string layerName;
    std::string name;
    std::string position;
    WorldPoint3 worldPoint;
};

struct HighlightedTile {
    Color color;
    QuadIndex index;
};

struct LookAtPoint {
    bool isPointingAtMap;
    WorldPoint3 worldPoint;
};

struct MapClickEvent {
    MouseButtonCode button;
    ClickSourceType source;
    ClickEventType type;
    WorldPoint3 worldPoint;
};

struct OsTouchInfo {
    OsTouchPosition position;
    double timestamp;
};

struct RouteWaypoint {
    RouteWaypointCategory category;
    std::string identifier;
    WorldPoint2 point;
    std::string title;
};

struct ServerConfig {
    std::string mapboxServerSecret;
    std::string mapboxServerURL;
    std::string segmentsServerURL;
    std::string stravaPoiServerURL;
    std::string stravaTileServerURL;
    TileSourceConfig tileSourceConfig;
};

struct VisibleAreaBounds {
    WorldPoint2 bottomLeftCorner;
    WorldPoint2 bottomRightCorner;
    WorldPoint2 topLeftCorner;
    WorldPoint2 topRightCorner;
};

struct WorldBounds2 {
    WorldPoint2 northEastCorner;
    WorldPoint2 southWestCorner;
};

struct WorldLine2 {
    std::vector<WorldPoint2> points;
};

struct CameraTargetPlus {
    bool avoidMountains;
    CameraTarget cameraTarget;
};

struct CandidateSegment {
    std::string identifier;
    bool isStarred;
    WorldLine2 line;
    std::string title;
};

struct ContentClickEvent {
    MouseButtonCode button;
    std::vector<ContentClickEventObject> objects;
    ClickSourceType source;
    ClickEventType type;
    WorldPoint3 worldPoint;
};

struct MouseMoveEvent {
    std::vector<ContentClickEventObject> objects;
    float x;
    float y;
};

struct OsTouch {
    OsTouchInfo currentInfo;
    int64_t id;
};

struct OverlayPolygon {
    std::vector<IntervalFloat> aspect;
    WorldLine2 boundary;
    Color fillColor;
    IntervalFloat gradientRange;
    IntervalFloat heightRange;
};

}  // namespace fatmap
