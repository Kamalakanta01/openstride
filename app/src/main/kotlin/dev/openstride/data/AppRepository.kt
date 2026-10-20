package dev.openstride.data

import dev.openstride.analysis.Achievements
import dev.openstride.analysis.Fitness
import dev.openstride.analysis.Heatmap
import dev.openstride.analysis.Records
import dev.openstride.analysis.Segments
import dev.openstride.local.ActivityEntity
import dev.openstride.local.ActivityStreamEntity
import dev.openstride.local.GearEntity
import dev.openstride.local.GoalEntity
import dev.openstride.local.LocalDatabase
import dev.openstride.local.PrivacyZoneEntity
import dev.openstride.local.RouteEntity
import dev.openstride.local.RoutePointEntity
import dev.openstride.local.SegmentEffortEntity
import dev.openstride.local.SegmentEntity
import dev.openstride.local.SegmentPointEntity
import dev.openstride.model.ActivityType
import dev.openstride.model.StreamType
import dev.openstride.recording.TrackPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDate

/**
 * Everything the user interface needs, in one place.
 *
 * The original app is a client of Strava's servers: every screen asks the
 * network for a page of results. Here the database is the server, and this
 * class is the API the screens talk to.
 */
class AppRepository(
    private val database: LocalDatabase,
) {
    // ---- activities ----------------------------------------------------

    fun observeActivities(): Flow<List<ActivityEntity>> = database.activityDao().observeAll()

    fun observeActivities(type: ActivityType): Flow<List<ActivityEntity>> = database.activityDao().observeByType(type.wireName)

    suspend fun activity(id: Long): ActivityEntity? = database.activityDao().byId(id)

    suspend fun deleteActivity(id: Long) = database.activityDao().delete(id)

    suspend fun saveActivity(activity: ActivityEntity): Long = database.activityDao().upsert(activity)

    /**
     * The track of an activity, rebuilt from its streams.
     *
     * The original app keeps the track in a compressed format and decodes it
     * on demand; here the streams are already separate arrays, so the track
     * is a zip of four of them.
     */
    suspend fun track(activityId: Long): List<TrackPoint> {
        val streams = database.streamDao().forActivity(activityId)
        val latitudes = stream(streams, StreamType.LATITUDE)
        val longitudes = stream(streams, StreamType.LONGITUDE)
        val altitudes = stream(streams, StreamType.ALTITUDE)
        val times = stream(streams, StreamType.TIME)
        if (latitudes.isEmpty() || longitudes.isEmpty()) return emptyList()

        return latitudes.indices.map { index ->
            TrackPoint(
                latitude = latitudes[index],
                longitude = longitudes.getOrElse(index) { 0.0 },
                altitudeMeters = altitudes.getOrElse(index) { 0.0 },
                timeMillis = times.getOrElse(index) { 0.0 }.toLong(),
            )
        }
    }

    private fun stream(
        streams: List<ActivityStreamEntity>,
        type: StreamType,
    ): List<Double> {
        val data = streams.firstOrNull { it.type == type.name }?.data ?: return emptyList()
        val buffer = java.nio.ByteBuffer.wrap(data)
        val values = mutableListOf<Double>()
        while (buffer.remaining() >= 8) {
            values += buffer.getDouble()
        }
        return values
    }

    // ---- training load -------------------------------------------------

    /** The training load of each day. */
    suspend fun dailyLoads(activities: List<ActivityEntity>): List<Fitness.DailyLoad> =
        withContext(Dispatchers.Default) {
            Fitness.dailyLoads(activities.map { it.toModel() })
        }

    /** The fitness and freshness chart. */
    suspend fun fitnessSeries(
        activities: List<ActivityEntity>,
        days: Int = 90,
    ): List<Fitness.Point> =
        withContext(Dispatchers.Default) {
            Fitness.series(dailyLoads(activities), days)
        }

    /** The load of each week. */
    suspend fun weeklyLoad(activities: List<ActivityEntity>): List<Fitness.Week> =
        withContext(Dispatchers.Default) {
            Fitness.weeks(dailyLoads(activities))
        }

    /** The athlete's personal bests. */
    suspend fun records(activities: List<ActivityEntity>): List<Records.Record> =
        withContext(Dispatchers.Default) {
            Records.of(activities.map { it.toModel() })
        }

    /** The badges the athlete has earned. */
    suspend fun achievements(activities: List<ActivityEntity>): List<Achievements.Badge> =
        withContext(Dispatchers.Default) {
            Achievements.of(activities.map { it.toModel() })
        }

    /** The athlete's personal heatmap. */
    suspend fun heatmap(activityIds: List<Long>): List<Heatmap.Cell> =
        withContext(Dispatchers.Default) {
            val tracks = activityIds.map { track(it) }
            Heatmap.cells(tracks)
        }

    // ---- local segments -------------------------------------------------

    /** The segments the athlete has found in their own history. */
    fun observeSegments(): Flow<List<SegmentEntity>> = database.segmentDao().observeAll()

    /** The points of a segment. */
    suspend fun segmentPoints(segmentId: Long): List<TrackPoint> =
        database.segmentPointDao().forSegment(segmentId).map {
            TrackPoint(it.latitude, it.longitude, it.elevationMeters, 0L)
        }

    /** The efforts on a segment. */
    suspend fun segmentEfforts(segmentId: Long): List<SegmentEffortEntity> = database.segmentDao().efforts(segmentId)

    /**
     * Looks for new segments in the athlete's history and records efforts.
     *
     * This is the local stand-in for Strava's crowdsourced segments: a
     * stretch of road covered twice becomes a segment, and every activity
     * over it afterwards is an effort.
     */
    suspend fun discoverSegments(activities: List<ActivityEntity>): Int {
        var found = 0
        val known = database.segmentDao().all().toMutableList()

        for (index in activities.indices) {
            for (other in index + 1 until activities.size) {
                val first = activities[index]
                val second = activities[other]
                if (first.type != second.type) continue

                val firstTrack = track(first.id)
                val secondTrack = track(second.id)
                if (firstTrack.size < 2 || secondTrack.size < 2) continue

                val overlap = Segments.overlap(firstTrack, secondTrack) ?: continue
                val alreadyKnown =
                    known.any { segment ->
                        val points = segmentPoints(segment.id)
                        points.size >= 2 &&
                            Segments.overlap(
                                points,
                                overlap.geometry.points,
                            ) != null
                    }
                if (alreadyKnown) continue

                val id =
                    database.segmentDao().upsert(
                        SegmentEntity(
                            name = "Local segment ${known.size + 1}",
                            activityType = first.type,
                            distanceMeters = overlap.geometry.distanceMeters,
                            elevationGainMeters = 0.0,
                            climbCategory = null,
                            localLegend = null,
                        ),
                    )
                database.segmentPointDao().upsert(
                    overlap.geometry.points.mapIndexed { position, point ->
                        SegmentPointEntity(
                            segmentId = id,
                            index = position,
                            latitude = point.latitude,
                            longitude = point.longitude,
                            distanceMeters = 0.0,
                            elevationMeters = point.altitudeMeters,
                        )
                    },
                )
                known +=
                    SegmentEntity(
                        id = id,
                        name = "Local segment ${known.size + 1}",
                        activityType = first.type,
                        distanceMeters = overlap.geometry.distanceMeters,
                        elevationGainMeters = 0.0,
                        climbCategory = null,
                        localLegend = null,
                    )
                found++
            }
        }

        // Record an effort for every activity that covers a known segment.
        for (activity in activities) {
            val activityTrack = track(activity.id)
            if (activityTrack.size < 2) continue
            for (segment in known) {
                val points = segmentPoints(segment.id)
                if (points.size < 2) continue
                val effort =
                    Segments.effort(activityTrack, Segments.Geometry(points, segment.distanceMeters))
                        ?: continue
                val (distance, seconds) = effort
                val already = segmentEfforts(segment.id).any { it.activityId == activity.id }
                if (already) continue
                database.segmentDao().upsertEffort(
                    SegmentEffortEntity(
                        segmentId = segment.id,
                        activityId = activity.id,
                        athleteId = 1,
                        elapsedSeconds = seconds,
                        distanceMeters = distance,
                        averageHeartRate = null,
                        maxHeartRate = null,
                        averageWatts = null,
                        isKom = false,
                    ),
                )
            }
        }
        return found
    }

    // ---- gear, goals, routes, privacy ----------------------------------

    fun observeGear(): Flow<List<GearEntity>> = database.gearDao().observeActive()

    suspend fun saveGear(gear: GearEntity): Long = database.gearDao().upsert(gear)

    fun observeGoals(): Flow<List<GoalEntity>> = database.goalDao().observeCurrent(LocalDate.now())

    suspend fun saveGoal(goal: GoalEntity): Long = database.goalDao().upsert(goal)

    fun observeRoutes(): Flow<List<RouteEntity>> = database.routeDao().observeAll()

    suspend fun saveRoute(
        route: RouteEntity,
        points: List<RoutePointEntity>,
    ): Long {
        val id = database.routeDao().upsert(route)
        database.routeDao().upsertPoints(points.map { it.copy(routeId = id) })
        return id
    }

    suspend fun routePoints(routeId: Long): List<RoutePointEntity> = database.routeDao().points(routeId)

    fun observePrivacyZones(): Flow<List<PrivacyZoneEntity>> = database.privacyZoneDao().observeAll()

    suspend fun savePrivacyZone(zone: PrivacyZoneEntity): Long = database.privacyZoneDao().upsert(zone)

    suspend fun deletePrivacyZone(id: Long) = database.privacyZoneDao().delete(id)
}

