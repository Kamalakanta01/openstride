package dev.openstride.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.openstride.OpenStrideApplication
import dev.openstride.location.DeviceLocation
import dev.openstride.map.FatmapMap
import dev.openstride.recording.RecordingSession
import dev.openstride.recording.formatDistance
import dev.openstride.recording.formatDuration
import org.maplibre.android.maps.MapLibreMap

/**
 * The browse map.
 *
 * It shows the track of the recording in progress, and a button that recentres
 * the camera on the athlete.
 */
@Composable
fun MapScreen() {
    val state by RecordingSession.state.collectAsState()
    val context = LocalContext.current
    val repository = (context.applicationContext as OpenStrideApplication).repository
    val activities by repository.observeActivities().collectAsState(initial = emptyList())
    var map by remember { mutableStateOf<MapLibreMap?>(null) }
    var heatmapOn by remember { mutableStateOf(false) }
    // The engine's own map, and the one the app falls back on. The engine
    // is the 1:1 reimplementation of the original's; the fallback is here
    // until the engine draws every layer.
    var useEngine by remember { mutableStateOf(true) }
    var engineReady by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (useEngine) {
            FatmapMap(
                modifier = Modifier.fillMaxSize(),
                latitude = state.currentLatitude ?: 22.9564,
                longitude = state.currentLongitude ?: 72.6236,
                zoom = 13.0,
                onEngineReady = { engineReady = true },
            )
        } else {
            OpenStrideMap(
                modifier = Modifier.fillMaxSize(),
                track = state.points,
                followTrack = state.isRecording,
                onMapReady = { ready -> map = ready },
            )
        }

        // The switch between the engine's map and the fallback.
        FilledTonalButton(
            onClick = { useEngine = !useEngine },
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
        ) { Text(if (useEngine) "Our engine" else "MapLibre") }

        if (state.isRecording) {
            Card(
                modifier =
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Text(
                        text = "Recording",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Text(
                        text = "${formatDuration(state.elapsedSeconds)} · ${formatDistance(state.distanceMeters)}",
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }
        }

        if (heatmapOn) {
            DrawHeatmap(
                map = map,
                activityIds = activities.map { it.id },
            )
            FilledTonalIconButton(
                onClick = { heatmapOn = false },
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
            ) {
                Icon(Icons.Filled.LocalFireDepartment, contentDescription = "Hide heatmap")
            }
        } else {
            FilledTonalIconButton(
                onClick = { heatmapOn = true },
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
            ) {
                Icon(Icons.Filled.LocalFireDepartment, contentDescription = "Show heatmap")
            }
        }

        if (state.currentLatitude != null && state.currentLongitude != null) {
            FilledTonalIconButton(
                onClick = {
                    val ready = map ?: return@FilledTonalIconButton
                    val position = DeviceLocation.location.value
                    if (position != null) {
                        ready.moveTo(position.latitude, position.longitude)
                    } else {
                        ready.moveTo(state.currentLatitude!!, state.currentLongitude!!)
                    }
                },
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
            ) {
                Icon(Icons.Filled.MyLocation, contentDescription = "Centre on me")
            }
        }
    }
}
