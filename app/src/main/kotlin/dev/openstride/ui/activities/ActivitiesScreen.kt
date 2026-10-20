package dev.openstride.ui.activities

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.openstride.OpenStrideApplication
import dev.openstride.model.ActivityType
import dev.openstride.recording.formatDistance
import dev.openstride.recording.formatDuration
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** The list of recorded activities. */
@Composable
fun ActivitiesScreen(onOpen: (Long) -> Unit) {
    val context = LocalContext.current
    val database = (context.applicationContext as OpenStrideApplication).database
    val activities by database.activityDao().observeAll().collectAsState(initial = emptyList())

    if (activities.isEmpty()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Nothing recorded yet",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Record an activity and it will show up here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding =
            androidx.compose.foundation.layout
                .PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(activities, key = { it.id }) { activity ->
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { onOpen(activity.id) },
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
                            text =
                                "${dateFormat.format(activity.startedAt.atZone(ZoneId.systemDefault()))} · " +
                                    activityTypeLabel(activity.type),
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
    }
}

internal val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm")

internal fun activityTypeLabel(wireName: String): String =
    ActivityType.entries.firstOrNull { it.wireName == wireName }?.wireName ?: wireName
