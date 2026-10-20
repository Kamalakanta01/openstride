package dev.openstride.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap

/**
 * Where the map should open.
 *
 * The athlete's last known position, if the phone has one and the app has
 * permission; otherwise a plain world view.
 */
object MapStart {
    /** The camera position to open the map at. */
    fun cameraFor(context: Context): Pair<LatLng, Double> {
        val location = lastKnownLocation(context)
        return if (location != null) {
            LatLng(location.first, location.second) to 13.0
        } else {
            LatLng(20.0, 0.0) to 2.0
        }
    }

    /** The last position the phone's GPS or network provider knew about. */
    fun lastKnownLocation(context: Context): Pair<Double, Double>? {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)
        var best: Pair<Double, Double>? = null
        var bestTime = 0L
        for (provider in providers) {
            val location =
                runCatching { manager.getLastKnownLocation(provider) }.getOrNull() ?: continue
            if (location.time >= bestTime) {
                bestTime = location.time
                best = location.latitude to location.longitude
            }
        }
        return best
    }

    /** Moves the camera to a position. */
    fun moveTo(
        map: MapLibreMap,
        latitude: Double,
        longitude: Double,
        zoom: Double,
    ) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoom), 800)
    }
}
