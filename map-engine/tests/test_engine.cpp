// Host tests for the map engine.
//
// Deliberately dependency-free: a tiny assertion harness, so the tests build
// and run anywhere with a C++17 compiler.

#include <cmath>
#include <cstdio>
#include <cstdlib>
#include <string>

#include "fatmap_engine.h"
#include "geo.h"
#include "sha256.h"

using namespace fatmap;

namespace {

int gFailures = 0;
int gChecks = 0;

void check(bool condition, const char* what, int line) {
    ++gChecks;
    if (!condition) {
        ++gFailures;
        std::printf("FAIL line %d: %s\n", line, what);
    }
}

void checkNear(double actual, double expected, double tolerance, const char* what, int line) {
    ++gChecks;
    if (std::fabs(actual - expected) > tolerance) {
        ++gFailures;
        std::printf("FAIL line %d: %s (got %.9f, expected %.9f)\n", line, what, actual, expected);
    }
}

#define CHECK(cond) check((cond), #cond, __LINE__)
#define CHECK_NEAR(actual, expected, tol) checkNear((actual), (expected), (tol), #actual, __LINE__)

void testProjection() {
    // The equator and the prime meridian land on the centre of the tile grid.
    CHECK_NEAR(geo::normalizeX(0.0), 0.5, 1e-12);
    CHECK_NEAR(geo::normalizeY(0.0), 0.5, 1e-12);

    // The projection round-trips.
    const geo::Point2 p = geo::denormalize(0.25, 0.75);
    CHECK_NEAR(p.latitude, -66.51326044311186, 1e-6);
    CHECK_NEAR(p.longitude, -90.0, 1e-6);

    // Longitudes wrap.
    CHECK_NEAR(geo::wrapLongitude(190.0), -170.0, 1e-9);
    CHECK_NEAR(geo::wrapLongitude(-190.0), 170.0, 1e-9);
}

void testResolution() {
    // At zoom 0 the whole world is 256 pixels wide, so the equator has a
    // resolution of about 156543 metres per pixel.
    CHECK_NEAR(geo::metersPerPixel(0.0, 0.0), 156543.03392, 0.01);
    // Each zoom level halves it.
    CHECK_NEAR(geo::metersPerPixel(1.0, 0.0), 78271.51696, 0.01);
    // Resolution shrinks with the cosine of the latitude.
    CHECK(geo::metersPerPixel(10.0, 60.0) < geo::metersPerPixel(10.0, 0.0));
}

void testGeodesy() {
    // One degree of latitude is about 111 km.
    CHECK_NEAR(geo::haversineMeters(0.0, 0.0, 1.0, 0.0), 111195.0, 200.0);
    // Due north is bearing zero.
    CHECK_NEAR(geo::bearingDegrees(0.0, 0.0, 1.0, 0.0), 0.0, 1e-6);
    // Due east is bearing ninety.
    CHECK_NEAR(geo::bearingDegrees(0.0, 0.0, 0.0, 1.0), 90.0, 1e-6);
}

void testEasing() {
    CHECK_NEAR(geo::easeInOut(0.0), 0.0, 1e-12);
    CHECK_NEAR(geo::easeInOut(1.0), 1.0, 1e-12);
    CHECK_NEAR(geo::easeInOut(0.5), 0.5, 1e-12);
    CHECK_NEAR(geo::smoothstep(0.5), 0.5, 1e-12);
    CHECK_NEAR(geo::shortestAngle(350.0, 10.0), 20.0, 1e-9);
}

void testSha256() {
    // The published test vectors.
    CHECK(sha256("", 0) ==
          "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
    CHECK(sha256("abc", 3) ==
          "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
    const std::string longer = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq";
    CHECK(sha256(longer.data(), longer.size()) ==
          "248d6a61d20638b8e5c026930c3e6039a33ce45964ff2167f6ecedd419db06c1");
}

void testEngineCamera() {
    Engine engine;
    CameraImpl& camera = engine.camera();

    camera.native_setCameraViewType(CameraViewType::CAMERA3D);
    CHECK(camera.native_getCameraViewType() == CameraViewType::CAMERA3D);

    // A flight moves the camera over time.
    camera.native_flyTo(48.8584, 2.2945, 14.0f, std::nullopt, std::nullopt, 1.0f,
                        CameraMargins{}, jni::Opaque());
    CHECK(engine.cameraTransition().active);
    engine.advance(0.5);
    const double latitude = engine.cameraTarget().latitude;
    CHECK(latitude > 0.0 && latitude < 48.8584);
    engine.advance(1.0);
    CHECK_NEAR(engine.cameraTarget().latitude, 48.8584, 1e-6);
    CHECK(!engine.cameraTransition().active);
}

void testEngineContent() {
    Engine engine;
    ContentImpl& content = engine.content();

    WorldLine2 line;
    line.points.push_back({47.3769, 8.5417});
    line.points.push_back({47.3769, 8.5418});

    const int64_t handle = content.native_addCustomLine(line, "red", "5", false, "line-1");
    CHECK(handle > 0);
    CHECK(engine.lines().size() == 1);
    CHECK(content.native_removeCustomLine(handle));
    CHECK(engine.lines().empty());

    content.native_addDroppedPin("pin-1", {47.0, 8.0});
    CHECK(engine.markers().size() == 1);
    content.native_removeAllDroppedPins();
    CHECK(engine.markers().empty());
}

void testEngineFeatureProperties() {
    Engine engine;
    OverriddenFeaturePropertiesImpl& properties = engine.featureProperties();

    properties.native_addBooleanProperty("showBikeLanes", true);
    properties.native_addIntegerProperty("maxLabels", 42);
    properties.native_addDoubleProperty("opacity", 0.5);
    properties.native_addStringProperty("style", "topo-dark");

    CHECK(properties.native_getBooleanProperty("showBikeLanes").value_or(false));
    CHECK(properties.native_getIntegerProperty("maxLabels").value_or(0) == 42);
    CHECK_NEAR(properties.native_getDoubleProperty("opacity").value_or(0.0), 0.5, 1e-12);
    CHECK(properties.native_getStringProperty("style") == "topo-dark");

    // Unknown keys report nothing rather than a default.
    CHECK(!properties.native_getBooleanProperty("missing").has_value());

    properties.native_removeProperty("showBikeLanes");
    CHECK(!properties.native_getBooleanProperty("showBikeLanes").has_value());
}

void testEngineTileUrls() {
    Engine engine;
    const std::string url = engine.tileSource().tileUrl(
        "{MAPBOX_TILES_SERVER_URL}/tiles/{z}/{x}/{y}.pbf?token={access_token}", 12, 2200, 1500);
    CHECK(url.find("/tiles/12/2200/1500.pbf") != std::string::npos);
    // Secrets are never echoed into a log line.
    CHECK(url.find("token=REDACTED") != std::string::npos);
}

void testEngineVisibleBounds() {
    Engine engine;
    engine.camera().native_flyTo(0.0, 0.0, 10.0f, std::nullopt, std::nullopt, 0.0f,
                                 CameraMargins{}, jni::Opaque());
    engine.advance(1.0);
    const VisibleAreaBounds bounds =
        engine.camera().native_getVisibleAreaBounds(CameraMargins{}, 0.0f);
    CHECK(bounds.bottomLeftCorner.latitude < bounds.topLeftCorner.latitude);
    CHECK(bounds.bottomLeftCorner.longitude < bounds.bottomRightCorner.longitude);
    // The centre is inside the visible rectangle.
    CHECK(bounds.bottomLeftCorner.latitude <= 0.0);
    CHECK(bounds.topLeftCorner.latitude >= 0.0);
}

void testEngineDebugDump() {
    Engine engine;
    const std::string dump = engine.debug().native_dumpEngineState();
    CHECK(dump.find("OpenStride map engine") != std::string::npos);
    CHECK(dump.find("camera target") != std::string::npos);
}

}  // namespace

int main() {
    testProjection();
    testResolution();
    testGeodesy();
    testEasing();
    testSha256();
    testEngineCamera();
    testEngineContent();
    testEngineFeatureProperties();
    testEngineTileUrls();
    testEngineVisibleBounds();
    testEngineDebugDump();

    std::printf("%d checks, %d failures\n", gChecks, gFailures);
    return gFailures == 0 ? EXIT_SUCCESS : EXIT_FAILURE;
}
