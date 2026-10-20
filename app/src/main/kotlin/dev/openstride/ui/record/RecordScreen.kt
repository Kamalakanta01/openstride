package dev.openstride.ui.record

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import dev.openstride.model.ActivityType
import dev.openstride.recording.RecordingService
import dev.openstride.recording.RecordingSession
import dev.openstride.recording.formatDistance
import dev.openstride.recording.formatDuration
import dev.openstride.recording.formatPace
import dev.openstride.ui.map.OpenStrideMap

/**
 * The record screen.
 *
 * The big button starts and stops a recording; the map above it follows the
 * track as it is recorded.
 */
@Composable
fun RecordScreen() {
    val context = LocalContext.current
    val state by RecordingSession.state.collectAsState()
    var type by remember { mutableStateOf(ActivityType.RUN) }
    var menuOpen by remember { mutableStateOf(false) }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) { granted ->
            hasPermission = granted[Manifest.permission.ACCESS_FINE_LOCATION] == true
        }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS,
                ),
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        ) {
            OpenStrideMap(
                modifier = Modifier.fillMaxSize(),
                track = state.points,
                followTrack = state.isRecording,
            )

            val error = state.error
            if (error != null) {
                Card(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Stat("Time", formatDuration(state.elapsedSeconds))
                Stat("Distance", formatDistance(state.distanceMeters))
                Stat("Pace", formatPace(state.averageSpeedMetersPerSecond, metric = true))
                Stat("Ascent", "${state.elevationGainMeters.toInt()} m")
            }

            if (!state.isRecording) {
                Box {
                    OutlinedButton(onClick = { menuOpen = true }) {
                        Text(type.wireName)
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        ActivityType.entries.forEach { candidate ->
                            DropdownMenuItem(
                                text = { Text(candidate.wireName) },
                                onClick = {
                                    type = candidate
                                    menuOpen = false
                                },
                            )
                        }
                    }
                }
            }

            if (state.isRecording) {
                Button(
                    onClick = { RecordingService.stop(context) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                    colors =
                        androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                        ),
                ) {
                    Icon(Icons.Filled.Stop, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Stop and save", style = MaterialTheme.typography.titleMedium)
                }
            } else {
                FilledTonalButton(
                    onClick = {
                        if (!hasPermission) {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.POST_NOTIFICATIONS,
                                ),
                            )
                        } else {
                            RecordingService.start(context, type)
                        }
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                ) {
                    Text("Start recording", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

/** One number on the recording panel. */
@Composable
private fun Stat(
    label: String,
    value: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
