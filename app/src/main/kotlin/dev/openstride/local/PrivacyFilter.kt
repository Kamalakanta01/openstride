package dev.openstride.local

import dev.openstride.model.PrivacyZone
import dev.openstride.model.PrivacyZoneType
import dev.openstride.recording.TrackPoint
import dev.openstride.recording.haversineMeters

/**
 * The privacy rules, applied to a recorded track.
 *
 * A privacy zone is a circle the athlete has drawn around a place they do
 * not want on their map. The original app blanks the parts of an activity
 * that fall inside one, and has separate rules for hiding the start and the
 * end of a run.
 *
 * This is the same promise, made locally. The raw track stays in the
 * database - it is the athlete's own data and they may want it themselves -
 * but the parts covered by a zone are never shown, and never exported.
 *
 * Pure Kotlin, so it can be tested on the host.
 */
object PrivacyFilter {
    /** How close to a zone's edge a point has to be to count as inside it. */
    const val EDGE_MARGIN_METERS = 5.0

    /**
     * The part of a track that may be shown.
     *
     * @return the track with the private parts removed, or an empty list if
     *   the whole activity is hidden.
     */
    fun visibleTrack(
        track: List<TrackPoint>,
        zones: List<PrivacyZone>,
    ): List<TrackPoint> {
        if (track.isEmpty()) return track
        if (zones.any { it.type == PrivacyZoneType.ENTIRE_MAP }) return emptyList()

        var visible = track

        for (zone in zones) {
            when (zone.type) {
                PrivacyZoneType.ENTIRE_MAP -> return emptyList()
                PrivacyZoneType.PRIVATE -> visible = cutOut(visible, zone)
                PrivacyZoneType.HIDDEN_START -> visible = hideStart(visible, zone)
                PrivacyZoneType.HIDDEN_END -> visible = hideEnd(visible, zone)
            }
        }
        return visible
    }

    /**
     * The start and end of an activity, as they may be shown.
     *
     * @return null when the activity is hidden from maps entirely.
     */
    fun visibleEndpoints(
        track: List<TrackPoint>,
        zones: List<PrivacyZone>,
    ): Pair<TrackPoint, TrackPoint>? {
        if (track.isEmpty()) return null
        if (zones.any { it.type == PrivacyZoneType.ENTIRE_MAP }) return null

        val visible = visibleTrack(track, zones)
        if (visible.isEmpty()) return null
        return visible.first() to visible.last()
    }

    /** Whether a point falls inside a zone. */
    fun isInside(
        point: TrackPoint,
        zone: PrivacyZone,
    ): Boolean =
        haversineMeters(point.latitude, point.longitude, zone.centerLatitude, zone.centerLongitude) <=
            zone.radiusMeters + EDGE_MARGIN_METERS

    /**
     * Removes the part of the track that is inside a zone.
     *
     * A point just outside the zone is kept, so the track does not end in
     * mid-air; but the points inside are gone.
     */
    private fun cutOut(
        track: List<TrackPoint>,
        zone: PrivacyZone,
    ): List<TrackPoint> {
        if (track.none { isInside(it, zone) }) return track
        // The points outside the zone are kept, so the track still joins up
        // across it.
        return track.filterNot { isInside(it, zone) }
    }

    /**
     * Cuts the track at the point where it leaves the zone.
     *
     * The hidden start is the part of the activity that happened inside or
     * before the zone, so the run no longer shows where it began.
     */
    private fun hideStart(
        track: List<TrackPoint>,
        zone: PrivacyZone,
    ): List<TrackPoint> {
        val index = track.indexOfFirst { !isInside(it, zone) }
        if (index < 0) return emptyList()
        // Keep the point where the athlete left the zone, so the track still
        // starts somewhere sensible.
        return track.subList(index.coerceAtMost(track.size - 1), track.size)
    }

    /** Cuts the track at the point where it enters the zone. */
    private fun hideEnd(
        track: List<TrackPoint>,
        zone: PrivacyZone,
    ): List<TrackPoint> {
        val index = track.indexOfFirst { isInside(it, zone) }
        if (index < 0) return track
        return track.subList(0, index.coerceAtLeast(1))
    }
}

/** Converts a stored zone into the model the filter works on. */
fun PrivacyZoneEntity.toModel() =
    PrivacyZone(
        id = id,
        name = name,
        centerLatitude = centerLatitude,
        centerLongitude = centerLongitude,
        radiusMeters = radiusMeters,
        type =
            PrivacyZoneType.entries.firstOrNull { it.name == type } ?: PrivacyZoneType.PRIVATE,
    )

/**
 * How far the start of an activity is from its first visible point.
 *
 * The original app shows this as "the start of this activity is hidden".
 */
fun startHiddenBy(
    track: List<TrackPoint>,
    zones: List<PrivacyZone>,
): Boolean {
    if (track.isEmpty()) return false
    val visible = PrivacyFilter.visibleTrack(track, zones)
    if (visible.isEmpty()) return true
    return visible.first() !== track.first()
}
