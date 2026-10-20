package dev.openstride.ui.log

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import dev.openstride.analysis.Fitness
import dev.openstride.data.AppRepository
import dev.openstride.data.toModel
import dev.openstride.local.ActivityEntity
import dev.openstride.model.ActivityType
import dev.openstride.recording.formatDistance
import dev.openstride.recording.formatDuration
import dev.openstride.ui.components.FilterRow
import dev.openstride.ui.components.StatRow
import dev.openstride.ui.components.TrainingCalendar
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * The training log.
 *
 * The original app's log is a list of activities with a filter by sport. Here
 * it is a calendar of the month, with the same list underneath, because a
 * month of training is easier to read as a shape than as a list.
 */
@Composable
fun LogScreen(onOpen: (Long) -> Unit) {
    val context = LocalContext.current
    val repository = remember { AppRepository((context.applicationContext as OpenStrideApplication).database) }

    val activities by repository.observeActivities().collectAsState(initial = emptyList())
    var month by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    var filter by remember { mutableStateOf<ActivityType?>(null) }

    val loads =
        remember(activities) {
            Fitness.dailyLoads(activities.map { it.toModel() }).associate { it.date to it.load }
        }
    val selectedType = filter
    val shown = activities.filter { selectedType == null || it.type == selectedType.wireName }
    val monthActivities =
        shown.filter {
            it.startedAt
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .month == month.month
        }
    val monthSeconds = monthActivities.sumOf { it.elapsedSeconds }
    val monthDistance = monthActivities.sumOf { it.distanceMeters }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding =
            androidx.compose.foundation.layout
                .PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Column {
                Text("Training log", style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = month.format(monthFormat),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                TrainingCalendar(
                    month = month,
                    loads = loads,
                    modifier = Modifier.padding(12.dp),
                )
            }
        }

        item {
            StatRow(
                stats =
                    listOf(
                        "Activities" to "${monthActivities.size}",
                        "Distance" to formatDistance(monthDistance),
                        "Time" to formatDuration(monthSeconds),
                        "Load" to "${loads.filterKeys { it.month == month.month }.values.sum().toInt()}",
                    ),
            )
        }

        item {
            FilterRow(
                items = listOf<ActivityType?>(null) + ActivityType.entries,
                selected = filter,
                label = { it?.wireName ?: "All" },
                onSelect = { filter = it },
            )
        }

        if (shown.isEmpty()) {
            item {
                Text(
                    text = "No activities yet. Record one and it will appear here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        items(shown, key = { it.id }) { activity ->
            ActivityRow(activity = activity, onClick = { onOpen(activity.id) })
        }
    }
}

private val monthFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")

private val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM, HH:mm")

/** One activity in the log. */
@Composable
fun ActivityRow(
    activity: ActivityEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick() },
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = activity.startedAt.atZone(ZoneId.systemDefault()).format(dateFormat),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatDistance(activity.distanceMeters),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = formatDuration(activity.elapsedSeconds),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