/** Converts a stored activity into the model the analysis code works on. */
fun ActivityEntity.toModel() =
    dev.openstride.model.Activity(
        id = id,
        athleteId = athleteId,
        name = name,
        type = ActivityType.entries.firstOrNull { it.wireName == type } ?: ActivityType.OTHER,
        startedAt = startedAt,
        elapsedSeconds = elapsedSeconds,
        movingSeconds = movingSeconds,
        distanceMeters = distanceMeters,
        elevationGainMeters = elevationGainMeters,
        elevationLossMeters = elevationLossMeters,
        averageSpeedMetersPerSecond = averageSpeedMetersPerSecond,
        maxSpeedMetersPerSecond = maxSpeedMetersPerSecond,
        averageHeartRate = averageHeartRate,
        maxHeartRate = maxHeartRate,
        calories = calories,
        commute = commute,
        trainer = trainer,
        manual = manual,
        visibility =
            dev.openstride.model.Visibility.entries
                .firstOrNull { it.name == visibility }
                ?: dev.openstride.model.Visibility.PUBLIC,
        gearId = gearId,
        routeId = routeId,
        startLatitude = startLatitude,
        startLongitude = startLongitude,
        endLatitude = endLatitude,
        endLongitude = endLongitude,
        polyline = polyline,
        createdAt = createdAt,
    )

/** Converts a model activity back into the stored form. */
fun dev.openstride.model.Activity.toEntity() =
    ActivityEntity(
        id = id,
        athleteId = athleteId,
        name = name,
        type = type.wireName,
        startedAt = startedAt,
        elapsedSeconds = elapsedSeconds,
        movingSeconds = movingSeconds,
        distanceMeters = distanceMeters,
        elevationGainMeters = elevationGainMeters,
        elevationLossMeters = elevationLossMeters,
        averageSpeedMetersPerSecond = averageSpeedMetersPerSecond,
        maxSpeedMetersPerSecond = maxSpeedMetersPerSecond,
        averageHeartRate = averageHeartRate,
        maxHeartRate = maxHeartRate,
        calories = calories,
        commute = commute,
        trainer = trainer,
        manual = manual,
        visibility = visibility.name,
        gearId = gearId,
        routeId = routeId,
        startLatitude = startLatitude,
        startLongitude = startLongitude,
        endLatitude = endLatitude,
        endLongitude = endLongitude,
        polyline = polyline,
        createdAt = createdAt,
    )
