package dev.openstride.ui.routes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.openstride.OpenStrideApplication
import dev.openstride.local.RouteEntity
import dev.openstride.local.RoutePointEntity
import dev.openstride.recording.TrackPoint
import dev.openstride.recording.haversineMeters
import dev.openstride.ui.map.OpenStrideMap
import kotlinx.coroutines.launch
import java.time.Instant

/**
 * The route planner.
 *
 * The original app's planner is a rich map editor with snapping, elevation
 * profiles and turn-by-turn directions from a routing service. There is no
 * routing service here, so a route is drawn by tapping the points it goes
 * through: the tap positions become the route's shape, and the distance and
 * the estimated time are computed from them.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RoutePlannerScreen(
    onDone: () -> Unit,
    onSaved: (Long) -> Unit = {},
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as OpenStrideApplication).repository
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var points by remember { mutableStateOf(emptyList<TrackPoint>()) }

    val distance =
        points.zipWithNext().sumOf { (a, b) ->
            haversineMeters(a.latitude, a.longitude, b.latitude, b.longitude)
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New route") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding),
        ) {
            OpenStrideMap(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                track = points,
                onTap = { point -> points = points + point },
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text =
                        if (points.size < 2) {
                            "Tap the map to add the points the route goes through."
                        } else {
                            "${points.size} points · ${(distance / 1000).let { "%.2f".format(it) }} km"
                        },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    androidx.compose.material3.OutlinedButton(
                        onClick = { points = points.dropLast(1) },
                        modifier = Modifier.weight(1f),
                    ) { Text("Undo") }
                    Button(
                        onClick = {
                            scope.launch {
                                val id =
                                    repository.saveRoute(
                                        route =
                                            RouteEntity(
                                                athleteId = 1,
                                                name = name.ifBlank { "My route" },
                                                description = null,
                                                distanceMeters = distance,
                                                elevationGainMeters = 0.0,
                                                estimatedSeconds = null,
                                                polyline = null,
                                                visibility = "PRIVATE",
                                                createdAt = Instant.now(),
                                            ),
                                        points =
                                            points.mapIndexed { index, point ->
                                                RoutePointEntity(
                                                    routeId = 0,
                                                    index = index,
                                                    latitude = point.latitude,
                                                    longitude = point.longitude,
                                                    distanceMeters = 0.0,
                                                    elapsedSeconds = 0,
                                                    elevationMeters = null,
                                                    gradePercent = null,
                                                    tempoZone = null,
                                                )
                                            },
                                    )
                                onSaved(id)
                                onDone()
                            }
                        },
                        enabled = points.size > 1,
                        modifier = Modifier.weight(1f),
                    ) { Text("Save route") }
                }
            }
        }
    }
}
