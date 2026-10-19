package dev.stravaoss.core.model

import java.time.Instant

/**
 * The activity types the app understands.
 *
 * Derived from the behaviour of the original app; the enum names are our own.
 */
enum class ActivityType(val wireName: String, val canBeIndoor: Boolean = false) {
    RUN("Run"),
    RIDE("Ride"),
    VIRTUAL_RIDE("VirtualRide", canBeIndoor = true),
    WALK("Walk"),
    HIKE("Hike"),
    SWIM("Swim", canBeIndoor = true),
    WORKOUT("Workout", canBeIndoor = true),
    ELLIPTICAL("Elliptical", canBeIndoor = true),
    ROWING("Rowing", canBeIndoor = true),
    SKI("AlpineSki"),
    SNOWBOARD("Snowboard"),
    OTHER("Other");
}

/** Who may see an activity. */
enum class Visibility { PUBLIC, FOLLOWERS, ONLY_ME }

enum class GearType { BIKE, SHOES, OTHER }

enum class ZoneType { POWER, HEART_RATE, SPEED, CADENCE }

/** A recorded or imported activity. */
data class Activity(
    val id: Long = 0,
    val athleteId: Long,
    val name: String,
    val type: ActivityType,
    val startedAt: Instant,
    val elapsedSeconds: Int,
    val movingSeconds: Int = 0,
    val distanceMeters: Double = 0.0,
    val elevationGainMeters: Double = 0.0,
    val elevationLossMeters: Double = 0.0,
    val averageSpeedMetersPerSecond: Double = 0.0,
    val maxSpeedMetersPerSecond: Double = 0.0,
    val averageHeartRate: Int? = null,
    val maxHeartRate: Int? = null,
    val calories: Int? = null,
    val commute: Boolean = false,
    val trainer: Boolean = false,
    val manual: Boolean = false,
    val visibility: Visibility = Visibility.FOLLOWERS,
    val gearId: Long? = null,
    val routeId: Long? = null,
    val startLatitude: Double? = null,
    val startLongitude: Double? = null,
    val endLatitude: Double? = null,
    val endLongitude: Double? = null,
    val polyline: String? = null,
    val createdAt: Instant = Instant.now(),
)

/** Time-series samples for one activity, kept apart because they are large. */
data class ActivityStreams(
    val activityId: Long,
    val type: StreamType,
    val data: ByteArray,
    val resolutionSeconds: Int,
) {
    // ByteArray needs explicit equality; data classes generate the wrong one.
    override fun equals(other: Any?): Boolean =
        other is ActivityStreams &&
            activityId == other.activityId &&
            type == other.type &&
            resolutionSeconds == other.resolutionSeconds &&
            data.contentEquals(other.data)

    override fun hashCode(): Int =
        ((activityId * 31 + type.hashCode()) * 31 + resolutionSeconds) * 31 + data.contentHashCode()
}

enum class StreamType {
    LATITUDE, LONGITUDE, DISTANCE, HEART_RATE, CADENCE, POWER, ALTITUDE, SPEED, GRADE, TEMPERATURE
}

/** The signed-in athlete. Local-only: this is never a Strava account. */
data class Athlete(
    val id: Long = 0,
    val name: String,
    val email: String? = null,
    val city: String? = null,
    val country: String? = null,
    val timezone: String = java.time.ZoneId.systemDefault().id,
    val weightKilograms: Double? = null,
    val createdAt: Instant = Instant.now(),
)

/** A route: an ordered list of points, plus summary metrics. */
data class Route(
    val id: Long = 0,
    val athleteId: Long,
    val name: String,
    val description: String? = null,
    val distanceMeters: Double,
    val elevationGainMeters: Double = 0.0,
    val estimatedSeconds: Int? = null,
    val polyline: String? = null,
    val visibility: Visibility = Visibility.PRIVATE,
    val createdAt: Instant = Instant.now(),
)

data class RoutePoint(
    val routeId: Long,
    val index: Int,
    val latitude: Double,
    val longitude: Double,
    val distanceMeters: Double,
    val elapsedSeconds: Int,
    val elevationMeters: Double? = null,
    val gradePercent: Double? = null,
    val tempoZone: Int? = null,
)

/** A segment: a stretch of road or trail that people race on. */
data class Segment(
    val id: Long = 0,
    val name: String,
    val activityType: ActivityType,
    val distanceMeters: Double,
    val elevationGainMeters: Double = 0.0,
    val climbCategory: Int? = null,
    val localLegend: LocalLegend? = null,
)

enum class LocalLegend { FEMALE, MALE, NON_BINARY, UNKNOWN }

/** A recorded effort on a segment. */
data class SegmentEffort(
    val id: Long = 0,
    val segmentId: Long,
    val activityId: Long,
    val athleteId: Long,
    val elapsedSeconds: Int,
    val distanceMeters: Double,
    val averageHeartRate: Int? = null,
    val maxHeartRate: Int? = null,
    val averageWatts: Double? = null,
    val isKom: Boolean = false,
)

data class Gear(
    val id: Long = 0,
    val athleteId: Long,
    val name: String,
    val type: GearType,
    val brand: String? = null,
    val model: String? = null,
    val distanceMeters: Double = 0.0,
    val retiredAt: Instant? = null,
)

data class Goal(
    val id: Long = 0,
    val type: GoalType,
    val targetValue: Double,
    val periodStart: java.time.LocalDate,
    val periodEnd: java.time.LocalDate,
)

enum class GoalType { WEEKLY_DISTANCE, MONTHLY_DISTANCE, ANNUAL_ELEVATION, WEEKLY_ELEVATION }

/**
 * An area the athlete has marked as private.
 *
 * The single most safety-relevant model in the app. Privacy zones must be applied
 * before any map render, export, or share — see [dev.stravaoss.core.local.PrivacyZoneFilter].
 */
data class PrivacyZone(
    val id: Long = 0,
    val name: String,
    val centerLatitude: Double,
    val centerLongitude: Double,
    val radiusMeters: Double,
    val type: PrivacyZoneType,
)

enum class PrivacyZoneType { PRIVATE, HIDDEN_START, HIDDEN_END, ENTIRE_MAP }

/** A recording in progress, or interrupted. */
data class UnsyncedActivity(
    val guid: String,
    val athleteId: Long,
    val type: ActivityType,
    val startedAt: Instant,
    val samples: ByteArray = ByteArray(0),
    val state: RecordingState = RecordingState.IN_PROGRESS,
) {
    override fun equals(other: Any?): Boolean =
        other is UnsyncedActivity &&
            guid == other.guid &&
            athleteId == other.athleteId &&
            type == other.type &&
            startedAt == other.startedAt &&
            state == other.state &&
            samples.contentEquals(other.samples)

    override fun hashCode(): Int =
        ((guid.hashCode() * 31 + athleteId.hashCode()) * 31 + type.hashCode()) * 31 + samples.contentHashCode()
}

enum class RecordingState { IN_PROGRESS, PAUSED, FINISHED, DISCARDED }
