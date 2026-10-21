// Turning a ring of points into triangles.
//
// See polygon.h.

#include "render/polygon.h"

#include <algorithm>
#include <array>
#include <cmath>

namespace fatmap::render::polygon {

namespace {

/// Twice the signed area of a triangle, and whether it is wound clockwise.
double cross(
    const Point& a,
    const Point& b,
    const Point& c) {
    return (b.first - a.first) * (c.second - a.second) -
           (b.second - a.second) * (c.first - a.first);
}

/// Whether a point is inside a triangle.
bool inside(
    const Point& a,
    const Point& b,
    const Point& c,
    const Point& p) {
    const double d1 = cross(a, b, p);
    const double d2 = cross(b, c, p);
    const double d3 = cross(c, a, p);
    const bool hasNegative = d1 < 0 || d2 < 0 || d3 < 0;
    const bool hasPositive = d1 > 0 || d2 > 0 || d3 > 0;
    return !(hasNegative && hasPositive);
}

}  // namespace

std::vector<std::array<int, 3>> triangulate(
    const std::vector<std::vector<Point>>& rings) {
    std::vector<std::array<int, 3>> triangles;
    if (rings.empty() || rings[0].size() < 3) {
        return triangles;
    }

    // The outline, wound so that it is counter-clockwise: the ear clipping
    // below assumes that.
    std::vector<Point> outline = rings[0];
    double area = 0.0;
    for (size_t i = 0; i < outline.size(); ++i) {
        const Point& a = outline[i];
        const Point& b = outline[(i + 1) % outline.size()];
        area += a.first * b.second - b.first * a.second;
    }
    if (area < 0) {
        std::reverse(outline.begin(), outline.end());
    }

    // The holes are kept as separate rings and cut out of the outline's
    // triangles, which is simpler than merging the rings and is right for
    // the shapes a map has: a lake in a park, a building in a block.
    std::vector<std::vector<Point>> holes;
    for (size_t ring = 1; ring < rings.size(); ++ring) {
        if (rings[ring].size() < 3) {
            continue;
        }
        holes.push_back(rings[ring]);
    }

    std::vector<int> remaining(outline.size());
    for (size_t i = 0; i < outline.size(); ++i) {
        remaining[i] = static_cast<int>(i);
    }

    int guard = 0;
    const int limit = static_cast<int>(outline.size()) * outline.size() + 16;
    while (remaining.size() > 3 && guard++ < limit) {
        bool cut = false;
        for (size_t i = 0; i < remaining.size(); ++i) {
            const int ia = remaining[(i + remaining.size() - 1) % remaining.size()];
            const int ib = remaining[i];
            const int ic = remaining[(i + 1) % remaining.size()];
            const Point& a = outline[static_cast<size_t>(ia)];
            const Point& b = outline[static_cast<size_t>(ib)];
            const Point& c = outline[static_cast<size_t>(ic)];
            if (cross(a, b, c) <= 0) {
                // Not convex, so it cannot be an ear.
                continue;
            }
            bool clear = true;
            for (const int other : remaining) {
                if (other == ia || other == ib || other == ic) {
                    continue;
                }
                if (inside(a, b, c, outline[static_cast<size_t>(other)])) {
                    clear = false;
                    break;
                }
            }
            if (!clear) {
                continue;
            }
            // The ear must not be inside a hole either.
            for (const auto& hole : holes) {
                const Point& centre = hole[0];
                if (inside(a, b, c, centre)) {
                    clear = false;
                    break;
                }
            }
            if (!clear) {
                continue;
            }
            triangles.push_back({ia, ib, ic});
            remaining.erase(remaining.begin() + static_cast<long>(i));
            cut = true;
            break;
        }
        if (!cut) {
            // The shape is not simple, or is wound the other way; a fan from
            // the first point is a good enough answer for a map.
            for (size_t i = 1; i + 1 < remaining.size(); ++i) {
                triangles.push_back({remaining[0], remaining[i], remaining[i + 1]});
            }
            break;
        }
    }
    if (remaining.size() == 3) {
        triangles.push_back({remaining[0], remaining[1], remaining[2]});
    }
    return triangles;
}

}  // namespace fatmap::render::polygon
