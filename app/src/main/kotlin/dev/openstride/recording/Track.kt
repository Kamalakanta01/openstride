/**
 * The geometry of a recorded track.
 *
 * Kept free of Android types so it can be unit tested on the host.
 */

package dev.openstride.recording

import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

/** One point of a recorded track. */
data class TrackPoint(
    val latitude: Double,
    val longitude: Double,
    val altitudeMeters: Double,
    val timeMillis: Long,
    val speedMetersPerSecond: Float = 0f,
    val heartRate: Int? = null,
    val accuracyMeters: Float = 0f,
)

/** The distance between two coordinates, in metres. */
fun haversineMeters(
    latitudeA: Double,
    longitudeA: Double,
    latitudeB: Double,
    longitudeB: Double,
): Double {
    val earthRadius = 6371008.8
    val dLat = Math.toRadians(latitudeB - latitudeA)
    val dLon = Math.toRadians(longitudeB - longitudeA)
    val a =
        sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(latitudeA)) * cos(Math.toRadians(latitudeB)) *
            sin(dLon / 2) * sin(dLon / 2)
    return 2 * earthRadius * atan2(sqrt(a), sqrt(1 - a))
}

/**
 * Encodes a track the way the original app does: Google's polyline format.
 *
 * The format stores each coordinate as a signed offset from the previous
 * one, in units of 1e-5 degrees for latitude and 1e-5 for longitude, then
 * writes those offsets as a base-64 style varint.
 */
fun encodePolyline(points: List<TrackPoint>): String {
    val out = StringBuilder()
    var lastLat = 0L
    var lastLon = 0L
    for (point in points) {
        val lat = Math.round(point.latitude * 1e5)
        val lon = Math.round(point.longitude * 1e5)
        encodeSigned(out, lat - lastLat)
        encodeSigned(out, lon - lastLon)
        lastLat = lat
        lastLon = lon
    }
    return out.toString()
}

private fun encodeSigned(
    out: StringBuilder,
    value: Long,
) {
    var v = if (value < 0) (value shl 1).inv() else value shl 1
    while (v >= 0x20) {
        out.append(((0x20 or (v.toInt() and 0x1f)) + 63).toChar())
        v = v shr 5
    }
    out.append((v.toInt() + 63).toChar())
}

/** Decodes a polyline back into coordinates. */
fun decodePolyline(encoded: String): List<Pair<Double, Double>> {
    val points = mutableListOf<Pair<Double, Double>>()
    var index = 0
    var lat = 0
    var lon = 0
    while (index < encoded.length) {
        var result = 0
        var shift = 0
        var byte: Int
        do {
            if (index >= encoded.length) return points
            byte = encoded[index++].code - 63
            result = result or ((byte and 0x1f) shl shift)
            shift += 5
        } while (byte >= 0x20)
        lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1

        result = 0
        shift = 0
        do {
            if (index >= encoded.length) return points
            byte = encoded[index++].code - 63
            result = result or ((byte and 0x1f) shl shift)
            shift += 5
        } while (byte >= 0x20)
        lon += if (result and 1 != 0) (result shr 1).inv() else result shr 1

        points += (lat / 1e5) to (lon / 1e5)
    }
    return points
}

/** The totals of a track, computed the way a sports app would. */
data class TrackSummary(
    val distanceMeters: Double = 0.0,
    val elevationGainMeters: Double = 0.0,
    val elevationLossMeters: Double = 0.0,
    val elapsedSeconds: Int = 0,
    val movingSeconds: Int = 0,
    val maxSpeedMetersPerSecond: Double = 0.0,
    val averageHeartRate: Int? = null,
    val maxHeartRate: Int? = null,
) {
    val averageSpeedMetersPerSecond: Double
        get() = if (movingSeconds > 0) distanceMeters / movingSeconds else 0.0
}

/**
 * Summarises a track.
 *
 * A point only counts towards the distance when it is a plausible step from
 * the last one: GPS noise while standing still would otherwise accumulate
 * into a plausible-looking distance.
 */
fun summarise(points: List<TrackPoint>): TrackSummary {
    if (points.isEmpty()) return TrackSummary()

    var distance = 0.0
    var gain = 0.0
    var loss = 0.0
    var moving = 0
    var maxSpeed = 0.0
    var heartSum = 0
    var heartCount = 0
    var maxHeart = 0

    for (index in 1 until points.size) {
        val previous = points[index - 1]
        val current = points[index]
        val step = haversineMeters(previous.latitude, previous.longitude, current.latitude, current.longitude)
        val seconds = (current.timeMillis - previous.timeMillis) / 1000.0
        // A jump of more than a few tens of metres in a second is a GPS
        // glitch, not movement.
        if (step <= MAX_PLAUSIBLE_STEP_METERS && seconds in 0.0..MAX_PLAUSIBLE_GAP_SECONDS) {
            distance += step
            if (step / max(seconds, 1.0) > 0.5) {
                moving++
            }
        }
        maxSpeed = maxOf(maxSpeed, current.speedMetersPerSecond.toDouble())

        val climb = current.altitudeMeters - previous.altitudeMeters
        if (climb > 0) {
            gain += climb
        } else {
            loss -= climb
        }

        current.heartRate?.let {
            heartSum += it
            heartCount++
            maxHeart = maxOf(maxHeart, it)
        }
    }

    val elapsed = ((points.last().timeMillis - points.first().timeMillis) / 1000.0).toInt()
    return TrackSummary(
        distanceMeters = distance,
        elevationGainMeters = gain,
        elevationLossMeters = loss,
        elapsedSeconds = elapsed,
        movingSeconds = moving,
        maxSpeedMetersPerSecond = maxSpeed,
        averageHeartRate = if (heartCount > 0) heartSum / heartCount else null,
        maxHeartRate = if (heartCount > 0) maxHeart else null,
    )
}

/** Formats a distance for the UI. */
fun formatDistance(meters: Double): String =
    if (meters < 1000) {
        String.format(Locale.US, "%.0f m", meters)
    } else {
        String.format(Locale.US, "%.2f km", meters / 1000)
    }

/** Formats a duration as h:mm:ss or m:ss. */
fun formatDuration(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hours > 0) {
        String.format(Locale.US, "%d:%02d:%02d", hours, minutes, secs)
    } else {
        String.format(Locale.US, "%d:%02d", minutes, secs)
    }
}

/** Formats a speed in the unit the athlete chose. */
fun formatSpeed(
    metersPerSecond: Double,
    metric: Boolean,
): String =
    if (metric) {
        String.format(Locale.US, "%.1f km/h", metersPerSecond * 3.6)
    } else {
        String.format(Locale.US, "%.1f mph", metersPerSecond * 2.236936)
    }

/** Formats a pace as min/km or min/mile. */
fun formatPace(
    metersPerSecond: Double,
    metric: Boolean,
): String {
    if (metersPerSecond <= 0.01) return "--:--"
    val metersPerUnit = if (metric) 1000.0 else 1609.344
    val secondsPerUnit = metersPerUnit / metersPerSecond
    val minutes = (secondsPerUnit / 60).toInt()
    val seconds = (secondsPerUnit % 60).toInt()
    return String.format(Locale.US, "%d:%02d /%s", minutes, seconds, if (metric) "km" else "mi")
}

private const val MAX_PLAUSIBLE_STEP_METERS = 200.0
private const val MAX_PLAUSIBLE_GAP_SECONDS = 30.0
