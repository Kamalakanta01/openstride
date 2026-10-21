package dev.openstride.recording

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import dev.openstride.R
import dev.openstride.local.LocalDatabase
import dev.openstride.model.ActivityType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Foreground service that records an activity.
 *
 * ## Why this looks the way it does
 *
 * The behaviour here is dictated by Android, not by preference:
 *
 *  - A "location" foreground service type is required, and the service must call
 *    [ServiceCompat.startForeground] within a few seconds of starting, or the system
 *    kills it and throws a `ForegroundServiceDidNotStartInTimeException`.
 *  - The service must be started again by the system after a process restart, and in
 *    that case it is handed a **null** Intent. A null intent therefore means
 *    "the process was restarted — resume the most recent resumable recording, or
 *    stop quietly", never "start a fresh recording".
 *  - The notification is not optional. It is the user's only indication that the
 *    GPS is on, and it is the thing users complain about when it is missing.
 *
 * ## The location source
 *
 * The platform [LocationManager] is used rather than Google's fused provider:
 * the fused provider needs Google Play services, and this app must work on a
 * phone without them.
 *
 * ## Recovery
 *
 * Recording state lives in the database ([dev.openstride.local.UnsyncedActivityDao]),
 * not in memory, so a process death loses nothing. On a null intent we look for a
 * resumable [dev.openstride.model.UnsyncedActivity]; if there is none, we stop.
 */
class RecordingService : LifecycleService() {
    private var recordingJob: Job? = null
    private val scope: CoroutineScope by lazy { CoroutineScope(SupervisorJob() + Dispatchers.Default) }

