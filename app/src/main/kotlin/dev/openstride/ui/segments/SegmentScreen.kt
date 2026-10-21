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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.openstride.OpenStrideApplication
import dev.openstride.recording.formatDuration
import dev.openstride.ui.map.OpenStrideMap
import kotlinx.coroutines.launch

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
    val repository = (context.applicationContext as OpenStrideApplication).repository

    val activities by repository.observeActivities().collectAsState(initial = emptyList())
    val segments by repository.observeSegments().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

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
            segment = selected,
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
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "%.0f m".format(segment.distanceMeters),
                                style = MaterialTheme.typography.titleSmall,
                            )
                            androidx.compose.material3.TextButton(
                                onClick = {
                                    scope.launch {
                                        repository.updateSegment(segment.copy(starred = !segment.starred))
                                    }
                                },
                            ) { Text(if (segment.starred) "Starred" else "Star") }
                        }
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
    segment: dev.openstride.local.SegmentEntity,
    onBack: () -> Unit,
) {
    val segmentId = segment.id
    val name = segment.name
    val distanceMeters = segment.distanceMeters
    val context = LocalContext.current
    val repository = (context.applicationContext as OpenStrideApplication).repository
    val scope = rememberCoroutineScope()
    var efforts by remember { mutableStateOf(emptyList<dev.openstride.local.SegmentEffortEntity>()) }
    var points by remember { mutableStateOf(emptyList<dev.openstride.recording.TrackPoint>()) }
    var editing by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(name) }

    LaunchedEffect(segmentId) {
        efforts = repository.segmentEfforts(segmentId)
        points = repository.segmentPoints(segmentId)
    }

    val best = efforts.minByOrNull { it.elapsedSeconds }

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
            if (editing) {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                )
                androidx.compose.material3.Button(
                    onClick = {
                        scope.launch {
                            repository.updateSegment(segment.copy(name = newName))
                            editing = false
                        }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) { Text("Save name") }
            } else {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "%.0f m".format(distanceMeters),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        if (best != null) {
                            Text(
                                text = "Best: ${formatDuration(best.elapsedSeconds)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                    androidx.compose.material3.TextButton(onClick = { editing = true }) {
                        Text("Rename")
                    }
                }
            }
            if (efforts.isEmpty()) {
                Text(
                    text = "No efforts on this segment yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            } else {
                efforts.sortedBy { it.elapsedSeconds }.forEach { effort ->
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Activity ${effort.activityId}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = formatDuration(effort.elapsedSeconds),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight =
                                if (effort == best) {
                                    androidx.compose.ui.text.font.FontWeight.Bold
                                } else {
                                    androidx.compose.ui.text.font.FontWeight.Normal
                                },
                        )
                    }
                }
            }
        }
    }
}
