package dev.openstride.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import dev.openstride.local.ActivityEntity
import dev.openstride.model.ActivityType
import kotlinx.coroutines.launch
import java.time.Instant

/**
 * Adding an activity by hand.
 *
 * The original app has this: a treadmill run, a swim, or a ride that was
 * recorded by something else has to be entered by hand. The numbers are
 * typed in, and there is no track.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ManualActivityScreen(onDone: () -> Unit) {
    val context = LocalContext.current
    val repository = (context.applicationContext as OpenStrideApplication).repository
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(ActivityType.RUN) }
    var distance by remember { mutableStateOf("") }
    var hours by remember { mutableStateOf("0") }
    var minutes by remember { mutableStateOf("30") }
    var seconds by remember { mutableStateOf("0") }
    var elevation by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add an activity") },
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
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = "Activity type",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ActivityType.entries.forEach { candidate ->
                    FilterChip(
                        selected = candidate == type,
                        onClick = { type = candidate },
                        label = { Text(candidate.wireName) },
                    )
                }
            }

            OutlinedTextField(
                value = distance,
                onValueChange = { distance = it },
                label = { Text("Distance (km)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = hours,
                    onValueChange = { hours = it },
                    label = { Text("Hours") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = minutes,
                    onValueChange = { minutes = it },
                    label = { Text("Minutes") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = seconds,
                    onValueChange = { seconds = it },
                    label = { Text("Seconds") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
            }
            OutlinedTextField(
                value = elevation,
                onValueChange = { elevation = it },
                label = { Text("Elevation gain (m)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = {
                    val metres = (distance.toDoubleOrNull() ?: 0.0) * 1000.0
                    val elapsed =
                        (hours.toIntOrNull() ?: 0) * 3600 +
                            (minutes.toIntOrNull() ?: 0) * 60 +
                            (seconds.toIntOrNull() ?: 0)
                    scope.launch {
                        repository.saveActivity(
                            ActivityEntity(
                                athleteId = 1,
                                name = name.ifBlank { "${type.wireName} (added by hand)" },
                                type = type.wireName,
                                startedAt = Instant.now(),
                                elapsedSeconds = elapsed,
                                movingSeconds = elapsed,
                                distanceMeters = metres,
                                elevationGainMeters = elevation.toDoubleOrNull() ?: 0.0,
                                elevationLossMeters = 0.0,
                                averageSpeedMetersPerSecond = if (elapsed > 0) metres / elapsed else 0.0,
                                maxSpeedMetersPerSecond = 0.0,
                                averageHeartRate = null,
                                maxHeartRate = null,
                                calories = null,
                                commute = false,
                                trainer = false,
                                manual = true,
                                visibility = "PUBLIC",
                                gearId = null,
                                routeId = null,
                                startLatitude = null,
                                startLongitude = null,
                                endLatitude = null,
                                endLongitude = null,
                                polyline = null,
                                createdAt = Instant.now(),
                            ),
                        )
                        onDone()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Save activity") }
        }
    }
}
