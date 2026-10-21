package dev.openstride.ui.profile

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.openstride.OpenStrideApplication
import dev.openstride.local.AthleteEntity
import dev.openstride.recording.formatDistance
import dev.openstride.recording.formatDuration
import dev.openstride.ui.components.Stat
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

/**
 * The athlete's own profile.
 *
 * There is no server, so this is the whole of the athlete's identity: a
 * name, a weight, and the units they like their numbers in.
 */
@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val database = (context.applicationContext as OpenStrideApplication).database
    val scope = rememberCoroutineScope()

    // The first focusable in a screen takes the focus when the screen
    // appears, and a text field does, so the keyboard opens over the screen
    // the moment the tab is tapped. A focusable of nothing takes it instead.
    val focusRequester = remember { FocusRequester() }
    var athlete by remember { mutableStateOf<AthleteEntity?>(null) }
    var name by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var metric by remember { mutableStateOf(true) }
    var saved by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        val current = database.athleteDao().current()
        athlete = current
        name = current?.name ?: ""
        weight = current?.weightKilograms?.toString() ?: ""
    }

    val activities by database.activityDao().observeAll().collectAsState(initial = emptyList())
    val totalDistance = activities.sumOf { it.distanceMeters }
    val totalSeconds = activities.sumOf { it.elapsedSeconds }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(0.dp)
                    .focusRequester(focusRequester)
                    .focusable(),
        )
        Text("Profile", style = MaterialTheme.typography.headlineSmall)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Totals",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Stat("Activities", "${activities.size}")
                    Stat("Distance", formatDistance(totalDistance))
                    Stat("Time", formatDuration(totalSeconds))
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Athlete",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        saved = false
                    },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = {
                        weight = it
                        saved = false
                    },
                    label = { Text("Weight (kg)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Metric units")
                    Switch(checked = metric, onCheckedChange = { metric = it })
                }
                Button(
                    onClick = {
                        scope.launch {
                            database.athleteDao().upsert(
                                AthleteEntity(
                                    id = athlete?.id ?: 0,
                                    name = name.ifBlank { "Athlete" },
                                    email = athlete?.email,
                                    city = athlete?.city,
                                    country = athlete?.country,
                                    timezone = ZoneId.systemDefault().id,
                                    weightKilograms = weight.toDoubleOrNull(),
                                    createdAt = athlete?.createdAt ?: Instant.now(),
                                ),
                            )
                            saved = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text(if (saved) "Saved" else "Save") }
            }
        }

        Text(
            text =
                "This app has no server. Everything you record stays on this device, " +
                    "and there is no account to sign in to.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
