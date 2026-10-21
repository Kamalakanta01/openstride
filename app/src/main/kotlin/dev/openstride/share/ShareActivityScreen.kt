package dev.openstride.share

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.openstride.OpenStrideApplication
import dev.openstride.recording.TrackPoint
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Sharing an activity.
 *
 * The original app renders a clip of the activity and offers it to the
 * apps that can post it. This does the same: a clip is rendered from the
 * activity's own track and numbers, written to the gallery, and offered to
 * the share sheet.
 */
@Composable
fun ShareActivityScreen(
    activityId: Long,
    onDone: () -> Unit,
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as OpenStrideApplication).repository
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf(0.0) }
    var elapsed by remember { mutableStateOf(0) }
    var pace by remember { mutableStateOf(0) }
    var ascent by remember { mutableStateOf(0.0) }
    var points by remember { mutableStateOf(emptyList<TrackPoint>()) }

    var working by remember { mutableStateOf(false) }
    var clip by remember { mutableStateOf<Uri?>(null) }
    var picture by remember { mutableStateOf<Uri?>(null) }
    var failed by remember { mutableStateOf(false) }

    LaunchedEffect(activityId) {
        val activity = repository.activity(activityId) ?: return@LaunchedEffect
        name = activity.name
        type = activity.type
        date = activity.startedAt.atZone(ZoneId.systemDefault()).format(DATE_FORMAT)
        distance = activity.distanceMeters
        elapsed = activity.elapsedSeconds
        ascent = activity.elevationGainMeters
        pace =
            if (activity.averageSpeedMetersPerSecond > 0) {
                (1000.0 / activity.averageSpeedMetersPerSecond).toInt()
            } else {
                0
            }
        // The track goes through the privacy filter, so a place the athlete
        // has hidden does not end up in a picture they share.
        points = repository.visibleTrack(activityId)
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Share", style = MaterialTheme.typography.headlineSmall)
        Text(
            text =
                "A short clip of the activity, with the track drawing itself and the " +
                    "numbers counting up. It is written to your gallery, and you can post " +
                    "it wherever you like.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "$type · $date",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (working) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                CircularProgressIndicator()
                Text(
                    text = "Rendering the clip…",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        if (failed) {
            Text(
                text =
                    "This device has no video encoder, so the clip could not be made. " +
                        "The same picture can be saved instead.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        if (picture != null) {
            Button(
                onClick = {
                    val uri = picture ?: return@Button
                    context.startActivity(
                        Intent.createChooser(
                            Intent(Intent.ACTION_SEND).apply {
                                type = "image/png"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            },
                            "Share your activity",
                        ),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Share the picture") }
            OutlinedButton(
                onClick = onDone,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Done") }
        } else if (clip != null) {
            Button(
                onClick = {
                    val uri = clip ?: return@Button
                    val intent =
                        Intent(Intent.ACTION_SEND).apply {
                            type = "video/mp4"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                    context.startActivity(
                        Intent.createChooser(intent, "Share your activity"),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Share the clip") }

            OutlinedButton(
                onClick = onDone,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Done") }
        } else if (!working) {
            Button(
                onClick = {
                    working = true
                    failed = false
                    scope.launch {
                        val card = ShareCard()
                        val encoder = VideoEncoder(context)
                        val uri =
                            if (encoder.isSupported) {
                                encoder.encode(FRAME_COUNT) { progress, canvas ->
                                    card.draw(
                                        canvas = canvas,
                                        activityName = name,
                                        activityType = type,
                                        date = date,
                                        distanceMeters = distance,
                                        elapsedSeconds = elapsed,
                                        paceSecondsPerKilometer = pace,
                                        elevationGainMeters = ascent,
                                        points = points,
                                        progress = progress,
                                    )
                                }
                            } else {
                                null
                            }
                        clip = uri
                        working = false
                        failed = uri == null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Make a clip") }

            OutlinedButton(
                onClick = {
                    working = true
                    failed = false
                    scope.launch {
                        picture =
                            ShareImage.save(
                                context = context,
                                activityName = name,
                                activityType = type,
                                date = date,
                                distanceMeters = distance,
                                elapsedSeconds = elapsed,
                                paceSecondsPerKilometer = pace,
                                elevationGainMeters = ascent,
                                points = points,
                            )
                        working = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Save a picture instead") }
        }
    }
}

/** How long the clip is. */
const val FRAME_COUNT = 240

private val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
