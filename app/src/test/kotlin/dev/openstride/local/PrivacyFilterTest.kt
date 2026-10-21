package dev.openstride.local

import dev.openstride.model.PrivacyZone
import dev.openstride.model.PrivacyZoneType
import dev.openstride.recording.TrackPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The privacy rules.
 *
 * A zone that is stored but not applied is worse than no zone at all: the
 * athlete believes a place is hidden and it is not.
 */
class PrivacyFilterTest {
    /** A track of ten points a hundred metres apart, heading east. */
    private fun track(): List<TrackPoint> =
        (0..9).map { index ->
            TrackPoint(
                latitude = 48.8584,
                longitude = 2.2945 + index * 0.001,
                altitudeMeters = 0.0,
                timeMillis = index * 1000L,
            )
        }

    /**
     * A zone over one point of the track.
     *
     * A hidden start has to cover where the activity began, and a hidden end
     * where it finished, so the centre is a parameter.
     */
    private fun zone(
        type: PrivacyZoneType,
        centreIndex: Int = 4,
        radius: Double = 200.0,
    ) = PrivacyZone(
        id = 1,
        name = "Home",
        centerLatitude = 48.8584,
        centerLongitude = 2.2945 + centreIndex * 0.001,
        radiusMeters = radius,
        type = type,
    )

    @Test
    fun `no zones leaves the track alone`() {
        val original = track()
        assertEquals(original, PrivacyFilter.visibleTrack(original, emptyList()))
    }

    @Test
    fun `a private zone cuts the points inside it out`() {
        val original = track()
        val visible = PrivacyFilter.visibleTrack(original, listOf(zone(PrivacyZoneType.PRIVATE)))
        assertTrue("the track should be shorter", visible.size < original.size)
        assertTrue(
            "no point inside the zone should survive",
            visible.none { PrivacyFilter.isInside(it, zone(PrivacyZoneType.PRIVATE)) },
        )
        assertTrue("the ends should still be there", visible.first() === original.first())
    }

    @Test
    fun `a hidden start cuts the beginning off`() {
        val original = track()
        val home = zone(PrivacyZoneType.HIDDEN_START, centreIndex = 0)
        val visible = PrivacyFilter.visibleTrack(original, listOf(home))
        assertTrue("the start should be gone", visible.size < original.size)
        assertTrue(
            "the first visible point should be outside the zone",
            visible.isNotEmpty() && !PrivacyFilter.isInside(visible.first(), home),
        )
    }

    @Test
    fun `a hidden end cuts the end off`() {
        val original = track()
        val home = zone(PrivacyZoneType.HIDDEN_END, centreIndex = 9)
        val visible = PrivacyFilter.visibleTrack(original, listOf(home))
        assertTrue("the end should be gone", visible.size < original.size)
        assertTrue(
            "the last visible point should be outside the zone",
            visible.isNotEmpty() && !PrivacyFilter.isInside(visible.last(), home),
        )
    }

    @Test
    fun `the whole map hides the activity entirely`() {
        val original = track()
        assertTrue(
            PrivacyFilter.visibleTrack(original, listOf(zone(PrivacyZoneType.ENTIRE_MAP))).isEmpty(),
        )
        assertNull(
            PrivacyFilter.visibleEndpoints(original, listOf(zone(PrivacyZoneType.ENTIRE_MAP))),
        )
    }

    @Test
    fun `the endpoints follow the zones`() {
        val original = track()
        val endpoints = PrivacyFilter.visibleEndpoints(original, listOf(zone(PrivacyZoneType.PRIVATE)))
        assertNotNull(endpoints)
        assertEquals(original.first(), endpoints!!.first)
        assertEquals(original.last(), endpoints.second)
    }

    @Test
    fun `the start is reported as hidden when it is cut off`() {
        val original = track()
        assertFalse(startHiddenBy(original, emptyList()))
        assertTrue(startHiddenBy(original, listOf(zone(PrivacyZoneType.HIDDEN_START, centreIndex = 0))))
    }

    @Test
    fun `a zone far away changes nothing`() {
        val original = track()
        val far =
            PrivacyZone(
                id = 2,
                name = "Elsewhere",
                centerLatitude = 10.0,
                centerLongitude = 20.0,
                radiusMeters = 100.0,
                type = PrivacyZoneType.PRIVATE,
            )
        assertEquals(original, PrivacyFilter.visibleTrack(original, listOf(far)))
    }
}
