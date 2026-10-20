// Projection and geodesic helpers for the map engine.
//
// Web Mercator, matching the tile scheme the original engine uses
// (Mapbox Vector Tiles, 256 px tiles, 2^z tiles per axis).

#pragma once

namespace fatmap::geo {

/// WGS-84 equatorial radius, in metres.
inline constexpr double kEarthRadius = 6378137.0;

/// Tile edge length in pixels at zoom 0.
inline constexpr double kTileSize = 256.0;

struct Point2 {
    double latitude = 0.0;
    double longitude = 0.0;
};

/// Wraps a longitude into [-180, 180).
double wrapLongitude(double longitude);

/// Clamps a latitude into the Web Mercator limit, +/-85.05112878.
double clampLatitude(double latitude);

/// Longitude to the tile X axis, in [0, 1].
double normalizeX(double longitude);

/// Latitude to the tile Y axis, in [0, 1].
double normalizeY(double latitude);

/// The inverse of [normalizeX] / [normalizeY].
Point2 denormalize(double x, double y);

/// Ground resolution in metres per pixel at the given zoom and latitude.
double metersPerPixel(double zoom, double latitude);

/// Great-circle distance in metres.
double haversineMeters(double lat1, double lon1, double lat2, double lon2);

/// Initial bearing from point 1 to point 2, in degrees clockwise from north.
double bearingDegrees(double lat1, double lon1, double lat2, double lon2);

/// Linear interpolation.
double lerp(double a, double b, double t);

/// Hermite smoothstep on [0, 1].
double smoothstep(double t);

/// Ease in and out; the standard curve for camera flights.
double easeInOut(double t);

/// Angle difference wrapped into (-180, 180].
double shortestAngle(double from, double to);

}  // namespace fatmap::geo