    private val locationManager by lazy { getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    private var locationListener: LocationListener? = null
    private var lastPoint: TrackPoint? = null
    private var startedAtMillis = 0L
    private var sessionGuid: String = UUID.randomUUID().toString()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        super.onStartCommand(intent, flags, startId)

        // Must happen before anything that can block, or the system will kill us.
        startForegroundWithNotification()

        if (intent == null) {
            // Process restart with no intent: resume, or stop.
            scope.launch {
                val resumable = unsyncedDao().resumable().firstOrNull()
                if (resumable == null) {
                    stopSelf()
                } else {
                    resumeRecording(resumable.guid)
                }
            }
            return START_STICKY
        }

        when (intent.getStringExtra(EXTRA_START_MODE)) {
            START_MODE_RECORD ->
                scope.launch {
                    startRecording(
                        type = intent.getStringExtra(EXTRA_ACTIVITY_TYPE).orEmpty(),
                        isIndoor = intent.getBooleanExtra(EXTRA_IS_INDOOR, false),
                    )
                }
            START_MODE_RESUME ->
                scope.launch {
                    resumeRecording(intent.getStringExtra(EXTRA_GUID).orEmpty())
                }
            START_MODE_STOP ->
                scope.launch {
                    finishRecording()
                }
            else -> stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        recordingJob?.cancel()
        scope.cancel()
        stopListening()
        super.onDestroy()
    }

    private suspend fun startRecording(
        type: String,
        isIndoor: Boolean,
    ) {
        val activityType = ActivityType.entries.firstOrNull { it.wireName == type } ?: ActivityType.RUN
        startedAtMillis = System.currentTimeMillis()

        RecordingSession.update {
            it.copy(
                isRecording = true,
                isPaused = false,
                activityType = activityType,
                startedAtMillis = startedAtMillis,
                elapsedSeconds = 0,
                distanceMeters = 0.0,
                points = emptyList(),
                error = null,
            )
        }

        persistProgress(guid = sessionGuid, state = "IN_PROGRESS")
        beginListening()
        startTicking()
    }

    private suspend fun resumeRecording(guid: String) {
        val row = unsyncedDao().byGuid(guid)
        if (row == null) {
            stopSelf()
            return
        }
        startedAtMillis = row.startedAt.toEpochMilli()
        RecordingSession.update {
            it.copy(
                isRecording = true,
                activityType = ActivityType.entries.firstOrNull { e -> e.wireName == row.type } ?: ActivityType.RUN,
                startedAtMillis = startedAtMillis,
                points = decodeSamples(row.samples),
                error = null,
            )
        }
        beginListening()
        startTicking()
    }

    private fun startTicking() {
        recordingJob?.cancel()
        recordingJob =
            scope.launch {
                while (true) {
                    delay(1000)
                    if (!RecordingSession.state.value.isRecording) continue
                    val state = RecordingSession.state.value
                    val elapsed = ((System.currentTimeMillis() - state.startedAtMillis) / 1000).toInt()
                    val summary = summarise(state.points)
                    RecordingSession.update {
                        it.copy(
                            elapsedSeconds = elapsed,
                            distanceMeters = summary.distanceMeters,
                            elevationGainMeters = summary.elevationGainMeters,
                            averageHeartRate = summary.averageHeartRate,
                            maxHeartRate = summary.maxHeartRate,
                        )
                    }
                    updateNotification()

                    // The track is written out every so often, so that a
                    // process death loses a few seconds and not the whole
                    // activity.
                    if (elapsed % PERSIST_EVERY_SECONDS == 0) {
                        persistProgress(guid = sessionGuid, state = "IN_PROGRESS")
                    }
                }
            }
    }

    /**
     * Subscribes to the location providers.
     *
     * GPS is the accurate one; the network provider fills the gaps when the
     * sky is blocked. Both are asked for, and a provider that is not
     * available is simply skipped.
     */
    private fun beginListening() {
        stopListening()
        val listener = LocationListener { onLocation(it) }
        locationListener = listener

        for (provider in PROVIDERS) {
            if (!hasLocationPermission()) {
                RecordingSession.update { it.copy(error = "Location permission is missing") }
                return
            }
            try {
                if (!locationManager.isProviderEnabled(provider)) continue
                locationManager.requestLocationUpdates(
                    provider,
                    MIN_INTERVAL_MILLIS,
                    MIN_DISTANCE_METERS,
                    listener,
                    mainLooper,
                )
            } catch (_: SecurityException) {
                RecordingSession.update { it.copy(error = "Location permission is missing") }
                return
            } catch (_: IllegalArgumentException) {
                // The provider is not on this device.
            }
        }
    }

    private fun stopListening() {
        locationListener?.let { listener ->
            runCatching { locationManager.removeUpdates(listener) }
        }
        locationListener = null
    }

    private fun onLocation(location: Location) {
        val point =
            TrackPoint(
                latitude = location.latitude,
                longitude = location.longitude,
                altitudeMeters = if (location.hasAltitude()) location.altitude else 0.0,
                timeMillis = if (location.time > 0) location.time else System.currentTimeMillis(),
                speedMetersPerSecond = if (location.hasSpeed()) location.speed else 0f,
                accuracyMeters = if (location.hasAccuracy()) location.accuracy else 0f,
            )

        // Drop a point that is obviously a glitch: a jump of more than a
        // kilometre between two samples is the receiver losing its fix, not
        // the athlete moving.
        val previous = lastPoint
        if (previous != null) {
            val step = haversineMeters(previous.latitude, previous.longitude, point.latitude, point.longitude)
            if (step > MAX_JUMP_METERS) return
        }
        lastPoint = point

        RecordingSession.update {
            it.copy(
                points = it.points + point,
                currentLatitude = point.latitude,
                currentLongitude = point.longitude,
                currentSpeedMetersPerSecond = point.speedMetersPerSecond,
                currentElevationMeters = point.altitudeMeters,
            )
        }
    }

    /** Stops the recording and writes the activity to the database. */
    private suspend fun finishRecording() {
        val state = RecordingSession.state.value
        stopListening()
        recordingJob?.cancel()

        if (state.points.isEmpty()) {
            RecordingSession.reset()
            stopSelf()
            return
        }

        val summary = summarise(state.points)
        val database = database()
        val athleteId = ensureAthlete(database)
        val startedAt = java.time.Instant.ofEpochMilli(state.startedAtMillis)

        val activityId =
            database.activityDao().upsert(
                dev.openstride.local.ActivityEntity(
                    athleteId = athleteId,
                    name = defaultName(state.activityType, startedAt),
                    type = state.activityType.wireName,
                    startedAt = startedAt,
                    elapsedSeconds = summary.elapsedSeconds,
                    movingSeconds = summary.movingSeconds,
                    distanceMeters = summary.distanceMeters,
                    elevationGainMeters = summary.elevationGainMeters,
                    elevationLossMeters = summary.elevationLossMeters,
                    averageSpeedMetersPerSecond = summary.averageSpeedMetersPerSecond,
                    maxSpeedMetersPerSecond = summary.maxSpeedMetersPerSecond,
                    averageHeartRate = summary.averageHeartRate,
                    maxHeartRate = summary.maxHeartRate,
                    calories = null,
                    commute = false,
                    trainer = false,
                    manual = false,
                    visibility = dev.openstride.model.Visibility.PUBLIC.name,
                    gearId = null,
                    routeId = null,
                    startLatitude = state.points.first().latitude,
                    startLongitude = state.points.first().longitude,
                    endLatitude = state.points.last().latitude,
                    endLongitude = state.points.last().longitude,
                    polyline = encodePolyline(state.points),
                    createdAt = java.time.Instant.now(),
                ),
            )

        database.streamDao().upsert(
            dev.openstride.local.ActivityStreamEntity(
                activityId = activityId,
                type = dev.openstride.model.StreamType.LATITUDE.name,
                data = encodeDoubles(state.points.map { it.latitude }),
                resolutionSeconds = 1,
            ),
        )
        database.streamDao().upsert(
            dev.openstride.local.ActivityStreamEntity(
                activityId = activityId,
                type = dev.openstride.model.StreamType.LONGITUDE.name,
                data = encodeDoubles(state.points.map { it.longitude }),
                resolutionSeconds = 1,
            ),
        )
        database.streamDao().upsert(
            dev.openstride.local.ActivityStreamEntity(
                activityId = activityId,
                type = dev.openstride.model.StreamType.ALTITUDE.name,
                data = encodeDoubles(state.points.map { it.altitudeMeters }),
                resolutionSeconds = 1,
            ),
        )
        database.streamDao().upsert(
            dev.openstride.local.ActivityStreamEntity(
                activityId = activityId,
                type = dev.openstride.model.StreamType.TIME.name,
                data = encodeDoubles(state.points.map { it.timeMillis.toDouble() }),
                resolutionSeconds = 1,
            ),
        )

        // The activity is saved, so the recovery row has done its job.
        unsyncedDao().delete(sessionGuid)

        RecordingSession.reset()
        stopSelf()
    }

    private suspend fun persistProgress(
        guid: String,
        state: String,
    ) {
        val current = RecordingSession.state.value
        database().unsyncedDao().upsert(
            dev.openstride.local.UnsyncedActivityEntity(
                guid = guid,
                athleteId = 1,
                type = current.activityType.wireName,
                startedAt = java.time.Instant.ofEpochMilli(current.startedAtMillis),
                samples = encodeSamples(current.points),
                state = state,
            ),
        )
    }

    private suspend fun ensureAthlete(database: LocalDatabase): Long {
        val existing = database.athleteDao().current()
        if (existing != null) return existing.id
        return database.athleteDao().upsert(
            dev.openstride.local.AthleteEntity(
                name = "Athlete",
                email = null,
                city = null,
                country = null,
                timezone =
                    java.time.ZoneId
                        .systemDefault()
                        .id,
                weightKilograms = null,
                createdAt = java.time.Instant.now(),
            ),
        )
    }

    private fun defaultName(
        type: ActivityType,
        startedAt: java.time.Instant,
    ): String {
        // Android's java.time is a subset of the desktop one: LocalTime has
        // no ofInstant, so the instant is turned into a zoned time first.
        val time = startedAt.atZone(java.time.ZoneId.systemDefault()).toLocalTime()
        return String.format(java.util.Locale.US, "%s at %d:%02d", type.wireName, time.hour, time.minute)
    }

    private fun startForegroundWithNotification() {
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION,
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun buildNotification(): Notification =
        NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.recording_notification_title))
            .setContentText(notificationText())
            .setSmallIcon(R.drawable.ic_stat_recording)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()

