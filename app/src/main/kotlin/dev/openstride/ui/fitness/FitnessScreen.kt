package dev.openstride.ui.fitness

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.openstride.OpenStrideApplication
import dev.openstride.analysis.Achievements
import dev.openstride.analysis.Fitness
import dev.openstride.analysis.Records
import dev.openstride.data.AppRepository
import dev.openstride.data.toModel
import dev.openstride.ui.components.FitnessChart
import dev.openstride.ui.components.StatRow
import dev.openstride.ui.components.WeeklyLoadChart

/**
 * Fitness, training load, records and badges.
 *
 * This is the "Training" and "Fitness" part of the original app: the
 * fitness and freshness chart, the weekly load, the personal records and the
 * achievements. All of it is computed on the device from the activities that
 * are stored.
 */
@Composable
fun FitnessScreen() {
    val context = LocalContext.current
    val repository = remember { AppRepository((context.applicationContext as OpenStrideApplication).database) }

    val activities by repository.observeActivities().collectAsState(initial = emptyList())

    val series =
        remember(activities) {
            val loads = Fitness.dailyLoads(activities.map { it.toModel() })
            Fitness.series(loads, endDate = loads.lastOrNull()?.date?.plusDays(10))
        }
    val weeks = remember(activities) { Fitness.weeks(Fitness.dailyLoads(activities.map { it.toModel() })) }
    val records = remember(activities) { Records.of(activities.map { it.toModel() }) }
    val badges = remember(activities) { Achievements.of(activities.map { it.toModel() }) }
    val streak = remember(activities) { Fitness.currentWeekStreak(activities.map { it.toModel() }) }
    val consistency = remember(activities) { Fitness.consistency(Fitness.dailyLoads(activities.map { it.toModel() })) }

    val latest = series.lastOrNull()
    val unlocked = badges.count { it.unlocked }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding =
            androidx.compose.foundation.layout
                .PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Text("Fitness", style = MaterialTheme.typography.headlineSmall)
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Fitness and freshness",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "The last 90 days of training, and what it is doing to you.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    FitnessChart(
                        points = series,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    StatRow(
                        stats =
                            listOf(
                                "Fitness" to (latest?.fitness?.toInt()?.toString() ?: "0"),
                                "Fatigue" to (latest?.fatigue?.toInt()?.toString() ?: "0"),
                                "Form" to (latest?.form?.toInt()?.toString() ?: "0"),
                            ),
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Weekly load",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    WeeklyLoadChart(weeks = weeks, modifier = Modifier.padding(top = 8.dp))
                    val thisWeek = weeks.lastOrNull()
                    StatRow(
                        stats =
                            listOf(
                                "This week" to (thisWeek?.load?.toInt()?.toString() ?: "0"),
                                "Weeks streak" to "$streak",
                                "Consistency" to "$consistency%",
                                "Badges" to "$unlocked/${badges.size}",
                            ),
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }

        item {
            Text(
                text = "Personal records",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        if (records.isEmpty()) {
            item {
                Text(
                    text = "Record an activity and your best times will appear here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        items(records, key = { it.title }) { record ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(record.title, style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = record.activityName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text = record.value,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        item {
            Text(
                text = "Achievements",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        items(badges, key = { it.title }) { badge ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    ) {
                        Text(
                            text = badge.title,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.weight(1f),
                            color =
                                if (badge.unlocked) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                        )
                        Text(
                            text = badge.progressText,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text = badge.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    LinearProgressIndicator(
                        progress = { badge.progress },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                    )
                }
            }
        }
    }
}
