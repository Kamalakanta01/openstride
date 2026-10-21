// Getting the bytes of a tile.
//
// See tile_cache.h.

#include "tiles/tile_cache.h"

#include <map>
#include <utility>

namespace fatmap::tiles {

TileCache::TileCache(
    Fetcher fetcher) :
    mFetcher(std::move(fetcher)) {}

bool TileCache::get(
    const std::string& url,
    std::vector<uint8_t>& out) {
    const auto it = mTiles.find(url);
    if (it != mTiles.end()) {
        out = it->second;
        return true;
    }
    if (!mFetcher) {
        return false;
    }
    std::vector<uint8_t> bytes;
    if (!mFetcher(url, bytes)) {
        return false;
    }
    // A tile that is empty is a failure, not a tile.
    if (bytes.empty()) {
        return false;
    }
    mTiles[url] = bytes;
    out = std::move(bytes);
    return true;
}

void TileCache::put(
    const std::string& url,
    std::vector<uint8_t> bytes) {
    mTiles[url] = std::move(bytes);
}

}  // namespace fatmap::tiles
