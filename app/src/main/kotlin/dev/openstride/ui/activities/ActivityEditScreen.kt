package dev.openstride.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import dev.openstride.local.ActivityEntity
import dev.openstride.model.ActivityType
import dev.openstride.model.Visibility
import kotlinx.coroutines.launch

/**
 * Editing an activity.
 *
 * The original app lets an activity be renamed, reclassified, made private
 * and assigned to a pair of shoes or a bike. Those columns have been in the
 * database since the beginning; this is the screen that fills them in.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ActivityEditScreen(
    activityId: Long,
    onDone: () -> Unit,
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as OpenStrideApplication).repository
    val scope = rememberCoroutineScope()

    var activity by remember { mutableStateOf<ActivityEntity?>(null) }
    val gear by repository.observeGear().collectAsState(initial = emptyList())

    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(ActivityType.RUN) }
    var private by remember { mutableStateOf(false) }
    var commute by remember { mutableStateOf(false) }
    var gearId by remember { mutableStateOf<Long?>(null) }
    var deleted by remember { mutableStateOf(false) }

    LaunchedEffect(activityId) {
        val current = repository.activity(activityId)
        activity = current
        current?.let {
            name = it.name
            type = ActivityType.entries.firstOrNull { e -> e.wireName == it.type } ?: ActivityType.RUN
            private = it.visibility == Visibility.PRIVATE.name
            commute = it.commute
            gearId = it.gearId
        }
    }

    val current = activity
    if (deleted) {
        onDone()
        return
    }
    if (current == null) {
        Box(loading = true)
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit activity") },
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
            androidx.compose.foundation.layout.FlowRow(
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

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Private",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                        )
                        Switch(checked = private, onCheckedChange = { private = it })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Commute",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                        )
                        Switch(checked = commute, onCheckedChange = { commute = it })
                    }
                }
            }

            if (gear.isNotEmpty()) {
                Text(
                    text = "Gear",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                androidx.compose.foundation.layout.FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = gearId == null,
                        onClick = { gearId = null },
                        label = { Text("None") },
                    )
                    gear.forEach { item ->
                        FilterChip(
                            selected = gearId == item.id,
                            onClick = { gearId = item.id },
                            label = { Text(item.name) },
                        )
                    }
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        repository.saveActivity(
                            current.copy(
                                name = name.ifBlank { current.name },
                                type = type.wireName,
                                visibility = if (private) Visibility.PRIVATE.name else Visibility.PUBLIC.name,
                                commute = commute,
                                gearId = gearId,
                            ),
                        )
                        onDone()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Save") }

            Button(
                onClick = {
                    scope.launch {
                        repository.deleteActivity(activityId)
                        deleted = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            ) { Text("Delete activity") }
        }
    }
}

/** A placeholder while the activity is being read. */
@Composable
private fun Box(loading: Boolean) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (loading) {
            Text("Loading", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
