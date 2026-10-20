#include "geo.h"

#include <algorithm>
#include <cmath>

namespace fatmap::geo {

namespace {
constexpr double kPi = 3.14159265358979323846;
constexpr double kMaxLatitude = 85.05112878;
}  // namespace

double wrapLongitude(double longitude) {
    if (longitude >= -180.0 && longitude < 180.0) {
        return longitude;
    }
    double wrapped = std::fmod(longitude + 180.0, 360.0);
    if (wrapped < 0.0) {
        wrapped += 360.0;
    }
    return wrapped - 180.0;
}

double clampLatitude(double latitude) {
    return std::clamp(latitude, -kMaxLatitude, kMaxLatitude);
}

double normalizeX(double longitude) {
    return (wrapLongitude(longitude) + 180.0) / 360.0;
}

double normalizeY(double latitude) {
    const double clamped = clampLatitude(latitude);
    const double phi = clamped * kPi / 180.0;
    return 0.5 - std::log(std::tan(phi) + 1.0 / std::cos(phi)) / (2.0 * kPi);
}

Point2 denormalize(double x, double y) {
    const double lon = x * 360.0 - 180.0;
    const double n = kPi * (1.0 - 2.0 * y);
    const double lat = std::atan(std::sinh(n)) * 180.0 / kPi;
    return {lat, lon};
}

double metersPerPixel(double zoom, double latitude) {
    const double phi = clampLatitude(latitude) * kPi / 180.0;
    const double worldPixels = kTileSize * std::pow(2.0, zoom);
    return std::cos(phi) * 2.0 * kPi * kEarthRadius / worldPixels;
}

double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
    const double phi1 = lat1 * kPi / 180.0;
    const double phi2 = lat2 * kPi / 180.0;
    const double dPhi = (lat2 - lat1) * kPi / 180.0;
    const double dLambda = (lon2 - lon1) * kPi / 180.0;
    const double a = std::sin(dPhi / 2.0) * std::sin(dPhi / 2.0) +
                     std::cos(phi1) * std::cos(phi2) * std::sin(dLambda / 2.0) *
                         std::sin(dLambda / 2.0);
    return 2.0 * kEarthRadius * std::atan2(std::sqrt(a), std::sqrt(1.0 - a));
}

double bearingDegrees(double lat1, double lon1, double lat2, double lon2) {
    const double phi1 = lat1 * kPi / 180.0;
    const double phi2 = lat2 * kPi / 180.0;
    const double dLambda = (lon2 - lon1) * kPi / 180.0;
    const double y = std::sin(dLambda) * std::cos(phi2);
    const double x = std::cos(phi1) * std::sin(phi2) -
                     std::sin(phi1) * std::cos(phi2) * std::cos(dLambda);
    return std::fmod(std::atan2(y, x) * 180.0 / kPi + 360.0, 360.0);
}

double lerp(double a, double b, double t) {
    return a + (b - a) * t;
}

double smoothstep(double t) {
    const double clamped = std::clamp(t, 0.0, 1.0);
    return clamped * clamped * (3.0 - 2.0 * clamped);
}

double easeInOut(double t) {
    const double clamped = std::clamp(t, 0.0, 1.0);
    if (clamped < 0.5) {
        return 2.0 * clamped * clamped;
    }
    const double f = -2.0 * clamped + 2.0;
    return 1.0 - (f * f) / 2.0;
}

double shortestAngle(double from, double to) {
    double delta = std::fmod(to - from, 360.0);
    if (delta > 180.0) {
        delta -= 360.0;
    }
    if (delta < -180.0) {
        delta += 360.0;
    }
    return delta;
}

}  // namespace fatmap::geo
