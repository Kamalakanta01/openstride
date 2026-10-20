package dev.openstride.ui.goals

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
import androidx.compose.material3.LinearProgressIndicator
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
import dev.openstride.data.AppRepository
import dev.openstride.local.GoalEntity
import dev.openstride.model.GoalType
import dev.openstride.recording.formatDistance
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * The athlete's goals.
 *
 * The original app's goals are set on the server and shared with the
 * community. Here a goal is a target for this week or this month, and the
 * progress is whatever has been recorded.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { AppRepository((context.applicationContext as OpenStrideApplication).database) }
    val scope = rememberCoroutineScope()

    val activities by repository.observeActivities().collectAsState(initial = emptyList())
    val goals by repository.observeGoals().collectAsState(initial = emptyList())

    var type by remember { mutableStateOf(GoalType.WEEKLY_DISTANCE) }
    var target by remember { mutableStateOf("") }

    val today = LocalDate.now()
    val thisWeekDistance =
        activities
            .filter { it.startedAt.atZone(java.time.ZoneId.systemDefault()).toLocalDate() >= today.minusDays(6) }
            .sumOf { it.distanceMeters }
    val thisMonthDistance =
        activities
            .filter {
                it.startedAt
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()
                    .month == today.month
            }.sumOf { it.distanceMeters }
    val thisMonthElevation =
        activities
            .filter {
                it.startedAt
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()
                    .month == today.month
            }.sumOf { it.elevationGainMeters }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Goals") },
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
                        Text("New goal", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        GoalType.entries.forEach { candidate ->
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
                        OutlinedTextField(
                            value = target,
                            onValueChange = { target = it },
                            label = { Text("Target") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Button(
                            onClick = {
                                val value = target.toDoubleOrNull() ?: return@Button
                                val (start, end) = periodFor(type, today)
                                scope.launch {
                                    repository.saveGoal(
                                        GoalEntity(
                                            type = type.name,
                                            targetValue = value,
                                            periodStart = start,
                                            periodEnd = end,
                                        ),
                                    )
                                    target = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text("Save goal") }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Text("This week", style = MaterialTheme.typography.labelSmall)
                            Text(formatDistance(thisWeekDistance), style = MaterialTheme.typography.titleSmall)
                        }
                        Column {
                            Text("This month", style = MaterialTheme.typography.labelSmall)
                            Text(formatDistance(thisMonthDistance), style = MaterialTheme.typography.titleSmall)
                        }
                        Column {
                            Text("Climb this month", style = MaterialTheme.typography.labelSmall)
                            Text("${thisMonthElevation.toInt()} m", style = MaterialTheme.typography.titleSmall)
                        }
                    }
                }
            }

            if (goals.isEmpty()) {
                item {
                    Text(
                        text = "No goals set.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            items(goals, key = { it.id }) { goal ->
                val done =
                    when (GoalType.entries.firstOrNull { it.name == goal.type }) {
                        GoalType.WEEKLY_DISTANCE -> thisWeekDistance
                        GoalType.MONTHLY_DISTANCE -> thisMonthDistance
                        GoalType.ANNUAL_ELEVATION, GoalType.WEEKLY_ELEVATION -> thisMonthElevation
                        null -> 0.0
                    }
                val progress = if (goal.targetValue <= 0) 0f else (done / goal.targetValue).toFloat().coerceIn(0f, 1f)
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = goal.type.lowercase().replace('_', ' '),
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        LinearProgressIndicator(
                            progress = { progress },
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
}

/** The period a goal of this kind covers. */
private fun periodFor(
    type: GoalType,
    today: LocalDate,
): Pair<LocalDate, LocalDate> =
    when (type) {
        GoalType.WEEKLY_DISTANCE, GoalType.WEEKLY_ELEVATION ->
            today.minusDays(6) to today.plusDays(1)
        GoalType.MONTHLY_DISTANCE ->
            today.withDayOfMonth(1) to today.withDayOfMonth(today.lengthOfMonth())
        GoalType.ANNUAL_ELEVATION ->
            today.withDayOfYear(1) to today.withDayOfYear(today.lengthOfYear())
    }
