// Turning a ring of points into triangles.
//
// OpenGL ES has no way of filling a shape: everything has to be triangles.
// The polygons in a vector tile are rings - the outline, then the holes in
// it - and this cuts them into triangles with the ear-clipping method.
//
// The arithmetic is plain geometry, so it can be tested on the host.

#pragma once

#include <array>
#include <utility>
#include <vector>

namespace fatmap::render::polygon {

/// A point in the plane.
using Point = std::pair<float, float>;

/// The indices of the triangles, into the ring that was passed in.
///
/// @param rings the outline first, then the holes in it.
std::vector<std::array<int, 3>> triangulate(
    const std::vector<std::vector<Point>>& rings);

}  // namespace fatmap::render::polygon
