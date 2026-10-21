package dev.openstride.ui.gear

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import dev.openstride.local.GearEntity
import dev.openstride.model.GearType
import dev.openstride.recording.formatDistance
import kotlinx.coroutines.launch

/**
 * The athlete's gear.
 *
 * The original app tracks bikes and shoes, their distance, and the activities
 * they were used on. Here the distance is computed from the activities that
 * used them.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GearScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = (context.applicationContext as OpenStrideApplication).repository
    val scope = rememberCoroutineScope()

    val activities by repository.observeActivities().collectAsState(initial = emptyList())
    val gear by repository.observeGear().collectAsState(initial = emptyList())

    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(GearType.BIKE) }
    var brand by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gear") },
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
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text("Add gear", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = brand,
                            onValueChange = { brand = it },
                            label = { Text("Brand (optional)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            GearType.entries.forEach { candidate ->
                                if (candidate == type) {
                                    Button(onClick = { type = candidate }) { Text(candidate.name.lowercase()) }
                                } else {
                                    androidx.compose.material3.OutlinedButton(onClick = { type = candidate }) {
                                        Text(candidate.name.lowercase())
                                    }
                                }
                            }
                        }
                        Button(
                            onClick = {
                                if (name.isBlank()) return@Button
                                scope.launch {
                                    repository.saveGear(
                                        GearEntity(
                                            athleteId = 1,
                                            name = name.trim(),
                                            type = type.name,
                                            brand = brand.trim().ifBlank { null },
                                            model = null,
                                            distanceMeters = 0.0,
                                            retiredAt = null,
                                        ),
                                    )
                                    name = ""
                                    brand = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text("Save") }
                    }
                }
            }

            if (gear.isEmpty()) {
                item {
                    Text(
                        text = "No gear yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            items(gear, key = { it.id }) { item ->
                val used = activities.filter { it.gearId == item.id }
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = listOfNotNull(item.brand, item.type.lowercase()).joinToString(" · "),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = formatDistance(used.sumOf { it.distanceMeters }),
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Text(
                                text = "${used.size} activities",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}
