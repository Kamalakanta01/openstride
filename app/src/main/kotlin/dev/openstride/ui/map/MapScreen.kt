package dev.openstride.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Card
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
import dev.openstride.data.AppRepository
import dev.openstride.recording.RecordingSession
import dev.openstride.recording.formatDistance
import dev.openstride.recording.formatDuration
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
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
    val repository = remember { AppRepository((context.applicationContext as OpenStrideApplication).database) }
    val activities by repository.observeActivities().collectAsState(initial = emptyList())
    var map by remember { mutableStateOf<MapLibreMap?>(null) }
    var heatmapOn by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        OpenStrideMap(
            modifier = Modifier.fillMaxSize(),
            track = state.points,
            followTrack = state.isRecording,
            onMapReady = { ready -> map = ready },
        )

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
                    ready.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(state.currentLatitude!!, state.currentLongitude!!),
                            16.0,
                        ),
                        500,
                    )
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
