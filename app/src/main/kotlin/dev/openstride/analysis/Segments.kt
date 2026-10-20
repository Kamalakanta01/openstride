package dev.openstride.analysis

import dev.openstride.recording.TrackPoint
import dev.openstride.recording.haversineMeters

/**
 * Local segments: the stretches of road the athlete keeps coming back to.
 *
 * The original app's segments are crowdsourced — Strava's users mark them
 * and everyone competes on them. There is no community here, so the segments
 * are found in the athlete's own history: a stretch of road that the
 * athlete has covered at least twice becomes a segment, and every later
 * activity over it is an effort.
 *
 * The geometry is matched by walking both tracks together and looking for
 * the longest run of samples that stay within [MATCH_TOLERANCE_METERS] of
 * each other. That is deliberately simple: it finds the common stretch of
 * two activities that were recorded the same way round, which is what a
 * segment is.
 */
object Segments {
    /** How far apart two samples may be and still count as the same place. */
    const val MATCH_TOLERANCE_METERS = 35.0

    /** The shortest common stretch that is worth calling a segment. */
    const val MINIMUM_LENGTH_METERS = 300.0

    /** A stretch of road, with the points that describe it. */
    data class Geometry(
        val points: List<TrackPoint>,
        val distanceMeters: Double,
    )

    /** The common stretch of two tracks. */
    data class Overlap(
        val geometry: Geometry,
        val firstDistanceMeters: Double,
        val secondDistanceMeters: Double,
    )

    /**
     * The longest stretch two tracks share.
     *
     * @return null when the tracks do not share enough to be a segment.
     */
    fun overlap(
        first: List<TrackPoint>,
        second: List<TrackPoint>,
    ): Overlap? {
        if (first.size < 2 || second.size < 2) return null

        var bestStart = 0
        var bestLength = 0.0
        var start = 0
        var index = 0
        var firstDistance = 0.0
        var secondDistance = 0.0
        var bestFirstDistance = 0.0
        var bestSecondDistance = 0.0

        while (index < first.size) {
            // Find the closest point in the second track to the current point
            // of the first.
            var closest = 0
            var closestDistance = Double.MAX_VALUE
            var candidate = (index - 64).coerceAtLeast(0)
            val limit = (index + 64).coerceAtMost(second.size - 1)
            while (candidate <= limit) {
                val gap =
                    haversineMeters(
                        first[index].latitude,
                        first[index].longitude,
                        second[candidate].latitude,
                        second[candidate].longitude,
                    )
                if (gap < closestDistance) {
                    closestDistance = gap
                    closest = candidate
                }
                candidate++
            }

            if (closestDistance <= MATCH_TOLERANCE_METERS) {
                if (index == start) {
                    firstDistance = 0.0
                    secondDistance = 0.0
                }
                if (index > 0) {
                    firstDistance +=
                        haversineMeters(
                            first[index - 1].latitude,
                            first[index - 1].longitude,
                            first[index].latitude,
                            first[index].longitude,
                        )
                    secondDistance +=
                        haversineMeters(
                            second[closest - 1].latitude,
                            second[closest - 1].longitude,
                            second[closest].latitude,
                            second[closest].longitude,
                        )
                }
                if (firstDistance > bestLength) {
                    bestLength = firstDistance
                    bestStart = start
                    bestFirstDistance = firstDistance
                    bestSecondDistance = secondDistance
                }
            } else {
                start = index + 1
            }
            index++
        }

        if (bestLength < MINIMUM_LENGTH_METERS) return null

        // The shared stretch ends where the match stopped; find how far it
        // runs in the second track from the start point.
        val geometry = slice(first, bestStart.toDouble(), bestStart + bestLength)
        return Overlap(geometry, bestFirstDistance, bestSecondDistance)
    }

