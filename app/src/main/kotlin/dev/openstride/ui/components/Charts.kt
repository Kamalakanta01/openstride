/**
 * The charts.
 *
 * They are drawn with Compose's canvas rather than a charting library: the
 * shapes needed here are simple, and a dependency would be a lot of bytes
 * for four graphs.
 */

package dev.openstride.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.openstride.analysis.Fitness
import java.time.LocalDate

/** The fitness and freshness chart: fitness, fatigue and form over time. */
@Composable
fun FitnessChart(
    points: List<Fitness.Point>,
    modifier: Modifier = Modifier,
) {
    val fitnessColour = MaterialTheme.colorScheme.primary
    val fatigueColour = MaterialTheme.colorScheme.tertiary
    val formColour = MaterialTheme.colorScheme.secondary

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Legend("Fitness", fitnessColour)
            Legend("Fatigue", fatigueColour)
            Legend("Form", formColour)
        }
        androidx.compose.foundation.Canvas(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(top = 8.dp),
        ) {
            if (points.size < 2) return@Canvas
            val values = points.flatMap { listOf(it.fitness, it.fatigue, it.form) }
            val low = values.min()
            val high = values.max()
            val span = (high - low).coerceAtLeast(1.0)
            val stepX = size.width / (points.size - 1).toFloat()

            fun y(value: Double): Float = size.height - ((value - low) / span).toFloat() * size.height

            // Zero, the line that separates fresh from fatigued.
            if (low < 0.0 && high > 0.0) {
                drawLine(
                    color = Color.Gray.copy(alpha = 0.4f),
                    start = Offset(0f, y(0.0)),
                    end = Offset(size.width, y(0.0)),
                    strokeWidth = 1f,
                )
            }

            fun line(
                selector: (Fitness.Point) -> Double,
                colour: Color,
            ) {
                val path = Path()
                points.forEachIndexed { index, point ->
                    val x = index * stepX
                    val py = y(selector(point))
                    if (index == 0) path.moveTo(x, py) else path.lineTo(x, py)
                }
                drawPath(
                    path = path,
                    color = colour,
                    style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round),
                )
            }

            line({ it.fitness }, fitnessColour)
            line({ it.fatigue }, fatigueColour)
            line({ it.form }, formColour)
        }
    }
}

/** The colour key under a chart. */
@Composable
private fun Legend(
    label: String,
    colour: Color,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier =
                Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(colour),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(start = 4.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** The weekly training load, as bars. */
@Composable
fun WeeklyLoadChart(
    weeks: List<Fitness.Week>,
    modifier: Modifier = Modifier,
) {
    val barColour = MaterialTheme.colorScheme.primary
    val recent = weeks.takeLast(16)
    androidx.compose.foundation.Canvas(
        modifier =
            modifier
                .fillMaxWidth()
                .height(120.dp),
    ) {
        if (recent.isEmpty()) return@Canvas
        val busiest = recent.maxOf { it.load }.coerceAtLeast(1.0)
        val slot = size.width / recent.size
        val barWidth = slot * 0.6f
        recent.forEachIndexed { index, week ->
            val height = (week.load / busiest).toFloat() * size.height
            drawRoundRect(
                color = barColour.copy(alpha = if (week.load > 0) 1f else 0.15f),
                topLeft = Offset(index * slot + (slot - barWidth) / 2, size.height - height),
                size =
                    androidx.compose.ui.geometry
                        .Size(barWidth, height),
                cornerRadius =
                    androidx.compose.ui.geometry
                        .CornerRadius(4f, 4f),
            )
        }
    }
}

/**
 * A month of training, as a calendar.
 *
 * The original app's training log is a list; the calendar is a better way
 * to see the shape of a month at a glance, and it is the same information.
 */
@Composable
fun TrainingCalendar(
    month: LocalDate,
    loads: Map<LocalDate, Double>,
    modifier: Modifier = Modifier,
    onSelect: (LocalDate) -> Unit = {},
) {
    val first = month.withDayOfMonth(1)
    val daysInMonth = month.lengthOfMonth()
    // Monday is the first column.
    val leading = (first.dayOfWeek.value - 1)
    val busiest = loads.values.maxOrNull()?.coerceAtLeast(1.0) ?: 1.0
    val primary = MaterialTheme.colorScheme.primary
    val surface = MaterialTheme.colorScheme.surfaceContainerHighest

    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        var day = 1
        while (day <= daysInMonth) {
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { column ->
                    val index = (day - 1) + leading
                    if (index % 7 != column || day > daysInMonth) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val date = month.withDayOfMonth(day)
                        val load = loads[date] ?: 0.0
                        val intensity = if (load > 0) (load / busiest).toFloat().coerceIn(0.15f, 1f) else 0f
                        Box(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(if (load > 0) primary.copy(alpha = intensity) else surface)
                                    .clickable { onSelect(date) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = day.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (intensity > 0.5f) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        day++
                    }
                }
            }
        }
    }
}

/**
 * A row of small numbers, used on the summary panels.
 *
 * The stats wrap onto a second line rather than being squeezed: this phone
 * runs at a 1.7x font scale, and four stats do not fit across the screen.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatRow(
    stats: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        stats.forEach { (label, value) ->
            Stat(label = label, value = value)
        }
    }
}

/** One number, with its label underneath. */
@Composable
fun Stat(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** A horizontal list of pills, used for filters. */
@Composable
fun <T> FilterRow(
    items: List<T>,
    selected: T,
    label: (T) -> String,
    modifier: Modifier = Modifier,
    onSelect: (T) -> Unit,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items) { item ->
            val isSelected = item == selected
            Box(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerHighest
                            },
                        ).border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(20.dp),
                        ).clickable { onSelect(item) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text(
                    text = label(item),
                    style = MaterialTheme.typography.labelLarge,
                    color =
                        if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                )
            }
        }
    }
}
