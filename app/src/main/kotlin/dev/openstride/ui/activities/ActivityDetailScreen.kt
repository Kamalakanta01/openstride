package dev.openstride.ui.activities

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.openstride.OpenStrideApplication
import dev.openstride.local.ActivityEntity
import dev.openstride.recording.decodeDoubles
import dev.openstride.recording.formatDistance
import dev.openstride.recording.formatDuration
import dev.openstride.recording.formatPace
import dev.openstride.recording.formatSpeed
import dev.openstride.recording.haversineMeters
import dev.openstride.ui.components.Stat
import dev.openstride.ui.map.OpenStrideMap
import kotlinx.coroutines.launch
import java.io.File
import java.time.ZoneId

/** One recorded activity, with its map, its numbers and its elevation profile. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActivityDetailScreen(
    activityId: Long,
    onEdit: () -> Unit = {},
    onShare: () -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = (context.applicationContext as OpenStrideApplication).repository
    val database = (context.applicationContext as OpenStrideApplication).database

    var activity by remember { mutableStateOf<ActivityEntity?>(null) }
    var altitudes by remember { mutableStateOf<List<Double>>(emptyList()) }
    var splits by remember { mutableStateOf(emptyList<dev.openstride.analysis.Splits.Split>()) }
    var track by remember { mutableStateOf(emptyList<dev.openstride.recording.TrackPoint>()) }
    var startHidden by remember { mutableStateOf(false) }
    var exporting by remember { mutableStateOf(false) }
    var exported by remember { mutableStateOf<String?>(null) }
    var photos by remember { mutableStateOf(emptyList<dev.openstride.local.ActivityPhotoEntity>()) }
    // The system photo picker needs no permission: the app only ever gets
    // back the one picture the athlete chose.
    val photoPickerLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.PickVisualMedia(),
        ) { uri ->
            val chosen = uri ?: return@rememberLauncherForActivityResult
            scope.launch {
                repository.addPhoto(activityId, chosen.toString())
                photos = database.photoDao().listForActivity(activityId)
            }
        }

    LaunchedEffect(activityId) {
        activity = database.activityDao().byId(activityId)
        // The track goes through the privacy filter, so the parts the
        // athlete has hidden are not shown.
        track = repository.visibleTrack(activityId)
        startHidden = repository.startHidden(activityId)
        photos = database.photoDao().listForActivity(activityId)
        val streams = database.streamDao().forActivity(activityId)
        altitudes = streams.firstOrNull { it.type == "ALTITUDE" }?.let { decodeDoubles(it.data) } ?: emptyList()

        // The splits need the distance covered at each sample, which the
        // streams do not hold; it is computed from the track instead.
        val latitudes = streams.firstOrNull { it.type == "LATITUDE" }?.let { decodeDoubles(it.data) } ?: emptyList()
        val longitudes = streams.firstOrNull { it.type == "LONGITUDE" }?.let { decodeDoubles(it.data) } ?: emptyList()
        val times = streams.firstOrNull { it.type == "TIME" }?.let { decodeDoubles(it.data) } ?: emptyList()
        if (latitudes.size > 1 && times.size == latitudes.size) {
            var distance = 0.0
            val cumulative = mutableListOf<Double>()
            for (index in latitudes.indices) {
                if (index > 0) {
                    distance +=
                        haversineMeters(
                            latitudes[index - 1],
                            longitudes[index - 1],
                            latitudes[index],
                            longitudes[index],
                        )
                }
                cumulative += distance
            }
            splits =
                dev.openstride.analysis.Splits.splits(
                    cumulativeDistance = cumulative,
                    cumulativeTime = times.map { it.toLong() },
                )
        }
    }

    val current = activity
    if (current == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("Loading")
        }
        return
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
    ) {
        OpenStrideMap(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(280.dp),
            track = track,
        )

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = current.name,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f),
                )
                androidx.compose.material3.TextButton(onClick = onEdit) { Text("Edit") }
                androidx.compose.material3.TextButton(onClick = onShare) { Text("Share") }
                androidx.compose.material3.TextButton(onClick = { exporting = true }) { Text("Export") }
            }

            Text(
                text =
                    "${dateFormat.format(current.startedAt.atZone(ZoneId.systemDefault()))} · " +
                        activityTypeLabel(current.type),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (startHidden) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "The start of this activity is hidden by a privacy zone.",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Stat("Distance", formatDistance(current.distanceMeters))
                Stat("Moving", formatDuration(current.movingSeconds))
                Stat("Elapsed", formatDuration(current.elapsedSeconds))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Stat("Speed", formatSpeed(current.averageSpeedMetersPerSecond, metric = true))
                Stat("Pace", formatPace(current.averageSpeedMetersPerSecond, metric = true))
                Stat("Ascent", "${current.elevationGainMeters.toInt()} m")
            }

            if (current.averageHeartRate != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Stat("Avg HR", "${current.averageHeartRate} bpm")
                    Stat("Max HR", "${current.maxHeartRate ?: 0} bpm")
                    Stat("Points", "${track.size}")
                }
            }

            androidx.compose.material3.OutlinedButton(
                onClick = {
                    photoPickerLauncher.launch(
                        androidx.activity.result.PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly,
                        ),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Attach a photograph") }

            if (photos.isNotEmpty()) {
                Text(
                    text = "Photographs",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                photos.forEach { photo ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = photo.caption ?: "Photograph",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                        )
                        androidx.compose.material3.TextButton(
                            onClick = {
                                scope.launch {
                                    repository.deletePhoto(photo.id)
                                    photos = database.photoDao().listForActivity(activityId)
                                }
                            },
                        ) { Text("Remove") }
                    }
                }
            }

            if (exporting) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Export",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text =
                                "The file is written to the app's own folder, so it can be " +
                                    "shared with anything on the phone.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        androidx.compose.foundation.layout.FlowRow(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            androidx.compose.material3.OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        val file =
                                            File(
                                                context.getExternalFilesDir(null),
                                                dev.openstride.io.Export.fileName(
                                                    current,
                                                    dev.openstride.io.Export.Format.GPX,
                                                ),
                                            )
                                        dev.openstride.io.Export.write(
                                            file,
                                            current,
                                            track,
                                            dev.openstride.io.Export.Format.GPX,
                                        )
                                        exported = "${file.name} (${file.length()} bytes)"
                                    }
                                },
                            ) { Text("GPX") }
                            androidx.compose.material3.OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        val file =
                                            File(
                                                context.getExternalFilesDir(null),
                                                dev.openstride.io.Export.fileName(
                                                    current,
                                                    dev.openstride.io.Export.Format.JSON,
                                                ),
                                            )
                                        dev.openstride.io.Export.write(
                                            file,
                                            current,
                                            track,
                                            dev.openstride.io.Export.Format.JSON,
                                        )
                                        exported = "${file.name} (${file.length()} bytes)"
                                    }
                                },
                            ) { Text("JSON") }
                        }
                        if (exported != null) {
                            Text(
                                text = "Written $exported",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp),
                            )
                        }
                    }
                }
            }

            if (splits.size > 1) {
                Text(
                    text = "Splits",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                splits.forEach { split ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text =
                                if (split.distanceMeters >= 1000) {
                                    "${(split.distanceMeters / 1000).let { "%.2f".format(it) }} km"
                                } else {
                                    "${split.distanceMeters.toInt()} m"
                                },
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = formatDuration(split.elapsedSeconds),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = formatPace(split.averageSpeedMetersPerSecond, metric = true),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            if (altitudes.size > 1) {
                Text(
                    text = "Elevation",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                ElevationProfile(
                    altitudes = altitudes,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                )
            }
        }
    }
}

/** The elevation profile, drawn as a filled chart. */
@Composable
private fun ElevationProfile(
    altitudes: List<Double>,
    modifier: Modifier = Modifier,
) {
    val line = MaterialTheme.colorScheme.primary
    val fill = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    Canvas(modifier = modifier) {
        val low = altitudes.min()
        val high = altitudes.max()
        val span = (high - low).coerceAtLeast(1.0)
        val stepX = size.width / (altitudes.size - 1).toFloat()

        val path = Path()
        altitudes.forEachIndexed { index, altitude ->
            val x = index * stepX
            val y = size.height - ((altitude - low) / span).toFloat() * size.height
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        val area =
            Path().apply {
                addPath(path)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
        drawPath(area, fill)

        drawPath(
            path = path,
            color = line,
            style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
        drawCircle(
            color = line,
            radius = 6f,
            center = Offset(altitudes.size * stepX, size.height),
        )
    }
}