    /**
     * The stretch of [track] that covers [segment].
     *
     * @return the distance along the segment that was covered, and the
     *   elapsed time over it, or null when the activity did not go there.
     */
    fun effort(
        track: List<TrackPoint>,
        segment: Geometry,
    ): Pair<Double, Int>? {
        if (track.size < 2 || segment.points.size < 2) return null

        var matched = 0
        var covered = 0.0
        var startedAt: Long? = null
        var lastTime: Long? = null
        var cursor = 0

        for (point in track) {
            // Advance the cursor to the closest point of the segment.
            var best = cursor
            var bestDistance = Double.MAX_VALUE
            var candidate = cursor
            while (candidate < segment.points.size) {
                val gap =
                    haversineMeters(
                        point.latitude,
                        point.longitude,
                        segment.points[candidate].latitude,
                        segment.points[candidate].longitude,
                    )
                if (gap < bestDistance) {
                    bestDistance = gap
                    best = candidate
                }
                candidate++
            }
            if (bestDistance > MATCH_TOLERANCE_METERS) continue

            if (startedAt == null) {
                startedAt = point.timeMillis
            } else {
                covered +=
                    haversineMeters(
                        track[track.indexOf(point) - 1].latitude,
                        track[track.indexOf(point) - 1].longitude,
                        point.latitude,
                        point.longitude,
                    )
            }
            lastTime = point.timeMillis
            matched++
            cursor = best
        }

        if (matched < 2 || startedAt == null || lastTime == null) return null
        return covered to ((lastTime - startedAt) / 1000).toInt()
    }

    /**
     * The part of a track between two distances along it.
     */
    private fun slice(
        track: List<TrackPoint>,
        fromDistance: Double,
        toDistance: Double,
    ): Geometry {
        val points = mutableListOf<TrackPoint>()
        var distance = 0.0
        for (index in 0 until track.size) {
            if (index > 0) {
                distance +=
                    haversineMeters(
                        track[index - 1].latitude,
                        track[index - 1].longitude,
                        track[index].latitude,
                        track[index].longitude,
                    )
            }
            if (distance >= fromDistance && distance <= toDistance) {
                points += track[index]
            }
        }
        if (points.size < 2) {
            return Geometry(track.take(2), 0.0)
        }
        var length = 0.0
        for (index in 1 until points.size) {
            length +=
                haversineMeters(
                    points[index - 1].latitude,
                    points[index - 1].longitude,
                    points[index].latitude,
                    points[index].longitude,
                )
        }
        return Geometry(points, length)
    }
}

/**
 * The athlete's personal heatmap.
 *
 * The original app draws a heatmap of everywhere the athlete has been. Here
 * it is built from the activities on the device: every point of every track
 * falls into a cell, and the cells are coloured by how often they were
 * visited.
 */
object Heatmap {
    /** One cell of the heatmap. */
    data class Cell(
        val latitude: Double,
        val longitude: Double,
        val count: Int,
    )

    /** The size of a heatmap cell, in metres. */
    const val CELL_METERS = 60.0

    /**
     * The heatmap of a set of tracks.
     *
     * @return the cells, busiest first.
     */
    fun cells(
        tracks: List<List<TrackPoint>>,
        cellMeters: Double = CELL_METERS,
    ): List<Cell> {
        val counts = linkedMapOf<String, Cell>()
        for (track in tracks) {
            for (point in track) {
                val key =
                    "${Math.floor(point.latitude * 100000 / cellMeters * 1000)}:" +
                        "${Math.floor(point.longitude * 100000 / cellMeters * 1000)}"
                val existing = counts[key]
                if (existing == null) {
                    counts[key] = Cell(point.latitude, point.longitude, 1)
                } else {
                    counts[key] = Cell(existing.latitude, existing.longitude, existing.count + 1)
                }
            }
        }
        return counts.values.sortedByDescending { it.count }
    }

    /** The opacity of a cell, from its visit count. */
    fun intensity(
        count: Int,
        busiest: Int,
    ): Float {
        if (busiest <= 0) return 0f
        // A square root curve: the difference between one and two visits
        // matters less than the difference between twenty and thirty.
        return Math.sqrt(count.toDouble() / busiest).toFloat().coerceIn(0.15f, 1f)
    }
}
