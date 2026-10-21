package dev.openstride.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Where the athlete is.
 *
 * One source of truth for the whole app: the map draws the dot from it, the
 * recorder records the track from it, and the "centre on me" button moves the
 * camera with it. The platform's location manager is used rather than
 * Google's fused provider, so the app works on a phone without Play
 * services.
 *
 * The last known position is published as soon as the permission arrives, so
 * the map can centre on the athlete before the first fix arrives.
 */
object DeviceLocation {
    private val _location = MutableStateFlow<Location?>(null)

    /** The last position the phone reported. */
    val location: StateFlow<Location?> = _location.asStateFlow()

    private var manager: LocationManager? = null
    private var listener: LocationListener? = null

    /** Whether the app may read the location. */
    fun hasPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    /**
     * Starts listening for positions.
     *
     * Calling it again with the permission already granted is harmless.
     */
    @SuppressLint("MissingPermission")
    fun start(context: Context) {
        if (!hasPermission(context)) return
        if (listener != null) return

        val service = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return
        manager = service

        val updates =
            LocationListener { position -> _location.value = position }

        for (provider in PROVIDERS) {
            try {
                if (!service.isProviderEnabled(provider)) continue
                service.requestLocationUpdates(
                    provider,
                    MIN_INTERVAL_MILLIS,
                    MIN_DISTANCE_METERS,
                    updates,
                    Looper.getMainLooper(),
                )
            } catch (_: SecurityException) {
                // The permission was taken away between the check and here.
                return
            } catch (_: IllegalArgumentException) {
                // The provider is not on this device.
            }
        }
        listener = updates

        // The last known position is often already there, and it saves the
        // athlete waiting for the first fix.
        _location.value = lastKnown(service)
    }

    /** Stops listening. */
    fun stop() {
        val service = manager
        val updates = listener
        if (service != null && updates != null) {
            runCatching { service.removeUpdates(updates) }
        }
        listener = null
    }

    /** The best last known position the phone has. */
    @SuppressLint("MissingPermission")
    private fun lastKnown(service: LocationManager): Location? {
        var best: Location? = null
        for (provider in PROVIDERS) {
            val candidate =
                runCatching { service.getLastKnownLocation(provider) }.getOrNull() ?: continue
            if (best == null || candidate.time > best.time) {
                best = candidate
            }
        }
        return best
    }

    private const val MIN_INTERVAL_MILLIS = 1000L
    private const val MIN_DISTANCE_METERS = 0f
    private val PROVIDERS =
        listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
}
