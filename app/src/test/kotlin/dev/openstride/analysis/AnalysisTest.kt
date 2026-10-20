package dev.openstride.analysis

import dev.openstride.model.Activity
import dev.openstride.model.ActivityType
import dev.openstride.model.Visibility
import dev.openstride.recording.TrackPoint
import dev.openstride.recording.haversineMeters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

/**
 * The training analysis.
 *
 * These run on the host: the analysis is plain Kotlin with no Android types,
 * so it can be checked without a device.
 */
class AnalysisTest {
    private fun activity(
        daysAgo: Long,
        distance: Double,
        seconds: Int = 3600,
        type: ActivityType = ActivityType.RUN,
        gain: Double = 0.0,
    ) = Activity(
        id = daysAgo,
        athleteId = 1,
        name = "Run",
        type = type,
        startedAt = Instant.now().minusSeconds(daysAgo * 86400),
        elapsedSeconds = seconds,
        movingSeconds = seconds,
        distanceMeters = distance,
        elevationGainMeters = gain,
        averageSpeedMetersPerSecond = distance / seconds,
        visibility = Visibility.PUBLIC,
    )

    @Test
    fun `the load of a day is the sum of the activities on it`() {
        val activities = listOf(activity(1, 5000.0), activity(1, 3000.0, type = ActivityType.RIDE))
        val loads = Fitness.dailyLoads(activities)
        assertEquals(1, loads.size)
        assertEquals(8000.0, loads[0].distanceMeters, 1.0)
        assertEquals(2, loads[0].activityCount)
        assertTrue("the load should be positive", loads[0].load > 0)
    }

    @Test
    fun `fitness rises with training and form falls`() {
        val activities = (0L..20L).map { activity(it, 10000.0) }
        val series = Fitness.series(Fitness.dailyLoads(activities))
        assertTrue(series.size >= 20)

        val first = series.first()
        val last = series.last()
        assertTrue("fitness should have risen", last.fitness > first.fitness)
        assertTrue("fatigue should have risen", last.fatigue > first.fatigue)
    }

    @Test
    fun `form falls after a burst of training and recovers with rest`() {
        val busy = (0L..9L).map { activity(it, 10000.0) }
        val loads = Fitness.dailyLoads(busy)
        val last = loads.last().date
        val series = Fitness.series(loads, days = 60, endDate = last.plusDays(20))

        val before = series.first()
        val lastTraining = series.last { it.load > 0 }
        val afterBurst = series.first { it.date > lastTraining.date }
        val afterRest = series.last()

        assertTrue("form should fall after a burst of training", afterBurst.form < before.form)
        assertTrue("form should recover with rest", afterRest.form > afterBurst.form)
    }

    @Test
    fun `the weekly load groups the days into weeks`() {
        val activities = (0L..20L).map { activity(it, 5000.0) }
        val weeks = Fitness.weeks(Fitness.dailyLoads(activities))
        assertTrue("three weeks of training", weeks.size >= 3)
        assertTrue(weeks.all { it.load > 0 })
    }

    @Test
    fun `the median speed ignores the outliers`() {
        val activities =
            listOf(
                activity(1, 3600.0, 3600),
                activity(2, 3600.0, 3600),
                activity(3, 3600.0, 3600),
                activity(4, 100000.0, 3600),
            )
        assertEquals(1.0, Fitness.medianSpeed(activities), 0.01)
    }

    @Test
    fun `the zones cover the whole speed range`() {
        val zones = Zones.zonesFor(5.0)
        assertEquals(5, zones.size)
        assertEquals(0.0, zones.first().fromSpeed, 0.001)
        assertEquals(5.0, zones.last().toSpeed, 0.001)
        // Every speed between zero and the maximum falls in exactly one zone.
        var speed = 0.0
        while (speed <= 5.0) {
            assertEquals(
                "speed $speed should be in a zone",
                1,
                zones.count { it.contains(speed) },
            )
            speed += 0.1
        }
    }

    @Test
    fun `the records find the best of each kind`() {
        val activities =
            listOf(
                activity(1, 5000.0, 1500),
                activity(2, 12000.0, 3600),
                activity(3, 8000.0, 2400, gain = 120.0),
            )
        val records = Records.of(activities)
        val longest = records.first { it.title == "Longest activity" }
        assertEquals(12000.0, longest.sortValue, 1.0)

        val climb = records.first { it.title == "Biggest climb" }
        assertEquals(120.0, climb.sortValue, 1.0)
    }

    @Test
    fun `the splits divide an activity into kilometres`() {
        // A straight line: 100 metres a second for 10 seconds is 1 km.
        val distance = (0..10).map { it * 100.0 }
        val time = (0..10).map { it * 1000L }
        val splits = Splits.splits(distance, time)
        assertEquals(1, splits.size)
        assertEquals(1000.0, splits[0].distanceMeters, 1.0)
        assertEquals(10, splits[0].elapsedSeconds)
    }

    @Test
    fun `the achievements unlock as the history grows`() {
        val badges = Achievements.of((0L..11L).map { activity(it, 1000.0) })
        val first = badges.first { it.title == "First activity" }
        assertTrue(first.unlocked)

        val ten = badges.first { it.title == "Ten activities" }
        assertTrue(ten.unlocked)

        val hundred = badges.first { it.title == "Hundred activities" }
        assertTrue(!hundred.unlocked)
        assertTrue(hundred.progress > 0f)
    }

    @Test
    fun `two activities over the same road share a segment`() {
        val track =
            (0..40).map { index ->
                TrackPoint(48.8584 + index * 0.0001, 2.2945, 0.0, index * 1000L)
            }
        val overlap = Segments.overlap(track, track)
        assertNotNull("the same track should overlap with itself", overlap)
        assertTrue("the overlap should be long", overlap!!.geometry.distanceMeters > 300.0)
    }

    @Test
    fun `two activities far apart share nothing`() {
        val first = (0..40).map { TrackPoint(48.8584 + it * 0.0001, 2.2945, 0.0, it * 1000L) }
        val second = (0..40).map { TrackPoint(10.0 + it * 0.0001, 20.0, 0.0, it * 1000L) }
        assertEquals(null, Segments.overlap(first, second))
    }

    @Test
    fun `the heatmap counts visits`() {
        val track =
            listOf(
                TrackPoint(48.8584, 2.2945, 0.0, 0L),
                TrackPoint(48.8584, 2.2945, 0.0, 1000L),
                TrackPoint(48.9, 2.3, 0.0, 2000L),
            )
        val cells = Heatmap.cells(listOf(track, track))
        assertEquals(2, cells.size)
        assertEquals(4, cells[0].count)
        assertEquals(2, cells[1].count)
    }

    @Test
    fun `the distance between two points is right`() {
        // One degree of latitude is about 111 kilometres.
        val metres = haversineMeters(48.0, 2.0, 49.0, 2.0)
        assertEquals(111195.0, metres, 500.0)
    }
}