    private fun notificationText(): String {
        val state = RecordingSession.state.value
        if (!state.isRecording) return getString(R.string.recording_notification_body)
        return getString(
            R.string.recording_notification_live,
            formatDuration(state.elapsedSeconds),
            formatDistance(state.distanceMeters),
        )
    }

    private fun updateNotification() {
        val manager = getSystemService(NotificationManager::class.java) ?: return
        runCatching { manager.notify(NOTIFICATION_ID, buildNotification()) }
    }

    private fun createNotificationChannel() {
        val manager = getSystemService(NotificationManager::class.java) ?: return
        val channel =
            NotificationChannel(
                CHANNEL_ID,
                getString(R.string.recording_channel_name),
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = getString(R.string.recording_channel_description)
                setShowBadge(false)
            }
        manager.createNotificationChannel(channel)
    }

    private fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    private fun database() = (application as dev.openstride.OpenStrideApplication).database

    private fun unsyncedDao() = database().unsyncedDao()

    companion object {
        const val CHANNEL_ID = "recording"
        const val NOTIFICATION_ID = 1001

        const val EXTRA_START_MODE = "start_mode"
        const val EXTRA_ACTIVITY_TYPE = "activity_type"
        const val EXTRA_GUID = "guid"
        const val EXTRA_IS_INDOOR = "is_indoor"

        const val START_MODE_RECORD = "record"
        const val START_MODE_RESUME = "resume"
        const val START_MODE_STOP = "stop"

        private const val PERSIST_EVERY_SECONDS = 10
        private const val MIN_INTERVAL_MILLIS = 1000L
        private const val MIN_DISTANCE_METERS = 0f
        private const val MAX_JUMP_METERS = 1000.0
        private val PROVIDERS = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)

