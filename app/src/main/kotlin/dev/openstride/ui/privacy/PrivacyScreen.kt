package dev.openstride.ui.privacy

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
import androidx.compose.material3.Button
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
import dev.openstride.local.PrivacyZoneEntity
import dev.openstride.model.PrivacyZoneType
import dev.openstride.ui.map.OpenStrideMap
import kotlinx.coroutines.launch

/**
 * Privacy zones.
 *
 * The most safety-relevant feature in the original app: a circle around a
 * home or a school that is blanked out of any activity that passes through
 * it. There is nowhere to publish an activity here, but the zones still
 * matter, because they are what keeps an activity's start and end points
 * from sitting in a database unhidden.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = (context.applicationContext as OpenStrideApplication).repository
    val scope = rememberCoroutineScope()

    val zones by repository.observePrivacyZones().collectAsState(initial = emptyList())

    var name by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var radius by remember { mutableStateOf("100") }
    var type by remember { mutableStateOf(PrivacyZoneType.PRIVATE) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy zones") },
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
                        "A zone hides part of every activity that passes through it. " +
                            "Hidden start and hidden end zones keep the start and finish " +
                            "of a run out of the open.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("Add a zone", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = latitude,
                                onValueChange = { latitude = it },
                                label = { Text("Latitude") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                            )
                            OutlinedTextField(
                                value = longitude,
                                onValueChange = { longitude = it },
                                label = { Text("Longitude") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        OutlinedTextField(
                            value = radius,
                            onValueChange = { radius = it },
                            label = { Text("Radius (m)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        PrivacyZoneType.entries.forEach { candidate ->
                            if (candidate == type) {
                                Button(onClick = { type = candidate }, modifier = Modifier.fillMaxWidth()) {
                                    Text(candidate.name.lowercase().replace('_', ' '))
                                }
                            } else {
                                androidx.compose.material3.OutlinedButton(
                                    onClick = { type = candidate },
                                    modifier = Modifier.fillMaxWidth(),
                                ) { Text(candidate.name.lowercase().replace('_', ' ')) }
                            }
                        }
                        Button(
                            onClick = {
                                val lat = latitude.toDoubleOrNull() ?: return@Button
                                val lon = longitude.toDoubleOrNull() ?: return@Button
                                val rad = radius.toDoubleOrNull() ?: return@Button
                                scope.launch {
                                    repository.savePrivacyZone(
                                        PrivacyZoneEntity(
                                            name = name.ifBlank { "Zone" },
                                            centerLatitude = lat,
                                            centerLongitude = lon,
                                            radiusMeters = rad,
                                            type = type.name,
                                        ),
                                    )
                                    name = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text("Save zone") }
                    }
                }
            }

            items(zones, key = { it.id }) { zone ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = zone.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = "${zone.radiusMeters.toInt()} m",
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                        Text(
                            text = zone.type.lowercase().replace('_', ' '),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        OpenStrideMap(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .padding(top = 8.dp),
                            track =
                                listOf(
                                    dev.openstride.recording.TrackPoint(
                                        zone.centerLatitude,
                                        zone.centerLongitude,
                                        0.0,
                                        0L,
                                    ),
                                ),
                        )
                    }
                }
            }
        }
    }
}
