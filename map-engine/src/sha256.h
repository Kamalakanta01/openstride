// SHA-256, used to verify offline map bundles.
//
// The offline bundle protocol carries a digest for each bundle; the engine
// refuses to load a bundle whose contents do not match.

#pragma once

#include <string>

namespace fatmap {

/// The SHA-256 of a byte range, lowercase hex.
std::string sha256(const void* data, size_t length);

/// The SHA-256 of a file's contents, lowercase hex. Empty on failure.
std::string sha256File(const std::string& path);

}  // namespace fatmap
