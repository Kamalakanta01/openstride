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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.openstride.ui.FitnessViewModel
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
    // The analysis is computed once, off the main thread, by the view model.
    val viewModel: FitnessViewModel = viewModel()
    val analysis by viewModel.analysis.collectAsState()
    val series = analysis.series

    val latest = series.lastOrNull()
    val unlocked = analysis.badges.count { it.unlocked }

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
                    WeeklyLoadChart(weeks = analysis.weeks, modifier = Modifier.padding(top = 8.dp))
                    val thisWeek = analysis.weeks.lastOrNull()
                    StatRow(
                        stats =
                            listOf(
                                "This week" to (thisWeek?.load?.toInt()?.toString() ?: "0"),
                                "Weeks streak" to "${analysis.streak}",
                                "Consistency" to "${analysis.consistency}%",
                                "Badges" to "$unlocked/${analysis.badges.size}",
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

        if (analysis.records.isEmpty()) {
            item {
                Text(
                    text = "Record an activity and your best times will appear here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        items(analysis.records, key = { it.title }) { record ->
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

        items(analysis.badges, key = { it.title }) { badge ->
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
