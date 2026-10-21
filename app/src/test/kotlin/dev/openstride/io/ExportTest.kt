package dev.openstride.io

import dev.openstride.local.ActivityEntity
import dev.openstride.recording.TrackPoint
import dev.openstride.recording.decodePolyline
import dev.openstride.recording.encodePolyline
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

/**
 * Writing an activity out, and the polyline format.
 *
 * The polyline format is the one the original app uses, so a track written
 * here reads the same there.
 */
class ExportTest {
    private fun activity() =
        ActivityEntity(
            id = 1,
            athleteId = 1,
            name = "Morning <run> & ride",
            type = "Run",
            startedAt = Instant.parse("2026-10-21T06:30:00Z"),
            elapsedSeconds = 1800,
            movingSeconds = 1700,
            distanceMeters = 5000.0,
            elevationGainMeters = 120.0,
            elevationLossMeters = 110.0,
            averageSpeedMetersPerSecond = 2.9,
            maxSpeedMetersPerSecond = 4.2,
            averageHeartRate = 150,
            maxHeartRate = 175,
            calories = 400,
            commute = false,
            trainer = false,
            manual = false,
            visibility = "PUBLIC",
            gearId = null,
            routeId = null,
            startLatitude = 48.8584,
            startLongitude = 2.2945,
            endLatitude = 48.86,
            endLongitude = 2.30,
            polyline = null,
            createdAt = Instant.parse("2026-10-21T06:30:00Z"),
        )

    private fun track() =
        listOf(
            TrackPoint(48.8584, 2.2945, 35.0, 0L),
            TrackPoint(48.8590, 2.2950, 37.0, 1000L),
            TrackPoint(48.8600, 2.2960, 40.0, 2000L),
        )

    @Test
    fun `the polyline survives a round trip`() {
        val points = track()
        val encoded = encodePolyline(points)
        val decoded = decodePolyline(encoded)
        assertEquals(points.size, decoded.size)
        points.forEachIndexed { index, point ->
            assertEquals(point.latitude, decoded[index].first, 1e-5)
            assertEquals(point.longitude, decoded[index].second, 1e-5)
        }
    }

    @Test
    fun `the gpx is well formed and holds the track`() {
        val gpx = Export.gpx(activity(), track())
        assertTrue("it should be xml", gpx.startsWith("<?xml"))
        assertTrue("it should declare the version", gpx.contains("version=\"1.1\""))
        assertTrue("it should have the track points", gpx.contains("<trkpt lat=\"48.8584\""))
        assertTrue("it should have the elevation", gpx.contains("<ele>35.0</ele>"))
        assertTrue("the name should be escaped", gpx.contains("Morning &lt;run&gt; &amp; ride"))
    }

    @Test
    fun `the json holds the numbers`() {
        val json = Export.json(activity(), track())
        assertTrue(json.contains("\"distanceMeters\": 5000.0"))
        assertTrue(json.contains("\"elevationGainMeters\": 120.0"))
        assertTrue(json.contains("\"lat\": 48.8584"))
        assertTrue("the name should be escaped", json.contains("Morning <run> & ride"))
    }

    @Test
    fun `the file name is safe`() {
        val name = Export.fileName(activity(), Export.Format.GPX)
        assertTrue("it should end with the extension", name.endsWith(".gpx"))
        assertTrue("it should have no spaces", !name.contains(" "))
        assertTrue(name.startsWith("2026-10-21"))
    }
}
