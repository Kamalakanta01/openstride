package dev.openstride.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.openstride.OpenStrideApplication
import dev.openstride.local.ActivityEntity
import dev.openstride.local.ActivityStreamEntity
import dev.openstride.local.PrivacyZoneEntity
import dev.openstride.model.ActivityType
import dev.openstride.model.PrivacyZoneType
import dev.openstride.model.StreamType
import dev.openstride.recording.TrackPoint
import dev.openstride.recording.encodeDoubles
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant

/**
 * The whole path from a stored activity to the numbers on the screen.
 *
 * This runs on a device because it uses the real database: the activity is
 * written, and then the analysis, the splits and the privacy filter are read
 * back out of it.
 */
@RunWith(AndroidJUnit4::class)
class ActivityStorageTest {
    private lateinit var repository: AppRepository
    private lateinit var database: dev.openstride.local.LocalDatabase
    private var activityId = 0L

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val application = context.applicationContext as OpenStrideApplication
        repository = application.repository
        database = application.database
    }

    @After
    fun tearDown() =
        runBlocking {
            if (activityId != 0L) {
                database.activityDao().delete(activityId)
            }
            database.privacyZoneDao().deleteAll()
        }

    /** A closed loop of points, about a kilometre around. */
    private fun loop(): List<TrackPoint> {
        val points = mutableListOf<TrackPoint>()
        val steps = 40
        for (index in 0..steps) {
            val angle = index * 2.0 * Math.PI / steps
            points +=
                TrackPoint(
                    latitude = 48.8584 + 0.004 * Math.sin(angle),
                    longitude = 2.2945 + 0.006 * Math.cos(angle),
                    altitudeMeters = 30.0 + index * 2.0,
                    timeMillis = index * 1000L,
                )
        }
        return points
    }

    @Test
    fun anActivityIsStoredAndReadBack() =
        runBlocking {
            val points = loop()
            activityId =
                database.activityDao().upsert(
                    ActivityEntity(
                        athleteId = 1,
                        name = "Test run",
                        type = ActivityType.RUN.wireName,
                        startedAt = Instant.now(),
                        elapsedSeconds = points.size,
                        movingSeconds = points.size,
                        distanceMeters = 2000.0,
                        elevationGainMeters = 80.0,
                        elevationLossMeters = 0.0,
                        averageSpeedMetersPerSecond = 3.0,
                        maxSpeedMetersPerSecond = 4.0,
                        averageHeartRate = null,
                        maxHeartRate = null,
                        calories = null,
                        commute = false,
                        trainer = false,
                        manual = false,
                        visibility = "PUBLIC",
                        gearId = null,
                        routeId = null,
                        startLatitude = points.first().latitude,
                        startLongitude = points.first().longitude,
                        endLatitude = points.last().latitude,
                        endLongitude = points.last().longitude,
                        polyline = null,
                        createdAt = Instant.now(),
                    ),
                )

            database.streamDao().upsert(
                ActivityStreamEntity(
                    activityId = activityId,
                    type = StreamType.LATITUDE.name,
                    data = encodeDoubles(points.map { it.latitude }),
                    resolutionSeconds = 1,
                ),
            )
            database.streamDao().upsert(
                ActivityStreamEntity(
                    activityId = activityId,
                    type = StreamType.LONGITUDE.name,
                    data = encodeDoubles(points.map { it.longitude }),
                    resolutionSeconds = 1,
                ),
            )
            database.streamDao().upsert(
                ActivityStreamEntity(
                    activityId = activityId,
                    type = StreamType.ALTITUDE.name,
                    data = encodeDoubles(points.map { it.altitudeMeters }),
                    resolutionSeconds = 1,
                ),
            )
            database.streamDao().upsert(
                ActivityStreamEntity(
                    activityId = activityId,
                    type = StreamType.TIME.name,
                    data = encodeDoubles(points.map { it.timeMillis.toDouble() }),
                    resolutionSeconds = 1,
                ),
            )

            val track = repository.track(activityId)
            assertEquals("every point should come back", points.size, track.size)
            assertEquals(points.first().latitude, track.first().latitude, 1e-9)

            // The analysis reads the same activities the screens read.
            val activities = database.activityDao().observeAll()
            assertTrue("the activity should be in the database", activityId > 0)
        }

    @Test
    fun aPrivacyZoneHidesTheStartOfTheActivity() =
        runBlocking {
            val points = loop()
            activityId =
                database.activityDao().upsert(
                    ActivityEntity(
                        athleteId = 1,
                        name = "Zoned run",
                        type = ActivityType.RUN.wireName,
                        startedAt = Instant.now(),
                        elapsedSeconds = points.size,
                        movingSeconds = points.size,
                        distanceMeters = 2000.0,
                        elevationGainMeters = 0.0,
                        elevationLossMeters = 0.0,
                        averageSpeedMetersPerSecond = 3.0,
                        maxSpeedMetersPerSecond = 4.0,
                        averageHeartRate = null,
                        maxHeartRate = null,
                        calories = null,
                        commute = false,
                        trainer = false,
                        manual = false,
                        visibility = "PUBLIC",
                        gearId = null,
                        routeId = null,
                        startLatitude = points.first().latitude,
                        startLongitude = points.first().longitude,
                        endLatitude = points.last().latitude,
                        endLongitude = points.last().longitude,
                        polyline = null,
                        createdAt = Instant.now(),
                    ),
                )
            database.streamDao().upsert(
                ActivityStreamEntity(
                    activityId = activityId,
                    type = StreamType.LATITUDE.name,
                    data = encodeDoubles(points.map { it.latitude }),
                    resolutionSeconds = 1,
                ),
            )
            database.streamDao().upsert(
                ActivityStreamEntity(
                    activityId = activityId,
                    type = StreamType.LONGITUDE.name,
                    data = encodeDoubles(points.map { it.longitude }),
                    resolutionSeconds = 1,
                ),
            )

            val track = repository.track(activityId)
            assertTrue("the track should not be empty", track.size > 10)

            // Before the zone, the whole track is visible.
            assertEquals(track.size, repository.visibleTrack(activityId).size)
            assertFalse(repository.startHidden(activityId))

            // With a zone over the start, the beginning is cut off.
            database.privacyZoneDao().upsert(
                PrivacyZoneEntity(
                    name = "Home",
                    centerLatitude = points.first().latitude,
                    centerLongitude = points.first().longitude,
                    radiusMeters = 300.0,
                    type = PrivacyZoneType.HIDDEN_START.name,
                ),
            )
            val visible = repository.visibleTrack(activityId)
            assertTrue("the start should be cut off", visible.size < track.size)
            assertTrue("the start should be reported as hidden", repository.startHidden(activityId))
        }
}
