package dev.openstride.map

import android.content.Context
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL

/**
 * The engine's way of getting a tile.
 *
 * The engine has no TLS of its own, and it should not have one: the
 * original carries its own certificate bundle, which means it trusts what
 * the app shipped rather than what the phone trusts. The engine asks the
 * application for the bytes instead, so the platform's network stack does
 * the work and the phone's trust store decides.
 *
 * The class is found by name from the engine, so the name here is part of
 * the contract between the two.
 */
object TileTransport {
    private const val TAG = "openstride-tiles"
    private const val TIMEOUT_MILLIS = 15_000

    /**
     * Fetches the bytes behind a URL.
     *
     * @return the bytes, or an empty array when the tile could not be
     *   fetched.
     */
    @JvmStatic
    fun fetch(url: String): ByteArray {
        var connection: HttpURLConnection? = null
        return try {
            connection =
                (URL(url).openConnection() as HttpURLConnection).apply {
                    connectTimeout = TIMEOUT_MILLIS
                    readTimeout = TIMEOUT_MILLIS
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", "OpenStride/0.1 (Android)")
                }
            if (connection.responseCode !in 200..299) {
                Log.d(TAG, "the tile was refused: ${connection.responseCode}")
                ByteArray(0)
            } else {
                connection.inputStream.use { it.readBytes() }
            }
        } catch (e: Exception) {
            Log.d(TAG, "the tile could not be fetched: ${e.message}")
            ByteArray(0)
        } finally {
            connection?.disconnect()
        }
    }

    /** The context the transport was set up with, for the tests. */
    internal var context: Context? = null
}
