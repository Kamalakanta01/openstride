// Getting the bytes of a tile.
//
// The engine has no TLS of its own, and it should not have one: the
// original carries its own certificate bundle, which means it trusts what
// the app shipped rather than what the phone trusts. This asks the
// application for the bytes instead, so the platform's network stack does
// the work and the phone's trust store decides.

#pragma once

#include <cstdint>
#include <functional>
#include <map>
#include <string>
#include <vector>

namespace fatmap::tiles {

/// Fetches the bytes behind a URL.
using Fetcher = std::function<bool(const std::string& url, std::vector<uint8_t>& out)>;

/// A tile, and where it came from.
struct Tile {
    std::vector<uint8_t> bytes;
    bool fromCache = false;
};

/**
 * The tiles the engine has, and the ones it does not.
 *
 * The cache is in memory: the tiles are small, the working set of a map is
 * a few dozen of them, and a disk cache would mean another set of file
 * handling the engine has to get right on its own.
 */
class TileCache {
  public:
    explicit TileCache(
        Fetcher fetcher = nullptr);

    /// Uses this fetcher for the tiles that are not cached.
    void setFetcher(Fetcher fetcher) { mFetcher = std::move(fetcher); }

    /// Whether there is a fetcher at all.
    bool canFetch() const { return static_cast<bool>(mFetcher); }

    /// The tile behind a URL, from the cache when it has one.
    bool get(
        const std::string& url,
        std::vector<uint8_t>& out);

    /// Puts a tile into the cache.
    void put(
        const std::string& url,
        std::vector<uint8_t> bytes);

    /// How many tiles are cached.
    size_t size() const { return mTiles.size(); }

    /// Empties the cache.
    void clear() { mTiles.clear(); }

  private:
    Fetcher mFetcher;
    std::map<std::string, std::vector<uint8_t>> mTiles;
};

}  // namespace fatmap::tiles
