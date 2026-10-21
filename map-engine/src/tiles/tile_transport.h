// Getting the bytes of a tile from the application.
//
// The engine has no TLS of its own, and it should not have one: the
// original carries its own certificate bundle, which means it trusts what
// the app shipped rather than what the phone trusts. This asks the
// application for the bytes, so the platform's network stack does the work.

#pragma once

#include <jni.h>

#include <string>

#include "tiles/tile_cache.h"

namespace fatmap::tiles {

/// The application class the engine asks for the tile bytes.
inline constexpr const char* kTransportClass = "dev/openstride/map/TileTransport";

/**
 * A fetcher that calls the application.
 *
 * The class is looked up once, with the class loader of the application, and
 * the method is called on the rendering thread, which is attached to the
 * runtime for the call.
 */
Fetcher applicationFetcher(JavaVM* vm);

}  // namespace fatmap::tiles
