package dev.openstride.ui.segments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import dev.openstride.OpenStrideApplication
import dev.openstride.data.AppRepository
import dev.openstride.recording.formatDuration
import dev.openstride.ui.map.OpenStrideMap

/**
 * The athlete's local segments.
 *
 * Strava's segments are marked by the community and every athlete competes
 * on them. With no community, a segment is a stretch of road the athlete has
 * covered more than once; the app finds them and records an effort every
 * time the athlete goes over one.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegmentScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { AppRepository((context.applicationContext as OpenStrideApplication).database) }

    val activities by repository.observeActivities().collectAsState(initial = emptyList())
    val segments by repository.observeSegments().collectAsState(initial = emptyList())

    var open by remember { mutableStateOf<Long?>(null) }
    var found by remember { mutableStateOf(0) }

    // Look for new segments whenever there is new activity to look at.
    LaunchedEffect(activities.size) {
        if (activities.size >= 2) {
            found = repository.discoverSegments(activities)
        }
    }

    val selected = segments.firstOrNull { it.id == open }

    if (selected != null) {
        SegmentDetail(
            segmentId = selected.id,
            name = selected.name,
            distanceMeters = selected.distanceMeters,
            onBack = { open = null },
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Local segments") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding),
            contentPadding =
                androidx.compose.foundation.layout
                    .PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Text(
                    text =
                        "A segment is a stretch of road you have covered more than once. " +
                            "The app finds them in your own history.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (found > 0) {
                item {
                    Text(
                        text = "$found new segment(s) found",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            if (segments.isEmpty()) {
                item {
                    Text(
                        text = "No segments yet. Record the same route twice and one will appear.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            items(segments, key = { it.id }) { segment ->
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable { open = segment.id },
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = segment.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = segment.activityType,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Text(
                            text = "%.0f m".format(segment.distanceMeters),
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                }
            }
        }
    }
}

/** One segment, with the efforts on it. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SegmentDetail(
    segmentId: Long,
    name: String,
    distanceMeters: Double,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val repository = remember { AppRepository((context.applicationContext as OpenStrideApplication).database) }
    var efforts by remember { mutableStateOf(emptyList<dev.openstride.local.SegmentEffortEntity>()) }
    var points by remember { mutableStateOf(emptyList<dev.openstride.recording.TrackPoint>()) }

    LaunchedEffect(segmentId) {
        efforts = repository.segmentEfforts(segmentId)
        points = repository.segmentPoints(segmentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                        .height(220.dp),
                track = points,
            )
            Text(
                text = "%.0f m".format(distanceMeters),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp),
            )
            if (efforts.isEmpty()) {
                Text(
                    text = "No efforts on this segment yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            } else {
                efforts.forEach { effort ->
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("Activity ${effort.activityId}", style = MaterialTheme.typography.bodyMedium)
                        Text(formatDuration(effort.elapsedSeconds), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
