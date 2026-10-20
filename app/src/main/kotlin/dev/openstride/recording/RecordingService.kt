package dev.openstride.recording

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import dev.openstride.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

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
 * ## Recovery
 *
 * Recording state lives in the database ([dev.openstride.local.UnsyncedActivityDao]),
 * not in memory, so a process death loses nothing. On a null intent we look for a
 * resumable [dev.openstride.model.UnsyncedActivity]; if there is none, we stop.
 */
class RecordingService : LifecycleService() {
    private var recordingJob: Job? = null
    private val scope: CoroutineScope by lazy { CoroutineScope(SupervisorJob() + Dispatchers.Default) }

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
            else -> stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        recordingJob?.cancel()
        scope.cancel()
        super.onDestroy()
    }

    private suspend fun startRecording(
        type: String,
        isIndoor: Boolean,
    ) {
        // TODO: wire up the location + sensor pipeline.
        // See docs/06-code-architecture.md for the reference behaviour.
    }

    private suspend fun resumeRecording(guid: String) {
        // TODO: reload samples from the database and restart the pipeline.
    }

    private fun startForegroundWithNotification() {
        val notification: Notification =
            NotificationCompat
                .Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.recording_notification_title))
                .setContentText(getString(R.string.recording_notification_body))
                .setSmallIcon(R.drawable.ic_stat_recording)
                .setOngoing(true)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                .build()

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

    private fun unsyncedDao() = (application as dev.openstride.OpenStrideApplication).database.unsyncedDao()

    companion object {
        const val CHANNEL_ID = "recording"
        const val NOTIFICATION_ID = 1001

        const val EXTRA_START_MODE = "start_mode"
        const val EXTRA_ACTIVITY_TYPE = "activity_type"
        const val EXTRA_GUID = "guid"
        const val EXTRA_IS_INDOOR = "is_indoor"

        const val START_MODE_RECORD = "record"
        const val START_MODE_RESUME = "resume"
    }
}