        /** Starts a new recording. */
        fun start(
            context: Context,
            type: ActivityType,
        ) {
            val intent =
                Intent(context, RecordingService::class.java).apply {
                    putExtra(EXTRA_START_MODE, START_MODE_RECORD)
                    putExtra(EXTRA_ACTIVITY_TYPE, type.wireName)
                }
            ContextCompat.startForegroundService(context, intent)
        }

        /** Stops the recording and saves the activity. */
        fun stop(context: Context) {
            val intent =
                Intent(context, RecordingService::class.java).apply {
                    putExtra(EXTRA_START_MODE, START_MODE_STOP)
                }
            ContextCompat.startForegroundService(context, intent)
        }
    }
}

/** Encodes the points of a track into the blob stored in the database. */
internal fun encodeSamples(points: List<TrackPoint>): ByteArray {
    val buffer = java.nio.ByteBuffer.allocate(points.size * 6 * 8)
    for (point in points) {
        buffer.putDouble(point.latitude)
        buffer.putDouble(point.longitude)
        buffer.putDouble(point.altitudeMeters)
        buffer.putDouble(point.timeMillis.toDouble())
        buffer.putFloat(point.speedMetersPerSecond)
        buffer.putInt(point.heartRate ?: -1)
    }
    return buffer.array()
}

/** Reads back the points of a track from the database blob. */
internal fun decodeSamples(data: ByteArray): List<TrackPoint> {
    if (data.isEmpty()) return emptyList()
    val buffer = java.nio.ByteBuffer.wrap(data)
    val points = mutableListOf<TrackPoint>()
    while (buffer.remaining() >= 6 * 8) {
        val latitude = buffer.getDouble()
        val longitude = buffer.getDouble()
        val altitude = buffer.getDouble()
        val time = buffer.getDouble().toLong()
        val speed = buffer.getFloat()
        val heart = buffer.getInt()
        points +=
            TrackPoint(
                latitude = latitude,
                longitude = longitude,
                altitudeMeters = altitude,
                timeMillis = time,
                speedMetersPerSecond = speed,
                heartRate = if (heart >= 0) heart else null,
            )
    }
    return points
}

/** Encodes a series of numbers into the blob stored in the database. */
internal fun encodeDoubles(values: List<Double>): ByteArray {
    val buffer = java.nio.ByteBuffer.allocate(values.size * 8)
    for (value in values) {
        buffer.putDouble(value)
    }
    return buffer.array()
}

/** Reads back a series of numbers from a database blob. */
internal fun decodeDoubles(data: ByteArray): List<Double> {
    val buffer = java.nio.ByteBuffer.wrap(data)
    val values = mutableListOf<Double>()
    while (buffer.remaining() >= 8) {
        values += buffer.getDouble()
    }
    return values
}
