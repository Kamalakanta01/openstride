package dev.openstride.recording

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * What the recording session looks like right now.
 *
 * The service owns the session; the UI observes it. Both live in the same
 * process, so a plain singleton is the simplest thing that works — no
 * binder, no IPC, and the UI sees the live track as it is recorded.
 */
data class RecordingState(
    val isRecording: Boolean = false,
    val isPaused: Boolean = false,
    val activityType: dev.openstride.model.ActivityType = dev.openstride.model.ActivityType.RUN,
    val startedAtMillis: Long = 0,
    val elapsedSeconds: Int = 0,
    val distanceMeters: Double = 0.0,
    val elevationGainMeters: Double = 0.0,
    val averageHeartRate: Int? = null,
    val maxHeartRate: Int? = null,
    val currentLatitude: Double? = null,
    val currentLongitude: Double? = null,
    val currentSpeedMetersPerSecond: Float = 0f,
    val currentElevationMeters: Double = 0.0,
    val points: List<TrackPoint> = emptyList(),
    val error: String? = null,
) {
    val averageSpeedMetersPerSecond: Double
        get() = if (elapsedSeconds > 0) distanceMeters / elapsedSeconds else 0.0
}

/** The live recording session, shared between the service and the user interface. */
object RecordingSession {
    private val _state = MutableStateFlow(RecordingState())

    /** The current state of the recording. */
    val state: StateFlow<RecordingState> = _state.asStateFlow()

    /** The points recorded so far. */
    val points: List<TrackPoint>
        get() = _state.value.points

    internal fun update(block: (RecordingState) -> RecordingState) {
        _state.value = block(_state.value)
    }

    internal fun reset() {
        _state.value = RecordingState()
    }
}
