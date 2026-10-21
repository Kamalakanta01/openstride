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
import dev.openstride.local.PrivacyFilter
import dev.openstride.local.PrivacyZoneEntity
import dev.openstride.local.RouteEntity
import dev.openstride.local.RoutePointEntity
import dev.openstride.local.SegmentEffortEntity
import dev.openstride.local.SegmentEntity
import dev.openstride.local.SegmentPointEntity
import dev.openstride.local.startHiddenBy
import dev.openstride.local.toModel
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
 *
 * There is one instance of this, held by the application, so the expensive
 * work — rebuilding the tracks, recomputing the analysis, rebuilding the
 * heatmap — is done once and shared by every screen.
 */
class AppRepository(
    private val database: LocalDatabase,
) {
    // ---- activities ----------------------------------------------------

    fun observeActivities(): Flow<List<ActivityEntity>> = database.activityDao().observeAll()

    fun observeActivities(type: ActivityType): Flow<List<ActivityEntity>> = database.activityDao().observeByType(type.wireName)

    suspend fun activity(id: Long): ActivityEntity? = database.activityDao().byId(id)

    suspend fun deleteActivity(id: Long) = database.activityDao().delete(id)

    /** The photographs attached to an activity. */
    fun observePhotos(activityId: Long) = database.photoDao().forActivity(activityId)

    /** Attaches a photograph to an activity. */
    suspend fun addPhoto(
        activityId: Long,
        uri: String,
        caption: String? = null,
    ): Long =
        database.photoDao().upsert(
            dev.openstride.local.ActivityPhotoEntity(
                activityId = activityId,
                uri = uri,
                caption = caption,
                takenAt = java.time.Instant.now(),
            ),
        )

    /** Removes a photograph. */
    suspend fun deletePhoto(id: Long) = database.photoDao().delete(id)

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

    /**
     * The part of an activity's track that may be shown.
     *
     * The privacy zones are applied here, so that no screen has to remember
     * to apply them.
     */
    suspend fun visibleTrack(activityId: Long): List<TrackPoint> = PrivacyFilter.visibleTrack(track(activityId), privacyZones())

    /** The privacy zones, as the filter wants them. */
    suspend fun privacyZones(): List<dev.openstride.model.PrivacyZone> = database.privacyZoneDao().all().map { it.toModel() }

    /** Whether the start of an activity is hidden by a zone. */
    suspend fun startHidden(activityId: Long): Boolean {
        val zones = privacyZones()
        if (zones.isEmpty()) return false
        return startHiddenBy(track(activityId), zones)
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

    // ---- the training analysis -----------------------------------------

    /**
     * The training analysis of a set of activities.
     *
     * Everything is computed from one pass over the activities, off the main
     * thread. The screens used to compute this five times over, on the main
     * thread, every time they recomposed.
     */
    suspend fun analysis(activities: List<ActivityEntity>): TrainingAnalysis =
        withContext(Dispatchers.Default) {
            val models = activities.map { it.toModel() }
            val loads = Fitness.dailyLoads(models)
            TrainingAnalysis(
                loads = loads,
                // The curve carries on for a while past the last activity:
                // the recovery is the interesting part of it.
                series = Fitness.series(loads, endDate = loads.lastOrNull()?.date?.plusDays(10)),
                weeks = Fitness.weeks(loads),
                records = Records.of(models),
                badges = Achievements.of(models),
                streak = Fitness.currentWeekStreak(models),
                consistency = Fitness.consistency(loads),
            )
        }

    /**
     * The athlete's personal heatmap.
     *
     * The privacy zones are applied to the tracks first: a place the athlete
     * has hidden does not belong on the heatmap either.
     */
    suspend fun heatmap(activityIds: List<Long>): List<Heatmap.Cell> =
        withContext(Dispatchers.Default) {
            val zones = privacyZones()
            val tracks = activityIds.map { PrivacyFilter.visibleTrack(track(it), zones) }
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

    /** Renames a segment and sets whether it is starred. */
    suspend fun updateSegment(segment: dev.openstride.local.SegmentEntity): Long = database.segmentDao().upsert(segment)

    /** The efforts on a segment. */
    suspend fun segmentEfforts(segmentId: Long): List<SegmentEffortEntity> = database.segmentDao().efforts(segmentId)

    /**
     * Looks for new segments in the athlete's history and records efforts.
     *
     * This is the local stand-in for Strava's crowdsourced segments: a
     * stretch of road covered twice becomes a segment, and every activity
     * over it afterwards is an effort.
     */
    suspend fun discoverSegments(activities: List<ActivityEntity>): Int =
        withContext(Dispatchers.Default) { discoverSegmentsOn(activities) }

    private suspend fun discoverSegmentsOn(activities: List<ActivityEntity>): Int {
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

/**
 * The training analysis, all of it at once.
 *
 * The screens show the load, the fitness curve, the weekly load, the
 * records, the badges, the streak and the consistency. They are all derived
 * from the same activities, so they are computed together rather than five
 * times over.
 */
data class TrainingAnalysis(
    val loads: List<Fitness.DailyLoad> = emptyList(),
    val series: List<Fitness.Point> = emptyList(),
    val weeks: List<Fitness.Week> = emptyList(),
    val records: List<Records.Record> = emptyList(),
    val badges: List<Achievements.Badge> = emptyList(),
    val streak: Int = 0,
    val consistency: Int = 0,
)

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
