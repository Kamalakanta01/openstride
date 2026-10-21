package dev.openstride.ui.routes

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
import dev.openstride.recording.formatDistance
import dev.openstride.ui.map.OpenStrideMap

/**
 * Saved routes.
 *
 * The original app has a route planner with a rich map editor and turn-by-turn
 * directions from a routing service. There is no routing service here, so a
 * route is the shape of an activity the athlete has done: it is saved, it is
 * drawn on the map, and it can be followed by recording over it.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteScreen(
    onBack: () -> Unit,
    onNew: () -> Unit = {},
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as OpenStrideApplication).repository

    val routes by repository.observeRoutes().collectAsState(initial = emptyList())
    var open by remember { mutableStateOf<Long?>(null) }

    val selected = routes.firstOrNull { it.id == open }
    var selectedPoints by remember { mutableStateOf(emptyList<dev.openstride.local.RoutePointEntity>()) }
    LaunchedEffect(selected?.id) {
        selectedPoints = selected?.let { repository.routePoints(it.id) } ?: emptyList()
    }
    if (selected != null) {
        RouteDetail(
            name = selected.name,
            distanceMeters = selected.distanceMeters,
            points = selectedPoints,
            onBack = { open = null },
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Routes") },
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
                androidx.compose.material3.Button(onClick = onNew, modifier = Modifier.fillMaxWidth()) {
                    Text("Draw a new route")
                }
                Text(
                    text =
                        "Save a route from an activity you have recorded, or draw a new one " +
                            "by tapping the points it goes through.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            if (routes.isEmpty()) {
                item {
                    Text(
                        text = "No routes yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            items(routes, key = { it.id }) { route ->
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable { open = route.id },
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = route.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text =
                                    route.createdAt
                                        .atZone(java.time.ZoneId.systemDefault())
                                        .toLocalDate()
                                        .toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Text(formatDistance(route.distanceMeters), style = MaterialTheme.typography.titleSmall)
                    }
                }
            }
        }
    }
}

/** One route, drawn on the map. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RouteDetail(
    name: String,
    distanceMeters: Double,
    points: List<dev.openstride.local.RoutePointEntity>,
    onBack: () -> Unit,
) {
    val track =
        points.map {
            dev.openstride.recording.TrackPoint(it.latitude, it.longitude, it.elevationMeters ?: 0.0, 0L)
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
                        .height(260.dp),
                track = track,
            )
            Text(
                text = formatDistance(distanceMeters),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
